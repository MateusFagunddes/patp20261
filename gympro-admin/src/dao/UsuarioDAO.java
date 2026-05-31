package dao;

import conexao.Conexao;
import entity.AlunoResumo;
import entity.AvisoResumo;
import entity.PersonalResumo;
import entity.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // insert
    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, status) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getStatus());

            stmt.executeUpdate();

            System.out.println("Usuário inserido com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // select
    public List<Usuario> listaUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try {
            Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario a = new Usuario();

                a.setId(rs.getInt("id"));
                a.setNome(rs.getString("nome"));
                a.setEmail(rs.getString("email"));
                a.setSenha(rs.getString("senha"));
                a.setStatus(rs.getString("status"));

                lista.add(a);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar: " + e.getMessage());
        }

        return lista;
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ? LIMIT 1";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return null;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setStatus(rs.getString("status"));
                return usuario;
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar usuario por email: " + e.getMessage());
        }

        return null;
    }

    public boolean usuarioTemPerfil(int usuarioId, int perfilId) {
        String sql = "SELECT 1 FROM usuario_perfil WHERE usuario_id = ? AND perfil_id = ? LIMIT 1";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return false;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, perfilId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Erro ao verificar perfil do usuario: " + e.getMessage());
            return false;
        }
    }

    public String obterPerfilPrincipal(int usuarioId) {
        String sql = "SELECT p.nome FROM perfis p INNER JOIN usuario_perfil up ON up.perfil_id = p.id WHERE up.usuario_id = ? ORDER BY p.id ASC LIMIT 1";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return "ALUNO";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nome");
            }
        } catch (Exception e) {
            System.out.println("Erro ao obter perfil principal: " + e.getMessage());
        }

        return "ALUNO";
    }

    public int contarAlunos() {
        return contar("SELECT COUNT(*) AS total FROM alunos");
    }

    public int contarPersonais() {
        return contar("SELECT COUNT(*) AS total FROM personais");
    }

    public int contarTreinosAtivos() {
        return contar("SELECT COUNT(*) AS total FROM treinos WHERE status = 'ATIVO'");
    }

    public int contarUsuariosPendentes() {
        return contar("SELECT COUNT(*) AS total FROM usuarios WHERE status = 'PENDENTE'");
    }

    public int contarMeusAlunosPersonal(int usuarioId) {
        String sql = "SELECT COUNT(*) AS total FROM aluno_personal ap INNER JOIN personais p ON p.id = ap.personal_id WHERE p.usuario_id = ?";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return 0;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.out.println("Erro ao contar alunos do personal: " + e.getMessage());
        }

        return 0;
    }

    public int contarMeusTreinosAtivosPersonal(int usuarioId) {
        String sql = "SELECT COUNT(*) AS total FROM treinos t INNER JOIN personais p ON p.id = t.personal_id WHERE p.usuario_id = ? AND t.status = 'ATIVO'";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return 0;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.out.println("Erro ao contar treinos do personal: " + e.getMessage());
        }

        return 0;
    }

    public String buscarUltimoAvisoTitulo() {
        String sql = "SELECT titulo FROM avisos ORDER BY data_publicacao DESC, id DESC LIMIT 1";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return "Sem conexao com o banco";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("titulo");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar ultimo aviso: " + e.getMessage());
        }

        return "Nenhum aviso publicado";
    }

    public List<AlunoResumo> listarAlunosResumo(String termo) {
        List<AlunoResumo> alunos = new ArrayList<>();
        String sql = "SELECT u.nome, u.email, COALESCE(a.telefone, '') AS telefone, COALESCE(a.objetivo, '') AS objetivo, u.status "
                + "FROM alunos a INNER JOIN usuarios u ON u.id = a.usuario_id "
                + "WHERE (? = '' OR u.nome LIKE CONCAT('%', ?, '%') OR u.email LIKE CONCAT('%', ?, '%')) "
                + "ORDER BY u.nome ASC";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return alunos;
            }
            String filtro = termo == null ? "" : termo.trim();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            stmt.setString(3, filtro);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                alunos.add(new AlunoResumo(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("objetivo"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }

        return alunos;
    }

    public List<PersonalResumo> listarPersonaisResumo(String termo) {
        List<PersonalResumo> personais = new ArrayList<>();
        String sql = "SELECT u.nome, u.email, COALESCE(p.cref, '') AS cref, COALESCE(p.especialidade, '') AS especialidade, u.status "
                + "FROM personais p INNER JOIN usuarios u ON u.id = p.usuario_id "
                + "WHERE (? = '' OR u.nome LIKE CONCAT('%', ?, '%') OR u.email LIKE CONCAT('%', ?, '%')) "
                + "ORDER BY u.nome ASC";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return personais;
            }
            String filtro = termo == null ? "" : termo.trim();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            stmt.setString(3, filtro);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                personais.add(new PersonalResumo(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("cref"),
                        rs.getString("especialidade"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar personais: " + e.getMessage());
        }

        return personais;
    }

    public List<AvisoResumo> listarAvisosResumo() {
        List<AvisoResumo> avisos = new ArrayList<>();
        String sql = "SELECT a.id, a.titulo, a.descricao, a.data_publicacao, a.publico, u.nome AS autor "
                + "FROM avisos a INNER JOIN usuarios u ON u.id = a.autor_id "
                + "ORDER BY a.data_publicacao DESC, a.id DESC";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return avisos;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                avisos.add(new AvisoResumo(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getString("data_publicacao"),
                        rs.getString("publico"),
                        rs.getString("autor")
                ));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar avisos: " + e.getMessage());
        }

        return avisos;
    }

    public boolean inserirAviso(String titulo, String descricao, String publico, int autorId) {
        String sql = "INSERT INTO avisos (titulo, descricao, data_publicacao, autor_id, publico) VALUES (?, ?, CURDATE(), ?, ?)";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return false;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, titulo);
            stmt.setString(2, descricao);
            stmt.setInt(3, autorId);
            stmt.setString(4, publico);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao inserir aviso: " + e.getMessage());
            return false;
        }
    }

    private int contar(String sql) {
        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return 0;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.out.println("Erro ao executar contagem: " + e.getMessage());
        }

        return 0;
    }

    // update
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome=?, email=?, senha=?, status=? WHERE id=?";

        try {
            Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getStatus());
            stmt.setInt(5, usuario.getId());

            stmt.executeUpdate();

            System.out.println("Usuario atualizado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }
    //deletar
    public void deletar( int id) {
      String sql = "DELETE FROM usuarios WHERE id=?";

      try { 
         Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql);

         stmt.setInt(1, id);

         stmt.executeUpdate();

         System.out.println("Usuario excluido com sucesso!");

      } catch (Exception e) {
         System.out.println("Erro ao excluir Usuario" + e.getMessage());
      }
   }
}
