import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Chat {

    private Socket cliente;

    public Chat(Socket cliente){
        this.cliente = cliente;
        System.out.println("[Conectado com " + cliente.getInetAddress().getHostAddress() + "]");
    }

    public void iniciarChat(){
        try {
            Thread tecladoListener = new Thread(new TecladoHandler(cliente));
            tecladoListener.start();
            

            Scanner entradaRede = new Scanner(cliente.getInputStream());

            while(entradaRede.hasNextLine() && !cliente.isClosed()) {
                String entrada = entradaRede.nextLine();

                if(entrada.equalsIgnoreCase("!enviar")){
                    FileTransfer ft = new FileTransfer();
                    ft.receberArquivo(cliente.getInputStream(), null);
                    continue;
                }

                if(!entrada.equals("!EndConnection!"))
                    System.out.println("[" + LocalDateTime.now().getHour() +
                            ":" + LocalDateTime.now().getMinute() +
                            ":" + LocalDateTime.now().getSecond() + "]>> " + entrada);
                else
                    cliente.close();

            }

            if(cliente.isClosed())
                System.out.println("[A conexão foi perdida]");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
