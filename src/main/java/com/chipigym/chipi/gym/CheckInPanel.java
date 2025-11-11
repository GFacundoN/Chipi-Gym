package com.chipigym.chipi.gym;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel para gestionar check-ins con escaneo de códigos QR
 */
public class CheckInPanel extends JPanel {
    
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    
    private final CheckInDAO checkInDAO = new CheckInDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    
    private QRScanner qrScanner;
    private WebcamPanel webcamPanel;
    private JButton btnIniciarCamara;
    private JButton btnDetenerCamara;
    private JLabel lblEstado;
    private JLabel lblUltimoCliente;
    private JTable tablaCheckIns;
    private DefaultTableModel tablaModel;
    private Timer scanTimer;
    private boolean escaneando = false;
    private Integer ultimoClienteEscaneado = null;
    private long ultimoTiempoEscaneo = 0;
    private static final long COOLDOWN_MS = 3000; // 3 segundos entre escaneos del mismo cliente
    
    public CheckInPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }
    
    private void initComponents() {
        // Panel principal dividido en dos: cámara y registros
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Panel izquierdo: Cámara y controles
        JPanel panelCamara = crearPanelCamara();
        splitPane.setLeftComponent(panelCamara);
        
        // Panel derecho: Tabla de check-ins
        JPanel panelRegistros = crearPanelRegistros();
        splitPane.setRightComponent(panelRegistros);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelCamara() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Tarjeta de la cámara
        JPanel cardCamara = new JPanel(new BorderLayout());
        cardCamara.setBackground(CARD_COLOR);
        cardCamara.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Título
        JLabel titulo = new JLabel("Escanear Código QR");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT_PRIMARY);
        cardCamara.add(titulo, BorderLayout.NORTH);
        
        // Panel para la webcam
        JPanel webcamContainer = new JPanel(new BorderLayout());
        webcamContainer.setBackground(Color.BLACK);
        webcamContainer.setPreferredSize(new Dimension(400, 300));
        webcamContainer.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        
        JLabel lblSinCamara = new JLabel("Cámara no iniciada", SwingConstants.CENTER);
        lblSinCamara.setForeground(Color.WHITE);
        lblSinCamara.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        webcamContainer.add(lblSinCamara, BorderLayout.CENTER);
        
        cardCamara.add(webcamContainer, BorderLayout.CENTER);
        
        // Panel de controles
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        controles.setBackground(CARD_COLOR);
        
        btnIniciarCamara = crearBoton("Iniciar Cámara", PRIMARY_COLOR);
        btnDetenerCamara = crearBoton("Detener Cámara", ERROR_COLOR);
        btnDetenerCamara.setEnabled(false);
        
        btnIniciarCamara.addActionListener(e -> iniciarCamara(webcamContainer));
        btnDetenerCamara.addActionListener(e -> detenerCamara());
        
        controles.add(btnIniciarCamara);
        controles.add(btnDetenerCamara);
        
        cardCamara.add(controles, BorderLayout.SOUTH);
        
        panel.add(cardCamara, BorderLayout.CENTER);
        
        // Panel de estado
        JPanel cardEstado = new JPanel(new BorderLayout());
        cardEstado.setBackground(CARD_COLOR);
        cardEstado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        lblEstado = new JLabel("Esperando...");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(TEXT_PRIMARY);
        
        lblUltimoCliente = new JLabel(" ");
        lblUltimoCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUltimoCliente.setForeground(new Color(107, 114, 128));
        
        JPanel estadoTextos = new JPanel(new GridLayout(2, 1, 0, 5));
        estadoTextos.setBackground(CARD_COLOR);
        estadoTextos.add(lblEstado);
        estadoTextos.add(lblUltimoCliente);
        
        cardEstado.add(estadoTextos, BorderLayout.CENTER);
        
        panel.add(cardEstado, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelRegistros() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Título
        JLabel titulo = new JLabel("Check-ins de Hoy");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        // Tabla de check-ins
        String[] columnas = {"Hora", "Cliente", "DNI", "Estado Membresía", "Días Rest."};
        tablaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCheckIns = new JTable(tablaModel);
        estilizarTabla(tablaCheckIns);
        
        JScrollPane scroll = new JScrollPane(tablaCheckIns);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);
        
        // Botón para actualizar
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(CARD_COLOR);
        panelBoton.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnActualizar = crearBoton("Actualizar", new Color(107, 114, 128));
        btnActualizar.addActionListener(e -> cargarCheckInsHoy());
        panelBoton.add(btnActualizar);
        
        panel.add(panelBoton, BorderLayout.SOUTH);
        
        // Cargar datos iniciales
        cargarCheckInsHoy();
        
        return panel;
    }
    
    private void iniciarCamara(JPanel container) {
        try {
            lblEstado.setText("Iniciando cámara...");
            lblEstado.setForeground(WARNING_COLOR);
            
            qrScanner = new QRScanner();
            if (!qrScanner.inicializarWebcam()) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo inicializar la cámara.\nVerifique que esté conectada y no esté en uso.",
                    "Error de Cámara",
                    JOptionPane.ERROR_MESSAGE);
                lblEstado.setText("Error al iniciar cámara");
                lblEstado.setForeground(ERROR_COLOR);
                return;
            }
            
            Webcam webcam = qrScanner.getWebcam();
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setFPSDisplayed(false);
            webcamPanel.setDisplayDebugInfo(false);
            webcamPanel.setMirrored(true);
            
            container.removeAll();
            container.add(webcamPanel, BorderLayout.CENTER);
            container.revalidate();
            container.repaint();
            
            btnIniciarCamara.setEnabled(false);
            btnDetenerCamara.setEnabled(true);
            escaneando = true;
            
            lblEstado.setText("Cámara activa - Acerque el código QR");
            lblEstado.setForeground(SUCCESS_COLOR);
            
            // Iniciar escaneo periódico
            iniciarEscaneoAutomatico();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al iniciar la cámara: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            lblEstado.setText("Error");
            lblEstado.setForeground(ERROR_COLOR);
        }
    }
    
    private void detenerCamara() {
        escaneando = false;
        
        if (scanTimer != null) {
            scanTimer.stop();
        }
        
        if (qrScanner != null) {
            qrScanner.cerrar();
        }
        
        // Resetear cooldown
        ultimoClienteEscaneado = null;
        ultimoTiempoEscaneo = 0;
        
        btnIniciarCamara.setEnabled(true);
        btnDetenerCamara.setEnabled(false);
        
        lblEstado.setText("Cámara detenida");
        lblEstado.setForeground(TEXT_PRIMARY);
        lblUltimoCliente.setText(" ");
    }
    
    private void iniciarEscaneoAutomatico() {
        scanTimer = new Timer(200, e -> { // Reducido a 200ms para detectar más rápido
            if (!escaneando || qrScanner == null) {
                return;
            }
            
            Integer idCliente = qrScanner.escanearQR();
            if (idCliente != null) {
                // Verificar cooldown para evitar múltiples registros
                long tiempoActual = System.currentTimeMillis();
                if (ultimoClienteEscaneado != null && 
                    ultimoClienteEscaneado.equals(idCliente) && 
                    (tiempoActual - ultimoTiempoEscaneo) < COOLDOWN_MS) {
                    // Ignorar, está en cooldown
                    return;
                }
                
                // Actualizar último escaneo
                ultimoClienteEscaneado = idCliente;
                ultimoTiempoEscaneo = tiempoActual;
                
                procesarCheckIn(idCliente);
            }
        });
        scanTimer.start();
    }
    
    private void procesarCheckIn(int idCliente) {
        try {
            // Obtener información del cliente
            Cliente cliente = clienteDAO.obtenerPorId(idCliente);
            if (cliente == null) {
                mostrarError("Cliente no encontrado (ID: " + idCliente + ")");
                return;
            }
            
            // Verificar estado de membresía
            Membresia membresia = membresiaDAO.obtenerMembresiaActiva(idCliente);
            if (membresia == null) {
                mostrarAdvertencia(cliente.getNombreCompleto() + " - SIN MEMBRESÍA ACTIVA");
                return;
            }
            
            int diasRestantes = membresia.calcularDiasRestantes();
            if (diasRestantes < 0) {
                mostrarAdvertencia(cliente.getNombreCompleto() + " - MEMBRESÍA VENCIDA");
                return;
            }
            
            // Registrar check-in
            CheckIn checkIn = new CheckIn(idCliente);
            int idCheckIn = checkInDAO.registrarCheckIn(checkIn);
            
            if (idCheckIn > 0) {
                String mensaje = cliente.getNombreCompleto() + " - Check-in exitoso";
                if (diasRestantes <= 5) {
                    mensaje += " (⚠ " + diasRestantes + " días restantes)";
                }
                mostrarExito(mensaje);
                cargarCheckInsHoy();
                
                // Reproducir sonido de éxito (opcional)
                Toolkit.getDefaultToolkit().beep();
            } else {
                mostrarError("Error al registrar check-in");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error: " + e.getMessage());
        }
    }
    
    private void cargarCheckInsHoy() {
        tablaModel.setRowCount(0);
        List<CheckIn> checkIns = checkInDAO.obtenerCheckInsHoy();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        for (CheckIn checkIn : checkIns) {
            String hora = checkIn.getFechaHora().format(formatter);
            String estadoMembresia = checkIn.getEstadoMembresia() != null ? 
                checkIn.getEstadoMembresia() : "SIN MEMBRESÍA";
            String diasRestantes = checkIn.getDiasMembresiaRestantes() != null ? 
                String.valueOf(checkIn.getDiasMembresiaRestantes()) : "N/A";
            
            tablaModel.addRow(new Object[]{
                hora,
                checkIn.getNombreCliente(),
                checkIn.getDniCliente(),
                estadoMembresia,
                diasRestantes
            });
        }
    }
    
    private void mostrarExito(String mensaje) {
        lblEstado.setText("✓ " + mensaje);
        lblEstado.setForeground(SUCCESS_COLOR);
        lblUltimoCliente.setText("Último registro: " + java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private void mostrarAdvertencia(String mensaje) {
        lblEstado.setText("⚠ " + mensaje);
        lblEstado.setForeground(WARNING_COLOR);
        lblUltimoCliente.setText("Alerta: " + java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss")));
        Toolkit.getDefaultToolkit().beep();
        Toolkit.getDefaultToolkit().beep(); // Doble beep para advertencia
    }
    
    private void mostrarError(String mensaje) {
        lblEstado.setText("✗ " + mensaje);
        lblEstado.setForeground(ERROR_COLOR);
        lblUltimoCliente.setText("Error: " + java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        return btn;
    }
    
    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(35);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(224, 231, 255));
        tabla.setSelectionForeground(TEXT_PRIMARY);
        
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(249, 250, 251));
        tabla.getTableHeader().setForeground(TEXT_PRIMARY);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, 
            new Color(229, 231, 235)));
    }
    
    /**
     * Método para limpiar recursos al cerrar el panel
     */
    public void limpiar() {
        detenerCamara();
    }
}
