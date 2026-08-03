package datos;


import entidades.DetalleReceta;
import entidades.RecetaMedica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecetaDAO {

    public static void registrarReceta(RecetaMedica receta, Connection cn) throws SQLException {
        String sqlReceta = "INSERT INTO recetas_medicas (idAtencion) VALUES (?)";
        String sqlDetalle = "INSERT INTO detalle_receta (idReceta, idMedicamento, cantidad, indicacion) VALUES (?, ?, ?, ?)";

        int idRecetaGenerado = -1;

        // 1. Insertar la cabecera de la Receta
        try (PreparedStatement psReceta = cn.prepareStatement(sqlReceta, Statement.RETURN_GENERATED_KEYS)) {
            psReceta.setInt(1, receta.getIdAtencion());
            psReceta.executeUpdate();

            try (ResultSet rs = psReceta.getGeneratedKeys()) {
                if (rs.next()) {
                    idRecetaGenerado = rs.getInt(1);
                    receta.setIdReceta(idRecetaGenerado);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para la receta.");
                }
            }
        }

        // 2. Insertar los detalles/medicamentos de la Receta (solo se registra la indicacion;
        //    el descuento de stock se realiza en Farmacia al despachar la entrega)
        if (receta.getDetalles() != null && !receta.getDetalles().isEmpty()) {
            try (PreparedStatement psDetalle = cn.prepareStatement(sqlDetalle)) {
                for (DetalleReceta detalle : receta.getDetalles()) {
                    psDetalle.setInt(1, idRecetaGenerado);
                    psDetalle.setInt(2, detalle.getIdMedicamento());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setString(4, detalle.getIndicacion());
                    psDetalle.executeUpdate();
                }
            }
        }
    }

    public static RecetaMedica obtenerRecetaConDetalles(int idReceta) {
        String sqlReceta = "SELECT idReceta, idAtencion, despachada FROM recetas_medicas WHERE idReceta = ?";
        RecetaMedica receta = null;

        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sqlReceta)) {
            ps.setInt(1, idReceta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    receta = new RecetaMedica();
                    receta.setIdReceta(rs.getInt("idReceta"));
                    receta.setIdAtencion(rs.getInt("idAtencion"));
                    receta.setDespachada(rs.getBoolean("despachada"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener receta: " + e.getMessage());
            return null;
        }

        if (receta == null) {
            return null;
        }

        String sqlDetalles = "SELECT dr.idDetalle, dr.idMedicamento, dr.cantidad, dr.indicacion, "
                           + "m.nombre AS nombreMedicamento "
                           + "FROM detalle_receta dr "
                           + "INNER JOIN medicamento m ON dr.idMedicamento = m.id_medicamento "
                           + "WHERE dr.idReceta = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sqlDetalles)) {
            ps.setInt(1, idReceta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleReceta detalle = new DetalleReceta();
                    detalle.setIdDetalle(rs.getInt("idDetalle"));
                    detalle.setIdMedicamento(rs.getInt("idMedicamento"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setIndicacion(rs.getString("indicacion"));
                    detalle.setNombreMedicamento(rs.getString("nombreMedicamento"));
                    receta.agregarDetalle(detalle);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de la receta: " + e.getMessage());
            return null;
        }

        return receta;
    }

    public static boolean marcarDespachada(int idReceta) {
        String sql = "UPDATE recetas_medicas SET despachada = TRUE WHERE idReceta = ?";
        int filasAfectadas = 0;
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al marcar receta como despachada: " + e.getMessage());
        }
        return filasAfectadas > 0;
    }
}