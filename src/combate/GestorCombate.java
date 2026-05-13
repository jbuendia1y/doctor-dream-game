package combate;

import cartas.Carta;
import entidades.Combatiente;
import entidades.Enemigo;
import entidades.Jugador;
import java.util.List;

public class GestorCombate {

    private Combatiente jugador;
    private List<Enemigo> enemigos;
    private boolean turnoJugador = true;
    private boolean terminado = false;

    public GestorCombate(Combatiente jugador, List<Enemigo> enemigos) {
        this.jugador = jugador;
        this.enemigos = enemigos;
    }

    public void jugarCarta(Jugador jugadorReal, Carta carta, Combatiente objetivo) {
        if (!turnoJugador || terminado) return;

        carta.usar(jugadorReal, objetivo);
        verificarFin();

        if (!terminado) {
            turnoJugador = false;
        }
    }

    public int ejecutarAtaqueEnemigo(Enemigo enemigo) {
        if (terminado) return 0;

        int danio = 5 + (int) (Math.random() * 6);
        jugador.recibirDanio(danio);

        verificarFin();
        return danio;
    }

    public void pasarTurnoJugador() {
        turnoJugador = true;
    }

    private void verificarFin() {
        if (!jugador.estaVivo()) {
            terminado = true;
            return;
        }
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                return;
            }
        }
        terminado = true;
    }

    public boolean esTurnoJugador() {
        return turnoJugador;
    }

    public boolean estaTerminado() {
        return terminado;
    }

    public Combatiente getJugador() {
        return jugador;
    }

    public List<Enemigo> getEnemigos() {
        return enemigos;
    }
}
