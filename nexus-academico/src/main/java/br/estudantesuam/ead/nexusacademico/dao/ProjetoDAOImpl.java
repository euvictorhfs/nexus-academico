package br.estudantesuam.ead.nexusacademico.dao;

import br.estudantesuam.ead.nexusacademico.model.Projeto;
import br.estudantesuam.ead.nexusacademico.model.Usuario;
import br.estudantesuam.ead.nexusacademico.model.Equipe;
import br.estudantesuam.ead.nexusacademico.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do ProjetoDAO usando JDBC e SQLite.
 * Gerencia a persistência da entidade Projeto, incluindo a referência ao Gerente e Equipes.
 */
public class ProjetoDAOImpl implements ProjetoDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final EquipeDAO equipeDAO = new EquipeDAOImpl();

    @Override
    public void salvar(Projeto projeto) throws Exception {
        String sql = "INSERT INTO projeto (nome, descricao, data_inicio, data_termino_prevista, status, gerente_cpf) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection c = Conexao.obterConexao()) {
            c.setAutoCommit(false); 
            
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, projeto.getNome());
                ps.setString(2, projeto.getDescricao());
                ps.setLong(3, projeto.getDataInicio().getTime()); 
                ps.setLong(4, projeto.getDataTerminoPrevista().getTime()); 
                ps.setString(5, projeto.getStatus());
                ps.setString(6, projeto.getGerente().getCpf());
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        projeto.setId(rs.getInt(1));
                    } else {
                        throw new SQLException("Falha ao obter ID gerado para o Projeto.");
                    }
                }
            }
            
            // Persiste as associações N:M usando a mesma conexão
            salvarEquipesDoProjeto(projeto, c); 
            c.commit(); 
            
        } catch (Exception e) {
            // Tratamento de rollback em caso de falha
            try (Connection c = Conexao.obterConexao()) {
                 c.rollback(); 
            } catch (SQLException rollbackEx) {
                System.err.println("Erro durante o rollback: " + rollbackEx.getMessage());
            }
            throw e; 
        } finally {
             // Garante que o autocommit seja restaurado
             try (Connection c = Conexao.obterConexao()) {
                 c.setAutoCommit(true); 
             } catch (SQLException finalEx) {
                 System.err.println("Erro ao resetar AutoCommit: " + finalEx.getMessage());
             }
        }
    }

    @Override
    public void atualizar(Projeto projeto) throws Exception {
        String sql = "UPDATE projeto SET nome=?, descricao=?, data_inicio=?, data_termino_prevista=?, status=?, gerente_cpf=? WHERE id=?";
        try (Connection c = Conexao.obterConexao()) {
            
            c.setAutoCommit(false); 

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, projeto.getNome());
                ps.setString(2, projeto.getDescricao());
                ps.setLong(3, projeto.getDataInicio().getTime());
                ps.setLong(4, projeto.getDataTerminoPrevista().getTime());
                ps.setString(5, projeto.getStatus());
                ps.setString(6, projeto.getGerente().getCpf());
                ps.setInt(7, projeto.getId());
                ps.executeUpdate();
            }

            removerEquipesDoProjeto(projeto.getId(), c); 
            salvarEquipesDoProjeto(projeto, c);          

            c.commit();
        } catch (Exception e) {
            try (Connection c = Conexao.obterConexao()) {
                 c.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro durante o rollback: " + rollbackEx.getMessage());
            }
            throw e; 
        } finally {
             try (Connection c = Conexao.obterConexao()) {
                 c.setAutoCommit(true); 
             } catch (SQLException finalEx) {
                 System.err.println("Erro ao resetar AutoCommit: " + finalEx.getMessage());
             }
        }
    }

    @Override
    public void remover(Integer id) throws Exception {
        String sql = "DELETE FROM projeto WHERE id=?";
        
        try (Connection c = Conexao.obterConexao()) {
            c.setAutoCommit(false); 

            removerEquipesDoProjeto(id, c); 
            
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            c.commit();
        } catch (Exception e) {
            try (Connection c = Conexao.obterConexao()) {
                 c.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro durante o rollback: " + rollbackEx.getMessage());
            }
            throw e; 
        } finally {
             try (Connection c = Conexao.obterConexao()) {
                 c.setAutoCommit(true);
             } catch (SQLException finalEx) {
                 System.err.println("Erro ao resetar AutoCommit: " + finalEx.getMessage());
             }
        }
    }

    @Override
    public Projeto buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM projeto WHERE id=?";
        try (Connection c = Conexao.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Projeto projeto = mapear(rs);
                    projeto.setEquipes(buscarEquipesDoProjeto(id));
                    return projeto;
                }
            }
        }
        return null;
    }

    @Override
    public List<Projeto> listarTodos() throws Exception {
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT * FROM projeto";
        try (Connection c = Conexao.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Projeto projeto = mapear(rs);
                // Carrega equipes
                projeto.setEquipes(buscarEquipesDoProjeto(projeto.getId())); 
                projetos.add(projeto);
            }
        }
        return projetos;
    }
    
    // Métodos para o relacionamento Many-to-Many
    
    private void salvarEquipesDoProjeto(Projeto projeto, Connection c) throws Exception {
        if (projeto.getEquipes() == null || projeto.getEquipes().isEmpty()) return;
        String sql = "INSERT INTO projeto_equipes (projeto_id, equipe_id) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Equipe e : projeto.getEquipes()) {
                ps.setInt(1, projeto.getId());
                ps.setInt(2, e.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void removerEquipesDoProjeto(Integer projetoId, Connection c) throws Exception {
        String sql = "DELETE FROM projeto_equipes WHERE projeto_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, projetoId);
            ps.executeUpdate();
        }
    }

    private List<Equipe> buscarEquipesDoProjeto(Integer projetoId) throws Exception {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT equipe_id FROM Projeto_Equipes WHERE projeto_id=?"; 
        try (Connection c = Conexao.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, projetoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipe equipe = equipeDAO.buscarPorId(rs.getInt("equipe_id")); 
                    if (equipe != null) {
                        equipes.add(equipe);
                    }
                }
            }
        }
        return equipes;
    }

    private Projeto mapear(ResultSet rs) throws SQLException {
        Projeto p = new Projeto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setDataInicio(new Date(rs.getLong("data_inicio"))); 
        p.setDataTerminoPrevista(new Date(rs.getLong("data_termino_prevista")));
        p.setStatus(rs.getString("status"));

        try {
            // Busca o Gerente pelo CPF
            Usuario gerente = usuarioDAO.buscarPorCpf(rs.getString("gerente_cpf"));
            p.setGerente(gerente);
        } catch (Exception e) {
            System.err.println("Erro ao buscar Gerente para projeto (CPF: " + rs.getString("gerente_cpf") + "): " + e.getMessage());
            p.setGerente(null); 
        }
        return p;
    }
}