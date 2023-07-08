package config;

import model.Lugar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Config {
    public static int CONST_QTD_LUGARES = 10;
    public static List<Lugar> lugares = new ArrayList();

    public static String dataAtual(String formato) {
        DateFormat dateFormat = new SimpleDateFormat(formato);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
