package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.controller.Autenticacao;
import br.estudantesuam.ead.nexusacademico.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class TelaLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Autenticacao autenticacao = new Autenticacao();

    private final JTextField campoLogin = new JTextField(20);
    private final JPasswordField campoSenha = new JPasswordField(20);

    public TelaLogin() {
        super("Nexus Acadêmico - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null); // Centraliza a tela
        setResizable(false);

        // Painel Principal
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("ACESSO AO SISTEMA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(0, 102, 90));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painel.add(lblTitulo, gbc);

        // Campo Login
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(lblLogin, gbc);
        
        campoLogin.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        painel.add(campoLogin, gbc);

        // Campo Senha
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(lblSenha, gbc);
        
        campoSenha.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        painel.add(campoSenha, gbc);

        // Botão Login
        JButton btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnLogin.setBackground(new Color(0, 102, 90));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        btnLogin.addActionListener(_ -> realizarLogin());
        painel.add(btnLogin, gbc);
        
        // Associa Enter ao botão de login
        getRootPane().setDefaultButton(btnLogin);

        getContentPane().add(painel);
    }
    
    private void realizarLogin() {
        String login = campoLogin.getText().trim();
        String senha = String.valueOf(campoSenha.getPassword());

        Optional<Usuario> usuarioLogado = autenticacao.autenticar(login, senha);

        if (usuarioLogado.isPresent()) {
            Usuario usuario = usuarioLogado.get();
            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getNome() + "!", "Login OK", JOptionPane.INFORMATION_MESSAGE);
            // Redireciona para a Tela Principal
            new TelaPrincipal(usuario).setVisible(true);
            this.dispose(); // Fecha a tela de login
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            campoSenha.setText("");
        }
    }
}