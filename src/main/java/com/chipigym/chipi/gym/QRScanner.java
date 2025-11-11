package com.chipigym.chipi.gym;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para escanear códigos QR desde la webcam
 */
public class QRScanner {
    
    private Webcam webcam;
    private MultiFormatReader reader;
    private volatile boolean running = false;
    
    public QRScanner() {
        // Configurar el lector de códigos QR con optimizaciones
        reader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        // POSSIBLE_FORMATS requiere una Collection, no un solo formato
        hints.put(DecodeHintType.POSSIBLE_FORMATS, java.util.Collections.singletonList(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        // Mejoras para detección más rápida y precisa
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE); // Permitir QR con ruido alrededor
        reader.setHints(hints);
    }
    
    /**
     * Inicializa la webcam
     */
    public boolean inicializarWebcam() {
        try {
            webcam = Webcam.getDefault();
            if (webcam == null) {
                System.err.println("No se encontró ninguna webcam");
                return false;
            }
            
            // Configurar resolución
            Dimension[] sizes = webcam.getViewSizes();
            if (sizes.length > 0) {
                // Usar una resolución intermedia
                Dimension size = new Dimension(640, 480);
                webcam.setViewSize(size);
            }
            
            webcam.open();
            running = true;
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene la webcam actual
     */
    public Webcam getWebcam() {
        return webcam;
    }
    
    /**
     * Captura una imagen de la webcam e intenta leer un código QR
     * @return ID del cliente si se detectó un QR válido, null en caso contrario
     */
    public Integer escanearQR() {
        if (webcam == null || !webcam.isOpen()) {
            return null;
        }
        
        try {
            BufferedImage image = webcam.getImage();
            if (image == null) {
                return null;
            }
            
            // Intentar con diferentes estrategias de binarización para mejor detección
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            try {
                Result result = reader.decode(bitmap);
                
                if (result != null) {
                    String texto = result.getText().trim();
                    try {
                        int id = Integer.parseInt(texto);
                        if (id > 0) { // Validar que sea un ID positivo
                            return id;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("QR no contiene un ID numérico válido: " + texto);
                    }
                }
            } catch (NotFoundException e) {
                // No se encontró código QR, intentar con GlobalHistogramBinarizer
                bitmap = new BinaryBitmap(new com.google.zxing.common.GlobalHistogramBinarizer(source));
                try {
                    Result result = reader.decode(bitmap);
                    if (result != null) {
                        String texto = result.getText().trim();
                        int id = Integer.parseInt(texto);
                        if (id > 0) {
                            return id;
                        }
                    }
                } catch (NotFoundException | NumberFormatException ignored) {
                    // No se pudo decodificar ni con el segundo intento
                }
            }
            
        } catch (Exception e) {
            // Solo mostrar errores inesperados
            if (!(e instanceof NotFoundException)) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * Cierra la webcam y libera recursos
     */
    public void cerrar() {
        running = false;
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }
    
    /**
     * Verifica si el scanner está en ejecución
     */
    public boolean isRunning() {
        return running && webcam != null && webcam.isOpen();
    }
    
    /**
     * Obtiene una imagen actual de la webcam
     */
    public BufferedImage capturarImagen() {
        if (webcam != null && webcam.isOpen()) {
            return webcam.getImage();
        }
        return null;
    }
}
