package com.chipigym.chipi.gym;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * clase que representa una membresía del gimnasio
 * maneja fechas de inicio, fin y estado de la membresía
 */
public class Membresia {
    private int idMembresia;
    private int idCliente;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado; // puede ser "activa" o "vencida"
    private Date fechaRenovacion;

    private static int contador = 1;

    /**
     * constructor completo para membresía desde base de datos
     */
    public Membresia(int idMembresia, int idCliente, Date fechaInicio, Date fechaFin, String estado, Date fechaRenovacion) {
        this.idMembresia = idMembresia;
        this.idCliente = idCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.fechaRenovacion = fechaRenovacion;
    }

    /**
     * crea una nueva membresía
     * @param idCliente id del cliente
     * @param fechaInicio fecha de inicio de la membresía
     * @param dias duración en días (por defecto 30)
     */
    public Membresia(int idCliente, Date fechaInicio, int dias) {
        this.idCliente = idCliente;
        this.fechaInicio = fechaInicio;
        // calcular fecha de fin sumando los días
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicio);
        cal.add(Calendar.DAY_OF_YEAR, dias);
        this.fechaFin = cal.getTime();
        this.estado = "Activa";
        this.fechaRenovacion = null;
    }

    /**
     * calcula los días restantes de la membresía
     * @return días restantes o 0 si está vencida
     */
    public int calcularDiasRestantes() {
        Date hoy = new Date();
        if (hoy.after(fechaFin)) { 
            estado = "Vencida"; 
            return 0; 
        }
        long diff = fechaFin.getTime() - hoy.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * verifica si la membresía está activa
     * @return true si está activa y tiene días restantes
     */
    public boolean estaActiva() { 
        return "Activa".equals(estado) && calcularDiasRestantes() > 0; 
    }

    /**
     * extiende la duración de la membresía
     * @param dias cantidad de días a extender
     */
    public void extenderMembresia(int dias) {
        Calendar cal = Calendar.getInstance();
        // si está vencida, extender desde hoy
        if (new Date().after(fechaFin)) {
            cal.setTime(new Date());
        } else {
            // si no, extender desde la fecha de fin actual
            cal.setTime(fechaFin);
        }
        cal.add(Calendar.DAY_OF_YEAR, dias);
        fechaFin = cal.getTime();
        estado = "Activa";
        fechaRenovacion = new Date();
    }

    // getters y setters
    public int getIdMembresia() { return idMembresia; }
    public void setIdMembresia(int idMembresia) { this.idMembresia = idMembresia; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public Date getFechaInicio() { return fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Date getFechaRenovacion() { return fechaRenovacion; }
    public void setFechaRenovacion(Date fechaRenovacion) { this.fechaRenovacion = fechaRenovacion; }
}
