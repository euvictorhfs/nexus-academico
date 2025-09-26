package br.estudantesuam.ead.nexusacademico.dao;

import br.estudantesuam.ead.nexusacademico.model.Equipe;

import java.util.List;

/**
 * EquipeDAO: Interface DAO (Data Access Object) para a entidade Equipe.
 * Define o contrato de persistência (CRUD).
 */
public interface EquipeDAO {
    void salvar(Equipe equipe) throws Exception;
    void atualizar(Equipe equipe) throws Exception;
    void remover(Integer id) throws Exception;
    Equipe buscarPorId(Integer id) throws Exception;
    List<Equipe> listarTodos() throws Exception;
}