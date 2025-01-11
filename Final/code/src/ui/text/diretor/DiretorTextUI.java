package ui.text.diretor;

import Business.subshorario.*;
import Business.subsutilizadores.Aluno;
import Business.subsutilizadores.DiretorCurso;
import Business.subsutilizadores.UtilizadoresFacade;
import data.business.AlunoUCDAO;
import data.business.HorarioTurnoDAO;
import data.business.UCDAO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import ui.Menu;

public class DiretorTextUI {

    private Menu menu;
    private String nif;
    private UtilizadoresFacade utilizadoresFacade;
    private HorariosFacade horariosFacade;
    private AlunoUCDAO alunoUCDAO;
    private final String diretor;
    private DiretorCurso diretorCurso;
    private UCDAO ucDAO;


    public DiretorTextUI(String nif, String nome, String diretorId) {
        this.nif = nif;
        this.utilizadoresFacade = new UtilizadoresFacade();
        this.horariosFacade = new HorariosFacade();
        this.alunoUCDAO = new AlunoUCDAO();
        this.diretor = nome;
        this.diretorCurso = utilizadoresFacade.getDiretorCurso(diretorId);
        this.menu = new Menu("Bem-vindo " + nome + " !");
        this.menu.addOption("0 - Retroceder", () -> {
        });
        this.menu.addOption("1 - Importar", this::importar);
        this.menu.addOption("2 - Listar alunos com estatuto", this::listarAlunoscomEstatuto);
        this.menu.addOption("3 - Gerar Horários", this::gerarHorarios);
        this.menu.addOption("4 - Gerar Horários Manualmente", this::gerarHorariosManualmente);
        this.menu.addOption("5 - Publicar Horários", this::publicarHorarios);
        this.menu.addOption("6 - Configurar Preferências", this::configurarPreferencias);
        this.menu.addOption("7 - Listar Alunos de um Turno", this::listarAlunos);
    }

    public void run() {
        this.menu.run();
    }

    // Importa de um ficheiro
    private void importar() {
        int n = 1;
        String folderPath = "../src/txt/alunos.csv";
        Path filePath = Paths.get(folderPath);

        if (!Files.exists(filePath)) {
            Menu.errorMessage("O ficheiro não foi encontrado no caminho: " + filePath);
            return;
        }

        try {
            List<String> linhas = Files.readAllLines(filePath);
            List<Aluno> alunos = new ArrayList<>();
            List<UC> ucs = new ArrayList<>();
            Map<Aluno, Set<UC>> alunoUCMap = new HashMap<>();  // Mapa para armazenar aluno -> UCs
            List <Integer> horarios = new ArrayList<>();

            Curso curso = new Curso();

            for (int i = 1; i < linhas.size(); i++) {
                String linha = linhas.get(i);
                String[] partes = linha.split(";");

                if (partes.length >= 15) {
                    String anoLetivo = partes[0];
                    String codigoEscola = partes[1];
                    String nomeEscola = partes[2];
                    String codigoCurso = partes[3];
                    String cursoNome = partes[4];
                    String edicao = partes[5];
                    String anoCurricular = partes[6];
                    String codigoUC = partes[7];
                    String unidadeCurricular = partes[8];
                    String codigoOpcao = partes[9];
                    String designacaoOpcao = partes[10];
                    String numeroMecanografico = partes[11];
                    String nome = partes[12];
                    String email = partes[13];
                    String genero = partes[14];
                    String regimeEspecial = partes.length > 15 ? partes[15] : "";

                    boolean estatuto = regimeEspecial != null && !regimeEspecial.isBlank();

                    boolean g = genero.equals("M");

                    // Criação do curso
                    curso = new Curso(codigoCurso, cursoNome, diretorCurso);
                    this.horariosFacade.importarCurso(codigoCurso, cursoNome, diretorCurso);

                    // Verifica se a UC já existe
                    UC uc = null;
                    for (UC existingUC : ucs) {
                        if (existingUC.getId().equals(codigoUC)) {
                            uc = existingUC;
                            break;
                        }
                    }
                    if (uc == null) {
                        uc = new UC(codigoUC, unidadeCurricular, Integer.parseInt(anoCurricular), Boolean.parseBoolean(codigoOpcao), curso);
                        ucs.add(uc); // Adiciona à lista se for nova
                    }

                    // Verifica se o aluno já existe
                    Aluno aluno = null;
                    for (Aluno existingAluno : alunos) {
                        if (existingAluno.getCodUtilizador().equals(numeroMecanografico)) {
                            aluno = existingAluno;
                            break;
                        }
                    }
                    if (aluno == null) {
                        aluno = new Aluno(numeroMecanografico, nome, email, null, estatuto, g, curso, n);
                        horarios.add(n);
                        n++;
                        alunos.add(aluno); // Adiciona à lista se for novo
                    }

                    // Adiciona a UC ao mapa do aluno
                    if (!alunoUCMap.containsKey(aluno)) {
                        alunoUCMap.put(aluno, new HashSet<>());
                    }
                    alunoUCMap.get(aluno).add(uc);

                    alunos.add(aluno);
                } else {
                    System.err.println("Linha inválida (número insuficiente de campos): " + linha);
                }
            }

            // Importa os alunos e as UCs para o sistema
            this.horariosFacade.importarHorarios(horarios);
            this.utilizadoresFacade.importarAlunos(alunos);
            this.horariosFacade.importarUCs(ucs);


            // Agora associamos as UCs aos alunos
            for (Map.Entry<Aluno, Set<UC>> entry : alunoUCMap.entrySet()) {
                Aluno aluno = entry.getKey();
                Set<UC> ucsAssociadas = entry.getValue();


                // Inserir as associações de UCs
                for (UC uc : ucsAssociadas) {
                    alunoUCDAO.inserirAssociacaoAlunoUC(aluno, uc); // Inserir associação após a importação
                    uc.addAluno(aluno);
                }
            }

            importarTurno();

        } catch (IOException e) {
            Menu.errorMessage("Erro ao ler o ficheiro. Verifique o caminho e tente novamente.");
        }
    }

    private void importarTurno() {
        String folderPath = "../src/txt/turnos.csv";
        Path filePath = Paths.get(folderPath);

        if (!Files.exists(filePath)) {
            Menu.errorMessage("O ficheiro não foi encontrado no caminho: " + filePath);
            return;
        }

        UCDAO ucDAO = UCDAO.getInstance();

        try {
            List<String> linhas = Files.readAllLines(filePath);
            List<Turno> turnos = new ArrayList<>();

            for (int i = 1; i < linhas.size(); i++) { // Ignora a primeira linha (cabeçalho)
                String linha = linhas.get(i);
                String[] partes = linha.split(",");

                if (partes.length == 9) {
                    int id = Integer.parseInt(partes[0]);
                    LocalTime inicio = LocalTime.parse(partes[1]);
                    LocalTime fim = LocalTime.parse(partes[2]);
                    int num = Integer.parseInt(partes[3]);
                    int dia = Integer.parseInt(partes[4]);
                    int tipo = Integer.parseInt(partes[5]);
                    int limite = Integer.parseInt(partes[6]);
                    int salaReferenciaFk = Integer.parseInt(partes[7]);
                    String idUCFk = partes[8];

                    UC uc = ucDAO.get(idUCFk);

                    if (uc != null) {
                        Turno turno = new Turno(id, inicio, fim, num, dia, tipo, limite, new Sala(salaReferenciaFk), uc);
                        uc.adicionarTurno(turno);
                        turnos.add(turno);

                    } else {
                        System.err.println("UC com ID " + idUCFk + " não encontrada.");
                    }
                } else {
                    System.err.println("Linha inválida (número insuficiente de campos): " + linha);
                }
            }

            // Importa os turnos para o sistema
            this.horariosFacade.importarTurnos(turnos);

        } catch (IOException e) {
            Menu.errorMessage("Erro ao ler o ficheiro. Verifique o caminho e tente novamente.");
        } catch (DateTimeParseException e) {
            Menu.errorMessage("Erro ao processar os horários no ficheiro. Verifique o formato (hh:mm:ss).");
        }
    }

    private void listarAlunoscomEstatuto() {
        String alunos = this.horariosFacade.listarAlunosComEstatuto();
        System.out.println(alunos);
    }

    private void gerarHorarios() {
        this.horariosFacade.gerarHorario();
    }

    private void publicarHorarios() {
        try {
            this.horariosFacade.publicarHorarios();
            Menu.successMessage("Todos os horários foram publicados com sucesso.");
        } catch (Exception e) {
            Menu.errorMessage("Erro ao publicar horários: " + e.getMessage());
        }
    }
    
    private void gerarHorariosManualmente() {
        String AlunoId = Menu.readInput("Insere código do Aluno: ");
        String lista1 = this.horariosFacade.listarUC(AlunoId);
        String UcId = Menu.readInput(lista1 + "\nInsira código da Uc: ");

        String lista2 = this.horariosFacade.listarTurnosDaUC(UcId);
        int turnoId = Integer.parseInt(Menu.readInput(lista2 + "\nInsira código do Turno: "));

        this.horariosFacade.gerarHorarioManualmente(AlunoId, turnoId);


    }

    private void listarAlunos() {

        HorarioTurnoDAO htDAO = HorarioTurnoDAO.getInstance();

        // Obter a lista de turnos formatada
        String lista = this.horariosFacade.listarTurnos();
        int turnoId = Integer.parseInt(Menu.readInput("Turnos: \n" + lista + "\nInsira código do Turno: "));

        // Criar o objeto Turno necessário para a busca
        Turno turno = new Turno();
        turno.setId(turnoId);


        // Buscar alunos associados ao turno
        List<String> alunos = htDAO.getAlunos(turnoId) ;

        // Validar se há alunos no turno
        if (alunos.isEmpty()) {
            System.out.println("Não existem alunos neste turno.");
            return;
        }

        // Construir a string para exibição
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de Alunos: \n");
        for (String aluno : alunos) {
            sb.append(aluno).append("\n");
        }

        // Exibir a lista de alunos
        System.out.println(sb.toString());
    }



    private void configurarPreferencias() {
        String idUc = Menu.readInput("Insira código da UC: ");
        String preferencia = Menu.readInput("Insira preferência: ");

        String nome = this.horariosFacade.configurarPreferencia(idUc, preferencia);

        if (nome != null) {
            Menu.successMessage("Preferência configurada com sucesso para a UC " + idUc + ".");
        } else {
            Menu.errorMessage("UC não encontrada.");
        }
    }

}
