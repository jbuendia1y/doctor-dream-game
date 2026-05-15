package juego.guardado;

import java.io.Serializable;

/**
 * Estado serializable de la partida.
 * Por ahora es mínimo, pero se puede extender con:
 * - nivelActual, vidaJugador, mazo, inventario, etc.
 */
public class DatosPartida implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int nivelActual;
    private final long timestamp;

    public DatosPartida(int nivelActual) {
        this.nivelActual = nivelActual;
        this.timestamp = System.currentTimeMillis();
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
