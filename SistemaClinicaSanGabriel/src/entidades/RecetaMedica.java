package entidades;

import java.util.ArrayList;
import java.util.List;

public class RecetaMedica {
    private int idReceta;
    private int idAtencion;
    private boolean despachada;
    private List<DetalleReceta> detalles;

    // Constructor vacío inicializando la lista de detalles
    public RecetaMedica() {
        this.detalles = new ArrayList<>();
    }

    // Método helper para agregar un renglón de medicamento
    public void agregarDetalle(DetalleReceta detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
        }
    }

    public void agregarMedicamento(int idMedicamento, String nombreMedicamento, int cantidad, String indicacion) {
        this.detalles.add(new DetalleReceta(idMedicamento, nombreMedicamento, cantidad, indicacion));
    }

    public boolean tieneDetalles() {
        return this.detalles != null && !this.detalles.isEmpty();
    }

    public int getCantidadMedicamentos() {
        return this.detalles == null ? 0 : this.detalles.size();
    }

    // --- GETTERS Y SETTERS ---

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public List<DetalleReceta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReceta> detalles) {
        this.detalles = detalles;
    }

    public boolean isDespachada() {
        return despachada;
    }

    public void setDespachada(boolean despachada) {
        this.despachada = despachada;
    }

    @Override
    public String toString() {
        return "RecetaMedica{idReceta=" + idReceta
                + ", idAtencion=" + idAtencion
                + ", medicamentos=" + getCantidadMedicamentos() + '}';
    }
}