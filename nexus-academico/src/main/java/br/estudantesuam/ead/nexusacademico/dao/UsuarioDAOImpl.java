package br.estudantesuam.ead.nexusacademico.dao;

import br.estudantesuam.ead.nexusacademico.model.Usuario;
import br.estudantesuam.ead.nexusacademico.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do UsuarioDAO usando SQLite.
 * CPF é chave primária (TEXT).
 */
public class UsuarioDAOImpl implements UsuarioDAO {

	/**
	 * Mapeia um ResultSet para um objeto Usuario
	 */
	private Usuario mapear(ResultSet rs) throws SQLException {
	    Usuario u = new Usuario();
	    u.setCpf(rs.getString("cpf"));
	    u.setNome(rs.getString("nome"));
	    u.setEmail(rs.getString("email"));
	    u.setCargo(rs.getString("cargo"));
	    u.setLogin(rs.getString("login"));
	    u.setSenha(rs.getString("senha")); 
	    u.setPerfil(rs.getString("perfil")); 
	    u.setGithubUrl(rs.getString("githubUrl"));
	    u.setTelefoneWhatsapp(rs.getString("telefoneWhatsapp"));
	    u.setCurso(rs.getString("curso"));
	    return u;
	}

	@Override
    public Usuario salvarUsuario(Usuario usuario) throws Exception {
        if (usuario == null) throw new IllegalArgumentException("Usuário nulo");
        String sql = "INSERT INTO Usuario (cpf, nome, email, cargo, login, senha, perfil, githubUrl, telefoneWhatsapp, curso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conexao = Conexao.obterConexao();
            PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, usuario.getCpf());
            ps.setString(2, usuario.getNome());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getCargo());
            ps.setString(5, usuario.getLogin());
            ps.setString(6, usuario.getSenha()); // Senha
            ps.setString(7, usuario.getPerfil()); 
            ps.setString(8, usuario.getGithubUrl());
            ps.setString(9, usuario.getTelefoneWhatsapp());
            ps.setString(10, usuario.getCurso());
            ps.executeUpdate();
        }
        return usuario;
    }

    @Override
    public Usuario buscarPorCpf(String cpf) throws Exception {
        String sql = "SELECT * FROM Usuario WHERE cpf = ?";
        try (Connection conexao = Conexao.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorLogin(String login) throws Exception {
        String sql = "SELECT * FROM Usuario WHERE login = ?";
        try (Connection conexao = Conexao.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";
        try (Connection conexao = Conexao.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public boolean atualizarUsuario(Usuario usuario) throws Exception {
        // CORREÇÃO: Usar 'senha' em vez de 'senha_hash' no UPDATE
        String sql = "UPDATE Usuario SET nome=?, email=?, cargo=?, login=?, senha=?, perfil=?, githubUrl=?, telefoneWhatsapp=?, curso=? WHERE cpf=?";
        try (Connection conexao = Conexao.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getCargo());
            ps.setString(4, usuario.getLogin());
            ps.setString(5, usuario.getSenha());
            ps.setString(6, usuario.getPerfil()); 
            ps.setString(7, usuario.getGithubUrl());
            ps.setString(8, usuario.getTelefoneWhatsapp());
            ps.setString(9, usuario.getCurso());
            ps.setString(10, usuario.getCpf()); // WHERE
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluirPorCpf(String cpf) throws Exception {
        String sql = "DELETE FROM Usuario WHERE cpf = ?";
        try (Connection conexao = Conexao.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, cpf);
            return ps.executeUpdate() > 0;
        }
    }
}