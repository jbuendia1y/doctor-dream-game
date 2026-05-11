package juego.controlador;

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
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * ControladorCombate — único responsable del flujo de turnos.
 *
 * Flujo por turno:
 *   1. Jugador elige carta → animación ACCIÓN (800ms para verla)
 *   2. Por cada enemigo vivo → animación TURNO_ENEMIGO (600ms) + aplicar daño
 *   3. Robar carta → TURNO_JUGADOR
 *
 * Se usa SwingWorker para hacer las pausas SIN congelar la UI,
 * así las animaciones tienen tiempo real de reproducirse.
 */
public class ControladorCombate {

    private GestorCombate  gestor;
    private Jugador        jugador;
    private List<Enemigo>  enemigos;
    private boolean        turnoEnProceso = false; // evita doble click

    public void iniciarCombate() {
        Mazo mazo = new Mazo();
        mazo.getPilaRobo().add(new CartaAtaque("Golpe Certero",   1, 22));
        mazo.getPilaRobo().add(new CartaAtaque("Bisturí",         1, 16));
        mazo.getPilaRobo().add(new CartaAtaque("Radiación",       2, 30));
        mazo.getPilaRobo().add(new CartaAtaque("Vacuna",          1, 18));
        mazo.getPilaRobo().add(new CartaDefensa("Escudo Celular", 1, 25));
        mazo.getPilaRobo().add(new CartaDefensa("Membrana",       1, 20));
        mazo.getPilaRobo().add(new CartaEfecto("Antibiótico",     2,  8));
        mazo.getPilaRobo().add(new CartaEfecto("Refuerzo",        2, 12));
        mazo.barajar();

        // Aqui creamos las stats del jugador y de los enemigos en pantalla
        jugador  = new Jugador("Doctor Dream", 250, mazo);  // 250 HP
        enemigos = new ArrayList<>();
        enemigos.add(new Enemigo("Moquillo",    40,  "Moquillo"));
        enemigos.add(new Enemigo("Moco Mutado", 70,  "Moco Mutado"));
        enemigos.add(new Enemigo("Boss",        150, "Boss"));

        gestor = new GestorCombate(jugador, enemigos);

        for (int i = 0; i < 4; i++) jugador.getMazo().robar();

        EventBus.getInstancia().publicar(Evento.INICIAR_COMBATE, this);
    }

    /**
     * Llamado desde el botón de carta en la UI (hilo EDT).
     * Usa SwingWorker para que las pausas no congelen la pantalla.
     */
    public void usarCarta(Carta carta) {
        // Guard: si ya hay un turno en proceso o el combate terminó, ignorar click
        if (turnoEnProceso || gestor.estaTerminado()) return;
        turnoEnProceso = true;

        // Deshabilitar cartas inmediatamente para evitar doble click
        EventBus.getInstancia().publicar(Evento.BLOQUEAR_CARTAS);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {

                // ── 1. TURNO DEL JUGADOR ──────────────────────────
                // Publicar animación de acción ANTES de aplicar el efecto
                // para que el panel la reciba limpia
                EventBus.getInstancia().publicar(Evento.CARTA_USADA, carta);
                Thread.sleep(900); // tiempo suficiente para ver la animación completa

                // Aplicar efecto de la carta
                boolean mato = gestor.aplicarCarta(carta);
                jugador.getMazo().descartar(carta);
                EventBus.getInstancia().publicar(Evento.VIDA_ACTUALIZADA, null);

                if (mato) {
                    EventBus.getInstancia().publicar(Evento.ENEMIGO_DERROTADO,
                            gestor.getTodosEnemigos().stream()
                                  .filter(e -> !e.estaVivo())
                                  .map(e -> e.getNombre())
                                  .reduce("", (a, b) -> b)); // nombre del último muerto
                    Thread.sleep(600);
                }

                // ¿Ganó el jugador?
                if (gestor.estaTerminado()) {
                    EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, true);
                    return null;
                }

                // ── 2. TURNO DE CADA ENEMIGO VIVO ─────────────────
                for (Enemigo e : gestor.getEnemigosVivos()) {
                    if (gestor.estaTerminado()) break;

                    // Mostrar qué enemigo ataca
                    EventBus.getInstancia().publicar(Evento.NUEVO_ENEMIGO, e);
                    EventBus.getInstancia().publicar(Evento.TURNO_ENEMIGO);
                    Thread.sleep(700); // pausa para ver la animación del enemigo

                    // Aplicar daño
                    int danioReal = gestor.aplicarAtaqueEnemigo(e);
                    EventBus.getInstancia().publicar(Evento.ENEMIGO_ATACO, danioReal);
                    EventBus.getInstancia().publicar(Evento.VIDA_ACTUALIZADA, null);
                    Thread.sleep(400); // pequeña pausa para que el jugador vea que le pegaron

                    // ¿Murió el jugador?
                    if (!jugador.estaVivo()) {
                        EventBus.getInstancia().publicar(Evento.COMBATE_TERMINADO, false);
                        return null;
                    }
                }

                // ── 3. VOLVER AL TURNO DEL JUGADOR ────────────────
                jugador.getMazo().robar();
                EventBus.getInstancia().publicar(Evento.TURNO_JUGADOR);
                return null;
            }

            @Override
            protected void done() {
                turnoEnProceso = false;
            }
        }.execute();
    }

    // ── Getters ───────────────────────────────────────────────────
    public Jugador       getJugador()         { return jugador; }
    public GestorCombate getGestor()          { return gestor; }

    public Enemigo getEnemigo() {
        Enemigo vivo = gestor.primerEnemigoVivo();
        return vivo != null ? vivo : enemigos.get(enemigos.size() - 1);
    }

    public List<Enemigo> getEnemigosActivos() {
        List<Enemigo> vivos = new ArrayList<>(gestor.getEnemigosVivos());
        if (vivos.isEmpty()) vivos.addAll(gestor.getTodosEnemigos());
        return vivos;
    }
}
