package ui.text.aluno;

import Business.subsutilizadores.UtilizadoresFacade;
import ui.Menu;

public class MainAlunoTextUI {

    private Menu menu;
    private UtilizadoresFacade utilizadoresFacade;

    public MainAlunoTextUI() {
        this.utilizadoresFacade = new UtilizadoresFacade();
        this.menu = new Menu("Área do Aluno");
        this.menu.addOption("0 - Retroceder", () -> {
        });
        this.menu.addOption("1 - Entrar", () -> entrar());
        this.menu.addOption("2- Registar Password", () -> registarPassword());
    }

    public void run() {
        this.menu.run();
    }

    private void entrar() {
        String alunoId = Menu.readInput("Insere o ID do Aluno: ");
        char[] password = Menu.readInput("Insere a password: ").toCharArray();
        String nome = this.utilizadoresFacade.autenticarAluno(alunoId, password);

        if (nome == null) {
            Menu.errorMessage("Credenciais inválidas.");
            return;
        }

        if(nome.equals("falta")) {
            System.out.println("O/A aluno/a " + alunoId + " não contêm password!");
            return;
        }

        new AlunoTextUI(alunoId, nome).run();
    }

    private void registarPassword() {
        String alunoId = Menu.readInput("Insere o ID do Aluno: ");
        char[] password = Menu.readInput("Insere a password: ").toCharArray();
        String nome = this.utilizadoresFacade.registarAluno(alunoId, password);

        if (nome == null) {
            Menu.errorMessage("Aluno não encontrado!");
            return;
        }
    }
}
