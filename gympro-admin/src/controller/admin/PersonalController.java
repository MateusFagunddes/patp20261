package controller.admin;

import dao.UsuarioDAO;
import entity.PersonalResumo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PersonalController {

	@FXML
	private TextField txtBusca;

	@FXML
	private TableView<PersonalResumo> tblPersonais;

	@FXML
	private TableColumn<PersonalResumo, String> colNome;

	@FXML
	private TableColumn<PersonalResumo, String> colEmail;

	@FXML
	private TableColumn<PersonalResumo, String> colCref;

	@FXML
	private TableColumn<PersonalResumo, String> colEspecialidade;

	@FXML
	private TableColumn<PersonalResumo, String> colStatus;

	@FXML
	private Label lblTotal;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	@FXML
	private void initialize() {
		colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colCref.setCellValueFactory(new PropertyValueFactory<>("cref"));
		colEspecialidade.setCellValueFactory(new PropertyValueFactory<>("especialidade"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		carregarPersonais("");
	}

	@FXML
	private void onPesquisar() {
		carregarPersonais(txtBusca.getText());
	}

	private void carregarPersonais(String termo) {
		List<PersonalResumo> personais = usuarioDAO.listarPersonaisResumo(termo);
		tblPersonais.setItems(FXCollections.observableArrayList(personais));
		int total = personais.size();
		if (total == 0) {
			lblTotal.setText("Showing 0 entries");
			return;
		}
		lblTotal.setText("Showing 1 to " + total + " entries");
	}
}
