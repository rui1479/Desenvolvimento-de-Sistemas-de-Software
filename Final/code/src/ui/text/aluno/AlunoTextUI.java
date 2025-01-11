package ui.text.aluno;

import java.util.List;

import Business.subshorario.Turno;
import Business.subsutilizadores.UtilizadoresFacade;
import ui.Menu;

import Business.subshorario.HorariosFacade;
import Business.subshorario.Horario;

public class AlunoTextUI {

    private Menu menu;
    private String alunoId;
    private String alunoNome;
    private UtilizadoresFacade utilizadoresFacade;
    private HorariosFacade horariosFacade;

    public AlunoTextUI(String alunoId, String nome) {
        this.alunoId = alunoId;
        this.alunoNome = nome;
        this.utilizadoresFacade = new UtilizadoresFacade();
        this.horariosFacade = new HorariosFacade();
        this.menu = new Menu("Área do Aluno: " + nome);
        this.menu.addOption("0 - Retroceder", () -> {});
        this.menu.addOption("1 - Consultar Horário", this::consultarHorario);
    }

    public void run() {
        this.menu.run();
    }

    private void consultarHorario() {
        Horario horario = this.horariosFacade.consultarHorario(utilizadoresFacade.consultarAluno(Integer.parseInt(this.alunoId)));
        if (horario == null) {
            Menu.errorMessage("Não foi possível encontrar um horário associado.");
        }
    }
}