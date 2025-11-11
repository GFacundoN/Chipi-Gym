package com.chipigym.chipi.gym;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * clase principal que inicia la aplicación chipi gym
 */
public class Main {
    public static void main(String[] args) {
        // configurar el tema visual moderno flatlaf
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Error al iniciar FlatLaf");
        }
        
        // iniciar la interfaz gráfica en el hilo de eventos de swing
        SwingUtilities.invokeLater(() -> {
            ChipiGymUI ui = new ChipiGymUI();
            ui.setVisible(true);
        });
    }
}