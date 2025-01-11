package Business.subshorario;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Business.subsutilizadores.Aluno;

public class Turno {
	private int id;
	private Time inicio;
	private Time fim;
	private int num;
	private int dia;
	private int tipo; // 1 - T , 2 - Tp, 3 -  Pl
	private int limite;
	public UC UC;
	private Sala sala;
	private Set<Aluno> alunos = new HashSet<>();

	public Turno(){
		this.id = 0;
		this.inicio = Time.valueOf(LocalTime.of(0, 0));
		this.fim = Time.valueOf(LocalTime.of(0, 0));
		this.num = 0;
		this.dia = 0;
		this.tipo = 0;
		this.limite = 0;
		this.UC = new UC();
		this.sala = new Sala();
	}

	public Turno(int id, Time inicio, Time fim, int num, int dia,int tipo,int limite, UC uc, Sala sala) {
		this.id = id;
		this.inicio = inicio;
		this.fim = fim;
		this.num = num;
		this.dia = dia;
		this.tipo = tipo;
		this.limite = limite;
		this.UC = uc;
		this.sala = sala;
	}


	public Turno(int id, LocalTime inicio, LocalTime fim, int num, int dia, int tipo, int limite, Sala sala, UC uc) {
		this.id = id;
		this.inicio = Time.valueOf(inicio);
		this.fim = Time.valueOf(fim);
		this.num = num;
		this.dia = dia;
		this.tipo = tipo;
		this.limite = limite;
		this.UC = uc;
		this.sala = sala;
	}


	public int getId() {
		return this.id;
	}

	public Time getInicio() {
		return this.inicio;
	}

	public Time getFim() {
		return this.fim;
	}

	public int getNum() {
		return this.num;
	}

	public int getDia() {
		return this.dia;
	}

	public UC getUC() {
		return this.UC;
	}
	public int getTipo() {
		return tipo;
	}
	public int getLimite() {
		return this.limite;
	}

	public Sala getSala() {
		return this.sala;
	}

	public Set<Aluno> getAlunos() {
		return this.alunos;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInicio(Time inicio) {
		this.inicio = inicio;
	}

	public void setFim(Time fim) {
		this.fim = fim;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public void setUC(UC uc) {
		this.UC = uc;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
	}

	public void setAlunos(Set<Aluno> alunos) {
		this.alunos = alunos;
	}

	public void setLimite(int limite) {
		this.limite = limite;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}


	public void adicionarAluno(Aluno aluno) {
		this.alunos.add(aluno);
		System.out.println("TURNO : " + this.alunos.size());
	}

	public List<Aluno> listarAlunos() {
		return new ArrayList<>(alunos);
	}

}