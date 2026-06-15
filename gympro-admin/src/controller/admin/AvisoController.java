package controller.admin;

import dao.UsuarioDAO;
import entity.AvisoResumo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

public class AvisoController {

	@FXML
	private TextField txtTitulo;

	@FXML
	private TextArea txtDescricao;

	@FXML
	private ComboBox<String> cbPublico;

	@FXML
	private TableView<AvisoResumo> tblAvisos;

	@FXML
	private TableColumn<AvisoResumo, String> colData;

	@FXML
	private TableColumn<AvisoResumo, String> colTitulo;

	@FXML
	private TableColumn<AvisoResumo, String> colPublico;

	@FXML
	private TableColumn<AvisoResumo, String> colAutor;

	@FXML
	private TableColumn<AvisoResumo, Void> colAcoes;

	@FXML
	private Label lblStatus;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private int autorId;

	private boolean podePublicar;

	@FXML
	private void initialize() {
		colData.setCellValueFactory(new PropertyValueFactory<>("dataPublicacao"));
		colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
		colPublico.setCellValueFactory(new PropertyValueFactory<>("publico"));
		colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
		cbPublico.setItems(FXCollections.observableArrayList("TODOS", "ALUNOS", "PERSONAIS"));
		cbPublico.getSelectionModel().select("TODOS");

		colAcoes.setCellFactory(col -> new TableCell<>() {
			private final Button btnExcluir = new Button("X");
			{
				setAlignment(Pos.CENTER_RIGHT);
				btnExcluir.getStyleClass().add("btn-danger");
				btnExcluir.setOnAction(ev -> deletar(getTableView().getItems().get(getIndex())));
			}
			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : btnExcluir);
			}
		});

		tblAvisos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
		carregarAvisos();
	}

	public void configurar(int autorId, boolean podePublicar) {
		this.autorId = autorId;
		this.podePublicar = podePublicar;
		txtTitulo.setDisable(!podePublicar);
		txtDescricao.setDisable(!podePublicar);
		cbPublico.setDisable(!podePublicar);
		lblStatus.setText(podePublicar ? "Publicacao habilitada" : "Visualizacao apenas");
	}

	@FXML
	private void onPublicar() {
		if (!podePublicar) {
			mostrar(Alert.AlertType.WARNING, "Avisos", "Seu perfil nao possui permissao para publicar avisos.");
			return;
		}

		String titulo = txtTitulo.getText() == null ? "" : txtTitulo.getText().trim();
		String descricao = txtDescricao.getText() == null ? "" : txtDescricao.getText().trim();
		String publico = cbPublico.getValue() == null ? "TODOS" : cbPublico.getValue();

		if (titulo.isEmpty() || descricao.isEmpty()) {
			mostrar(Alert.AlertType.WARNING, "Avisos", "Preencha titulo e descricao.");
			return;
		}

		boolean ok = usuarioDAO.inserirAviso(titulo, descricao, publico, autorId);
		if (!ok) {
			mostrar(Alert.AlertType.ERROR, "Avisos", "Nao foi possivel publicar o aviso.");
			return;
		}

		txtTitulo.clear();
		txtDescricao.clear();
		cbPublico.getSelectionModel().select("TODOS");
		carregarAvisos();
		mostrar(Alert.AlertType.INFORMATION, "Avisos", "Aviso publicado com sucesso.");
	}

	private void deletar(AvisoResumo aviso) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("Excluir o aviso \"" + aviso.getTitulo() + "\"?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			if (!usuarioDAO.deletarAviso(aviso.getId())) {
				mostrar(Alert.AlertType.ERROR, "Avisos", "Nao foi possivel excluir o aviso.");
				return;
			}
			carregarAvisos();
		}
	}

	private void carregarAvisos() {
		List<AvisoResumo> avisos = usuarioDAO.listarAvisosResumo();
		tblAvisos.setItems(FXCollections.observableArrayList(avisos));
	}

	private void mostrar(Alert.AlertType tipo, String titulo, String mensagem) {
		Alert alert = new Alert(tipo);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(mensagem);
		alert.showAndWait();
	}
}
