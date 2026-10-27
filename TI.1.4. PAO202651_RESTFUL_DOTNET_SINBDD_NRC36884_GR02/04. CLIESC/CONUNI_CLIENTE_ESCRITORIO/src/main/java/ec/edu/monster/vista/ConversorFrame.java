package ec.edu.monster.vista;

import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.controlador.Configuracion;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ExcepcionApi;
import ec.edu.monster.modelo.SesionUsuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ConversorFrame extends JFrame {

    private static final int ANCHO = 650;
    private static final int ALTO = 730;

    private final ApiService apiService = new ApiService();
    private final String usuario;

    // Catalogo que envia el servidor (CU-03), por categoria
    private final Map<String, List<String>> unidadesPorCategoria = new HashMap<>();

    private JComboBox<String> cmbCategoria;
    private JComboBox<String> cmbOrigen;
    private JComboBox<String> cmbDestino;
    private JTextField txtValor;
    private JButton btnConvertir;
    private JLabel lblResultado;
    private JLabel lblEstado;

    public ConversorFrame(String usuario) {
        this.usuario = usuario;

        setTitle("Calculadora de Conversión de Unidades - Usuario: " + usuario);
        setSize(ANCHO, ALTO);
        setResizable(false);
        setLocationRelativeTo(null);

        // Confirmar antes de salir
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        crearInterfaz();
        cargarCategorias();
    }

    // ------------------------------------------------------------------
    // Interfaz
    // ------------------------------------------------------------------
    private void crearInterfaz() {
        PanelFondo fondo = new PanelFondo(Estilos.imagen("fondoes.jpg"), new BorderLayout());
        fondo.add(crearEncabezado(), BorderLayout.NORTH);
        fondo.add(crearFormulario(), BorderLayout.CENTER);
        fondo.add(crearBarraEstado(), BorderLayout.SOUTH);
        setContentPane(fondo);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilos.ENCABEZADO);
        encabezado.setPreferredSize(new Dimension(ANCHO, 60));
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        encabezado.add(Estilos.etiqueta("Calculadora de Conversión de Unidades", Font.BOLD, 15, Color.WHITE),
                BorderLayout.WEST);

        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelUsuario.setOpaque(false);
        panelUsuario.add(Estilos.etiqueta(usuario, Font.BOLD, 11, Color.WHITE));

        // El taller no pide cerrar sesion: el boton solo aparece si se activa en Configuracion
        if (Configuracion.PERMITIR_CERRAR_SESION) {
            JButton btnCerrarSesion = Estilos.boton("Cerrar sesión", Estilos.BOTON_SESION,
                    Estilos.BOTON_SESION_HOVER, 11, new Dimension(115, 30));
            btnCerrarSesion.addActionListener(e -> cerrarSesion());
            panelUsuario.add(btnCerrarSesion);
        }
        encabezado.add(panelUsuario, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearFormulario() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBackground(Color.WHITE);
        principal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(15, 15, 12, 15)));

        // Titulo de seccion
        JLabel titulo = Estilos.etiqueta("Selecciona el tipo de conversión", Font.BOLD, 13, Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Estilos.TITULO_SECCION);
        barraTitulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        barraTitulo.add(titulo);
        agregar(principal, barraTitulo, 38);
        principal.add(Box.createVerticalStrut(10));

        // Categoria, origen y destino (CU-02 y CU-03)
        cmbCategoria = new JComboBox<>();
        cmbCategoria.addActionListener(e -> alCambiarCategoria());
        cmbOrigen = new JComboBox<>();
        cmbDestino = new JComboBox<>();
        agregarFila(principal, "Categoría:", cmbCategoria);
        agregarFila(principal, "Origen:", cmbOrigen);
        agregarFila(principal, "Destino:", cmbDestino);

        // Linea separadora
        principal.add(Box.createVerticalStrut(10));
        JPanel separador = new JPanel();
        separador.setBackground(Estilos.SEPARADOR);
        agregar(principal, separador, 3);
        principal.add(Box.createVerticalStrut(10));

        // Valor a convertir
        txtValor = new JTextField();
        txtValor.addActionListener(e -> convertir());
        agregarFila(principal, "Valor a convertir:", txtValor);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        btnConvertir = Estilos.boton("CONVERTIR", Estilos.BOTON_CONVERTIR, Estilos.BOTON_CONVERTIR_HOVER,
                13, new Dimension(210, 48));
        btnConvertir.addActionListener(e -> convertir());
        JButton btnLimpiar = Estilos.boton("LIMPIAR", Estilos.BOTON_LIMPIAR, Estilos.BOTON_LIMPIAR_HOVER,
                13, new Dimension(210, 48));
        btnLimpiar.addActionListener(e -> limpiar());
        botones.add(btnConvertir);
        botones.add(btnLimpiar);
        principal.add(Box.createVerticalStrut(12));
        agregar(principal, botones, 48);
        principal.add(Box.createVerticalStrut(12));

        // Resultado
        JPanel panelResultado = new JPanel(new BorderLayout(0, 4));
        panelResultado.setBackground(Estilos.FONDO_RESULTADO);
        panelResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE_RESULTADO, 3),
                BorderFactory.createEmptyBorder(10, 10, 12, 10)));
        JLabel tituloResultado = Estilos.etiqueta("Resultado:", Font.BOLD, 12, Estilos.TITULO_RESULTADO);
        tituloResultado.setHorizontalAlignment(SwingConstants.CENTER);
        lblResultado = Estilos.etiqueta("---", Font.BOLD, 18, Estilos.VALOR_RESULTADO);
        lblResultado.setHorizontalAlignment(SwingConstants.CENTER);
        panelResultado.add(tituloResultado, BorderLayout.NORTH);
        panelResultado.add(lblResultado, BorderLayout.CENTER);
        agregar(principal, panelResultado, 85);

        contenedor.add(principal, BorderLayout.NORTH);
        return contenedor;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));

        lblEstado = Estilos.etiqueta("Verificando conexión...", Font.BOLD, 11, new Color(0x666666));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(Color.WHITE);
        caja.setBorder(BorderFactory.createRaisedBevelBorder());
        caja.setPreferredSize(new Dimension(ANCHO - 50, 50));
        caja.add(lblEstado);
        barra.add(caja);
        return barra;
    }

    private void agregarFila(JPanel panel, String texto, Component campo) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        JLabel etiqueta = Estilos.etiqueta(texto, Font.BOLD, 11, Estilos.ETIQUETA);
        etiqueta.setPreferredSize(new Dimension(140, 30));
        campo.setFont(Estilos.fuente(Font.PLAIN, 12));
        fila.add(etiqueta, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(8));
        agregar(panel, fila, 32);
    }

    private void agregar(JPanel panel, javax.swing.JComponent componente, int alto) {
        componente.setAlignmentX(Component.CENTER_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        componente.setPreferredSize(new Dimension(componente.getPreferredSize().width, alto));
        panel.add(componente);
    }

    // ------------------------------------------------------------------
    // Comunicacion con el servidor
    // ------------------------------------------------------------------
    // CU-02 Consultar catalogo
    private void cargarCategorias() {
        TareaApi.ejecutar(this,
                apiService::obtenerCategorias,
                categorias -> {
                    cmbCategoria.removeAllItems();
                    for (String categoria : categorias) {
                        cmbCategoria.addItem(FormatoNumero.nombreVisible(categoria));
                    }
                    actualizarEstado("Servidor disponible", Estilos.ESTADO_EXITO);
                },
                this::procesarError);
    }

    // CU-03 Consultar unidades por categoria
    private void alCambiarCategoria() {
        Object seleccion = cmbCategoria.getSelectedItem();
        if (seleccion == null) {
            return;
        }
        String categoria = FormatoNumero.codigo(seleccion.toString());
        lblResultado.setText("---");

        if (unidadesPorCategoria.containsKey(categoria)) {
            mostrarUnidades(unidadesPorCategoria.get(categoria));
            return;
        }
        TareaApi.ejecutar(this,
                () -> apiService.obtenerUnidades(categoria),
                unidades -> {
                    unidadesPorCategoria.put(categoria, unidades);
                    mostrarUnidades(unidades);
                },
                this::procesarError);
    }

    private void mostrarUnidades(List<String> unidades) {
        List<String> nombres = new ArrayList<>();
        for (String unidad : unidades) {
            nombres.add(FormatoNumero.nombreVisible(unidad));
        }
        cmbOrigen.removeAllItems();
        cmbDestino.removeAllItems();
        for (String nombre : nombres) {
            cmbOrigen.addItem(nombre);
            cmbDestino.addItem(nombre);
        }
        if (nombres.size() > 1) {
            cmbDestino.setSelectedIndex(1);
        }
    }

    // CU-04 Realizar conversion
    private void convertir() {
        if (cmbCategoria.getSelectedItem() == null) {
            avisar("Selección requerida", "Por favor, seleccione una categoría");
            return;
        }
        if (cmbOrigen.getSelectedItem() == null || cmbDestino.getSelectedItem() == null) {
            avisar("Selección requerida", "Por favor, seleccione las unidades de origen y destino");
            return;
        }

        String valorTexto = txtValor.getText().trim().replace(',', '.');
        if (valorTexto.isEmpty()) {
            avisar("Valor requerido", "Por favor, ingrese un valor a convertir");
            txtValor.requestFocus();
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido",
                    "Valor inválido", JOptionPane.ERROR_MESSAGE);
            txtValor.requestFocus();
            return;
        }

        ConversionRequest request = new ConversionRequest(
                FormatoNumero.codigo(cmbCategoria.getSelectedItem().toString()),
                FormatoNumero.codigo(cmbOrigen.getSelectedItem().toString()),
                FormatoNumero.codigo(cmbDestino.getSelectedItem().toString()),
                valor);

        btnConvertir.setEnabled(false);
        TareaApi.ejecutar(this,
                () -> apiService.realizarConversion(request),
                respuesta -> {
                    btnConvertir.setEnabled(true);
                    lblResultado.setText(FormatoNumero.formatear(valor) + " "
                            + FormatoNumero.nombreVisible(respuesta.getUnidadOrigen()) + " = "
                            + FormatoNumero.formatear(respuesta.getResultado()) + " "
                            + FormatoNumero.nombreVisible(respuesta.getUnidadDestino()));
                    actualizarEstado(respuesta.getMensaje(), Estilos.ESTADO_EXITO);
                },
                error -> {
                    btnConvertir.setEnabled(true);
                    procesarError(error);
                });
    }

    // CU-08 a CU-11: muestra el mensaje del servidor y reacciona segun el codigo
    private void procesarError(ExcepcionApi e) {
        if (e.esErrorDeSesion()) {
            dispose();
            new LoginFrame(e.getMessage() + "\n\nVuelva a iniciar sesión.").setVisible(true);
            return;
        }

        lblResultado.setText("---");
        if (e.esErrorDeRed()) {
            actualizarEstado("Servidor no disponible", Estilos.ESTADO_AVISO);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
        } else {
            actualizarEstado("Error " + e.getCodigo(), Estilos.ESTADO_ERROR);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error " + e.getCodigo(), JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Acciones de la ventana
    // ------------------------------------------------------------------
    private void limpiar() {
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        txtValor.setText("");
        lblResultado.setText("---");
        txtValor.requestFocus();

        // Verificar conexion nuevamente (solo informativo)
        TareaApi.ejecutar(this,
                () -> apiService.verificarConexion(),
                conectado -> {
                    if (conectado) {
                        actualizarEstado("Servidor disponible", Estilos.ESTADO_EXITO);
                    } else {
                        actualizarEstado("Servidor no disponible", Estilos.ESTADO_AVISO);
                    }
                },
                this::procesarError);
    }

    private void actualizarEstado(String mensaje, Color color) {
        lblEstado.setText(mensaje);
        lblEstado.setForeground(color);
    }

    private void avisar(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    // Solo se usa si Configuracion.PERMITIR_CERRAR_SESION = true
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar la sesión de " + usuario + "?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            SesionUsuario.cerrar();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea salir de la aplicación?", "Salir", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
