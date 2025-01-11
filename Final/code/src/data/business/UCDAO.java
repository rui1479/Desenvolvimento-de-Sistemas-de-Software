package data.business;

import Business.subshorario.Curso;
import Business.subshorario.Turno;
import Business.subshorario.UC;
import Business.subsutilizadores.Aluno;
import Business.subsutilizadores.DiretorCurso;
import data.DAOconfig;

import java.sql.*;
import java.util.*;

public class UCDAO implements Map<String, UC> {

	private static UCDAO ucDAO = null;

	public UCDAO() {

	}

	/**
	 * Retorna uma instância única (singleton) da classe UCDAO, garantindo que apenas uma instância
	 * da classe seja criada e retornada durante todo o ciclo de vida da aplicação.
	 * Se a instância ainda não foi criada, cria uma nova instância e a retorna. Caso contrário,
	 * retorna a instância existente.
	 * @return uma instância única (singleton) da classe UCDAO.
	 */


	public static UCDAO getInstance() {
		if (ucDAO == null) {
			ucDAO = new UCDAO();
		}
		return ucDAO;
	}

	/**
	 * Retorna o uc associado à chave especificada, se existir na base de dados.
	 * @param key a chave cujo valor associado deve ser retornado.
	 * @return o uc associado à chave especificada, ou null se a chave não estiver presente na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a consulta à base de dados.
	 */
	@Override
	public UC get(Object key) {
		UC uc = null;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement("SELECT * FROM uc WHERE id = ?")) {
			pstm.setString(1, key.toString());
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next()) {

					String cursoId = rs.getString("idCurso_fk");
					Curso curso = null;

					if (cursoId != null) {
						curso = new CursoDAO().get(cursoId);
					}

					uc = new UC(
							rs.getString("id"),
							rs.getString("nome"),
							rs.getString("preferencia"),
							rs.getInt("ano"),
							rs.getBoolean("opcional"),
							curso
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uc;
	}


	/**
	 * Insere um novo uc na base de dados ou atualiza um existente, associando-o à chave especificada.
	 * @param key a chave com a qual o uc será associado na base de dados.
	 * @return o uc associado à chave especificada, ou null se a inserção não foi realizada com sucesso.
	 * @throws IllegalArgumentException se o uc passado como valor for inválido (nulo ou com campos essenciais nulos).
	 * @throws NullPointerException se ocorrer um erro durante a execução da inserção/atualização na base de dados.
	 */

	@Override
	public UC put(String key, UC uc) {
		if (uc.getCurso() == null) {
			throw new IllegalArgumentException("Curso não pode ser nulo");
		}

		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement(
					 "INSERT INTO uc (id, nome, preferencia, ano, opcional, idCurso_fk) " +
							 "VALUES (?, ?, ?, ?, ?, ?) " +
							 "ON DUPLICATE KEY UPDATE nome = VALUES(nome), preferencia = VALUES(preferencia), " +
							 "ano = VALUES(ano), opcional = VALUES(opcional), idCurso_fk = VALUES(idCurso_fk)")) {

			pstm.setString(1, uc.getId());
			pstm.setString(2, uc.getNome());
			pstm.setString(3, uc.getPreferencia());
			pstm.setInt(4, uc.getAno());
			pstm.setBoolean(5, uc.getOpcional());
			pstm.setObject(6, uc.getCurso().getId());

			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uc;
	}


	/**
	 * Remove todos os registos do uc da base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a limpeza da base de dados.
	 */

	@Override
	public void clear() {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {
			stm.executeUpdate("DELETE FROM uc");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retorna um conjunto contendo todas as chaves (Nome) presentes na base de dados.
	 * @return um conjunto de chaves (Nome) da base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a obtenção das chaves da base de dados.
	 */

	@Override
	public Set<String> keySet() {
		Set<String> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT id FROM uc")) {
			while (rs.next()) {
				res.add(rs.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return res;
	}

	/**
	 * Retorna uma coleção contendo todos os ucs presentes na base de dados.
	 * @return uma coleção de turno da base de dados.
	 * @throws RuntimeException se ocorrer um erro durante a obtenção dos ucs da base de dados.
	 */

	@Override
	public Collection<UC> values() {
		Collection<UC> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM uc")) {
			while (rs.next()) {
				String cursoId = rs.getString("idCurso_fk");
				Curso curso = null;

				if (cursoId != null) {
					curso = new CursoDAO().get(cursoId);
				}

				UC uc = new UC(
						rs.getString("id"),
						rs.getString("nome"),
						rs.getString("preferencia"),
						rs.getInt("ano"),
						rs.getBoolean("opcional"),
						curso
				);

				res.add(uc);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Retorna o número de registos (ucs) armazenados na base de dados.
	 * @return o número de registos (ucs) armazenados.
	 */

	@Override
	public int size() {
		int i = 0;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM uc")) {
			if (rs.next()) {
				i = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return i;
	}



	/**
	 * Verifica se a base de dados de ucs está vazia.
	 * @return true se a base de dados de ucs estiver vazia, false caso contrário.
	 */


	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	/**
	 * Verifica se a chave especificada está presente na base de dados de ucs.
	 * @param key a chave a ser verificada.
	 * @return true se a chave especificada estiver presente, false caso contrário.
	 */

	@Override
	public boolean containsKey(Object key) {
		boolean r;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement srm = conn.createStatement();
			 ResultSet rs = srm.executeQuery("SELECT Id FROM uc WHERE Id = '" + key + "'")) {
			r = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return r;
	}

	/**
	 * Verifica se um determinado valor está presente na base de dados de ucs.
	 * @param value o valor a ser verificado.
	 * @return true se o valor especificado estiver presente na base de dados, false caso contrário.
	 */

	@Override
	public boolean containsValue(Object value) {
		UC uc = (UC) value;
		return this.containsKey(uc.getId());
	}

	/**
	 * Insere todos os turnos de um mapa na base de dados.
	 */

	@Override
	public void putAll(Map<? extends String, ? extends UC> map) {
		for (UC uc : map.values()) {
			this.put(uc.getId(), uc);
		}
	}

	/**
	 * Remove o uc associado à chave especificada da base de dados.
	 * @param key a chave do uc a ser removido.
	 * @return o uc removido da base de dados, ou null se não foi encontrado.
	 * @throws NullPointerException se ocorrer um erro durante a remoção da base de dados.
	 */

	@Override
	public UC remove(Object key) {
		UC uc = this.get(key);
		if (uc != null) {
			try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
				 Statement stm = conn.createStatement()) {
				conn.setAutoCommit(false);

				stm.executeUpdate("DELETE FROM uc WHERE Id = '" + key + "'");
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new NullPointerException(e.getMessage());
			}
		}
		return uc;
	}

	/**
	 * Retorna um conjunto de pares que representa cada entrada na base de dados.
	 * @return um conjunto de pares que representa cada entrada na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a obtenção das entradas da base de dados.
	 */
	@Override
	public Set<Map.Entry<String, UC>> entrySet() {
		Set<Map.Entry<String, UC>> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM uc")) {
			while (rs.next()) {
				String key = rs.getString("Id");
				res.add(new AbstractMap.SimpleEntry<>(key, this.get(key)));

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return res;
	}


}
