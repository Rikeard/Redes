import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

            new PrintStream(saida).println(file.getName() + "?"+ MAX_BYTES + "?" + file.length() + "?" + getSHA1(pathArquivo));

            boolean terminou = false;
            int indexPacote = 0;

            String resp = "";
            DecimalFormat df = new DecimalFormat("0");
            long tamanhoEnviado = 0;

            while (!terminou) {

                ByteBuffer buffer = ByteBuffer.allocate(MAX_BYTES).putInt(indexPacote);

                byte[] bufferRead = new byte[MAX_BYTES-4];
                int read = streamArquivo.readNBytes(bufferRead,0,MAX_BYTES-4);
                buffer.put(bufferRead);

                tamanhoEnviado += read;
                if(read < MAX_BYTES-4){
                    terminou = true;
                }

                indexPacote++;


                String np = "[" + df.format((double) tamanhoEnviado/(double) file.length()*100d) + "%]";
                if(!np.equals(resp)){
                    System.out.println(np);
                    resp = np;
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
            long tamanhoArquivo = Long.parseLong(header[2]);
            String hash = header[3];

            try {
                streamArquivo = new FileOutputStream(nome);
            } catch (Exception e) {
                System.out.println("Arquivo não encontrado");
                return;
            }


            boolean terminou = false;
            int indexPacote = 0;
            long tamanhoRecebido = 0;

            String resp = "";
            DecimalFormat df = new DecimalFormat("0");

            while (!terminou) {

                byte[] bufferRead = new byte[tamanhoPacote];
                int read = entrada.readNBytes(bufferRead, 0, tamanhoPacote);
                int indexRecebido = ByteBuffer.wrap(bufferRead, 0, 4).getInt();

                long totalQueFalta = tamanhoArquivo-tamanhoRecebido;
                if(totalQueFalta >= tamanhoPacote) {
                    streamArquivo.write(bufferRead, 4, tamanhoPacote - 4);
                    tamanhoRecebido += tamanhoPacote-4;
                }else {
                    streamArquivo.write(bufferRead, 4, (int) (totalQueFalta));
                    tamanhoRecebido += totalQueFalta;
                    terminou = true;
                }

                indexPacote++;

                String np = "[" + df.format((double) tamanhoRecebido/(double) tamanhoArquivo*100d) + "%]";
                if(!np.equals(resp)){
                    System.out.println(np);
                    resp = np;
                }


            }

            String calcHash = getSHA1(nome);
            if(!hash.equals(calcHash)){
                System.out.println("Transferência falhou");
                return;
            }else{
                System.out.println("Hash verdadeira " + calcHash );
            }

            double tempoSegundos = ((double) System.currentTimeMillis()- (double) tempoInicial)/1000d;
            double velocidade = ((double) tamanhoPacote*((double) indexPacote-1d))/((double) tempoSegundos);
            DecimalFormat nEnum = new DecimalFormat("#");
            nEnum.setMaximumFractionDigits(3);
            System.out.println("Arquivo recebido com sucesso! | Tamanho do arquivo (" +
                    new File(nome).length() +
                    ") | Pacotes recebidos (" + (indexPacote-1) + ") | Velocidade " +  nEnum.format(((double) velocidade*8d)) + " bits/s");


        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }


    public String getSHA1(String arquivo) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(Files.readAllBytes(Paths.get(arquivo)));

        StringBuilder resultado = new StringBuilder();
        for(byte b : md.digest()){
            resultado.append(String.format("%02x",b));
        }
        return resultado.toString();
    }
}