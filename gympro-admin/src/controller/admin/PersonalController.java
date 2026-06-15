package controller.admin;

import dao.UsuarioDAO;
import entity.PersonalResumo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class PersonalController {

	@FXML private TextField txtBusca;
	@FXML private TableView<PersonalResumo> tblPersonais;
	@FXML private TableColumn<PersonalResumo, String> colNome;
	@FXML private TableColumn<PersonalResumo, String> colEmail;
	@FXML private TableColumn<PersonalResumo, String> colCref;
	@FXML private TableColumn<PersonalResumo, String> colEspecialidade;
	@FXML private TableColumn<PersonalResumo, String> colStatus;
	@FXML private TableColumn<PersonalResumo, Void> colAcoes;
	@FXML private Label lblTotal;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	@FXML
	private void initialize() {
		colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colCref.setCellValueFactory(new PropertyValueFactory<>("cref"));
		colEspecialidade.setCellValueFactory(new PropertyValueFactory<>("especialidade"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

		tblPersonais.setEditable(true);

		colNome.setCellFactory(TextFieldTableCell.forTableColumn());
		colNome.setOnEditCommit(e -> { e.getRowValue().setNome(e.getNewValue()); salvarEdicao(e.getRowValue()); });

		colCref.setCellFactory(TextFieldTableCell.forTableColumn());
		colCref.setOnEditCommit(e -> { e.getRowValue().setCref(e.getNewValue()); salvarEdicao(e.getRowValue()); });

		colEspecialidade.setCellFactory(TextFieldTableCell.forTableColumn());
		colEspecialidade.setOnEditCommit(e -> { e.getRowValue().setEspecialidade(e.getNewValue()); salvarEdicao(e.getRowValue()); });

		colAcoes.setCellFactory(col -> new TableCell<>() {
			private final Button btnEditar = new Button("Editar");
			private final Button btnExcluir = new Button("X");
			private final HBox actions = new HBox(6, btnEditar, btnExcluir);
			{
				setAlignment(Pos.CENTER_RIGHT);
				actions.setAlignment(Pos.CENTER_RIGHT);
				btnEditar.getStyleClass().add("btn-page");
				btnExcluir.getStyleClass().add("btn-danger");
				btnEditar.setOnAction(ev -> abrirModal(getTableView().getItems().get(getIndex())));
				btnExcluir.setOnAction(ev -> deletar(getTableView().getItems().get(getIndex())));
			}
			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : actions);
			}
		});

		tblPersonais.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
		carregarPersonais("");
	}

	@FXML
	private void onPesquisar() {
		carregarPersonais(txtBusca.getText());
	}

	@FXML
	private void onNovoPersonal() {
		abrirModal(null);
	}

	private void abrirModal(PersonalResumo existente) {
		boolean edicao = existente != null;
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(edicao ? "Editar Personal" : "Novo Personal");
		dialog.setHeaderText(edicao ? "Atualizar cadastro do personal" : "Cadastrar novo personal");
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/view/styles/admin-dark.css").toExternalForm());
		dialog.getDialogPane().getStyleClass().add("app-dialog");

		ButtonType salvarType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(salvarType, ButtonType.CANCEL);

		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);

		TextField nomeField = new TextField();
		nomeField.setPromptText("Nome");
		nomeField.getStyleClass().add("field-dark");
		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.getStyleClass().add("field-dark");
		emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (!newVal) {
				String v = emailField.getText() == null ? "" : emailField.getText().trim();
				if (!v.isEmpty() && !v.contains("@")) {
					emailField.setStyle("-fx-border-color: #ef4444; -fx-border-width: 1.5;");
				} else {
					emailField.setStyle("");
				}
			}
		});
		PasswordField senhaField = new PasswordField();
		senhaField.setPromptText(edicao ? "Nova senha (opcional)" : "Senha");
		senhaField.getStyleClass().add("field-dark");
		TextField crefField = new TextField();
		crefField.setPromptText("CREF");
		crefField.getStyleClass().add("field-dark");
		TextField especialidadeField = new TextField();
		especialidadeField.setPromptText("Especialidade");
		especialidadeField.getStyleClass().add("field-dark");
		ComboBox<String> statusField = new ComboBox<>();
		statusField.getItems().addAll("ATIVO", "PENDENTE", "BLOQUEADO");
		statusField.setPrefWidth(220);
		statusField.getStyleClass().add("field-dark");

		if (edicao) {
			nomeField.setText(existente.getNome());
			emailField.setText(existente.getEmail());
			crefField.setText(existente.getCref());
			especialidadeField.setText(existente.getEspecialidade());
			statusField.setValue(existente.getStatus());
		} else {
			statusField.setValue("ATIVO");
		}

		form.addRow(0, new Label("Nome"), nomeField);
		form.addRow(1, new Label("Email"), emailField);
		form.addRow(2, new Label("Senha"), senhaField);
		form.addRow(3, new Label("CREF"), crefField);
		form.addRow(4, new Label("Especialidade"), especialidadeField);
		form.addRow(5, new Label("Status"), statusField);
		dialog.getDialogPane().setContent(form);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isEmpty() || result.get() != salvarType) return;

		String nome = nomeField.getText() == null ? "" : nomeField.getText().trim();
		String email = emailField.getText() == null ? "" : emailField.getText().trim();
		String senha = senhaField.getText() == null ? "" : senhaField.getText();
		String cref = crefField.getText() == null ? "" : crefField.getText().trim();
		String especialidade = especialidadeField.getText() == null ? "" : especialidadeField.getText().trim();
		String status = statusField.getValue() == null ? "ATIVO" : statusField.getValue();

		if (nome.isEmpty() || email.isEmpty() || (!edicao && senha.isEmpty())) {
			mostrar(Alert.AlertType.WARNING, edicao ? "Nome e email sao obrigatorios." : "Nome, email e senha sao obrigatorios.");
			return;
		}

		if (!email.contains("@")) {
			mostrar(Alert.AlertType.WARNING, "Email invalido. O email deve conter @.");
			return;
		}

		String senhaHash = senha.isBlank() ? null : BCrypt.hashpw(senha, BCrypt.gensalt(10));
		boolean ok;
		if (edicao) {
			ok = usuarioDAO.atualizarPersonal(existente.getUsuarioId(), existente.getPersonalId(), nome, email, cref, especialidade, status, senhaHash);
			if (!ok) { mostrar(Alert.AlertType.ERROR, "Nao foi possivel atualizar o personal."); return; }
		} else {
			ok = usuarioDAO.criarPersonal(nome, email, senhaHash, cref, especialidade);
			if (!ok) { mostrar(Alert.AlertType.ERROR, "Nao foi possivel cadastrar o personal. Verifique se o email ja existe."); return; }
		}
		carregarPersonais(txtBusca.getText());
	}

	private void salvarEdicao(PersonalResumo p) {
		boolean ok = usuarioDAO.atualizarPersonal(p.getUsuarioId(), p.getPersonalId(), p.getNome(), p.getEmail(), p.getCref(), p.getEspecialidade(), p.getStatus(), null);
		if (!ok) { mostrar(Alert.AlertType.ERROR, "Nao foi possivel salvar a alteracao."); carregarPersonais(txtBusca.getText()); }
	}

	private void deletar(PersonalResumo p) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("Excluir o personal " + p.getNome() + "?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			if (!usuarioDAO.deletarPersonal(p.getUsuarioId())) {
				mostrar(Alert.AlertType.ERROR, "Nao foi possivel excluir o personal.");
				return;
			}
			carregarPersonais(txtBusca.getText());
		}
	}

	private void carregarPersonais(String termo) {
		List<PersonalResumo> personais = usuarioDAO.listarPersonaisResumo(termo);
		tblPersonais.setItems(FXCollections.observableArrayList(personais));
		int total = personais.size();
		lblTotal.setText(total == 0 ? "Showing 0 entries" : "Showing 1 to " + total + " entries");
	}

	private void mostrar(Alert.AlertType tipo, String mensagem) {
		Alert alert = new Alert(tipo);
		alert.setHeaderText(null);
		alert.setContentText(mensagem);
		alert.showAndWait();
	}
}
