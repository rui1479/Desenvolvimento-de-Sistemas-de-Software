package Business.subsutilizadores;

import Business.subshorario.Curso;
import Business.subshorario.Turno;

import java.util.List;

public interface ISSUtilizadores {

	public String autenticarDiretorCurso(String codUtilizador, char[] password);

	public String autenticarAluno(String codUtilizador, char[] password);

	public String registarAluno(String codUtilizador, char[] password);

	public void importarAlunos(List<Aluno> lista);

	public List<Aluno> listarAlunos();

	public void notificarAlunos();

	public Aluno consultarAluno(int aIdAluno);

	public int hashCode();

	public boolean equals(Object aObject);
}