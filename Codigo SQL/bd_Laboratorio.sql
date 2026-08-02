-- Ejecucion primordial --
CREATE TABLE IF NOT EXISTS examen_laboratorio (
    id_examen INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    tipo_examen VARCHAR(100) NOT NULL,
    observaciones TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    fecha_solicitud DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resultado_examen (
    id_resultado INT AUTO_INCREMENT PRIMARY KEY,
    id_examen INT NOT NULL,
    detalle_resultado TEXT NOT NULL,
    observaciones TEXT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_examen) REFERENCES examen_laboratorio(id_examen)
);

-- Fin de ejecucion primordial --

-- Examenes de laboratorio de prueba --
INSERT INTO examen_laboratorio (id_atencion, tipo_examen, estado)
VALUES 
(101, 'Hemograma Completo', 'Pendiente'),
(102, 'Examen de Orina Completo', 'Pendiente'),
(103, 'Perfil Lipídico', 'Pendiente');