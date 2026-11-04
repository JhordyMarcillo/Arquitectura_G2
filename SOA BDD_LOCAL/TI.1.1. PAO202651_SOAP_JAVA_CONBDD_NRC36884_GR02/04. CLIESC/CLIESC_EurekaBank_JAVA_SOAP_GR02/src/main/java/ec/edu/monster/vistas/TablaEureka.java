package ec.edu.monster.vistas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/** Tabla de solo lectura con el estilo EurekaBank: filas altas, lineas suaves y encabezado gris. */
public class TablaEureka extends JTable {

    private final DefaultTableModel modelo;

    public TablaEureka(String[] columnas) {
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        setModel(modelo);
        setRowHeight(42);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(Tema.BORDE);
        setIntercellSpacing(new Dimension(0, 1));
        setFont(Tema.fuente(Font.PLAIN, 13));
        setFillsViewportHeight(true);
        setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JTableHeader encabezado = getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new Dimension(0, 40));
        encabezado.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
                JLabel celda = (JLabel) super.getTableCellRendererComponent(t, valor, false, false, fila, col);
                celda.setText(String.valueOf(valor).toUpperCase());
                celda.setFont(Tema.fuente(Font.BOLD, 11));
                celda.setForeground(Tema.TEXTO_SECUNDARIO);
                celda.setBackground(new Color(0xF8FAFC));
                celda.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 1, 0, Tema.BORDE), BorderFactory.createEmptyBorder(0, 16, 0, 16)));
                celda.setHorizontalAlignment(alineacionDerecha[col] ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return celda;
            }
        });
        setDefaultRenderer(Object.class, new Celda(null));
    }

    private boolean[] alineacionDerecha = new boolean[64];

    public void alinearDerecha(int... columnas) {
        for (int c : columnas) {
            alineacionDerecha[c] = true;
        }
    }

    public void anchoColumna(int columna, int ancho) {
        getColumnModel().getColumn(columna).setPreferredWidth(ancho);
        getColumnModel().getColumn(columna).setMaxWidth(ancho * 2);
    }

    /** Usa un renderizador propio para la columna (color, icono o pastilla segun el valor). */
    public void renderizar(int columna, Function<Object, JLabel> fabrica) {
        getColumnModel().getColumn(columna).setCellRenderer(new Celda(fabrica));
    }

    public void cargar(List<Object[]> filas) {
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    public JScrollPane conDesplazamiento() {
        JScrollPane panel = new JScrollPane(this);
        panel.setBorder(BorderFactory.createEmptyBorder());
        panel.getViewport().setBackground(Color.WHITE);
        return panel;
    }

    private class Celda extends DefaultTableCellRenderer {
        private final Function<Object, JLabel> fabrica;

        Celda(Function<Object, JLabel> fabrica) {
            this.fabrica = fabrica;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
            Color fondo = sel ? Tema.PRIMARIO_SUAVE : (fila % 2 == 1 ? new Color(0xF8FAFC) : Color.WHITE);
            if (fabrica != null) {
                JPanel contenedor = new JPanel(new java.awt.FlowLayout(
                        alineacionDerecha[col] ? java.awt.FlowLayout.RIGHT : java.awt.FlowLayout.LEFT, 16, 9));
                contenedor.setBackground(fondo);
                contenedor.add(fabrica.apply(valor));
                return contenedor;
            }
            JLabel celda = (JLabel) super.getTableCellRendererComponent(t, valor, false, false, fila, col);
            celda.setBackground(fondo);
            celda.setForeground(Tema.TEXTO);
            celda.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            celda.setHorizontalAlignment(alineacionDerecha[col] ? SwingConstants.RIGHT : SwingConstants.LEFT);
            return celda;
        }
    }
}
