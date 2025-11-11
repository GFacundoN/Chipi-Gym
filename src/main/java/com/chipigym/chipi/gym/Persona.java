package com.chipigym.chipi.gym;

/**
 * clase base que representa una persona con datos básicos
 */
public class Persona {
    private String nombreCompleto;
    private String dni;
    private int edad;
    private String telefono;

    public Persona(String nombreCompleto, String dni, int edad, String telefono) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.edad = edad;
        this.telefono = telefono;
    }

    // getters y setters
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
