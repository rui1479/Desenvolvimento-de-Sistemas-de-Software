package ui.text;

import ui.Menu;
import ui.text.aluno.MainAlunoTextUI;
import ui.text.diretor.MainDiretorTextUI;

public class MainTextUI {

    private Menu menu;

    public MainTextUI() {
        this.menu = new Menu("Página Inicial da Swap");
        this.menu.addOption("0 - Sair do sistema", () -> {
        });
        this.menu.addOption("1 - Interface de Aluno", () -> new MainAlunoTextUI().run());
        this.menu.addOption("2 - Interface de Diretor de Curso", () -> new MainDiretorTextUI().run());
    }

    public void run() {
        this.menu.run();
    }
}
