/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import datos.MedicamentoDAO;
import datos.SesionUsuario;
import entidades.EntregaMedicamento;
import entidades.Medicamento;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author LENOVO
 */
public class FarmaciaLOG {
    private final MedicamentoDAO medicamentoDAO;
    public FarmaciaLOG() {
        this.medicamentoDAO = new MedicamentoDAO();
    }
    // Consulta inventario
    public List<Medicamento> obtenerInventario() throws Exception {
        try {
            return medicamentoDAO.listar();
        } catch (SQLException e) {
            throw new Exception("Error al consultar el inventario de farmacia: " + e.getMessage());
        }
    }

    // Lo que es despacho o entrega
    public String procesarEntrega(int idAtencion, int idMedicamento, int cantidad) throws Exception {
        // Validar lo minimo
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a entregar debe ser mayor a 0.");
        }

        try {
            // 1. Ver si existe o no la medicina
            Medicamento med = medicamentoDAO.obtenerPorId(idMedicamento);
            if (med == null || !med.isEstado()) {
                throw new Exception("El medicamento seleccionado no existe o está inactivo.");
            }
            // 2. Ver stock
            if (med.getStockActual() < cantidad) {
                throw new Exception("Stock insuficiente. Stock disponible: " + med.getStockActual() + " unidades.");
            }
            // 3.Actualizo inventario
            int nuevoStock = med.getStockActual() - cantidad;
            boolean stockActualizado = medicamentoDAO.actualizarStock(idMedicamento, nuevoStock);
            if (!stockActualizado) {
                throw new Exception("No se pudo actualizar el stock en la base de datos.");
            }
            // 4. Registro entrega
            EntregaMedicamento entrega = new EntregaMedicamento();
            entrega.setIdAtencion(idAtencion);
            entrega.setIdMedicamento(idMedicamento);
            entrega.setCantidad(cantidad);
            boolean entregaRegistrada = medicamentoDAO.registrarEntrega(entrega);
            if (!entregaRegistrada) {
                throw new Exception("Error al registrar la transacción de entrega.");
            }
            // 5. Auditoria
            try {
                AuditoriaLOG.registrarAuditoria(
                    SesionUsuario.getInstance().getIdUsuario(),
                    "Farmacia",
                    "Despachó medicamento ID: " + idMedicamento + " (Cantidad: " + cantidad + ") para Atención N° " + idAtencion
                );
            } catch (Exception e) {
                System.err.println("Error al registrar la auditoría de Farmacia: " + e.getMessage());
            }
            // 6.Alerta de stock mínimo
            String mensajeResultado = "Medicamento despachado con éxito.";
            if (nuevoStock <= med.getStockMinimo()) {
                mensajeResultado += "\n ¡ALERTA DE STOCK MÍNIMO! El stock actual (" 
                                 + nuevoStock + ") es menor o igual al mínimo permitido (" 
                                 + med.getStockMinimo() + "). Requiere reabastecimiento.";
            }
            return mensajeResultado;
        } catch (SQLException e) {
            throw new Exception("Error en la base de datos al procesar la entrega: " + e.getMessage());
        }
    }
    public boolean registrarMedicamento(Medicamento med) throws Exception {
        try {
            // Llama al método insertar/guardar de tu DAO
            return medicamentoDAO.insertar(med); 
        } catch (SQLException e) {
            throw new Exception("Error al guardar el medicamento: " + e.getMessage());
        }
    }
}
