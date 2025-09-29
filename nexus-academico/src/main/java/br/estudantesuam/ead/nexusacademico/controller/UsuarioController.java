package br.estudantesuam.ead.nexusacademico.controller;

import br.estudantesuam.ead.nexusacademico.dao.UsuarioDAO;
import br.estudantesuam.ead.nexusacademico.dao.UsuarioDAOImpl;
import br.estudantesuam.ead.nexusacademico.model.Usuario;
import br.estudantesuam.ead.nexusacademico.util.ValidadorUsuario;

import java.util.List;

/**
 * Controlador para a entidade Usuario.
 * Responsável por gerenciar as operações de negócio (CRUD) e validações.
 */
public class UsuarioController {
    private final UsuarioDAO usuarioDAO;
    private static final String CPF_ADMIN = "00000000191"; 

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    private void validarCamposObrigatorios(Usuario usuario, boolean isCadastro) {
        if (!ValidadorUsuario.obrigatorio(usuario.getNome())) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (!ValidadorUsuario.obrigatorio(usuario.getEmail()) || !ValidadorUsuario.validarEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail é obrigatório e deve ser válido.");
        }
        if (!ValidadorUsuario.obrigatorio(usuario.getLogin())) {
            throw new IllegalArgumentException("Login é obrigatório.");
        }
        if (isCadastro) {
            if (!ValidadorUsuario.obrigatorio(usuario.getSenha())) {
                throw new IllegalArgumentException("Senha é obrigatória no cadastro.");
            }
        }
        
        // Ignora a validação de formato de CPF para o usuário admin inicial
        if (!usuario.getCpf().equals(CPF_ADMIN)) { 
            if (!ValidadorUsuario.obrigatorio(usuario.getCpf()) || !ValidadorUsuario.validarCpf(usuario.getCpf())) {
                throw new IllegalArgumentException("CPF é obrigatório e deve ser válido.");
            }
        } else if (!ValidadorUsuario.obrigatorio(usuario.getCpf())) {
             throw new IllegalArgumentException("CPF é obrigatório.");
        }
        
        String telefone = usuario.getTelefoneWhatsapp();
        if (ValidadorUsuario.obrigatorio(telefone) && !ValidadorUsuario.validarTelefone(telefone)) {
            throw new IllegalArgumentException("Número de WhatsApp inválido. Use o formato: DD + Número (somente dígitos).");
        }
    }

    /**
     * Salva um novo usuário.
     * @param usuario o objeto usuário a ser salvo
     * @throws IllegalArgumentException se os dados forem inválidos
     * @throws Exception se ocorrer um erro na persistência
     */
    public void salvarUsuario(Usuario usuario) throws Exception {
        validarCamposObrigatorios(usuario, true);
        
        if (usuarioDAO.buscarPorCpf(usuario.getCpf()) != null) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }
        if (usuarioDAO.buscarPorLogin(usuario.getLogin()) != null) {
            throw new IllegalArgumentException("Login já está em uso. Escolha outro.");
        }
        
        usuarioDAO.salvarUsuario(usuario);
    }

    /**
     * Atualiza um usuário existente.
     * @param usuario o objeto usuário a ser atualizado
     * @throws IllegalArgumentException se os dados forem inválidos
     * @throws Exception se ocorrer um erro na persistência
     */
    public void atualizarUsuario(Usuario usuario) throws Exception {
        validarCamposObrigatorios(usuario, false);
        
        Usuario usuarioPorLogin = usuarioDAO.buscarPorLogin(usuario.getLogin());
        if (usuarioPorLogin != null && !usuarioPorLogin.getCpf().equals(usuario.getCpf())) {
            throw new IllegalArgumentException("Login já está em uso por outro usuário.");
        }

        usuarioDAO.atualizarUsuario(usuario);
    }
    
    /**
     * Remove um usuário pelo CPF.
     * @param cpf o CPF do usuário a ser removido
     * @throws Exception se ocorrer um erro na persistência
     */
    public void removerUsuario(String cpf) throws Exception {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do usuário não pode ser nulo para remoção.");
        }
        if (!usuarioDAO.excluirPorCpf(cpf)) {
             throw new Exception("Não foi possível excluir o usuário (CPF não encontrado ou erro de persistência).");
        }
    }

    /**
     * Lista todos os usuários.
     * @return uma lista de todos os usuários
     * @throws Exception se ocorrer um erro na persistência
     */
    public List<Usuario> listarTodos() throws Exception {
        return usuarioDAO.listarTodos();
    }
}