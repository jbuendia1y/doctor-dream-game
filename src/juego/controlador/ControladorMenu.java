package juego.controlador;

import juego.eventbus.EventBus;
import juego.eventbus.Evento;

public class ControladorMenu {

    public void mostrarMenu() {
        EventBus.getInstancia().publicar(Evento.MOSTRAR_MENU);
    }

    public void iniciarCombate() {
        ControladorCombate ctrl = new ControladorCombate();
        ctrl.iniciarCombate();
        EventBus.getInstancia().publicar(Evento.MOSTRAR_COMBATE, ctrl);
    }
}
