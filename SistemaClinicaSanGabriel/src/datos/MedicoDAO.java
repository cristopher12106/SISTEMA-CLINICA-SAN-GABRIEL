/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import entidades.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
 
//@Harold

public class MedicoDAO {

    public static boolean registrarMedico(Medico medico){
        String sql = "INSERT INTO Medicos (idUsuario, codigo, colegiatura, dni, nombres, apellidos, telefono, correo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int filasAfectadas = 0;
        Connection cn = ConexionBD.getInstancia().getConexion();

        try(PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, medico.getIdUsuario());
            ps.setString(2, medico.getCodigo());
            ps.setString(3, medico.getColegiatura());
            ps.setString(4, medico.getDni());
            ps.setString(5, medico.getNombres());
            ps.setString(6, medico.getApellidos());
            ps.setString(7, medico.getTelefono());
            ps.setString(8, medico.getCorreo());

            filasAfectadas = ps.executeUpdate();

            if(filasAfectadas > 0){
                try(ResultSet rs = ps.getGeneratedKeys()){
                    if(rs.next()){
                        int idMedico = rs.getInt(1);
                        for(Especialidad esp : medico.getEspecialidades()){
                            asignarEspecialidad(idMedico, obtenerIdEspecialidadPorNombre(esp.getNombre()));
                        }
                    }
                }
            }
        } catch (SQLException e){
            System.err.println("Error al registrar medico: " + e.getMessage());
        }
        return filasAfectadas > 0;
    }

    public static void asignarEspecialidad(int idMedico, int idEspecialidad){
        String sql = "INSERT INTO Medico_Especialidad (idMedico, idEspecialidad) VALUES (?, ?)";
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setInt(1, idMedico);
            ps.setInt(2, idEspecialidad);
            ps.executeUpdate();
        } catch (SQLException e){
            System.err.println("Error al asignar especialidad: " + e.getMessage());
        }
    }

    // RN-13: colegiatura unica
    public static boolean existeColegiatura(String colegiatura){
        String sql = "SELECT COUNT(*) AS existe FROM Medicos WHERE colegiatura = ?";
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, colegiatura);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("existe") > 0;
            }
        } catch (SQLException e){
            System.err.println("Error al validar colegiatura: " + e.getMessage());
        }
        return false;
    }

    public static ArrayList<Medico> listarMedicos(){
        String sql = "SELECT * FROM Medicos";
        ArrayList<Medico> lista = new ArrayList<>();
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Medico medico = new Medico();
                medico.setCodigo(rs.getString("codigo"));
                medico.setColegiatura(rs.getString("colegiatura"));
                medico.setDni(rs.getString("dni"));
                medico.setNombres(rs.getString("nombres"));
                medico.setApellidos(rs.getString("apellidos"));
                medico.setTelefono(rs.getString("telefono"));
                medico.setCorreo(rs.getString("correo"));
                medico.setEspecialidades(listarEspecialidadesPorMedico(rs.getInt("idMedico")));
                lista.add(medico);
            }
        } catch (SQLException e){
            System.err.println("Error al listar medicos: " + e.getMessage());
        }
        return lista;
    }

    public static ArrayList<Especialidad> listarEspecialidadesPorMedico(int idMedico){
        String sql = "SELECT e.* FROM Especialidades e "
                    + "INNER JOIN Medico_Especialidad me ON e.idEspecialidad = me.idEspecialidad "
                    + "WHERE me.idMedico = ?";
        ArrayList<Especialidad> lista = new ArrayList<>();
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setInt(1, idMedico);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Especialidad esp = new Especialidad();
                esp.setCodigo(rs.getString("codigo"));
                esp.setNombre(rs.getString("nombre"));
                esp.setDescripcion(rs.getString("descripcion"));
                lista.add(esp);
            }
        } catch (SQLException e){
            System.err.println("Error al listar especialidades del medico: " + e.getMessage());
        }
        return lista;
    }

    public static ArrayList<Especialidad> listarEspecialidades(){
        String sql = "SELECT * FROM Especialidades";
        ArrayList<Especialidad> lista = new ArrayList<>();
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Especialidad esp = new Especialidad();
                esp.setCodigo(rs.getString("codigo"));
                esp.setNombre(rs.getString("nombre"));
                esp.setDescripcion(rs.getString("descripcion"));
                lista.add(esp);
            }
        } catch (SQLException e){
            System.err.println("Error al listar especialidades: " + e.getMessage());
        }
        return lista;
    }

    public static boolean registrarEspecialidad(Especialidad especialidad){
        String sql = "INSERT INTO Especialidades (codigo, nombre, descripcion) VALUES (?, ?, ?)";
        int filasAfectadas = 0;
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, especialidad.getCodigo());
            ps.setString(2, especialidad.getNombre());
            ps.setString(3, especialidad.getDescripcion());
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e){
            System.err.println("Error al registrar especialidad: " + e.getMessage());
        }
        return filasAfectadas > 0;
    }

    public static int obtenerIdMedico(String codigo){
        String sql = "SELECT idMedico FROM Medicos WHERE codigo = ?";
        int id = -1;
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                id = rs.getInt("idMedico");
            }
        } catch (SQLException e){
            System.err.println("Error al obtener id de medico: " + e.getMessage());
        }
        return id;
    }

    public static int obtenerIdEspecialidadPorNombre(String nombre){
        String sql = "SELECT idEspecialidad FROM Especialidades WHERE nombre = ?";
        int id = -1;
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
         ps.setString(1, nombre);
         ResultSet rs = ps.executeQuery();
         if(rs.next()){
             id = rs.getInt("idEspecialidad");
         }
        } catch (SQLException e){
         System.err.println("Error al obtener id de especialidad: " + e.getMessage());
        }
        return id;
    }

    public static boolean actualizarMedico(Medico medico){
        String sql = "UPDATE Medicos SET dni = ?, nombres = ?, apellidos = ?, telefono = ?, correo = ? "
                    + "WHERE codigo = ?";
        int filasAfectadas = 0;
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, medico.getDni());
            ps.setString(2, medico.getNombres());
            ps.setString(3, medico.getApellidos());
            ps.setString(4, medico.getTelefono());
            ps.setString(5, medico.getCorreo());
            ps.setString(6, medico.getCodigo());
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e){
            System.err.println("Error al actualizar medico: " + e.getMessage());
        }
        return filasAfectadas > 0;
    }

    public static boolean eliminarMedico(String codigo){
        String sql = "DELETE FROM Medicos WHERE codigo = ?";
        int filasAfectadas = 0;
        Connection cn = ConexionBD.getInstancia().getConexion();
        try(PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, codigo);
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e){
            System.err.println("Error al eliminar medico: " + e.getMessage());
        }
        return filasAfectadas > 0;
    }
}
