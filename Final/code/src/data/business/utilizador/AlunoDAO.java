package data.business.utilizador;

import Business.subshorario.Curso;
import Business.subshorario.Horario;
import Business.subshorario.UC;
import Business.subsutilizadores.*;
import data.DAOconfig;
import data.business.CursoDAO;
import data.business.HorarioDAO;
import data.business.AlunoUCDAO;
import data.business.UCDAO;


import java.sql.*;

import java.util.*;

public class AlunoDAO implements Map<String, Aluno> {
    private static AlunoDAO alunoDAO = null;
    private static CursoDAO cursoDAO = null;
    private static HorarioDAO horarioDAO = null;


    private AlunoDAO() {

    }

    /**
     * Retorna uma instância única (singleton) da classe AlunoDAO, garantindo que apenas uma instância
     * da classe seja criada e retornada durante todo o ciclo de vida da aplicação.
     * Se a instância ainda não foi criada, cria uma nova instância e a retorna. Caso contrário,
     * retorna a instância existente.
     * @return uma instância única (singleton) da classe AlunoDAO.
     */

    public static AlunoDAO getInstance() {
        if (alunoDAO == null) {
            alunoDAO = new AlunoDAO();
        }
        return alunoDAO;
    }

    /**
     * Retorna o aluno associado à chave especificada, se existir na base de dados.
     * @param key a chave cujo valor associado deve ser retornado.
     * @return o aluno associado à chave especificada, ou null se a chave não estiver presente na base de dados.
     * @throws NullPointerException se ocorrer um erro durante a consulta à base de dados.
     */

    @Override
    public Aluno get(Object key) {
        Aluno aluno = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM Alunos WHERE id = ?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    char[] passwordArray = password != null ? password.toCharArray() : null;

                    // Alterando para usar o ID do curso
                    String cursoId = rs.getString("idCurso_fk");
                    Curso curso = new CursoDAO().get(cursoId); // Buscar o curso pelo ID

                    aluno = new Aluno(
                            rs.getString("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            passwordArray,
                            rs.getBoolean("estatuto"),
                            rs.getBoolean("genero"),
                            curso

                    );

                    Horario horario = new HorarioDAO().get(rs.getInt("idHorario_fk"));
                    aluno.setHorario(horario);


                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aluno;
    }


    /**
     * Insere um novo aluno na base de dados ou atualiza um existente, associando-o à chave especificada.
     * @param key a chave com a qual o aluno será associado na base de dados.
     * @return o aluno associado à chave especificada, ou null se a inserção não foi realizada com sucesso.
     * @throws IllegalArgumentException se o aluno passado como valor for inválido (nulo ou com campos essenciais nulos).
     * @throws NullPointerException se ocorrer um erro durante a execução da inserção/atualização na base de dados.
     */

    @Override
    public Aluno put(String key, Aluno aluno) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(
                     "INSERT INTO Alunos (id, nome, email, password, estatuto, genero,idHorario_fk, idCurso_fk) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE nome = VALUES(nome), email = VALUES(email), password = VALUES(password), " +
                             "estatuto = VALUES(estatuto),genero = VALUES(genero), idHorario_fk = VALUES(idHorario_fk), idCurso_fk = VALUES(idCurso_fk)")) {
            pstm.setString(1, aluno.getCodUtilizador());
            pstm.setString(2, aluno.getNome());
            pstm.setString(3, aluno.getEmail());
            if (aluno.getPassword() == null || aluno.getPassword().length == 0) {
                pstm.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pstm.setString(4, new String(aluno.getPassword()));
            }
            pstm.setBoolean(5, aluno.getEstatuto());
            pstm.setBoolean(6, aluno.getGenero());
            if (aluno.getHorario() != null) {
                pstm.setInt(7, aluno.getHorario().getId());
            } else {
                pstm.setNull(7, java.sql.Types.INTEGER);
            }
            pstm.setString(8, aluno.getCurso().getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aluno;
    }


    /**
     * Remove todos os registos do aluno da base de dados.
     * @throws NullPointerException se ocorrer um erro durante a limpeza da base de dados.
     */

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM alunos");
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
             ResultSet rs = stm.executeQuery("SELECT Id FROM alunos")) {
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
     * Retorna uma coleção contendo todos os alunos presentes na base de dados.
     * @return uma coleção de alunos da base de dados.
     * @throws RuntimeException se ocorrer um erro durante a obtenção dos alunos da base de dados.
     */

    @Override
    public Collection<Aluno> values() {
        Collection<Aluno> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Alunos")) {
            while (rs.next()) {
                Aluno aluno = new Aluno(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("password") != null ? rs.getString("password").toCharArray() : null,
                        rs.getBoolean("estatuto"),
                        rs.getBoolean("genero")
                );

                // Recuperando o curso e horário
                String cursoId = rs.getString("idCurso_fk");
                aluno.setCurso(new CursoDAO().get(cursoId));
                aluno.setHorario(new HorarioDAO().get(rs.getInt("idHorario_fk")));

                res.add(aluno);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Retorna o número de registos (alunos) armazenados na base de dados.
     * @return o número de registos (alunos) armazenados.
     */

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM alunos")) {
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
     * Verifica se a base de dados de alunos está vazia.
     * @return true se a base de dados de alunos estiver vazia, false caso contrário.
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
             ResultSet rs = srm.executeQuery("SELECT Id FROM alunos WHERE Id = '" + key + "'")) {
            r = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se um determinado valor está presente na base de dados de alunos.
     * @param value o valor a ser verificado.
     * @return true se o valor especificado estiver presente na base de dados, false caso contrário.
     */

    @Override
    public boolean containsValue(Object value) {
        Aluno aluno = (Aluno) value;
        return this.containsKey(aluno.getCodUtilizador());
    }

    /**
     * Insere todos os alunos de um mapa na base de dados.
     */

    @Override
    public void putAll(Map<? extends String, ? extends Aluno> map) {
        for (Aluno aluno : map.values()) {
            this.put(aluno.getCodUtilizador(), aluno);
        }
    }

    /**
     * Remove o aluno associado à chave especificada da base de dados.
     * @param key a chave do aluno a ser removido.
     * @return o aluno removido da base de dados, ou null se não foi encontrado.
     * @throws NullPointerException se ocorrer um erro durante a remoção da base de dados.
     */

    @Override
    public Aluno remove(Object key) {
        Aluno aluno = this.get(key);
        if (aluno != null) {
            try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
                 Statement stm = conn.createStatement()) {
                conn.setAutoCommit(false);

                stm.executeUpdate("DELETE FROM alunos WHERE Id = '" + key + "'");
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }
        }
        return aluno;
    }

    /**
     * Retorna um conjunto de pares que representa cada entrada na base de dados.
     * @return um conjunto de pares que representa cada entrada na base de dados.
     * @throws NullPointerException se ocorrer um erro durante a obtenção das entradas da base de dados.
     */
    @Override
    public Set<Map.Entry<String, Aluno>> entrySet() {
        Set<Map.Entry<String, Aluno>> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM alunos")) {
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


    public List<UC> getUCsMatriculadas(Aluno aluno) {
        List<UC> ucsMatriculadas = new ArrayList<>();
        String sql = "SELECT idUC_fk FROM Aluno_UC WHERE idAluno_fk = ?";

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, aluno.getCodUtilizador());

            // Execute the query
            try (ResultSet rs = pstm.executeQuery()) {
                // Iterate over the result set
                while (rs.next()) {
                    String idUC = rs.getString("idUC_fk");

                    // Fetch the UC object
                    UC uc = UCDAO.getInstance().get(idUC);

                    // Add to list if not null
                    if (uc != null) {
                        ucsMatriculadas.add(uc);
                    }
                }
            }
        } catch (SQLException e) {
            // Log and rethrow
            System.err.println("Error retrieving matriculated UCs for student: " + aluno.getCodUtilizador());
            e.printStackTrace();
            throw new RuntimeException("Error fetching UCs matriculated for student.", e);
        }

        return ucsMatriculadas;
    }

    public List<String> obterAlunosNaoAlocados() {
        List<String> alunos = new ArrayList<>();
        String sql = "SELECT A.id, A.nome " +
                "FROM Alunos A " +
                "LEFT JOIN Horario_Turno HT ON A.idHorario_fk = HT.idHorario_fk " +
                "WHERE HT.idHorario_fk IS NULL";

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                String alunoInfo = "ID: " + rs.getString("id") + ", Nome: " + rs.getString("nome");
                alunos.add(alunoInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alunos;
    }

}

