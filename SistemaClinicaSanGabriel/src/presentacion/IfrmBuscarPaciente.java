/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

/**
 *
 * @author sthef
 */

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import entidades.Paciente;
import entidades.SeguroMedico;
import entidades.Apoderado;
import logica.PacienteLOG;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDesktopPane;
public class IfrmBuscarPaciente extends javax.swing.JInternalFrame {

    private Paciente pacienteEncontrado;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnEditar;
    private javax.swing.JTextField txtSeguroResultado;
    private javax.swing.JTextField txtApoderadoResultado;
    private javax.swing.JTextField txtHistoriaClinicaResultado;
    private javax.swing.JCheckBox chkIncluirInactivos;

    public IfrmBuscarPaciente() {
        btnSalir = new javax.swing.JButton();
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });

        btnEditar = new javax.swing.JButton();
        btnEditar.setText("Editar");
        btnEditar.setEnabled(false);
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        txtSeguroResultado = new javax.swing.JTextField();
        txtSeguroResultado.setEditable(false);

        txtApoderadoResultado = new javax.swing.JTextField();
        txtApoderadoResultado.setEditable(false);

        txtHistoriaClinicaResultado = new javax.swing.JTextField();
        txtHistoriaClinicaResultado.setEditable(false);

        chkIncluirInactivos = new javax.swing.JCheckBox("Incluir inactivos");
        chkIncluirInactivos.setToolTipText("Marcar para buscar también pacientes inactivos");

        initComponents();

        // Agregar componentes dinámicos después de initComponents
        agregarComponentesDinamicos();
    }
    private void btnBuscarPorDniActionPerformed(java.awt.event.ActionEvent evt) {
        String dni = txtBusquedaDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI para buscar.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean soloActivos = !chkIncluirInactivos.isSelected();
        pacienteEncontrado = PacienteLOG.buscarPorDni(dni, soloActivos);
        mostrarDatosEnFormulario(pacienteEncontrado);
    }

    private void btnBuscarPorNombreActionPerformed(java.awt.event.ActionEvent evt) {
        String texto = txtBusquedaNombre.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre o apellido para buscar.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean soloActivos = !chkIncluirInactivos.isSelected();
        java.util.List<Paciente> resultados = PacienteLOG.buscarPorNombreOApellido(texto, soloActivos);
        // Se asume una tabla (tblResultados) con su respectivo DefaultTableModel para mostrar la lista
        cargarResultadosEnTabla(resultados);
    }

    private void btnBuscarPorHistoriaActionPerformed(java.awt.event.ActionEvent evt) {
        String historia = txtBusquedaHistoria.getText().trim();
        if (historia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un número de historia clínica.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean soloActivos = !chkIncluirInactivos.isSelected();
        pacienteEncontrado = PacienteLOG.buscarPorHistoriaClinica(historia, soloActivos);
        mostrarDatosEnFormulario(pacienteEncontrado);
    }

    private void mostrarDatosEnFormulario(Paciente paciente) {
        if (paciente == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontro ningun paciente con esos datos.\nVerifique el DNI o numero de historia clinica e intente nuevamente.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            btnEditar.setEnabled(false);
            limpiarFormularioResultado();
            return;
        }
        txtNombresResultado.setText(paciente.getNombres());
        txtApellidosResultado.setText(paciente.getApellidos());
        txtDireccionResultado.setText(paciente.getDireccion());
        txtTelefonoResultado.setText(paciente.getTelefono());
        txtEstadoResultado.setText(paciente.isEstado() ? "Activo" : "Inactivo");
        txtHistoriaClinicaResultado.setText(paciente.getNumeroHistoriaClinica());

        // Seguro
        SeguroMedico seguro = paciente.getSeguroMedico();
        if (seguro != null) {
            txtSeguroResultado.setText(seguro.getCompania() + " - " + seguro.getTipoCobertura() + " (Poliza: " + seguro.getNumeroPoliza() + ")");
        } else {
            txtSeguroResultado.setText("Sin seguro");
        }

        // Apoderado
        Apoderado apoderado = paciente.getApoderado();
        if (apoderado != null) {
            txtApoderadoResultado.setText(apoderado.getNombres() + " " + apoderado.getApellidos() + " - " + apoderado.getParentesco() + " (DNI: " + apoderado.getDni() + ")");
        } else {
            txtApoderadoResultado.setText("Sin apoderado");
        }

        btnEditar.setEnabled(true); // Siempre habilitado: activos se editan/desactivan, inactivos se activan
    }

    private void limpiarFormularioResultado() {
        txtNombresResultado.setText("");
        txtApellidosResultado.setText("");
        txtDireccionResultado.setText("");
        txtTelefonoResultado.setText("");
        txtEstadoResultado.setText("");
        txtHistoriaClinicaResultado.setText("");
        txtSeguroResultado.setText("");
        txtApoderadoResultado.setText("");
    }

    private void abrirEditarPaciente(Paciente paciente) {
        if (paciente == null) return;
        IfrmEditarPaciente frmEditar = new IfrmEditarPaciente(paciente);
        JDesktopPane desktop = getDesktopPane();
        if (desktop != null) {
            desktop.add(frmEditar);
            frmEditar.setVisible(true);
            try {
                frmEditar.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
        }
        dispose(); // Cierra el buscador al abrir el editor
    }

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        if (pacienteEncontrado != null) {
            abrirEditarPaciente(pacienteEncontrado);
        }
    }

    private void cargarResultadosEnTabla(java.util.List<Paciente> pacientes) {
        if (pacientes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron pacientes con ese nombre o apellido.\nIntente con otro termino de busqueda.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnas = {"DNI", "Historia Clínica", "Nombres", "Apellidos", "Teléfono", "Seguro", "Apoderado", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Paciente p : pacientes) {
            String seguroInfo = "Sin seguro";
            SeguroMedico seguro = p.getSeguroMedico();
            if (seguro != null) {
                seguroInfo = seguro.getCompania() + " (" + seguro.getTipoCobertura() + ")";
            }

            String apoderadoInfo = "Sin apoderado";
            Apoderado apoderado = p.getApoderado();
            if (apoderado != null) {
                apoderadoInfo = apoderado.getNombres() + " " + apoderado.getApellidos() + " (" + apoderado.getParentesco() + ")";
            }

            Object[] fila = {
                p.getDni(),
                p.getNumeroHistoriaClinica(),
                p.getNombres(),
                p.getApellidos(),
                p.getTelefono(),
                seguroInfo,
                apoderadoInfo,
                p.isEstado() ? "Activo" : "Inactivo"
            };
            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabla);

        JDialog dialogo = new JDialog((java.awt.Frame) null, "Resultados de búsqueda", true);
        dialogo.setLayout(new BorderLayout());
        dialogo.add(scrollPane, BorderLayout.CENTER);

        // Capturar el estado del checkbox al momento de abrir el diálogo
        final boolean soloActivos = !chkIncluirInactivos.isSelected();

        // Botón Editar en el diálogo
        javax.swing.JButton btnEditarDialogo = new javax.swing.JButton("Editar");
        btnEditarDialogo.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                String dni = (String) modelo.getValueAt(fila, 0);
                Paciente paciente = PacienteLOG.buscarPorDni(dni, soloActivos);
                if (paciente != null) {
                    dialogo.dispose();
                    abrirEditarPaciente(paciente);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(dialogo, "Seleccione un paciente de la lista.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        });
        javax.swing.JPanel panelBotones = new javax.swing.JPanel();
        panelBotones.add(btnEditarDialogo);
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setSize(900, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtBusquedaDni = new javax.swing.JTextField();
        btnBuscarPorDni = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtBusquedaNombre = new javax.swing.JTextField();
        btnBuscarPorNombre = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtBusquedaHistoria = new javax.swing.JTextField();
        btnBuscarPorHistoria = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtNombresResultado = new javax.swing.JTextField();
        txtApellidosResultado = new javax.swing.JTextField();
        txtDireccionResultado = new javax.swing.JTextField();
        txtTelefonoResultado = new javax.swing.JTextField();
        txtEstadoResultado = new javax.swing.JTextField();

        jLabel1.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel1.setText("Busqueda de Paciente");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("DNI");

        txtBusquedaDni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBusquedaDniActionPerformed(evt);
            }
        });

        btnBuscarPorDni.setText("Buscar");
        btnBuscarPorDni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPorDniActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtBusquedaDni, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(btnBuscarPorDni)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBusquedaDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarPorDni)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("Nombre");

        btnBuscarPorNombre.setText("Buscar");
        btnBuscarPorNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPorNombreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnBuscarPorNombre))
                    .addComponent(txtBusquedaNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtBusquedaNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuscarPorNombre)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setText("Numero de historial clinico");

        btnBuscarPorHistoria.setText("Buscar");
        btnBuscarPorHistoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPorHistoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel4))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(btnBuscarPorHistoria)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 40, Short.MAX_VALUE)
                .addComponent(txtBusquedaHistoria, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBusquedaHistoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarPorHistoria)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        jLabel5.setText("Resultado");

        jLabel7.setText("Nombres");

        jLabel8.setText("Apellidos");

        jLabel9.setText("Direccion");

        jLabel10.setText("Telefono");

        jLabel11.setText("Estado");

        txtNombresResultado.setEditable(false);

        txtApellidosResultado.setEditable(false);

        txtDireccionResultado.setEditable(false);

        txtTelefonoResultado.setEditable(false);

        txtEstadoResultado.setEditable(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(236, 236, 236)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEstadoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefonoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDireccionResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtApellidosResultado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                        .addComponent(txtNombresResultado, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGap(475, 475, 475))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtNombresResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtApellidosResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtDireccionResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtTelefonoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtEstadoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void agregarComponentesDinamicos() {
        javax.swing.GroupLayout jPanel4Layout = (javax.swing.GroupLayout) jPanel4.getLayout();
        javax.swing.GroupLayout layout = (javax.swing.GroupLayout) getContentPane().getLayout();

        // 1) Crear labels para historia clinica, seguro y apoderado
        javax.swing.JLabel lblHistoriaClinica = new javax.swing.JLabel("Historia Clínica");
        javax.swing.JLabel lblSeguro = new javax.swing.JLabel("Seguro Medico");
        javax.swing.JLabel lblApoderado = new javax.swing.JLabel("Apoderado");

        // 2) Agregar al final de jPanel4 (después de Estado)
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblHistoriaClinica, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblSeguro, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblApoderado, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEstadoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefonoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDireccionResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtApellidosResultado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                        .addComponent(txtNombresResultado, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(txtHistoriaClinicaResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeguroResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApoderadoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(475, 475, 475))
        );

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtNombresResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtApellidosResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtDireccionResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtTelefonoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtEstadoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHistoriaClinica)
                    .addComponent(txtHistoriaClinicaResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSeguro)
                    .addComponent(txtSeguroResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblApoderado)
                    .addComponent(txtApoderadoResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        // Botón Editar y checkbox al layout principal (junto a btnSalir)
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkIncluirInactivos)
                .addGap(18, 18, 18)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIncluirInactivos))
                .addContainerGap())
        );

        pack();
    }

    private void txtBusquedaDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBusquedaDniActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBusquedaDniActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarPorDni;
    private javax.swing.JButton btnBuscarPorHistoria;
    private javax.swing.JButton btnBuscarPorNombre;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField txtApellidosResultado;
    private javax.swing.JTextField txtBusquedaDni;
    private javax.swing.JTextField txtBusquedaHistoria;
    private javax.swing.JTextField txtBusquedaNombre;
    private javax.swing.JTextField txtDireccionResultado;
    private javax.swing.JTextField txtEstadoResultado;
    private javax.swing.JTextField txtNombresResultado;
    private javax.swing.JTextField txtTelefonoResultado;
    // End of variables declaration//GEN-END:variables
}
