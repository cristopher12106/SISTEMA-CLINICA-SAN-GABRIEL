/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logica.adapter;

import javax.swing.JTable;

/**
 *
 * @author Cristopher
 */
public interface ExportadorReporte {
    void exportar(JTable tabla, String tituloReporte);
}
