package dao;

import conexao.Conexao;
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
