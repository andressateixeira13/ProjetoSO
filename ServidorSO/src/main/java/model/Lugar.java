package model;

import java.util.Collection;
import java.util.Date;

public class Lugar {
    private int id;
    private String reserva;
    private String dataReserva;

    private String ipReserva;

    public Lugar(int id) {
        this.id = id;
    }

    public static Lugar lugarPorID(Collection<Lugar> lugarCollection, String idProcurado) {
        return lugarCollection.stream().filter(lug -> idProcurado.equals(Integer.toString(lug.getId()))).findFirst().orElse(null);
    }

    public void FazerReserva(String reservaNome, String dataReserva, String ipRequest) {
        this.setReserva(reservaNome);
        this.setDataReserva(dataReserva);
        this.setIpReserva(ipRequest);
    }

    @Override
    public String toString() {
        return "Lugar{" + "id=" + id + ", reserva='" + reserva + '\'' +
                ", dataReserva='" + dataReserva + '\'' +
                ", ipReserva='" + ipReserva + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getReserva() {
        return reserva;
    }

    public void setReserva(String reserva) {
        this.reserva = reserva;
    }

    public String getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(String dataReserva) {
        this.dataReserva = dataReserva;
    }

    public String getIpReserva() {
        return ipReserva;
    }

    public void setIpReserva(String ipReserva) {
        this.ipReserva = ipReserva;
    }
}