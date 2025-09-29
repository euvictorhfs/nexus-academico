package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.controller.EquipeController; 
import br.estudantesuam.ead.nexusacademico.model.Equipe;
import br.estudantesuam.ead.nexusacademico.model.Usuario;
import br.estudantesuam.ead.nexusacademico.view.componentes.BotaoEditor;
import br.estudantesuam.ead.nexusacademico.view.componentes.BotaoRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para a aba de Gestão de Equipes (CRUD).
 * Corrigido para utilizar EquipeController.
 */
public class PainelEquipes extends JPanel {
    private static final long serialVersionUID = 1L;
    private final EquipeController equipeController = new EquipeController(); 
    private JTable tabelaEquipes;
    private DefaultTableModel modeloTabela;
    private final TelaPrincipal telaPai;

    public PainelEquipes(TelaPrincipal telaPai) {
        this.telaPai = telaPai;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título e Botão Adicionar
        JLabel lblTitulo = new JLabel("Gestão de Equipes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        JButton btnAdicionar = new JButton("+ Adicionar Nova Equipe");
        DesignTelasUI.configurarBotaoPrimario(btnAdicionar); 
        btnAdicionar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAdicionar.addActionListener(e -> new TelaCadastroEquipe(telaPai).setVisible(true));

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);
        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(btnAdicionar, BorderLayout.EAST);
        
        add(painelTopo, BorderLayout.NORTH);
        
        // Tabela
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrição", "Membros", "Ações"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                // Coluna "Ações" é o índice 4
                return column == 4; 
            }
        };
        
        tabelaEquipes = new JTable(modeloTabela);
        DesignTelasUI.configurarTabela(tabelaEquipes); 

        int colunaAcoes = 4;
        
        // Configuração do Renderizador e Editor de Botões
        tabelaEquipes.getColumn("Ações").setCellRenderer(new BotaoRenderer());
        tabelaEquipes.getColumn("Ações").setCellEditor(new BotaoEditor<Equipe>(this::editarEquipe, this::excluirEquipe));
        tabelaEquipes.setRowHeight(36);
        
        // PADRONIZAÇÃO DO LAYOUT DE BOTÕES: Fixa a largura em 180px
        tabelaEquipes.getColumnModel().getColumn(colunaAcoes).setMinWidth(180);
        tabelaEquipes.getColumnModel().getColumn(colunaAcoes).setMaxWidth(180);
        tabelaEquipes.getColumnModel().getColumn(colunaAcoes).setPreferredWidth(180);
        
        // Ajusta a largura da coluna ID
        tabelaEquipes.getColumnModel().getColumn(0).setMaxWidth(40);


        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        carregarEquipes();
    }

    public void carregarEquipes() {
        modeloTabela.setRowCount(0);
        try {
            List<Equipe> equipes = equipeController.listarTodos(); 
            for (Equipe e : equipes) {
                modeloTabela.addRow(new Object[]{
                    e.getId(), 
                    e.getNome(), 
                    e.getDescricao(), 
                    formatarMembros(e.getMembros()),
                    e 
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar equipes: " + e.getMessage(), "Erro de DAO", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatarMembros(List<Usuario> membros) {
        if (membros == null || membros.isEmpty()) return "Nenhum membro";
        if (membros.size() > 3) {
            return membros.size() + " membros (incluindo " + membros.get(0).getNome() + "...)";
        }
        return membros.stream().map(Usuario::getNome).reduce((a, b) -> a + ", " + b).orElse("");
    }

    private void editarEquipe(Equipe e) {
        new TelaCadastroEquipe(telaPai, e).setVisible(true); 
    }

    private void excluirEquipe(Equipe e) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir a equipe " + e.getNome() + "?", 
            "Confirmação de Exclusão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                equipeController.removerEquipe(e.getId()); 
                JOptionPane.showMessageDialog(this, "Equipe excluída com sucesso.");
                carregarEquipes(); 
                telaPai.atualizarConteudo(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao tentar excluir: " + ex.getMessage(), "Erro de Exclusão", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}