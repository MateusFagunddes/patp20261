package controller.login;

import controller.admin.DashboardController;
import dao.UsuarioDAO;
import entity.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class LoginController {
    private static final int PERFIL_ADMIN_ID = 1;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Button btnCadastrar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void initialize() {
        btnCadastrar.setVisible(false);
        btnCadastrar.setManaged(false);
    }

    @FXML
    private void onEntrar(ActionEvent event) {
        String email = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String senha = txtSenha.getText() == null ? "" : txtSenha.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrar(Alert.AlertType.WARNING, "Login", "Informe usuario e senha.");
            return;
        }

        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            ocultarCadastro();
            mostrar(Alert.AlertType.ERROR, "Login", "Usuario nao encontrado ou banco indisponivel.");
            return;
        }

        if (!"ATIVO".equalsIgnoreCase(usuario.getStatus())) {
            ocultarCadastro();
            mostrar(Alert.AlertType.WARNING, "Login", "Usuario encontrado, mas nao esta ativo.");
            return;
        }

        String senhaBanco = usuario.getSenha() == null ? "" : usuario.getSenha();
        if (validarSenha(senha, senhaBanco)) {
            boolean liberarCadastro = usuarioDAO.usuarioTemPerfil(usuario.getId(), PERFIL_ADMIN_ID);
            btnCadastrar.setVisible(liberarCadastro);
            btnCadastrar.setManaged(liberarCadastro);
            String perfilPrincipal = usuarioDAO.obterPerfilPrincipal(usuario.getId());
            try {
                abrirDashboard(usuario, perfilPrincipal);
                Stage loginStage = (Stage) txtUsuario.getScene().getWindow();
                loginStage.close();
            } catch (IOException e) {
                mostrar(Alert.AlertType.ERROR, "Login", "Erro ao abrir dashboard: " + e.getMessage());
            }
            return;
        }

        ocultarCadastro();
        mostrar(Alert.AlertType.ERROR, "Login", "Senha invalida.");
    }

    @FXML
    private void onCadastrar(ActionEvent event) {
        mostrar(Alert.AlertType.INFORMATION, "Cadastro", "Fluxo de cadastro ainda nao foi implementado no desktop.");
    }

    private void mostrar(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void ocultarCadastro() {
        btnCadastrar.setVisible(false);
        btnCadastrar.setManaged(false);
    }

    private boolean validarSenha(String senhaInformada, String senhaBanco) {
        if (senhaInformada.equals(senhaBanco)) {
            return true;
        }

        if (!(senhaBanco.startsWith("$2y$") || senhaBanco.startsWith("$2a$") || senhaBanco.startsWith("$2b$"))) {
            return false;
        }

        String hashNormalizado = senhaBanco.startsWith("$2y$") ? "$2a$" + senhaBanco.substring(4) : senhaBanco;
        try {
            return BCrypt.checkpw(senhaInformada, hashNormalizado);
        } catch (Exception e) {
            return false;
        }
    }

    private void abrirDashboard(Usuario usuario, String perfil) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard/dashboard.fxml"));
        Parent root = loader.load();
        DashboardController controller = loader.getController();
        controller.configurar(usuario, perfil);

        Stage stage = new Stage();
        stage.setTitle("Dashboard - " + perfil);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
