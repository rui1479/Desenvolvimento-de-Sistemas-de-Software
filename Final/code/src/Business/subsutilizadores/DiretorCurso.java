package Business.subsutilizadores;

import Business.subshorario.Curso;
import Business.subshorario.UC;

public class DiretorCurso extends Utilizador {
	public Curso curso;

	public DiretorCurso(String codUtilizador, String nome, String email, char[] password) {
		super(codUtilizador, nome, email, password);
	}

	public DiretorCurso() {
		super();
	}

	public Curso getCurso() {
		return this.curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public void configurarPreferecias(UC uc, String preferencias) {
		// Configurar preferências do diretor para a UC
	}


}