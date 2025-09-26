package br.estudantesuam.ead.nexusacademico.util;

import java.util.regex.Pattern;

/**
 * ValidadorUsuario: métodos utilitários para validar CPF, e-mail e outros campos.
 * Implementa algoritmo oficial de verificação do CPF (módulo 11).
 */
public final class ValidadorUsuario {

    private ValidadorUsuario() {}

    private static final Pattern PADRAO_EMAIL = Pattern.compile("^[\\w.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PADRAO_TELEFONE = Pattern.compile("^\\d{10,11}$"); 
    
    /**
     * Valida formato de e-mail.
     * @param email string do e-mail
     * @return true se válido
     */
    public static boolean validarEmail(String email) {
        if (email == null) return false;
        return PADRAO_EMAIL.matcher(email).matches();
    }

    /**
     * Valida CPF (aceita zeros à esquerda). Remove pontos e traços, exige 11 dígitos.
     * @param cpf string do CPF
     * @return true se válido
     */
    public static boolean validarCpf(String cpf) {
        if (cpf == null) return false;
        String apenasDigitos = cpf.replaceAll("\\D", "");
        if (apenasDigitos.length() != 11) return false;
        // rejeita CPF com todos dígitos iguais (ex: 00000000000)
        if (apenasDigitos.matches("(\\d)\\1{10}")) return false;

        try {
            int[] numeros = new int[11];
            for (int i = 0; i < 11; i++) numeros[i] = Character.getNumericValue(apenasDigitos.charAt(i));

            // primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) soma += numeros[i] * (10 - i);
            int resto1 = soma % 11; 
            int dig1 = (resto1 < 2) ? 0 : 11 - resto1;
            if (dig1 != numeros[9]) return false;

            // segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) soma += numeros[i] * (11 - i);
            int resto2 = soma % 11; 
            int dig2 = (resto2 < 2) ? 0 : 11 - resto2;
            return dig2 == numeros[10];
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida se a string é um número de telefone com 10 ou 11 dígitos.
     * Assume que pontos, traços, espaços e parênteses já foram removidos.
     * (Ex: 41999998888 ou 1188887777)
     * @param telefone string do telefone (somente dígitos)
     * @return true se válido
     */
    public static boolean validarTelefone(String telefone) {
        if (telefone == null) return false;
        String apenasDigitos = telefone.replaceAll("\\D", "");
        return PADRAO_TELEFONE.matcher(apenasDigitos).matches();
    }

    /**
     * Valida se texto não é nulo nem vazio.
     */
    public static boolean obrigatorio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
}