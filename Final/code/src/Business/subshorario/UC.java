package Business.subshorario;

import java.util.*;

// import Implementation_Model.Business.subshorarioDAO.Turno;
// import Business.subshorario.Turno;
import Business.subsutilizadores.Aluno;

public class UC {
	private String id;
	private String nome;
	private String preferencia;
	private int ano;
	private boolean opcional;
	public Curso curso;
	private List<Turno> turnos;
	private Set<Aluno> alunos;

	public UC() {
		this.id = "";
		this.nome = "";
		this.preferencia = "";
		this.ano = 0;
		this.opcional = false;
		this.curso = new Curso();
		this.turnos = new ArrayList<>();
	}

	public UC (String id, String nome, int ano) {
		this.id = id;
		this.nome = nome;
		this.preferencia = "";
		this.ano = ano;
		this.opcional = false;
		this.curso = new Curso();
		this.turnos = new ArrayList<>();

	}

	public UC( String id, String nome, int ano, boolean opcional,Curso curso) {
		this.id = id;
		this.nome = nome;
		this.ano = ano;
		this.opcional = opcional;
		this.curso = curso;
		this.turnos = new ArrayList<>();

	}

	public UC(String id, String nome, String preferencia, int ano, boolean opcional, Curso curso) {
		this.id = id;
		this.nome = nome;
		this.preferencia = preferencia;
		this.ano = ano;
		this.opcional = opcional;
		this.curso = curso;
		this.turnos = new ArrayList<>();
	}


	public String getNome() {
		return this.nome;
	}

	public String getId() {
		return this.id;
	}

	public String getPreferencia() {
		return this.preferencia;
	}

	public int getAno() {
		return this.ano;
	}

	public boolean getOpcional() {
		return this.opcional;
	}

	public Curso getCurso() {
		return this.curso;
	}

	public List<Turno> getTurnos() {
		return this.turnos;
	}

	public Set<Aluno> getAlunos() {
		return this.alunos;
	}

	public void addAluno(Aluno a) {
		if (this.alunos == null) this.alunos = new HashSet<>();
		this.alunos.add(a);
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPreferencia(String preferencia) {
		this.preferencia = preferencia;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public void setOpcional(boolean opcional) {
		this.opcional = opcional;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public void setTurnos(List<Turno> turnos) {
		this.turnos = turnos;
	}

	public void setAlunos(Set<Aluno> alunos) {
		this.alunos = alunos;
	}

	public List<Business.subshorario.Turno> listarTurnos() {
		throw new UnsupportedOperationException();
	}

	public void adicionarTurno(Turno turno) {
		if (this.turnos == null) {
			this.turnos = new ArrayList<>();
		}
		this.turnos.add(turno);
	}


	public void removerTurno(Turno turno) {
		turnos.remove(turno);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;  // Verifica se as referências são as mesmas
		if (obj == null || getClass() != obj.getClass()) return false;
		UC uc = (UC) obj;

		return Objects.equals(id, uc.id) &&
				Objects.equals(nome, uc.nome) &&
				ano == uc.ano;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nome, ano);
	}
}