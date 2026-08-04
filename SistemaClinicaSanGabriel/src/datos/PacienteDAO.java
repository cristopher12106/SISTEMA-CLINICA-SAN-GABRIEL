package datos;

import entidades.Apoderado;
import entidades.Paciente;
import entidades.SeguroMedico;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public static boolean insertar(Paciente paciente) {
        String sql = "INSERT INTO paciente (dni, nombres, apellidos, fecha_nacimiento, sexo, telefono, "
                + "direccion, numero_historia_clinica, id_seguro, id_apoderado, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int filasAfectadas = 0;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, paciente.getDni());
            ps.setString(2, paciente.getNombres());
            ps.setString(3, paciente.getApellidos());
            ps.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            ps.setString(5, paciente.getSexo());
            ps.setString(6, paciente.getTelefono());
            ps.setString(7, paciente.getDireccion());
            ps.setString(8, paciente.getNumeroHistoriaClinica());

            if (paciente.getSeguroMedico() != null) {
                ps.setInt(9, paciente.getSeguroMedico().getIdSeguro());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            if (paciente.getApoderado() != null) {
                ps.setInt(10, paciente.getApoderado().getIdApoderado());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            ps.setBoolean(11, paciente.isEstado());
            filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        paciente.setIdPaciente(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar paciente: " + e.getMessage());
        }

        return filasAfectadas > 0;
    }

    public static boolean actualizar(Paciente paciente) {
        String sql = "UPDATE paciente SET dni=?, nombres=?, apellidos=?, fecha_nacimiento=?, sexo=?, "
                + "telefono=?, direccion=?, numero_historia_clinica=?, id_seguro=?, id_apoderado=? "
                + "WHERE id_paciente=?";
        int filasAfectadas = 0;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, paciente.getDni());
            ps.setString(2, paciente.getNombres());
            ps.setString(3, paciente.getApellidos());
            ps.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            ps.setString(5, paciente.getSexo());
            ps.setString(6, paciente.getTelefono());
            ps.setString(7, paciente.getDireccion());
            ps.setString(8, paciente.getNumeroHistoriaClinica());

            if (paciente.getSeguroMedico() != null) {
                ps.setInt(9, paciente.getSeguroMedico().getIdSeguro());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            if (paciente.getApoderado() != null) {
                ps.setInt(10, paciente.getApoderado().getIdApoderado());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            ps.setInt(11, paciente.getIdPaciente());
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
        }

        return filasAfectadas > 0;
    }

    public static boolean eliminarLogico(int idPaciente) {
        String sql = "UPDATE paciente SET estado = false WHERE id_paciente = ?";
        int filasAfectadas = 0;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al inactivar paciente: " + e.getMessage());
        }

        return filasAfectadas > 0;
    }

    public static boolean activarLogico(int idPaciente) {
        String sql = "UPDATE paciente SET estado = true WHERE id_paciente = ?";
        int filasAfectadas = 0;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al activar paciente: " + e.getMessage());
        }

        return filasAfectadas > 0;
    }

    public static Paciente buscarPorId(int idPaciente) {
        String sql = "SELECT * FROM paciente WHERE id_paciente = ?";
        Object[] datos = null;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    datos = leerDatosBasicos(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente por id: " + e.getMessage());
        }

        if (datos == null) return null;
        return construirPaciente(datos);
    }

    public static Paciente buscarPorDni(String dni) {
        return buscarPorDni(dni, true);
    }

    public static Paciente buscarPorDni(String dni, boolean soloActivos) {
        String sql = "SELECT * FROM paciente WHERE dni = ?";
        if (soloActivos) sql += " AND estado = 1";
        return buscarUno(sql, dni);
    }

    public static Paciente buscarPorHistoriaClinica(String numeroHistoriaClinica) {
        return buscarPorHistoriaClinica(numeroHistoriaClinica, true);
    }

    public static Paciente buscarPorHistoriaClinica(String numeroHistoriaClinica, boolean soloActivos) {
        String sql = "SELECT * FROM paciente WHERE numero_historia_clinica = ?";
        if (soloActivos) sql += " AND estado = 1";
        return buscarUno(sql, numeroHistoriaClinica);
    }

    public static List<Paciente> buscarPorNombre(String nombreOApellido) {
        return buscarPorNombre(nombreOApellido, true);
    }

    public static List<Paciente> buscarPorNombre(String nombreOApellido, boolean soloActivos) {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM paciente WHERE (nombres LIKE ? OR apellidos LIKE ?)";
        if (soloActivos) sql += " AND estado = 1";

        List<Object[]> datosBasicos = new ArrayList<>();

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String like = "%" + nombreOApellido + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    datosBasicos.add(leerDatosBasicos(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar pacientes por nombre: " + e.getMessage());
        }

        for (Object[] d : datosBasicos) {
            lista.add(construirPaciente(d));
        }

        return lista;
    }

    public static List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM paciente";

        List<Object[]> datosBasicos = new ArrayList<>();

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                datosBasicos.add(leerDatosBasicos(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pacientes: " + e.getMessage());
        }

        for (Object[] d : datosBasicos) {
            lista.add(construirPaciente(d));
        }

        return lista;
    }

    private static Paciente buscarUno(String sql, String parametro) {
        Object[] datos = null;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    datos = leerDatosBasicos(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
        }

        if (datos == null) return null;
        return construirPaciente(datos);
    }

    private static Object[] leerDatosBasicos(ResultSet rs) throws SQLException {
        int idPaciente = rs.getInt("id_paciente");
        String dni = rs.getString("dni");
        String nombres = rs.getString("nombres");
        String apellidos = rs.getString("apellidos");
        LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento").toLocalDate();
        String sexo = rs.getString("sexo");
        String telefono = rs.getString("telefono");
        String direccion = rs.getString("direccion");
        String numeroHistoriaClinica = rs.getString("numero_historia_clinica");
        int idSeguro = rs.getInt("id_seguro");
        boolean nuloSeguro = rs.wasNull();
        int idApoderado = rs.getInt("id_apoderado");
        boolean nuloApoderado = rs.wasNull();
        boolean estado = rs.getBoolean("estado");

        return new Object[]{idPaciente, dni, nombres, apellidos, fechaNacimiento,
                sexo, telefono, direccion, numeroHistoriaClinica,
                idSeguro, nuloSeguro, idApoderado, nuloApoderado, estado};
    }

    private static Paciente construirPaciente(Object[] d) {
        SeguroMedico seguro = null;
        if (!(boolean) d[10]) {
            seguro = SeguroDAO.buscarPorId((int) d[9]);
        }

        Apoderado apoderado = null;
        if (!(boolean) d[12]) {
            apoderado = buscarApoderadoPorId((int) d[11]);
        }

        return new Paciente.Builder()
                .idPaciente((int) d[0])
                .dni((String) d[1])
                .nombres((String) d[2])
                .apellidos((String) d[3])
                .fechaNacimiento((LocalDate) d[4])
                .sexo((String) d[5])
                .telefono((String) d[6])
                .direccion((String) d[7])
                .numeroHistoriaClinica((String) d[8])
                .seguroMedico(seguro)
                .apoderado(apoderado)
                .estado((boolean) d[13])
                .build();
    }

    private static Apoderado buscarApoderadoPorId(int idApoderado) {
        String sql = "SELECT * FROM apoderado WHERE id_apoderado = ?";
        Apoderado apoderado = null;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idApoderado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    apoderado = new Apoderado(
                            rs.getInt("id_apoderado"),
                            rs.getString("dni"),
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getString("telefono"),
                            rs.getString("parentesco"),
                            rs.getBoolean("estado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar apoderado: " + e.getMessage());
        }

        return apoderado;
    }
}
