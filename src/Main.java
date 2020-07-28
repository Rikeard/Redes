import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    static int PORTA = 1557;

    public static void main(String[] args) throws Exception {
        System.out.println("Configuração inicializada na " + PORTA);

        switch(modoDeExecucao()){
            case 0:
                iniciarServidor();
                break;

            case 1:
                iniciarCliente();
                break;

            default:
                System.exit(1);
                break;
        }
    }


    public static int modoDeExecucao(){
        Scanner teclado = new Scanner(System.in);
        System.out.println("Você deseja executar como\n(S)ervidor ou (C)lient");
        String resposta = teclado.next();
        if(resposta.equalsIgnoreCase("s")){
            return 0;
        }else if(resposta.equalsIgnoreCase("c")){
            return 1;
        }else{
            return -1;
        }
    }

    public static void iniciarServidor(){
        try {
            ServerSocket server = new ServerSocket(PORTA);
            System.out.println("Servidor iniciado na porta " + PORTA);
            while (true) {
                Socket cliente = server.accept();
                new Chat(cliente).iniciarChat();
                System.out.println("\nAguardando nova conexão");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void iniciarCliente() throws IOException {
        Scanner teclado = new Scanner(System.in);
        System.out.println("Insira o endereço IP");
        String ip = teclado.next();
        Socket cliente;
        try {
            cliente = new Socket(ip, PORTA);
        }catch(Exception e){
            System.out.println("Conexão não foi possível");
            iniciarCliente();
            return;
        }

        new Chat(cliente).iniciarChat();


    }
}


