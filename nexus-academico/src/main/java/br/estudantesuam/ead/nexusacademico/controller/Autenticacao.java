package br.estudantesuam.ead.nexusacademico.controller;

import br.estudantesuam.ead.nexusacademico.dao.UsuarioDAOImpl;
import br.estudantesuam.ead.nexusacademico.model.Usuario;

import java.util.Optional;

/**
 * Controller de Autenticação
 * Responsável pela lógica de login e validação de credenciais.
 */
public class Autenticacao {
    /** Instância do DAO para acesso aos dados de usuário (Princípio da Dependência). */
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl(); 

    /**
     * Tenta autenticar um usuário no sistema.
     * * @param login O login fornecido pelo usuário.
     * @param senha A senha em texto plano fornecida.
     * @return Um {@code Optional<Usuario>} contendo o objeto Usuario se a autenticação
     * for bem-sucedida (login e senha corretos), ou um Optional vazio caso contrário.
     */
    public Optional<Usuario> autenticar(String login, String senha) {
        // Validação de entrada: Evita NullPointerException e consultas desnecessárias.
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorLogin(login);

            // Verifica se o usuário foi encontrado e se a senha corresponde.
            if (usuario != null && senha.equals(usuario.getSenha())) {
                return Optional.of(usuario);
            }
        } catch (Exception e) {
            // Loga o erro em caso de falha de comunicação com o banco de dados, etc.
            System.err.println("Erro na camada de autenticação ao buscar usuário: " + e.getMessage());
            // Retorna vazio para garantir que o erro não autentique o usuário.
        }

        return Optional.empty();
    }
}