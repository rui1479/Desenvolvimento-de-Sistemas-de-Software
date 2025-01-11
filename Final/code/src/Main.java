import data.DatabaseInitializer;
import ui.text.MainTextUI;

public class Main{

    public static void main(String[] args) throws Exception{

        // Inicializar o banco de dados
        DatabaseInitializer.initialize();

        new MainTextUI().run();
    }
}