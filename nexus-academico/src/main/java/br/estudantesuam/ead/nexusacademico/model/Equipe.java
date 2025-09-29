package br.estudantesuam.ead.nexusacademico.model;

import java.util.List;

public class Equipe {
    private Integer id;
    private String nome;
    private String descricao;
    private List<Usuario> membros; // Usuários vinculados
    
    public Equipe() {}

    public Equipe(Integer id, String nome, String descricao, List<Usuario> membros) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = membros;
    }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Usuario> getMembros() { return membros; }
    public void setMembros(List<Usuario> membros) { this.membros = membros; }

    @Override
    public String toString() {
        return nome; 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipe equipe = (Equipe) o;
        return id != null && id.equals(equipe.id); 
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}