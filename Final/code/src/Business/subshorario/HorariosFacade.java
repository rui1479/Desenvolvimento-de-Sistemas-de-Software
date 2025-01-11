package Business.subshorario;

import Business.subsutilizadores.Aluno;
import Business.subsutilizadores.DiretorCurso;
import data.business.*;
import data.business.utilizador.AlunoDAO;
import java.io.File;
import java.util.*;

public class HorariosFacade implements ISSHorarios {

    private UCDAO ucDAO;
    private HorarioDAO horarioDAO;
    private TurnoDAO turnoDAO;
    private SalaDAO salaDAO;
    private CursoDAO cursoDAO;
    private AlunoDAO alunoDAO;
    private AlunoUCDAO alunoUCDAO;
    int alocados = 0;

    public HorariosFacade() {
        this.ucDAO = UCDAO.getInstance();
        this.horarioDAO = HorarioDAO.getInstance();
        this.turnoDAO = TurnoDAO.getInstance();
        this.salaDAO = SalaDAO.getInstance();
        this.cursoDAO = CursoDAO.getInstance();
        this.alunoDAO = AlunoDAO.getInstance();
    }

    // Método de importação de alunos
    @Override
    public void importarUCs(List<UC> lista) {
        List<String> resultados = new ArrayList<>();

        for (UC uc : lista) {
            if (this.ucDAO.containsKey(uc.getId())) {
                resultados.add("UC já existente: " + uc.getId() + " - " + uc.getNome());
            } else {
                this.ucDAO.put(uc.getId(), uc);
                resultados.add("UC importada com sucesso: " + uc.getId() + " - " + uc.getNome());
            }
        }

        System.out.println("Importação de ucs concluída com sucesso.");
    }

    @Override
    public void importarHorarios(List<Integer> lista) {
        List<String> resultados = new ArrayList<>();

        for (Integer hor : lista) {
            Horario horario = new Horario(hor);
            if (this.horarioDAO.containsKey(hor)) {
                resultados.add("Horario já existente: " + hor);
            } else {
                this.horarioDAO.put(hor, horario);
                resultados.add("Horario importada com sucesso: " + hor);
            }
        }

        System.out.println("Importação dos horarios concluída com sucesso.");
    }

    @Override
    public String configurarPreferencia(String idUc, String preferencia) {
        UC ucObj = this.ucDAO.get(idUc);

        if (ucObj != null) {
            ucObj.setPreferencia(preferencia);
            this.ucDAO.put(idUc, ucObj);
            return "Preferência configurada com sucesso.";
        }
        return null;
    }

    @Override
    public String importarCurso(String codigoCurso, String nomeCurso, DiretorCurso diretorCurso) {
        Curso cursoObj = this.cursoDAO.get(codigoCurso);

        if (cursoObj != null) {
            return "Curso já existente.";
        } else {
            cursoObj = new Curso(codigoCurso, nomeCurso, diretorCurso);
            this.cursoDAO.put(codigoCurso, cursoObj);
            return "Curso importado com sucesso.";
        }
    }

    @Override
    public void importarTurnos(List<Turno> turnos) {
        TurnoDAO turnoDAO = TurnoDAO.getInstance();
        List<String> resultados = new ArrayList<>();

        for (Turno turno : turnos) {
            try {
                // Se o turno já existe, atualiza; caso contrário, insere um novo.
                if (turnoDAO.containsKey(turno.getId())) {
                    turnoDAO.put(turno.getId(), turno);
                    resultados.add("Turno atualizado com sucesso: ID " + turno.getId());
                } else {
                    turnoDAO.put(turno.getId(), turno);
                    resultados.add("Turno inserido com sucesso: ID " + turno.getId());
                }
            } catch (Exception e) {
                resultados.add("Erro ao processar turno ID " + turno.getId() + ": " + e.getMessage());
            }
        }

    }

    public String aux(int tipo) {
        switch (tipo) {
            case 1:
                return "T";
            case 2:
                return "TP";
            case 3:
                return "PL";
            default:
                System.out.println("Wrong type");
                return "";  // Retorna uma string vazia se i tipo for invalido
        }
    }

    @Override
    public String listarTurnos() { // UI para decidir escolha de turno a listar
        HorarioTurnoDAO htDAO = HorarioTurnoDAO.getInstance();
        StringBuilder sb = new StringBuilder();

        // Organizar os turnos pela UC e, dentro de cada UC, pela ID do turno
        turnoDAO.values().stream()
                .sorted(Comparator.comparing((Turno turno) -> turno.getUC().getNome()) // Ordena pela UC (nome)
                        .thenComparing(Turno::getId)) // Ordena pelo ID do turno
                .forEach(turno -> {
                    sb.append("Turno ID: ").append(turno.getId()).append(", Turno: ")
                            .append(aux(turno.getTipo())).append(turno.getNum())
                            .append(", UC: ").append(turno.getUC().getNome())
                            .append(" , Nº Alunos: ").append(htDAO.getAlunos(turno.getId()).size());
                    sb.append("\n");

                });
        // Retorna a lista de turnos como uma string
        return sb.toString();
    }

	public void executaPreferencia(List <Aluno> alunos, String preferencia){
		if (preferencia == null) return;
		switch (preferencia.toLowerCase()) {
			case "media":
				// Ordena os alunos pela média (exemplo assumindo que Aluno tem um método getMedia())
				//alunos.sort(Comparator.comparingDouble(Aluno::ge).reversed()); // Ordena da maior para a menor média
				System.out.println("Alunos ordenados por média.");
				break;

			case "grupo":
				// Ordena os alunos por grupo (exemplo assumindo que Aluno tem um método getGrupo())
				//alunos.sort(Comparator.comparing(Aluno::getG));
				System.out.println("Alunos ordenados por grupo.");
				break;
			case "genero":
				// Ordena os alunos por género (exemplo assumindo que Aluno tem um método getGenero())
				alunos.sort(Comparator.comparing(Aluno::getGenero));
				System.out.println("Alunos ordenados por género.");
				break;
			default:
				// Se a preferência não for reconhecida, não altera a lista
				System.out.println("Preferência não reconhecida. Nenhuma ordenação aplicada.");
				break;
		}
	}

    public void gerarHorario() {

        List<Aluno> alunos = new ArrayList<>(alunoDAO.values());
		Set <UC> unF = new HashSet<>();
		AlunoUCDAO aucDAO = AlunoUCDAO.getInstance();

        int alunosTurnoCount = 0; // Contagem de alunos alocados por turno
        int n = 0;

        for (Aluno aluno : alunos) {
            n++;

            List<UC> ucsMatriculadas = alunoDAO.getUCsMatriculadas(aluno);

            for (UC uc : ucsMatriculadas) {

                List<Turno> turnosT = turnoDAO.getByTipo(1, uc.getId());

                List<Turno> turnosTp = turnoDAO.getByTipo(2, uc.getId());
                if (turnosTp.isEmpty()) {
                    turnosTp = turnoDAO.getByTipo(3, uc.getId());
                }

                String preferencia = uc.getPreferencia();

				if (preferencia == null) {
					newAuxAlocador(uc, turnosT, aluno, 1);
					newAuxAlocador(uc, turnosTp, aluno, 2);
				}
				else unF.add(uc);

            }
        }
		for (UC uc : unF){

			List<Aluno> inscritos = aucDAO.obterAlunosDaUC(uc.getId());

			executaPreferencia(inscritos,uc.getPreferencia());

			List<Turno> turnosT = turnoDAO.getByTipo(1, uc.getId());
			List<Turno> turnosTp = turnoDAO.getByTipo(2, uc.getId());

			if (turnosTp.isEmpty()) {
				turnosTp = turnoDAO.getByTipo(3, uc.getId());
			}
			for (Aluno aluno : inscritos){
				newAuxAlocador(uc, turnosT, aluno, 1);
				newAuxAlocador(uc, turnosTp, aluno, 2);
			}
		}

		System.out.println("ALUNOS ALOCADOS : " + n);
    }





    public void newAuxAlocador(UC uc, List<Turno> turnos, Aluno aluno, int tipo) {
        HorarioTurnoDAO htDAO = HorarioTurnoDAO.getInstance();

        for (Turno turno : turnos) {

            int nAlunos = htDAO.getAlunos(turno.getId()).size();

            int dI = turno.getInicio().getHours();
            int dia = turno.getDia();
            int dH = dI * dia; // cria a key (hora início * dia)

            int limiteTurno = turno.getLimite();
            int limiteSala = turno.getSala().getCapacidade();
            int limite;

            if (tipo == 1) {
                limite = limiteSala / 2; 
            }else if (limiteTurno > limiteSala) {
                limite = limiteSala - limiteSala / 6; 
            }else {
                limite = limiteTurno - limiteTurno / 6; //  ( limite/6 para reservar espaço para alocação manual )
            }

            if (nAlunos < limite) {

                Horario horario = aluno.getHorario();
                if (horario == null) {
                    horario = new Horario(); // Inicializa o horário, caso esteja nulo
                    aluno.setHorario(horario);
                }

                // Verifica se o aluno já está alocado no turno ou horário atual
                if (horario.getTurno(dH) != null) {
                    System.out.println("Aluno " + aluno.getCodUtilizador() + " já está alocado no horário: " + dI + " do dia " + dia);
                    continue; // Não tenta alocar novamente no mesmo horário
                }

                switch (tipo) {
                    case 1:
                        boolean inscritoT = htDAO.verificarInscricaoTurno(aluno.getCodUtilizador(), uc.getId(), 1);
                        if (inscritoT) {
                            System.out.println("Aluno " + aluno.getCodUtilizador() + " já está alocado em turno T");
                            continue;
                        }
                    case 2:
                        boolean inscritoTp = htDAO.verificarInscricaoTurno(aluno.getCodUtilizador(), uc.getId(), 2);
                        if (inscritoTp) {
                            System.out.println("Aluno " + aluno.getCodUtilizador() + " já está alocado em turno Tp");
                            continue;
                        }
                    case 3:
                        boolean inscritoPl = htDAO.verificarInscricaoTurno(aluno.getCodUtilizador(), uc.getId(), 3);
                        if (inscritoPl) {
                            System.out.println("Aluno " + aluno.getCodUtilizador() + " já está alocado em turno Pl");
                            continue;
                        }
                }

                horario.adicionarTurno(dH, turno); // Adicionar turno ao horario
                
                htDAO.adicionarHorarioTurno(horario.getId(), turno.getId()); // Adicionar turno a tabela horario-turno

                return;

            }

        }
    }

    public String listarUC(String alunoID) {
        AlunoUCDAO alunoUcDAO = AlunoUCDAO.getInstance();
        TurnoDAO turnoDAO = TurnoDAO.getInstance();
        HorarioTurnoDAO htDAO = HorarioTurnoDAO.getInstance();
        StringBuilder sb = new StringBuilder();

        // Obter a lista de UCs associadas ao aluno
        List<UC> ucs = alunoUcDAO.obterUCsDoAluno(alunoID);

        // Verifica se o aluno está inscrito em alguma UC
        if (ucs.isEmpty()) {
            return "O aluno com ID " + alunoID + " não está inscrito em nenhuma UC.";
        }

        // Construir a lista de UCs
        sb.append("UCs em que o aluno ").append(alunoID).append(" está inscrito:\n");
        for (UC uc : ucs) {
            // Verificar se o aluno está inscrito em turnos do tipo T, TP ou PL
            boolean inscritoT = htDAO.verificarInscricaoTurno(alunoID, uc.getId(), 1); // Tipo 1 - T
            boolean inscritoTP = htDAO.verificarInscricaoTurno(alunoID, uc.getId(), 2); // Tipo 2 - TP
            boolean inscritoPL = htDAO.verificarInscricaoTurno(alunoID, uc.getId(), 3); // Tipo 3 - PL

            // Adicionar informações sobre a UC
            sb.append("ID: ").append(uc.getId())
                    .append(", Nome: ").append(uc.getNome())
                    .append(", Ano: ").append(uc.getAno())
                    .append(", Opcional: ").append(uc.getOpcional() ? "Sim" : "Não")
                    .append(", T: ").append(inscritoT ? "Sim" : "Não")
                    .append(", TP: ").append(inscritoTP ? "Sim" : "Não")
                    .append(", PL: ").append(inscritoPL ? "Sim" : "Não")
                    .append("\n");
        }

        return sb.toString();
    }

    public String listarTurnosDaUC(String ucID) {
        TurnoDAO turnoDAO = TurnoDAO.getInstance();
        StringBuilder sb = new StringBuilder();

        // Obter a lista de turnos da UC
        List<Turno> turnos = turnoDAO.obterTurnosDaUC(ucID);

        // Verifica se há turnos para a UC
        if (turnos.isEmpty()) {
            return "A UC com ID " + ucID + " não possui turnos disponíveis.";
        }

        // Construir a lista de turnos
        sb.append("Turnos disponíveis para a UC ").append(ucID).append(":\n");
        for (Turno turno : turnos) {
            sb.append("ID: ").append(turno.getId())
                    .append(", Tipo: ").append(turno.getTipo() == 1 ? "T" : turno.getTipo() == 2 ? "TP" : "PL")
                    .append(", Dia: ").append(turno.getDia())
                    .append(", Início: ").append(turno.getInicio())
                    .append(", Fim: ").append(turno.getFim())
                    .append(", Limite: ").append(turno.getLimite())
                    .append(", Sala: ").append(turno.getSala().getReferencia())
                    .append("\n");
        }

        return sb.toString();
    }

    // ir a ucs matriculadas e ver turnos disponiveis e alocar o aluno
    public void gerarHorarioManualmente(String AlunoID, int turnoID) {
        AlunoDAO alunoDAO = AlunoDAO.getInstance();
        TurnoDAO turnoDAO = TurnoDAO.getInstance();
        HorarioTurnoDAO htDAO = HorarioTurnoDAO.getInstance();

        Turno turno = turnoDAO.get(turnoID);

        int dI = turno.getInicio().getHours();
        int dia = turno.getDia();
        int dH = dI * dia;

        Aluno aluno = alunoDAO.get(AlunoID);
        aluno.getHorario().adicionarTurno(dH, turno);

        Horario horario = alunoDAO.get(AlunoID).getHorario();

        htDAO.adicionarHorarioTurno(horario.getId(), turnoID);

    }

    public String listarAlunosComEstatuto(){
        AlunoDAO alunoDAO = AlunoDAO.getInstance();
        StringBuilder sb = new StringBuilder();
        List<Aluno> alunos = new ArrayList<>(alunoDAO.values());
        for (Aluno aluno : alunos) {
            if (aluno.getEstatuto()) {
                sb.append("Aluno: ").append(aluno.getNome()).append(", Número: ").append(aluno.getCodUtilizador()).append("\n");
            }
        }
        return sb.toString();
    }

    public boolean validarHorario(List<Aluno> aAlunos, List<Turno> aTurnos) {
        for (Aluno aluno : aAlunos) {
            List<UC> ucsMatriculadas = alunoDAO.getUCsMatriculadas(aluno);
            Horario horario = aluno.getHorario();
            if (horario == null) {
                System.out.println("Aluno " + aluno.getCodUtilizador() + " não tem horário.");
                return false;
            }
    
            for (UC uc : ucsMatriculadas) {
                List<Turno> turnosUC = uc.getTurnos();
                boolean temTurnoTipo1 = false;
                boolean temTurnoTipo2 = false;
                boolean temTurnoTipo3 = false;
    
                for (Turno turno : turnosUC) {
                    if (horario.getTurnos().containsValue(turno)) {
                        if (turno.getTipo() == 1) {
                            temTurnoTipo1 = true;
                        } else if (turno.getTipo() == 2) {
                            temTurnoTipo2 = true;
                        } else if (turno.getTipo() == 3) {
                            temTurnoTipo3 = true;
                        }
                    }
                }
    
                boolean precisaTurnoTipo1 = turnosUC.stream().anyMatch(t -> t.getTipo() == 1);
                boolean precisaTurnoTipo2 = turnosUC.stream().anyMatch(t -> t.getTipo() == 2);
                boolean precisaTurnoTipo3 = turnosUC.stream().anyMatch(t -> t.getTipo() == 3);
    
                if ((precisaTurnoTipo1 && !temTurnoTipo1) || (precisaTurnoTipo2 && !temTurnoTipo2) || (precisaTurnoTipo3 && !temTurnoTipo3)) {
                    System.out.println("Aluno " + aluno.getCodUtilizador() + " não tem todos os turnos necessários para a UC " + uc.getId());
                    return false;
                }
            }
        }
        return true;
    }

    public void publicarHorarios() {
        try {
            // Obter todos os horários do DAO
            Collection<Horario> horarios = horarioDAO.values();
            if (horarios.isEmpty()) {
                throw new IllegalStateException("Nenhum horário gerado para publicação.");
            }

            // Marcar todos os horários como publicados
            if (!validarHorario(new ArrayList<>(alunoDAO.values()), new ArrayList<>(turnoDAO.values()))) {
                throw new IllegalStateException("Horários inválidos. Não é possível publicar.");
            }
            for (Horario horario : horarios) {
                horario.setPublicado(true); // Define como publicado
                horarioDAO.put(horario.getId(), horario); // Atualiza no banco
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao publicar horários: " + e.getMessage());
        }
    }

    public Horario consultarHorario(Aluno aluno) {
        if (aluno == null) {
            System.out.println("Erro: O objeto aluno fornecido é nulo.");
            return null;
        }

		HorarioTurnoDAO htDAO = HorarioTurnoDAO.getInstance();

		Horario horario = aluno.getHorario();
		System.out.println("Obtendo horário da base de dados...");
		if (horario == null || !horario.isPublicado()) {
			System.out.println("Nenhum horário encontrado para o aluno: " + aluno.getNome());
			return null;
		}
		aluno.setHorario(horario);

        // Obter os turnos associados ao horário
        List<Turno> turnos = htDAO.getTurnosHorario(horario.getId());
        if (turnos.isEmpty()) {
            System.out.println("O aluno " + aluno.getNome() + " não possui turnos associados ao horário.");
            return horario;
        }

        // Ordenar os turnos por dia e hora de início
        turnos.sort(Comparator.comparingInt(Turno::getDia).thenComparing(Turno::getInicio));

        // Imprimir os detalhes do horário e turnos
        System.out.println("Horário do aluno: " + aluno.getNome());
        System.out.println("------------------------------------------------------");
        for (Turno turno : turnos) {
            String diaSemana = getDiaSemana(turno.getDia());
            System.out.printf(
                    "Turno ID: %d | Tipo: %s | UC: %s | Sala: %s | Dia: %s | Início: %s | Fim: %s%n",
                    turno.getId(),
                    turno.getTipo() == 1 ? "T" : turno.getTipo() == 2 ? "TP" : "PL",
                    turno.getUC().getNome(),
                    turno.getSala().getReferencia(),
                    diaSemana,
                    turno.getInicio(),
                    turno.getFim()
            );
        }
        System.out.println("------------------------------------------------------");

        return horario; // Retorna o objeto Horario
    }

// Método auxiliar para converter o dia em texto
    private String getDiaSemana(int dia) {
        switch (dia) {
            case 1:
                return "Segunda-feira";
            case 2:
                return "Terça-feira";
            case 3:
                return "Quarta-feira";
            case 4:
                return "Quinta-feira";
            case 5:
                return "Sexta-feira";
            default:
                return "Dia inválido";
        }
    }
}
