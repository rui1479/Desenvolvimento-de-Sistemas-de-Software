package data.business;

import Business.subshorario.Horario;
import Business.subsutilizadores.DiretorCurso;
import data.DAOconfig;
import data.business.utilizador.DiretorDAO;
import org.w3c.dom.html.HTMLHeadElement;

import java.sql.*;
import java.util.*;
import java.util.Map;

import static data.business.HorarioDAO.getInstance;

public class HorarioDAO implements Map<Integer, Horario> {

	private static HorarioDAO horarioDAO = null;

	public HorarioDAO() {

	}

	/**
	 * Retorna uma instância única (singleton) da classe HorarioDAO, garantindo que apenas uma instância
	 * da classe seja criada e retornada durante todo o ciclo de vida da aplicação.
	 * Se a instância ainda não foi criada, cria uma nova instância e a retorna. Caso contrário,
	 * retorna a instância existente.
	 * @return uma instância única (singleton) da classe HorarioDAO.
	 */


	public static HorarioDAO getInstance() {
		if (horarioDAO == null) {
			horarioDAO = new HorarioDAO();
		}
		return horarioDAO;
	}

	/**
	 * Retorna o horario associado à chave especificada, se existir na base de dados.
	 * @param key a chave cujo valor associado deve ser retornado.
	 * @return o horario associado à chave especificada, ou null se a chave não estiver presente na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a consulta à base de dados.
	 */
	@Override
	public Horario get(Object key) {
		Horario horario = null;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement("SELECT * FROM Horario WHERE id = ?")) {
			pstm.setInt(1, (Integer) key);
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next()) {
					horario = new Horario(rs.getInt("id"));
					horario.setPublicado(rs.getBoolean("publicado"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return horario;
	}

	/**
	 * Insere um novo horario na base de dados ou atualiza um existente, associando-o à chave especificada.
	 * @param key a chave com a qual o horario será associado na base de dados.
	 * @return o horario associado à chave especificada, ou null se a inserção não foi realizada com sucesso.
	 * @throws IllegalArgumentException se o horario passado como valor for inválido (nulo ou com campos essenciais nulos).
	 * @throws NullPointerException se ocorrer um erro durante a execução da inserção/atualização na base de dados.
	 */

	@Override
	public Horario put(Integer key, Horario horario) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement(
					 "INSERT INTO Horario (id, publicado) VALUES (?, ?) ON DUPLICATE KEY UPDATE publicado = VALUES(publicado)")) {
			pstm.setInt(1, horario.getId());
			pstm.setBoolean(2, horario.isPublicado());
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return horario;
	}

	/**
	 * Remove todos os registos do horario da base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a limpeza da base de dados.
	 */

	@Override
	public void clear() {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {
			stm.executeUpdate("DELETE FROM Horario");
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
	public Set<Integer> keySet() {
		Set<Integer> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT id FROM Horario")) {
			while (rs.next()) {
				res.add(rs.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Retorna uma coleção contendo todos os horarios presentes na base de dados.
	 * @return uma coleção de horarios da base de dados.
	 * @throws RuntimeException se ocorrer um erro durante a obtenção dos horarios da base de dados.
	 */

	@Override
	public Collection<Horario> values() {
		Collection<Horario> res = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM Horario")) {
			while (rs.next()) {
				Horario horario = new Horario(rs.getInt("id"));
				horario.setPublicado(rs.getBoolean("publicado"));
				res.add(horario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * Retorna o número de registos (horarios) armazenados na base de dados.
	 * @return o número de registos (horarios) armazenados.
	 */

	@Override
	public int size() {
		int count = 0;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM Horario")) {
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * Verifica se a base de dados de horarios está vazia.
	 * @return true se a base de dados de horarios estiver vazia, false caso contrário.
	 */

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	/**
	 * Verifica se a chave especificada está presente na base de dados de horarios.
	 * @param key a chave a ser verificada.
	 * @return true se a chave especificada estiver presente, false caso contrário.
	 */

	@Override
	public boolean containsKey(Object key) {
		boolean exists = false;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement("SELECT id FROM Horario WHERE id = ?")) {
			pstm.setInt(1, (Integer) key);
			try (ResultSet rs = pstm.executeQuery()) {
				exists = rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}

	/**
	 * Verifica se um determinado valor está presente na base de dados de horarios.
	 * @param value o valor a ser verificado.
	 * @return true se o valor especificado estiver presente na base de dados, false caso contrário.
	 */

	@Override
	public boolean containsValue(Object value) {
		Horario horario = (Horario) value;
		return this.containsKey(horario.getId());
	}

	/**
	 * Insere todos os horarios de um mapa na base de dados.
	 */

	@Override
	public void putAll(Map<? extends Integer, ? extends Horario> map) {
		for (Horario horario : map.values()) {
			this.put(horario.getId(), horario);
		}
	}

	/**
	 * Remove o horario associado à chave especificada da base de dados.
	 * @param key a chave do horario a ser removido.
	 * @return o horario removido da base de dados, ou null se não foi encontrado.
	 * @throws NullPointerException se ocorrer um erro durante a remoção da base de dados.
	 */

	@Override
	public Horario remove(Object key) {
		Horario horario = this.get(key);
		if (horario != null) {
			try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
				 PreparedStatement pstm = conn.prepareStatement("DELETE FROM horario WHERE id = ?")) {
				pstm.setInt(1, (Integer) key);
				pstm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return horario;
	}

	/**
	 * Retorna um conjunto de pares que representa cada entrada na base de dados.
	 * @return um conjunto de pares que representa cada entrada na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a obtenção das entradas da base de dados.
	 */

	@Override
	public Set<Map.Entry<Integer, Horario>> entrySet() {
		Set<Map.Entry<Integer, Horario>> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM horario")) {
			while (rs.next()) {
				int key = rs.getInt("id");
				res.add(new AbstractMap.SimpleEntry<>(key, new Horario(key)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
}
