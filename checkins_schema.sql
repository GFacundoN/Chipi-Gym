-- Tabla para registrar los check-ins de los clientes
CREATE TABLE IF NOT EXISTS CheckIn (
    idCheckIn INT AUTO_INCREMENT PRIMARY KEY,
    idCliente INT NOT NULL,
    fechaHora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipoRegistro ENUM('ENTRADA', 'SALIDA') DEFAULT 'ENTRADA',
    metodoRegistro ENUM('QR', 'MANUAL') DEFAULT 'QR',
    notas VARCHAR(255),
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente) ON DELETE CASCADE,
    INDEX idx_cliente (idCliente),
    INDEX idx_fecha (fechaHora)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vista para obtener check-ins con información del cliente
CREATE OR REPLACE VIEW vista_checkins AS
SELECT 
    c.idCheckIn,
    c.idCliente,
    cl.nombreCompleto,
    cl.dni,
    c.fechaHora,
    c.tipoRegistro,
    c.metodoRegistro,
    c.notas,
    m.estado AS estadoMembresia,
    DATEDIFF(m.fechaFin, CURDATE()) AS diasMembresiaRestantes
FROM CheckIn c
INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
LEFT JOIN Membresia m ON cl.idCliente = m.idCliente 
    AND m.idMembresia = (
        SELECT MAX(m2.idMembresia) 
        FROM Membresia m2 
        WHERE m2.idCliente = cl.idCliente
    )
ORDER BY c.fechaHora DESC;
