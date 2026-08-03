package presentacion;

import datos.ApoderadoDAO;
import datos.AtencionMedicaDAO;
import datos.MedicamentoDAO;
import datos.SeguroDAO;
import datos.SesionUsuario;
import entidades.Apoderado;
import entidades.AtencionMedica;
import entidades.Cita;
import entidades.Diagnostico;
import entidades.Especialidad;
import entidades.HorarioMedico;
import entidades.Medicamento;
import entidades.Medico;
import entidades.Paciente;
import entidades.Rol;
import entidades.SeguroMedico;
import entidades.SignosVitales;
import entidades.Usuario;
import logica.CitaLOG;
import logica.HorarioLOG;
import logica.MedicoLOG;
import logica.PacienteLOG;
import logica.UsuarioLOG;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DatosDePrueba {

    private static SeguroMedico SEGURO_PRUEBA;
    private static Apoderado APODERADO_PRUEBA;

    public static void main(String[] args) {
        cargarMedicamentos();
    }
    //1.USUARIOS
    public static void cargarUsuarios() {
        SesionUsuario.getInstance().iniciarSesion(new Usuario("admin", "%Admin2026", Rol.ADMINISTRADOR, true));
        // ADMINISTRADOR (3: 2 + 1 extra)
        UsuarioLOG.registrarUsuario("admin", "%Admin2026", Rol.ADMINISTRADOR, true);
        UsuarioLOG.registrarUsuario("mrodriguez", "#ModAdmin2", Rol.ADMINISTRADOR, true);
        UsuarioLOG.registrarUsuario("gcastillo", "@Admin789", Rol.ADMINISTRADOR, true);

        // RECEPCIONISTA (3: 2 + 1 extra)
        UsuarioLOG.registrarUsuario("mgarcia", "$Recep123", Rol.RECEPCIONISTA, true);
        UsuarioLOG.registrarUsuario("cpenai", "!Recep456", Rol.RECEPCIONISTA, true);
        UsuarioLOG.registrarUsuario("svaldez", "&Recep789", Rol.RECEPCIONISTA, true);

        // MEDICO (10)
        UsuarioLOG.registrarUsuario("jandrade", "*Doctor12", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("sromero", "%Doctor34", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("vhurtado", "?Doctor56", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("lrojas", "@Doctor90", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("msalazar", "#Doctor11", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("cmendoza", "$Doctor22", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("ecabrera", "%Doctor33", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("arivas", "!Doctor44", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("nfloresm", "&Doctor55", Rol.MEDICO, true);
        UsuarioLOG.registrarUsuario("jcornejo", "?Doctor66", Rol.MEDICO, true);

        // ENFERMERA (2)
        UsuarioLOG.registrarUsuario("lflores", ".Enfer901", Rol.ENFERMERA, true);
        UsuarioLOG.registrarUsuario("rtorres", "+Enfer234", Rol.ENFERMERA, true);

        // LABORATORISTA (2)
        UsuarioLOG.registrarUsuario("pcastro", "-Lab2026!", Rol.LABORATORISTA, true);
        UsuarioLOG.registrarUsuario("knoa", "_Lab2026?", Rol.LABORATORISTA, true);

        // FARMACEUTICO (3: 2 + 1 extra)
        UsuarioLOG.registrarUsuario("dchavez", "Farma#123", Rol.FARMACEUTICO, true);
        UsuarioLOG.registrarUsuario("yquispe", "Farma$456", Rol.FARMACEUTICO, true);
        UsuarioLOG.registrarUsuario("maguilar", "Farma@789", Rol.FARMACEUTICO, true);

        // CAJERO (2)
        UsuarioLOG.registrarUsuario("aespinoza", "Caja%7890", Rol.CAJERO, true);
        UsuarioLOG.registrarUsuario("fcampos", "Caja!1122", Rol.CAJERO, true);

        // DIRECTOR_MEDICO (2)
        UsuarioLOG.registrarUsuario("director", "Direct#10", Rol.DIRECTOR_MEDICO, true);
        UsuarioLOG.registrarUsuario("mherrera", "DirMed@11", Rol.DIRECTOR_MEDICO, true);

        SesionUsuario.getInstance().cerrarSesion();
    }

    //2.PACIENTES
    public static void cargarPacientes() {
        Paciente.Builder primerPaciente = new Paciente.Builder()
                .dni("12345678").nombres("Juan Carlos").apellidos("Perez Gomez")
                .fechaNacimiento(LocalDate.of(1985, 3, 15)).sexo("M")
                .telefono("987654321").direccion("Av. Los Olivos 123, Trujillo")
                .numeroHistoriaClinica("10000001");
        if (SEGURO_PRUEBA != null) {
            primerPaciente.seguroMedico(SEGURO_PRUEBA);
        }
        if (APODERADO_PRUEBA != null) {
            primerPaciente.apoderado(APODERADO_PRUEBA);
        }
        PacienteLOG.registrarPaciente(primerPaciente.build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("23456789").nombres("Maria Fernanda").apellidos("Lopez Diaz")
                .fechaNacimiento(LocalDate.of(1990, 7, 22)).sexo("F")
                .telefono("976543210").direccion("Jr. La Merced 456, Trujillo")
                .numeroHistoriaClinica("10000002").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("34567890").nombres("Carlos Alberto").apellidos("Ramirez Torres")
                .fechaNacimiento(LocalDate.of(1978, 11, 3)).sexo("M")
                .telefono("965432109").direccion("Urb. El Molino Mz B Lt 8, Trujillo")
                .numeroHistoriaClinica("10000003").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("45678901").nombres("Ana Lucia").apellidos("Chavez Medina")
                .fechaNacimiento(LocalDate.of(1995, 1, 28)).sexo("F")
                .telefono("954321098").direccion("Av. America Sur 789, Trujillo")
                .numeroHistoriaClinica("10000004").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("56789012").nombres("Luis Miguel").apellidos("Fernandez Rojas")
                .fechaNacimiento(LocalDate.of(1982, 9, 10)).sexo("M")
                .telefono("943210987").direccion("Psj. Los Jardines 234, Trujillo")
                .numeroHistoriaClinica("10000005").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("67890123").nombres("Rosa Elena").apellidos("Sanchez Vargas")
                .fechaNacimiento(LocalDate.of(1992, 5, 19)).sexo("F")
                .telefono("932109876").direccion("Calle San Martin 567, Trujillo")
                .numeroHistoriaClinica("10000006").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("78901234").nombres("Pedro Andres").apellidos("Castro Huaman")
                .fechaNacimiento(LocalDate.of(1975, 12, 1)).sexo("M")
                .telefono("921098765").direccion("Av. Victor Larco 890, Trujillo")
                .numeroHistoriaClinica("10000007").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("89012345").nombres("Carmen Rosa").apellidos("Flores Quispe")
                .fechaNacimiento(LocalDate.of(1988, 4, 14)).sexo("F")
                .telefono("910987654").direccion("Jr. Bolivar 321, Trujillo")
                .numeroHistoriaClinica("10000008").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("90123456").nombres("Jose Antonio").apellidos("Salazar Mendoza")
                .fechaNacimiento(LocalDate.of(1998, 8, 30)).sexo("M")
                .telefono("909876543").direccion("Urb. San Andres Mz C Lt 12, Trujillo")
                .numeroHistoriaClinica("10000009").build());

        PacienteLOG.registrarPaciente(new Paciente.Builder()
                .dni("01234567").nombres("Lucia Ines").apellidos("Gutierrez Paredes")
                .fechaNacimiento(LocalDate.of(1993, 6, 25)).sexo("F")
                .telefono("998765432").direccion("Av. España 654, Trujillo")
                .numeroHistoriaClinica("10000010").build());
    }
    public static void cargarSeguroYApoderado() {
        SeguroMedico seguro = new SeguroMedico(0, "Rimac EPS", "1000000001", "T", true);
        if (SeguroDAO.insertar(seguro)) {
            SEGURO_PRUEBA = seguro;
        }

        Apoderado apoderado = new Apoderado(0, "87654321", "Carmen Rosa", "Gutierrez Lopez", "987654321", "Madre", true);
        if (ApoderadoDAO.insertar(apoderado)) {
            APODERADO_PRUEBA = apoderado;
        }
    }

    //3.MEDICOS, CITA Y HORARIOS
    public static void cargarEspecialidades() {
        if (!MedicoLOG.listarEspecialidades().isEmpty()) {
            System.out.println("Ya existen especialidades registradas, no se volvieron a crear.");
            return;
        }

        registrarEspecialidad("ESP001", "Medicina General", "Atencion integral de pacientes adultos");
        registrarEspecialidad("ESP002", "Pediatria", "Atencion medica de ninos y adolescentes");
        registrarEspecialidad("ESP003", "Cardiologia", "Prevencion y tratamiento de enfermedades del corazon");
        registrarEspecialidad("ESP004", "Dermatologia", "Diagnostico y tratamiento de enfermedades de la piel");
        registrarEspecialidad("ESP005", "Ginecologia", "Salud integral de la mujer");
    }

    private static void registrarEspecialidad(String codigo, String nombre, String descripcion) {
        MedicoLOG.registrarEspecialidad(new Especialidad(codigo, nombre, descripcion));
    }

    public static void cargarMedicos() {
        List<Especialidad> especialidadesDisponibles = MedicoLOG.listarEspecialidades();
        if (especialidadesDisponibles.isEmpty()) {
            System.out.println("No hay especialidades registradas. Los medicos no se podran registrar.");
            return;
        }

        registrarMedico("jandrade", "10001", "CM-10001", "20123456", "Jose Andres", "Jandrade Rios", "987600001", "jandrade@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("sromero", "10002", "CM-10002", "20123457", "Sofia", "Romero Vega", "987600002", "sromero@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("vhurtado", "10003", "CM-10003", "20123458", "Victor", "Hurtado Luna", "987600003", "vhurtado@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("lrojas", "10004", "CM-10004", "20123459", "Luis", "Rojas Campos", "987600004", "lrojas@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("msalazar", "10005", "CM-10005", "20123460", "Marta", "Salazar Diaz", "987600005", "msalazar@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("cmendoza", "10006", "CM-10006", "20123461", "Carlos", "Mendoza Gil", "987600006", "cmendoza@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("ecabrera", "10007", "CM-10007", "20123462", "Elena", "Cabrera Soto", "987600007", "ecabrera@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("arivas", "10008", "CM-10008", "20123463", "Alonso", "Rivas Paredes", "987600008", "arivas@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("nfloresm", "10009", "CM-10009", "20123464", "Natalia", "Flores Marin", "987600009", "nfloresm@colegiomedico.pe", especialidadesDisponibles);
        registrarMedico("jcornejo", "10010", "CM-10010", "20123465", "Julio", "Cornejo Roca", "987600010", "jcornejo@colegiomedico.pe", especialidadesDisponibles);
    }
    public static void cargarHorarios() {
        String[] dias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes"};
        for (int i = 1; i <= 10; i++) {
            String codigoMedico = String.format("%05d", i + 10000);
            for (String dia : dias) {
                registrarHorario(codigoMedico, dia, "08:00", "13:00");
            }
        }
    }
    public static void cargarCitas() {
        List<Medico> medicos = MedicoLOG.listarMedicos();
        List<Paciente> pacientes = PacienteLOG.listarPacientes();
        if (medicos.isEmpty() || pacientes.isEmpty()) {
            System.out.println("No hay medicos o pacientes registrados, no se generaron citas.");
            return;
        }

        Random random = new Random();
        int contadorCodigo = 1;
        LocalDate hoy = LocalDate.now();

        for (Medico medico : medicos) {
            List<HorarioMedico> horarios = HorarioLOG.listarHorariosPorMedico(medico);
            if (horarios.isEmpty()) {
                continue;
            }

            int cantidadCitas = 1 + random.nextInt(3);
            for (int i = 0; i < cantidadCitas; i++) {
                for (int intento = 0; intento < 30; intento++) {
                    HorarioMedico horario = horarios.get(random.nextInt(horarios.size()));
                    LocalDate fecha = proximaFechaPorDia(hoy, horario.getDiaSemana());
                    if (fecha == null) {
                        continue;
                    }
                    String hora = horaAleatoriaEnRango(horario.getHoraInicio(), horario.getHoraFin(), random);
                    if (hora == null) {
                        continue;
                    }

                    if (!CitaLOG.consultarDisponibilidad(medico, fecha.toString(), hora)) {
                        continue;
                    }

                    Paciente paciente = pacientes.get(random.nextInt(pacientes.size()));
                    Cita cita = new Cita.CitaBuilder()
                            .setCodigo(String.format("CIT-%04d", contadorCodigo++))
                            .setMedico(medico)
                            .setNumeroHistoriaClinica(paciente.getNumeroHistoriaClinica())
                            .setFecha(fecha.toString())
                            .setHora(hora)
                            .setEstado("Programada")
                            .setObservaciones("Cita generada de prueba")
                            .build();
                    CitaLOG.registrarCita(cita);
                    break;
                }
            }
        }
    }

    //4 y 5.ATENCION MEDICA Y REGISTRO CLINICO, FARMACIO LABORATORIO Y INVENTARIO
    public static void cargarMedicamentos() {
        MedicamentoDAO dao = new MedicamentoDAO();
        try {
            if (dao.listar().isEmpty()) {
                dao.insertar(new Medicamento(0, "Paracetamol 500mg", "Analgesico y antipiretico", 100, 20, 1.50, true));
                dao.insertar(new Medicamento(0, "Ibuprofeno 400mg", "Antiinflamatorio no esteroideo", 80, 15, 2.00, true));
                dao.insertar(new Medicamento(0, "Amoxicilina 500mg", "Antibiotico de amplio espectro", 60, 10, 3.50, true));
                dao.insertar(new Medicamento(0, "Omeprazol 20mg", "Inhibidor de la bomba de protones", 90, 15, 4.20, true));
                dao.insertar(new Medicamento(0, "Losartan 50mg", "Antihipertensivo", 70, 10, 5.00, true));
                dao.insertar(new Medicamento(0, "Metformina 850mg", "Antidiabetico oral", 75, 12, 3.80, true));
                dao.insertar(new Medicamento(0, "Amlodipino 5mg", "Antihipertensivo calcioantagonista", 65, 10, 4.50, true));
                dao.insertar(new Medicamento(0, "Azitromicina 500mg", "Antibiotico macrolido", 50, 8, 6.00, true));
                dao.insertar(new Medicamento(0, "Diclofenaco 50mg", "Antiinflamatorio", 85, 15, 2.50, true));
                dao.insertar(new Medicamento(0, "Loratadina 10mg", "Antihistaminico", 95, 20, 2.20, true));
                dao.insertar(new Medicamento(0, "Salbutamol 100mcg", "Broncodilatador (inhalador)", 30, 5, 12.00, true));
                dao.insertar(new Medicamento(0, "Clorfenamina 4mg", "Antihistaminico", 110, 20, 1.80, true));
                dao.insertar(new Medicamento(0, "Albendazol 400mg", "Antiparasitario", 70, 10, 3.00, true));
                dao.insertar(new Medicamento(0, "Vitamina C 500mg", "Suplemento vitaminico", 120, 25, 1.20, true));
                dao.insertar(new Medicamento(0, "Hierro 100mg", "Suplemento de hierro", 90, 15, 2.00, true));
                dao.insertar(new Medicamento(0, "Acido Folico 1mg", "Suplemento", 80, 15, 1.50, true));
                dao.insertar(new Medicamento(0, "Prednisona 20mg", "Corticosteroide", 45, 8, 3.00, true));
                dao.insertar(new Medicamento(0, "Warfarina 5mg", "Anticoagulante", 25, 5, 7.50, true));
                dao.insertar(new Medicamento(0, "Insulina NPH 100UI/ml", "Antidiabetico", 20, 4, 25.00, true));
                dao.insertar(new Medicamento(0, "Tramadol 50mg", "Analgesico opioide", 40, 6, 8.00, true));
            } else {
                System.out.println("Ya existen medicamentos registrados, no se volvieron a crear.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar medicamentos: " + e.getMessage());
        }
    }










    //FUNCIONES AUXILIARES

    private static void registrarMedico(String username, String codigo, String colegiatura,
            String dni, String nombres, String apellidos, String telefono, String correo,
            List<Especialidad> especialidades) {
        Usuario usuario = UsuarioLOG.buscarUsuario(username);
        if (usuario == null) {
            System.out.println("No se encontro el usuario " + username + ", no se registro su Medico.");
            return;
        }
        List<Especialidad> especialidadesMedico = new ArrayList<>(especialidades);
        Medico medico = new Medico(codigo, usuario.getIdUsuario(), colegiatura, dni,
                nombres, apellidos, telefono, correo, especialidadesMedico);
        MedicoLOG.registrarMedico(medico);
    }



    private static void registrarHorario(String codigoMedico, String diaSemana,
            String horaInicio, String horaFin) {
        Medico medico = new Medico();
        medico.setCodigo(codigoMedico);
        HorarioMedico horario = new HorarioMedico();
        horario.setMedico(medico);
        horario.setDiaSemana(diaSemana);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        HorarioLOG.registrarHorario(horario);
    }



    private static LocalDate proximaFechaPorDia(LocalDate desde, String diaSemana) {
        LocalDate fecha = desde.plusDays(1);
        for (int i = 0; i < 15; i++) {
            if (diaDeLaSemana(fecha).equalsIgnoreCase(diaSemana)) {
                return fecha;
            }
            fecha = fecha.plusDays(1);
        }
        return null;
    }

    private static String diaDeLaSemana(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return switch (dia) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miercoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sabado";
            case SUNDAY -> "Domingo";
        };
    }

    private static String horaAleatoriaEnRango(String horaInicio, String horaFin, Random random) {
        List<String> slots = new ArrayList<>();
        LocalTime actual = LocalTime.parse(horaInicio);
        LocalTime fin = LocalTime.parse(horaFin);
        while (actual.isBefore(fin)) {
            slots.add(actual.toString());
            actual = actual.plusMinutes(30);
        }
        if (slots.isEmpty()) {
            return null;
        }
        return slots.get(random.nextInt(slots.size()));
    }
}
