package logica;

import datos.AtencionMedicaDAO;
import datos.CitaDAO;
import entidades.AtencionMedica;
import entidades.Cita;
import entidades.Diagnostico;
import entidades.SignosVitales;

import javax.swing.JOptionPane;

public class AtencionMedicaLOG {

    public AtencionMedica obtenerAtencion(int idAtencion) {
        if (idAtencion <= 0) {
            JOptionPane.showMessageDialog(null, "El id de la atención debe ser mayor a 0.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        AtencionMedica atencion = AtencionMedicaDAO.buscarPorId(idAtencion);

        if (atencion == null) {
            JOptionPane.showMessageDialog(null, "No se encontró una atención médica con el id " + idAtencion + ".", "No encontrada", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return atencion;
    }

    public boolean registrarAtencion(AtencionMedica atencion) {

        // 1. Validaciones generales de la Atención
        if (atencion == null) {
            JOptionPane.showMessageDialog(null, "No se proporcionaron datos para la atención médica.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // RN-23: validar que el código de cita exista y esté en estado programada
        String codigoCita = atencion.getCodigoCita();
        if (codigoCita == null || codigoCita.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El código de cita es obligatorio.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!codigoCita.matches("CIT-\\d{4}")) {
            JOptionPane.showMessageDialog(null, "El código de cita no tiene el formato válido (ej: CIT-0001).", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Cita cita = CitaDAO.buscarPorCodigo(codigoCita);
        if (cita == null) {
            JOptionPane.showMessageDialog(null, "No existe una cita registrada con el código " + codigoCita + ".", "Cita no encontrada", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!"Programada".equalsIgnoreCase(cita.getEstado())) {
            JOptionPane.showMessageDialog(null, "La cita " + codigoCita + " no está en estado Programada (estado actual: " + cita.getEstado() + "). No se puede registrar la atención.", "Cita no válida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (atencion.getMotivoConsulta() == null || atencion.getMotivoConsulta().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El motivo de la consulta es obligatorio.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2. Validaciones de Signos Vitales
        SignosVitales sv = atencion.getSignosVitales();
        if (sv != null) {
            if (sv.getPeso() <= 0) {
                JOptionPane.showMessageDialog(null, "El peso del paciente debe ser mayor a 0 kg.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (sv.getTalla() <= 0) {
                JOptionPane.showMessageDialog(null, "La talla del paciente debe ser mayor a 0 cm.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (sv.getPas() > 0 && sv.getPad() > 0 && sv.getPas() <= sv.getPad()) {
                JOptionPane.showMessageDialog(null, "La Presión Arterial Sistólica (PAS) debe ser mayor a la Diastólica (PAD).", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        // 3. Validaciones de Diagnósticos
        if (atencion.getListaDiagnosticos() == null || atencion.getListaDiagnosticos().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe registrar al menos un diagnóstico para el paciente.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        for (Diagnostico d : atencion.getListaDiagnosticos()) {
            if (d.getDescripcion() == null || d.getDescripcion().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Uno de los diagnósticos no cuenta con una descripción.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (d.getTipo() == null || (!d.getTipo().equalsIgnoreCase("Presuntivo") && !d.getTipo().equalsIgnoreCase("Definitivo"))) {
                JOptionPane.showMessageDialog(null, "El tipo de diagnóstico debe ser 'Presuntivo' o 'Definitivo'.", "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        // 4. Invocar a la Capa de Datos (DAO)
        boolean guardadoExitoso = AtencionMedicaDAO.registrarAtencionCompleta(atencion);

        if (guardadoExitoso) {
            JOptionPane.showMessageDialog(null, "La atención médica se registró correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Ocurrió un error en la base de datos al registrar la atención.", "Error BD", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}