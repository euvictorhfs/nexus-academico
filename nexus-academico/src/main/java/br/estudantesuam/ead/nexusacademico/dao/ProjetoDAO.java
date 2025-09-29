package br.estudantesuam.ead.nexusacademico.dao;

import br.estudantesuam.ead.nexusacademico.model.Projeto;

import java.util.List;

/**
 * Interface DAO para a entidade Projeto.
 * Define as operações de CRUD.
 */
public interface ProjetoDAO {
    void salvar(Projeto projeto) throws Exception;
    void atualizar(Projeto projeto) throws Exception;
    void remover(Integer id) throws Exception;
    Projeto buscarPorId(Integer id) throws Exception;
    List<Projeto> listarTodos() throws Exception;
}