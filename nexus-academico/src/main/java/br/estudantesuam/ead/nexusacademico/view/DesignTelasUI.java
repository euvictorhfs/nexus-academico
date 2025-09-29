package br.estudantesuam.ead.nexusacademico.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * DesignTelasUI: utilitário para criar layouts padrão de telas (formulários, cabeçalho, botões).
 * Reutilizável em todas as views do sistema.
 */
public class DesignTelasUI {
    
    // Cor primária do sistema
    public static final Color COR_PRIMARIA = new Color(0, 102, 90); 

    /**
     * Cria o cabeçalho padrão com botão de voltar.
     * @param titulo O título do painel/tela.
     * @param acaoVoltar A ação a ser executada ao clicar em Voltar (geralmente dispose()).
     * @return JPanel configurado.
     */
    public static JPanel criarCabecalho(String titulo, Runnable acaoVoltar) {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(COR_PRIMARIA);
        cabecalho.setPreferredSize(new Dimension(0, 70));
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        cabecalho.add(lblTitulo, BorderLayout.CENTER);
        
        JButton btnVoltar = new JButton("←");
        btnVoltar.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnVoltar.setBackground(COR_PRIMARIA);
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> acaoVoltar.run());

        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelVoltar.setBackground(COR_PRIMARIA);
        painelVoltar.add(btnVoltar);
        
        cabecalho.add(painelVoltar, BorderLayout.WEST);
        
        return cabecalho;
    }

    /**
     * Cria o painel de formulário central com labels e campos em GridBagLayout.
     * @param labels Array de Strings para os rótulos.
     * @param campos Array de JComponent (JTextField, JComboBox, etc.) para os campos.
     * @param botoes Array de JButtons a serem adicionados após os campos.
     * @return JPanel centralizado com o formulário.
     */
    public static JPanel criarFormulario(String[] labels, JComponent[] campos, JButton[] botoes) {
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        
        // Adiciona Labels e Campos
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
            painelCentral.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 1;
            campos[i].setFont(new Font("SansSerif", Font.PLAIN, 20));
            campos[i].setPreferredSize(new Dimension(600, 40));
            
            // Adiciona JScrollPane para JTextArea (descrição)
            if (campos[i] instanceof JScrollPane) {
                 ((JScrollPane) campos[i]).getViewport().getView().setFont(new Font("SansSerif", Font.PLAIN, 20));
                 ((JScrollPane) campos[i]).setPreferredSize(new Dimension(600, 100)); // Altura maior para JTextArea
                 painelCentral.add(campos[i], gbc);
            } else {
                 painelCentral.add(campos[i], gbc);
            }
        }
        
        // Painel para os botões (posicionado abaixo dos campos)
        if (botoes != null && botoes.length > 0) {
            // CORREÇÃO: Passa os botões originais para o método que já aplica a configuração
            JPanel painelBotoes = criarPainelBotoes(botoes); 
            
            gbc.gridx = 0;
            gbc.gridy = labels.length;
            gbc.gridwidth = 2; // Ocupa as duas colunas
            gbc.fill = GridBagConstraints.NONE; // Garante que o painel de botões não estique horizontalmente
            gbc.anchor = GridBagConstraints.CENTER; // Centraliza o painel de botões
            gbc.insets = new Insets(30, 16, 16, 16);
            painelCentral.add(painelBotoes, gbc);
        }

        return painelCentral;
    }

    /**
     * Cria um painel contendo os botões com formatação e tamanho consistente.
     * @param botoes Array de JButtons a serem incluídos.
     * @return JPanel contendo os botões.
     */
    private static JPanel criarPainelBotoes(JButton[] botoes) {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        painelBotoes.setOpaque(false);
        for (JButton btn : botoes) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 22));
            // CORREÇÃO: Aumenta a largura preferencial para acomodar "Cadastrar Usuário"
            btn.setPreferredSize(new Dimension(250, 50)); 
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Verifica se as configurações primárias/secundárias foram aplicadas (as cores)
            if (btn.getBackground().equals(COR_PRIMARIA)) {
                configurarBotaoPrimario(btn);
            } else {
                // Caso contrário, aplica a configuração secundária por default ou por cor
                configurarBotaoSecundario(btn); 
            }
            painelBotoes.add(btn);
        }
        return painelBotoes;
    }

    /**
     * Configura a aparência de um botão primário (ex: Salvar/Cadastrar).
     * @param botao O JButton a ser configurado.
     */
    public static void configurarBotaoPrimario(JButton botao) {
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(Color.WHITE);
        // Não definimos o tamanho aqui, pois o criarPainelBotoes já o faz.
    }
    
    /**
     * Configura a aparência de um botão secundário (ex: Cancelar).
     * @param botao O JButton a ser configurado.
     */
    public static void configurarBotaoSecundario(JButton botao) {
        botao.setBackground(new Color(220, 220, 220)); 
        botao.setForeground(Color.BLACK);
        // Não definimos o tamanho aqui, pois o criarPainelBotoes já o faz.
    }
    
    /**
     * Configura a aparência visual de uma JTable (cabeçalho, linhas e fontes).
     * @param tabela A JTable a ser configurada.
     */
    public static void configurarTabela(JTable tabela) {
        // Fonte e altura da linha
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tabela.setRowHeight(30);
        tabela.setSelectionBackground(new Color(173, 216, 230)); 
        tabela.setSelectionForeground(Color.BLACK);
        
        // Configuração do Cabeçalho
        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setBackground(COR_PRIMARIA);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false); 

        // Centraliza o texto do cabeçalho
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        // Centraliza o conteúdo das células
        DefaultTableCellRenderer centroRenderer = new DefaultTableCellRenderer();
        centroRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Aplica o renderer de centralização a todas as colunas
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centroRenderer);
        }
    }
}