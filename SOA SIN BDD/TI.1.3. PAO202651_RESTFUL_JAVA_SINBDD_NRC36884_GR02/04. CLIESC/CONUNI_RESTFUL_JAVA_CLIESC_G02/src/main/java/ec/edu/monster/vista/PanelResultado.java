package ec.edu.monster.vista;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para mostrar resultados de conversiones - Diseño compacto y funcional
 * @author ACER NITRO V15
 */
public class PanelResultado extends JPanel {
    
    // Colores oficiales de Monsters Inc.
    private static final Color AZUL_PRINCIPAL = new Color(33, 169, 218);      // #21a9da
    private static final Color ROJO_MONSTER = new Color(246, 105, 113);        // #f66971
    private static final Color AMARILLO_MONSTER = new Color(245, 216, 128);   // #f5d880
    private static final Color PURPURA_MONSTER = new Color(140, 107, 205);     // #8c6bcd
    private static final Color AZUL_CLARO = new Color(159, 220, 250);         // #9fdcfa
    private static final Color BLANCO = new Color(255, 255, 255);
    private static final Color GRIS_OSCURO = new Color(60, 60, 60);
    private static final Color GRIS_CLARO = new Color(240, 240, 240);
    
    // Componentes
    private JLabel lblTitulo;
    private JTextArea areaResultado;
    
    public PanelResultado() {
        inicializarComponentes();
        configurarLayout();
    }
    
    private void inicializarComponentes() {
        // Título
        lblTitulo = new JLabel("RESULTADO DE LA CONVERSIÓN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(AZUL_PRINCIPAL);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Área de resultado
        areaResultado = new JTextArea();
        areaResultado.setFont(new Font("Arial", Font.PLAIN, 14));
        areaResultado.setForeground(GRIS_OSCURO);
        areaResultado.setBackground(new Color(248, 249, 250));
        areaResultado.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        areaResultado.setEditable(false);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setLineWrap(true);
        areaResultado.setText("El resultado de la conversión aparecerá aquí...");
        areaResultado.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    
    private void configurarLayout() {
        setBackground(BLANCO);
        setLayout(new BorderLayout());
        
        // Título
        add(lblTitulo, BorderLayout.NORTH);
        
        // Área de resultado
        add(areaResultado, BorderLayout.CENTER);
    }
    
    public void mostrarResultado(String resultado) {
        System.out.println("PanelResultado - Recibido resultado: " + resultado);
        areaResultado.setText(resultado);
        areaResultado.setForeground(AZUL_PRINCIPAL);
        areaResultado.setBackground(AZUL_CLARO);
        areaResultado.setFont(new Font("Arial", Font.BOLD, 16));
        areaResultado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_PRINCIPAL, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        System.out.println("PanelResultado - Resultado mostrado correctamente");
    }
    
    public void mostrarError(String error) {
        System.out.println("PanelResultado - Recibido error: " + error);
        areaResultado.setText("ERROR: " + error);
        areaResultado.setForeground(ROJO_MONSTER);
        areaResultado.setBackground(new Color(255, 240, 240));
        areaResultado.setFont(new Font("Arial", Font.BOLD, 14));
        areaResultado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        System.out.println("PanelResultado - Error mostrado correctamente");
    }
    
    public void limpiar() {
        areaResultado.setText("El resultado de la conversión aparecerá aquí...");
        areaResultado.setForeground(GRIS_OSCURO);
        areaResultado.setBackground(new Color(248, 249, 250));
        areaResultado.setFont(new Font("Arial", Font.PLAIN, 14));
        areaResultado.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}