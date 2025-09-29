package br.estudantesuam.ead.nexusacademico.model;

import java.util.Objects;

/**
 * Modelo que representa um Usuário no sistema.
 * Aplica Encapsulamento.
 */
public class Usuario {
    // Campos privados
    private String nome;
    private String cpf; // Chave Primária
    private String email;
    private String cargo;
    private String login;
    private String senha;
    private String perfil; // administrador, gerente, colaborador
    private String githubUrl;
    private String telefoneWhatsapp;
    private String curso;

    // Construtor padrão
    public Usuario() {}

    // Construtor completo
    public Usuario(String nome, String cpf, String email, String cargo, String login, String senha, String perfil, String githubUrl, String telefoneWhatsapp, String curso) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
        this.githubUrl = githubUrl;
        this.telefoneWhatsapp = telefoneWhatsapp;
        this.curso = curso;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getTelefoneWhatsapp() { return telefoneWhatsapp; }
    public void setTelefoneWhatsapp(String telefoneWhatsapp) { this.telefoneWhatsapp = telefoneWhatsapp; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    // Sobrescrita para JComboBox (ex: comboGerente)
    @Override
    public String toString() {
        return nome; 
    }

    // Sobrescrita para comparações (usada no JList e ComboBox)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(cpf, usuario.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}