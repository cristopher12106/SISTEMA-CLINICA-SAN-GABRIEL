/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import datos.LaboratorioDAO;
import datos.SesionUsuario;
import entidades.ExamenLaboratorio;
import entidades.ResultadoExamen;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class LaboratorioLOG {

    private final LaboratorioDAO laboratorioDAO;

    public LaboratorioLOG() {
        this.laboratorioDAO = new LaboratorioDAO();
    }
    public boolean registrarSolicitud(ExamenLaboratorio examen) throws Exception {
        if (examen == null) {
            throw new IllegalArgumentException("El examen no puede ser nulo.");
        }
        if (examen.getIdPaciente() <= 0) {
            throw new IllegalArgumentException("Debe ingresar un ID de paciente válido.");
        }
        if (examen.getTipoExamen() == null || examen.getTipoExamen().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de examen.");
        }

        try {
            boolean registrado = laboratorioDAO.registrarExamen(examen);
            if (registrado) {
                try {
                    AuditoriaLOG.registrarAuditoria(
                        SesionUsuario.getInstance().getIdUsuario(),
                        "Laboratorio",
                        "Registró solicitud de examen (" + examen.getTipoExamen() + ") para Paciente ID: " + examen.getIdPaciente()
                    );
                } catch (Exception e) {
                    System.err.println("Error al registrar auditoría en Laboratorio: " + e.getMessage());
                }
            }
            return registrado;
        } catch (SQLException e) {
            throw new Exception("Error al registrar la solicitud de examen: " + e.getMessage());
        }
    }

    // Solicitudes q aun no se atendieron
    public List<ExamenLaboratorio> listarSolicitudesPendientes() throws Exception {
        try {
            return laboratorioDAO.listarPorEstado("Pendiente");
        } catch (SQLException e) {
            throw new Exception("Error al consultar las solicitudes pendientes: " + e.getMessage());
        }
    }

    // Cambiar estado del examen 
    public boolean cambiarEstadoExamen(int idExamen, String nuevoEstado) throws Exception {
        // Validar estados permitidos (RN-32)
        if (!nuevoEstado.equals("Pendiente") && !nuevoEstado.equals("En proceso") &&
            !nuevoEstado.equals("Finalizado") && !nuevoEstado.equals("Entregado")) {
            throw new IllegalArgumentException("El estado '" + nuevoEstado + "' no es válido.");
        }
        try {
            ExamenLaboratorio examen = laboratorioDAO.obtenerPorId(idExamen);
            if (examen == null) {
                throw new Exception("No existe la orden de examen solicitada.");
            }
            return laboratorioDAO.actualizarEstado(idExamen, nuevoEstado);
        } catch (SQLException e) {
            throw new Exception("Error al cambiar el estado del examen: " + e.getMessage());
        }
    }

    // Registrar Resultados del Examen
    public boolean registrarResultado(int idExamen, String detalleResultado, String observaciones) throws Exception {
        if (detalleResultado == null || detalleResultado.trim().isEmpty()) {
            throw new IllegalArgumentException("El detalle del resultado no puede estar vacío.");
        }
        try {
            ExamenLaboratorio examen = laboratorioDAO.obtenerPorId(idExamen);
            if (examen == null) {
                throw new Exception("No se puede registrar resultado sin una orden médica previa válida");
            }
            ResultadoExamen resultado = new ResultadoExamen();
            resultado.setIdExamen(idExamen);
            resultado.setDetalleResultado(detalleResultado);
            resultado.setObservaciones(observaciones);
            boolean registrado = laboratorioDAO.registrarResultado(resultado);
            if (registrado) {
                laboratorioDAO.actualizarEstado(idExamen, "Finalizado");
                try {
                    AuditoriaLOG.registrarAuditoria(
                        SesionUsuario.getInstance().getIdUsuario(),
                        "Laboratorio",
                        "Registró resultado para el Examen ID: " + idExamen
                    );
                } catch (Exception e) {
                    System.err.println("Error al registrar auditoría en Laboratorio: " + e.getMessage());
                }
            }
            return registrado;
        } catch (SQLException e) {
            throw new Exception("Error al registrar el resultado del examen: " + e.getMessage());
        }
    }
}