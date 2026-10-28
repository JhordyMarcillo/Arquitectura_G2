package ec.edu.monster.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal profesional de Monsters Inc. Converter
 * Diseño empresarial con colores oficiales y layout optimizado
 * @author ACER NITRO V15
 */
public class VentanaPrincipal extends JFrame {
    
    // Colores oficiales de Monsters Inc.
    private static final Color AZUL_PRINCIPAL = new Color(33, 169, 218);      // #21a9da
    private static final Color ROJO_MONSTER = new Color(246, 105, 113);        // #f66971
    private static final Color AMARILLO_MONSTER = new Color(245, 216, 128);   // #f5d880
    private static final Color PURPURA_MONSTER = new Color(140, 107, 205);     // #8c6bcd
    private static final Color AZUL_CLARO = new Color(159, 220, 250);         // #9fdcfa
    private static final Color BLANCO = new Color(255, 255, 255);
    private static final Color GRIS_OSCURO = new Color(60, 60, 60);
    private static final Color GRIS_CLARO = new Color(240, 240, 240);
    private static final Color GRIS_MEDIO = new Color(200, 200, 200);
    
    // Componentes principales
    private JPanel panelHeader;
    private JPanel panelNavegacion;
    private JPanel panelContenido;
    private JPanel panelFooter;
    
    // Botones de navegación
    private JButton btnTemperatura;
    private JButton btnLongitud;
    private JButton btnPeso;
    
    // Paneles de conversión
    private PanelTemperatura panelTemperatura;
    private PanelLongitud panelLongitud;
    private PanelPeso panelPeso;
    
    public VentanaPrincipal() {
        inicializarComponentes();
        configurarVentana();
        configurarLayout();
        configurarEventos();
        
        // Mostrar panel de temperatura por defecto después de configurar todo
        SwingUtilities.invokeLater(() -> {
            mostrarPanelTemperatura();
        });
    }
    
    /**
     * Inicializa todos los componentes
     */
    private void inicializarComponentes() {
        // Panel header
        panelHeader = new JPanel();
        panelHeader.setBackground(AZUL_PRINCIPAL);
        panelHeader.setPreferredSize(new Dimension(1200, 80));
        
        // Panel navegación
        panelNavegacion = new JPanel();
        panelNavegacion.setBackground(GRIS_CLARO);
        panelNavegacion.setPreferredSize(new Dimension(1200, 60));
        
        // Panel contenido
        panelContenido = new JPanel();
        panelContenido.setBackground(BLANCO);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_CLARO, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        
        // Panel footer
        panelFooter = new JPanel();
        panelFooter.setBackground(GRIS_OSCURO);
        panelFooter.setPreferredSize(new Dimension(1200, 40));
        
        // Botones de navegación
        btnTemperatura = crearBotonNavegacion("TEMPERATURA", AZUL_PRINCIPAL);
        btnLongitud = crearBotonNavegacion("LONGITUD", PURPURA_MONSTER);
        btnPeso = crearBotonNavegacion("PESO", ROJO_MONSTER);
        
        // Paneles de conversión
        panelTemperatura = new PanelTemperatura();
        panelLongitud = new PanelLongitud();
        panelPeso = new PanelPeso();
    }
    
    /**
     * Crea un botón de navegación con estilo profesional
     */
    private JButton crearBotonNavegacion(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(BLANCO);
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(130, 40));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(oscurecerColor(color));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    /**
     * Oscurece un color para el efecto hover
     */
    private Color oscurecerColor(Color color) {
        return new Color(
            Math.max(0, color.getRed() - 30),
            Math.max(0, color.getGreen() - 30),
            Math.max(0, color.getBlue() - 30)
        );
    }
    
    /**
     * Configura la ventana principal
     */
    private void configurarVentana() {
        setTitle("Monsters Inc. Converter - Sistema de Conversiones Empresarial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1000, 700));
        getContentPane().setBackground(BLANCO);
    }
    
    /**
     * Configura el layout de la ventana con diseño compacto
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Header
        configurarHeader();
        add(panelHeader, BorderLayout.NORTH);
        
        // Panel principal con diseño en grid compacto
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(new Color(248, 249, 250));
        
        // Panel superior con navegación
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(BLANCO);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        // Título de navegación
        JLabel lblTituloNav = new JLabel("SELECCIONE EL TIPO DE CONVERSIÓN");
        lblTituloNav.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloNav.setForeground(GRIS_OSCURO);
        panelSuperior.add(lblTituloNav, BorderLayout.WEST);
        
        // Botones de navegación en panel horizontal
        JPanel panelBotonesNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotonesNav.setBackground(BLANCO);
        panelBotonesNav.add(btnTemperatura);
        panelBotonesNav.add(btnLongitud);
        panelBotonesNav.add(btnPeso);
        panelSuperior.add(panelBotonesNav, BorderLayout.EAST);
        
        panelCentral.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel de contenido principal - SOLO panel izquierdo
        JPanel panelContenidoPrincipal = new JPanel(new BorderLayout());
        panelContenidoPrincipal.setBackground(new Color(248, 249, 250));
        panelContenidoPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Panel único - Formulario de conversión
        JPanel panelFormulario = new JPanel();
        panelFormulario.setBackground(BLANCO);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panelFormulario.setLayout(new BorderLayout());
        
        // Mensaje de bienvenida temporal
        JLabel lblBienvenida = new JLabel("Seleccione un tipo de conversión para comenzar");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        lblBienvenida.setForeground(GRIS_OSCURO);
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormulario.add(lblBienvenida, BorderLayout.CENTER);
        
        panelContenidoPrincipal.add(panelFormulario, BorderLayout.CENTER);
        
        panelCentral.add(panelContenidoPrincipal, BorderLayout.CENTER);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Footer
        configurarFooter();
        add(panelFooter, BorderLayout.SOUTH);
        
        // Guardar referencia al panel de formulario para cambios dinámicos
        panelContenido = panelFormulario;
    }
    
    /**
     * Configura el header con logo y título
     */
    private void configurarHeader() {
        panelHeader.setLayout(new BorderLayout());
        
        // Logo izquierdo
        JPanel panelLogo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLogo.setBackground(AZUL_PRINCIPAL);
        
        // Intentar cargar imagen del icono, si no existe usar texto
        ImageIcon iconoImg = cargarImagen("/images/icon.png");
        JLabel lblIcono;
        if (iconoImg != null) {
            // Solo mostrar la imagen sin borde amarillo
            lblIcono = new JLabel(iconoImg);
        } else {
            lblIcono = new JLabel("MI");
            lblIcono.setFont(new Font("Arial", Font.BOLD, 24));
            lblIcono.setForeground(BLANCO);
            lblIcono.setBackground(new Color(0, 0, 0, 0)); // Transparente
            lblIcono.setOpaque(false);
        }
        lblIcono.setPreferredSize(new Dimension(50, 50));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setVerticalAlignment(SwingConstants.CENTER);
        
        JLabel lblEmpresa = new JLabel("MONSTERS INC.");
        lblEmpresa.setFont(new Font("Arial", Font.BOLD, 28));
        lblEmpresa.setForeground(BLANCO);
        
        panelLogo.add(lblIcono);
        panelLogo.add(Box.createHorizontalStrut(15));
        panelLogo.add(lblEmpresa);
        
        panelHeader.add(panelLogo, BorderLayout.WEST);
        
        // Título central
        JLabel lblTitulo = new JLabel("Sistema de Conversiones");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(AZUL_CLARO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        
        // Usuario derecho con botón cerrar sesión
        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelUsuario.setBackground(AZUL_PRINCIPAL);
        
        JLabel lblUsuario = new JLabel("Usuario: MONSTER");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        lblUsuario.setForeground(BLANCO);
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Arial", Font.PLAIN, 11));
        btnCerrarSesion.setForeground(BLANCO);
        btnCerrarSesion.setBackground(ROJO_MONSTER);
        btnCerrarSesion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROJO_MONSTER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover en botón cerrar sesión
        btnCerrarSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCerrarSesion.setBackground(new Color(200, 50, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCerrarSesion.setBackground(ROJO_MONSTER);
            }
        });
        
        // Evento para cerrar sesión
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        panelUsuario.add(lblUsuario);
        panelUsuario.add(btnCerrarSesion);
        panelHeader.add(panelUsuario, BorderLayout.EAST);
    }
    
    /**
     * Configura el footer
     */
    private void configurarFooter() {
        panelFooter.setLayout(new BorderLayout());
        
        JLabel lblFooter = new JLabel("Monsters Inc. Converter Enterprise Edition v1.0 - © 2026");
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 11));
        lblFooter.setForeground(BLANCO);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        panelFooter.add(lblFooter, BorderLayout.CENTER);
        
        // Botón de ayuda
        JButton btnAyuda = new JButton("¿Tienes alguna duda?");
        btnAyuda.setFont(new Font("Arial", Font.BOLD, 10));
        btnAyuda.setForeground(BLANCO);
        btnAyuda.setBackground(new Color(0, 0, 0, 0)); // Transparente
        btnAyuda.setBorderPainted(false);
        btnAyuda.setFocusPainted(false);
        btnAyuda.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAyuda.addActionListener(e -> mostrarMensajeAyuda());
        panelFooter.add(btnAyuda, BorderLayout.EAST);
        
        JLabel lblEstado = new JLabel("Sistema Operativo");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 11));
        lblEstado.setForeground(BLANCO);
        panelFooter.add(lblEstado, BorderLayout.SOUTH);
    }
    
    /**
     * Configura los eventos de los botones
     */
    private void configurarEventos() {
        btnTemperatura.addActionListener(e -> mostrarPanelTemperatura());
        btnLongitud.addActionListener(e -> mostrarPanelLongitud());
        btnPeso.addActionListener(e -> mostrarPanelPeso());
    }
    
    /**
     * Muestra el panel de temperatura
     */
    private void mostrarPanelTemperatura() {
        cambiarPanel(panelTemperatura);
        actualizarBotonActivo(btnTemperatura);
    }
    
    /**
     * Muestra el panel de longitud
     */
    private void mostrarPanelLongitud() {
        cambiarPanel(panelLongitud);
        actualizarBotonActivo(btnLongitud);
    }
    
    /**
     * Muestra el panel de peso
     */
    private void mostrarPanelPeso() {
        cambiarPanel(panelPeso);
        actualizarBotonActivo(btnPeso);
    }
    

    /**
     * Cambia el panel de contenido
     */
    private void cambiarPanel(JPanel nuevoPanel) {
        panelContenido.removeAll();
        panelContenido.setLayout(new BorderLayout());
        panelContenido.add(nuevoPanel, BorderLayout.CENTER);
        panelContenido.setVisible(true);
        panelContenido.revalidate();
        panelContenido.repaint();
        
        // Forzar actualización de la ventana
        revalidate();
        repaint();
    }
    
    private void actualizarBotonActivo(JButton botonActivo) {
        // Resetear todos los botones
        Color[] colores = {AZUL_PRINCIPAL, PURPURA_MONSTER, ROJO_MONSTER};
        JButton[] botones = {btnTemperatura, btnLongitud, btnPeso};
        
        for (int i = 0; i < botones.length; i++) {
            botones[i].setBackground(colores[i]);
            botones[i].setForeground(BLANCO);
        }
        
        // Resaltar botón activo
        botonActivo.setBackground(GRIS_OSCURO);
        botonActivo.setForeground(BLANCO);
    }
    
    /**
     * Cierra la sesión y vuelve al login
     */
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            // Cerrar ventana principal
            this.dispose();
            
            // Mostrar ventana de login
            SwingUtilities.invokeLater(() -> {
                VentanaLogin ventanaLogin = new VentanaLogin();
                ventanaLogin.setVisible(true);
            });
        }
    }
    
    /**
     * Muestra un mensaje de ayuda al usuario
     */
    private void mostrarMensajeAyuda() {
        String mensaje = "¡Bienvenido al Sistema de Conversiones de Monsters Inc.!\n\n" +
                         "Esta aplicación te permite realizar conversiones entre diferentes unidades de medida:\n\n" +
                         "• TEMPERATURA: Celsius, Fahrenheit, Kelvin\n" +
                         "• LONGITUD: Metros, Pies, Pulgadas, Yardas\n" +
                         "• PESO: Kilogramos, Libras, Onzas\n\n" +
                         "Simplemente selecciona el tipo de conversión, ingresa el valor y elige las unidades " +
                         "para obtener el resultado al instante.";
        
        JOptionPane.showMessageDialog(
            this, 
            mensaje, 
            "Acerca del Sistema", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Carga una imagen de forma segura desde resources
     */
    private ImageIcon cargarImagen(String ruta) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url != null) {
                return new ImageIcon(url);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + ruta);
        }
        return null;
    }
    
    // Getters para el controlador
    public PanelTemperatura getPanelTemperatura() { return panelTemperatura; }
    public PanelLongitud getPanelLongitud() { return panelLongitud; }
    public PanelPeso getPanelPeso() { return panelPeso; }
}