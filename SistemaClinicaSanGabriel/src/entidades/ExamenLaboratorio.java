/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;
import java.sql.Timestamp;
/**
 *
 * @author LENOVO
 */
public class ExamenLaboratorio {
    private int idExamen;
    private int idPaciente;    
    private String tipoExamen;  
    private String observaciones;
    private String estado;       //opciones:"Pendiente", "En proceso", "Finalizado", "Entregado"
    private Timestamp fechaSolicitud;
    public ExamenLaboratorio() {
    }
    public ExamenLaboratorio(int idExamen, int idAtencion, String tipoExamen, String estado, Timestamp fechaSolicitud) {
        this.idExamen = idExamen;
        this.idPaciente = idAtencion;
        this.tipoExamen = tipoExamen;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
    }
    // Get y Set:
    public int getIdExamen() {
        return idExamen;
    }
    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
    }
    public int getIdPaciente() {
        return idPaciente;
    }
    public void setIdPaciente(int idAtencion) {
        this.idPaciente = idPaciente;
    }
    public String getTipoExamen() {
        return tipoExamen;
    }
    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }
     public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Timestamp getFechaSolicitud() {
        return fechaSolicitud;
    }
    public void setFechaSolicitud(Timestamp fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
}
