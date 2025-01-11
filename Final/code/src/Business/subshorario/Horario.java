package Business.subshorario;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Business.subsutilizadores.Aluno;

public class Horario {
    private int id;
    private boolean publicado; // Novo atributo para rastrear publicação
    private HashMap<Integer, Turno> turnos; // Integer key vai ser a soma das horas mais o dia
    public Aluno aluno;

    public Horario() {
        this.id = 0;
        this.publicado = false;
        this.turnos = new HashMap<>();
        this.aluno = new Aluno();
    }

    public Horario(int id) {
        this.id = id;
        this.publicado = false; // Valor padrão
        this.turnos = new HashMap<>();
        this.aluno = null; // Valor padrão
    }

    public Horario(int id, Aluno aluno) {
        this.id = id;
        this.publicado = false;
        this.aluno = aluno;
        this.turnos = new HashMap<>();
    }

    public Horario(int id, Aluno aluno, HashMap<Integer,Turno> turnos) {
        this.id = id;
        this.publicado = false;
        this.aluno = aluno;
        this.turnos = turnos;
    }

    public int getId() {
        return id;
    }

    public boolean isPublicado() {
        return publicado;
    }

    public void setPublicado(boolean publicado) {
        this.publicado = publicado;
    }

    public  HashMap<Integer,Turno> getTurnos() {
        return turnos;
    }

    public Turno getTurno(int key) {

        return turnos.get(key); // Retorna null se o turno com o ID não for encontrado
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTurnos(HashMap<Integer,Turno> turnos) {
        this.turnos = turnos;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public void adicionarTurno(int key, Turno turno) {
        this.turnos.put(key, turno);
    }

    public void removerTurno(Turno turno) {
        this.turnos.remove(turno);
    }

    public List<Turno> listarTurnos() {
        return new ArrayList<>(this.turnos.values());
    }



}