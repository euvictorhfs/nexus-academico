package br.estudantesuam.ead.nexusacademico.util;

import br.estudantesuam.ead.nexusacademico.model.Equipe;

/**
 * ValidadorEquipe: Métodos para validar a entidade Equipe.
 * Toda a lógica de validação de dados de negócio está contida nesta classe.
 */
public final class ValidadorEquipe {

    private ValidadorEquipe() {}

    /**
     * Valida os dados de uma equipe.
     * @param equipe a ser validada
     * @throws IllegalArgumentException com a mensagem de erro apropriada
     */
    public static void validar(Equipe equipe) {
        if (equipe == null) {
            throw new IllegalArgumentException("Equipe não pode ser nula.");
        }
        if (!obrigatorio(equipe.getNome())) {
            throw new IllegalArgumentException("O nome da equipe é obrigatório.");
        }
        if (equipe.getMembros() == null || equipe.getMembros().isEmpty()) {
            throw new IllegalArgumentException("A equipe deve ter pelo menos um membro.");
        }
        
        // Validação da descrição (opcional, mas checa limite de tamanho, se aplicável)
        if (equipe.getDescricao() != null && equipe.getDescricao().length() > 500) {
            throw new IllegalArgumentException("A descrição não pode ter mais de 500 caracteres.");
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
}