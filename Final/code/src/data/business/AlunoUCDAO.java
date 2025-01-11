package data.business;

import Business.subshorario.Curso;
import Business.subshorario.Horario;
import Business.subshorario.UC;
import Business.subsutilizadores.Aluno;
import data.DAOconfig;
import data.business.utilizador.AlunoDAO;
import ui.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoUCDAO {
    private static AlunoUCDAO alunoUcDAO = null;

    public static AlunoUCDAO getInstance() {
        if (alunoUcDAO == null) {
            alunoUcDAO = new AlunoUCDAO();
        }
        return alunoUcDAO;
    }

    public void inserirAssociacaoAlunoUC(Aluno aluno, UC uc) {
        String verificaUCExistenteSQL = "SELECT COUNT(*) FROM uc WHERE id = ?";
        String verificaAssociacaoExistenteSQL = "SELECT COUNT(*) FROM Aluno_UC WHERE idAluno_fk = ? AND idUC_fk = ?";
        String sql = "INSERT INTO Aluno_UC (idAluno_fk, idUC_fk) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {
            // Verifica se a UC existe
            try (PreparedStatement stmt = conn.prepareStatement(verificaUCExistenteSQL)) {
                stmt.setString(1, uc.getId());
                ResultSet rs = stmt.executeQuery();
                rs.next();

                // Se a UC não existir, não faz mais nada
                if (rs.getInt(1) == 0) {
                    System.out.println("UC não existe: " + uc.getNome());
                    System.out.println("Erro: A UC não existe.");
                    return;
                }
            }

            // Verifica se a associação já existe
            try (PreparedStatement stmt2 = conn.prepareStatement(verificaAssociacaoExistenteSQL)) {
                stmt2.setString(1, aluno.getCodUtilizador());
                stmt2.setString(2, uc.getId());
                ResultSet rs2 = stmt2.executeQuery();
                rs2.next();

                // Só insere se a associação não existir
                if (rs2.getInt(1) == 0) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, aluno.getCodUtilizador());
                        pstmt.setString(2, uc.getId());
                        pstmt.executeUpdate();
                    }
                } else {
                    System.out.println("Associação já existe: " + aluno.getCodUtilizador() + " - " + uc.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Menu.errorMessage("Erro ao associar aluno à UC.");
        }
    }

    /**
     * Associa um aluno a uma UC (inscrição).
     *
     * @param idAluno o ID do aluno.
     * @param idUC    o ID da UC.
     */
    public void adicionarAlunoUC(String idAluno, String idUC) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(
                     "INSERT INTO Aluno_UC (idAluno_fk, idUC_fk) VALUES (?, ?)")) {
            pstm.setString(1, idAluno);
            pstm.setString(2, idUC);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao associar Aluno e UC: " + e.getMessage());
        }
    }

    /**
     * Remove a associação entre um aluno e uma UC.
     *
     * @param idAluno o ID do aluno.
     * @param idUC    o ID da UC.
     */
    public void removerAlunoUC(String idAluno, int idUC) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(
                     "DELETE FROM Aluno_UC WHERE idAluno_fk = ? AND idUC_fk = ?")) {
            pstm.setString(1, idAluno);
            pstm.setInt(2, idUC);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao remover associação Aluno-UC: " + e.getMessage());
        }
    }

    /**
     * Obtém as UCs associadas a um aluno.
     *
     * @param idAluno o ID do aluno.
     * @return uma lista de IDs das UCs associadas.
     */
    public List<UC> obterUCsDoAluno(String idAluno) {
        List<UC> ucs = new ArrayList<>();
        String sql = "SELECT UC.id, UC.nome, UC.preferencia, UC.ano, UC.opcional, UC.idCurso_fk " +
                "FROM Aluno_UC AU " +
                "JOIN UC ON AU.idUC_fk = UC.id " +
                "WHERE AU.idAluno_fk = ?";

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, idAluno); // Define o ID do aluno no parâmetro da query

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String preferencia = rs.getString("preferencia");
                    if (preferencia == null) {
                        preferencia = ""; // Define um valor padrão para 'preferencia' se for NULL
                    }

                    // Alterando para usar o ID do curso
                    String cursoId = rs.getString("idCurso_fk");
                    Curso curso = new CursoDAO().get(cursoId); // Buscar o curso pelo ID

                    // Cria o objeto UC com os valores recuperados
                    UC uc = new UC(
                            rs.getString("id"),       // ID da UC
                            rs.getString("nome"),     // Nome da UC
                            preferencia,              // Preferência
                            rs.getInt("ano"),         // Ano
                            rs.getBoolean("opcional"),// Se é opcional ou não
                            curso// ID do curso (alterado para String)
                    );
                    ucs.add(uc); // Adiciona a UC à lista
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime erros no log
        }
        return ucs; // Retorna a lista de UCs
    }


    /**
     * Obtém os alunos associados a uma UC.
     *
     * @param idUC o ID da UC.
     * @return uma lista de IDs de alunos associados à UC.
     */
    public List<Aluno> obterAlunosDaUC(String idUC) {
        List<Aluno> alunos = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT a.* FROM Aluno_UC auc JOIN Alunos a ON auc.idAluno_fk = a.id WHERE auc.idUC_fk = ?")) {
            pstm.setString(1, idUC);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {

                    String password = rs.getString("password");
                    char[] passwordArray = password != null ? password.toCharArray() : null;

                    // Alterando para usar o ID do curso
                    String cursoId = rs.getString("idCurso_fk");
                    Curso curso = new CursoDAO().get(cursoId); // Buscar o curso pelo ID

                    Aluno aluno = new Aluno(
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
                    alunos.add(aluno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao obter alunos da UC: " + e.getMessage());
        }
        return alunos;
    }
}



