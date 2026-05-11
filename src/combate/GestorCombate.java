package combate;

import cartas.Carta;
import entidades.Combatiente;
import entidades.Enemigo;
import entidades.Jugador;
import java.util.ArrayList;
import java.util.List;

/**
 * GestorCombate — solo administra ESTADO del combate.
 * NO publica eventos, NO avanza turnos internamente.
 * El ControladorCombate es el único que orquesta el flujo.
 */
public class GestorCombate {

    private final Jugador      jugador;
    private final List<Enemigo> enemigos;
    private boolean terminado = false;

    public GestorCombate(Jugador jugador, List<Enemigo> enemigos) {
        this.jugador  = jugador;
        this.enemigos = new ArrayList<>(enemigos);
    }

    /** Aplica el efecto de la carta sobre el primer enemigo vivo. Devuelve true si mató al enemigo. */
    public boolean aplicarCarta(Carta carta) {
        Enemigo objetivo = primerEnemigoVivo();
        if (objetivo == null) { verificarFin(); return false; }
        carta.usar(jugador, objetivo);
        verificarFin();
        return !objetivo.estaVivo();
    }

    /** Un enemigo concreto ataca al jugador. Devuelve el daño real recibido (después del escudo). */
    public int aplicarAtaqueEnemigo(Enemigo e) {
        int vidaAntes = jugador.obtenerVida();
        jugador.recibirDanio(e.calcularDanio());
        int danioReal = vidaAntes - jugador.obtenerVida(); // lo que realmente bajó
        verificarFin();
        return danioReal;
    }

    private void verificarFin() {
        if (!jugador.estaVivo() || todosEnemigosDerotados()) terminado = true;
    }

    private boolean todosEnemigosDerotados() {
        return enemigos.stream().noneMatch(Combatiente::estaVivo);
    }

    public Enemigo primerEnemigoVivo() {
        return enemigos.stream().filter(Combatiente::estaVivo).findFirst().orElse(null);
    }

    public List<Enemigo> getEnemigosVivos() {
        return enemigos.stream().filter(Combatiente::estaVivo).toList();
    }

    public List<Enemigo> getTodosEnemigos() { return enemigos; }
    public boolean estaTerminado()          { return terminado; }
    public boolean ganoJugador()            { return terminado && jugador.estaVivo(); }
    public Jugador getJugador()             { return jugador; }
}
