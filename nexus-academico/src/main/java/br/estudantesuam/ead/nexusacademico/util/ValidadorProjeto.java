package br.estudantesuam.ead.nexusacademico.util;

import br.estudantesuam.ead.nexusacademico.model.Projeto;

import java.util.Calendar;
import java.util.Date;

/**
 * ValidadorProjeto: métodos para validar a entidade Projeto.
 * Toda a lógica de validação está contida nesta classe.
 */
public final class ValidadorProjeto {

    private ValidadorProjeto() {}

    /**
     * Valida os dados de um projeto.
     * @param projeto a ser validado
     * @throws IllegalArgumentException com a mensagem de erro apropriada
     */
    public static void validar(Projeto projeto) {
        if (projeto == null) {
            throw new IllegalArgumentException("Projeto não pode ser nulo.");
        }
        if (!obrigatorio(projeto.getNome())) {
            throw new IllegalArgumentException("O nome do projeto é obrigatório.");
        }

        // Validação da descrição
        if (projeto.getDescricao() != null && projeto.getDescricao().length() > 800) {
            throw new IllegalArgumentException("A descrição do projeto não pode ter mais de 800 caracteres.");
        }

        // Validação da data de início
        if (projeto.getDataInicio() == null) {
            throw new IllegalArgumentException("A data de início é obrigatória.");
        }
        validarAnoData(projeto.getDataInicio());
        
        // Validação da data de término
        if (projeto.getDataTerminoPrevista() == null) {
            throw new IllegalArgumentException("A data de término prevista é obrigatória.");
        }
        validarAnoData(projeto.getDataTerminoPrevista());
        
        if (projeto.getDataTerminoPrevista().before(projeto.getDataInicio())) {
            throw new IllegalArgumentException("A data de término prevista não pode ser anterior à data de início.");
        }
        
        if (!obrigatorio(projeto.getStatus())) {
            throw new IllegalArgumentException("O status do projeto é obrigatório.");
        }
        
        if (projeto.getGerente() == null || !obrigatorio(projeto.getGerente().getCpf())) {
            throw new IllegalArgumentException("Gerente é um campo obrigatório. Escolha um gerente para o projeto.");
        }
    }

    /**
     * Checa se a string não é nula, vazia ou contém apenas espaços em branco.
     * @param texto string a ser verificada
     * @return true se o texto for válido
     */
    private static boolean obrigatorio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Valida o ano da data, verificando se é maior ou igual a 1990, usando Calendar.
     * @param data a ser validada
     */
    private static void validarAnoData(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        if (cal.get(Calendar.YEAR) < 1990) {
            throw new IllegalArgumentException("Informe uma data válida (a partir de 1990).");
        }
    }
}