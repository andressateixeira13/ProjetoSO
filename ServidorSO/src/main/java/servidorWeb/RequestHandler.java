package servidorWeb;

import log.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class RequestHandler extends Thread {
    private Socket socket;

    RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String formProcesso = "";
            try (InputStream in = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {

                byte[] buffer = new byte[20000];
                int nBytes = in.read(buffer);

                String request = new String(buffer, 0, nBytes);
                String[] lines = request.split("\n");

                for (int i = 0; i < lines.length; i++) {
                    Log.logTexto("[LINHA " + (i + 1) + "] " + lines[i]);
                    System.out.println("[LINHA " + (i + 1) + "] " + lines[i]);
                }

                String[] requestLine = lines[0].split(" ");
                String resource = requestLine[1];

                Log.logTexto("[RECURSO] " + resource);
                System.out.println("[RECURSO] " + resource);

                if (resource.equals("/")) {
                    resource = "/index.html";
                } else if (resource.contains("/reserva")) {
                    String[] arr = resource.split("[?]");
                    if (arr.length > 1) {
                        String[] form = arr[1].split("&");
                    }
                    resource = "/reserva.html";
                } else if (resource.contains("/index")) {
                    formProcesso = ProcessaHTML.ProcessaIndexForm(resource, this.socket.getRemoteSocketAddress().toString());
                    resource = "/index.html";
                }

                resource = resource.replace('/', File.separatorChar);
                String header = "HTTP/1.1 200 OK\n" +
                        "Content-Type: " + getContentType(resource) + "; charset=utf-8\n\n";

                File file = new File("arquivos_html" + resource);
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
