package br.estudantesuam.ead.nexusacademico.dao;

import br.estudantesuam.ead.nexusacademico.model.Usuario;

import java.util.List;

/**
 * DAO para Usuario. Operações base CRUD usando CPF como identificador.
 */
public interface UsuarioDAO {
    Usuario salvarUsuario(Usuario usuario) throws Exception;
    Usuario buscarPorCpf(String cpf) throws Exception;
    Usuario buscarPorLogin(String login) throws Exception;
    List<Usuario> listarTodos() throws Exception;
    boolean atualizarUsuario(Usuario usuario) throws Exception;
    boolean excluirPorCpf(String cpf) throws Exception;
}