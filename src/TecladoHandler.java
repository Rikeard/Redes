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
                }else if(in.startsWith("!enviar")){
                    String[] data = in.split(" ");
                    String nome = data[1];
                    int tamanhoPacote = Integer.parseInt(data[2]);
                    FileTransfer ft = new FileTransfer();
                    ft.enviarArquivo(this.cliente.getOutputStream(), nome, tamanhoPacote);
                    continue;
                }
                saida.println(in);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
