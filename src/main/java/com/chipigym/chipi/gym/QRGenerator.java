package com.chipigym.chipi.gym;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para generar códigos QR de clientes
 */
public class QRGenerator {
    
    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;
    private static final String QR_DIRECTORY = "qr_clientes";
    
    /**
     * Genera un código QR para un cliente
     * @param idCliente ID del cliente
     * @param nombreCliente Nombre del cliente (para el nombre del archivo)
     * @return BufferedImage con el código QR generado
     */
    public static BufferedImage generarQR(int idCliente, String nombreCliente) throws WriterException {
        // El contenido del QR es simplemente el ID del cliente
        String contenido = String.valueOf(idCliente);
        
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
        
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Genera y guarda un código QR en un archivo
     * @param idCliente ID del cliente
     * @param nombreCliente Nombre del cliente
     * @return Ruta del archivo generado o null si hubo error
     */
    public static String generarYGuardarQR(int idCliente, String nombreCliente) {
        try {
            // Crear directorio si no existe
            Path dirPath = Paths.get(QR_DIRECTORY);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Generar código QR
            BufferedImage qrImage = generarQR(idCliente, nombreCliente);
            
            // Limpiar nombre del cliente para usar en archivo
            String nombreArchivo = nombreCliente.replaceAll("[^a-zA-Z0-9]", "_");
            String rutaArchivo = QR_DIRECTORY + File.separator + "cliente_" + idCliente + "_" + nombreArchivo + ".png";
            
            // Guardar imagen
            File archivoQR = new File(rutaArchivo);
            ImageIO.write(qrImage, "PNG", archivoQR);
            
            return archivoQR.getAbsolutePath();
            
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Genera códigos QR para todos los clientes de una lista
     * @param clientes Lista de clientes
     * @return Número de códigos QR generados exitosamente
     */
    public static int generarQRsParaClientes(java.util.List<Cliente> clientes) {
        int exitosos = 0;
        
        for (Cliente cliente : clientes) {
            String ruta = generarYGuardarQR(cliente.getIdCliente(), cliente.getNombreCompleto());
            if (ruta != null) {
                exitosos++;
            }
        }
        
        return exitosos;
    }
    
    /**
     * Verifica si ya existe un archivo QR para un cliente
     */
    public static boolean existeQR(int idCliente) {
        Path dirPath = Paths.get(QR_DIRECTORY);
        if (!Files.exists(dirPath)) {
            return false;
        }
        
        File[] archivos = dirPath.toFile().listFiles((dir, name) -> 
            name.startsWith("cliente_" + idCliente + "_") && name.endsWith(".png")
        );
        
        return archivos != null && archivos.length > 0;
    }
    
    /**
     * Obtiene la ruta del archivo QR de un cliente si existe
     */
    public static String obtenerRutaQR(int idCliente) {
        Path dirPath = Paths.get(QR_DIRECTORY);
        if (!Files.exists(dirPath)) {
            return null;
        }
        
        File[] archivos = dirPath.toFile().listFiles((dir, name) -> 
            name.startsWith("cliente_" + idCliente + "_") && name.endsWith(".png")
        );
        
        if (archivos != null && archivos.length > 0) {
            return archivos[0].getAbsolutePath();
        }
        
        return null;
    }
}
