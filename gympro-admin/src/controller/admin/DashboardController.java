package controller.admin;

import dao.UsuarioDAO;
import entity.Usuario;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardController {

	@FXML
	private Label lblFrameTitulo;

	@FXML
	private Label lblFrameSubtitulo;

	@FXML
	private Label lblUsuario;

	@FXML
	private Label lblPerfil;

	@FXML
	private Label lblMetrica1Titulo;

	@FXML
	private Label lblMetrica1Valor;

	@FXML
	private Label lblMetrica2Titulo;

	@FXML
	private Label lblMetrica2Valor;

	@FXML
	private Label lblMetrica3Titulo;

	@FXML
	private Label lblMetrica3Valor;

	@FXML
	private Label lblMetrica4Titulo;

	@FXML
	private Label lblMetrica4Valor;

	@FXML
	private Label lblAviso;

	@FXML
	private Button btnAlunos;

	@FXML
	private Button btnPersonais;

	@FXML
	private Button btnAvisos;

	@FXML
	private Button btnDashboard;

	@FXML
	private Button btnSair;

	@FXML
	private Button btnToggleSidebar;

	@FXML
	private Label lblBrand;

	@FXML
	private VBox sidebar;

	@FXML
	private StackPane contentPane;

	@FXML
	private VBox homePane;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario usuarioLogado;

	private String perfilLogado;

	private static final String BTN_ATIVO_STYLE = "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;";

	private static final String BTN_INATIVO_STYLE = "-fx-background-color: #1f2937; -fx-text-fill: #e5e7eb; -fx-background-radius: 8;";

	private static final String ITEM_DASHBOARD = "\u2302  Dashboard";

	private static final String ITEM_ALUNOS = "\u263A  Alunos";

	private static final String ITEM_PERSONAIS = "\u2699  Personais";

	private static final String ITEM_AVISOS = "\u2709  Avisos";

	private static final String ITEM_SAIR = "\u21AA  Sair";

	private static final String ICON_DASHBOARD = "\u2302";

	private static final String ICON_ALUNOS = "\u263A";

	private static final String ICON_PERSONAIS = "\u2699";

	private static final String ICON_AVISOS = "\u2709";

	private static final String ICON_SAIR = "\u21AA";

	private static final double SIDEBAR_EXPANDED_WIDTH = 240.0;

	private static final double SIDEBAR_COLLAPSED_WIDTH = 80.0;

	private static final Duration TRANSITION_DURATION = Duration.millis(300);

	private boolean sidebarColapsada;

	private String tituloDashboard;

	private String subtituloDashboard;

	private Timeline sidebarTimeline;

	@FXML
	private void initialize() {
		btnDashboard.setMaxWidth(Double.MAX_VALUE);
		btnAlunos.setMaxWidth(Double.MAX_VALUE);
		btnPersonais.setMaxWidth(Double.MAX_VALUE);
		btnAvisos.setMaxWidth(Double.MAX_VALUE);
		btnSair.setMaxWidth(Double.MAX_VALUE);
		btnToggleSidebar.setText("\u2630");
		btnToggleSidebar.setTooltip(new Tooltip("Recolher/expandir menu"));
		definirTextosExpandidos();
		sidebarColapsada = false;
		sidebar.setPrefWidth(SIDEBAR_EXPANDED_WIDTH);
		sidebar.setMinWidth(SIDEBAR_EXPANDED_WIDTH);
		sidebar.setMaxWidth(SIDEBAR_EXPANDED_WIDTH);
	}

	public void configurar(Usuario usuario, String perfil) {
		String perfilNormalizado = perfil == null ? "ALUNO" : perfil.toUpperCase();
		this.usuarioLogado = usuario;
		this.perfilLogado = perfilNormalizado;

		lblUsuario.setText("Usuario: " + usuario.getNome() + " (" + usuario.getEmail() + ")");
		lblPerfil.setText("Perfil: " + perfilNormalizado);
		lblAviso.setText("Ultimo aviso: " + usuarioDAO.buscarUltimoAvisoTitulo());

		boolean admin = "ADMIN".equals(perfilNormalizado);
		btnAlunos.setDisable(!admin);
		btnPersonais.setDisable(!admin);
		btnAvisos.setDisable(false);
		btnDashboard.setDisable(false);

		if ("ADMIN".equals(perfilNormalizado)) {
			tituloDashboard = "Dashboard";
			subtituloDashboard = "Visao geral do administrador";
			lblMetrica1Titulo.setText("Alunos");
			lblMetrica1Valor.setText(String.valueOf(usuarioDAO.contarAlunos()));
			lblMetrica2Titulo.setText("Personais");
			lblMetrica2Valor.setText(String.valueOf(usuarioDAO.contarPersonais()));
			lblMetrica3Titulo.setText("Treinos Ativos");
			lblMetrica3Valor.setText(String.valueOf(usuarioDAO.contarTreinosAtivos()));
			lblMetrica4Titulo.setText("Pendentes");
			lblMetrica4Valor.setText(String.valueOf(usuarioDAO.contarUsuariosPendentes()));
		} else if ("PERSONAL".equals(perfilNormalizado)) {
			tituloDashboard = "Dashboard";
			subtituloDashboard = "Visao geral do personal";
			lblMetrica1Titulo.setText("Meus Alunos");
			lblMetrica1Valor.setText(String.valueOf(usuarioDAO.contarMeusAlunosPersonal(usuario.getId())));
			lblMetrica2Titulo.setText("Meus Treinos Ativos");
			lblMetrica2Valor.setText(String.valueOf(usuarioDAO.contarMeusTreinosAtivosPersonal(usuario.getId())));
			lblMetrica3Titulo.setText("Total Alunos");
			lblMetrica3Valor.setText(String.valueOf(usuarioDAO.contarAlunos()));
			lblMetrica4Titulo.setText("Total Personais");
			lblMetrica4Valor.setText(String.valueOf(usuarioDAO.contarPersonais()));
		} else {
			tituloDashboard = "Dashboard";
			subtituloDashboard = "Visao geral do aluno";
			lblMetrica1Titulo.setText("Treinos Ativos");
			lblMetrica1Valor.setText(String.valueOf(usuarioDAO.contarTreinosAtivos()));
			lblMetrica2Titulo.setText("Avisos");
			lblMetrica2Valor.setText(usuarioDAO.buscarUltimoAvisoTitulo().isEmpty() ? "0" : "1");
			lblMetrica3Titulo.setText("Status");
			lblMetrica3Valor.setText(usuario.getStatus());
			lblMetrica4Titulo.setText("Perfil");
			lblMetrica4Valor.setText(perfilNormalizado);
		}

		onAbrirHome();
	}

	@FXML
	private void onAbrirHome() {
		trocarConteudo(homePane, tituloDashboard, subtituloDashboard);
		aplicarBotaoAtivo(btnDashboard);
	}

	@FXML
	private void onAbrirAlunos() {
		if (!"ADMIN".equals(perfilLogado)) {
			mostrarAvisoPermissao();
			return;
		}
		carregarNoFrame("/view/admin/alunos.fxml", "Alunos", "Gerenciamento de alunos");
		aplicarBotaoAtivo(btnAlunos);
	}

	@FXML
	private void onAbrirPersonais() {
		if (!"ADMIN".equals(perfilLogado)) {
			mostrarAvisoPermissao();
			return;
		}
		carregarNoFrame("/view/admin/personais.fxml", "Personais", "Gerenciamento de personais");
		aplicarBotaoAtivo(btnPersonais);
	}

	@FXML
	private void onAbrirAvisos() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/avisos.fxml"));
			Parent root = loader.load();
			AvisoController controller = loader.getController();
			controller.configurar(usuarioLogado.getId(), "ADMIN".equals(perfilLogado));
			trocarConteudo(root, "Avisos", "Comunicados internos da academia");
			aplicarBotaoAtivo(btnAvisos);
		} catch (IOException e) {
			mostrarErro("Erro ao abrir tela de avisos: " + e.getMessage());
		}
	}

	private void carregarNoFrame(String caminho, String titulo, String subtitulo) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(caminho));
			trocarConteudo(root, titulo, subtitulo);
		} catch (IOException e) {
			mostrarErro("Erro ao abrir tela: " + e.getMessage());
		}
	}

	private void trocarConteudo(Node novoConteudo, String titulo, String subtitulo) {
		lblFrameTitulo.setText(titulo);
		lblFrameSubtitulo.setText(subtitulo);

		Node conteudoAtual = contentPane.getChildren().isEmpty() ? null : contentPane.getChildren().get(0);
		if (conteudoAtual == null) {
			novoConteudo.setOpacity(0);
			contentPane.getChildren().setAll(novoConteudo);
			FadeTransition fadeInDireto = new FadeTransition(TRANSITION_DURATION, novoConteudo);
			fadeInDireto.setFromValue(0);
			fadeInDireto.setToValue(1);
			fadeInDireto.play();
			return;
		}

		FadeTransition fadeOut = new FadeTransition(TRANSITION_DURATION.divide(2), conteudoAtual);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);
		fadeOut.setOnFinished(event -> {
			novoConteudo.setOpacity(0);
			contentPane.getChildren().setAll(novoConteudo);
		});

		FadeTransition fadeIn = new FadeTransition(TRANSITION_DURATION.divide(2), novoConteudo);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		SequentialTransition sequencia = new SequentialTransition(fadeOut, fadeIn);
		sequencia.play();
	}

	private void aplicarBotaoAtivo(Button botaoAtivo) {
		btnDashboard.setStyle(BTN_INATIVO_STYLE);
		btnAlunos.setStyle(BTN_INATIVO_STYLE);
		btnPersonais.setStyle(BTN_INATIVO_STYLE);
		btnAvisos.setStyle(BTN_INATIVO_STYLE);
		botaoAtivo.setStyle(BTN_ATIVO_STYLE);
	}

	@FXML
	private void onToggleSidebar() {
		if (sidebarTimeline != null) {
			sidebarTimeline.stop();
		}

		sidebarColapsada = !sidebarColapsada;
		double larguraAtual = sidebar.getWidth() <= 0 ? sidebar.getPrefWidth() : sidebar.getWidth();

		if (sidebarColapsada) {
			sidebar.setMinWidth(Region.USE_COMPUTED_SIZE);
			sidebar.setMaxWidth(Double.MAX_VALUE);
			animarTextoSidebar(false);

			btnDashboard.setText(ICON_DASHBOARD);
			btnAlunos.setText(ICON_ALUNOS);
			btnPersonais.setText(ICON_PERSONAIS);
			btnAvisos.setText(ICON_AVISOS);
			btnSair.setText(ICON_SAIR);

			Tooltip.install(btnDashboard, new Tooltip("Dashboard"));
			Tooltip.install(btnAlunos, new Tooltip("Alunos"));
			Tooltip.install(btnPersonais, new Tooltip("Personais"));
			Tooltip.install(btnAvisos, new Tooltip("Avisos"));
			Tooltip.install(btnSair, new Tooltip("Sair"));
			animarLarguraSidebar(larguraAtual, SIDEBAR_COLLAPSED_WIDTH);
			return;
		}

		sidebar.setMinWidth(Region.USE_COMPUTED_SIZE);
		sidebar.setMaxWidth(Double.MAX_VALUE);
		animarTextoSidebar(true);

		definirTextosExpandidos();

		btnDashboard.setTooltip(null);
		btnAlunos.setTooltip(null);
		btnPersonais.setTooltip(null);
		btnAvisos.setTooltip(null);
		btnSair.setTooltip(null);

		animarLarguraSidebar(larguraAtual, SIDEBAR_EXPANDED_WIDTH);
	}

	private void animarLarguraSidebar(double de, double para) {
		sidebarTimeline = new Timeline(
			new KeyFrame(Duration.ZERO, new KeyValue(sidebar.prefWidthProperty(), de, Interpolator.EASE_BOTH)),
			new KeyFrame(TRANSITION_DURATION, new KeyValue(sidebar.prefWidthProperty(), para, Interpolator.EASE_BOTH))
		);

		sidebarTimeline.setOnFinished(event -> {
			sidebar.setMinWidth(para);
			sidebar.setMaxWidth(para);
		});

		sidebarTimeline.play();
	}

	private void animarTextoSidebar(boolean exibir) {
		if (exibir) {
			lblBrand.setManaged(true);
			lblUsuario.setManaged(true);
			lblPerfil.setManaged(true);
			lblBrand.setVisible(true);
			lblUsuario.setVisible(true);
			lblPerfil.setVisible(true);

			lblBrand.setOpacity(0);
			lblUsuario.setOpacity(0);
			lblPerfil.setOpacity(0);

			FadeTransition fadeBrand = new FadeTransition(TRANSITION_DURATION.divide(2), lblBrand);
			fadeBrand.setFromValue(0);
			fadeBrand.setToValue(1);

			FadeTransition fadeUsuario = new FadeTransition(TRANSITION_DURATION.divide(2), lblUsuario);
			fadeUsuario.setFromValue(0);
			fadeUsuario.setToValue(1);

			FadeTransition fadePerfil = new FadeTransition(TRANSITION_DURATION.divide(2), lblPerfil);
			fadePerfil.setFromValue(0);
			fadePerfil.setToValue(1);

			new ParallelTransition(fadeBrand, fadeUsuario, fadePerfil).play();
			return;
		}

		FadeTransition fadeBrand = new FadeTransition(TRANSITION_DURATION.divide(2), lblBrand);
		fadeBrand.setFromValue(lblBrand.getOpacity());
		fadeBrand.setToValue(0);

		FadeTransition fadeUsuario = new FadeTransition(TRANSITION_DURATION.divide(2), lblUsuario);
		fadeUsuario.setFromValue(lblUsuario.getOpacity());
		fadeUsuario.setToValue(0);

		FadeTransition fadePerfil = new FadeTransition(TRANSITION_DURATION.divide(2), lblPerfil);
		fadePerfil.setFromValue(lblPerfil.getOpacity());
		fadePerfil.setToValue(0);

		ParallelTransition paralelo = new ParallelTransition(fadeBrand, fadeUsuario, fadePerfil);
		paralelo.setOnFinished(event -> {
			lblBrand.setVisible(false);
			lblUsuario.setVisible(false);
			lblPerfil.setVisible(false);
			lblBrand.setManaged(false);
			lblUsuario.setManaged(false);
			lblPerfil.setManaged(false);
		});
		paralelo.play();
	}

	private void definirTextosExpandidos() {
		btnDashboard.setText(ITEM_DASHBOARD);
		btnAlunos.setText(ITEM_ALUNOS);
		btnPersonais.setText(ITEM_PERSONAIS);
		btnAvisos.setText(ITEM_AVISOS);
		btnSair.setText(ITEM_SAIR);
	}

	private void mostrarErro(String mensagem) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Erro");
		alert.setHeaderText(null);
		alert.setContentText(mensagem);
		alert.showAndWait();
	}

	private void mostrarAvisoPermissao() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Permissao");
		alert.setHeaderText(null);
		alert.setContentText("Seu perfil nao possui acesso a esta tela.");
		alert.showAndWait();
	}

	@FXML
	private void onSair() {
		Stage stage = (Stage) lblFrameTitulo.getScene().getWindow();
		stage.close();
	}
}
