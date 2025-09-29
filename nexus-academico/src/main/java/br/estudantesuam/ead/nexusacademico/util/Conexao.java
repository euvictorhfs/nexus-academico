package br.estudantesuam.ead.nexusacademico.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexao (Factory): Classe utilitária responsável por criar e fornecer
 * uma nova conexão com o banco de dados SQLite. Este é o ponto central
 * para gerenciar a persistência de dados.
 * <p>
 * O arquivo de banco será 'nexus.db' no diretório de execução da aplicação.
 * </p>
 */
public class Conexao {

    /**
     * URL de conexão para o banco de dados SQLite.
     * O formato é jdbc:sqlite:[caminho_do_arquivo].
     */
    private static final String URL_CONEXAO = "jdbc:sqlite:nexus.db";

    /**
     * Retorna uma nova conexão JDBC ativa com o banco de dados.
     * </p>
     * @return Connection ativa com o banco de dados.
     * @throws SQLException se ocorrer um erro ao estabelecer a conexão.
     */
    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL_CONEXAO);
    }
}