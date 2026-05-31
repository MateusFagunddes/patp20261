package controller.admin;

import dao.UsuarioDAO;
import entity.AlunoResumo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

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
	private Label lblTotal;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	@FXML
	private void initialize() {
		colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		colObjetivo.setCellValueFactory(new PropertyValueFactory<>("objetivo"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		carregarAlunos("");
	}

	@FXML
	private void onPesquisar() {
		carregarAlunos(txtBusca.getText());
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
}
