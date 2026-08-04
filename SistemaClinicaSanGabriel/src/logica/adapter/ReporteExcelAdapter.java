/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica.adapter;

import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author Cristopher
 */
public class ReporteExcelAdapter implements ExportadorReporte {
    private ExcelExternoLOG excelEngine;

    public ReporteExcelAdapter() {
        this.excelEngine = new ExcelExternoLOG();
    }

    @Override
    public void exportar(JTable tabla, String tituloReporte) {
        int totalFilas = tabla.getRowCount();
        int totalColumnas = tabla.getColumnCount();
        
        excelEngine.crearHojaCalculoXls(tituloReporte, totalColumnas, totalFilas);
        
        JOptionPane.showMessageDialog(null,
                "Reporte '" + tituloReporte + "' exportado exitosamente a Excel.",
                "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
}
