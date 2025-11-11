package com.chipigym.chipi.gym;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * clase que representa un cliente del gimnasio
 * extiende de persona y agrega información específica del cliente
 */
public class Cliente extends Persona {
    private int idCliente;
    private Date fechaInscripcion;
    private boolean tieneAptoFisico;
    private String archivoApto;
    private List<Membresia> membresias = new ArrayList<>();

    public Cliente(int idCliente, String nombreCompleto, String dni, int edad, String telefono, Date fechaInscripcion) {
        super(nombreCompleto, dni, edad, telefono);
        this.idCliente = idCliente;
        this.fechaInscripcion = fechaInscripcion;
    }

    public Cliente(String nombreCompleto, String dni, int edad, String telefono, Date fechaInscripcion) {
        super(nombreCompleto, dni, edad, telefono);
        this.fechaInscripcion = fechaInscripcion;
    }

    /**
     * agrega una membresía al cliente
     */
    public void agregarMembresia(Membresia m) { membresias.add(m); }

    /**
     * calcula los días restantes de la membresía activa
     * @return días restantes o 0 si no tiene membresías
     */
    public int diasRestantes() {
        if (membresias.isEmpty()) return 0;
        return membresias.get(membresias.size() - 1).calcularDiasRestantes();
    }

    // getters y setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public Date getFechaInscripcion() { return fechaInscripcion; }
    public boolean isTieneAptoFisico() { return tieneAptoFisico; }
    public void setTieneAptoFisico(boolean v) { this.tieneAptoFisico = v; }
    public String getArchivoApto() { return archivoApto; }
    public void setArchivoApto(String ruta) { this.archivoApto = ruta; }
    public List<Membresia> getMembresias() { return membresias; }
}
