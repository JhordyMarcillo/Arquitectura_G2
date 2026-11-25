package ec.edu.monster.vistas;

import ec.edu.monster.Principal;
import ec.edu.monster.controladores.TareaServicio;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.RespuestaOperacion;
import ec.edu.monster.utilidades.Formato;
import ec.edu.monster.vistas.VentanaPrincipal.Operacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Formulario y comprobante de deposito, retiro o transferencia. */
public class PanelOperacion extends JPanel {

    private final VentanaPrincipal ventana;
    private final Operacion tipo;
    private final JTextField campoCuenta = Tema.campo("8 digitos");
    private final JPasswordField campoClave = Tema.campoClave("Clave de 6 digitos");
    private final JTextField campoDestino = Tema.campo("8 digitos");
    private final JTextField campoImporte = Tema.campo("0.00");
    private final JButton botonRegistrar;
    private final Tema.Tarjeta comprobante = new Tema.Tarjeta();

    public PanelOperacion(VentanaPrincipal ventana, Operacion tipo) {
        this.ventana = ventana;
        this.tipo = tipo;
        String nombre = switch (tipo) {
            case DEPOSITO -> "deposito";
            case RETIRO -> "retiro";
            default -> "transferencia";
        };
        botonRegistrar = Tema.botonPrimario("Registrar " + nombre);
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 24, 0));
        add(formulario(nombre));
        add(comprobante);
        mostrarEspera();
    }

    private JPanel formulario(String nombre) {
        Tema.Tarjeta tarjeta = new Tema.Tarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));

        String descripcion = switch (tipo) {
            case DEPOSITO -> "El importe se abona a la cuenta.";
            case RETIRO -> "Se valida la clave y el saldo disponible.";
            default -> "Ambas cuentas deben ser de la misma moneda.";
        };
        JLabel encabezado = Tema.etiqueta("<html><b style='font-size:13px'>Registrar " + nombre + "</b><br><span style='color:#64748B;"
                + "font-size:9px'>" + descripcion + "</span></html>", 14, Font.PLAIN, Tema.TEXTO);
        JLabel caja = PanelCuentas.cajaIcono(Tema.icono(nombre, 20, Tema.PRIMARIO), Tema.PRIMARIO_SUAVE);
        caja.setPreferredSize(new Dimension(48, 48));
        JPanel cabecera = new JPanel(new BorderLayout(14, 0));
        cabecera.setOpaque(false);
        cabecera.add(caja, BorderLayout.WEST);
        cabecera.add(encabezado, BorderLayout.CENTER);
        cabecera.setAlignmentX(LEFT_ALIGNMENT);
        cabecera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        tarjeta.add(cabecera);
        tarjeta.add(Box.createVerticalStrut(22));

        agregar(tarjeta, tipo == Operacion.TRANSFERENCIA ? "Cuenta de origen" : "Cuenta", campoCuenta);
        if (tipo != Operacion.DEPOSITO) {
            agregar(tarjeta, "Clave de la cuenta", campoClave);
        }
        if (tipo == Operacion.TRANSFERENCIA) {
            agregar(tarjeta, "Cuenta de destino", campoDestino);
        }
        campoImporte.setFont(Tema.fuente(Font.BOLD, 17));
        agregar(tarjeta, "Importe", campoImporte);

        JLabel nota = Tema.etiqueta("<html><div style='width:330px'>" + (tipo == Operacion.TRANSFERENCIA
                ? "Cada cuenta paga el costo por movimiento: S/ 2.00 en soles y US$ 0.60 en dolares."
                : "Se aplica el costo por movimiento: S/ 2.00 en cuentas en soles y US$ 0.60 en dolares.") + "</div></html>",
                12, Font.PLAIN, Tema.TEXTO_SECUNDARIO);
        nota.setIcon(Tema.icono("info", 15, Tema.TEXTO_SECUNDARIO));
        nota.setIconTextGap(10);
        Tema.Tarjeta cajaNota = new Tema.Tarjeta(Tema.FONDO);
        cajaNota.setLayout(new BorderLayout());
        cajaNota.setBorder(BorderFactory.createEmptyBorder(10, 14, 12, 14));
        cajaNota.add(nota);
        cajaNota.setAlignmentX(LEFT_ALIGNMENT);
        cajaNota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        tarjeta.add(cajaNota);
        tarjeta.add(Box.createVerticalStrut(18));

        botonRegistrar.addActionListener(e -> registrar());
        tarjeta.add(botonRegistrar);
        return tarjeta;
    }

    private static void agregar(JPanel panel, String rotulo, JTextField campo) {
        panel.add(Tema.rotulo(rotulo));
        panel.add(campo);
        panel.add(Box.createVerticalStrut(14));
    }

    // ------------------------------------------------------------------ registro

    private void registrar() {
        String cuenta = campoCuenta.getText().trim();
        String clave = new String(campoClave.getPassword());
        String destino = campoDestino.getText().trim();
        BigDecimal importe = Formato.leerImporte(campoImporte.getText());
        String error = !Formato.esCuenta(cuenta) ? "La cuenta debe tener 8 digitos."
                : tipo != Operacion.DEPOSITO && clave.isEmpty() ? "Ingrese la clave de la cuenta."
                : tipo == Operacion.TRANSFERENCIA && !Formato.esCuenta(destino) ? "La cuenta de destino debe tener 8 digitos."
                : importe == null ? "Ingrese un importe mayor a cero (ej. 150.50)." : null;
        if (error != null) {
            ventana.notificar(error, true);
            return;
        }

        botonRegistrar.setEnabled(false);
        TareaServicio.ejecutar(() -> {
            RespuestaOperacion r = switch (tipo) {
                case DEPOSITO -> Principal.SERVICIO.depositar(cuenta, importe);
                case RETIRO -> Principal.SERVICIO.retirar(cuenta, clave, importe);
                default -> Principal.SERVICIO.transferir(cuenta, clave, destino, importe);
            };
            String moneda = Principal.SERVICIO.consultarMovimientos(r.getCuenta()).getMoneda();
            return new Object[]{r, moneda};
        }, resultado -> {
            RespuestaOperacion r = (RespuestaOperacion) resultado[0];
            mostrarExito(r, (String) resultado[1], importe, destino);
            ventana.notificar(r.getMensaje(), false);
            campoImporte.setText("");
            campoClave.setText("");
            botonRegistrar.setEnabled(true);
        }, e -> {
            botonRegistrar.setEnabled(true);
            if (e.requiereLogin()) {
                ventana.manejarError(e);
            } else {
                mostrarError(e);
            }
        });
    }

    // ------------------------------------------------------------------ comprobante

    private void mostrarEspera() {
        comprobante.removeAll();
        comprobante.setLayout(new GridBagLayout());
        JLabel vacio = Tema.etiqueta("<html><center><b style='font-size:12px;color:#0F172A'>El comprobante aparecera aqui</b><br>"
                + "Complete el formulario y registre la operacion.</center></html>", 13, Font.PLAIN, Tema.TEXTO_SECUNDARIO);
        vacio.setHorizontalAlignment(SwingConstants.CENTER);
        comprobante.add(vacio);
        comprobante.setPreferredSize(new Dimension(0, 300));
    }

    private void mostrarExito(RespuestaOperacion r, String moneda, BigDecimal importe, String destino) {
        String[][] lineas = tipo == Operacion.TRANSFERENCIA
                ? new String[][]{{"Operacion", "TRANSFERENCIA"}, {"Cuenta de origen", r.getCuenta()}, {"Cuenta de destino", destino},
                    {"Importe", Formato.dinero(moneda, importe)}, {"Nuevo saldo", Formato.dinero(moneda, r.getSaldo())},
                    {"Empleado", Principal.SERVICIO.getSesion().getNombreEmpleado()}, {"Fecha y hora", ahora()}}
                : new String[][]{{"Operacion", tipo.name()}, {"Cuenta", r.getCuenta()}, {"Importe", Formato.dinero(moneda, importe)},
                    {"Nuevo saldo", Formato.dinero(moneda, r.getSaldo())},
                    {"Empleado", Principal.SERVICIO.getSesion().getNombreEmpleado()}, {"Fecha y hora", ahora()}};
        pintarComprobante(true, "Operacion exitosa", r.getMensaje(), lineas);
    }

    private void mostrarError(ExcepcionServicio e) {
        pintarComprobante(false, "Operacion rechazada", e.getMessage(), new String[][]{{"Codigo", e.getCodigo()}});
    }

    private void pintarComprobante(boolean exito, String titulo, String mensaje, String[][] lineas) {
        comprobante.removeAll();
        comprobante.setLayout(new BoxLayout(comprobante, BoxLayout.Y_AXIS));
        comprobante.setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));

        Color color = exito ? Tema.VERDE : Tema.ROJO;
        JLabel circulo = new JLabel(Tema.icono(exito ? "check" : "x", 28, Color.WHITE), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = Tema.suavizado(g);
                g2.setColor(color);
                int d = Math.min(getWidth(), getHeight());
                g2.fillOval((getWidth() - d) / 2, 0, d, d);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circulo.setPreferredSize(new Dimension(64, 64));
        circulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        circulo.setAlignmentX(CENTER_ALIGNMENT);
        comprobante.add(circulo);
        comprobante.add(Box.createVerticalStrut(14));
        JLabel textoTitulo = Tema.etiqueta(titulo, 18, Font.BOLD, Tema.TEXTO);
        textoTitulo.setAlignmentX(CENTER_ALIGNMENT);
        comprobante.add(textoTitulo);
        JLabel textoMensaje = Tema.etiqueta("<html><center>" + mensaje + "</center></html>", 13, Font.PLAIN, Tema.TEXTO_SECUNDARIO);
        textoMensaje.setAlignmentX(CENTER_ALIGNMENT);
        textoMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        comprobante.add(textoMensaje);
        comprobante.add(Box.createVerticalStrut(20));

        JPanel detalle = new JPanel(new GridBagLayout());
        detalle.setOpaque(false);
        detalle.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDE),
                BorderFactory.createEmptyBorder(12, 0, 0, 0)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 5, 0);
        for (int i = 0; i < lineas.length; i++) {
            boolean destacado = "Nuevo saldo".equals(lineas[i][0]);
            c.gridy = i;
            c.gridx = 0;
            c.weightx = 1;
            c.anchor = GridBagConstraints.WEST;
            detalle.add(Tema.etiqueta(lineas[i][0], 13, Font.PLAIN, Tema.TEXTO_SECUNDARIO), c);
            c.gridx = 1;
            c.weightx = 0;
            c.anchor = GridBagConstraints.EAST;
            detalle.add(Tema.etiqueta(lineas[i][1], destacado ? 18 : 13, Font.BOLD, destacado ? Tema.PRIMARIO : Tema.TEXTO), c);
        }
        detalle.setAlignmentX(CENTER_ALIGNMENT);
        detalle.setMaximumSize(new Dimension(Integer.MAX_VALUE, detalle.getPreferredSize().height));
        comprobante.add(detalle);
        comprobante.revalidate();
        comprobante.repaint();
    }

    private static String ahora() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
