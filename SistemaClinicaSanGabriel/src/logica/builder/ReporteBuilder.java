/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica.builder;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @author Cristopher
 */

public class ReporteBuilder {
    private Reporte reporte;

    public ReporteBuilder() {
        reporte = new Reporte();
    }

    public ReporteBuilder setTitulo(String titulo) {
        reporte.setTitulo(titulo);
        return this;
    }

    public ReporteBuilder setFechaGeneracion(LocalDate fechaGeneracion) {
        reporte.setFechaGeneracion(fechaGeneracion);
        return this;
    }

    public ReporteBuilder setDatos(ArrayList<?> datos) {
        reporte.setDatos(datos);
        return this;
    }

    public Reporte build() {
        return reporte;
    }
}
