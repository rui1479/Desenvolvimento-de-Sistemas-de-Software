package Business.subshorario;

public class Sala {
	private int referencia;
	private int capacidade;
	public Turno turno;

	public Sala(int referencia, int capacidade) {
		this.referencia = referencia;
		this.capacidade = capacidade;
	}

	public Sala() {
		this.referencia = 0;
		this.capacidade = 0;
	}

	public Sala(int referencia) {
		this.referencia = referencia;
		this.capacidade = 0;
	}

	public int getReferencia() {
		return referencia;
	}

	public int getCapacidade() {
		return capacidade;
	}

	public void setReferencia(int referencia) {
		this.referencia = referencia;
	}

	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}

	public boolean verificarDisponibilidade(Horario aHorario) {
		throw new UnsupportedOperationException();
	}

	public void definirCapacidade(int aCapacidade) {
		throw new UnsupportedOperationException();
	}


}