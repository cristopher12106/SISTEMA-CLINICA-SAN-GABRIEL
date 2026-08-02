/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import entidades.ExamenLaboratorio;
import entidades.ResultadoExamen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class LaboratorioDAO {

    // Registrar una nueva solicitud de examen de laboratorio
    public boolean registrarExamen(ExamenLaboratorio examen) throws SQLException {
        String sql = "INSERT INTO examen_laboratorio (id_paciente, tipo_examen, observaciones, estado) VALUES (?, ?, ?, ?)";
        
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, examen.getIdPaciente());
            ps.setString(2, examen.getTipoExamen());
            ps.setString(3, examen.getObservaciones());
            ps.setString(4, examen.getEstado());
            
            return ps.executeUpdate() > 0;
        }
    }

    // Listamos segun el estado del examen de lab.
    public List<ExamenLaboratorio> listarPorEstado(String estado) throws SQLException {
        List<ExamenLaboratorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM examen_laboratorio WHERE estado = ?";
        
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExamenLaboratorio ex = new ExamenLaboratorio(
                        rs.getInt("id_examen"),
                        rs.getInt("id_paciente"),
                        rs.getString("tipo_examen"),
                        rs.getString("estado"),
                        rs.getTimestamp("fecha_solicitud")
                    );
                    lista.add(ex);
                }
            }
        }
        return lista;
    }

    // Obtener solicitud de examen por ID
    public ExamenLaboratorio obtenerPorId(int idExamen) throws SQLException {
        ExamenLaboratorio ex = null;
        String sql = "SELECT * FROM examen_laboratorio WHERE id_examen = ?";
        
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idExamen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ex = new ExamenLaboratorio(
                        rs.getInt("id_examen"),
                        rs.getInt("id_paciente"), 
                        rs.getString("tipo_examen"),
                        rs.getString("estado"),
                        rs.getTimestamp("fecha_solicitud")
                    );
                }
            }
        }
        return ex;
    }

    // Ir cambiando el estado del examen
    public boolean actualizarEstado(int idExamen, String nuevoEstado) throws SQLException {
        String sql = "UPDATE examen_laboratorio SET estado = ? WHERE id_examen = ?";
        
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idExamen);
            return ps.executeUpdate() > 0;
        }
    }

    // Resultado final del examen
    public boolean registrarResultado(ResultadoExamen resultado) throws SQLException {
        String sql = "INSERT INTO resultado_examen (id_examen, detalle_resultado, observaciones, fecha_registro) "
                   + "VALUES (?, ?, ?, NOW())";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, resultado.getIdExamen());
            ps.setString(2, resultado.getDetalleResultado());
            ps.setString(3, resultado.getObservaciones());
            return ps.executeUpdate() > 0;
        }
    }
}