package br.estudantesuam.ead.nexusacademico.controller;

import br.estudantesuam.ead.nexusacademico.dao.EquipeDAOImpl;
import br.estudantesuam.ead.nexusacademico.model.Equipe;
import br.estudantesuam.ead.nexusacademico.util.ValidadorEquipe;

import java.util.List;

/**
 * Controller para Equipe: Contém a lógica de negócio e validações,
 * orquestrando a comunicação entre a View e o DAO.
 */
public class EquipeController {
    private final EquipeDAOImpl equipeDAO = new EquipeDAOImpl();

    public void salvarEquipe(Equipe equipe) throws Exception {
        ValidadorEquipe.validar(equipe);
        equipeDAO.salvar(equipe);
    }
    
    public void atualizarEquipe(Equipe equipe) throws Exception {
        ValidadorEquipe.validar(equipe);
        equipeDAO.atualizar(equipe);
    }
    
    /**
     * Remove uma equipe pelo ID, delegando ao DAO.
     * @param id O ID da equipe a ser removida.
     * @throws Exception se ocorrer erro no DAO.
     */
    public void removerEquipe(Integer id) throws Exception {
        equipeDAO.remover(id);
    }
    
    /**
     * Lista todas as equipes.
     * @return Lista de todas as equipes.
     * @throws Exception se ocorrer erro no DAO.
     */
    public List<Equipe> listarTodos() throws Exception {
        return equipeDAO.listarTodos();
    }
}