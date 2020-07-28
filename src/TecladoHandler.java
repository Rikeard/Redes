import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TecladoHandler implements Runnable {

    private Socket cliente;

    public TecladoHandler(Socket cliente){
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            Scanner teclado = new Scanner(System.in);

            PrintStream saida = new PrintStream((this.cliente.getOutputStream()));
            //saida.println("[Você foi conectado com sucesso]");
            while(teclado.hasNextLine() && !cliente.isClosed()){
                String in = teclado.nextLine();
                if(in.equalsIgnoreCase("!sair")){
                    saida.println("!EndConnection!");
                    cliente.close();
                    break;
                }else if(in.equalsIgnoreCase("!enviar")){
                    System.out.println("[Insira o path do arquivo]");
                    String nome = teclado.nextLine();
                    saida.println("!enviar");
                    FileTransfer ft = new FileTransfer();
                    ft.enviarArquivo(this.cliente.getOutputStream(), nome);
                    continue;
                }
                saida.println(in);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
