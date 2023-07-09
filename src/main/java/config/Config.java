package config;

import model.Lugar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Config {
    public static int CONST_QTD_LUGARES = 10;//define a quantidade de lugares para ser usado lá na inicialização dos lugares na classe SimpleSocketServer 
    public static ArrayList<Lugar> lugares = new ArrayList<>();//cria uma lista de lugares

    public static String dataAtual(String formato) {
        DateFormat dateFormat = new SimpleDateFormat(formato);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
