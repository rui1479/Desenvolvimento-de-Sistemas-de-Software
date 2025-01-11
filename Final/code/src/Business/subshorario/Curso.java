package Business.subshorario;

import java.util.HashSet;
import java.util.Set;

import Business.subsutilizadores.Aluno;
import Business.subsutilizadores.DiretorCurso;

public class Curso {
    private String id;
    private String nome;
    private Set<UC> ucs;
    private DiretorCurso diretor;
    private Set<Aluno> alunos;

    public Curso() {
        this.id = "";
        this.nome = "";
        this.ucs = new HashSet<>();
        this.diretor = new DiretorCurso();
        this.alunos = new HashSet<>();
    }

    public Curso(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Curso(String id, String nome, Set<UC> ucs, DiretorCurso diretor) {
        this.id = id;
        this.nome = nome;
        this.ucs = ucs;
        this.diretor = diretor;
    }

    public Curso(String id, String nome, DiretorCurso diretor) {
        this.id = id;
        this.nome = nome;
        this.diretor = diretor;
    }

    public String getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public Set<UC> getUCs() {
        return this.ucs;
    }

    public DiretorCurso getDiretor() {
        return this.diretor;
    }

    public Set<Aluno> getAlunos() {
        return this.alunos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setUCs(Set<UC> ucs) {
        this.ucs = ucs;
    }

    public void setAlunos(Set<Aluno> alunos) {
        this.alunos = alunos;
    }

    public void setDiretor(DiretorCurso diretor) {
        this.diretor = diretor;
    }





}