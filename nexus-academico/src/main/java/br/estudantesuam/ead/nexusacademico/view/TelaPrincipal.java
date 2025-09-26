package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * TelaPrincipal: Container principal da aplicação.
 * Contém o JTabbedPane para navegação entre PainelDashboard, PainelUsuarios, etc.
 */
public class TelaPrincipal extends JFrame {
    private PainelEquipes painelEquipes;
    private PainelDashboard painelDashboard; 
    private PainelProjetos painelProjetos; 
    private PainelUsuarios painelUsuarios; 
    private final Usuario usuarioLogado;
    
    public TelaPrincipal(Usuario usuarioLogado) {
        super("Nexus Acadêmico - Gestão de Projetos e Equipes");
        this.usuarioLogado = usuarioLogado;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        // Instanciação dos Painéis Reais
        painelDashboard = new PainelDashboard(this); 
        painelProjetos = new PainelProjetos(this);
        painelEquipes = new PainelEquipes(this);
        painelUsuarios = new PainelUsuarios(this);
        
        tabbedPane.addTab("Dashboard", painelDashboard);
        tabbedPane.addTab("Projetos", painelProjetos);
        tabbedPane.addTab("Equipes", painelEquipes);
        tabbedPane.addTab("Usuários", painelUsuarios);
        
        // Listener para garantir que as tabelas sejam carregadas ao mudar de aba
        tabbedPane.addChangeListener(_ -> {
            if (tabbedPane.getSelectedComponent() == painelProjetos) {
                painelProjetos.carregarProjetos();
            } else if (tabbedPane.getSelectedComponent() == painelEquipes) {
                painelEquipes.carregarEquipes();
            } else if (tabbedPane.getSelectedComponent() == painelUsuarios) {
                painelUsuarios.carregarUsuarios();
            } else if (tabbedPane.getSelectedComponent() == painelDashboard) {
                painelDashboard.carregarDashboard();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
        
        // Exibe informação do usuário logado
        JLabel lblStatus = new JLabel("Logado como: " + usuarioLogado.getNome() + " (" + usuarioLogado.getCargo() + ")");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblStatus, BorderLayout.SOUTH);
    }
    
    public void carregarEquipes() {
        if (painelEquipes != null) {
            painelEquipes.carregarEquipes();
        }
    }
    
    public void carregarProjetos() {
        if (painelProjetos != null) {
            painelProjetos.carregarProjetos();
        }
    }
    
    public void carregarUsuarios() {
        if (painelUsuarios != null) {
            painelUsuarios.carregarUsuarios();
        }
    }
    
    // Método para atualizar o dashboard
    public void carregarDashboard() {
        if (painelDashboard != null) {
            painelDashboard.carregarDashboard();
        }
    }
    
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void atualizarConteudo() {
        carregarEquipes();
        carregarProjetos();
        carregarUsuarios();
        carregarDashboard();
    }
}