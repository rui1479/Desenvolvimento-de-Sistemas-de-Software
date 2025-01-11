package Business.subsutilizadores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Business.subshorario.Curso;
import Business.subshorario.Turno;
import data.business.utilizador.AlunoDAO;
import data.business.utilizador.DiretorDAO;

public class UtilizadoresFacade implements ISSUtilizadores {
	private DiretorDAO diretorDAO;
	private AlunoDAO alunoDAO;

	public UtilizadoresFacade() {
		this.diretorDAO = DiretorDAO.getInstance();
		this.alunoDAO = AlunoDAO.getInstance();
	}


	public DiretorCurso getDiretorCurso(String diretorId) {
		return this.diretorDAO.get(diretorId);
	}

	// Método de autenticação do Diretor de Curso
	@Override
	public String autenticarDiretorCurso(String codUtilizador, char[] password) {
		DiretorCurso diretor = this.diretorDAO.get(codUtilizador);
		if (diretor != null) {
			char[] passwordArmazenada = diretor.getPassword();
			if (Arrays.equals(password, passwordArmazenada)) {
				return diretor.getNome();
			}
		}
		return null;
	}


	// Método de autenticação do Aluno
	@Override
	public String autenticarAluno(String codUtilizador, char[] password) {
		Aluno aluno = this.alunoDAO.get(codUtilizador);
		if (aluno != null) {
			char[] passwordArmazenada = aluno.getPassword();
			if(passwordArmazenada == null){
				return "falta";
			}
			if (Arrays.equals(password, passwordArmazenada)) {
				return aluno.getNome();
			}
		}
		return null;
	}

	@Override
	public String registarAluno(String codUtilizador, char[] password) {
		Aluno aluno = this.alunoDAO.get(codUtilizador);
		if (aluno != null) {
			aluno.setPassword(password);
			alunoDAO.put(codUtilizador, aluno);
			System.out.println("Aluno registado com sucesso.");
			return aluno.getNome();
		}
		return null;
	}


	// Método de importação de alunos
	public void importarAlunos(List<Aluno> lista) {
		List<String> resultados = new ArrayList<>();

		for (Aluno aluno : lista) {
			if (this.alunoDAO.containsKey(aluno.getCodUtilizador())) {
				resultados.add("Erro: Aluno duplicado (" + aluno.getCodUtilizador() + ").");
			} else {
				this.alunoDAO.put(aluno.getCodUtilizador(), aluno);
				resultados.add("Aluno importado com sucesso: " + aluno.getCodUtilizador() + " - " + aluno.getNome());
			}
		}

		System.out.println("Importação de alunos concluída com sucesso.");

	}

	// Método de listagem de alunos
	@Override
	public List<Aluno> listarAlunos() {
		List<Aluno> alunos = new ArrayList<>();
		for (Aluno aluno : this.alunoDAO.values()) {
			alunos.add(aluno);
		}
		return alunos;
	}

	// Método de notificação de alunos
	@Override
	public void notificarAlunos() {
		List<Aluno> alunos = listarAlunos();
		for (Aluno aluno : alunos) {
			System.out.println("Notificação enviada para: " + aluno.getEmail());
		}
	}

	// Método de consulta de aluno
	@Override
	public Aluno consultarAluno(int aIdAluno) {
		return this.alunoDAO.get(String.valueOf(aIdAluno));
	}

}
