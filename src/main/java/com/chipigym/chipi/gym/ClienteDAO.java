package com.chipigym.chipi.gym;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * clase de acceso a datos para clientes
 * maneja todas las operaciones de base de datos relacionadas con clientes
 */
public class ClienteDAO {
    /**
     * agrega un nuevo cliente a la base de datos
     * @param c cliente a agregar
     * @return id del cliente creado o -1 si hay error
     */
    public int agregarCliente(Cliente c) {
        String sql = "INSERT INTO Cliente(nombreCompleto, dni, edad, telefono, fechaInscripcion, tieneAptoFisico, archivoApto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getNombreCompleto());
            stmt.setString(2, c.getDni());
            stmt.setInt(3, c.getEdad());
            stmt.setString(4, c.getTelefono());
            stmt.setDate(5, new java.sql.Date(c.getFechaInscripcion().getTime()));
            stmt.setBoolean(6, c.isTieneAptoFisico());
            stmt.setString(7, c.getArchivoApto());
            int affected = stmt.executeUpdate();
            if (affected == 0) return -1;
            // obtener el id generado automáticamente
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setIdCliente(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * busca un cliente por su id
     * @param id id del cliente a buscar
     * @return cliente encontrado o null si no existe
     */
    public Cliente obtenerPorId(int id) {
        String sql = "SELECT idCliente, nombreCompleto, dni, edad, telefono, fechaInscripcion, tieneAptoFisico, archivoApto FROM Cliente WHERE idCliente = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapCliente(rs);
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * busca clientes según criterios de búsqueda
     * @param nombre nombre a buscar (puede ser parcial)
     * @param dni dni a buscar (puede ser parcial)
     * @param telefono teléfono a buscar (puede ser parcial)
     * @param tieneApto filtro de apto físico (true/false/null para cualquiera)
     * @return lista de clientes que cumplen los criterios
     */
    public List<Cliente> buscarClientes(String nombre, String dni, String telefono, Boolean tieneApto) {
        // construir consulta dinámica según los filtros proporcionados
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT idCliente, nombreCompleto, dni, edad, telefono, fechaInscripcion, tieneAptoFisico, archivoApto FROM Cliente WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (nombre != null && !nombre.isBlank()) { sb.append(" AND nombreCompleto LIKE ?"); params.add("%" + nombre + "%"); }
        if (dni != null && !dni.isBlank()) { sb.append(" AND dni LIKE ?"); params.add("%" + dni + "%"); }
        if (telefono != null && !telefono.isBlank()) { sb.append(" AND telefono LIKE ?"); params.add("%" + telefono + "%"); }
        if (tieneApto != null) { sb.append(" AND tieneAptoFisico = ?"); params.add(tieneApto);
        }

        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) stmt.setString(i + 1, s);
                else if (p instanceof Boolean b) stmt.setBoolean(i + 1, b);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCliente(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * elimina un cliente de la base de datos
     * @param id id del cliente a eliminar
     * @return true si se eliminó correctamente, false si hubo error
     */
    public boolean eliminarCliente(int id) {
        String sql = "DELETE FROM Cliente WHERE idCliente = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * mapea un resultset de la base de datos a un objeto cliente
     * @param rs resultset con los datos del cliente
     * @return objeto cliente con los datos mapeados
     */
    private Cliente mapCliente(ResultSet rs) throws SQLException {
        int id = rs.getInt("idCliente");
        String nombre = rs.getString("nombreCompleto");
        String dni = rs.getString("dni");
        int edad = rs.getInt("edad");
        String tel = rs.getString("telefono");
        Date fecha = rs.getDate("fechaInscripcion");
        boolean apto = rs.getBoolean("tieneAptoFisico");
        String archivo = rs.getString("archivoApto");
        Cliente c = new Cliente(id, nombre, dni, edad, tel, fecha);
        c.setTieneAptoFisico(apto);
        c.setArchivoApto(archivo);
        return c;
    }
}
