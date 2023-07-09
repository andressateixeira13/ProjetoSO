package servidorWeb;

import config.Config;
import log.Log;
import model.Lugar;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleSocketServer {//Classe que cria o servidor 
    private ServerSocket serverSocket;//Cria um socket para o servidor 
    private int port;//Cria uma porta para o servidor
    private boolean running;//Cria uma variavel para verificar se o servidor está rodando ou não 

    public SimpleSocketServer(int port) {//Construtor da classe SimpleSocketServer que recebe a porta como parametro
        this.port = port;//Atribui a porta recebida como parametro para a variavel port 
        this.running = false;//Atribui false para a variavel running 
    }

    public void startServer() {//Metodo que inicia o servidor
        try (ServerSocket serverSocket = new ServerSocket(port)) {//Cria um socket para o servidor passando a porta como parametro
            this.serverSocket = serverSocket;//Atribui o socket criado para a variavel serverSocket 
            this.running = true;//Atribui true para a variavel running 
            Log.logTexto("Servidor iniciado. Aguardando conexões...");//Escreve no arquivo de log que o servidor foi iniciado

            while (running) {//Enquanto running for true, o loop é executado
                Socket socket = serverSocket.accept();//cria um socket para aceitar conexões
                RequestHandler requestHandler = new RequestHandler(socket);//Cria um objeto requestHandler da classe RequestHandler passando o socket que foi criado na linha acima como parametro
                Thread thread = new Thread(requestHandler);//Cria um objeto thread da classe Thread passando o objeto requestHandler que foi criado na linha acima como parametro
                thread.start();//Inicia o requestHandler usando o metodo start da classe Thread
            }
        } catch (IOException e) {//Caso ocorra algum erro, mostra o erro
            e.printStackTrace();//Mostra o erro
        } finally {//Caso ocorra algum erro, o servidor é encerrado
            stopServer();//Chama o metodo stopServer para encerrar o servidor
        }
    }

    public void stopServer() {//Metodo que encerra o servidor
        running = false;//Atribui false para a variavel running
        if (serverSocket != null && !serverSocket.isClosed()) {//Se o socket do servidor não for nulo e não estiver fechado, o servidor é encerrado
            try {//Tenta encerrar o servidor
                serverSocket.close();//Encerra o servidor
                Log.logTexto("Servidor encerrado.");//Escreve no arquivo de log que o servidor foi encerrado
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {//Ao rodar o programa, inicializa os lugares
        inicializarLugares();//Inicializa os lugares 
        Log log = new Log();//Cria um objeto log
        SimpleSocketServer server = new SimpleSocketServer(8080);//Cria um objeto server da classe SimpleSocketServer passando a porta como parametro
        server.startServer();//Inicia o servidor

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
        if (Config.lugares.isEmpty()) {//Se a lista de lugares estiver vazia, inicializa os lugares
            for (int i = 1; i <= Config.CONST_QTD_LUGARES; i++) {//o loop é executado de acordo com a quantidade de lugares, que é definida na classe Config
                Lugar lugar = new Lugar(i);//a cada iteração, é criado um objeto lugar com o número do lugar lá na classe Lugar
                Config.lugares.add(lugar);//Adiciona o objeto lugar na lista de lugares lá na classe Config
            }
        }
    }
}
