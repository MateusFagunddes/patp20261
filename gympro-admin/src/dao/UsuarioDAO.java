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
        String sql = "SELECT a.id AS aluno_id, a.usuario_id, u.nome, u.email, COALESCE(a.telefone, '') AS telefone, COALESCE(a.objetivo, '') AS objetivo, u.status "
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
                        rs.getInt("aluno_id"),
                        rs.getInt("usuario_id"),
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

    public boolean criarAluno(String nome, String email, String senhaHash, String telefone, String objetivo) {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha, status) VALUES (?, ?, ?, 'ATIVO')";
        String sqlAluno = "INSERT INTO alunos (usuario_id, telefone, objetivo) VALUES (?, ?, ?)";
        String sqlPerfil = "INSERT INTO usuario_perfil (usuario_id, perfil_id) VALUES (?, 3)";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);
            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            stmtUsuario.setString(1, nome);
            stmtUsuario.setString(2, email);
            stmtUsuario.setString(3, senhaHash);
            stmtUsuario.executeUpdate();

            ResultSet keys = stmtUsuario.getGeneratedKeys();
            if (!keys.next()) {
                conn.rollback();
                conn.setAutoCommit(true);
                return false;
            }

            int usuarioId = keys.getInt(1);

            PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno);
            stmtAluno.setInt(1, usuarioId);
            stmtAluno.setString(2, telefone == null || telefone.isBlank() ? null : telefone);
            stmtAluno.setString(3, objetivo == null || objetivo.isBlank() ? null : objetivo);
            stmtAluno.executeUpdate();

            PreparedStatement stmtPerfil = conn.prepareStatement(sqlPerfil);
            stmtPerfil.setInt(1, usuarioId);
            stmtPerfil.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao criar aluno: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarAluno(int usuarioId, int alunoId, String nome, String email, String telefone, String objetivo, String status, String senhaHash) {
        String sqlUsuario = senhaHash == null || senhaHash.isBlank()
                ? "UPDATE usuarios SET nome = ?, email = ?, status = ? WHERE id = ?"
                : "UPDATE usuarios SET nome = ?, email = ?, senha = ?, status = ? WHERE id = ?";
        String sqlAluno = "UPDATE alunos SET telefone = ?, objetivo = ? WHERE id = ?";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return false;
            }

            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario);
            stmtUsuario.setString(1, nome);
            stmtUsuario.setString(2, email);
            if (senhaHash == null || senhaHash.isBlank()) {
                stmtUsuario.setString(3, status);
                stmtUsuario.setInt(4, usuarioId);
            } else {
                stmtUsuario.setString(3, senhaHash);
                stmtUsuario.setString(4, status);
                stmtUsuario.setInt(5, usuarioId);
            }
            stmtUsuario.executeUpdate();

            PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno);
            stmtAluno.setString(1, telefone == null || telefone.isBlank() ? null : telefone);
            stmtAluno.setString(2, objetivo == null || objetivo.isBlank() ? null : objetivo);
            stmtAluno.setInt(3, alunoId);
            stmtAluno.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao atualizar aluno: " + e.getMessage());
            return false;
        }
    }

    public boolean deletarAluno(int usuarioId) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) {
                return false;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao deletar aluno: " + e.getMessage());
            return false;
        }
    }

    public List<PersonalResumo> listarPersonaisResumo(String termo) {
        List<PersonalResumo> personais = new ArrayList<>();
        String sql = "SELECT p.id AS personal_id, p.usuario_id, u.nome, u.email, COALESCE(p.cref, '') AS cref, COALESCE(p.especialidade, '') AS especialidade, u.status "
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
                        rs.getInt("personal_id"),
                        rs.getInt("usuario_id"),
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

    public boolean criarPersonal(String nome, String email, String senhaHash, String cref, String especialidade) {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha, status) VALUES (?, ?, ?, 'ATIVO')";
        String sqlPersonal = "INSERT INTO personais (usuario_id, cref, especialidade) VALUES (?, ?, ?)";
        String sqlPerfil = "INSERT INTO usuario_perfil (usuario_id, perfil_id) VALUES (?, 2)";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) return false;

            conn.setAutoCommit(false);
            PreparedStatement stmtU = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            stmtU.setString(1, nome);
            stmtU.setString(2, email);
            stmtU.setString(3, senhaHash);
            stmtU.executeUpdate();

            ResultSet keys = stmtU.getGeneratedKeys();
            if (!keys.next()) { conn.rollback(); conn.setAutoCommit(true); return false; }
            int usuarioId = keys.getInt(1);

            PreparedStatement stmtP = conn.prepareStatement(sqlPersonal);
            stmtP.setInt(1, usuarioId);
            stmtP.setString(2, cref == null || cref.isBlank() ? null : cref);
            stmtP.setString(3, especialidade == null || especialidade.isBlank() ? null : especialidade);
            stmtP.executeUpdate();

            PreparedStatement stmtPerfil = conn.prepareStatement(sqlPerfil);
            stmtPerfil.setInt(1, usuarioId);
            stmtPerfil.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao criar personal: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarPersonal(int usuarioId, int personalId, String nome, String email, String cref, String especialidade, String status, String senhaHash) {
        String sqlUsuario = senhaHash == null || senhaHash.isBlank()
                ? "UPDATE usuarios SET nome = ?, email = ?, status = ? WHERE id = ?"
                : "UPDATE usuarios SET nome = ?, email = ?, senha = ?, status = ? WHERE id = ?";
        String sqlPersonal = "UPDATE personais SET cref = ?, especialidade = ? WHERE id = ?";

        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) return false;

            PreparedStatement stmtU = conn.prepareStatement(sqlUsuario);
            stmtU.setString(1, nome);
            stmtU.setString(2, email);
            if (senhaHash == null || senhaHash.isBlank()) {
                stmtU.setString(3, status);
                stmtU.setInt(4, usuarioId);
            } else {
                stmtU.setString(3, senhaHash);
                stmtU.setString(4, status);
                stmtU.setInt(5, usuarioId);
            }
            stmtU.executeUpdate();

            PreparedStatement stmtP = conn.prepareStatement(sqlPersonal);
            stmtP.setString(1, cref == null || cref.isBlank() ? null : cref);
            stmtP.setString(2, especialidade == null || especialidade.isBlank() ? null : especialidade);
            stmtP.setInt(3, personalId);
            stmtP.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao atualizar personal: " + e.getMessage());
            return false;
        }
    }

    public boolean deletarPersonal(int usuarioId) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) return false;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao deletar personal: " + e.getMessage());
            return false;
        }
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

    public boolean deletarAviso(int id) {
        String sql = "DELETE FROM avisos WHERE id = ?";
        try {
            Connection conn = Conexao.getConexao();
            if (conn == null) return false;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao deletar aviso: " + e.getMessage());
            return false;
        }
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
