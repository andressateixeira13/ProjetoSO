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

public class Log {
    private static BlockingQueue<String> buffer = new LinkedBlockingQueue<>();
    private static File logFile;
    private static ExecutorService executorService;

    static {
        logFile = createFile();
        executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new LogWriter());
    }

    public static File createFile() {
        File logDirectory = new File("arquivos_log");
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
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

    public static void logTexto(String texto) {
        try {
            buffer.put(texto);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class LogWriter implements Runnable {
        @Override
        public void run() {
            try (FileWriter fileWriter = new FileWriter(logFile, true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                while (true) {
                    String mensagem = buffer.take();
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
