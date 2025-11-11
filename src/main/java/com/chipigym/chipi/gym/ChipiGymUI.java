package com.chipigym.chipi.gym;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * interfaz gráfica principal del sistema chipi gym
 * maneja el alta de clientes y la búsqueda/visualización de registros
 */
public class ChipiGymUI extends JFrame {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    
    // paleta de colores moderna
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);      // Indigo
    private static final Color PRIMARY_HOVER = new Color(99, 90, 249);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);     // Green
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

    // componentes del formulario de alta de cliente
    private JTextField tfNombre;
    private JTextField tfDni;
    private JSpinner spEdad;
    private JTextField tfTelefono;
    private JFormattedTextField tfFechaInscripcion;
    private JCheckBox cbApto;
    private JTextField tfArchivoApto;

    // componentes del formulario de búsqueda
    private JTextField bId;
    private JTextField bNombre;
    private JTextField bDni;
    private JTextField bTelefono;
    private JComboBox<String> bApto;
    private JTable tabla;
    private DefaultTableModel tablaModel;
    private int hoveredRow = -1;
    
    // componentes de membresías
    private JTable tablaMembresias;
    private DefaultTableModel tablaMembresiaModel;
    private JTextField mIdCliente;
    private JTextField mDniCliente;
    
    // panel de check-in
    private CheckInPanel checkInPanel;

    public ChipiGymUI() {
        super("Chipi Gym - Gestión de Clientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // establecer color de fondo
        getContentPane().setBackground(BACKGROUND_COLOR);

        // panel de encabezado
        JPanel header = crearHeader();
        add(header, BorderLayout.NORTH);

        // pestañas con estilo moderno
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(CARD_COLOR);
        tabs.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        tabs.addTab(" Alta Cliente  ", crearPanelAlta());
        tabs.addTab(" Buscar Clientes  ", crearPanelBusqueda());
        tabs.addTab(" Membresías  ", crearPanelMembresias());
        tabs.addTab(" Check-In  ", crearPanelCheckIn());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        mainContainer.setBorder(new EmptyBorder(0, 20, 20, 20));
        mainContainer.add(tabs, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }
    
    /**
     * crea el panel de encabezado de la aplicación
     */
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel titulo = new JLabel("Chipi Gym");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        
        JLabel subtitulo = new JLabel("Sistema de Gestión de Clientes");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(224, 231, 255));
        
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 5));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);
        
        header.add(textos, BorderLayout.WEST);
        
        return header;
    }

    /**
     * crea el panel para dar de alta nuevos clientes
     */
    private JPanel crearPanelAlta() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_COLOR);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // panel tipo tarjeta
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;

        tfNombre = crearTextField(30);
        tfDni = crearTextField(15);
        spEdad = new JSpinner(new SpinnerNumberModel(18, 0, 120, 1));
        estilizarSpinner(spEdad);
        tfTelefono = crearTextField(15);
        tfFechaInscripcion = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        tfFechaInscripcion.setColumns(15);
        tfFechaInscripcion.setValue(new Date());
        tfFechaInscripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbApto = new JCheckBox("Tiene apto físico");
        cbApto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbApto.setBackground(CARD_COLOR);
        tfArchivoApto = crearTextField(25);
        tfArchivoApto.setEditable(false);
        JButton btnExaminar = crearBotonSecundario("Seleccionar archivo");
        btnExaminar.addActionListener(this::seleccionarArchivoApto);

        // construir formulario fila por fila
        form.add(crearLabel("Nombre completo *"), c);
        c.gridx = 1; form.add(tfNombre, c);
        // Fila 1
        c.gridx = 0; c.gridy++; form.add(crearLabel("DNI *"), c);
        c.gridx = 1; form.add(tfDni, c);
        // Fila 2
        c.gridx = 0; c.gridy++; form.add(crearLabel("Edad *"), c);
        c.gridx = 1; form.add(spEdad, c);
        // Fila 3
        c.gridx = 0; c.gridy++; form.add(crearLabel("Teléfono *"), c);
        c.gridx = 1; form.add(tfTelefono, c);
        // Fila 4
        c.gridx = 0; c.gridy++; form.add(crearLabel("Fecha inscripción (yyyy-MM-dd) *"), c);
        c.gridx = 1; form.add(tfFechaInscripcion, c);
        // Fila 5
        c.gridx = 0; c.gridy++; form.add(crearLabel("Apto físico"), c);
        c.gridx = 1; form.add(cbApto, c);
        // Fila 6
        c.gridx = 0; c.gridy++; form.add(crearLabel("Archivo apto (opcional)"), c);
        JPanel archivoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        archivoPanel.setBackground(CARD_COLOR);
        archivoPanel.add(tfArchivoApto);
        archivoPanel.add(btnExaminar);
        c.gridx = 1; form.add(archivoPanel, c);

        // botón para guardar el cliente
        JButton btnGuardar = crearBotonPrimario("Guardar Cliente");
        btnGuardar.addActionListener(this::guardarCliente);
        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        c.insets = new Insets(20, 10, 10, 10);
        form.add(btnGuardar, c);

        card.add(form, BorderLayout.CENTER);
        
        // envolver en scroll para permitir desplazamiento
        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        container.add(scroll, BorderLayout.CENTER);
        
        return container;
    }

    /**
     * crea el panel para buscar y visualizar clientes
     */
    private JPanel crearPanelBusqueda() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_COLOR);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // tarjeta para los filtros de búsqueda
        JPanel cardFiltros = new JPanel(new BorderLayout());
        cardFiltros.setBackground(CARD_COLOR);
        cardFiltros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 30, 20, 30)
        ));

        JPanel filtros = new JPanel(new GridBagLayout());
        filtros.setBackground(CARD_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        bId = crearTextField(10);
        bNombre = crearTextField(20);
        bDni = crearTextField(15);
        bTelefono = crearTextField(15);
        bApto = new JComboBox<>(new String[]{"Cualquiera", "Sí", "No"});
        bApto.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        int y = 0;
        c.gridx = 0; c.gridy = y; filtros.add(crearLabel("ID:"), c);
        c.gridx = 1; filtros.add(bId, c);
        c.gridx = 2; filtros.add(crearLabel("Nombre:"), c);
        c.gridx = 3; filtros.add(bNombre, c);
        y++;
        c.gridx = 0; c.gridy = y; filtros.add(crearLabel("DNI:"), c);
        c.gridx = 1; filtros.add(bDni, c);
        c.gridx = 2; filtros.add(crearLabel("Teléfono:"), c);
        c.gridx = 3; filtros.add(bTelefono, c);
        y++;
        c.gridx = 0; c.gridy = y; filtros.add(crearLabel("Apto físico:"), c);
        c.gridx = 1; filtros.add(bApto, c);

        JButton btnBuscar = crearBotonPrimario("Buscar");
        btnBuscar.addActionListener(this::buscarClientes);
        JButton btnBuscarId = crearBotonSecundario("Buscar por ID");
        btnBuscarId.addActionListener(this::buscarPorId);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        acciones.setBackground(CARD_COLOR);
        acciones.add(btnBuscar);
        acciones.add(btnBuscarId);

        cardFiltros.add(filtros, BorderLayout.CENTER);
        cardFiltros.add(acciones, BorderLayout.SOUTH);

        // tarjeta para la tabla de resultados
        JPanel cardTabla = new JPanel(new BorderLayout());
        cardTabla.setBackground(CARD_COLOR);
        cardTabla.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // tabla de resultados
        String[] columnas = {"ID", "Nombre", "DNI", "Edad", "Teléfono", "Fecha Inscripción", "Apto", "Archivo", "QR", ""};
        tablaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(tablaModel);
        estilizarTabla(tabla);
        
        // agregar listener para manejar clicks en la columna de archivo y botón eliminar
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabla.rowAtPoint(e.getPoint());
                int col = tabla.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 7) { // columna 7 es "archivo"
                    String filePath = (String) tabla.getValueAt(row, col);
                    if (filePath != null && !filePath.trim().isEmpty()) {
                        abrirArchivo(filePath);
                    }
                } else if (row >= 0 && col == 8) { // columna 8 es botón QR
                    generarYMostrarQR(row);
                } else if (row >= 0 && col == 9) { // columna 9 es botón eliminar
                    eliminarCliente(row);
                }
            }
        });
        
        // agregar listener para efecto hover
        tabla.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tabla.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    tabla.repaint();
                }
            }
        });
        
        // resetear hover cuando el mouse sale de la tabla
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                tabla.repaint();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        cardTabla.add(scroll, BorderLayout.CENTER);

        // diseño principal
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(cardFiltros, BorderLayout.NORTH);
        mainPanel.add(cardTabla, BorderLayout.CENTER);

        container.add(mainPanel, BorderLayout.CENTER);
        return container;
    }

    /**
     * crea el panel para gestionar membresías
     */
    private JPanel crearPanelMembresias() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_COLOR);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // tarjeta superior con opciones de renovación
        JPanel cardRenovacion = new JPanel(new BorderLayout());
        cardRenovacion.setBackground(CARD_COLOR);
        cardRenovacion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        JPanel formRenovacion = new JPanel(new GridBagLayout());
        formRenovacion.setBackground(CARD_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        mIdCliente = crearTextField(10);
        mDniCliente = crearTextField(15);
        
        c.gridx = 0; c.gridy = 0;
        formRenovacion.add(crearLabel("ID Cliente:"), c);
        c.gridx = 1;
        formRenovacion.add(mIdCliente, c);
        
        c.gridx = 2;
        formRenovacion.add(crearLabel("DNI Cliente:"), c);
        c.gridx = 3;
        formRenovacion.add(mDniCliente, c);
        
        JButton btnBuscarCliente = crearBotonPrimario("Buscar Cliente");
        btnBuscarCliente.addActionListener(this::buscarClienteMembresia);
        c.gridx = 4;
        formRenovacion.add(btnBuscarCliente, c);
        
        JButton btnRenovar30 = crearBotonSecundario("Renovar 30 días");
        btnRenovar30.addActionListener(e -> renovarMembresia(30));
        c.gridx = 5;
        formRenovacion.add(btnRenovar30, c);
        
        cardRenovacion.add(formRenovacion, BorderLayout.CENTER);
        
        // tarjeta con tabla de membresías a renovar
        JPanel cardTabla = new JPanel(new BorderLayout());
        cardTabla.setBackground(CARD_COLOR);
        cardTabla.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titulo = new JLabel("Membresías Vencidas o Por Vencer (próximos 5 días)");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        cardTabla.add(titulo, BorderLayout.NORTH);
        
        // tabla de membresías
        String[] columnas = {"ID", "Nombre", "DNI", "Teléfono", "Vencimiento", "Días Restantes", "Acción"};
        tablaMembresiaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMembresias = new JTable(tablaMembresiaModel);
        estilizarTablaMembresias(tablaMembresias);
        
        // listener para renovar al hacer click en la columna de acción
        tablaMembresias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablaMembresias.rowAtPoint(e.getPoint());
                int col = tablaMembresias.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 6) { // columna de acción
                    int idCliente = (Integer) tablaMembresias.getValueAt(row, 0);
                    renovarMembresiaDirecto(idCliente);
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaMembresias);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        cardTabla.add(scroll, BorderLayout.CENTER);
        
        // botón para actualizar la lista
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(CARD_COLOR);
        JButton btnActualizar = crearBotonSecundario("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarMembresiasARenovar());
        panelBotones.add(btnActualizar);
        cardTabla.add(panelBotones, BorderLayout.SOUTH);
        
        // layout principal
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(cardRenovacion, BorderLayout.NORTH);
        mainPanel.add(cardTabla, BorderLayout.CENTER);
        
        container.add(mainPanel, BorderLayout.CENTER);
        
        // cargar datos iniciales
        cargarMembresiasARenovar();
        
        return container;
    }
    
    /**
     * crea el panel de check-in con escáner QR
     */
    private JPanel crearPanelCheckIn() {
        checkInPanel = new CheckInPanel();
        
        // Agregar listener para limpiar recursos al cerrar ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (checkInPanel != null) {
                    checkInPanel.limpiar();
                }
            }
        });
        
        return checkInPanel;
    }

    /**
     * abre un diálogo para seleccionar el archivo del apto físico
     */
    private void seleccionarArchivoApto(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            tfArchivoApto.setText(f.getAbsolutePath());
        }
    }

    /**
     * guarda un nuevo cliente en la base de datos
     * valida los campos obligatorios antes de guardar
     */
    private void guardarCliente(ActionEvent e) {
        try {
            String nombre = tfNombre.getText().trim();
            String dni = tfDni.getText().trim();
            int edad = (Integer) spEdad.getValue();
            String tel = tfTelefono.getText().trim();
            Date fecha = parseFecha(tfFechaInscripcion.getText().trim());
            boolean apto = cbApto.isSelected();
            String archivo = tfArchivoApto.getText().trim();

            if (nombre.isEmpty() || dni.isEmpty() || tel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete los campos obligatorios: Nombre, DNI, Teléfono", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente cte = new Cliente(nombre, dni, edad, tel, fecha);
            cte.setTieneAptoFisico(apto);
            if (!archivo.isEmpty()) cte.setArchivoApto(archivo);

            int id = clienteDAO.agregarCliente(cte);
            if (id > 0) {
                // crear membresía automática de 30 días para el nuevo cliente
                Membresia membresia = new Membresia(id, new Date(), 30);
                int idMembresia = membresiaDAO.agregarMembresia(membresia);
                
                if (idMembresia > 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    JOptionPane.showMessageDialog(this, 
                        "Cliente guardado con ID: " + id + "\n" +
                        "Membresía creada hasta: " + sdf.format(membresia.getFechaFin()), 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Cliente guardado con ID: " + id + "\n" +
                        "ADVERTENCIA: No se pudo crear la membresía automáticamente", 
                        "Éxito Parcial", 
                        JOptionPane.WARNING_MESSAGE);
                }
                limpiarAlta();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * limpia todos los campos del formulario de alta
     */
    private void limpiarAlta() {
        tfNombre.setText("");
        tfDni.setText("");
        spEdad.setValue(18);
        tfTelefono.setText("");
        tfFechaInscripcion.setValue(new Date());
        cbApto.setSelected(false);
        tfArchivoApto.setText("");
    }

    /**
     * busca clientes según los filtros ingresados
     */
    private void buscarClientes(ActionEvent e) {
        try {
            String nombre = safeText(bNombre);
            String dni = safeText(bDni);
            String tel = safeText(bTelefono);
            Boolean apto = switch (bApto.getSelectedIndex()) {
                case 1 -> Boolean.TRUE; // Sí
                case 2 -> Boolean.FALSE; // No
                default -> null; // Cualquiera
            };
            List<Cliente> lista = clienteDAO.buscarClientes(nombre, dni, tel, apto);
            cargarTabla(lista);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error en la búsqueda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * busca un cliente específico por su id
     */
    private void buscarPorId(ActionEvent e) {
        try {
            String txt = bId.getText().trim();
            if (txt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un ID", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(txt);
            Cliente c = clienteDAO.obtenerPorId(id);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "No se encontró cliente con ID: " + id, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla(List.of());
            } else {
                cargarTabla(List.of(c));
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El ID debe ser numérico", "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * carga la lista de clientes en la tabla
     * @param lista lista de clientes a mostrar
     */
    private void cargarTabla(List<Cliente> lista) {
        tablaModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Cliente c : lista) {
            tablaModel.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombreCompleto(),
                    c.getDni(),
                    c.getEdad(),
                    c.getTelefono(),
                    c.getFechaInscripcion() != null ? sdf.format(c.getFechaInscripcion()) : "",
                    c.isTieneAptoFisico() ? "Sí" : "No",
                    c.getArchivoApto() != null ? c.getArchivoApto() : "",
                    "", // QR button column
                    "" // Delete button column
            });
        }
    }
    
    /**
     * genera y muestra el código QR de un cliente
     * @param row fila de la tabla que contiene el cliente
     */
    private void generarYMostrarQR(int row) {
        try {
            int idCliente = (Integer) tabla.getValueAt(row, 0);
            String nombreCliente = (String) tabla.getValueAt(row, 1);
            
            // Generar el código QR
            String rutaQR = QRGenerator.generarYGuardarQR(idCliente, nombreCliente);
            
            if (rutaQR != null) {
                // Mostrar el código QR en un diálogo
                ImageIcon qrIcon = new ImageIcon(rutaQR);
                
                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(new EmptyBorder(15, 15, 15, 15));
                
                JLabel lblTitulo = new JLabel("Código QR - " + nombreCliente);
                lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(lblTitulo, BorderLayout.NORTH);
                
                JLabel lblQR = new JLabel(qrIcon);
                lblQR.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                panel.add(lblQR, BorderLayout.CENTER);
                
                JLabel lblInfo = new JLabel("<html><center>ID Cliente: " + idCliente + 
                    "<br>Archivo guardado en: " + rutaQR + "</center></html>");
                lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(lblInfo, BorderLayout.SOUTH);
                
                JOptionPane.showMessageDialog(this, panel, "Código QR Generado", 
                    JOptionPane.PLAIN_MESSAGE);
                
                // Preguntar si desea abrir la carpeta
                int opcion = JOptionPane.showConfirmDialog(this, 
                    "¿Desea abrir la carpeta con los códigos QR?",
                    "Abrir carpeta",
                    JOptionPane.YES_NO_OPTION);
                
                if (opcion == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File("qr_clientes"));
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo generar el código QR",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al generar código QR: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * elimina un cliente de la base de datos
     * muestra un diálogo de confirmación antes de eliminar
     * @param row fila de la tabla que contiene el cliente a eliminar
     */
    private void eliminarCliente(int row) {
        int idCliente = (Integer) tabla.getValueAt(row, 0);
        String nombreCliente = (String) tabla.getValueAt(row, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar al cliente \"" + nombreCliente + "\"?\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean eliminado = clienteDAO.eliminarCliente(idCliente);
            if (eliminado) {
                JOptionPane.showMessageDialog(
                    this,
                    "Cliente eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // actualizar la tabla eliminando la fila
                tablaModel.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar el cliente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * abre un archivo con la aplicación predeterminada del sistema
     * @param rutaArchivo ruta completa del archivo a abrir
     */
    private void abrirArchivo(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                JOptionPane.showMessageDialog(this, 
                    "El archivo no existe: " + rutaArchivo, 
                    "Archivo no encontrado", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // abrir archivo con la aplicación predeterminada del sistema
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(archivo);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se puede abrir el archivo en este sistema", 
                        "Operación no soportada", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Desktop no está soportado en este sistema", 
                    "Operación no soportada", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al abrir el archivo: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * obtiene el texto de un campo de forma segura
     * @return texto del campo o cadena vacía si es null
     */
    private String safeText(JTextField f) { 
        return f.getText() == null ? "" : f.getText().trim(); 
    }

    /**
     * convierte un string a fecha en formato yyyy-MM-dd
     * @param s string con la fecha
     * @return fecha parseada o fecha actual si el string está vacío
     */
    private Date parseFecha(String s) throws ParseException {
        if (s == null || s.isBlank()) return new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(s);
    }
    
    // ========== métodos para gestión de membresías ==========
    
    /**
     * busca un cliente para gestionar su membresía
     */
    private void buscarClienteMembresia(ActionEvent e) {
        try {
            String idText = mIdCliente.getText().trim();
            String dni = mDniCliente.getText().trim();
            
            Cliente cliente = null;
            
            if (!idText.isEmpty()) {
                int id = Integer.parseInt(idText);
                cliente = clienteDAO.obtenerPorId(id);
            } else if (!dni.isEmpty()) {
                List<Cliente> lista = clienteDAO.buscarClientes("", dni, "", null);
                if (!lista.isEmpty()) {
                    cliente = lista.get(0);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Ingrese ID o DNI del cliente", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente no encontrado", 
                    "Sin resultados", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // mostrar información del cliente y su membresía
            mostrarInfoMembresia(cliente);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "El ID debe ser numérico", 
                "Validación", 
                JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * muestra información de la membresía del cliente
     */
    private void mostrarInfoMembresia(Cliente cliente) {
        Membresia membresia = membresiaDAO.obtenerMembresiaActiva(cliente.getIdCliente());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        StringBuilder info = new StringBuilder();
        info.append("Cliente: ").append(cliente.getNombreCompleto()).append("\n");
        info.append("DNI: ").append(cliente.getDni()).append("\n\n");
        
        if (membresia != null) {
            info.append("Estado de Membresía:\n");
            info.append("Fecha Inicio: ").append(sdf.format(membresia.getFechaInicio())).append("\n");
            info.append("Fecha Fin: ").append(sdf.format(membresia.getFechaFin())).append("\n");
            info.append("Estado: ").append(membresia.getEstado()).append("\n");
            int diasRestantes = membresia.calcularDiasRestantes();
            info.append("Días Restantes: ").append(diasRestantes).append("\n");
            
            if (diasRestantes <= 0) {
                info.append("\n⚠ Membresía VENCIDA");
            } else if (diasRestantes <= 5) {
                info.append("\n⚠ Membresía por vencer pronto");
            }
        } else {
            info.append("⚠ El cliente NO tiene membresía activa");
        }
        
        JOptionPane.showMessageDialog(this, 
            info.toString(), 
            "Información de Membresía", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * renueva la membresía del cliente buscado
     */
    private void renovarMembresia(int dias) {
        try {
            String idText = mIdCliente.getText().trim();
            
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Primero busque un cliente", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int idCliente = Integer.parseInt(idText);
            
            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Renovar membresía por " + dias + " días?",
                "Confirmar Renovación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean renovado = membresiaDAO.renovarMembresia(idCliente, dias);
                if (renovado) {
                    JOptionPane.showMessageDialog(this, 
                        "Membresía renovada exitosamente por " + dias + " días", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarMembresiasARenovar();
                    mIdCliente.setText("");
                    mDniCliente.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo renovar la membresía", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * renueva membresía directamente desde la tabla
     */
    private void renovarMembresiaDirecto(int idCliente) {
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Renovar membresía por 30 días?",
            "Confirmar Renovación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean renovado = membresiaDAO.renovarMembresia(idCliente, 30);
            if (renovado) {
                JOptionPane.showMessageDialog(this, 
                    "Membresía renovada exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarMembresiasARenovar();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo renovar la membresía", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * carga la lista de membresías a renovar
     */
    private void cargarMembresiasARenovar() {
        tablaMembresiaModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<Object[]> lista = membresiaDAO.obtenerMembresiasARenovar(5);
        
        for (Object[] fila : lista) {
            tablaMembresiaModel.addRow(new Object[]{
                fila[0], // idCliente
                fila[1], // nombreCompleto
                fila[2], // dni
                fila[3], // telefono
                sdf.format((Date) fila[4]), // fechaFin
                fila[5], // diasRestantes
                "Renovar" // acción
            });
        }
    }
    
    /**
     * estiliza la tabla de membresías
     */
    private void estilizarTablaMembresias(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(35);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(243, 244, 246));
        tabla.setSelectionBackground(new Color(224, 231, 255));
        tabla.setSelectionForeground(TEXT_PRIMARY);
        
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        // centrar columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tabla.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Días restantes
        
        // renderizador para la columna de acción
        tabla.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JButton btn = new JButton("Renovar");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setForeground(Color.WHITE);
                btn.setBackground(SUCCESS_COLOR);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return btn;
            }
        });
        
        // ajustar anchos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);
    }
    
    // ========== métodos auxiliares para estilizado moderno ==========
    
    /**
     * crea un label con estilo personalizado
     */
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * crea un campo de texto con estilo personalizado
     */
    private JTextField crearTextField(int columnas) {
        JTextField tf = new JTextField(columnas);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }
    
    /**
     * aplica estilos personalizados a un spinner
     */
    private void estilizarSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }
    
    /**
     * crea un botón con estilo primario (color principal)
     */
    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_COLOR);
            }
        });
        
        return btn;
    }
    
    /**
     * crea un botón con estilo secundario (color gris)
     */
    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(new Color(243, 244, 246));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(229, 231, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(243, 244, 246));
            }
        });
        
        return btn;
    }
    
    /**
     * aplica estilos personalizados a la tabla de resultados
     * configura colores, tamaños y renderizadores personalizados
     */
    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(35);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(243, 244, 246));
        tabla.setSelectionBackground(new Color(224, 231, 255));
        tabla.setSelectionForeground(TEXT_PRIMARY);
        
        // estilizar encabezado de la tabla
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        // centrar columnas numéricas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tabla.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Edad
        tabla.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Apto
        
        // renderizador personalizado para la columna de archivo (mostrar como enlace clickeable)
        tabla.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setBorder(new EmptyBorder(5, 10, 5, 10));
                
                if (value != null && !value.toString().trim().isEmpty()) {
                    String filePath = value.toString();
                    String fileName = new File(filePath).getName();
                    // mostrar solo el nombre del archivo como enlace azul subrayado
                    label.setText("<html><u style='color:#4F46E5; cursor:pointer;'>" + fileName + "</u></html>");
                    label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    label.setToolTipText("Click para abrir: " + filePath);
                } else {
                    label.setText("-");
                    label.setForeground(TEXT_SECONDARY);
                }
                
                if (isSelected) {
                    label.setBackground(new Color(224, 231, 255));
                    label.setOpaque(true);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setOpaque(true);
                }
                
                return label;
            }
        });
        
        // renderizador personalizado para la columna del botón QR
        tabla.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JButton btn = new JButton("QR");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setForeground(Color.WHITE);
                btn.setBackground(new Color(79, 70, 229)); // Primary color
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(45, 28));
                btn.setToolTipText("Generar código QR");
                return btn;
            }
        });
        
        // renderizador personalizado para la columna del botón eliminar
        tabla.getColumnModel().getColumn(9).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                panel.setOpaque(true);
                
                if (isSelected) {
                    panel.setBackground(new Color(224, 231, 255));
                } else {
                    panel.setBackground(Color.WHITE);
                }
                
                // solo mostrar botón eliminar cuando se hace hover sobre la fila
                if (row == hoveredRow) {
                    JButton deleteButton = new JButton("X");
                    deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    deleteButton.setForeground(Color.WHITE);
                    deleteButton.setBackground(new Color(220, 38, 38)); // Red color
                    deleteButton.setFocusPainted(false);
                    deleteButton.setBorderPainted(false);
                    deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    deleteButton.setPreferredSize(new Dimension(28, 28));
                    deleteButton.setToolTipText("Eliminar cliente");
                    deleteButton.setMargin(new Insets(0, 0, 0, 0));
                    panel.add(deleteButton);
                }
                
                return panel;
            }
        });
        
        // ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180); // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100); // DNI
        tabla.getColumnModel().getColumn(3).setPreferredWidth(60);  // Edad
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120); // Teléfono
        tabla.getColumnModel().getColumn(5).setPreferredWidth(130); // Fecha
        tabla.getColumnModel().getColumn(6).setPreferredWidth(60);  // Apto
        tabla.getColumnModel().getColumn(7).setPreferredWidth(150); // Archivo
        tabla.getColumnModel().getColumn(8).setPreferredWidth(50);  // QR
        tabla.getColumnModel().getColumn(8).setMaxWidth(50);
        tabla.getColumnModel().getColumn(9).setPreferredWidth(40);  // Delete
        tabla.getColumnModel().getColumn(9).setMaxWidth(40);
    }
}
