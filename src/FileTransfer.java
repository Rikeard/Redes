import java.io.*;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Scanner;

public class FileTransfer {

    public void enviarArquivo(OutputStream saida, String pathArquivo, int tamanhoPacote){
        try {
            FileInputStream streamArquivo;
            File file;
            int MAX_BYTES = tamanhoPacote;

            try {
                file = new File(pathArquivo);
                streamArquivo = new FileInputStream(pathArquivo);
            } catch (Exception e) {
                System.out.println("Arquivo não encontrado");
                return;
            }

            new PrintStream(saida).println("!enviar");

            int quantidadeDePacotes = (int) Math.ceil(file.length()/(double) (MAX_BYTES-4));

            System.out.printf("Tamanho do arquivo %d, Tamanho de pacotes calculado %d", file.length(), quantidadeDePacotes);

            new PrintStream(saida).println(file.getName() + "?"+ MAX_BYTES + "?" + quantidadeDePacotes);

            boolean terminou = false;
            int indexPacote = 0;

            String percent = "";

            while (!terminou) {

                ByteBuffer buffer = ByteBuffer.allocate(MAX_BYTES).putInt(indexPacote);

                byte[] bufferRead = new byte[MAX_BYTES-4];
                int read = streamArquivo.readNBytes(bufferRead,0,MAX_BYTES-4);
                buffer.put(bufferRead);

                if(read < MAX_BYTES-4){
                    terminou = true;
                }else{
                    indexPacote++;
                }

                DecimalFormat df = new DecimalFormat("0.0");
                String percentAtual = df.format(((double)indexPacote/(double) quantidadeDePacotes)*100d);
                if(!percent.equals(percentAtual)){
                    percent = percentAtual;
                    System.out.println(percent + "% [" + indexPacote + "]");
                }

                saida.write(buffer.array());
                saida.flush();

            }

            System.out.println(indexPacote);
            System.out.println("Fim do envio");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void receberArquivo(InputStream entrada, String pathArquivo){
        System.out.println("Transferência iniciada");
        long tempoInicial = System.currentTimeMillis();

        String percent = "";

        try {
            FileOutputStream streamArquivo;
            Scanner sc = new Scanner(entrada);
            String[] header = sc.nextLine().split("\\?");
            String nome = header[0];
            int tamanhoPacote = Integer.parseInt(header[1]);
            int quantidadePacotes = Integer.parseInt(header[2]);

            try {
                streamArquivo = new FileOutputStream("/home/rikeard/Desktop/" + nome);
            } catch (Exception e) {
                System.out.println("Arquivo não encontrado");
                return;
            }


            boolean terminou = false;
            int indexPacote = 0;

            while (!terminou) {

                byte[] bufferRead = new byte[tamanhoPacote];
                int read = entrada.readNBytes(bufferRead, 0, tamanhoPacote);
                int indexRecebido = ByteBuffer.wrap(bufferRead, 0, 4).getInt();

                streamArquivo.write(bufferRead, 4, tamanhoPacote-4);
                indexPacote++;


                if(indexPacote >= quantidadePacotes){
                    terminou = true;
                }

                DecimalFormat df = new DecimalFormat("0");
                String percentAtual = df.format(((double)indexPacote/(double) quantidadePacotes)*100d);
                if(!percent.equals(percentAtual)){
                    percent = percentAtual;
                    System.out.println(percent + "% [" + indexRecebido + "]");

                }

            }

            double tempoSegundos = ((double) System.currentTimeMillis()- (double) tempoInicial)/1000d;
            double velocidade = ((double) tamanhoPacote*((double) indexPacote-1d))/((double) tempoSegundos);
            DecimalFormat nEnum = new DecimalFormat("#");
            nEnum.setMaximumFractionDigits(3);
            System.out.println("Arquivo recebido com sucesso! | Tamanho do arquivo (" +
                    new File("/home/rikeard/Desktop/" + nome).length() +
                    ") | Pacotes recebidos (" + (indexPacote-1) + ") | Velocidade " +  nEnum.format(((double) velocidade*8d)) + " bits/s");


        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }
}
