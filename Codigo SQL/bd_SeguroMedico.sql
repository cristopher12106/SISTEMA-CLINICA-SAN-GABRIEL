-- Ejecucion primordial -- 
CREATE TABLE IF NOT EXISTS seguro_medico (
    id_seguro INT AUTO_INCREMENT PRIMARY KEY,
    compania VARCHAR(100) NOT NULL,
    numero_poliza VARCHAR(50) NOT NULL,
    tipo_cobertura VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

-- Fin de ejecucion primordial -- 
-- Seguro medico de prueba
INSERT INTO seguro_medico (compania, numero_poliza, tipo_cobertura, estado)
VALUES ('Rimac Seguros', '1234567890', 'T', TRUE);