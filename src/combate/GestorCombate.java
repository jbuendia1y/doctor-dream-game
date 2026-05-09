package combate;

import cartas.Carta;
import entidades.Combatiente;
import entidades.Jugador;

public class GestorCombate {

    private Combatiente jugador;
    private Combatiente enemigo;
    private boolean turnoJugador = true;
    private boolean terminado = false;

    public GestorCombate(Combatiente jugador, Combatiente enemigo) {
        this.jugador = jugador;
        this.enemigo = enemigo;
    }

    // El controlador debe llamar este método para usar la carta
    public void jugarCarta(Jugador jugadorReal, Carta carta) {
        if (!turnoJugador || terminado) return;

        carta.usar(jugadorReal, enemigo);
        verificarFin();

        if (!terminado) {
            turnoJugador = false;
        }
    }

    public int ejecutarTurnoEnemigo() {
        if (turnoJugador || terminado) return 0;

        int danio = 5 + (int) (Math.random() * 6);
        jugador.recibirDanio(danio);

        verificarFin();
        turnoJugador = true;
        return danio;
    }

    private void verificarFin() {
        if (!jugador.estaVivo() || !enemigo.estaVivo()) {
            terminado = true;
        }
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

    public Combatiente getEnemigo() {
        return enemigo;
    }
}