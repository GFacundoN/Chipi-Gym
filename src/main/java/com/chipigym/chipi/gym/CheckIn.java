package com.chipigym.chipi.gym;

import java.time.LocalDateTime;

/**
 * Clase que representa un registro de check-in del gimnasio
 */
public class CheckIn {
    private int idCheckIn;
    private int idCliente;
    private LocalDateTime fechaHora;
    private TipoRegistro tipoRegistro;
    private MetodoRegistro metodoRegistro;
    private String notas;
    
    // Información adicional del cliente (para vistas)
    private String nombreCliente;
    private String dniCliente;
    private String estadoMembresia;
    private Integer diasMembresiaRestantes;
    
    public enum TipoRegistro {
        ENTRADA, SALIDA
    }
    
    public enum MetodoRegistro {
        QR, MANUAL
    }
    
    // Constructor completo
    public CheckIn(int idCheckIn, int idCliente, LocalDateTime fechaHora, 
                   TipoRegistro tipoRegistro, MetodoRegistro metodoRegistro, String notas) {
        this.idCheckIn = idCheckIn;
        this.idCliente = idCliente;
        this.fechaHora = fechaHora;
        this.tipoRegistro = tipoRegistro;
        this.metodoRegistro = metodoRegistro;
        this.notas = notas;
    }
    
    // Constructor para nuevo check-in
    public CheckIn(int idCliente, TipoRegistro tipoRegistro, MetodoRegistro metodoRegistro) {
        this.idCliente = idCliente;
        this.fechaHora = LocalDateTime.now();
        this.tipoRegistro = tipoRegistro;
        this.metodoRegistro = metodoRegistro;
    }
    
    // Constructor simplificado
    public CheckIn(int idCliente) {
        this(idCliente, TipoRegistro.ENTRADA, MetodoRegistro.QR);
    }
    
    // Getters y Setters
    public int getIdCheckIn() { return idCheckIn; }
    public void setIdCheckIn(int idCheckIn) { this.idCheckIn = idCheckIn; }
    
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    
    public TipoRegistro getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(TipoRegistro tipoRegistro) { this.tipoRegistro = tipoRegistro; }
    
    public MetodoRegistro getMetodoRegistro() { return metodoRegistro; }
    public void setMetodoRegistro(MetodoRegistro metodoRegistro) { this.metodoRegistro = metodoRegistro; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    
    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }
    
    public String getEstadoMembresia() { return estadoMembresia; }
    public void setEstadoMembresia(String estadoMembresia) { this.estadoMembresia = estadoMembresia; }
    
    public Integer getDiasMembresiaRestantes() { return diasMembresiaRestantes; }
    public void setDiasMembresiaRestantes(Integer diasMembresiaRestantes) { 
        this.diasMembresiaRestantes = diasMembresiaRestantes; 
    }
    
    @Override
    public String toString() {
        return "CheckIn{" +
                "idCheckIn=" + idCheckIn +
                ", idCliente=" + idCliente +
                ", fechaHora=" + fechaHora +
                ", tipoRegistro=" + tipoRegistro +
                ", metodoRegistro=" + metodoRegistro +
                '}';
    }
}
