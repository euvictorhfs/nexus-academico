package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.controller.EquipeController;
import br.estudantesuam.ead.nexusacademico.controller.ProjetoController;
import br.estudantesuam.ead.nexusacademico.model.Equipe;
import br.estudantesuam.ead.nexusacademico.model.Projeto;
import br.estudantesuam.ead.nexusacademico.model.Usuario;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * PainelDashboard: Visão geral com estatísticas e informações estratégicas.
 * Versão final com interatividade (clique), carga assíncrona e design otimizado.
 */
public class PainelDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    // Controllers são a camada de Abstração, intermediando a View e o DAO/Repository
    private final ProjetoController projetoController = new ProjetoController();
    private final EquipeController equipeController = new EquipeController();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private JLabel lblTotalProjetos;
    private JLabel lblProjetosAtivos;
    private JLabel lblTotalEquipes;
    
    private JPanel painelStatusProjetos;
    private JPanel painelAlertas;
    private JList<Map.Entry<Usuario, List<Projeto>>> listaResumoGerentes; 
    private JList<Equipe> listaEquipes; 

    private List<Projeto> projetosCache = new ArrayList<>();
    private List<Equipe> equipesCache = new ArrayList<>();

    private static final Color COR_PRIMARIA = new Color(0, 102, 90);

    public PainelDashboard(TelaPrincipal telaPai) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        inicializarComponentes();
        // A chamada é feita aqui para iniciar a carga ao instanciar o painel,
        // garantindo que o dashboard não carregue vazio inicialmente.
        carregarDashboard(); 
    }
    
    private void inicializarComponentes() {
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setOpaque(false);
        JLabel lblTitulo = new JLabel("Visão Geral do Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTitulo.setForeground(COR_PRIMARIA); 
        painelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(painelTitulo, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridLayout(2, 1, 0, 20));
        painelCentral.setOpaque(false);
        
        JPanel painelLinha1 = new JPanel(new GridLayout(1, 2, 20, 0));
        painelLinha1.setOpaque(false);
        
        JPanel painelTotais = new JPanel(new GridLayout(1, 3, 20, 0));
        painelTotais.setOpaque(false);

        lblTotalProjetos = new JLabel("...", SwingConstants.CENTER);
        painelTotais.add(criarPainelEstatistica("Total de Projetos", lblTotalProjetos, new Color(240, 250, 255), COR_PRIMARIA));
        
        lblProjetosAtivos = new JLabel("...", SwingConstants.CENTER);
        painelTotais.add(criarPainelEstatistica("Projetos Ativos", lblProjetosAtivos, new Color(240, 255, 240), new Color(0, 128, 0)));
        
        lblTotalEquipes = new JLabel("...", SwingConstants.CENTER);
        painelTotais.add(criarPainelEstatistica("Total de Equipes", lblTotalEquipes, new Color(255, 248, 240), new Color(255, 140, 0)));
        
        painelStatusProjetos = new JPanel(new GridLayout(4, 1, 10, 10)); 
        painelStatusProjetos.setOpaque(false);
        painelStatusProjetos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Status de Projetos", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("SansSerif", Font.BOLD, 16),
            COR_PRIMARIA
        ));
        
        for(String status : new String[]{"Em Andamento", "Planejado", "Concluído", "Cancelado"}) {
            painelStatusProjetos.add(criarBoxStatus(status, 0L, Color.LIGHT_GRAY.brighter()));
        }
        
        painelLinha1.add(painelTotais);
        painelLinha1.add(painelStatusProjetos);
        
        JPanel painelLinha2 = new JPanel(new GridLayout(1, 3, 20, 0));
        painelLinha2.setOpaque(false);
        
        // 2.1: Alertas (Prazo Expirando)
        painelAlertas = new JPanel();
        painelAlertas.setLayout(new BoxLayout(painelAlertas, BoxLayout.Y_AXIS));
        painelAlertas.setBackground(new Color(255, 245, 245)); 
        Color corAlertaBorda = new Color(220, 20, 60);
        painelAlertas.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(corAlertaBorda), 
            "Alertas de Prazo (Próximos/Atrasados)", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("SansSerif", Font.BOLD, 16),
            corAlertaBorda
        ));
        painelAlertas.add(new JLabel("Carregando alertas..."));
        JScrollPane scrollAlertas = new JScrollPane(painelAlertas);
        scrollAlertas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        painelLinha2.add(scrollAlertas);

        // 2.2: Resumo de Alocação por Gerente (JList interativa)
        DefaultListModel<Map.Entry<Usuario, List<Projeto>>> modeloListaGerentes = new DefaultListModel<>();
        Usuario placeholderGerenteCarregando = new Usuario();
        placeholderGerenteCarregando.setNome("Carregando gerentes...");
        modeloListaGerentes.addElement(Map.entry(placeholderGerenteCarregando, new ArrayList<>())); 
        
        listaResumoGerentes = new JList<>(modeloListaGerentes);
        listaResumoGerentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaResumoGerentes.setFont(new Font("SansSerif", Font.PLAIN, 15));
        listaResumoGerentes.setCellRenderer(new GerenteResumoRenderer()); 
        
        listaResumoGerentes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    Map.Entry<Usuario, List<Projeto>> item = listaResumoGerentes.getSelectedValue();
                    if (item != null && item.getKey().getCpf() != null) {
                        exibirDetalhesGerente(item.getKey(), item.getValue());
                    }
                }
            }
        });
        
        JScrollPane scrollResumo = new JScrollPane(listaResumoGerentes);
        scrollResumo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)), 
            "Alocação por Gerente (Clique Duplo)", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("SansSerif", Font.BOLD, 16)
        ));
        painelLinha2.add(scrollResumo);
        
        // 2.3: Detalhe das Equipes (JList interativa)
        DefaultListModel<Equipe> modeloListaEquipes = new DefaultListModel<>();
        
        Equipe equipePlaceholder = new Equipe();
        equipePlaceholder.setNome("Carregando equipes...");
        modeloListaEquipes.addElement(equipePlaceholder);
        
        listaEquipes = new JList<>(modeloListaEquipes);
        listaEquipes.setFont(new Font("SansSerif", Font.PLAIN, 15));
        listaEquipes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaEquipes.setCellRenderer(new EquipeResumoRenderer()); 

        listaEquipes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    Equipe equipe = listaEquipes.getSelectedValue();
                    if (equipe != null && equipe.getId() != null) { 
                        exibirDetalhesEquipe(equipe);
                    }
                }
            }
        });
        
        JScrollPane scrollEquipes = new JScrollPane(listaEquipes);
        scrollEquipes.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_PRIMARIA), 
            "Detalhe das Equipes (Clique Duplo)", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("SansSerif", Font.BOLD, 16),
            COR_PRIMARIA
        ));
        painelLinha2.add(scrollEquipes);
        
        painelCentral.add(painelLinha1);
        painelCentral.add(painelLinha2);
        
        add(painelCentral, BorderLayout.CENTER);
    }
    
    /**
     * Define o estado inicial de "Carregando" nos componentes do dashboard.
     */
    private void atualizarPlaceholdersParaCarregando() {
        lblTotalProjetos.setText("...");
        lblProjetosAtivos.setText("...");
        lblTotalEquipes.setText("...");
        
        // Alertas
        painelAlertas.removeAll();
        painelAlertas.add(Box.createVerticalStrut(5)); 
        JLabel lblCarregandoAlertas = new JLabel("Carregando alertas...");
        lblCarregandoAlertas.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblCarregandoAlertas.setForeground(Color.GRAY.darker());
        lblCarregandoAlertas.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelAlertas.add(lblCarregandoAlertas);
        
        // Listas
        DefaultListModel<Map.Entry<Usuario, List<Projeto>>> modeloGerentes = new DefaultListModel<>();
        Usuario placeholderGerente = new Usuario();
        placeholderGerente.setNome("Carregando gerentes...");
        modeloGerentes.addElement(Map.entry(placeholderGerente, new ArrayList<>()));
        listaResumoGerentes.setModel(modeloGerentes);
        
        DefaultListModel<Equipe> modeloEquipes = new DefaultListModel<>();
        Equipe equipePlaceholder = new Equipe();
        equipePlaceholder.setNome("Carregando equipes...");
        modeloEquipes.addElement(equipePlaceholder);
        listaEquipes.setModel(modeloEquipes);
        
        this.revalidate();
        this.repaint();
    }
    
    /**
     * Inicia o carregamento dos dados em background (THREAD).
     */
    public void carregarDashboard() {
        // 1. Define placeholders visíveis (LOADING state)
        atualizarPlaceholdersParaCarregando(); 

        // 2. Inicia o carregamento assíncrono na thread de background
        new Thread(() -> {
            Exception erroPersistencia = null;
            
            try {
                // Simulação de delay para visualização do loading (opcional, mas bom para debug)
                Thread.sleep(200); 
                
                // Chamadas de persistência (DAO/Controller)
                projetosCache = projetoController.listarTodos();
                equipesCache = equipeController.listarTodos();
                
                // Garante que as listas nunca sejam NULL, apenas vazias
                if (projetosCache == null) projetosCache = new ArrayList<>();
                if (equipesCache == null) equipesCache = new ArrayList<>();
                
            } catch (Exception e) {
                erroPersistencia = e;
                // Log de erro CRÍTICO para o console
                System.err.println("Erro CRÍTICO ao carregar dados do Dashboard (DAO/Conexão): " + e.getMessage());
                e.printStackTrace();
            }
            
            final Exception erroFinal = erroPersistencia;
            
            // 3. Atualiza a interface gráfica na thread de Eventos (SwingUtilities.invokeLater)
            SwingUtilities.invokeLater(() -> {
                if (erroFinal != null) {
                    // Exibe o erro de forma visual no dashboard
                    exibirErroDeCarregamento(erroFinal); 
                } else {
                    // Atualização de Estatísticas (Linha 1)
                    lblTotalProjetos.setText(String.valueOf(projetosCache.size()));
                    lblProjetosAtivos.setText(String.valueOf(projetosCache.stream()
                        .filter(p -> "Em Andamento".equalsIgnoreCase(p.getStatus()))
                        .count()));
                    lblTotalEquipes.setText(String.valueOf(equipesCache.size()));

                    // Gera o resumo interativo e alertas (Linha 2)
                    gerarResumoEstrategico();
                }
            });
        }).start(); // Inicia a thread
    }

    // --- MÉTODOS DE RENDERIZAÇÃO E AUXILIARES ---

    private void exibirErroDeCarregamento(Exception e) {
        lblTotalProjetos.setText("FALHA");
        lblProjetosAtivos.setText("FALHA");
        lblTotalEquipes.setText("FALHA");
        
        // Remove todos os componentes e adiciona a mensagem de erro
        painelStatusProjetos.removeAll();
        painelStatusProjetos.setLayout(new FlowLayout(FlowLayout.CENTER));
        painelStatusProjetos.add(new JLabel("<html><font color='red'>Erro de conexão: " + e.getMessage() + "</font></html>"));
        
        painelAlertas.removeAll();
        painelAlertas.setLayout(new FlowLayout(FlowLayout.CENTER));
        painelAlertas.add(new JLabel("<html><font color='red'>Falha ao carregar alertas.</font></html>"));
        
        // Atualiza as JLists com erro
        DefaultListModel<Map.Entry<Usuario, List<Projeto>>> modeloGerentes = new DefaultListModel<>();
        Usuario placeholderGerente = new Usuario();
        placeholderGerente.setNome("ERRO DE CARGA");
        modeloGerentes.addElement(Map.entry(placeholderGerente, new ArrayList<>()));
        listaResumoGerentes.setModel(modeloGerentes);
        
        DefaultListModel<Equipe> modeloEquipes = new DefaultListModel<>();
        Equipe equipeErro = new Equipe();
        equipeErro.setNome("ERRO DE CARGA");
        modeloEquipes.addElement(equipeErro);
        listaEquipes.setModel(modeloEquipes);

        this.revalidate();
        this.repaint();
    }
    
    // Método auxiliar para exibir detalhes do gerente
    private void exibirDetalhesGerente(Usuario gerente, List<Projeto> projetos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<h2>Projetos de ").append(gerente.getNome()).append("</h2>");
        sb.append("<hr style='border: 1px solid ").append(toHtmlColor(COR_PRIMARIA)).append(";'>");
        
        if (projetos.isEmpty()) {
            sb.append("<p>Nenhum projeto alocado.</p>");
        } else {
            sb.append("<ul style='list-style-type: none; padding: 0;'>");
            for (Projeto p : projetos) {
                String statusColor = "black";
                if ("Concluído".equalsIgnoreCase(p.getStatus())) statusColor = "green";
                else if ("Em Andamento".equalsIgnoreCase(p.getStatus())) statusColor = "darkorange";
                else if ("Cancelado".equalsIgnoreCase(p.getStatus())) statusColor = "red";
                
                String dataFim = p.getDataTerminoPrevista() != null ? dateFormat.format(p.getDataTerminoPrevista()) : "N/A";
                
                sb.append("<li>")
                  .append("<b>").append(p.getNome()).append("</b>")
                  .append(" &bull; Status: <font color='").append(statusColor).append("'>").append(p.getStatus()).append("</font>")
                  .append(" &bull; Término: ").append(dataFim)
                  .append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append("</html>");
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Detalhes de Alocação: " + gerente.getNome(), JOptionPane.PLAIN_MESSAGE);
    }
    
    // Método auxiliar para exibir detalhes da equipe
    private void exibirDetalhesEquipe(Equipe equipe) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<h2>Membros da Equipe: ").append(equipe.getNome()).append("</h2>");
        sb.append("<p><b>Descrição:</b> ").append(equipe.getDescricao()).append("</p>");
        sb.append("<hr style='border: 1px solid ").append(toHtmlColor(COR_PRIMARIA)).append(";'>");
        
        List<Usuario> membros = equipe.getMembros();
        if (membros == null || membros.isEmpty()) {
            sb.append("<p>Nenhum membro alocado a esta equipe.</p>");
        } else {
            sb.append("<ul style='column-count: 2; list-style-type: none; padding: 0;'>"); 
            for (Usuario u : membros) {
                sb.append("<li>")
                  .append("• ").append(u.getNome())
                  .append(" (").append(u.getCargo()).append(")")
                  .append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append("</html>");
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Detalhes da Equipe: " + equipe.getNome(), JOptionPane.PLAIN_MESSAGE);
    }
    
    private String toHtmlColor(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
    
    // Método para criar os painéis de estatísticas
    private JPanel criarPainelEstatistica(String titulo, JLabel labelValor, Color corFundo, Color corTexto) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(corFundo);
        painel.setPreferredSize(new Dimension(200, 120));
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(corTexto.darker()); 
        
        labelValor.setFont(new Font("SansSerif", Font.BOLD, 48));
        labelValor.setForeground(corTexto);
        labelValor.setHorizontalAlignment(SwingConstants.CENTER);
        
        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(labelValor, BorderLayout.CENTER);
        return painel;
    }
    
    // Método para criar as caixas de status
    private JPanel criarBoxStatus(String status, long count, Color corFundo) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel lblStatus = new JLabel(status, SwingConstants.LEFT);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblStatus.setForeground(Color.DARK_GRAY);
        
        JLabel lblCount = new JLabel(String.valueOf(count), SwingConstants.RIGHT);
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblCount.setForeground(COR_PRIMARIA.darker());
        
        painel.add(lblStatus, BorderLayout.WEST);
        painel.add(lblCount, BorderLayout.EAST);
        return painel;
    }

    /**
     * Gera e exibe o resumo estratégico (Linha 2).
     */
    private void gerarResumoEstrategico() {
        
        // --- 1. Status dos Projetos ---
        Map<String, Long> projetosPorStatus = projetosCache.stream()
            .collect(Collectors.groupingBy(Projeto::getStatus, Collectors.counting()));
            
        painelStatusProjetos.removeAll();
        // A ordem aqui é importante para o layout
        painelStatusProjetos.add(criarBoxStatus("Em Andamento", projetosPorStatus.getOrDefault("Em Andamento", 0L), new Color(240, 255, 240)));
        painelStatusProjetos.add(criarBoxStatus("Planejado", projetosPorStatus.getOrDefault("Planejado", 0L), new Color(240, 248, 255)));
        painelStatusProjetos.add(criarBoxStatus("Concluído", projetosPorStatus.getOrDefault("Concluído", 0L), new Color(245, 245, 220)));
        painelStatusProjetos.add(criarBoxStatus("Cancelado", projetosPorStatus.getOrDefault("Cancelado", 0L), new Color(255, 240, 245)));
        painelStatusProjetos.revalidate();
        painelStatusProjetos.repaint();

        // --- 2. Alertas de Prazo ---
        painelAlertas.removeAll();
        painelAlertas.add(Box.createVerticalStrut(5)); 
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date hojeSemTempo = cal.getTime();
        
        // Filtra e ordena projetos ativos com data de término prevista
        List<Projeto> alertasProjetos = projetosCache.stream()
            .filter(p -> "Em Andamento".equalsIgnoreCase(p.getStatus()) && p.getDataTerminoPrevista() != null)
            .sorted(Comparator.comparing(Projeto::getDataTerminoPrevista))
            .collect(Collectors.toList());
        
        int alertasCont = 0;
        
        if (alertasProjetos.isEmpty()) {
            JLabel lblNenhumAlerta = new JLabel("Nenhum projeto requer atenção imediata.");
            lblNenhumAlerta.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblNenhumAlerta.setForeground(Color.GRAY.darker());
            lblNenhumAlerta.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelAlertas.add(lblNenhumAlerta);
        } else {
            for (Projeto p : alertasProjetos) {
                long diff = p.getDataTerminoPrevista().getTime() - hojeSemTempo.getTime();
                long diasRestantes = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                
                String tempoRestante;
                Color corAlerta;
                
                if (diasRestantes < 0) {
                    tempoRestante = (diasRestantes * -1) + " dias de ATRASO"; 
                    corAlerta = new Color(220, 20, 60); // Vermelho Crítico
                    alertasCont++;
                } else if (diasRestantes == 0) {
                    tempoRestante = "ENTREGA HOJE!"; 
                    corAlerta = new Color(255, 140, 0); // Laranja Forte
                    alertasCont++;
                } else if (diasRestantes <= 30) {
                    tempoRestante = diasRestantes + " dias restantes";
                    corAlerta = new Color(255, 140, 0); // Laranja
                    alertasCont++;
                } else {
                    continue; // Ignora projetos com mais de 30 dias
                }
                
                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
                wrapper.setOpaque(false);
                JLabel lblAlerta = new JLabel(String.format("• %s: %s", p.getNome(), tempoRestante));
                lblAlerta.setFont(new Font("SansSerif", Font.BOLD, 13));
                lblAlerta.setForeground(corAlerta);
                wrapper.add(lblAlerta);
                wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
                painelAlertas.add(wrapper);
            }
        }
        
        Color corAlertaBorda = new Color(220, 20, 60);
        painelAlertas.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(corAlertaBorda), 
            "Alertas de Prazo (" + alertasCont + " críticos)", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("SansSerif", Font.BOLD, 16), 
            corAlertaBorda
        ));
        painelAlertas.revalidate();
        painelAlertas.repaint();

        // --- 3. Resumo de Alocação por Gerente (Interativo) ---
        Map<Usuario, List<Projeto>> projetosAgrupadosPorGerente = projetosCache.stream()
            .filter(p -> p.getGerente() != null)
            .collect(Collectors.groupingBy(Projeto::getGerente));

        List<Map.Entry<Usuario, List<Projeto>>> gerentesOrdenados = projetosAgrupadosPorGerente.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()))
            .collect(Collectors.toList());
            
        DefaultListModel<Map.Entry<Usuario, List<Projeto>>> modeloGerentes = new DefaultListModel<>();
        if (gerentesOrdenados.isEmpty()) {
            Usuario placeholder = new Usuario();
            placeholder.setNome("Nenhum projeto alocado.");
            modeloGerentes.addElement(Map.entry(placeholder, new ArrayList<>()));
        } else {
            gerentesOrdenados.forEach(modeloGerentes::addElement);
        }
        listaResumoGerentes.setModel(modeloGerentes);
        listaResumoGerentes.revalidate();

        // --- 4. Detalhe das Equipes (Interativo) ---
        DefaultListModel<Equipe> modeloEquipes = new DefaultListModel<>();
        if (equipesCache.isEmpty()) {
            Equipe equipeVazia = new Equipe();
            equipeVazia.setNome("Nenhuma equipe registrada.");
            modeloEquipes.addElement(equipeVazia);
        } else {
            equipesCache.forEach(modeloEquipes::addElement);
        }
        listaEquipes.setModel(modeloEquipes);
        listaEquipes.revalidate();
    }
    
    // Renderer Customizado para a lista de Gerentes
    private class GerenteResumoRenderer extends JLabel implements ListCellRenderer<Map.Entry<Usuario, List<Projeto>>> {
        private static final long serialVersionUID = 1L;
        public GerenteResumoRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Map.Entry<Usuario, List<Projeto>>> list, Map.Entry<Usuario, List<Projeto>> value, int index, boolean isSelected, boolean cellHasFocus) {
            Usuario gerente = value.getKey();
            List<Projeto> projetos = value.getValue();
            
            if (gerente.getCpf() == null) { // Placeholder / Erro
                setText(gerente.getNome());
                setForeground(Color.GRAY.darker());
            } else {
                String nomeCurto = gerente.getNome().split(" ")[0]; 
                setText(String.format("%s (%d projetos)", nomeCurto, projetos.size()));
                setFont(new Font("SansSerif", Font.BOLD, 15));
                setForeground(COR_PRIMARIA.darker());
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
    
    // Renderer Customizado para a lista de Equipes
    private class EquipeResumoRenderer extends JLabel implements ListCellRenderer<Equipe> {
        private static final long serialVersionUID = 1L;
        public EquipeResumoRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Equipe> list, Equipe equipe, int index, boolean isSelected, boolean cellHasFocus) {
            int countMembros = equipe.getMembros() != null ? equipe.getMembros().size() : 0;
            
            if (equipe.getId() == null) { // Placeholder / Erro
                setText(equipe.getNome());
                setFont(new Font("SansSerif", Font.ITALIC, 14));
                setForeground(Color.GRAY.darker());
            } else {
                setText(String.format("%s (%d membros)", equipe.getNome(), countMembros));
                setFont(new Font("SansSerif", Font.PLAIN, 15));
                setForeground(Color.DARK_GRAY);
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
}