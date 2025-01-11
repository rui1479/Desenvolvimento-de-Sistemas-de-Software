package data;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        // Caminhos para os arquivos SQL
        String createScript = "../SQL/create.sql";
        String populateScript = "../SQL/populate.sql";

        try (Connection connection = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement statement = connection.createStatement()) {

            // Executar o script de criação
            executeScript(statement, createScript);
            // Executar o script de população
            executeScript(statement, populateScript);

            System.out.println("Banco de dados inicializado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }

    private static void executeScript(Statement statement, String filePath) throws Exception {
        String script = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] commands = script.split(";"); // Divide os comandos por ';'

        for (String command : commands) {
            if (!command.trim().isEmpty()) {
                statement.execute(command.trim());
            }
        }
        System.out.println("Script executado: " + filePath);
    }
}
