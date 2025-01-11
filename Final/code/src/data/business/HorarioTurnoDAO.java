package data.business;

import Business.subshorario.Curso;
import Business.subshorario.Sala;
import Business.subshorario.Turno;
import Business.subshorario.UC;
import Business.subsutilizadores.DiretorCurso;
import data.DAOconfig;
import data.business.utilizador.DiretorDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HorarioTurnoDAO {

    public static HorarioTurnoDAO htDAO = null;;


    public HorarioTurnoDAO(){

    }

    public static HorarioTurnoDAO getInstance() {

        if (htDAO == null) {
            htDAO = new HorarioTurnoDAO();
        }
        return htDAO;
    }



    public void adicionarHorarioTurno(int horario, int turno) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(
                     "INSERT INTO Horario_Turno (idHorario_fk, idTurno_fk) VALUES (?, ?)")) {
            pstm.setInt(1, horario);
            pstm.setInt(2, turno);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao associar Horario e Turno: " + e.getMessage());
        }
    }



    public List<String> getAlunos(Object key) {
        List <String> nomes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT alunos.nome , alunos.idHorario_fk FROM alunos JOIN horario_turno ON alunos.idHorario_fk = horario_turno.idHorario_fk WHERE horario_turno.idTurno_fk = ?;")) {
            pstm.setInt(1, (int) key);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");

                    nomes.add(nome);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomes;
    }

    public List <Turno> getTurnosHorario(Object key) {
        List <Turno> res = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT t.* FROM horario_turno ht JOIN turno t ON ht.idTurno_fk = t.id WHERE ht.idHorario_fk = ?;")) {
            pstm.setInt(1, (Integer) key);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Turno turno = null;
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

                    res.add(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    public boolean verificarInscricaoTurno(String alunoID, String ucID, int tipoTurno) {
        String sql = "SELECT COUNT(*) " +
                "FROM Turno T " +
                "JOIN Horario_Turno HT ON T.id = HT.idTurno_fk " +
                "JOIN Alunos A ON HT.idHorario_fk = A.idHorario_fk " +
                "WHERE A.id = ? AND T.idUC_fk = ? AND T.tipo = ?";
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, alunoID);
            pstm.setString(2, ucID);
            pstm.setInt(3, tipoTurno);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }








}
