/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.JOptionPane;

 // @Harold

public class CitaLOG {

    public static boolean registrarCita(Cita cita) {
        // Validar formato de historia clinica (8 digitos, ej: 72819023)
        if (!cita.getNumeroHistoriaClinica().matches("\\d{8}")) {
            JOptionPane.showMessageDialog(null,
                    "La historia clínica debe ser un número de 8 dígitos.",
                    "Historia clínica inválida", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validar formato de codigo de cita (CIT- seguido de 4 digitos, ej: CIT-0123)
        if (!cita.getCodigo().matches("CIT-\\d{4}")) {
            JOptionPane.showMessageDialog(null,
                    "El codigo de cita debe tener el formato (CIT- seguido de 4 digitos).",
                    "Codigo invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato de fecha (yyyy-MM-dd) y que sea una fecha real y valida
        if (!validarFecha(cita.getFecha())) {
            JOptionPane.showMessageDialog(null,
                    "La fecha no es valida. Debe tener el formato AAAA-MM-DD (ej: 2026-08-15).",
                    "Fecha invalida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato de hora (HH:mm, 24 horas)
        if (!cita.getHora().matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            JOptionPane.showMessageDialog(null,
                    "La hora debe tener el formato HH:mm en formato 24 horas (ej: 14:30).",
                    "Hora invalida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // RN-16: solo se pueden programar citas para pacientes registrados
        Paciente paciente = PacienteDAO.buscarPorHistoriaClinica(cita.getNumeroHistoriaClinica());
        if (paciente == null) {
            JOptionPane.showMessageDialog(null,
                    "No existe un paciente registrado con la historia clinica " + cita.getNumeroHistoriaClinica() + ".",
                    "Paciente no registrado", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // RN-14 / RN-19: la hora de la cita debe estar dentro del horario registrado del medico
        if (!estaDentroDeHorario(cita.getMedico(), cita.getFecha(), cita.getHora())) {
            JOptionPane.showMessageDialog(null,
                    "La hora seleccionada no esta dentro del horario de atencion del medico.",
                    "Horario no permitido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // RN-15 / RN-17: verificar disponibilidad del medico antes de registrar
        if (CitaDAO.medicoOcupado(cita.getMedico(), cita.getFecha(), cita.getHora())) {
            JOptionPane.showMessageDialog(null,
                    "El medico ya tiene una cita registrada en esa fecha y hora.",
                    "Medico no disponible", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito = CitaDAO.registrarCita(cita);
        if (exito) {
            AuditoriaLOG.registrarAuditoria(
                SesionUsuario.getInstance().getIdUsuario(),
                "Citas",
                "Registró la cita " + cita.getCodigo() + " para el paciente con HC " + cita.getNumeroHistoriaClinica()
            );
        }
        return exito;
    }

        // RN-21: cancelacion permitida solo hasta 2 horas antes de la cita
    public static boolean cancelarCita(String codigoCita) {
        Cita cita = CitaDAO.buscarPorCodigo(codigoCita);
        if (cita == null) {
            JOptionPane.showMessageDialog(null,
                "No se encontro la cita.",
                "Cita no encontrada", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        LocalDateTime fechaHoraCita = obtenerFechaHora(cita.getFecha(), cita.getHora());
        LocalDateTime ahora = LocalDateTime.now();

        if (fechaHoraCita.minusHours(2).isBefore(ahora)) {
            JOptionPane.showMessageDialog(null,
                "Solo se puede cancelar una cita hasta 2 horas antes de la hora programada.",
                "Cancelacion no permitida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito = CitaDAO.actualizarEstadoCita(codigoCita, "Cancelada");
        if (exito) {
            AuditoriaLOG.registrarAuditoria(
                SesionUsuario.getInstance().getIdUsuario(),
                "Citas","Canceló la cita N° " + codigoCita);
        }
        
        return exito;
    }
        
    public static boolean reprogramarCita(String codigoCita, String nuevaFecha, String nuevaHora) {
        // Validar formato de fecha
        if (!validarFecha(nuevaFecha)) {
            JOptionPane.showMessageDialog(null,
                    "La fecha no es valida. Debe tener el formato AAAA-MM-DD (ej: 2026-08-15).",
                    "Fecha invalida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato de hora (HH:mm, 24 horas)
        if (!nuevaHora.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            JOptionPane.showMessageDialog(null,
                    "La hora debe tener el formato HH:mm en formato 24 horas (ej: 14:30).",
                    "Hora invalida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Cita cita = CitaDAO.buscarPorCodigo(codigoCita);
        if (cita == null) {
            JOptionPane.showMessageDialog(null,
                    "No se encontro la cita.",
                    "Cita no encontrada", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (cita.getEstado().equals("Atendida")) {
            JOptionPane.showMessageDialog(null,
                    "No se puede reprogramar una cita que ya fue atendida.",
                    "Reprogramacion no permitida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!estaDentroDeHorario(cita.getMedico(), nuevaFecha, nuevaHora)) {
            JOptionPane.showMessageDialog(null,
                    "La nueva hora no esta dentro del horario de atencion del medico.",
                    "Horario no permitido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (CitaDAO.medicoOcupado(cita.getMedico(), nuevaFecha, nuevaHora)) {
            JOptionPane.showMessageDialog(null,
                    "El medico ya tiene otra cita registrada en esa fecha y hora.",
                    "Medico no disponible", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito = CitaDAO.reprogramarCita(codigoCita, nuevaFecha, nuevaHora);
        if (exito) {
            AuditoriaLOG.registrarAuditoria(
                SesionUsuario.getInstance().getIdUsuario(),
                "Citas",
                "Reprogramó la cita N° " + codigoCita + " para " + nuevaFecha + " " + nuevaHora
            );
        }
        return exito;
    }

    // HU-16 / RN-15: consultar disponibilidad del medico para evitar conflictos de horario
    public static boolean consultarDisponibilidad(Medico medico, String fecha, String hora) {
        return !CitaDAO.medicoOcupado(medico, fecha, hora);
    }

    public static ArrayList<Cita> listarCitas() {
        return CitaDAO.listarCitas();
    }

    private static boolean estaDentroDeHorario(Medico medico, String fecha, String hora) {
        ArrayList<HorarioMedico> horarios = HorarioDAO.listarHorariosPorMedico(medico);
        String diaSemana = obtenerDiaSemana(fecha);
        LocalTime horaCita = LocalTime.parse(hora);

        for (HorarioMedico h : horarios) {
            System.out.println("no hay horarios");
            if (h.getDiaSemana().equalsIgnoreCase(diaSemana)) {
                LocalTime inicio = LocalTime.parse(h.getHoraInicio());
                LocalTime fin = LocalTime.parse(h.getHoraFin());

                if (!horaCita.isBefore(inicio) && horaCita.isBefore(fin)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String obtenerDiaSemana(String fecha) {
        LocalDate fechaLocal = LocalDate.parse(fecha);
        java.time.DayOfWeek dia = fechaLocal.getDayOfWeek();

        return switch (dia) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miercoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sabado";
            case SUNDAY -> "Domingo";
            default -> "";
        };
    }

    private static LocalDateTime obtenerFechaHora(String fecha, String hora) {
        String texto = fecha + "T" + hora;
        return LocalDateTime.parse(texto);
    }
    
    private static boolean validarFecha(String fecha) {
        try {
            LocalDate.parse(fecha); // usa formato ISO por defecto: yyyy-MM-dd
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Cita buscarPorCodigo(String codigoCita) {
        if (codigoCita == null || codigoCita.trim().isEmpty()) {
            return null;
        }
        return CitaDAO.buscarPorCodigo(codigoCita.trim());
    }
}