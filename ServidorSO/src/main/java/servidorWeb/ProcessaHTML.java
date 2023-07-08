package servidorWeb;

import config.Config;
import log.Log;
import model.Atributo;
import model.Lugar;

import java.util.ArrayList;
import java.util.List;

public class ProcessaHTML {
    public static String processaIndex(String html)
    {
        StringBuilder cardLugares = new StringBuilder();
        try {
            for (Lugar lugar : Config.lugares) {
                if(lugar.getReserva() == null) {
                    cardLugares.append("<div class=\"col-md-4 mt-3\">")
                            .append("<h5 class=\"card-title\">Lugar ").append(lugar.getId()).append("</h5>")
                            .append("<h6 class=\"card-subtitle mb-2 text-muted\">Status: Livre</h6>")
                            .append("<a href=\"reserva.html?id=").append(lugar.getId()).append("\" class=\"btn btn-success\">Reservar</a>")
                            .append("</div>")
                            .append("</div>")
                            .append("</div>");
                } else {
                    cardLugares.append("<div class=\"col-md-4 mt-3\">")
                            .append("<div class=\"card text-center\">")
                            .append("<div class=\"card-body\">")
                            .append("<h5 class=\"card-title\">Lugar ").append(lugar.getId()).append("</h5>")
                            .append("<h6 class=\"card-subtitle mb-2 text-muted\">Status: Reservado</h6>")
                            .append("<p class=\"card-text\">Reservado por: ").append(lugar.getReserva()).append("</p>")
                            .append("<p class=\"card-text\">Data/Hora da Reserva: ").append(lugar.getDataReserva().replace('X',':').replace('+',' ')).append("</p>")
                            .append("</div>")
                            .append("</div>")
                            .append("</div>");
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return cardLugares.toString();
    }

    public synchronized static String ProcessaIndexForm(String resource, String ipRequest) {
        try {
            String[] arr = resource.split("[?]");
            if(arr.length > 1) {
                String[] form = arr[1].split("[&]");
                List<Atributo> atributos = new ArrayList();
                for (String input : form) {
                    String nome = input.split("=")[0];
                    String valor = input.split("=")[1];
                    Atributo atributo = new Atributo(nome, valor);
                    atributos.add(atributo);
                }
                //Processar lugar
                Atributo id = Atributo.atributoPorNome(atributos, "id");

                if(id != null) {
                    Lugar lugarReservado = Lugar.lugarPorID(Config.lugares, id.getValor());

                    if(lugarReservado != null)
                    {
                        if(lugarReservado.getReserva() == null)
                        {
                            Atributo reserva = Atributo.atributoPorNome(atributos, "reserva");
                            Atributo dataReserva = Atributo.atributoPorNome(atributos, "dataReserva");

                            String dataReservaFormatada;
                            dataReservaFormatada = dataReserva.getValor().replace('X',':').replace('+',' ');

                            lugarReservado.FazerReserva(reserva.getValor(), dataReservaFormatada, ipRequest);

                            Log.logTexto("[RESERVADO UM LUGAR] " + lugarReservado.toString());
                            System.out.println("[RESERVADO UM LUGAR] " + lugarReservado.toString());
                            return "RESERVADO UM LUGAR COM SUCESSO";
                        } else {
                            Log.logTexto("[LUGAR JÁ ESTÁ RESERVADO]  " + lugarReservado.toString());
                            System.out.println("[LUGAR JÁ ESTÁ RESERVADO] " + lugarReservado.toString());
                            return "LUGAR JÁ ESTÁ RESERVADO, TENTE OUTRO.";
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return "";
    }
}
