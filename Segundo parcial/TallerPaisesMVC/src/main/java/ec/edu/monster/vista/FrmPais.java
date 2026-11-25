package ec.edu.monster.vista;

import ec.edu.monster.controlador.PaisController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import ec.edu.monster.modelo.ApiException;
import ec.edu.monster.modelo.Config;
import ec.edu.monster.modelo.Pais;

/** Vista (Swing). Solo arma la interfaz y delega todo en PaisController. */
public class FrmPais extends JFrame {

    private final PaisController controller = new PaisController();
    private final NumberFormat fmt = NumberFormat.getIntegerInstance(new Locale("es", "EC"));

    // datos que muestra la tabla en este momento
    private List<Pais> mostrados = new ArrayList<>();
    private boolean mostrandoApi = false;

    // barra superior
    private final JButton btnImportar = new JButton("IMPORTAR PAÍSES");
    private final JButton btnApiKey = new JButton("API Key...");
    private final JLabel lblEstado = new JLabel("Listo.");

    // actividad 1: consulta a la API
    private final JTextField txtApi = new JTextField(16);
    private final JButton btnBuscarApi = new JButton("Consultar en API");
    private final JButton btnGuardarApi = new JButton("Guardar resultado en BD");

    // actividad 4: busquedas en BD
    private final JTextField txtBuscar = new JTextField(14);
    private final JButton btnBuscar = new JButton("Buscar por nombre");
    private final JComboBox<String> cmbRegion = new JComboBox<>();
    private final JButton btnRegion = new JButton("Filtrar región");
    private final JButton btnTodos = new JButton("Mostrar todos");

    // actividad 3: tabla
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Capital", "Región", "Población", "Moneda"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    // actividad 2: formulario CRUD
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtCapital = new JTextField();
    private final JTextField txtRegion = new JTextField();
    private final JTextField txtPoblacion = new JTextField();
    private final JTextField txtMoneda = new JTextField();
    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnCrear = new JButton("Crear");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");

    // actividad 5: reportes
    private final JButton btnReporte = new JButton("Generar reportes");
    private final DefaultTableModel modeloReporte =
            new DefaultTableModel(new String[]{"Región", "Número de países"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
    private final JTextArea txtReporte = new JTextArea(5, 40);

    public FrmPais() {
        super("Monstruos S.A. — Archivo de Países (REST Countries + MySQL, MVC)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 680);
        setLocationRelativeTo(null);
        setIconImage(TemaSullivan.icono());
        armarVista();
        conectarEventos();
        cargarDesdeBD();
        avisarSiFaltaKey();
    }

    // ------------------------------------------------------------------ vista

    private void armarVista() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnImportar);
        barra.add(btnApiKey);
        barra.add(lblEstado);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Países", armarPestanaPaises());
        pestanas.addTab("Reportes", armarPestanaReportes());

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(TemaSullivan.crearBanner("Monstruos S.A. — Archivo de Países",
                "Sullivan revisa los registros: sin gritos, solo datos"), BorderLayout.NORTH);
        norte.add(barra, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(norte, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);
    }

    private JPanel armarPestanaPaises() {
        cmbRegion.addItem("Todas");
        for (String r : PaisController.REGIONES.keySet()) {
            cmbRegion.addItem(r);
        }

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("País en la API:"));
        fila1.add(txtApi);
        fila1.add(btnBuscarApi);
        fila1.add(btnGuardarApi);
        btnGuardarApi.setEnabled(false);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("En la BD:"));
        fila2.add(txtBuscar);
        fila2.add(btnBuscar);
        fila2.add(new JLabel("Región:"));
        fila2.add(cmbRegion);
        fila2.add(btnRegion);
        fila2.add(btnTodos);

        JPanel norte = new JPanel(new GridLayout(2, 1));
        norte.add(fila1);
        norte.add(fila2);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        TemaSullivan.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);

        JPanel form = new JPanel(new GridLayout(2, 5, 6, 2));
        for (String t : new String[]{"Nombre", "Capital", "Región (API)", "Población", "Moneda"}) {
            form.add(new JLabel(t));
        }
        form.add(txtNombre);
        form.add(txtCapital);
        form.add(txtRegion);
        form.add(txtPoblacion);
        form.add(txtMoneda);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnNuevo);
        botones.add(btnCrear);
        botones.add(btnActualizar);
        botones.add(btnEliminar);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBorder(BorderFactory.createTitledBorder("Mantenimiento (CRUD)"));
        sur.add(form, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);

        JPanel p = new JPanel(new BorderLayout());
        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private JPanel armarPestanaReportes() {
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btnReporte);

        JPanel p = new JPanel(new BorderLayout());
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(modeloReporte)), BorderLayout.CENTER);
        p.add(new JScrollPane(txtReporte), BorderLayout.SOUTH);
        return p;
    }

    // ---------------------------------------------------------------- eventos

    private void conectarEventos() {
        btnImportar.addActionListener(e -> importar());
        btnApiKey.addActionListener(e -> pedirApiKey());
        btnBuscarApi.addActionListener(e -> consultarApi());
        btnGuardarApi.addActionListener(e -> guardarResultadosApi());
        btnBuscar.addActionListener(e -> buscarPorNombre());
        txtBuscar.addActionListener(e -> buscarPorNombre());
        txtApi.addActionListener(e -> consultarApi());
        btnRegion.addActionListener(e -> filtrarRegion());
        btnTodos.addActionListener(e -> cargarDesdeBD());
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnCrear.addActionListener(e -> crear());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnReporte.addActionListener(e -> generarReporte());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pasarSeleccionAlFormulario();
            }
        });
    }

    // Importar todo desde la API (en segundo plano para no congelar la ventana)
    private void importar() {
        habilitarAcciones(false);
        lblEstado.setText("Importando...");
        new SwingWorker<Integer, int[]>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return controller.importarPaises((hechos, total) ->
                        publish(new int[]{hechos, total}));
            }

            @Override
            protected void process(List<int[]> trozos) {
                int[] u = trozos.get(trozos.size() - 1);
                lblEstado.setText("Importando... " + u[0] + " / " + u[1]);
            }

            @Override
            protected void done() {
                habilitarAcciones(true);
                try {
                    int n = get();
                    lblEstado.setText("Importación finalizada: " + n + " países.");
                    JOptionPane.showMessageDialog(FrmPais.this,
                            "Importación Finalizada (" + n + " países)");
                    cargarDesdeBD();
                } catch (Exception ex) {
                    lblEstado.setText("Error al importar.");
                    mostrarError(ex);
                }
            }
        }.execute();
    }

    // Actividad 1
    private void consultarApi() {
        String nombre = txtApi.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe el nombre de un país (ej. ecuador).");
            return;
        }
        habilitarAcciones(false);
        lblEstado.setText("Consultando la API...");
        new SwingWorker<List<Pais>, Void>() {
            @Override
            protected List<Pais> doInBackground() throws Exception {
                return controller.buscarEnApi(nombre);
            }

            @Override
            protected void done() {
                habilitarAcciones(true);
                try {
                    List<Pais> r = get();
                    mostrar(r, true);
                    lblEstado.setText(r.size() + " resultado(s) de la API para \"" + nombre + "\".");
                    btnGuardarApi.setEnabled(!r.isEmpty());
                } catch (Exception ex) {
                    lblEstado.setText("Error al consultar la API.");
                    mostrarError(ex);
                }
            }
        }.execute();
    }

    private void guardarResultadosApi() {
        try {
            int n = controller.guardarEnBD(mostrados);
            lblEstado.setText(n + " país(es) guardado(s) en la BD.");
            cargarDesdeBD();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void buscarPorNombre() {
        try {
            mostrar(controller.buscarPorNombreBD(txtBuscar.getText().trim()), false);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    // Actividad 4
    private void filtrarRegion() {
        String sel = (String) cmbRegion.getSelectedItem();
        if (sel == null || sel.equals("Todas")) {
            cargarDesdeBD();
            return;
        }
        try {
            mostrar(controller.buscarPorRegion(sel), false);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void cargarDesdeBD() {
        try {
            mostrar(controller.listar(), false);
        } catch (SQLException ex) {
            lblEstado.setText("Sin conexión a MySQL.");
            mostrarError(ex);
        }
    }

    // Actividad 2: CRUD
    private void crear() {
        try {
            controller.crear(leerFormulario(0));
            cargarDesdeBD();
            limpiarFormulario();
            lblEstado.setText("País creado.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        Pais sel = paisSeleccionado();
        if (sel == null || mostrandoApi) {
            JOptionPane.showMessageDialog(this, "Selecciona un país de la base de datos.");
            return;
        }
        try {
            controller.actualizar(leerFormulario(sel.getId()));
            cargarDesdeBD();
            lblEstado.setText("País actualizado.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        Pais sel = paisSeleccionado();
        if (sel == null || mostrandoApi) {
            JOptionPane.showMessageDialog(this, "Selecciona un país de la base de datos.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "¿Eliminar \"" + sel.getNombre() + "\"?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.eliminar(sel.getId());
            cargarDesdeBD();
            limpiarFormulario();
            lblEstado.setText("País eliminado.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    // Actividad 5
    private void generarReporte() {
        try {
            PaisController.Reporte r = controller.generarReporte();
            modeloReporte.setRowCount(0);
            r.paisesPorRegion.forEach((reg, n) -> modeloReporte.addRow(new Object[]{reg, n}));
            StringBuilder sb = new StringBuilder();
            sb.append("Total de países en la BD : ").append(r.totalPaises).append('\n');
            if (r.masPoblado != null) {
                sb.append("País más poblado         : ").append(r.masPoblado.getNombre())
                        .append(" (").append(fmt.format(r.masPoblado.getPoblacion()))
                        .append(" hab.)\n");
            }
            if (r.regionConMasPaises != null) {
                sb.append("Región con más países    : ").append(r.regionConMasPaises)
                        .append(" (").append(r.paisesPorRegion.get(r.regionConMasPaises))
                        .append(")\n");
            } else {
                sb.append("No hay datos: importa los países primero.\n");
            }
            txtReporte.setText(sb.toString());
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    // ---------------------------------------------------------------- ayudas

    private void mostrar(List<Pais> lista, boolean deApi) {
        mostrados = lista;
        mostrandoApi = deApi;
        btnGuardarApi.setEnabled(deApi && !lista.isEmpty());
        modelo.setRowCount(0);
        for (Pais p : lista) {
            modelo.addRow(new Object[]{
                deApi ? "" : p.getId(), p.getNombre(), p.getCapital(), p.getRegion(),
                fmt.format(p.getPoblacion()), p.getMoneda()});
        }
        if (!deApi) {
            lblEstado.setText(lista.size() + " país(es) en la base de datos.");
        }
    }

    private Pais paisSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int idx = tabla.convertRowIndexToModel(fila);
        return idx < mostrados.size() ? mostrados.get(idx) : null;
    }

    private void pasarSeleccionAlFormulario() {
        Pais p = paisSeleccionado();
        if (p == null) {
            return;
        }
        txtNombre.setText(p.getNombre());
        txtCapital.setText(p.getCapital());
        txtRegion.setText(p.getRegion());
        txtPoblacion.setText(String.valueOf(p.getPoblacion()));
        txtMoneda.setText(p.getMoneda());
    }

    private Pais leerFormulario(int id) {
        long poblacion;
        try {
            String t = txtPoblacion.getText().trim();
            poblacion = t.isEmpty() ? 0 : Long.parseLong(t);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La población debe ser un número entero.");
        }
        Pais p = new Pais(txtNombre.getText().trim(), txtCapital.getText().trim(),
                txtRegion.getText().trim(), poblacion, txtMoneda.getText().trim());
        p.setId(id);
        return p;
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        txtNombre.setText("");
        txtCapital.setText("");
        txtRegion.setText("");
        txtPoblacion.setText("");
        txtMoneda.setText("");
    }

    private void habilitarAcciones(boolean on) {
        btnImportar.setEnabled(on);
        btnBuscarApi.setEnabled(on);
    }

    private void pedirApiKey() {
        String actual = Config.getApiKey();
        String key = JOptionPane.showInputDialog(this,
                "Pega tu API key de REST Countries\n(https://restcountries.com/sign-up).\n"
                + "Se guarda en config.properties.", actual);
        if (key == null) {
            return;
        }
        try {
            Config.setApiKey(key);
            lblEstado.setText(key.isBlank() ? "API key borrada." : "API key guardada.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void avisarSiFaltaKey() {
        if (Config.getApiKey().isEmpty()) {
            lblEstado.setText("Falta la API key: usa el botón \"API Key...\" antes de importar.");
        }
    }

    private void mostrarError(Throwable ex) {
        Throwable causa = ex.getCause() != null
                && (ex instanceof java.util.concurrent.ExecutionException) ? ex.getCause() : ex;
        String titulo = causa instanceof ApiException ? "Error de la API"
                : causa instanceof SQLException ? "Error de base de datos" : "Aviso";
        JOptionPane.showMessageDialog(this, causa.getMessage(), titulo,
                JOptionPane.ERROR_MESSAGE);
    }
}
