package data.business;

import Business.subshorario.Horario;
import Business.subshorario.UC;
import Business.subshorario.Sala;
import Business.subshorario.Turno;
import data.DAOconfig;

import java.sql.*;
import java.util.*;

public class TurnoDAO implements Map<Integer, Turno> {

	private static TurnoDAO turnoDAO = null;

	private TurnoDAO() {

	}

	/**
	 * Retorna uma instância única (singleton) da classe TurnoDAO, garantindo que apenas uma instância
	 * da classe seja criada e retornada durante todo o ciclo de vida da aplicação.
	 * Se a instância ainda não foi criada, cria uma nova instância e a retorna. Caso contrário,
	 * retorna a instância existente.
	 * @return uma instância única (singleton) da classe TurnoDAO.
	 */


	public static TurnoDAO getInstance() {
		if (turnoDAO == null) {
			turnoDAO = new TurnoDAO();
		}
		return turnoDAO;
	}

	/**
	 * Retorna o turno associado à chave especificada, se existir na base de dados.
	 * @param key a chave cujo valor associado deve ser retornado.
	 * @return o turno associado à chave especificada, ou null se a chave não estiver presente na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a consulta à base de dados.
	 */
	@Override
	public Turno get(Object key) {
		Turno turno = null;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement("SELECT * FROM turno WHERE id = ?")) {
			pstm.setInt(1, (Integer) key);
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next()) {

					String salaId = rs.getString("salaReferencia_fk");
					Sala sala = null;

					if (salaId != null) {
						sala = new SalaDAO().get(salaId);
					}

					String ucId = rs.getString("idUC_fk");
					UC uc = null;

					if (ucId != null) {
						uc = new UCDAO().get(ucId);
					}


					turno = new Turno(rs.getInt("id"),
							rs.getTime("inicio"),
							rs.getTime("fim"),
							rs.getInt("num"),
							rs.getInt("dia"),
							rs.getInt("tipo"),
							rs.getInt("limite"),
							uc,
							sala);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return turno;
	}




	/**
	 * Insere um novo turno na base de dados ou atualiza um existente, associando-o à chave especificada.
	 * @param key a chave com a qual o turno será associado na base de dados.
	 * @return o turno associado à chave especificada, ou null se a inserção não foi realizada com sucesso.
	 * @throws IllegalArgumentException se o turno passado como valor for inválido (nulo ou com campos essenciais nulos).
	 * @throws NullPointerException se ocorrer um erro durante a execução da inserção/atualização na base de dados.
	 */

	@Override
	public Turno put(Integer key, Turno turno) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement(
					 "INSERT INTO turno (id, inicio, fim, num, dia, tipo, limite, idUC_fk, salaReferencia_fk) " +
							 "VALUES (?, ?, ?,?,?,?,?,?,?) " +
							 "ON DUPLICATE KEY UPDATE inicio = VALUES(inicio), fim = VALUES(fim), dia = VALUES(dia), num = VALUES(num),tipo = VALUES(tipo),limite = VALUES(limite), idUC_fk =  VALUES(idUC_fk), salaReferencia_fk = VALUES(salaReferencia_fk)")) {

			pstm.setInt(1, turno.getId());
			pstm.setTime(2, turno.getInicio());
			pstm.setTime(3, turno.getFim());
			pstm.setInt(4, turno.getNum());
			pstm.setInt(5, turno.getDia());
			pstm.setInt(6, turno.getTipo());
			pstm.setInt(7, turno.getLimite());

			pstm.setString(8, turno.getUC().getId());
			pstm.setInt(9, turno.getSala().getReferencia());



			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return turno;
	}

	/**
	 * Remove todos os registos do turno da base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a limpeza da base de dados.
	 */

	@Override
	public void clear() {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {
			stm.executeUpdate("DELETE FROM turno");
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
			 ResultSet rs = stm.executeQuery("SELECT id FROM turno")) {
			while (rs.next()) {
				res.add(rs.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return res;
	}


	/**
	 * Retorna uma coleção contendo todos os turnos presentes na base de dados.
	 * @return uma coleção de turno da base de dados.
	 * @throws RuntimeException se ocorrer um erro durante a obtenção dos turnos da base de dados.
	 */

	@Override

	public Collection<Turno> values(){
		Collection<Turno> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM turno")) {
			while (rs.next()) {

				String salaId = rs.getString("salaReferencia_fk");
				Sala sala = null;

				if (salaId != null) {
					sala = new SalaDAO().get(salaId);
				}

				String ucId = rs.getString("idUC_fk");
				UC uc = null;

				if (ucId != null) {
					uc = new UCDAO().get(ucId);
				}

				Turno turno = new Turno(rs.getInt("id"),
						rs.getTime("inicio"),
						rs.getTime("inicio"),
						rs.getInt("num"),
						rs.getInt("dia"),
						rs.getInt("tipo"),
						rs.getInt("limite"),
						uc,
						sala);

				res.add(turno);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Retorna o número de registos (turnos) armazenados na base de dados.
	 * @return o número de registos (turnos) armazenados.
	 */

	@Override
	public int size() {
		int i = 0;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM turno")) {
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
	 * Verifica se a base de dados de turnos está vazia.
	 * @return true se a base de dados de turnos estiver vazia, false caso contrário.
	 */


	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	/**
	 * Verifica se a chave especificada está presente na base de dados de turnos.
	 * @param key a chave a ser verificada.
	 * @return true se a chave especificada estiver presente, false caso contrário.
	 */

	@Override
	public boolean containsKey(Object key) {
		boolean r;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement srm = conn.createStatement();
			 ResultSet rs = srm.executeQuery("SELECT Id FROM turno WHERE Id = '" + key + "'")) {
			r = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
		return r;
	}

	/**
	 * Verifica se um determinado valor está presente na base de dados de turnos.
	 * @param value o valor a ser verificado.
	 * @return true se o valor especificado estiver presente na base de dados, false caso contrário.
	 */

	@Override
	public boolean containsValue(Object value) {
		Turno turno = (Turno) value;
		return this.containsKey(turno.getId());
		}

	/**
	 * Insere todos os turnos de um mapa na base de dados.
	 */

	@Override
	public void putAll(Map<? extends Integer, ? extends Turno> map) {
		for (Turno turno : map.values()) {
			this.put(turno.getId(), turno);
		}
	}

	/**
	 * Remove o turno associado à chave especificada da base de dados.
	 * @param key a chave do turno a ser removido.
	 * @return o turno removido da base de dados, ou null se não foi encontrado.
	 * @throws NullPointerException se ocorrer um erro durante a remoção da base de dados.
	 */

	@Override
	public Turno remove(Object key) {
		Turno turno = this.get(key);
		if (turno != null) {
			try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
				 PreparedStatement pstm = conn.prepareStatement("DELETE FROM turno WHERE id = ?")) {
				pstm.setInt(1, (Integer) key);
				pstm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return turno;
	}



	/**
	 * Retorna um conjunto de pares que representa cada entrada na base de dados.
	 * @return um conjunto de pares que representa cada entrada na base de dados.
	 * @throws NullPointerException se ocorrer um erro durante a obtenção das entradas da base de dados.
	 */
	@Override
	public Set<Map.Entry<Integer, Turno>> entrySet() {
		Set<Map.Entry<Integer, Turno>> res = new HashSet<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM turno")) {
			while (rs.next()) {
				int key = rs.getInt("id");
				String salaId = rs.getString("salaReferencia_fk");
				Sala sala = null;

				if (salaId != null) {
					sala = new SalaDAO().get(salaId);
				}

				String ucId = rs.getString("idUC_fk");
				UC uc = null;

				if (ucId != null) {
					uc = new UCDAO().get(ucId);
				}

				Turno turno = new Turno(key,
						rs.getTime("inicio"),
						rs.getTime("inicio"),
						rs.getInt("num"),
						rs.getInt("dia"),
						rs.getInt("tipo"),
						rs.getInt("limite"),
						uc,
						sala);
				res.add(new AbstractMap.SimpleEntry<>(key, turno));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}



	// Função que retorna os turnos de uma UC filtrados por tipo
	public List<Turno> getTurnosPorTipoEUC(UC uc, int tipo) {
		List<Turno> turnosFiltrados = new ArrayList<>();

		List<Turno> turnos = turnoDAO.getTurnosPorUC(uc);

		for (Turno turno : turnos) {
			if (turno.getTipo() == tipo) {
				turnosFiltrados.add(turno);
			}
		}

		return turnosFiltrados;
	}

	public List<Turno> getTurnosPorUC(UC uc) {
		List<Turno> turnosUC = new ArrayList<>();

		for (Turno turno : turnoDAO.values()) {
			if (turno.getUC().equals(uc)) {
				turnosUC.add(turno);
			}
		}

		return turnosUC;
	}



	public List <Turno> getByTipo(Object tipo, String ucID) {
		List<Turno> res = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement("SELECT * FROM turno WHERE tipo = ? AND idUC_fk = ?")) {
			pstm.setInt(1, (Integer) tipo);
			pstm.setString(2, ucID);


			try (ResultSet rs = pstm.executeQuery()) {
				while (rs.next()) {

					String salaId = rs.getString("salaReferencia_fk");
					Sala sala = null;

					if (salaId != null) {
						sala = new SalaDAO().get(salaId);
					}

					String ucId = rs.getString("idUC_fk");
					UC uc = null;

					if (ucId != null) {
						uc = new UCDAO().get(ucId);
					}


					Turno turno = new Turno(rs.getInt("id"),
							rs.getTime("inicio"),
							rs.getTime("inicio"),
							rs.getInt("num"),
							rs.getInt("dia"),
							rs.getInt("tipo"),
							rs.getInt("limite"),
							uc,
							sala);

					res.add(turno);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public List<Turno> obterTurnosDaUC(String ucID) {
		List<Turno> turnos = new ArrayList<>();
		String sql = "SELECT id, inicio, fim, num, dia, tipo, limite, salaReferencia_fk, idUC_fk " +
				"FROM Turno WHERE idUC_fk = ?";

		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement pstm = conn.prepareStatement(sql)) {

			pstm.setString(1, ucID);

			try (ResultSet rs = pstm.executeQuery()) {
				while (rs.next()) {
					String salaId = rs.getString("salaReferencia_fk");
					Sala sala = null;

					if (salaId != null) {
						sala = new SalaDAO().get(salaId); // Recupera a sala associada, se houver
					}

					String ucId = rs.getString("idUC_fk"); // Certifique-se de que está presente no SELECT
					UC uc = null;

					if (ucId != null) {
						uc = new UCDAO().get(ucId); // Recupera a UC associada, se houver
					}

					Turno turno = new Turno(
							rs.getInt("id"),
							rs.getTime("inicio"),
							rs.getTime("fim"),
							rs.getInt("num"),
							rs.getInt("dia"),
							rs.getInt("tipo"),
							rs.getInt("limite"),
							uc,
							sala
					);
					turnos.add(turno);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return turnos;
	}




}
