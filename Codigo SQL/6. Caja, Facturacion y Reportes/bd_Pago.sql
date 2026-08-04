-- Orden: primero AtencionMedica, luego Pago --
-- Ejecucion primordial --

CREATE TABLE IF NOT EXISTS pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_atencion INT NOT NULL,
    fecha_pago DATE NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_pago_atencion
        FOREIGN KEY (id_atencion) REFERENCES atenciones_medicas(idAtencion)
);

-- Pagos de prueba
INSERT INTO pago (id_atencion, fecha_pago, monto, metodo_pago, estado)
VALUES (1, '2026-07-26', 120.00, 'Efectivo', TRUE);

INSERT INTO pago (id_atencion, fecha_pago, monto, metodo_pago, estado)
VALUES (2, '2026-07-26', 250.00, 'Tarjeta', TRUE);
