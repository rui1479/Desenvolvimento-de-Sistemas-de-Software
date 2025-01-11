package Business.subsutilizadores;

import java.util.Arrays;

public class Utilizador {
	private String codUtilizador;
	private String nome;
	private String email;

	private char[] password;

	public Utilizador(String codUtilizador, String nome, String email, char[] password) {
		this.codUtilizador = codUtilizador;
		this.nome = nome;
		this.email = email;
		this.password = password;
	}

	public Utilizador() {
		this.codUtilizador = "";
		this.nome = "";
		this.email = "";
		this.password = new char[0];
	}

	public String getCodUtilizador() {
		return codUtilizador;
	}

	public String getEmail() {
		return email;
	}

	public String getNome() {
		return nome;
	}

	public char[] getPassword() {
		return password;
	}

	public void setCodUtilizador(String codUtilizador) {
		this.codUtilizador = codUtilizador;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}


}