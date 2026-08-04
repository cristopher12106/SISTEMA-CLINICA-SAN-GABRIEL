/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica.builder;

import java.time.LocalDate;
import java.util.ArrayList;
/**
 *
 * @author Cristopher
 */
public class Reporte {

    private String titulo;
    private LocalDate fechaGeneracion;
    private ArrayList<?> datos;

    public Reporte() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public ArrayList<?> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<?> datos) {
        this.datos = datos;
    }

}
