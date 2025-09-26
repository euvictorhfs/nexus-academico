package br.estudantesuam.ead.nexusacademico;

import br.estudantesuam.ead.nexusacademico.util.Conexao;
import br.estudantesuam.ead.nexusacademico.view.TelaLogin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Main: Ponto de entrada da aplicação.
 * Responsável por:
 * 1. Inicializar o esquema do banco de dados (SQLite)
 *    a partir do arquivo 'schema.sql'.
 * 2. Iniciar a interface gráfica Swing, abrindo a {@link TelaLogin}.
 */
public class Main {
    
    /**
     * Método principal (entry point).
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {    	
    	inicializarBanco();
        javax.swing.SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }

    /**
     * Inicializa ou atualiza o esquema do banco de dados SQLite
     * lendo e executando o arquivo {@code schema.sql}.
     */
    private static void inicializarBanco() {
        // Uso de try-with-resources para garantir o fechamento seguro de Connection e Statement.
		try (Connection c = Conexao.obterConexao();
	            Statement st = c.createStatement()) {
	
	           // Carrega o arquivo schema.sql como um recurso do classpath.
	           InputStream is = Main.class.getResourceAsStream("/db/schema.sql");
	           
	           if (is == null) {
	               System.err.println("ERRO: O arquivo 'schema.sql' não foi encontrado em /db/schema.sql. Verifique o caminho no JAR/recursos do Maven.");
	               return;
	           }
	           
	           // Lê o conteúdo do arquivo e o junta em uma única string SQL.
	           try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
	               String sql = reader.lines().collect(Collectors.joining("\n"));
	
	               // Executa os comandos SQL um por um, separados por ';'.
	               for (String comando : sql.split(";")) {
	                   String cmd = comando.trim();
	                   if (!cmd.isEmpty()) st.execute(cmd);
	               }
	           } 
	
	           System.out.println("Status: Banco inicializado / esquema aplicado (se necessário).");
	
	       } catch (Exception e) {
	           System.err.println("ERRO FATAL: Falha ao inicializar o banco de dados.");
	           e.printStackTrace();
	       }
	   }
}