package br.estudantesuam.ead.nexusacademico.view;

import br.estudantesuam.ead.nexusacademico.controller.ProjetoController;
import br.estudantesuam.ead.nexusacademico.dao.EquipeDAOImpl;
import br.estudantesuam.ead.nexusacademico.dao.UsuarioDAOImpl;
import br.estudantesuam.ead.nexusacademico.model.Equipe;
import br.estudantesuam.ead.nexusacademico.model.Projeto;
import br.estudantesuam.ead.nexusacademico.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class TelaCadastroProjeto extends JFrame {
    private static final long serialVersionUID = 1L;
    private final ProjetoController projetoController = new ProjetoController();
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private final EquipeDAOImpl equipeDAO = new EquipeDAOImpl();
    private final TelaPrincipal telaPai;
    private Projeto projetoAtual;

    private JTextField campoNome, campoDataInicio, campoDataTermino;
    private JTextArea campoDescricao;
    private JComboBox<String> comboStatus;
    private JComboBox<Usuario> comboGerente;
    private JList<Equipe> listaEquipes;
    private DefaultListModel<Equipe> modeloListaEquipes;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Construtor para novo projeto
    public TelaCadastroProjeto(TelaPrincipal telaPai) {
        this(telaPai, null);
    }

    // Construtor para edição de projeto
    public TelaCadastroProjeto(TelaPrincipal telaPai, Projeto projeto) {
        super(projeto == null ? "Cadastro de Projeto" : "Edição de Projeto: " + projeto.getNome());
        this.telaPai = telaPai;
        this.projetoAtual = projeto;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(235, 235, 235));
        setLayout(new BorderLayout());

        carregarDadosIniciais(); // Chama a lógica de carregamento
        inicializarComponentes();
        
        if (projetoAtual != null) {
            preencherFormulario();
        }
    }

    // MÉTODO DE CORREÇÃO: Trata a exceção ao carregar dados do DAO
    private void carregarDadosIniciais() {
        try {
            // Carrega Gerentes (Todos os usuários com perfil 'gerente' ou 'administrador')
            List<Usuario> todosUsuarios = usuarioDAO.listarTodos();
            Vector<Usuario> gerentes = new Vector<>(todosUsuarios.stream()
                .filter(u -> "gerente".equalsIgnoreCase(u.getPerfil()) || "administrador".equalsIgnoreCase(u.getPerfil()))
                .collect(Collectors.toList()));
            
            comboGerente = new JComboBox<>(gerentes);

            // Carrega todas as Equipes
            modeloListaEquipes = new DefaultListModel<>();
            equipeDAO.listarTodos().forEach(modeloListaEquipes::addElement);
            listaEquipes = new JList<>(modeloListaEquipes);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados iniciais (Gerentes/Equipes): " + e.getMessage(), 
                                          "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            // Pode-se optar por fechar a tela se os dados forem essenciais
            // this.dispose();
        }
    }

    private void inicializarComponentes() {
        // Campos e Modelos
        campoNome = new JTextField();
        campoDescricao = new JTextArea(4, 50);
        campoDataInicio = new JTextField("dd/MM/yyyy");
        campoDataTermino = new JTextField("dd/MM/yyyy");
        
        comboStatus = new JComboBox<>(new String[]{"Planejado", "Em Andamento", "Concluído", "Cancelado"});
        
        JScrollPane scrollDescricao = new JScrollPane(campoDescricao);
        JScrollPane scrollEquipes = new JScrollPane(listaEquipes);
        scrollEquipes.setPreferredSize(new Dimension(600, 150));

        // Labels e Componentes para o formulário
        String[] labels = {"Nome", "Descrição", "Data Início", "Data Término Prevista", "Status", "Gerente Responsável", "Equipes"};
        JComponent[] campos = {campoNome, scrollDescricao, campoDataInicio, campoDataTermino, comboStatus, comboGerente, scrollEquipes};
        JButton btnSalvar = criarBotaoSalvar();

        JPanel formulario = DesignTelasUI.criarFormulario(labels, campos, new JButton[]{btnSalvar});
        JPanel cabecalho = DesignTelasUI.criarCabecalho(getTitle(), () -> this.dispose());
        
        add(cabecalho, BorderLayout.NORTH);
        add(new JScrollPane(formulario), BorderLayout.CENTER);
    }
    
    private void preencherFormulario() {
        campoNome.setText(projetoAtual.getNome());
        campoDescricao.setText(projetoAtual.getDescricao());
        
        if (projetoAtual.getDataInicio() != null) {
            campoDataInicio.setText(dateFormat.format(projetoAtual.getDataInicio()));
        }
        if (projetoAtual.getDataTerminoPrevista() != null) {
            campoDataTermino.setText(dateFormat.format(projetoAtual.getDataTerminoPrevista()));
        }
        
        comboStatus.setSelectedItem(projetoAtual.getStatus());
        comboGerente.setSelectedItem(projetoAtual.getGerente());
        
        // Selecionar equipes associadas
        if (projetoAtual.getEquipes() != null && !projetoAtual.getEquipes().isEmpty()) {
            List<Integer> indicesSelecionados = new ArrayList<>();
            List<Equipe> equipesAtuais = projetoAtual.getEquipes();
            
            for (int i = 0; i < modeloListaEquipes.getSize(); i++) {
                Equipe equipeModelo = modeloListaEquipes.getElementAt(i);
                // Compara as equipes usando o equals (que deve comparar o ID)
                if (equipesAtuais.contains(equipeModelo)) { 
                    indicesSelecionados.add(i);
                }
            }
            // Converte List<Integer> para int[]
            int[] indicesArray = indicesSelecionados.stream().mapToInt(i -> i).toArray();
            listaEquipes.setSelectedIndices(indicesArray);
        }
    }

    private JButton criarBotaoSalvar() {
        JButton btnSalvar = new JButton(projetoAtual == null ? "Cadastrar Projeto" : "Salvar Alterações");
        btnSalvar.addActionListener(_ -> salvarProjeto());
        return btnSalvar;
    }

    private void salvarProjeto() {
        try {
            Date dataInicio = dateFormat.parse(campoDataInicio.getText());
            Date dataTermino = dateFormat.parse(campoDataTermino.getText());
            String status = (String) comboStatus.getSelectedItem();
            Usuario gerente = (Usuario) comboGerente.getSelectedItem();
            List<Equipe> equipesSelecionadas = listaEquipes.getSelectedValuesList();

            Projeto projeto = projetoAtual == null ? new Projeto() : projetoAtual;

            projeto.setNome(campoNome.getText());
            projeto.setDescricao(campoDescricao.getText());
            projeto.setDataInicio(dataInicio);
            projeto.setDataTerminoPrevista(dataTermino);
            projeto.setStatus(status);
            projeto.setGerente(gerente);
            projeto.setEquipes(new ArrayList<>(equipesSelecionadas));

            if (projeto.getId() == null) {
                projetoController.salvarProjeto(projeto);
                JOptionPane.showMessageDialog(this, "Projeto cadastrado com sucesso!");
            } else {
                projetoController.atualizarProjeto(projeto);
                JOptionPane.showMessageDialog(this, "Projeto atualizado com sucesso!");
            }
            
            telaPai.atualizarConteudo();
            this.dispose();
            
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar projeto: " + e.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}