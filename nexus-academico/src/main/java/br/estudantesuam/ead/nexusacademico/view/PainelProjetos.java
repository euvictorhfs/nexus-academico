package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.controller.ProjetoController;
import br.estudantesuam.ead.nexusacademico.model.Projeto;
import br.estudantesuam.ead.nexusacademico.view.componentes.BotaoEditor;
import br.estudantesuam.ead.nexusacademico.view.componentes.BotaoRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Painel para a aba de Gestão de Projetos (CRUD).
 */
public class PainelProjetos extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ProjetoController projetoController = new ProjetoController();
    private JTable tabelaProjetos;
    private DefaultTableModel modeloTabela;
    private final TelaPrincipal telaPai;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public PainelProjetos(TelaPrincipal telaPai) {
        this.telaPai = telaPai;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Gestão de Projetos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        JButton btnAdicionar = new JButton("+ Adicionar Novo Projeto");
        DesignTelasUI.configurarBotaoPrimario(btnAdicionar); 
        btnAdicionar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAdicionar.addActionListener(e -> new TelaCadastroProjeto(telaPai).setVisible(true));

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);
        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(btnAdicionar, BorderLayout.EAST);
        
        add(painelTopo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Status", "Início", "Término Previsto", "Gerente", "Ações"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                // Coluna "Ações" é o índice 6
                return column == 6; 
            }
        };

        tabelaProjetos = new JTable(modeloTabela);
        DesignTelasUI.configurarTabela(tabelaProjetos);

        int colunaAcoes = 6;
        
        // Configuração do Renderizador e Editor de Botões
        tabelaProjetos.getColumn("Ações").setCellRenderer(new BotaoRenderer());
        tabelaProjetos.getColumn("Ações").setCellEditor(new BotaoEditor<Projeto>(this::editarProjeto, this::excluirProjeto));
        tabelaProjetos.setRowHeight(36);
        
        // PADRONIZAÇÃO DO LAYOUT DE BOTÕES: Fixa a largura em 180px
        tabelaProjetos.getColumnModel().getColumn(colunaAcoes).setMinWidth(180);
        tabelaProjetos.getColumnModel().getColumn(colunaAcoes).setMaxWidth(180);
        tabelaProjetos.getColumnModel().getColumn(colunaAcoes).setPreferredWidth(180); 
        
        // Ajusta a largura da coluna ID
        tabelaProjetos.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        carregarProjetos();
    }

    public void carregarProjetos() {
        modeloTabela.setRowCount(0);
        try {
            List<Projeto> projetos = projetoController.listarTodos();
            for (Projeto p : projetos) {
                modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getStatus(),
                    p.getDataInicio() != null ? dateFormat.format(p.getDataInicio()) : "",
                    p.getDataTerminoPrevista() != null ? dateFormat.format(p.getDataTerminoPrevista()) : "",
                    p.getGerente() != null ? p.getGerente().getNome() : "N/A", 
                    p // Passa o objeto Projeto para a célula de Ações
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar projetos: " + e.getMessage(), "Erro de DAO", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProjeto(Projeto p) {
        new TelaCadastroProjeto(telaPai, p).setVisible(true);
    }

    private void excluirProjeto(Projeto p) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir o projeto '" + p.getNome() + "'?", 
            "Confirmação de Exclusão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                projetoController.removerProjeto(p.getId());
                JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso.");
                carregarProjetos();
                telaPai.carregarDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao tentar excluir: " + ex.getMessage(), "Erro de Exclusão", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}