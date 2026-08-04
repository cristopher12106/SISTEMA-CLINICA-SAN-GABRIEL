package presentacion;

import logica.FarmaciaLOG;
import entidades.DetalleReceta;
import entidades.Medicamento;
import entidades.RecetaMedica;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author LENOVO
 */
public class IfrmDespachoFarmacia extends JInternalFrame {

    private final FarmaciaLOG farmaciaLOG;
    private RecetaMedica recetaCargada;

    private final JTextField txtIdReceta = new JTextField(12);
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnDespachar = new JButton("Registrar Despacho");
    private final JTable tblDespacho;

    public IfrmDespachoFarmacia() {
        this.farmaciaLOG = new FarmaciaLOG();

        this.setTitle("Despacho de Medicamentos - Farmacia");
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);

        tblDespacho = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Medicamento", "Cantidad", "Stock Disponible", "Precio (S/)"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        construirUI();
    }

    private void construirUI() {
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        pnlBusqueda.add(new JLabel("ID Receta:"));
        pnlBusqueda.add(txtIdReceta);
        pnlBusqueda.add(btnBuscar);
        btnBuscar.addActionListener(e -> btnBuscarActionPerformed());
        txtIdReceta.addActionListener(e -> btnBuscarActionPerformed());

        JScrollPane scroll = new JScrollPane(tblDespacho);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBotones.add(btnDespachar);
        btnDespachar.addActionListener(e -> btnDespacharActionPerformed());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlBusqueda, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(pnlBotones, BorderLayout.SOUTH);

        pack();
        setSize(580, 380);
    }

    private void btnBuscarActionPerformed() {
        String texto = txtIdReceta.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de la receta a buscar.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idReceta = Integer.parseInt(texto);
            recetaCargada = farmaciaLOG.cargarReceta(idReceta);
            cargarTabla();
            if (recetaCargada.isDespachada()) {
                btnDespachar.setEnabled(false);
                JOptionPane.showMessageDialog(this, "La receta N° " + idReceta + " ya fue despachada anteriormente.", "Receta ya despachada", JOptionPane.WARNING_MESSAGE);
            } else {
                btnDespachar.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID de receta debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Receta no encontrada", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarTabla() throws Exception {
        DefaultTableModel modelo = (DefaultTableModel) tblDespacho.getModel();
        modelo.setRowCount(0);
        if (recetaCargada == null || recetaCargada.getDetalles() == null || recetaCargada.getDetalles().isEmpty()) {
            return;
        }

        Map<Integer, Medicamento> inventario = new HashMap<>();
        for (Medicamento m : farmaciaLOG.obtenerInventario()) {
            inventario.put(m.getIdMedicamento(), m);
        }

        for (DetalleReceta d : recetaCargada.getDetalles()) {
            Medicamento med = inventario.get(d.getIdMedicamento());
            String nombre = med != null ? med.getNombre() : d.getNombreMedicamento();
            int stock = med != null ? med.getStockActual() : 0;
            double precio = med != null ? med.getPrecioUnitario() : 0.0;
            modelo.addRow(new Object[]{nombre, d.getCantidad(), stock, precio});
        }
    }

    private void btnDespacharActionPerformed() {
        if (recetaCargada == null) {
            JOptionPane.showMessageDialog(this, "Primero busque una receta válida para registrar el despacho.", "Despacho no disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String mensaje = farmaciaLOG.procesarDespachoReceta(recetaCargada.getIdReceta());
            JOptionPane.showMessageDialog(this, mensaje, "Proceso de Despacho", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de Despacho", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarCampos() {
        recetaCargada = null;
        txtIdReceta.setText("");
        btnDespachar.setEnabled(true);
        ((DefaultTableModel) tblDespacho.getModel()).setRowCount(0);
        txtIdReceta.requestFocus();
    }
}
