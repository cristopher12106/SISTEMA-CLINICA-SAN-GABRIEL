-- MODULO USUARIOS
CREATE DATABASE sistema_clinica_san_gabriel;
USE sistema_clinica_san_gabriel;

CREATE TABLE Usuarios (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

select * from Usuarios;

CREATE TABLE Auditorias (
    idAuditoria INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    operacion VARCHAR(255) NOT NULL,
    
    CONSTRAINT fk_auditoria_usuario 
        FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario)
        ON DELETE RESTRICT
);


-- MODULO PACIENTES
CREATE TABLE IF NOT EXISTS apoderado (
    id_apoderado INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(8) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    parentesco VARCHAR(50) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

select * from paciente;

CREATE TABLE IF NOT EXISTS seguro_medico (
    id_seguro INT AUTO_INCREMENT PRIMARY KEY,
    compania VARCHAR(100) NOT NULL,
    numero_poliza VARCHAR(50) NOT NULL,
    tipo_cobertura VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

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

-- MODULO MEDICOS Y CITAS

CREATE TABLE Especialidades (
    idEspecialidad INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE Medicos (
    idMedico INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    colegiatura VARCHAR(20) NOT NULL UNIQUE,
    dni VARCHAR(15) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(100),
    
    CONSTRAINT fk_Medicos_usuarios
        FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario)
        ON DELETE RESTRICT
);

CREATE TABLE Horarios_Medicos (
    idHorario INT AUTO_INCREMENT PRIMARY KEY,
    idMedico INT NOT NULL,
    diaSemana VARCHAR(15) NOT NULL,
    horaInicio TIME NOT NULL,
    horaFin TIME NOT NULL,
    FOREIGN KEY (idMedico) REFERENCES Medicos(idMedico)
);
select * from citas;
select * from Horarios_Medicos;
TRUNCATE TABLE Horarios_Medicos;

CREATE TABLE Citas (
    idCita INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    idMedico INT NOT NULL,
    numeroHistoriaClinica VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Programada',
    observaciones VARCHAR(255),
    FOREIGN KEY (idMedico) REFERENCES Medicos(idMedico)
);

CREATE TABLE Medico_Especialidad (
    idMedico INT NOT NULL,
    idEspecialidad INT NOT NULL,
    PRIMARY KEY (idMedico, idEspecialidad),
    FOREIGN KEY (idMedico) REFERENCES Medicos(idMedico),
    FOREIGN KEY (idEspecialidad) REFERENCES Especialidades(idEspecialidad)
);

-- MODULO ATENCION MEDICA

CREATE TABLE atenciones_medicas (
    idAtencion INT AUTO_INCREMENT PRIMARY KEY,
    codigoCita VARCHAR(20) NOT NULL,
    motivoConsulta TEXT NOT NULL,
    antecedentes TEXT,
    planTratamiento TEXT,
    observaciones TEXT,
    fechaAtencion DATETIME DEFAULT CURRENT_TIMESTAMP
);

select * from atenciones_medicas;

CREATE TABLE signos_vitales (
    idSignos INT AUTO_INCREMENT PRIMARY KEY,
    idAtencion INT NOT NULL,
    pas DOUBLE NOT NULL,
    pad DOUBLE NOT NULL,
    temperatura DOUBLE NOT NULL,
    peso DOUBLE NOT NULL,
    talla DOUBLE NOT NULL,
    fc INT NOT NULL,
    fr INT NOT NULL,
    imc DOUBLE NOT NULL,
    CONSTRAINT fk_signos_atencion FOREIGN KEY (idAtencion) REFERENCES atenciones_medicas(idAtencion) ON DELETE CASCADE
);

CREATE TABLE diagnosticos_atencion (
    idDiagnostico INT AUTO_INCREMENT PRIMARY KEY,
    idAtencion INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- 'Presuntivo' o 'Definitivo'
    CONSTRAINT fk_diag_atencion FOREIGN KEY (idAtencion) REFERENCES atenciones_medicas(idAtencion) ON DELETE CASCADE
);

CREATE TABLE recetas_medicas (
    idReceta INT AUTO_INCREMENT PRIMARY KEY,
    idAtencion INT NOT NULL,
    fechaEmision DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_receta_atencion FOREIGN KEY (idAtencion) REFERENCES atenciones_medicas(idAtencion) ON DELETE CASCADE
);

CREATE TABLE detalle_receta (
    idDetalle INT AUTO_INCREMENT PRIMARY KEY,
    idReceta INT NOT NULL,
    idMedicamento INT NOT NULL,
    cantidad INT NOT NULL,
    indicacion TEXT NOT NULL,
    CONSTRAINT fk_detalle_receta FOREIGN KEY (idReceta) REFERENCES recetas_medicas(idReceta) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_medicamento FOREIGN KEY (idMedicamento) REFERENCES medicamento(id_Medicamento)
);
