package br.estudantesuam.ead.nexusacademico.controller;

import br.estudantesuam.ead.nexusacademico.dao.ProjetoDAOImpl;
import br.estudantesuam.ead.nexusacademico.model.Projeto;
import br.estudantesuam.ead.nexusacademico.util.ValidadorProjeto;

import java.util.List;

/**
 * Controller para Projeto: Contém a lógica de negócio e validações,
 * orquestrando a comunicação entre a View e o DAO.
 */
public class ProjetoController {
    private final ProjetoDAOImpl projetoDAO = new ProjetoDAOImpl();

    /**
     * Salva um novo projeto após validações.
     * @param projeto Objeto Projeto a ser salvo.
     * @throws Exception se a validação falhar ou ocorrer erro no DAO.
     */
    public void salvarProjeto(Projeto projeto) throws Exception {
        ValidadorProjeto.validar(projeto);
        projetoDAO.salvar(projeto);
    }

    /**
     * Atualiza um projeto existente após validações.
     * @param projeto Objeto Projeto a ser atualizado.
     * @throws Exception se a validação falhar ou ocorrer erro no DAO.
     */
    public void atualizarProjeto(Projeto projeto) throws Exception {
        ValidadorProjeto.validar(projeto);
        projetoDAO.atualizar(projeto);
    }

    /**
     * Lista todos os projetos.
     * @return Lista de todos os projetos.
     * @throws Exception se ocorrer erro no DAO.
     */
    public List<Projeto> listarTodos() throws Exception {
        return projetoDAO.listarTodos();
    }
    
    /**
     * Remove um projeto pelo ID.
     * @param id o ID do projeto a ser removido.
     * @throws Exception se ocorrer erro no DAO.
     */
    public void removerProjeto(Integer id) throws Exception {
        projetoDAO.remover(id);
    }
}