package data.business;

import Business.subshorario.Curso;
import Business.subshorario.Turno;
import Business.subshorario.UC;
import Business.subsutilizadores.Aluno;
import Business.subsutilizadores.DiretorCurso;
import data.DAOconfig;
import data.business.utilizador.DiretorDAO;

import java.sql.*;
import java.util.*;

public class CursoDAO implements Map<String, Curso> {

    private static CursoDAO cursoDAO = null;
    private static DiretorDAO diretorDAO = null;

    public CursoDAO() {

    }

    /**
     * Retorna uma instância única (singleton) da classe CursoDAO, garantindo que apenas uma instância
     * da classe seja criada e retornada durante todo o ciclo de vida da aplicação.
     * Se a instância ainda não foi criada, cria uma nova instância e a retorna. Caso contrário,
     * retorna a instância existente.
     * @return uma instância única (singleton) da classe CursoDAO.
     */


    public static CursoDAO getInstance() {
        if (cursoDAO == null) {
            cursoDAO = new CursoDAO();
        }
        return cursoDAO;
    }

    /**
     * Retorna o curso associado à chave especificada, se existir na base de dados.
     * @param key a chave cujo valor associado deve ser retornado.
     * @return o curso associado à chave especificada, ou null se a chave não estiver presente na base de dados.
     * @throws NullPointerException se ocorrer um erro durante a consulta à base de dados.
     */
    @Override
    public Curso get(Object key) {
        Curso curso = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM curso WHERE id = ?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String diretorId = rs.getString("diretorcurso_fk");

                    DiretorCurso diretor = DiretorDAO.getInstance().get(diretorId);

                    curso = new Curso(
                            rs.getString("id"),
                            rs.getString("nome"),
                            diretor
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return curso;
    }



    /**
     * Insere um novo curso na base de dados ou atualiza um existente, associando-o à chave especificada.
     * @param key a chave com a qual o curso será associado na base de dados.
     * @return o curso associado à chave especificada, ou null se a inserção não foi realizada com sucesso.
     * @throws IllegalArgumentException se o uc passado como valor for inválido (nulo ou com campos essenciais nulos).
     * @throws NullPointerException se ocorrer um erro durante a execução da inserção/atualização na base de dados.
     */

    @Override
    public Curso put(String key, Curso curso) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(
                     "INSERT INTO curso (id, nome, diretorcurso_fk) " +
                             "VALUES (?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE nome = VALUES(nome), diretorcurso_fk = VALUES(diretorcurso_fk)")) {

            pstm.setString(1, curso.getId());
            pstm.setString(2, curso.getNome());

            pstm.setString(3, curso.getDiretor().getCodUtilizador());

            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return curso;
    }



    /**
     * Remove todos os registos do curso da base de dados.
     * @throws NullPointerException se ocorrer um erro durante a limpeza da base de dados.
     */

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM curso");
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
             ResultSet rs = stm.executeQuery("SELECT id FROM curso")) {
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
     * Retorna uma coleção contendo todos os cursos presentes na base de dados.
     * @return uma coleção de turno da base de dados.
     * @throws RuntimeException se ocorrer um erro durante a obtenção dos cursos da base de dados.
     */

    @Override
    public Collection<Curso> values() {
        Collection<Curso> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM curso")) {
            while (rs.next()) {
                Curso curso = new Curso(
                        rs.getString("id"),
                        rs.getString("nome"),
                        (DiretorCurso) rs.getObject("diretorcurso_fk")
                );

                res.add(curso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Retorna o número de registos (cursos) armazenados na base de dados.
     * @return o número de registos (cursos) armazenados.
     */

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM curso")) {
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
     * Verifica se a base de dados de cursos está vazia.
     * @return true se a base de dados de cursos estiver vazia, false caso contrário.
     */


    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Verifica se a chave especificada está presente na base de dados de cursos.
     * @param key a chave a ser verificada.
     * @return true se a chave especificada estiver presente, false caso contrário.
     */

    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement srm = conn.createStatement();
             ResultSet rs = srm.executeQuery("SELECT Id FROM curso WHERE Id = '" + key + "'")) {
            r = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se um determinado valor está presente na base de dados de cursos.
     * @param value o valor a ser verificado.
     * @return true se o valor especificado estiver presente na base de dados, false caso contrário.
     */

    @Override
    public boolean containsValue(Object value) {
        Curso curso = (Curso) value;
        return this.containsKey(curso.getId());
    }

    /**
     * Insere todos os turnos de um mapa na base de dados.
     */

    @Override
    public void putAll(Map<? extends String, ? extends Curso> map) {
        for (Curso curso : map.values()) {
            this.put(curso.getId(), curso);
        }
    }

    /**
     * Remove o curso associado à chave especificada da base de dados.
     * @param key a chave do curso a ser removido.
     * @return o curso removido da base de dados, ou null se não foi encontrado.
     * @throws NullPointerException se ocorrer um erro durante a remoção da base de dados.
     */

    @Override
    public Curso remove(Object key) {
        Curso curso = this.get(key);
        if (curso != null) {
            try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
                 Statement stm = conn.createStatement()) {
                conn.setAutoCommit(false);

                stm.executeUpdate("DELETE FROM curso WHERE Id = '" + key + "'");
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }
        return curso;
    }

    /**
     * Retorna um conjunto de pares que representa cada entrada na base de dados.
     * @return um conjunto de pares que representa cada entrada na base de dados.
     * @throws NullPointerException se ocorrer um erro durante a obtenção das entradas da base de dados.
     */
    @Override
    public Set<Map.Entry<String, Curso>> entrySet() {
        Set<Map.Entry<String, Curso>> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM curso")) {
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
