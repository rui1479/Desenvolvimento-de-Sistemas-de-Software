package Business.subsutilizadores;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import Business.subshorario.Curso;
import Business.subshorario.Horario;
import Business.subshorario.UC;

public class Aluno extends Utilizador {
	private boolean estatuto;
	private boolean genero;
	public Curso curso;
	public Set<UC> uc;
	private Horario horario;

	public Aluno() {
		super();
		this.estatuto = false;
		this.curso = new Curso();
		this.uc = new HashSet<>();
	}

	public Aluno(String codUtilizador, String nome, String email, char[] password, boolean estatuto, boolean genero ,Curso curso) {
		super(codUtilizador, nome, email, password);
		this.estatuto = estatuto;
		this.genero = genero;
		this.curso = curso;
		this.uc = new HashSet<>();
	}

	public Aluno(String codUtilizador, String nome, String email, char[] password, boolean estatuto, boolean genero, Curso curso, int id) {
		super(codUtilizador, nome, email, password);
		this.estatuto = estatuto;
		this.genero = genero;
		this.curso = curso;
		this.uc = new HashSet<>();
		this.horario = new Horario(id);

	}

	public Aluno(String codUtilizador, String nome, String email, char[] password, boolean estatuto, boolean genero) {
		super(codUtilizador, nome, email, password);
		this.estatuto = estatuto;
		this.genero = genero;
		this.uc = new HashSet<>();
	}

	public Aluno(String codUtilizador, String nome, String email, char[] password) {
		super(codUtilizador, nome, email, password);
		this.estatuto = false;
	}



	public boolean getEstatuto() {
		return this.estatuto;
	}

	public void setEstatuto(boolean estatuto) {
		this.estatuto = estatuto;
	}

	public Horario getHorario() {
		return this.horario;
	}

	public void setHorario(Horario horario) {
		this.horario = horario;
	}

	public Set<UC> getUCs() {
		return this.uc;
	}

	public void setUCs(HashSet<UC> uc) {
		this.uc = uc;
	}

	public Curso getCurso() {
		return this.curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean getGenero() {
		return this.genero;
	}

	public void setGenero(boolean genero) {
		this.genero = genero;
	}

	public void inscreverNaUC(UC uc) {
		this.uc.add(uc);
	}

	public File exportarMeuHorario(String formato) {
		// Gerar e exportar arquivo no formato especificado
		return new File("horario_" + this.getCodUtilizador() + "." + formato);
	}
}