package com.chipigym.chipi.gym;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * clase que maneja la conexión a la base de datos mysql
 */
public class ConexionBD {
    // configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/chipi_gym";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    /**
     * obtiene una conexión a la base de datos
     * @return conexión activa a la base de datos
     * @throws SQLException si hay error al conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
