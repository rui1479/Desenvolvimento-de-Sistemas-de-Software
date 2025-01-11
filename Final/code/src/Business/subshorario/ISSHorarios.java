package Business.subshorario;

import java.io.File;
import java.util.Date;
import java.util.List;

import Business.subsutilizadores.Aluno;
import Business.subsutilizadores.DiretorCurso;

public interface ISSHorarios {

	public void importarUCs(List<UC> lista);

	public void importarHorarios(List<Integer> lista);

	public String configurarPreferencia(String uc, String preferencia);

	public String importarCurso(String codigoCurso, String nomeCurso, DiretorCurso diretorCurso);

	public void importarTurnos(List<Turno> turnos);

	public String listarTurnos();

	public void gerarHorario();

	public boolean validarHorario(List<Aluno> aAlunos, List<Turno> aTurnos);

	public void publicarHorarios();

	public Horario consultarHorario(Aluno aAluno);

	public int hashCode();

	public boolean equals(Object aObject);
}