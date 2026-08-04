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
public class ReportePdfAdapter implements ExportadorReporte {
    private PdfExternoLOG pdfEngine;

    public ReportePdfAdapter() {
        this.pdfEngine = new PdfExternoLOG();
    }

    @Override
    public void exportar(JTable tabla, String tituloReporte) {
        int totalFilas = tabla.getRowCount();
        
        pdfEngine.generarDocumentoPdf(tituloReporte, totalFilas);
        
        JOptionPane.showMessageDialog(null,
                "Reporte '" + tituloReporte + "' exportado exitosamente a PDF.",
                "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
}
