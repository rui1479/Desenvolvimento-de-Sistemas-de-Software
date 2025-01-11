package data.business.utilizador;

import Business.subsutilizadores.DiretorCurso;
import data.DAOconfig;

import java.sql.*;
import java.util.*;

public class DiretorDAO implements Map<String, DiretorCurso> {

	private static DiretorDAO diretorDAO = null;

	private DiretorDAO() {
		/*
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {
			String sql = "CREATE TABLE IF NOT EXISTS diretores (" +
					"Id VARCHAR(10) NOT NULL PRIMARY KEY, " +
					"Nome VARCHAR(100) NOT NULL, " +
					"Email VARCHAR(100) UNIQUE NOT NULL)";
			stm.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		 */
	}

	/**
	 * Retorna uma instância única (singleton) da classe DiretorDAO, garantindo que apenas uma instância
	 * da classe seja criada e retornada durante todo o ciclo de vida da aplicação.
	 * Se a instância ainda não foi criada, cria uma nova instância e a retorna. Caso contrário,
	 * retorna a instância existente.
	 * @return uma instância única (singleton) da classe DiretorDAO.
	 */


	public static DiretorDAO getInstance() {
		if (diretorDAO == null) {
			diretorDAO = new DiretorDAO();
		}
		return diretorDAO;
	}

	/**
	 * Retorna o diretor associado à chave especificada, se existir na base de dados.
	 * @param key a chave cujo valor associado deve ser retornado.
	 * @return o diretor associado à chave especificada, ou null se a chave não estiver presente na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a consulta à base de dados.
	 */
	@Override
	public DiretorCurso get(Object key) {
		DiretorCurso diretor = null;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement("SELECT * FROM Diretores WHERE Id = ?")) {
			pstm.setString(1, key.toString());
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next()) {
					diretor = new DiretorCurso(rs.getString("Id"),
							rs.getString("Nome"),
							rs.getString("Email"),
							rs.getString("Password").toCharArray());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return diretor;
	}

	/**
	 * Insere um novo diretor na base de dados ou atualiza um existente, associando-o à chave especificada.
	 * @param key a chave com a qual o diretor será associado na base de dados.
	 * @return o diretor associado à chave especificada, ou null se a inserção não foi realizada com sucesso.
	 * @throws IllegalArgumentException se o diretor passado como valor for inválido (nulo ou com campos essenciais nulos).
	 * @throws NullPointerException se ocorrer um erro durante a execução da inserção/atualização na base de dados.
	 */

	@Override
	public DiretorCurso put(String key, DiretorCurso diretor) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement(
					 "INSERT INTO diretores (Id, Nome, Email) VALUES (?, ?, ?) " +
							 "ON DUPLICATE KEY UPDATE Nome = VALUES(Nome), Email = VALUES(Email)")) {
			pstm.setString(1, diretor.getCodUtilizador());
			pstm.setString(2, diretor.getNome());
			pstm.setString(3, diretor.getEmail());
			pstm.setString(4, String.valueOf(diretor.getPassword()));
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return diretor;
	}

	/**
	 * Remove todos os registos do diretores da base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a limpeza da base de dados.
	 */

	@Override
	public void clear() {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {
			stm.executeUpdate("TRUNCATE diretores");
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
			 ResultSet rs = stm.executeQuery("SELECT Id FROM diretores")) {
			while (rs.next()) {
				res.add(rs.getString("Id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return res;
	}

	/**
	 * Retorna uma coleção contendo todos os diretores presentes na base de dados.
	 * @return uma coleção de diretores da base de dados.
	 * @throws RuntimeException se ocorrer um erro durante a obtenção dos diretores da base de dados.
	 */

	@Override
	public Collection<DiretorCurso> values() {
		Collection<DiretorCurso> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM diretores")) {
			while (rs.next()) {
				res.add(new DiretorCurso(rs.getString("Id"),
						rs.getString("Nome"),
						rs.getString("Email"),
						rs.getString("Password").toCharArray()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return res;
	}

	/**
	 * Retorna o número de registos (diretores) armazenados na base de dados.
	 * @return o número de registos (diretores) armazenados.
	 */

	@Override
	public int size() {
		int i = 0;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM diretores")) {
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
	 * Verifica se a base de dados de diretores está vazia.
	 * @return true se a base de dados de diretores estiver vazia, false caso contrário.
	 */


	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	/**
	 * Verifica se a chave especificada está presente na base de dados de diretores.
	 * @param key a chave a ser verificada.
	 * @return true se a chave especificada estiver presente, false caso contrário.
	 */

	@Override
	public boolean containsKey(Object key) {
		boolean r;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement srm = conn.createStatement();
			 ResultSet rs = srm.executeQuery("SELECT Id FROM diretores WHERE Id = '" + key + "'")) {
			r = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return r;
	}

	/**
	 * Verifica se um determinado valor está presente na base de dados de diretores.
	 * @param value o valor a ser verificado.
	 * @return true se o valor especificado estiver presente na base de dados, false caso contrário.
	 */

	@Override
	public boolean containsValue(Object value) {
		DiretorCurso diretor = (DiretorCurso) value;
		return this.containsKey(diretor.getCodUtilizador());
	}

	/**
	 * Insere todos os diretores de um mapa na base de dados.
	 */

	@Override
	public void putAll(Map<? extends String, ? extends DiretorCurso> map) {
		for (DiretorCurso diretor : map.values()) {
			this.put(diretor.getCodUtilizador(), diretor);
		}
	}

	/**
	 * Remove o diretor associado à chave especificada da base de dados.
	 * @param key a chave do diretor a ser removido.
	 * @return o diretor removido da base de dados, ou null se não foi encontrado.
	 * @throws NullPointerException se ocorrer um erro durante a remoção da base de dados.
	 */

	@Override
	public DiretorCurso remove(Object key) {
		DiretorCurso diretor = this.get(key);
		if (diretor != null) {
			try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
				 Statement stm = conn.createStatement()) {
				conn.setAutoCommit(false);

				stm.executeUpdate("DELETE FROM diretores WHERE Id = '" + key + "'");
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new NullPointerException(e.getMessage());
			}
		}
		return diretor;
	}

	/**
	 * Retorna um conjunto de pares que representa cada entrada na base de dados.
	 * @return um conjunto de pares que representa cada entrada na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a obtenção das entradas da base de dados.
	 */
	@Override
	public Set<Map.Entry<String, DiretorCurso>> entrySet() {
		Set<Map.Entry<String, DiretorCurso>> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM diretores")) {
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
