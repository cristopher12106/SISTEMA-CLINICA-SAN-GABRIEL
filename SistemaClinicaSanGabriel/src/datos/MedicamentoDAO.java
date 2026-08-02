/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;
import entidades.Medicamento;
import entidades.EntregaMedicamento;
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
public class MedicamentoDAO {
    public List<Medicamento> listar() throws SQLException {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamento WHERE estado = 1"; 
        try (Connection cn = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Medicamento m = new Medicamento(
                    rs.getInt("id_medicamento"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock_actual"),
                    rs.getInt("stock_minimo"),
                    rs.getDouble("precio_unitario"),
                    rs.getBoolean("estado")
                );
                lista.add(m);
            }
        }
        return lista;
    }
    // Busqued de medicamento por ID
    public Medicamento obtenerPorId(int idMedicamento) throws SQLException {
        Medicamento m = null;
        String sql = "SELECT * FROM medicamento WHERE id_medicamento = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMedicamento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    m = new Medicamento(
                        rs.getInt("id_medicamento"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("stock_actual"),
                        rs.getInt("stock_minimo"),
                        rs.getDouble("precio_unitario"),
                        rs.getBoolean("estado")
                    );
                }
            }
        }
        return m;
    }
    // Actualizamos stocck
    public boolean actualizarStock(int idMedicamento, int nuevaCantidad) throws SQLException {
        String sql = "UPDATE medicamento SET stock_actual = ? WHERE id_medicamento = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idMedicamento);
            return ps.executeUpdate() > 0;
        }
    }

    // Descuenta el stock dentro de la misma transacción (RN-28)
    public static boolean descontarStock(int idMedicamento, int cantidad, Connection cn) throws SQLException {
        String sql = "UPDATE medicamento SET stock_actual = stock_actual - ? WHERE id_medicamento = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idMedicamento);
            return ps.executeUpdate() > 0;
        }
    }

    // Registro deespacho/entrega
    public boolean registrarEntrega(EntregaMedicamento entrega) throws SQLException {
        String sql = "INSERT INTO entrega_medicamento (id_atencion, id_medicamento, cantidad, fecha_entrega) "
                   + "VALUES (?, ?, ?, NOW())";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdAtencion());
            ps.setInt(2, entrega.getIdMedicamento());
            ps.setInt(3, entrega.getCantidad());
            return ps.executeUpdate() > 0;
        }
    }
    public boolean insertar(Medicamento med) throws SQLException {
        String sql = "INSERT INTO medicamento (nombre, descripcion, stock_actual, stock_minimo, precio_unitario, estado) "
                   + "VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, med.getNombre());
            ps.setString(2, med.getDescripcion());
            ps.setInt(3, med.getStockActual());
            ps.setInt(4, med.getStockMinimo()); // Si no le asignas valor en la vista, se enviará el por defecto
            ps.setDouble(5, med.getPrecioUnitario());
            return ps.executeUpdate() > 0;
        }
    }
}
