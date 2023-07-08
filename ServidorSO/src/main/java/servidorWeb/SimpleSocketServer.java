package servidorWeb;

import config.Config;
import log.Log;
import model.Lugar;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleSocketServer {
    private ServerSocket serverSocket;
    private int port;
    private boolean running;

    public SimpleSocketServer(int port) {
        this.port = port;
        this.running = false;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            this.running = true;
            Logs.logTexto("Servidor iniciado. Aguardando conexões...");

            while (running) {
                Socket socket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(socket);
                requestHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                Log.logTexto("Servidor encerrado.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*public static void main(String[] args) {
        InicializaLugar();
        Log log = new Log();
        SimpleSocketServer server = new SimpleSocketServer(8080);
        server.startServer();

        // Automatically shutdown in 1 minute
        try {
            while (true) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.stopServer();
    }*/

    /*public static void InicializaLugar() {
        if (Global.lugares.isEmpty()) {
            for (int i = 1; i <= Global.CONST_QTD_LUGARES; i++) {
                Lugar lugar = new Lugar(i);
                Global.lugares.add(lugar);
            }
        }
    }*/
    public static void main(String[] args) {
        inicializarLugares();
        Log log = new Log();
        SimpleSocketServer server = new SimpleSocketServer(8080);
        server.startServer();

        // Automaticamente encerra em 1 minuto
        try {
            while (true) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.stopServer();
    }

    public static void inicializarLugares() {
        if (Config.lugares.isEmpty()) {
            for (int i = 1; i <= Config.CONST_QTD_LUGARES; i++) {
                Lugar lugar = new Lugar(i);
                Config.lugares.add(lugar);
            }
        }
    }
}
