package juego.controlador;

import juego.eventbus.EventBus;
import juego.eventbus.Evento;
import juego.guardado.DatosPartida;
import juego.guardado.GestorGuardado;
import juego.modelo.GestorNiveles;
import juego.modelo.Nivel;

public class ControladorMenu {

    public void mostrarMenu() {
        EventBus.getInstancia().publicar(Evento.MOSTRAR_MENU);
    }

    /**
     * Inicia una partida nueva desde cero.
     * Borra cualquier partida guardada anterior y arranca fresh
     * en el consultorio con el nivel 1.
     */
    public void iniciarPartida() {
        GestorGuardado.eliminar();
        GestorGuardado.guardar(new DatosPartida(1));

        Nivel nivel = GestorNiveles.getNivel(0);
        EventBus.getInstancia().publicar(Evento.MOSTRAR_CONSULTORIO, nivel);
    }

    /**
     * Continúa una partida guardada.
     * Carga el nivel guardado y va al consultorio.
     */
    public void continuarPartida() {
        DatosPartida datos = GestorGuardado.cargar();
        if (datos == null) {
            iniciarPartida();
            return;
        }

        Nivel nivel = GestorNiveles.getNivel(datos.getNivelActual() - 1);
        if (nivel == null) {
            nivel = GestorNiveles.getNivel(0);
        }
        EventBus.getInstancia().publicar(Evento.MOSTRAR_CONSULTORIO, nivel);
    }
}
