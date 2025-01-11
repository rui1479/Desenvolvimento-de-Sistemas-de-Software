package ui;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Menu{

    public static final String RESET = "\033[0m";
    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String WHITE_BOLD = "\033[1;37m"; 
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String YELLOW_BOLD = "\033[1;33m";

    private static final Scanner input = new Scanner(System.in);
    
    private String title;
    private List<String> options;
    private List<Handler> handlers;


    public Menu(String title){
        this.title = title;
        this.options = new ArrayList<String>();
        this.handlers = new ArrayList<Handler>();
    }


    public void addOption(String opcao, Handler handler){
        this.options.add(opcao);
        this.handlers.add(handler);
    }


    public void run(){

        show();
        int option;

        while ((option = readOption()) != 0){
            this.handlers.get(option).execute();
            show();
        }
    }


    private void show(){
        System.out.println(PURPLE_BOLD + this.title + RESET);
        System.out.println(this.options
            .stream()
            .collect(Collectors.joining("\n",YELLOW_BOLD,RESET)));
    }


    private int readOption(){

        int option = -1;
        System.out.print(YELLOW_BOLD + ">>> " + RESET);

        while (option == -1){

            try{

                option = Integer.parseInt(input.nextLine());                
                if (option < 0 || option >= this.options.size()){
                    throw new Exception();
                }
            }

            catch (NoSuchElementException e){
                option = 0;
            }

            catch (Exception e){
                System.out.println(RED_BOLD + "Opção inválida" + RESET);
                System.out.print(YELLOW_BOLD + ">>> " + RESET);
                option = -1;
            }
        }

        return option;
    }


    public static String readInput(String message){
        System.out.print(WHITE_BOLD + message + RESET);
        return input.nextLine();
    }


    public static List<String> readInputWhile(String message){
        
        String input;
        List<String> inputList = new ArrayList<String>();

        while (!(input = readInput(message)).equals("sair")){
            inputList.add(input);
        }

        return inputList;
    }


    public static void errorMessage(String message){
        System.out.println(RED_BOLD + message + RESET);
    }

    public static void normalMessage(String message){
        System.out.println(WHITE_BOLD + message + RESET);
    }

    public static void successMessage(String message){
        System.out.println(GREEN_BOLD + message + RESET);
    }

    public static void warningMessage(String message){
        System.out.println(YELLOW_BOLD + message + RESET);
    }

    public static void showList(List<?> list, String title){
        System.out.println(PURPLE_BOLD + title + RESET);
        list.forEach(System.out::println);
    }
}