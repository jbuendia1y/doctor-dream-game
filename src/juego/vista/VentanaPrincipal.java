package juego.vista;

import java.awt.CardLayout;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import juego.controlador.ControladorCombate;
import juego.controlador.ControladorMenu;
import juego.eventbus.EventBus;
import juego.eventbus.Evento;

public class VentanaPrincipal extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel panelPrincipal;
    private final PanelMenu panelMenu;

    public VentanaPrincipal(ControladorMenu controladorMenu) {
        setTitle("Doctor Dream - Guardianes del Cuerpo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        panelMenu = new PanelMenu(controladorMenu);
        panelPrincipal.add(panelMenu, "menu");

        add(panelPrincipal);
        suscribirEventos();
    }

    private void suscribirEventos() {
        EventBus bus = EventBus.getInstancia();

        Consumer<Object> mostrarMenu = datos -> cardLayout.show(panelPrincipal, "menu");
        bus.suscribir(Evento.MOSTRAR_MENU, mostrarMenu);

        bus.suscribir(Evento.MOSTRAR_COMBATE, datos -> {
            JPanel antiguo = findPanel("combate");
            if (antiguo != null) {
                panelPrincipal.remove(antiguo);
            }
            ControladorCombate ctrl = (ControladorCombate) datos;
            PanelCombate panelCombate = new PanelCombate(ctrl);
            panelCombate.setName("combate");
            panelPrincipal.add(panelCombate, "combate");
            cardLayout.show(panelPrincipal, "combate");
        });
    }

    private JPanel findPanel(String name) {
        for (java.awt.Component c : panelPrincipal.getComponents()) {
            if (name.equals(c.getName())) {
                return (JPanel) c;
            }
        }
        return null;
    }
}
