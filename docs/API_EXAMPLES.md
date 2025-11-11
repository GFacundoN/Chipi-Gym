# 📚 API & Code Examples

This document provides code examples for developers who want to extend or integrate with Chipi Gym.

## 📁 Table of Contents
- [Client Operations](#client-operations)
- [Membership Operations](#membership-operations)
- [Check-In Operations](#check-in-operations)
- [QR Code Operations](#qr-code-operations)

---

## 👥 Client Operations

### Register a New Client

```java
ClienteDAO clienteDAO = new ClienteDAO();

Cliente cliente = new Cliente(
    "Juan Pérez",           // Nombre completo
    "12345678",             // DNI
    25,                     // Edad
    "+54 9 11 1234-5678",  // Teléfono
    new Date()              // Fecha inscripción
);

cliente.setTieneAptoFisico(true);
cliente.setArchivoApto("/path/to/certificate.pdf");

int idCliente = clienteDAO.agregarCliente(cliente);
System.out.println("Cliente registrado con ID: " + idCliente);
```

### Search Clients

```java
ClienteDAO clienteDAO = new ClienteDAO();

// Search by multiple criteria
List<Cliente> resultados = clienteDAO.buscarClientes(
    "Juan",      // Nombre (partial match)
    "12345",     // DNI (partial match)
    null,        // Teléfono (null = any)
    true         // Solo con apto físico
);

// Search by ID
Cliente cliente = clienteDAO.obtenerPorId(1);

// Get all clients
List<Cliente> todosLosClientes = clienteDAO.obtenerTodos();
```

### Update Client

```java
ClienteDAO clienteDAO = new ClienteDAO();
Cliente cliente = clienteDAO.obtenerPorId(1);

cliente.setTelefono("+54 9 11 9999-8888");
cliente.setTieneAptoFisico(false);

boolean actualizado = clienteDAO.actualizarCliente(cliente);
```

### Delete Client

```java
ClienteDAO clienteDAO = new ClienteDAO();
boolean eliminado = clienteDAO.eliminarCliente(1);
```

---

## 💳 Membership Operations

### Create New Membership

```java
MembresiaDAO membresiaDAO = new MembresiaDAO();

Membresia membresia = new Membresia(
    1,              // ID Cliente
    new Date(),     // Fecha inicio
    30              // Días de duración
);

int idMembresia = membresiaDAO.agregarMembresia(membresia);
```

### Get Active Membership

```java
MembresiaDAO membresiaDAO = new MembresiaDAO();
Membresia membresia = membresiaDAO.obtenerMembresiaActiva(1);

if (membresia != null) {
    int diasRestantes = membresia.calcularDiasRestantes();
    System.out.println("Días restantes: " + diasRestantes);
}
```

### Renew Membership

```java
MembresiaDAO membresiaDAO = new MembresiaDAO();
boolean renovada = membresiaDAO.renovarMembresia(1, 30); // 30 días
```

### Check Memberships to Renew

```java
MembresiaDAO membresiaDAO = new MembresiaDAO();

// Get memberships expiring in 5 days or less
List<Object[]> aRenovar = membresiaDAO.obtenerMembresiasARenovar(5);

for (Object[] fila : aRenovar) {
    int idCliente = (Integer) fila[0];
    String nombre = (String) fila[1];
    int diasRestantes = (Integer) fila[5];
    
    System.out.println(nombre + " - " + diasRestantes + " días");
}
```

---

## 📸 Check-In Operations

### Register Check-In

```java
CheckInDAO checkInDAO = new CheckInDAO();

CheckIn checkIn = new CheckIn(
    1,                                  // ID Cliente
    LocalDateTime.now(),                // Fecha y hora
    CheckIn.TipoRegistro.ENTRADA,       // Tipo
    CheckIn.MetodoRegistro.QR,          // Método
    "Check-in automático via QR"        // Notas (opcional)
);

int idCheckIn = checkInDAO.registrarCheckIn(checkIn);
```

### Get Today's Check-Ins

```java
CheckInDAO checkInDAO = new CheckInDAO();
List<CheckIn> checkInsHoy = checkInDAO.obtenerCheckInsHoy();

for (CheckIn checkIn : checkInsHoy) {
    System.out.println(checkIn.getNombreCliente() + " - " + 
                      checkIn.getFechaHora());
}
```

### Get Check-Ins by Client

```java
CheckInDAO checkInDAO = new CheckInDAO();
List<CheckIn> historial = checkInDAO.obtenerCheckInsPorCliente(1);
```

### Check if Client Already Checked In Today

```java
CheckInDAO checkInDAO = new CheckInDAO();
boolean yaRegistro = checkInDAO.yaHizoCheckInHoy(1);

if (yaRegistro) {
    System.out.println("Cliente ya registró entrada hoy");
}
```

### Get Attendance Statistics

```java
CheckInDAO checkInDAO = new CheckInDAO();

// Check-ins in last 30 days
int asistencias = checkInDAO.contarCheckIns(1, 30);
System.out.println("Asistencias último mes: " + asistencias);
```

---

## 🔲 QR Code Operations

### Generate QR Code for Client

```java
// Simple generation
String rutaQR = QRGenerator.generarYGuardarQR(1, "Juan Pérez");
System.out.println("QR guardado en: " + rutaQR);

// Check if QR already exists
boolean existe = QRGenerator.existeQR(1);

// Generate QR for all clients
ClienteDAO clienteDAO = new ClienteDAO();
List<Cliente> clientes = clienteDAO.obtenerTodos();

for (Cliente cliente : clientes) {
    QRGenerator.generarYGuardarQR(
        cliente.getIdCliente(), 
        cliente.getNombreCompleto()
    );
}
```

### Scan QR Code

```java
QRScanner scanner = new QRScanner();

// Initialize webcam
if (scanner.inicializarWebcam()) {
    System.out.println("Cámara lista");
    
    // Scan for QR
    Integer idCliente = scanner.escanearQR();
    
    if (idCliente != null) {
        System.out.println("Cliente detectado: " + idCliente);
    }
    
    // Close when done
    scanner.cerrar();
}
```

---

## 🔧 Database Connection

### Custom Connection

```java
public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/chipi_gym";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

### Using Connection in DAO

```java
public List<Cliente> obtenerTodos() {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT * FROM Cliente";
    
    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            Cliente cliente = mapCliente(rs);
            lista.add(cliente);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return lista;
}
```

---

## 🎨 UI Examples

### Create Custom Panel

```java
public class MiPanel extends JPanel {
    
    public MiPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(249, 250, 251));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }
    
    private void initComponents() {
        // Your UI code here
    }
}
```

### Add to Main Window

```java
// In ChipiGymUI.java
tabs.addTab(" Mi Pestaña  ", crearMiPanel());

private JPanel crearMiPanel() {
    return new MiPanel();
}
```

---

## 🔐 Input Validation Examples

### Validate DNI

```java
public boolean validarDNI(String dni) {
    if (dni == null || dni.trim().isEmpty()) {
        return false;
    }
    
    // Remove spaces and dashes
    dni = dni.replaceAll("[\\s-]", "");
    
    // Check if it's numeric and has valid length
    return dni.matches("\\d{7,8}");
}
```

### Validate Phone

```java
public boolean validarTelefono(String telefono) {
    if (telefono == null || telefono.trim().isEmpty()) {
        return false;
    }
    
    // Remove spaces, dashes, parentheses
    telefono = telefono.replaceAll("[\\s\\-()]", "");
    
    // Check if it has at least 10 digits
    return telefono.matches("\\+?\\d{10,}");
}
```

---

## 📊 Report Generation Example

### Generate Daily Report

```java
public void generarReporteDiario() {
    CheckInDAO checkInDAO = new CheckInDAO();
    List<CheckIn> checkIns = checkInDAO.obtenerCheckInsHoy();
    
    System.out.println("=== REPORTE DIARIO ===");
    System.out.println("Fecha: " + LocalDate.now());
    System.out.println("Total asistencias: " + checkIns.size());
    System.out.println("\nDetalle:");
    
    for (CheckIn checkIn : checkIns) {
        System.out.printf("%s - %s - %s%n",
            checkIn.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")),
            checkIn.getNombreCliente(),
            checkIn.getMetodoRegistro()
        );
    }
}
```

---

## 🧪 Testing Examples

### Test Client Registration

```java
@Test
public void testAgregarCliente() {
    ClienteDAO dao = new ClienteDAO();
    Cliente cliente = new Cliente("Test User", "99999999", 25, "1234567890", new Date());
    
    int id = dao.agregarCliente(cliente);
    assertTrue(id > 0, "ID debe ser mayor a 0");
    
    // Cleanup
    dao.eliminarCliente(id);
}
```

---

## 📝 Notes

- All examples use the DAO pattern
- Error handling should be added for production code
- Connection resources are automatically closed with try-with-resources
- Always validate user input before database operations

---

For more examples, check the source code in `src/main/java/com/chipigym/chipi/gym/`
