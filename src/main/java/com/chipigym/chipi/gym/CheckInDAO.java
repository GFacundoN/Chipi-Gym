package com.chipigym.chipi.gym;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar los check-ins de clientes
 */
public class CheckInDAO {
    
    /**
     * Registra un nuevo check-in en la base de datos
     */
    public int registrarCheckIn(CheckIn checkIn) {
        String sql = "INSERT INTO CheckIn (idCliente, fechaHora, tipoRegistro, metodoRegistro, notas) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, checkIn.getIdCliente());
            stmt.setTimestamp(2, Timestamp.valueOf(checkIn.getFechaHora()));
            stmt.setString(3, checkIn.getTipoRegistro().name());
            stmt.setString(4, checkIn.getMetodoRegistro().name());
            stmt.setString(5, checkIn.getNotas());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Obtiene todos los check-ins del día actual
     */
    public List<CheckIn> obtenerCheckInsHoy() {
        String sql = "SELECT * FROM vista_checkins " +
                     "WHERE DATE(fechaHora) = CURDATE() " +
                     "ORDER BY fechaHora DESC";
        
        return ejecutarConsulta(sql);
    }
    
    /**
     * Obtiene los últimos N check-ins
     */
    public List<CheckIn> obtenerUltimosCheckIns(int limite) {
        String sql = "SELECT * FROM vista_checkins ORDER BY fechaHora DESC LIMIT ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            return ejecutarConsulta(stmt);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    /**
     * Obtiene todos los check-ins de un cliente específico
     */
    public List<CheckIn> obtenerCheckInsPorCliente(int idCliente) {
        String sql = "SELECT * FROM vista_checkins WHERE idCliente = ? ORDER BY fechaHora DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            return ejecutarConsulta(stmt);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    /**
     * Obtiene el último check-in de un cliente
     */
    public CheckIn obtenerUltimoCheckIn(int idCliente) {
        String sql = "SELECT * FROM vista_checkins WHERE idCliente = ? " +
                     "ORDER BY fechaHora DESC LIMIT 1";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            List<CheckIn> resultados = ejecutarConsulta(stmt);
            
            if (!resultados.isEmpty()) {
                return resultados.get(0);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Verifica si un cliente ya hizo check-in hoy
     */
    public boolean yaHizoCheckInHoy(int idCliente) {
        String sql = "SELECT COUNT(*) FROM CheckIn " +
                     "WHERE idCliente = ? AND DATE(fechaHora) = CURDATE()";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Obtiene estadísticas de asistencia de un cliente
     */
    public int contarCheckIns(int idCliente, int ultimosDias) {
        String sql = "SELECT COUNT(*) FROM CheckIn " +
                     "WHERE idCliente = ? AND fechaHora >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            stmt.setInt(2, ultimosDias);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Método auxiliar para ejecutar consultas SELECT
     */
    private List<CheckIn> ejecutarConsulta(String sql) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            return ejecutarConsulta(stmt);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    /**
     * Método auxiliar para ejecutar consultas con PreparedStatement
     */
    private List<CheckIn> ejecutarConsulta(PreparedStatement stmt) throws SQLException {
        List<CheckIn> lista = new ArrayList<>();
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CheckIn checkIn = new CheckIn(
                    rs.getInt("idCheckIn"),
                    rs.getInt("idCliente"),
                    rs.getTimestamp("fechaHora").toLocalDateTime(),
                    CheckIn.TipoRegistro.valueOf(rs.getString("tipoRegistro")),
                    CheckIn.MetodoRegistro.valueOf(rs.getString("metodoRegistro")),
                    rs.getString("notas")
                );
                
                // Datos adicionales de la vista
                checkIn.setNombreCliente(rs.getString("nombreCompleto"));
                checkIn.setDniCliente(rs.getString("dni"));
                checkIn.setEstadoMembresia(rs.getString("estadoMembresia"));
                
                // Manejar null para diasMembresiaRestantes
                // MySQL devuelve Long para DATEDIFF, convertir a Integer
                Object diasObj = rs.getObject("diasMembresiaRestantes");
                Integer diasRestantes = null;
                if (diasObj != null) {
                    diasRestantes = ((Number) diasObj).intValue();
                }
                checkIn.setDiasMembresiaRestantes(diasRestantes);
                
                lista.add(checkIn);
            }
        }
        
        return lista;
    }
}
