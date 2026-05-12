package juego.controlador;

import javax.swing.Timer;
import cartas.Carta;
import cartas.CartaAtaque;
import cartas.CartaDefensa;
import cartas.CartaEfecto;
import combate.GestorCombate;
import entidades.Enemigo;
import entidades.Jugador;
import juego.eventbus.EventBus;
import juego.eventbus.Evento;
import mazo.Mazo;

public class ControladorCombate {

    private GestorCombate gestor;
    private Jugador jugador;
    private Enemigo enemigo;

    public void iniciarCombate() {
        Mazo mazo = new Mazo();
        mazo.getPilaRobo().add(new CartaAtaque("Golpe Certero", 1, 15));
        mazo.getPilaRobo().add(new CartaAtaque("Bisturí", 1, 10));
        mazo.getPilaRobo().add(new CartaDefensa("Escudo Celular", 1, 20));
        mazo.getPilaRobo().add(new CartaEfecto("Antibiótico", 2, 5));
        mazo.getPilaRobo().add(new CartaAtaque("Radiación", 2, 20));
        mazo.getPilaRobo().add(new CartaDefensa("Membrana", 1, 15));
        mazo.getPilaRobo().add(new CartaAtaque("Vacuna", 1, 12));
        mazo.getPilaRobo().add(new CartaEfecto("Refuerzo", 2, 8));
        mazo.barajar();

        jugador = new Jugador("Doctor Dream", 100, mazo);
        enemigo = new Enemigo("Virus Letal", 60, "Virus");
        gestor = new GestorCombate(jugador, enemigo);

        for (int i = 0; i < 4; i++) {
            jugador.getMazo().robar();
        }

        EventBus.getInstancia().publicar(Evento.INICIAR_COMBATE, this);
    }

    public void usarCarta(Carta carta) {
        if (!gestor.esTurnoJugador() || gestor.estaTerminado()) {
            return;
        }

        gestor.jugarCarta(jugador, carta);
        jugador.getMazo().descartar(carta);

        EventBus.getInstancia().publicar(Evento.CARTA_USADA, carta);
        EventBus.getInstancia().publicar(Evento.VIDA_ACTUALIZADA, this);

        if (gestor.estaTerminado()) {
            boolean gano = jugador.estaVivo();
            EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, gano);
            return;
        }

        // Esperar a que termine la animación de la carta
        Timer timerCarta = new Timer(900, e1 -> {
            ((Timer) e1.getSource()).stop();

            EventBus.getInstancia().publicar(Evento.TURNO_ENEMIGO);

            int danioRecibido = gestor.ejecutarTurnoEnemigo();

            EventBus.getInstancia().publicar(Evento.ENEMIGO_ATACO, danioRecibido);
            EventBus.getInstancia().publicar(Evento.VIDA_ACTUALIZADA, this);

            if (gestor.estaTerminado()) {
                EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, false);
                return;
            }

            // Esperar a que termine la animación de daño
            Timer timerDanio = new Timer(500, e2 -> {
                ((Timer) e2.getSource()).stop();

                jugador.getMazo().robar();
                EventBus.getInstancia().publicar(Evento.TURNO_JUGADOR, this);
            });
            timerDanio.setRepeats(false);
            timerDanio.start();
        });
        timerCarta.setRepeats(false);
        timerCarta.start();
    }

    public GestorCombate getGestor() {
        return gestor;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Enemigo getEnemigo() {
        return enemigo;
    }
}
