package servidorWeb;

import log.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class RequestHandler implements Runnable {//Classe que cria o servidor implementando a classe Runnable, que é uma classe que permite que a classe seja executada como uma thread
    private Socket socket;//Cria um socket para o servidor 

    RequestHandler(Socket socket) {//Construtor da classe RequestHandler que recebe lá da classe SimpleSocketServer o socket como parametro
        this.socket = socket;//Atribui o socket recebido como parametro para a variavel socket
    }

    @Override//Sobrescreve o metodo run da classe Thread
    public void run() {//Metodo que é executado quando o servidor é iniciado
        try {//Tenta executar o metodo que recebe os dados do cliente e envia os dados para o cliente
            String formProcesso = "";//Cria uma variavel para armazenar o processo do formulario
            try (InputStream in = socket.getInputStream();//tenta criar um inputstream para receber os dados do cliente
                 OutputStream out = socket.getOutputStream()) {//tenta criar um outputstream para enviar os dados para o cliente

                byte[] buffer = new byte[20000];//Cria um buffer para armazenar os dados recebidos do cliente
                int nBytes = in.read(buffer);//cria uma variavel para armazenar a quantidade de bytes recebidos do cliente, recebendo como parametro o buffer

                String request = new String(buffer, 0, nBytes);//Cria uma string para armazenar os dados recebidos do cliente, recebendo como parametro o buffer, a posição inicial e a quantidade de bytes recebidos
                String[] lines = request.split("\n");//Cria um array de string para armazenar as linhas recebidas do cliente, recebendo como parametro a string request e o separador "\n"

                for (int i = 0; i < lines.length; i++) {//Loop para percorrer o array de string lines, enquanto i for menor que o tamanho do array lines, o loop é executado
                    Log.logTexto("[LINHA " + (i + 1) + "] " + lines[i]);//Chama o metodo logTexto da classe Log para escrever no arquivo de log as linhas recebidas do cliente
                    System.out.println("[LINHA " + (i + 1) + "] " + lines[i]);//Mostra no console as linhas recebidas do cliente
                }

                String[] requestLine = lines[0].split(" ");//Cria um array de string para armazenar a primeira linha recebida do cliente, recebendo como parametro a string lines[0] e o separador " "
                String resource = requestLine[1];//Cria uma string para armazenar o recurso recebido do cliente, recebendo como parametro a string requestLine[1]

                Log.logTexto("[RECURSO] " + resource);//Chama o metodo logTexto da classe Log para escrever no arquivo de log o recurso recebido do cliente
                System.out.println("[RECURSO] " + resource);//Mostra no console o recurso recebido do cliente

                if (resource.equals("/")) {//Se o recurso recebido do cliente for igual a "/", o recurso recebe "/index.html"
                    resource = "/index.html";
                } else if (resource.contains("/reserva")) {//Se o recurso recebido do cliente conter "/reserva", o recurso recebe "/reserva.html"
                    String[] arr = resource.split("[?]");//Cria um array de string para armazenar o recurso recebido do cliente, recebendo como parametro o recurso e o separador "?"
                    if (arr.length > 1) {//Se o tamanho do array for maior que 1, significa que o array contem o separador "?"
                        String[] form = arr[1].split("&");//Cria um array de string para armazenar o formulario recebido do cliente, recebendo como parametro o array arr[1] e o separador "&"
                    }
                    resource = "/reserva.html";//O recurso recebe "/reserva.html"
                } else if (resource.contains("/index")) {//Se o recurso recebido do cliente conter "/index", o recurso recebe "/index.html"
                    formProcesso = ProcessaHTML.ProcessaIndexForm(resource, this.socket.getRemoteSocketAddress().toString());//Chama o metodo ProcessaIndexForm da classe ProcessaHTML para processar o formulario recebido do cliente
                    resource = "/index.html";//O recurso recebe "/index.html"
                }

                resource = resource.replace('/', File.separatorChar);//Substitui o separador "/" pelo separador do sistema operacional
                String header = "HTTP/1.1 200 OK\n" +
                        "Content-Type: " + getContentType(resource) + "; charset=utf-8\n\n";//Cria um cabeçalho para o servidor

                File file = new File("arquivos_html" + resource);//Cria um arquivo para armazenar o recurso recebido do cliente
                try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                    if (!file.exists()) {
                        bout.write("404 NOT FOUND\n\n".getBytes(StandardCharsets.UTF_8));
                    } else {
                        try (InputStream fileIn = new FileInputStream(file)) {
                            bout.write(header.getBytes(StandardCharsets.UTF_8));
                            byte[] fileData = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = fileIn.read(fileData)) != -1) {
                                bout.write(fileData, 0, bytesRead);
                            }
                        }
                    }
                    String response = processVariables(bout);
                    if (resource.contains("index")) {
                        response = response.replace("<%lugares%>", ProcessaHTML.processaIndex(response));
                        if (!formProcesso.isEmpty()) {
                            response = response.replace("//mensagem", "alert('" + formProcesso + "')");
                        }
                    }
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String processVariables(ByteArrayOutputStream bout) {
        return new String(bout.toByteArray(), StandardCharsets.UTF_8);
    }

    private static String getContentType(String resourceName) {
        if (resourceName.toLowerCase().endsWith(".css")) {
            return "text/css";
        } else if (resourceName.toLowerCase().endsWith(".jpg") || resourceName.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (resourceName.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (resourceName.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/html";
        }
    }
}

