package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.controller.UsuarioController;
import br.estudantesuam.ead.nexusacademico.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroUsuario extends JFrame {
    private static final long serialVersionUID = 1L;

    private final UsuarioController usuarioController = new UsuarioController();
    private final TelaPrincipal telaPai;
    private Usuario usuarioAtual;

    private final JTextField campoNome = new JTextField();
    private final JTextField campoCpf = new JTextField();
    private final JTextField campoEmail = new JTextField();
    private final JTextField campoLogin = new JTextField();
    private final JTextField campoGithub = new JTextField();
    private final JTextField campoWhatsapp = new JTextField();
    private final JTextField campoCurso = new JTextField();
    private final JPasswordField campoSenha = new JPasswordField();
    private final JComboBox<String> comboCargo;

    private final String[] cargos = {"Colaborador", "Gerente", "Administrador"};

    public TelaCadastroUsuario(TelaPrincipal telaPai) {
        this(telaPai, null);
    }

    public TelaCadastroUsuario(TelaPrincipal telaPai, Usuario usuario) {
        super(usuario == null ? "Cadastro de Usuário" : "Edição de Usuário");
        this.telaPai = telaPai;
        this.usuarioAtual = usuario;
        
        this.comboCargo = new JComboBox<>(cargos);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(235, 235, 235));
        setLayout(new BorderLayout());

        // 1. Cabeçalho
        JPanel cabecalho = DesignTelasUI.criarCabecalho(
            usuario == null ? "Novo Cadastro de Usuário" : "Edição de Usuário", 
            () -> dispose() 
        );
        add(cabecalho, BorderLayout.NORTH);

        // 2. Formulário
        String[] labels = {"Nome Completo", "CPF (somente dígitos)", "E-mail", "Login", "Senha", "Cargo", "GitHub URL (Opcional)", "WhatsApp (DD+Número - Opcional)", "Curso/Área (Opcional)"};
        JComponent[] campos = {campoNome, campoCpf, campoEmail, campoLogin, campoSenha, comboCargo, campoGithub, campoWhatsapp, campoCurso};
        
        JButton btnSalvar = criarBotaoSalvar();
        JButton btnCancelar = new JButton("Cancelar");
        DesignTelasUI.configurarBotaoSecundario(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());
        
        JPanel painelFormulario = DesignTelasUI.criarFormulario(labels, campos, new JButton[]{btnSalvar, btnCancelar});

        JScrollPane scrollPane = new JScrollPane(painelFormulario);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        if (usuarioAtual != null) {
            preencherDados();
        } else {
             campoCpf.setEditable(true);
             campoCpf.setBackground(Color.WHITE);
        }
    }
    
    private void preencherDados() {
        campoNome.setText(usuarioAtual.getNome());
        campoCpf.setText(usuarioAtual.getCpf());
        campoCpf.setEditable(false); 
        campoCpf.setBackground(new Color(240, 240, 240));
        campoEmail.setText(usuarioAtual.getEmail());
        campoLogin.setText(usuarioAtual.getLogin());
        campoGithub.setText(usuarioAtual.getGithubUrl());
        campoWhatsapp.setText(usuarioAtual.getTelefoneWhatsapp());
        campoCurso.setText(usuarioAtual.getCurso());
        
        String cargoAtual = usuarioAtual.getCargo();
        for (int i = 0; i < cargos.length; i++) {
            if (cargos[i].equalsIgnoreCase(cargoAtual)) {
                comboCargo.setSelectedIndex(i);
                break;
            }
        }
        
        campoSenha.setText("");
    }

    private JButton criarBotaoSalvar() {
        JButton btnSalvar = new JButton(usuarioAtual == null ? "Cadastrar Usuário" : "Atualizar Usuário");
        DesignTelasUI.configurarBotaoPrimario(btnSalvar);
        
        btnSalvar.addActionListener(e -> {
            try {
                String senhaDigitada = String.valueOf(campoSenha.getPassword()).trim();
                String senhaParaSalvar = senhaDigitada;
                String cargoSelecionado = (String) comboCargo.getSelectedItem();

                if (usuarioAtual != null && senhaDigitada.isEmpty()) {
                    senhaParaSalvar = usuarioAtual.getSenha();
                }
                
                Usuario usuario = usuarioAtual == null ? new Usuario() : usuarioAtual;

                usuario.setNome(campoNome.getText());
                usuario.setCpf(campoCpf.getText()); 
                usuario.setEmail(campoEmail.getText());
                usuario.setCargo(cargoSelecionado);
                usuario.setLogin(campoLogin.getText());
                usuario.setGithubUrl(campoGithub.getText());
                usuario.setTelefoneWhatsapp(campoWhatsapp.getText());
                usuario.setCurso(campoCurso.getText());
                usuario.setSenha(senhaParaSalvar);
                // O perfil é o mesmo que o cargo
                usuario.setPerfil(cargoSelecionado != null ? cargoSelecionado.toLowerCase() : null);

                if (usuarioAtual == null) {
                    usuarioController.salvarUsuario(usuario);
                    JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
                } else {
                    usuarioController.atualizarUsuario(usuario);
                    JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
                }

                telaPai.atualizarConteudo();
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        return btnSalvar;
    }
}