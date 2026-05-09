package doctordream;

import javax.swing.SwingUtilities;
import juego.controlador.ControladorMenu;
import juego.vista.VentanaPrincipal;

public class DoctorDream {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ControladorMenu controladorMenu = new ControladorMenu();
            VentanaPrincipal ventana = new VentanaPrincipal(controladorMenu);
            ventana.setVisible(true);
        });
    }
}
