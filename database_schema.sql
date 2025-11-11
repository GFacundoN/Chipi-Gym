-- script para crear la base de datos chipi gym
-- ejecutar este script en mysql workbench o desde consola

CREATE DATABASE IF NOT EXISTS chipi_gym;
USE chipi_gym;

-- tabla de clientes
CREATE TABLE IF NOT EXISTS Cliente (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    nombreCompleto VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    edad INT NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fechaInscripcion DATE NOT NULL,
    tieneAptoFisico BOOLEAN DEFAULT FALSE,
    archivoApto VARCHAR(500),
    INDEX idx_dni (dni),
    INDEX idx_nombre (nombreCompleto)
);

-- tabla de membresías
CREATE TABLE IF NOT EXISTS Membresia (
    idMembresia INT AUTO_INCREMENT PRIMARY KEY,
    idCliente INT NOT NULL,
    fechaInicio DATE NOT NULL,
    fechaFin DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'Activa',
    fechaRenovacion DATE,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente) ON DELETE CASCADE,
    INDEX idx_cliente (idCliente),
    INDEX idx_estado (estado),
    INDEX idx_fecha_fin (fechaFin)
);

-- vista para ver clientes con su membresía activa
CREATE OR REPLACE VIEW vista_clientes_membresias AS
SELECT 
    c.idCliente,
    c.nombreCompleto,
    c.dni,
    c.telefono,
    m.idMembresia,
    m.fechaInicio,
    m.fechaFin,
    m.estado,
    DATEDIFF(m.fechaFin, CURDATE()) as diasRestantes,
    CASE 
        WHEN CURDATE() > m.fechaFin THEN 'Vencida'
        WHEN DATEDIFF(m.fechaFin, CURDATE()) <= 5 THEN 'Por vencer'
        ELSE 'Activa'
    END as estadoCalculado
FROM Cliente c
LEFT JOIN Membresia m ON c.idCliente = m.idCliente
    AND m.idMembresia = (
        SELECT MAX(m2.idMembresia) 
        FROM Membresia m2 
        WHERE m2.idCliente = c.idCliente
    );

-- procedimiento almacenado para renovar membresía
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS renovar_membresia(
    IN p_idCliente INT,
    IN p_diasExtension INT
)
BEGIN
    DECLARE v_fechaInicio DATE;
    DECLARE v_fechaFin DATE;
    
    -- obtener la fecha fin de la última membresía
    SELECT fechaFin INTO v_fechaFin
    FROM Membresia
    WHERE idCliente = p_idCliente
    ORDER BY idMembresia DESC
    LIMIT 1;
    
    -- si la membresía está vencida, iniciar desde hoy
    -- si no, extender desde la fecha de fin actual
    IF v_fechaFin < CURDATE() THEN
        SET v_fechaInicio = CURDATE();
    ELSE
        SET v_fechaInicio = v_fechaFin;
    END IF;
    
    SET v_fechaFin = DATE_ADD(v_fechaInicio, INTERVAL p_diasExtension DAY);
    
    -- insertar nueva membresía
    INSERT INTO Membresia (idCliente, fechaInicio, fechaFin, estado, fechaRenovacion)
    VALUES (p_idCliente, v_fechaInicio, v_fechaFin, 'Activa', CURDATE());
    
    -- actualizar membresías anteriores como vencidas
    UPDATE Membresia 
    SET estado = 'Vencida'
    WHERE idCliente = p_idCliente 
    AND idMembresia < LAST_INSERT_ID();
END //
DELIMITER ;

-- datos de ejemplo (opcional, comentar si no se desea)
-- INSERT INTO Cliente (nombreCompleto, dni, edad, telefono, fechaInscripcion, tieneAptoFisico)
-- VALUES ('Juan Pérez', '12345678', 25, '1234567890', CURDATE(), TRUE);
