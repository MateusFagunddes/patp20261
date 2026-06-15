package controller.admin;

import dao.UsuarioDAO;
import entity.AlunoResumo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TextFormatter;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class AlunoController {

	@FXML
	private TextField txtBusca;

	@FXML
	private TableView<AlunoResumo> tblAlunos;

	@FXML
	private TableColumn<AlunoResumo, String> colNome;

	@FXML
	private TableColumn<AlunoResumo, String> colEmail;

	@FXML
	private TableColumn<AlunoResumo, String> colTelefone;

	@FXML
	private TableColumn<AlunoResumo, String> colObjetivo;

	@FXML
	private TableColumn<AlunoResumo, String> colStatus;

	@FXML
	private TableColumn<AlunoResumo, Void> colAcoes;

	@FXML
	private Label lblTotal;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	@FXML
	private void initialize() {
		colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		colObjetivo.setCellValueFactory(new PropertyValueFactory<>("objetivo"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		tblAlunos.setEditable(true);

		colNome.setCellFactory(TextFieldTableCell.forTableColumn());
		colNome.setOnEditCommit(event -> {
			AlunoResumo aluno = event.getRowValue();
			aluno.setNome(event.getNewValue());
			salvarEdicao(aluno);
		});

		colTelefone.setCellFactory(TextFieldTableCell.forTableColumn());
		colTelefone.setOnEditCommit(event -> {
			AlunoResumo aluno = event.getRowValue();
			aluno.setTelefone(event.getNewValue());
			salvarEdicao(aluno);
		});

		colObjetivo.setCellFactory(TextFieldTableCell.forTableColumn());
		colObjetivo.setOnEditCommit(event -> {
			AlunoResumo aluno = event.getRowValue();
			aluno.setObjetivo(event.getNewValue());
			salvarEdicao(aluno);
		});

		colStatus.setCellFactory(ComboBoxTableCell.forTableColumn("ATIVO", "PENDENTE", "BLOQUEADO"));
		colStatus.setOnEditCommit(event -> {
			AlunoResumo aluno = event.getRowValue();
			aluno.setStatus(event.getNewValue());
			salvarEdicao(aluno);
		});

		colAcoes.setCellFactory(column -> new TableCell<>() {
			private final Button btnEditar = new Button("Editar");
			private final Button btnExcluir = new Button("X");
			private final HBox actions = new HBox(6, btnEditar, btnExcluir);

			{
				setAlignment(Pos.CENTER_RIGHT);
				actions.setAlignment(Pos.CENTER_RIGHT);
				btnEditar.getStyleClass().add("btn-page");
				btnExcluir.getStyleClass().add("btn-danger");
				btnEditar.setOnAction(event -> {
					AlunoResumo aluno = getTableView().getItems().get(getIndex());
					onEditarAluno(aluno);
				});
				btnExcluir.setOnAction(event -> {
					AlunoResumo aluno = getTableView().getItems().get(getIndex());
					onDeletarAluno(aluno);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : actions);
			}
		});

		tblAlunos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

		carregarAlunos("");
	}

	@FXML
	private void onPesquisar() {
		carregarAlunos(txtBusca.getText());
	}

	@FXML
	private void onNovoAluno() {
		abrirModalAluno(null);
	}

	private void onEditarAluno(AlunoResumo aluno) {
		abrirModalAluno(aluno);
	}

	private void abrirModalAluno(AlunoResumo alunoExistente) {
		Dialog<ButtonType> dialog = new Dialog<>();
		boolean edicao = alunoExistente != null;
		dialog.setTitle(edicao ? "Editar Aluno" : "Novo Aluno");
		dialog.setHeaderText(edicao ? "Atualizar cadastro do aluno" : "Cadastrar novo aluno");
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/view/styles/admin-dark.css").toExternalForm());
		dialog.getDialogPane().getStyleClass().add("app-dialog");

		ButtonType salvarType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(salvarType, ButtonType.CANCEL);

		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);

		TextField nomeField = new TextField();
		nomeField.setPromptText("Nome");
		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		PasswordField senhaField = new PasswordField();
		senhaField.setPromptText(edicao ? "Nova senha opcional" : "Senha");
		TextField telefoneField = new TextField();
		telefoneField.setPromptText("(99) 99999-9999");

		UnaryOperator<TextFormatter.Change> filtroTelefone = change -> {
			String digitos = change.getControlNewText().replaceAll("[^0-9]", "");
			if (digitos.length() > 11) return null;
			StringBuilder fmt = new StringBuilder();
			for (int i = 0; i < digitos.length(); i++) {
				if (i == 0) fmt.append("(");
				if (i == 2) fmt.append(") ");
				if (i == 7) fmt.append("-");
				fmt.append(digitos.charAt(i));
			}
			change.setText(fmt.toString());
			change.setRange(0, change.getControlText().length());
			change.setCaretPosition(fmt.length());
			change.setAnchor(fmt.length());
			return change;
		};
		telefoneField.setTextFormatter(new TextFormatter<>(filtroTelefone));

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
		TextField objetivoField = new TextField();
		objetivoField.setPromptText("Objetivo");
		ComboBox<String> statusField = new ComboBox<>();
		statusField.getItems().addAll("ATIVO", "PENDENTE", "BLOQUEADO");
		statusField.setPrefWidth(220);

		nomeField.getStyleClass().add("field-dark");
		emailField.getStyleClass().add("field-dark");
		senhaField.getStyleClass().add("field-dark");
		telefoneField.getStyleClass().add("field-dark");
		objetivoField.getStyleClass().add("field-dark");
		statusField.getStyleClass().add("field-dark");

		if (edicao) {
			nomeField.setText(alunoExistente.getNome());
			emailField.setText(alunoExistente.getEmail());
			telefoneField.setText(alunoExistente.getTelefone());
			objetivoField.setText(alunoExistente.getObjetivo());
			statusField.setValue(alunoExistente.getStatus());
		} else {
			statusField.setValue("ATIVO");
		}

		form.addRow(0, new Label("Nome"), nomeField);
		form.addRow(1, new Label("Email"), emailField);
		form.addRow(2, new Label("Senha"), senhaField);
		form.addRow(3, new Label("Telefone"), telefoneField);
		form.addRow(4, new Label("Objetivo"), objetivoField);
		form.addRow(5, new Label("Status"), statusField);

		dialog.getDialogPane().setContent(form);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isEmpty() || result.get() != salvarType) {
			return;
		}

		String nome = nomeField.getText() == null ? "" : nomeField.getText().trim();
		String email = emailField.getText() == null ? "" : emailField.getText().trim();
		String senha = senhaField.getText() == null ? "" : senhaField.getText();
		String telefone = telefoneField.getText() == null ? "" : telefoneField.getText().trim();
		String objetivo = objetivoField.getText() == null ? "" : objetivoField.getText().trim();
		String status = statusField.getValue() == null ? "ATIVO" : statusField.getValue().trim();

		if (nome.isEmpty() || email.isEmpty() || (!edicao && senha.isEmpty())) {
			mostrar(Alert.AlertType.WARNING, edicao ? "Nome e email sao obrigatorios." : "Nome, email e senha sao obrigatorios.");
			return;
		}

		if (!email.contains("@")) {
			mostrar(Alert.AlertType.WARNING, "Email invalido. O email deve conter @.");
			return;
		}

		String digitos = telefone.replaceAll("[^0-9]", "");
		if (!telefone.isEmpty() && digitos.length() < 11) {
			mostrar(Alert.AlertType.WARNING, "Telefone invalido. Use o formato (99) 99999-9999.");
			return;
		}

		String senhaHash = senha.isBlank() ? null : BCrypt.hashpw(senha, BCrypt.gensalt(10));
		boolean ok;
		if (edicao) {
			ok = usuarioDAO.atualizarAluno(
					alunoExistente.getUsuarioId(),
					alunoExistente.getAlunoId(),
					nome,
					email,
					telefone,
					objetivo,
					status,
					senhaHash
			);
			if (!ok) {
				mostrar(Alert.AlertType.ERROR, "Nao foi possivel atualizar o aluno.");
				return;
			}
		} else {
			ok = usuarioDAO.criarAluno(nome, email, senhaHash, telefone, objetivo);
			if (!ok) {
				mostrar(Alert.AlertType.ERROR, "Nao foi possivel cadastrar o aluno. Verifique se o email ja existe.");
				return;
			}
		}

		carregarAlunos(txtBusca.getText());
	}

	private void salvarEdicao(AlunoResumo aluno) {
		boolean ok = usuarioDAO.atualizarAluno(
				aluno.getUsuarioId(),
				aluno.getAlunoId(),
				aluno.getNome(),
				aluno.getEmail(),
				aluno.getTelefone(),
				aluno.getObjetivo(),
				aluno.getStatus(),
				null
		);

		if (!ok) {
			mostrar(Alert.AlertType.ERROR, "Nao foi possivel salvar a alteracao.");
			carregarAlunos(txtBusca.getText());
		}
	}

	private void onDeletarAluno(AlunoResumo aluno) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("Excluir o aluno " + aluno.getNome() + "?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			boolean ok = usuarioDAO.deletarAluno(aluno.getUsuarioId());
			if (!ok) {
				mostrar(Alert.AlertType.ERROR, "Nao foi possivel excluir o aluno.");
				return;
			}
			carregarAlunos(txtBusca.getText());
		}
	}

	private void carregarAlunos(String termo) {
		List<AlunoResumo> alunos = usuarioDAO.listarAlunosResumo(termo);
		tblAlunos.setItems(FXCollections.observableArrayList(alunos));
		int total = alunos.size();
		if (total == 0) {
			lblTotal.setText("Showing 0 entries");
			return;
		}
		lblTotal.setText("Showing 1 to " + total + " entries");
	}

	private void mostrar(Alert.AlertType tipo, String mensagem) {
		Alert alert = new Alert(tipo);
		alert.setHeaderText(null);
		alert.setContentText(mensagem);
		alert.showAndWait();
	}
}
