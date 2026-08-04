-- Ejecucion primordial -- 
CREATE TABLE IF NOT EXISTS apoderado (
    id_apoderado INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(8) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    parentesco VARCHAR(50) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);
-- Fin de ejecucion primordial -- 
-- Datos de prueba
INSERT INTO apoderado (dni, nombres, apellidos, telefono, parentesco, estado)
VALUES ('12345678', 'Maria', 'Gonzalez', '987654321', 'Madre', TRUE);