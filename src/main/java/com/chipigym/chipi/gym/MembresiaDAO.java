package com.chipigym.chipi.gym;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * clase de acceso a datos para membresías
 * maneja las operaciones de base de datos relacionadas con membresías
 */
public class MembresiaDAO {
    
    /**
     * agrega una nueva membresía a la base de datos
     * @param m membresía a agregar
     * @return id de la membresía creada o -1 si hay error
     */
    public int agregarMembresia(Membresia m) {
        String sql = "INSERT INTO Membresia(idCliente, fechaInicio, fechaFin, estado, fechaRenovacion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, m.getIdCliente());
            stmt.setDate(2, new java.sql.Date(m.getFechaInicio().getTime()));
            stmt.setDate(3, new java.sql.Date(m.getFechaFin().getTime()));
            stmt.setString(4, m.getEstado());
            if (m.getFechaRenovacion() != null) {
                stmt.setDate(5, new java.sql.Date(m.getFechaRenovacion().getTime()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            int affected = stmt.executeUpdate();
            if (affected == 0) return -1;
            
            // obtener el id generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    m.setIdMembresia(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * obtiene la membresía activa de un cliente
     * @param idCliente id del cliente
     * @return membresía activa o null si no tiene
     */
    public Membresia obtenerMembresiaActiva(int idCliente) {
        String sql = "SELECT idMembresia, idCliente, fechaInicio, fechaFin, estado, fechaRenovacion " +
                     "FROM Membresia WHERE idCliente = ? ORDER BY idMembresia DESC LIMIT 1";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapMembresia(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * obtiene todas las membresías de un cliente
     * @param idCliente id del cliente
     * @return lista de membresías
     */
    public List<Membresia> obtenerMembresiasCliente(int idCliente) {
        List<Membresia> lista = new ArrayList<>();
        String sql = "SELECT idMembresia, idCliente, fechaInicio, fechaFin, estado, fechaRenovacion " +
                     "FROM Membresia WHERE idCliente = ? ORDER BY fechaInicio DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapMembresia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    /**
     * renueva la membresía de un cliente
     * @param idCliente id del cliente
     * @param dias días de extensión (por defecto 30)
     * @return true si se renovó correctamente
     */
    public boolean renovarMembresia(int idCliente, int dias) {
        // marcar membresías anteriores como vencidas
        String sqlUpdate = "UPDATE Membresia SET estado = 'Vencida' WHERE idCliente = ?";
        
        // obtener la última membresía para calcular fechas
        Membresia ultima = obtenerMembresiaActiva(idCliente);
        Date fechaInicio;
        Date hoy = new Date();
        
        if (ultima != null && ultima.getFechaFin().after(hoy)) {
            // si tiene membresía activa, extender desde la fecha de fin
            fechaInicio = ultima.getFechaFin();
        } else {
            // si está vencida o no tiene, iniciar desde hoy
            fechaInicio = hoy;
        }
        
        // crear nueva membresía
        Membresia nueva = new Membresia(idCliente, fechaInicio, dias);
        nueva.setFechaRenovacion(hoy);
        
        try (Connection conn = ConexionBD.getConnection()) {
            // marcar anteriores como vencidas
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setInt(1, idCliente);
                stmt.executeUpdate();
            }
            
            // agregar nueva membresía
            int id = agregarMembresia(nueva);
            return id > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * obtiene todos los clientes con membresías vencidas o por vencer
     * @param diasAnticipacion días de anticipación para considerar "por vencer"
     * @return lista de clientes con membresías a renovar
     */
    public List<Object[]> obtenerMembresiasARenovar(int diasAnticipacion) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.idCliente, c.nombreCompleto, c.dni, c.telefono, " +
                     "m.fechaFin, DATEDIFF(m.fechaFin, CURDATE()) as diasRestantes " +
                     "FROM Cliente c " +
                     "INNER JOIN Membresia m ON c.idCliente = m.idCliente " +
                     "WHERE m.idMembresia = (SELECT MAX(m2.idMembresia) FROM Membresia m2 WHERE m2.idCliente = c.idCliente) " +
                     "AND DATEDIFF(m.fechaFin, CURDATE()) <= ? " +
                     "ORDER BY diasRestantes ASC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, diasAnticipacion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[6];
                    fila[0] = rs.getInt("idCliente");
                    fila[1] = rs.getString("nombreCompleto");
                    fila[2] = rs.getString("dni");
                    fila[3] = rs.getString("telefono");
                    fila[4] = rs.getDate("fechaFin");
                    fila[5] = rs.getInt("diasRestantes");
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    /**
     * mapea un resultset a un objeto membresía
     */
    private Membresia mapMembresia(ResultSet rs) throws SQLException {
        int id = rs.getInt("idMembresia");
        int idCliente = rs.getInt("idCliente");
        Date fechaInicio = rs.getDate("fechaInicio");
        Date fechaFin = rs.getDate("fechaFin");
        String estado = rs.getString("estado");
        Date fechaRenovacion = rs.getDate("fechaRenovacion");
        
        return new Membresia(id, idCliente, fechaInicio, fechaFin, estado, fechaRenovacion);
    }
}
