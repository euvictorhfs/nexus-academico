package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.dao.UsuarioDAOImpl;
import br.estudantesuam.ead.nexusacademico.controller.EquipeController;
import br.estudantesuam.ead.nexusacademico.model.Equipe;
import br.estudantesuam.ead.nexusacademico.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TelaCadastroEquipe extends JFrame {
    private final EquipeController equipeController = new EquipeController();
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private final TelaPrincipal telaPai;
    private Equipe equipeEditando; 

    private final JTextField campoNome = new JTextField();
    private final JTextArea campoDescricao = new JTextArea(4, 50); 
    private final JList<Usuario> listaMembros;
    private final DefaultListModel<Usuario> modeloListaMembros = new DefaultListModel<>();

    public TelaCadastroEquipe(TelaPrincipal telaPai) {
        this(telaPai, null);
    }

    public TelaCadastroEquipe(TelaPrincipal telaPai, Equipe equipe) {
        super(equipe == null ? "Cadastro de Equipe" : "Edição de Equipe: " + (equipe != null ? equipe.getNome() : ""));
        this.telaPai = telaPai;
        this.equipeEditando = equipe;
        
        this.listaMembros = new JList<>(modeloListaMembros);
        this.listaMembros.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario u) {
                    setText(u.getNome() + " (" + u.getCargo() + ")");
                }
                return this;
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(235, 235, 235));
        setLayout(new BorderLayout());

        carregarMembrosDisponiveis();

        // Cabeçalho
        JPanel cabecalho = DesignTelasUI.criarCabecalho(
            equipe == null ? "Novo Cadastro de Equipe" : "Edição de Equipe", 
            () -> dispose()
        );
        add(cabecalho, BorderLayout.NORTH);

        // Formulário
        String[] labels = {"Nome da Equipe", "Descrição (Opcional)", "Membros (Ctrl+Clique para multi-seleção)"};
        
        JScrollPane scrollDescricao = new JScrollPane(campoDescricao);
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);
        scrollDescricao.setPreferredSize(new Dimension(600, 100)); 
        JScrollPane scrollMembros = new JScrollPane(listaMembros);
        scrollMembros.setPreferredSize(new Dimension(600, 200)); 
        
        JComponent[] campos = {
            campoNome, scrollDescricao, scrollMembros
        };
        
        JButton btnSalvar = criarBotaoSalvar();
        JButton btnCancelar = new JButton("Cancelar");
        DesignTelasUI.configurarBotaoSecundario(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());
        
        JPanel painelFormulario = DesignTelasUI.criarFormulario(labels, campos, new JButton[]{btnSalvar, btnCancelar});

        JScrollPane scrollPane = new JScrollPane(painelFormulario);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        if (equipeEditando != null) {
            preencherDados();
        }
    }

    private void carregarMembrosDisponiveis() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            modeloListaMembros.clear();
            for (Usuario u : usuarios) {
                modeloListaMembros.addElement(u);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de usuários: " + e.getMessage(), "Erro de DAO", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void preencherDados() {
        campoNome.setText(equipeEditando.getNome());
        campoDescricao.setText(equipeEditando.getDescricao());

        // Selecionar membros existentes
        if (equipeEditando.getMembros() != null) {
            List<Integer> indicesSelecionados = new ArrayList<>();
            List<Usuario> membrosEquipe = equipeEditando.getMembros();

            List<String> cpfsMembros = membrosEquipe.stream()
                                            .map(Usuario::getCpf)
                                            .toList();
            
            for (int i = 0; i < modeloListaMembros.getSize(); i++) {
                if (cpfsMembros.contains(modeloListaMembros.getElementAt(i).getCpf())) {
                    indicesSelecionados.add(i);
                }
            }
            
            int[] indices = indicesSelecionados.stream().mapToInt(i -> i).toArray();
            listaMembros.setSelectedIndices(indices);
        }
    }

    private JButton criarBotaoSalvar() {
        JButton btnSalvar = new JButton(equipeEditando == null ? "Cadastrar Equipe" : "Atualizar Equipe");
        DesignTelasUI.configurarBotaoPrimario(btnSalvar);
        
        btnSalvar.addActionListener(e -> {
            try {
                String nome = campoNome.getText();
                String descricao = campoDescricao.getText();
                List<Usuario> membrosSelecionados = listaMembros.getSelectedValuesList();

                Equipe equipe = equipeEditando != null 
                    ? equipeEditando 
                    : new Equipe();
                    
                equipe.setNome(nome);
                equipe.setDescricao(descricao);
                equipe.setMembros(new ArrayList<>(membrosSelecionados));

                if (equipeEditando == null) {
                    equipeController.salvarEquipe(equipe);
                    JOptionPane.showMessageDialog(this, "Equipe cadastrada com sucesso!");
                } else {
                    equipeController.atualizarEquipe(equipe);
                    JOptionPane.showMessageDialog(this, "Equipe atualizada com sucesso!");
                }

                telaPai.atualizarConteudo();
                this.dispose();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar equipe: " + ex.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        return btnSalvar;
    }
}