package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.dao.UsuarioDAOImpl;
import br.estudantesuam.ead.nexusacademico.model.Usuario;
import br.estudantesuam.ead.nexusacademico.view.componentes.BotaoEditor;
import br.estudantesuam.ead.nexusacademico.view.componentes.BotaoRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.EventObject;
import java.util.function.BiConsumer;

/**
 * Painel para a aba de Gestão de Usuários (CRUD).
 */
public class PainelUsuarios extends JPanel {
    private static final long serialVersionUID = 1L;
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private JTable tabelaUsuarios;
    private DefaultTableModel modeloTabela;
    private final TelaPrincipal telaPai;
    private final Map<String, Boolean> cpfVisibilidade = new HashMap<>();

    public PainelUsuarios(TelaPrincipal telaPai) {
        this.telaPai = telaPai;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        carregarUsuarios();
    }
    
    private void inicializarComponentes() {
        // Título e Botão Adicionar (PADRONIZADO)
        JLabel lblTitulo = new JLabel("Gestão de Usuários");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        // Removido: lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton btnAdicionar = new JButton("+ Adicionar Novo Usuário");
        DesignTelasUI.configurarBotaoPrimario(btnAdicionar);
        btnAdicionar.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        // CORREÇÃO: Abre a TelaCadastroUsuario, não TelaCadastroProjeto
        btnAdicionar.addActionListener(e -> new TelaCadastroUsuario(telaPai).setVisible(true));
        
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);
        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(btnAdicionar, BorderLayout.EAST);
        
        add(painelTopo, BorderLayout.NORTH);
        
        // Configuração da Tabela
        String[] colunas = {"Nome", "CPF", "E-mail", "Cargo", "Login", "Perfil", "GitHub", "WhatsApp", "Ações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            // A coluna Ações e os Links devem ser editáveis para permitir o clique
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7 || column == 8;
            }
        };
        tabelaUsuarios = new JTable(modeloTabela);
        DesignTelasUI.configurarTabela(tabelaUsuarios);
        tabelaUsuarios.setRowHeight(36); // Altura da linha padronizada

        configurarRenderersEEditores();

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void configurarRenderersEEditores() {
        int colunaAcoes = 8;
        
        // Configuração de Ações
        tabelaUsuarios.getColumnModel().getColumn(colunaAcoes).setCellRenderer(new BotaoRenderer());
        tabelaUsuarios.getColumnModel().getColumn(colunaAcoes).setCellEditor(new BotaoEditor<Usuario>(this::editarUsuario, this::excluirUsuario));
        // PADRONIZAÇÃO DO LAYOUT DE BOTÕES: Fixa a largura em 180px
        tabelaUsuarios.getColumnModel().getColumn(colunaAcoes).setMinWidth(180);
        tabelaUsuarios.getColumnModel().getColumn(colunaAcoes).setMaxWidth(180);
        tabelaUsuarios.getColumnModel().getColumn(colunaAcoes).setPreferredWidth(180);
        
        // Configuração para as colunas de Link (GitHub e WhatsApp)
        int colunaGitHub = 6;
        int colunaWhatsApp = 7;
        
        // Ação para abrir o URL
        BiConsumer<String, String> acaoAbrirUrl = (url, tipo) -> {
            try {
                if (tipo.equals("WhatsApp")) {
                    // Limpa o número antes de montar a URL
                    url = "https://wa.me/" + url.replaceAll("[^0-9]", "");
                }
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException ex) {
                JOptionPane.showMessageDialog(this, "Não foi possível abrir o link. Verifique a URL.", "Erro de Navegação", JOptionPane.ERROR_MESSAGE);
            }
        };
        
        tabelaUsuarios.getColumnModel().getColumn(colunaGitHub).setCellRenderer(new HiperlinkRenderer("GitHub"));
        tabelaUsuarios.getColumnModel().getColumn(colunaGitHub).setCellEditor(new HiperlinkEditor(acaoAbrirUrl, "GitHub"));
        // Largura fixa para Ícones
        tabelaUsuarios.getColumnModel().getColumn(colunaGitHub).setMinWidth(80);
        tabelaUsuarios.getColumnModel().getColumn(colunaGitHub).setMaxWidth(80);
        tabelaUsuarios.getColumnModel().getColumn(colunaGitHub).setPreferredWidth(80);
        
        tabelaUsuarios.getColumnModel().getColumn(colunaWhatsApp).setCellRenderer(new HiperlinkRenderer("WhatsApp"));
        tabelaUsuarios.getColumnModel().getColumn(colunaWhatsApp).setCellEditor(new HiperlinkEditor(acaoAbrirUrl, "WhatsApp"));
        // Largura fixa para Ícones
        tabelaUsuarios.getColumnModel().getColumn(colunaWhatsApp).setMinWidth(80);
        tabelaUsuarios.getColumnModel().getColumn(colunaWhatsApp).setMaxWidth(80);
        tabelaUsuarios.getColumnModel().getColumn(colunaWhatsApp).setPreferredWidth(80);
        
        // Configuração da coluna CPF (Mascara/Visualização)
        int colunaCPF = 1;
        tabelaUsuarios.getColumnModel().getColumn(colunaCPF).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String cpf = (String) value;
                // Exibe o CPF oculto por padrão (***.***.***-**)
                String cpfOculto = "***********"; 
                if (cpf != null && cpf.length() == 11) {
                    cpfOculto = "***." + cpf.substring(4, 7) + "." + cpf.substring(7, 10) + "-**";
                }
                
                // Pega o CPF real da linha para mapear a visibilidade
                // É necessário checar se a linha existe no modelo (evita erro de IndexOutOfBounds)
                Usuario usuario = null;
                if (row >= 0 && row < modeloTabela.getRowCount()) {
                    usuario = (Usuario) modeloTabela.getValueAt(row, colunaAcoes);
                }
                
                String cpfCompleto = (usuario != null) ? usuario.getCpf() : cpfOculto;

                // Toggle logic (apenas visual, o editor não é ativado aqui)
                String texto = cpfVisibilidade.getOrDefault(cpfCompleto, false) ? cpfCompleto : cpfOculto;
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, texto, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });
        
        // Adiciona listener de clique para toggle da visibilidade do CPF
        tabelaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabelaUsuarios.rowAtPoint(e.getPoint());
                int col = tabelaUsuarios.columnAtPoint(e.getPoint());
                
                // Verifica se o clique foi na coluna CPF
                if (row >= 0 && col == colunaCPF) {
                    // O objeto Usuario está na coluna Ações
                    Usuario usuario = (Usuario) modeloTabela.getValueAt(row, colunaAcoes);
                    if (usuario != null && usuario.getCpf() != null) {
                        String cpf = usuario.getCpf();
                        boolean visivel = cpfVisibilidade.getOrDefault(cpf, false);
                        cpfVisibilidade.put(cpf, !visivel);
                        tabelaUsuarios.repaint(); // Redesenha a tabela para aplicar a mudança
                    }
                }
            }
        });
    }

    public void carregarUsuarios() {
        modeloTabela.setRowCount(0); // Limpa a tabela
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            for (Usuario u : usuarios) {
                // Passamos o objeto Usuario na última coluna para ser usado pelo BotaoEditor/Renderer
                modeloTabela.addRow(new Object[]{
                    u.getNome(),
                    u.getCpf(),
                    u.getEmail(),
                    u.getCargo(),
                    u.getLogin(),
                    u.getPerfil(),
                    u.getGithubUrl(),
                    u.getTelefoneWhatsapp(),
                    u // Objeto Usuario para ações (Editar/Excluir)
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + e.getMessage(), "Erro de DAO", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarUsuario(Usuario u) {
        new TelaCadastroUsuario(telaPai, u).setVisible(true);
    }

    private void excluirUsuario(Usuario u) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir o usuário " + u.getNome() + " (CPF: " + u.getCpf() + ")?", 
            "Confirmação de Exclusão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (usuarioDAO.excluirPorCpf(u.getCpf())) { 
                    JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso.");
                    carregarUsuarios(); 
                    telaPai.atualizarConteudo(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Erro: Não foi possível excluir o usuário (CPF não encontrado ou erro de BD).", "Erro de Exclusão", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao tentar excluir: " + ex.getMessage(), "Erro de Exclusão", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // --- CLASSE INTERNA: HiperlinkRenderer (Ajustado para Ícones) ---
    private class HiperlinkRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final String tipo; 
        
        public HiperlinkRenderer(String tipo) {
            this.tipo = tipo;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String url = (String) value;
            
            if (url != null && !url.trim().isEmpty()) {
                // Usando ícones unicode
                String textoBotao = tipo.equals("GitHub") ? "🔗" : "📞"; 
                
                JLabel label = new JLabel(textoBotao, SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 18));
                label.setForeground(DesignTelasUI.COR_PRIMARIA); 
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                label.setToolTipText("Abrir " + tipo);
                
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setBackground(table.getBackground());
                }
                label.setOpaque(true);
                return label;
            }
            
            return new JLabel("-", SwingConstants.CENTER);
        }
    }

    // --- CLASSE INTERNA: HiperlinkEditor (Ajustado para Ícones) ---
    private class HiperlinkEditor extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 1L;
        private String valorAtual;
        private final JButton btnClique = new JButton();
        private final String tipo;
        private final BiConsumer<String, String> acaoAbrirUrl;

        public HiperlinkEditor(BiConsumer<String, String> acaoAbrirUrl, String tipo) {
            this.acaoAbrirUrl = acaoAbrirUrl;
            this.tipo = tipo;
            btnClique.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnClique.setFocusPainted(false);
            btnClique.addActionListener(e -> {
                fireEditingStopped(); 
                if (valorAtual != null && !valorAtual.trim().isEmpty()) {
                    acaoAbrirUrl.accept(valorAtual, tipo);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            valorAtual = (String) value;
            
            if (valorAtual != null && !valorAtual.trim().isEmpty()) {
                String textoBotao = tipo.equals("GitHub") ? "🔗" : "📞"; 
                btnClique.setText(textoBotao); 
                btnClique.setFont(new Font("SansSerif", Font.BOLD, 18)); 
                btnClique.setBackground(new Color(220, 240, 240));
                btnClique.setForeground(DesignTelasUI.COR_PRIMARIA);
                btnClique.setToolTipText("Abrir " + tipo + ": " + valorAtual);
                return btnClique; 
            } else {
                JLabel lblInativo = new JLabel("-", SwingConstants.CENTER);
                lblInativo.setOpaque(true);
                // Configura a cor de fundo correta para evitar inconsistência visual
                lblInativo.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground()); 
                return lblInativo;
            }
        }

        @Override
        public Object getCellEditorValue() {
            return valorAtual;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }
    }
}