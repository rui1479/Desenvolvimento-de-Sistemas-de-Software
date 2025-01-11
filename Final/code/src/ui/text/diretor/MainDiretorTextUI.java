package ui.text.diretor;

import Business.subsutilizadores.ISSUtilizadores;
import Business.subsutilizadores.UtilizadoresFacade;
import ui.Menu;

public class MainDiretorTextUI {

    private Menu menu;
    private ISSUtilizadores utilizadoresFacade;

    public MainDiretorTextUI() {
        this.utilizadoresFacade = new UtilizadoresFacade();
        this.menu = new Menu("Área do Diretor de Curso");
        this.menu.addOption("0 - Retroceder", () -> {
        });
        this.menu.addOption("1 - Entrar", this::entrar);
    }

    public void run() {
        this.menu.run();
    }

    private void entrar() {

        String diretorId = Menu.readInput("Insere código de Diretor: ");
        String password = Menu.readInput("Insere a password: ");

        String nome = this.utilizadoresFacade.autenticarDiretorCurso(diretorId, password.toCharArray());


        if (nome == null) {
            Menu.errorMessage("Credenciais inválidas.");
            return;
        }

        new DiretorTextUI(diretorId, nome, diretorId).run();
    }
}
