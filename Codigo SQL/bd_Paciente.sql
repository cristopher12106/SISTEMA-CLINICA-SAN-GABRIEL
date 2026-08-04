-- orden de registro: primero Apoderado, luego SeguroMedico, y por último Paciente --
-- Ejecucion primordial -- 
CREATE TABLE IF NOT EXISTS paciente (
    id_paciente INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(8) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    sexo VARCHAR(10) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    numero_historia_clinica VARCHAR(50) NOT NULL UNIQUE,
    id_seguro INT NULL,
    id_apoderado INT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_seguro) REFERENCES seguro_medico(id_seguro),
    FOREIGN KEY (id_apoderado) REFERENCES apoderado(id_apoderado)
);
-- Fin de ejecucion primordial -- 
-- paciente de prueba
INSERT INTO paciente (dni, nombres, apellidos, fecha_nacimiento, sexo, telefono, direccion, numero_historia_clinica, id_seguro, id_apoderado, estado)
VALUES ('87654321', 'Juan', 'Perez', '2000-05-15', 'M', '988776655', 'Av. Principal 123', '0000000001', 1, 1, TRUE);
