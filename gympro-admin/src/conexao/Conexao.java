package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String DB_MODE = System.getenv().getOrDefault("DB_MODE", "local");
    private static final String LOCAL_HOST = System.getenv().getOrDefault("DB_LOCAL_HOST", "127.0.0.1");
    private static final String LOCAL_PORT = System.getenv().getOrDefault("DB_LOCAL_PORT", "3306");
    private static final String LOCAL_NAME = System.getenv().getOrDefault("DB_LOCAL_NAME", "gympro");
    private static final String LOCAL_USER = System.getenv().getOrDefault("DB_LOCAL_USER", "root");
    private static final String LOCAL_PASS = System.getenv().getOrDefault("DB_LOCAL_PASS", "");
    private static final String REMOTE_HOST = System.getenv().getOrDefault("DB_REMOTE_HOST", "26.97.201.138");
    private static final String REMOTE_PORT = System.getenv().getOrDefault("DB_REMOTE_PORT", "3306");
    private static final String REMOTE_NAME = System.getenv().getOrDefault("DB_REMOTE_NAME", "gympro");
    private static final String REMOTE_USER = System.getenv().getOrDefault("DB_REMOTE_USER", "gympro_user");
    private static final String REMOTE_PASS = System.getenv().getOrDefault("DB_REMOTE_PASS", "123456");

    private static String getUrl() {
        if ("remote".equalsIgnoreCase(DB_MODE)) {
            return "jdbc:mysql://" + REMOTE_HOST + ":" + REMOTE_PORT + "/" + REMOTE_NAME + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }
        return "jdbc:mysql://" + LOCAL_HOST + ":" + LOCAL_PORT + "/" + LOCAL_NAME + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String getUser() {
        return "remote".equalsIgnoreCase(DB_MODE) ? REMOTE_USER : LOCAL_USER;
    }

    private static String getPassword() {
        return "remote".equalsIgnoreCase(DB_MODE) ? REMOTE_PASS : LOCAL_PASS;
    }

    public static Connection getConexao() {
        try {
            Connection conn = DriverManager.getConnection(getUrl(), getUser(), getPassword());
            System.out.println("Conectado com sucesso!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }
}