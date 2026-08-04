package logica;

import datos.PacienteDAO;
import datos.ApoderadoDAO;
import datos.SeguroDAO;
import entidades.Paciente;
import entidades.Apoderado;
import entidades.SeguroMedico;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import datos.SesionUsuario;

public class PacienteLOG {

    // Patrones de validación
    private static final Pattern PATRON_DNI = Pattern.compile("^\\d{8}$");
    private static final Pattern PATRON_HISTORIA_CLINICA = Pattern.compile("^\\d{8}$");
    private static final Pattern PATRON_NOMBRES = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$");
    private static final Pattern PATRON_SEXO = Pattern.compile("^[MF]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{9}$");
    private static final Pattern PATRON_POLIZA = Pattern.compile("^\\d{10}$");
    private static final Pattern PATRON_COBERTURA = Pattern.compile("^[TP]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATRON_PARENTESCO = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$");

    public static boolean registrarPaciente(Paciente paciente) {
        if (!validarDatosCompletos(paciente)) {
            return false;
        }

        // Validar DNI único (busca en todos, activos e inactivos)
        Paciente existente = PacienteDAO.buscarPorDni(paciente.getDni(), false);
        if (existente != null) {
            JOptionPane.showMessageDialog(null,
                    "El DNI " + paciente.getDni() + " ya está registrado a nombre de:\n"
                    + existente.getNombres() + " " + existente.getApellidos() + ".\n\n"
                    + "Si es un paciente diferente, verifique que el DNI sea correcto.",
                    "DNI ya registrado", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar Historia Clínica única
        Paciente existenteHC = PacienteDAO.buscarPorHistoriaClinica(paciente.getNumeroHistoriaClinica(), false);
        if (existenteHC != null) {
            JOptionPane.showMessageDialog(null,
                    "La historia clínica Nro. " + paciente.getNumeroHistoriaClinica() + " ya está asignada a:\n"
                    + existenteHC.getNombres() + " " + existenteHC.getApellidos() + ".\n\n"
                    + "Cada paciente debe tener un número de historia clínica único.",
                    "Historia clínica duplicada", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar menor de edad con apoderado
        if (esMenorDeEdad(paciente.getFechaNacimiento()) && paciente.getApoderado() == null) {
            JOptionPane.showMessageDialog(null,
                    "El paciente es menor de 18 años.\n"
                    + "Por ley, los menores de edad deben contar con un apoderado o tutor responsable.\n\n"
                    + "Por favor, complete los datos del apoderado en la sección correspondiente.",
                    "Apoderado requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar apoderado si se proporciona
        if (paciente.getApoderado() != null) {
            if (!validarApoderado(paciente.getApoderado(), paciente)) {
                return false;
            }
            // Verificar si el apoderado ya existe por DNI
            Apoderado apodExistente = ApoderadoDAO.buscarPorId(paciente.getApoderado().getIdApoderado());
            if (apodExistente == null) {
                // Insertar nuevo apoderado
                if (!ApoderadoDAO.insertar(paciente.getApoderado())) {
                    JOptionPane.showMessageDialog(null,
                            "Error al registrar el apoderado.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        // Validar seguro si se proporciona
        if (paciente.getSeguroMedico() != null) {
            if (!validarSeguroMedico(paciente.getSeguroMedico())) {
                return false;
            }
        }

        boolean exito = PacienteDAO.insertar(paciente);
        if (exito) {
            try {
                AuditoriaLOG.registrarAuditoria(
                        SesionUsuario.getInstance().getIdUsuario(),
                        "Pacientes",
                        "Registró al paciente DNI: " + paciente.getDni()
                );
            } catch (Exception e) {
                System.err.println("Error al registrar la auditoría de Pacientes: " + e.getMessage());
            }
            JOptionPane.showMessageDialog(null,
                    "El paciente " + paciente.getNombres() + " " + paciente.getApellidos()
                    + " ha sido registrado correctamente.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "No se pudo registrar el paciente. Verifique la conexión a la base de datos e intente nuevamente.",
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
        }
        return exito;
    }

    public static boolean actualizarPaciente(Paciente paciente) {
        if (!validarDatosCompletos(paciente)) {
            return false;
        }

        if (esMenorDeEdad(paciente.getFechaNacimiento()) && paciente.getApoderado() == null) {
            JOptionPane.showMessageDialog(null,
                    "El paciente es menor de 18 años y no tiene apoderado registrado.\n"
                    + "Debe asignar un apoderado o tutor responsable antes de guardar los cambios.",
                    "Apoderado requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar apoderado si se proporciona
        if (paciente.getApoderado() != null) {
            if (!validarApoderado(paciente.getApoderado(), paciente)) {
                return false;
            }
        }

        // Validar seguro si se proporciona
        if (paciente.getSeguroMedico() != null) {
            if (!validarSeguroMedico(paciente.getSeguroMedico())) {
                return false;
            }
        }

        boolean exito = PacienteDAO.actualizar(paciente);
        if (exito) {
            try {
                AuditoriaLOG.registrarAuditoria(
                        SesionUsuario.getInstance().getIdUsuario(),
                        "Pacientes",
                        "Actualizó al paciente DNI: " + paciente.getDni()
                );
            } catch (Exception e) {
                System.err.println("Error al registrar la auditoría de Pacientes: " + e.getMessage());
            }
            JOptionPane.showMessageDialog(null,
                    "Los datos de " + paciente.getNombres() + " " + paciente.getApellidos()
                    + " se han actualizado correctamente.",
                    "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "No se pudieron guardar los cambios. Verifique la conexión e intente nuevamente.",
                    "Error al actualizar", JOptionPane.ERROR_MESSAGE);
        }
        return exito;
    }

    public static Paciente buscarPorDni(String dni) {
        return buscarPorDni(dni, true);
    }

    public static Paciente buscarPorDni(String dni, boolean soloActivos) {
        if (dni == null || dni.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un DNI para buscar.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!PATRON_DNI.matcher(dni).matches()) {
            JOptionPane.showMessageDialog(null, "El DNI debe tener exactamente 8 dígitos numéricos.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Paciente paciente = PacienteDAO.buscarPorDni(dni, soloActivos);
        if (paciente == null) {
            String msg = soloActivos 
                ? "No se encontró ningún paciente ACTIVO con el DNI: " + dni + ".\nVerifique que el DNI sea correcto e intente nuevamente."
                : "No se encontró ningún paciente con el DNI: " + dni + ".\nVerifique que el DNI sea correcto e intente nuevamente.";
            JOptionPane.showMessageDialog(null, msg, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
        return paciente;
    }

    public static List<Paciente> buscarPorNombreOApellido(String texto) {
        return buscarPorNombreOApellido(texto, true);
    }

    public static List<Paciente> buscarPorNombreOApellido(String texto, boolean soloActivos) {
        if (texto == null || texto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un nombre o apellido para buscar.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return List.of();
        }

        List<Paciente> resultado = PacienteDAO.buscarPorNombre(texto.trim(), soloActivos);
        if (resultado.isEmpty()) {
            String msg = soloActivos
                ? "No se encontraron pacientes ACTIVOS con el nombre o apellido: \"" + texto + "\".\nIntente con un término de búsqueda diferente."
                : "No se encontraron pacientes con el nombre o apellido: \"" + texto + "\".\nIntente con un término de búsqueda diferente.";
            JOptionPane.showMessageDialog(null, msg, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
        return resultado;
    }

    public static Paciente buscarPorHistoriaClinica(String numeroHistoriaClinica) {
        return buscarPorHistoriaClinica(numeroHistoriaClinica, true);
    }

    public static Paciente buscarPorHistoriaClinica(String numeroHistoriaClinica, boolean soloActivos) {
        if (numeroHistoriaClinica == null || numeroHistoriaClinica.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un número de historia clínica.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!PATRON_HISTORIA_CLINICA.matcher(numeroHistoriaClinica).matches()) {
            JOptionPane.showMessageDialog(null, "El número de historia clínica debe tener exactamente 8 dígitos numéricos.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Paciente paciente = PacienteDAO.buscarPorHistoriaClinica(numeroHistoriaClinica, soloActivos);
        if (paciente == null) {
            String msg = soloActivos
                ? "No se encontró ningún paciente ACTIVO con la historia clínica Nro. " + numeroHistoriaClinica + ".\nVerifique el número e intente nuevamente."
                : "No se encontró ningún paciente con la historia clínica Nro. " + numeroHistoriaClinica + ".\nVerifique el número e intente nuevamente.";
            JOptionPane.showMessageDialog(null, msg, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
        return paciente;
    }

    public static boolean inactivarPaciente(int idPaciente) {
        boolean exito = PacienteDAO.eliminarLogico(idPaciente);
        if (exito) {
            try {
                AuditoriaLOG.registrarAuditoria(
                        SesionUsuario.getInstance().getIdUsuario(),
                        "Pacientes",
                        "Inactivó al paciente ID: " + idPaciente
                );
            } catch (Exception e) {
                System.err.println("Error al registrar la auditoría de Pacientes: " + e.getMessage());
            }
            JOptionPane.showMessageDialog(null,
                    "El paciente ha sido marcado como inactivo.\n"
                    + "Ya no aparecerá en las búsquedas activas del sistema.",
                    "Paciente inactivado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "No se pudo inactivar al paciente. Verifique la conexión e intente nuevamente.",
                    "Error al inactivar", JOptionPane.ERROR_MESSAGE);
        }
        return exito;
    }

    public static boolean activarPaciente(int idPaciente) {
        boolean exito = PacienteDAO.activarLogico(idPaciente);
        if (exito) {
            try {
                AuditoriaLOG.registrarAuditoria(
                        SesionUsuario.getInstance().getIdUsuario(),
                        "Pacientes",
                        "Reactivó al paciente ID: " + idPaciente
                );
            } catch (Exception e) {
                System.err.println("Error al registrar la auditoría de Pacientes: " + e.getMessage());
            }
            JOptionPane.showMessageDialog(null,
                    "El paciente ha sido reactivado correctamente.\n"
                    + "Ahora aparecerá en las búsquedas activas del sistema.",
                    "Paciente activado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "No se pudo activar al paciente. Verifique la conexión e intente nuevamente.",
                    "Error al activar", JOptionPane.ERROR_MESSAGE);
        }
        return exito;
    }

    public static List<Paciente> listarPacientes() {
        return PacienteDAO.listarTodos();
    }

    private static boolean esMenorDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return false;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18;
    }

    private static boolean validarDatosCompletos(Paciente paciente) {
        StringBuilder errores = new StringBuilder();

        // DNI
        if (paciente.getDni() == null || paciente.getDni().trim().isEmpty()) {
            errores.append("- El DNI es obligatorio.\n");
        } else if (!PATRON_DNI.matcher(paciente.getDni()).matches()) {
            errores.append("- El DNI debe tener exactamente 8 dígitos numéricos.\n");
        }

        // Nombres
        if (paciente.getNombres() == null || paciente.getNombres().trim().isEmpty()) {
            errores.append("- Los nombres son obligatorios.\n");
        } else if (!PATRON_NOMBRES.matcher(paciente.getNombres().trim()).matches()) {
            errores.append("- Los nombres solo pueden contener letras y espacios.\n");
        }

        // Apellidos
        if (paciente.getApellidos() == null || paciente.getApellidos().trim().isEmpty()) {
            errores.append("- Los apellidos son obligatorios.\n");
        } else if (!PATRON_NOMBRES.matcher(paciente.getApellidos().trim()).matches()) {
            errores.append("- Los apellidos solo pueden contener letras y espacios.\n");
        }

        // Fecha de nacimiento
        if (paciente.getFechaNacimiento() == null) {
            errores.append("- La fecha de nacimiento es obligatoria.\n");
        } else if (paciente.getFechaNacimiento().isAfter(LocalDate.now())) {
            errores.append("- La fecha de nacimiento no puede ser futura.\n");
        }

        // Sexo
        if (paciente.getSexo() == null || paciente.getSexo().trim().isEmpty()) {
            errores.append("- El sexo es obligatorio.\n");
        } else if (!PATRON_SEXO.matcher(paciente.getSexo().trim().toUpperCase()).matches()) {
            errores.append("- El sexo debe ser 'M' o 'F'.\n");
        }

        // Teléfono
        if (paciente.getTelefono() == null || paciente.getTelefono().trim().isEmpty()) {
            errores.append("- El teléfono es obligatorio.\n");
        } else if (!PATRON_TELEFONO.matcher(paciente.getTelefono().trim()).matches()) {
            errores.append("- El teléfono debe tener exactamente 9 dígitos numéricos.\n");
        }

        // Dirección
        if (paciente.getDireccion() == null || paciente.getDireccion().trim().isEmpty()) {
            errores.append("- La dirección es obligatoria.\n");
        }

        // Número historia clínica
        if (paciente.getNumeroHistoriaClinica() == null || paciente.getNumeroHistoriaClinica().trim().isEmpty()) {
            errores.append("- El número de historia clínica es obligatorio.\n");
        } else if (!PATRON_HISTORIA_CLINICA.matcher(paciente.getNumeroHistoriaClinica()).matches()) {
            errores.append("- El número de historia clínica debe tener exactamente 8 dígitos numéricos.\n");
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "Los siguientes campos son obligatorios o tienen formato inválido:\n\n" + errores.toString(),
                    "Validación de datos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private static boolean validarApoderado(Apoderado apoderado, Paciente paciente) {
        StringBuilder errores = new StringBuilder();

        if (apoderado.getDni() == null || apoderado.getDni().trim().isEmpty()) {
            errores.append("- El DNI del apoderado es obligatorio.\n");
        } else if (!PATRON_DNI.matcher(apoderado.getDni()).matches()) {
            errores.append("- El DNI del apoderado debe tener exactamente 8 dígitos numéricos.\n");
        }

        if (apoderado.getNombres() == null || apoderado.getNombres().trim().isEmpty()) {
            errores.append("- Los nombres del apoderado son obligatorios.\n");
        } else if (!PATRON_NOMBRES.matcher(apoderado.getNombres().trim()).matches()) {
            errores.append("- Los nombres del apoderado solo pueden contener letras y espacios.\n");
        }

        if (apoderado.getApellidos() == null || apoderado.getApellidos().trim().isEmpty()) {
            errores.append("- Los apellidos del apoderado son obligatorios.\n");
        } else if (!PATRON_NOMBRES.matcher(apoderado.getApellidos().trim()).matches()) {
            errores.append("- Los apellidos del apoderado solo pueden contener letras y espacios.\n");
        }

        if (apoderado.getTelefono() == null || apoderado.getTelefono().trim().isEmpty()) {
            errores.append("- El teléfono del apoderado es obligatorio.\n");
        } else if (!PATRON_TELEFONO.matcher(apoderado.getTelefono().trim()).matches()) {
            errores.append("- El teléfono del apoderado debe tener exactamente 9 dígitos numéricos.\n");
        }

        if (apoderado.getParentesco() == null || apoderado.getParentesco().trim().isEmpty()) {
            errores.append("- El parentesco es obligatorio.\n");
        } else if (!PATRON_PARENTESCO.matcher(apoderado.getParentesco().trim()).matches()) {
            errores.append("- El parentesco solo puede contener letras y espacios.\n");
        }

        // Verificar si el apoderado está registrado como paciente
        if (paciente != null && apoderado.getDni() != null && !apoderado.getDni().trim().isEmpty()) {
            Paciente apoderadoComoPaciente = PacienteDAO.buscarPorDni(apoderado.getDni(), false);
            if (apoderadoComoPaciente != null) {
                if (esMenorDeEdad(apoderadoComoPaciente.getFechaNacimiento())) {
                    errores.append("- El apoderado está registrado como paciente y es menor de edad. Debe ser mayor de 18 años.\n");
                }
                // Verificar que no sea el mismo paciente
                if (apoderadoComoPaciente.getIdPaciente() == paciente.getIdPaciente()) {
                    errores.append("- El apoderado no puede ser el mismo paciente.\n");
                }
            }
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "Datos del apoderado inválidos:\n\n" + errores.toString(),
                    "Validación de apoderado", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private static boolean validarSeguroMedico(SeguroMedico seguro) {
        StringBuilder errores = new StringBuilder();

        if (seguro.getCompania() == null || seguro.getCompania().trim().isEmpty()) {
            errores.append("- La compañía del seguro es obligatoria.\n");
        }

        if (seguro.getNumeroPoliza() == null || seguro.getNumeroPoliza().trim().isEmpty()) {
            errores.append("- El número de póliza es obligatorio.\n");
        } else if (!PATRON_POLIZA.matcher(seguro.getNumeroPoliza().trim()).matches()) {
            errores.append("- El número de póliza debe tener exactamente 10 dígitos numéricos.\n");
        }

        if (seguro.getTipoCobertura() == null || seguro.getTipoCobertura().trim().isEmpty()) {
            errores.append("- El tipo de cobertura es obligatorio.\n");
        } else if (!PATRON_COBERTURA.matcher(seguro.getTipoCobertura().trim().toUpperCase()).matches()) {
            errores.append("- La cobertura debe ser 'T' (Total) o 'P' (Parcial).\n");
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "Datos del seguro inválidos:\n\n" + errores.toString(),
                    "Validación de seguro", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}