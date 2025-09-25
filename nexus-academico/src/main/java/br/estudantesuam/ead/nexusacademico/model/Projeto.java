package br.estudantesuam.ead.nexusacademico.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Modelo Projeto: representa uma tarefa ou atividade.
 * Inclui o Gerente (1:1) e as Equipes (M:N) responsáveis.
 */
public class Projeto {
    private Integer id;
    private String nome;
    private String descricao;
    private Date dataInicio;
    private Date dataTerminoPrevista;
    private String status;
    private Usuario gerente; // Gerente responsável (1:1)
    private List<Equipe> equipes; // Equipes vinculadas (M:N)

    public Projeto() {
        this.equipes = new ArrayList<>(); // Inicializa para evitar NullPointerException
    }

    public Projeto(Integer id, String nome, String descricao, Date dataInicio, Date dataTerminoPrevista, String status, Usuario gerente, List<Equipe> equipes) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.status = status;
        this.gerente = gerente;
        this.equipes = equipes != null ? equipes : new ArrayList<>();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataTerminoPrevista() { return dataTerminoPrevista; }
    public void setDataTerminoPrevista(Date dataTerminoPrevista) { this.dataTerminoPrevista = dataTerminoPrevista; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Usuario getGerente() { return gerente; }
    public void setGerente(Usuario gerente) { this.gerente = gerente; }

    public List<Equipe> getEquipes() { return equipes; }
    public void setEquipes(List<Equipe> equipes) { this.equipes = equipes; }
}