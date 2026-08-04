/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.MedicoDAO;
import datos.SesionUsuario;
import javax.swing.JOptionPane;

// @Harold

public class MedicoLOG {

    public static boolean registrarMedico(Medico medico) {
        if(UsuarioLOG.buscarUsuario(medico.getIdUsuario()) == null){
            JOptionPane.showMessageDialog(null,
                    "No esta registrado el Usuario",
                    "Datos no validos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(UsuarioLOG.buscarUsuario(medico.getIdUsuario()).getRol() != Rol.MEDICO){
            JOptionPane.showMessageDialog(null,
                    "El usuario no tiene el rol de medico",
                    "Datos no validos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (medico.getCodigo().isEmpty() || medico.getColegiatura().isEmpty()
            || medico.getDni().isEmpty() || medico.getNombres().isEmpty()
            || medico.getApellidos().isEmpty()) {
        JOptionPane.showMessageDialog(null,
                "Todos los campos obligatorios deben estar completos.",
                "Datos incompletos", JOptionPane.WARNING_MESSAGE);
        return false;
        }

        // Validar formato de codigo (5 digitos numericos)
        if (!medico.getCodigo().matches("\\d{5}")) {
            JOptionPane.showMessageDialog(null,
                    "El codigo debe tener exactamente 5 digitos numericos.",
                    "Codigo invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato de colegiatura (CM- seguido de 5 digitos, ej: CM-12345)
        if (!medico.getColegiatura().matches("CM-\\d{5}")) {
            JOptionPane.showMessageDialog(null,
                    "La colegiatura debe tener el formato CM-12345 (CM- seguido de 5 digitos).",
                    "Colegiatura invalida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato de DNI (8 digitos numericos)
        if (!medico.getDni().matches("\\d{8}")) {
            JOptionPane.showMessageDialog(null,
                    "El DNI debe tener exactamente 8 digitos numericos.",
                    "DNI invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato de telefono (9 digitos numericos)
        if (!medico.getTelefono().isEmpty() && !medico.getTelefono().matches("\\d{9}")) {
            JOptionPane.showMessageDialog(null,
                    "El telefono debe tener exactamente 9 digitos numericos.",
                    "Telefono invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato de correo (debe terminar en @colegiomedico.pe)
        if (!medico.getCorreo().isEmpty() && !medico.getCorreo().matches("^[\\w.+-]+@colegiomedico\\.pe$")) {
            JOptionPane.showMessageDialog(null,
                    "El correo debe tener el formato nombre@colegiomedico.pe",
                    "Correo invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // RN-12: especialidad obligatoria
        if (medico.getEspecialidades() == null || medico.getEspecialidades().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "El medico debe tener al menos una especialidad.",
                    "Especialidad obligatoria", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // RN-13: colegiatura unica
        if (MedicoDAO.existeColegiatura(medico.getColegiatura())) {
            JOptionPane.showMessageDialog(null,
                    "Ya existe un medico registrado con la colegiatura " + medico.getColegiatura() + ".",
                    "Colegiatura duplicada", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito = MedicoDAO.registrarMedico(medico);
        if (exito) {
            AuditoriaLOG.registrarAuditoria(
                SesionUsuario.getInstance().getIdUsuario(),
                "Medicos",
                "Registró al medico " + medico.getCodigo() + " - " + medico.getNombres() + " " + medico.getApellidos()
            );
        }
        return exito;
    }

    public static boolean actualizarMedico(Medico medico) {
        if (medico.getCodigo() == null || medico.getCodigo().equals("NN")) {
            JOptionPane.showMessageDialog(null,
                    "Debe seleccionar un medico valido para actualizar.",
                    "Medico invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        boolean exito = MedicoDAO.actualizarMedico(medico);
        if (exito) {
            AuditoriaLOG.registrarAuditoria(
                SesionUsuario.getInstance().getIdUsuario(),
                "Medicos",
                "Actualizó al medico " + medico.getCodigo()
            );
        }
        return exito;
    }

    public static boolean eliminarMedico(String codigo) {
        boolean exito = MedicoDAO.eliminarMedico(codigo);
        if (exito) {
            AuditoriaLOG.registrarAuditoria(
                SesionUsuario.getInstance().getIdUsuario(),
                "Medicos",
                "Eliminó al medico " + codigo
            );
        }
        return exito;
    }

    public static java.util.ArrayList<Medico> listarMedicos() {
        return MedicoDAO.listarMedicos();
    }

    public static java.util.ArrayList<Especialidad> listarEspecialidades() {
        return MedicoDAO.listarEspecialidades();
    }

    public static boolean registrarEspecialidad(Especialidad especialidad) {
        if (especialidad == null || especialidad.getCodigo() == null || especialidad.getCodigo().trim().isEmpty()
                || especialidad.getNombre() == null || especialidad.getNombre().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "El codigo y el nombre de la especialidad son obligatorios.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito = MedicoDAO.registrarEspecialidad(especialidad);
        if (exito) {
            AuditoriaLOG.registrarAuditoria(
                SesionUsuario.getInstance().getIdUsuario(),
                "Especialidades",
                "Registró la especialidad " + especialidad.getCodigo() + " - " + especialidad.getNombre()
            );
        }
        return exito;
    }
}