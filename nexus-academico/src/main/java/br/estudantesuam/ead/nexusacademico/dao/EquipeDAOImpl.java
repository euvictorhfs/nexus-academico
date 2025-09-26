package br.estudantesuam.ead.nexusacademico.dao;

import br.estudantesuam.ead.nexusacademico.model.Equipe;
import br.estudantesuam.ead.nexusacademico.model.Usuario;
import br.estudantesuam.ead.nexusacademico.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * EquipeDAOImpl: Implementa a interface EquipeDAO e gerencia a persistência
 * da entidade Equipe no banco SQLite.
 */
public class EquipeDAOImpl implements EquipeDAO {

    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    @Override
    public void salvar(Equipe equipe) throws Exception {
        String sqlEquipe = "INSERT INTO Equipe (nome, descricao) VALUES (?, ?)";
        String sqlMembros = "INSERT INTO Equipe_Membros (equipe_id, usuario_cpf) VALUES (?, ?)";

        try (Connection conn = Conexao.obterConexao()) {
            conn.setAutoCommit(false); // Início da transação

            // 1. Salva a Equipe e Obtém o ID
            try (PreparedStatement psEquipe = conn.prepareStatement(sqlEquipe, Statement.RETURN_GENERATED_KEYS)) {
                psEquipe.setString(1, equipe.getNome());
                psEquipe.setString(2, equipe.getDescricao());
                psEquipe.executeUpdate();

                try (ResultSet rs = psEquipe.getGeneratedKeys()) {
                    if (rs.next()) {
                        equipe.setId(rs.getInt(1));
                    }
                }
            }

            // 2. Insere os Membros
            if (equipe.getMembros() != null && !equipe.getMembros().isEmpty()) {
                try (PreparedStatement psMembros = conn.prepareStatement(sqlMembros)) {
                    for (Usuario membro : equipe.getMembros()) {
                        psMembros.setInt(1, equipe.getId());
                        psMembros.setString(2, membro.getCpf());
                        psMembros.addBatch();
                    }
                    psMembros.executeBatch();
                }
            }

            conn.commit(); // Fim da transação
        } catch (Exception e) {
            throw new Exception("Erro ao salvar equipe: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Equipe equipe) throws Exception {
        String sqlEquipe = "UPDATE Equipe SET nome = ?, descricao = ? WHERE id = ?";
        String sqlDeletarMembros = "DELETE FROM Equipe_Membros WHERE equipe_id = ?";
        String sqlInserirMembros = "INSERT INTO Equipe_Membros (equipe_id, usuario_cpf) VALUES (?, ?)";

        try (Connection conn = Conexao.obterConexao()) {
            conn.setAutoCommit(false);

            // 1. Atualiza dados principais da Equipe
            try (PreparedStatement psEquipe = conn.prepareStatement(sqlEquipe)) {
                psEquipe.setString(1, equipe.getNome());
                psEquipe.setString(2, equipe.getDescricao());
                psEquipe.setInt(3, equipe.getId());
                psEquipe.executeUpdate();
            }

            // 2. Gerencia os Membros (Deleta tudo e insere novamente)
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeletarMembros)) {
                psDelete.setInt(1, equipe.getId());
                psDelete.executeUpdate();
            }

            if (equipe.getMembros() != null && !equipe.getMembros().isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInserirMembros)) {
                    for (Usuario membro : equipe.getMembros()) {
                        psInsert.setInt(1, equipe.getId());
                        psInsert.setString(2, membro.getCpf());
                        psInsert.addBatch();
                    }
                    psInsert.executeBatch();
                }
            }

            conn.commit();
        } catch (Exception e) {
            throw new Exception("Erro ao atualizar equipe: " + e.getMessage(), e);
        }
    }

    @Override
    public void remover(Integer id) throws Exception {
        // A exclusão de Equipe_Membros deve ser gerenciada via ON DELETE CASCADE (se configurado)
        String sql = "DELETE FROM Equipe WHERE id = ?";
        
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Erro ao remover equipe: " + e.getMessage(), e);
        }
    }

    @Override
    public Equipe buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Equipe WHERE id = ?";
        
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEquipe(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Equipe> listarTodos() throws Exception {
        String sql = "SELECT * FROM Equipe";
        List<Equipe> equipes = new ArrayList<>();
        
        try (Connection conn = Conexao.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                equipes.add(mapearEquipe(rs));
            }
        }
        return equipes;
    }

    // Método auxiliar para mapear ResultSet para Equipe
    private Equipe mapearEquipe(ResultSet rs) throws Exception {
        Equipe equipe = new Equipe();
        equipe.setId(rs.getInt("id"));
        equipe.setNome(rs.getString("nome"));
        equipe.setDescricao(rs.getString("descricao"));

        // Carregar Membros (objetos Usuario)
        List<Usuario> membros = carregarMembrosDaEquipe(equipe.getId());
        equipe.setMembros(membros);
        
        return equipe;
    }

    // Método auxiliar para carregar os membros associados à equipe
    private List<Usuario> carregarMembrosDaEquipe(Integer equipeId) throws Exception {
        String sql = "SELECT usuario_cpf FROM Equipe_Membros WHERE equipe_id = ?";
        List<Usuario> membros = new ArrayList<>();

        try (Connection conn = Conexao.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cpfMembro = rs.getString("usuario_cpf");
                    // Garantimos que a chamada ao DAO de usuário retorna o objeto Usuario
                    Usuario membro = usuarioDAO.buscarPorCpf(cpfMembro); 
                    if (membro != null) {
                        membros.add(membro);
                    }
                }
            }
        }
        return membros;
    }
}