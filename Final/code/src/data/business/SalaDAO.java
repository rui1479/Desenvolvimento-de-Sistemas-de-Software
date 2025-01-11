package data.business;

import Business.subshorario.Curso;
import Business.subshorario.Sala;
import Business.subshorario.Turno;
import Business.subshorario.UC;
import Business.subsutilizadores.Aluno;
import Business.subsutilizadores.DiretorCurso;
import data.DAOconfig;

import java.sql.*;
import java.util.*;

public class SalaDAO implements Map<Integer, Sala> {

    private static SalaDAO salaDAO = null;

    SalaDAO() {

    }

    /**
     * Retorna uma instância única (singleton) da classe SalaDAO, garantindo que apenas uma instância
     * da classe seja criada e retornada durante todo o ciclo de vida da aplicação.
     * Se a instância ainda não foi criada, cria uma nova instância e a retorna. Caso contrário,
     * retorna a instância existente.
     * @return uma instância única (singleton) da classe SalaDAO.
     */


    public static SalaDAO getInstance() {
        if (salaDAO == null) {
            salaDAO = new SalaDAO();
        }
        return salaDAO;
    }

    /**
     * Retorna a sala associado à chave especificada, se existir na base de dados.
     * @param key a chave cujo valor associado deve ser retornado.
     * @return a sala associado à chave especificada, ou null se a chave não estiver presente na base de dados.
     * @throws NullPointerException se ocorrer um erro durante a consulta à base de dados.
     */
    @Override
    public Sala get(Object key) {
        Sala sala = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM sala WHERE referencia = ?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {

                    sala = new Sala(
                            rs.getInt("referencia"),
                            rs.getInt("capacidade")
                    );
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sala;
    }


    /**
     * Insere uma nova sala na base de dados ou atualiza um existente, associando-o à chave especificada.
     * @param key a chave com a qual a sala será associado na base de dados.
     * @return a sala associado à chave especificada, ou null se a inserção não foi realizada com sucesso.
     * @throws IllegalArgumentException se a sala passado como valor for inválido (nulo ou com campos essenciais nulos).
     * @throws NullPointerException se ocorrer um erro durante a execução da inserção/atualização na base de dados.
     */

    @Override
    public Sala put(Integer key, Sala sala) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(
                     "INSERT INTO sala (referencia, capacidade) " +
                             "VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE referencia = VALUES(referencia), capacidade = VALUES(capacidade)")) {

            pstm.setInt(1, sala.getReferencia());
            pstm.setInt(2, sala.getCapacidade());

            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sala;
    }


    /**
     * Remove todos os registos da sala da base de dados.
     * @throws NullPointerException se ocorrer um erro durante a limpeza da base de dados.
     */

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM sala");
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
             ResultSet rs = stm.executeQuery("SELECT referencia FROM sala")) {
            while (rs.next()) {
                res.add(rs.getInt("referencia"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    /**
     * Retorna uma coleção contendo todos as salas presentes na base de dados.
     * @return uma coleção de turno da base de dados.
     * @throws RuntimeException se ocorrer um erro durante a obtenção das salas da base de dados.
     */

    @Override
    public Collection<Sala> values() {
        Collection<Sala> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM sala")) {
            while (rs.next()) {
                Sala sala = new Sala(
                        rs.getInt("referencia"),
                        rs.getInt("capacidade")
                );

                res.add(sala);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Retorna o número de registos (salas) armazenados na base de dados.
     * @return o número de registos (salas) armazenados.
     */

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM sala")) {
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
     * Verifica se a base de dados de salas está vazia.
     * @return true se a base de dados de salas estiver vazia, false caso contrário.
     */


    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Verifica se a chave especificada está presente na base de dados de salas.
     * @param key a chave a ser verificada.
     * @return true se a chave especificada estiver presente, false caso contrário.
     */

    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement srm = conn.createStatement();
             ResultSet rs = srm.executeQuery("SELECT referencia FROM sala WHERE Id = '" + key + "'")) {
            r = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se um determinado valor está presente na base de dados de salas.
     * @param value o valor a ser verificado.
     * @return true se o valor especificado estiver presente na base de dados, false caso contrário.
     */

    @Override
    public boolean containsValue(Object value) {
        Sala sala = (Sala) value;
        return this.containsKey(sala.getReferencia());
    }

    /**
     * Insere todos os turnos de um mapa na base de dados.
     */

    @Override
    public void putAll(Map<? extends Integer, ? extends Sala> map) {
        for (Sala sala : map.values()) {
            this.put(sala.getReferencia(), sala);
        }
    }

    /**
     * Remove a sala associado à chave especificada da base de dados.
     * @param key a chave da sala a ser removido.
     * @return a sala removido da base de dados, ou null se não foi encontrado.
     * @throws NullPointerException se ocorrer um erro durante a remoção da base de dados.
     */

    @Override
    public Sala remove(Object key) {
        Sala sala = this.get(key);
        if (sala != null) {
            try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
                 Statement stm = conn.createStatement()) {
                conn.setAutoCommit(false);

                stm.executeUpdate("DELETE FROM sala WHERE Id = '" + key + "'");
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }
        return sala;
    }

    /**
     * Retorna um conjunto de pares que representa cada entrada na base de dados.
     * @return um conjunto de pares que representa cada entrada na base de dados.
     * @throws NullPointerException se ocorrer um erro durante a obtenção das entradas da base de dados.
     */
    @Override
    public Set<Map.Entry<Integer, Sala>> entrySet() {
        Set<Map.Entry<Integer, Sala>> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM sala")) {
            while (rs.next()) {
                Integer key = rs.getInt("referencia");
                res.add(new AbstractMap.SimpleEntry<>(key, this.get(key)));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

}
