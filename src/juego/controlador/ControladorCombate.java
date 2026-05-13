package juego.controlador;

import javax.swing.Timer;
import cartas.Carta;
import cartas.CartaAtaque;
import cartas.CartaCuracion;
import cartas.CartaDefensa;
import cartas.CartaEfecto;
import combate.GestorCombate;
import entidades.Combatiente;
import entidades.Enemigo;
import entidades.Jugador;
import juego.eventbus.EventBus;
import juego.eventbus.Evento;
import mazo.Mazo;
import java.util.Arrays;
import java.util.List;

public class ControladorCombate {

    private GestorCombate gestor;
    private Jugador jugador;
    private List<Enemigo> enemigos;

    public void iniciarCombate() {
        Mazo mazo = new Mazo();
        mazo.getPilaRobo().add(new CartaAtaque("Paracetamol", 1, 20));
        mazo.getPilaRobo().add(new CartaAtaque("Vacuna Influenza", 1, 20));
        mazo.getPilaRobo().add(new CartaDefensa("Descanso", 1, 20));
        mazo.getPilaRobo().add(new CartaEfecto("Mascarilla", 2, 5));
        mazo.getPilaRobo().add(new CartaCuracion("Sopa de pollo", 1, 10));
        mazo.getPilaRobo().add(new CartaCuracion("Hidratación", 2, 20));
        mazo.barajar();

        jugador = new Jugador("Doctor Dream", 200, mazo);
        enemigos = Arrays.asList(
                new Enemigo("BOSS", 100, "BOSS"),
                new Enemigo("Moco Mutado", 40, "Moco Mutado"),
                new Enemigo("Moquillo", 30, "Moquillo"),
                new Enemigo("Virus", 20, "Virus")
        );
        gestor = new GestorCombate(jugador, enemigos);

        for (int i = 0; i < 4; i++) {
            jugador.getMazo().robar();
        }

        EventBus.getInstancia().publicar(Evento.INICIAR_COMBATE, this);
    }

    public void usarCarta(Carta carta, Combatiente objetivo) {
        if (!gestor.esTurnoJugador() || gestor.estaTerminado()) {
            return;
        }

        gestor.jugarCarta(jugador, carta, objetivo);
        jugador.getMazo().descartar(carta);

        EventBus.getInstancia().publicar(Evento.CARTA_USADA, carta);
        EventBus.getInstancia().publicar(Evento.VIDA_ACTUALIZADA, this);

        if (gestor.estaTerminado()) {
            boolean gano = jugador.estaVivo();
            EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, gano);
            return;
        }

        Timer timerCarta = new Timer(900, e1 -> {
            ((Timer) e1.getSource()).stop();
            ataqueEnemigoSecuencial(0);
        });
        timerCarta.setRepeats(false);
        timerCarta.start();
    }

    private int indiceSiguienteVivo(int desde) {
        for (int i = desde; i < enemigos.size(); i++) {
            if (enemigos.get(i).estaVivo()) return i;
        }
        return -1;
    }

    private void ataqueEnemigoSecuencial(int index) {
        if (gestor.estaTerminado()) return;

        final int nextIndex = indiceSiguienteVivo(index);

        if (nextIndex == -1) {
            if (gestor.estaTerminado()) {
                if (!jugador.estaVivo()) {
                    EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, false);
                }
                return;
            }

            jugador.getMazo().robar();
            gestor.pasarTurnoJugador();
            EventBus.getInstancia().publicar(Evento.TURNO_JUGADOR, this);
            return;
        }

        Enemigo enemigo = enemigos.get(nextIndex);
        EventBus.getInstancia().publicar(Evento.TURNO_ENEMIGO, enemigo);

        int danioRecibido = gestor.ejecutarAtaqueEnemigo(enemigo);

        EventBus.getInstancia().publicar(Evento.ENEMIGO_ATACO, new Object[]{enemigo, danioRecibido});
        EventBus.getInstancia().publicar(Evento.VIDA_ACTUALIZADA, this);

        if (gestor.estaTerminado()) {
            if (!jugador.estaVivo()) {
                EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, false);
            } else {
                EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, true);
            }
            return;
        }

        Timer timerDanio = new Timer(500, e2 -> {
            ((Timer) e2.getSource()).stop();
            ataqueEnemigoSecuencial(nextIndex + 1);
        });
        timerDanio.setRepeats(false);
        timerDanio.start();
    }

    public GestorCombate getGestor() {
        return gestor;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public List<Enemigo> getEnemigos() {
        return enemigos;
    }
}
