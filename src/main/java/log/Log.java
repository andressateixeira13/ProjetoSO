package log;

import config.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Log {//Classe que cria o arquivo de log
    private static BlockingQueue<String> buffer = new LinkedBlockingQueue<>();//Cria um buffer para armazenar as mensagens de log 
    private static File logFile;//Cria um arquivo de log  
    private static ExecutorService executorService;//Cria um executor de serviço  

    static {
        logFile = createFile();//Cria o arquivo de log e armazena na variavel logFile que foi criada acima 
        executorService = Executors.newFixedThreadPool(2);//Cria um executor de serviço com 2 threads 
        executorService.execute(new LogWriter());//Executa o LogWriter 
    }

    public static File createFile() {
        File logDirectory = new File("arquivos_log");//Cria um diretorio para armazenar os arquivos de log
        if (!logDirectory.exists()) {//Se o diretorio não existir, cria o diretorio 
            logDirectory.mkdirs();//Cria o diretorio 
        }

        String logFileName = "log" + Config.dataAtual("yyyy-MM-dd_HH-mm") + ".txt";
        File logFile = new File(logDirectory, logFileName);

        try {
            if (logFile.createNewFile()) {
                System.out.println("File created: " + logFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return logFile;
    }

    public static void logTexto(String texto) {//Metodo que escreve no arquivo de log
        try {//Tenta escrever no arquivo de log
            buffer.put(texto);//Adiciona a mensagem de log no buffer
        } catch (InterruptedException e) {//Caso ocorra algum erro, mostra o erro
            Thread.currentThread().interrupt();//Mostra o erro 
        }
    }

    private static class LogWriter implements Runnable {//Classe que escreve no arquivo de log
        @Override//Sobrescreve o metodo run da classe Runnable
        public void run() {//Metodo que escreve no arquivo de log
            try (FileWriter fileWriter = new FileWriter(logFile, true);//Cria um objeto fileWriter da classe FileWriter passando o arquivo de log como parametro
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {//Cria um objeto bufferedWriter da classe BufferedWriter passando o fileWriter como parametro

                while (true) {//Enquanto true, o loop é executado
                    String mensagem = buffer.take();//Pega a mensagem de log do buffer
                    String logTexto = "[" + Config.dataAtual("yyyy-MM-dd HH:mm:ss") + "] " + mensagem + "\n";
                    bufferedWriter.write(logTexto);
                    bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
