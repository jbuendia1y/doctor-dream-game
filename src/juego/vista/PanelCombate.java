package juego.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import cartas.Carta;
import cartas.CartaAtaque;
import cartas.CartaDefensa;
import cartas.CartaEfecto;
import entidades.Enemigo;
import juego.controlador.ControladorCombate;
import juego.eventbus.EventBus;
import juego.eventbus.Evento;
import juego.vista.sprite.GestorRecursos;
import juego.vista.sprite.PanelSpritesCentro;

public class PanelCombate extends JPanel {

    private final ControladorCombate controlador;
    private final JLabel lblVidaJugador;
    private final JLabel lblVidaEnemigo;
    private final JLabel lblEscudo;
    private final JLabel lblBono;
    private final JLabel lblTurno;
    private final JLabel lblNombreEnemigo;
    private final JPanel panelMano;
    private final JTextArea areaLog;
    private final PanelSpritesCentro panelSprites;
    private final List<Runnable> limpiezas = new ArrayList<>();

    public PanelCombate(ControladorCombate controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 25, 40));

        // ── HUD superior ──────────────────────────────────────────
        JPanel panelHUD = new JPanel(new GridLayout(1, 4, 10, 0));
        panelHUD.setBackground(new Color(25, 35, 55));

        Enemigo enemigoActual = controlador.getEnemigosActivos().get(0);

        lblVidaJugador = new JLabel("❤️ " + controlador.getJugador().obtenerVida(), SwingConstants.CENTER);
        lblEscudo      = new JLabel("🛡️ " + controlador.getJugador().obtenerEscudo(), SwingConstants.CENTER);
        lblBono        = new JLabel("⚔️ +" + controlador.getJugador().obtenerBonoDanio(), SwingConstants.CENTER);
        lblVidaEnemigo = new JLabel("💀 " + enemigoActual.obtenerVida(), SwingConstants.CENTER);
        lblTurno       = new JLabel("🎲 Tu turno", SwingConstants.CENTER);

        Font hudFont = new Font("Monospaced", Font.BOLD, 16);
        for (JLabel l : new JLabel[]{lblVidaJugador, lblEscudo, lblBono, lblVidaEnemigo, lblTurno}) {
            l.setFont(hudFont);
        }
        lblVidaJugador.setForeground(new Color(80, 220, 80));
        lblEscudo.setForeground(new Color(80, 200, 255));
        lblBono.setForeground(new Color(255, 220, 80));
        lblVidaEnemigo.setForeground(new Color(240, 80, 80));
        lblTurno.setForeground(new Color(80, 220, 80));

        JLabel lblNombreJugador = new JLabel(controlador.getJugador().getNombre(), SwingConstants.CENTER);
        lblNombreJugador.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblNombreJugador.setForeground(new Color(100, 200, 255));

        lblNombreEnemigo = new JLabel(enemigoActual.getNombre(), SwingConstants.CENTER);
        lblNombreEnemigo.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblNombreEnemigo.setForeground(new Color(240, 100, 100));

        JPanel panelJugador = new JPanel(new GridLayout(2, 1));
        panelJugador.setOpaque(false);
        panelJugador.add(lblNombreJugador);
        panelJugador.add(lblVidaJugador);

        JPanel panelBuff = new JPanel(new GridLayout(2, 1));
        panelBuff.setOpaque(false);
        panelBuff.add(lblEscudo);
        panelBuff.add(lblBono);

        JPanel panelEnemigo = new JPanel(new GridLayout(2, 1));
        panelEnemigo.setOpaque(false);
        panelEnemigo.add(lblNombreEnemigo);
        panelEnemigo.add(lblVidaEnemigo);

        panelHUD.add(panelJugador);
        panelHUD.add(panelBuff);
        panelHUD.add(lblTurno);
        panelHUD.add(panelEnemigo);
        add(panelHUD, BorderLayout.NORTH);

        // ── Sprites ───────────────────────────────────────────────
        panelSprites = new PanelSpritesCentro(controlador);
        add(panelSprites, BorderLayout.CENTER);

        // ── Log lateral ───────────────────────────────────────────
        areaLog = new JTextArea(6, 22);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setBackground(new Color(10, 15, 30));
        areaLog.setForeground(new Color(180, 200, 220));
        areaLog.setLineWrap(true);
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(javax.swing.BorderFactory.createTitledBorder("📋 Registro Turnos"));
        scrollLog.setPreferredSize(new Dimension(280, 0));
        add(scrollLog, BorderLayout.EAST);

        // ── Cartas ────────────────────────────────────────────────
        panelMano = new JPanel();
        panelMano.setBackground(new Color(20, 30, 50));
        add(panelMano, BorderLayout.SOUTH);

        actualizarMano();
        suscribirEventos();
    }

    private void suscribirEventos() {
        EventBus bus = EventBus.getInstancia();

        // Actualizar números del HUD
        Consumer<Object> cbVida = d -> SwingUtilities.invokeLater(() -> {
            lblVidaJugador.setText("❤️ "  + controlador.getJugador().obtenerVida());
            lblEscudo.setText("🛡️ "       + controlador.getJugador().obtenerEscudo());
            lblBono.setText("⚔️ +"        + controlador.getJugador().obtenerBonoDanio());
            Enemigo actual = controlador.getEnemigo();
            if (actual != null) {
                lblVidaEnemigo.setText("💀 " + actual.obtenerVida());
                lblNombreEnemigo.setText(actual.getNombre());
            }
        });
        bus.suscribir(Evento.VIDA_ACTUALIZADA, cbVida);
        limpiezas.add(() -> bus.desuscribir(Evento.VIDA_ACTUALIZADA, cbVida));

        // Carta usada → animación de ACCIÓN (true = una sola vez, vuelve a idle solo)
        Consumer<Object> cbCarta = d -> SwingUtilities.invokeLater(() -> {
            Carta c = (Carta) d;
            if (c instanceof CartaAtaque) {
                panelSprites.setAnimacionJugador("doctor_ataque",  true);
            } else if (c instanceof CartaDefensa) {
                panelSprites.setAnimacionJugador("doctor_defensa", true);
            } else if (c instanceof CartaEfecto) {
                panelSprites.setAnimacionJugador("doctor_boost",   true);
            }
            panelSprites.setMensajeCentral("▶ " + c.getNombre());
            log("▶ " + controlador.getJugador().getNombre() + " usó: " + c.getNombre());
        });
        bus.suscribir(Evento.CARTA_USADA, cbCarta);
        limpiezas.add(() -> bus.desuscribir(Evento.CARTA_USADA, cbCarta));

        // Bloquear cartas (turno en proceso)
        Consumer<Object> cbBloquear = d -> SwingUtilities.invokeLater(() -> {
            habilitarCartas(false);
            lblTurno.setText("⏳ Procesando...");
            lblTurno.setForeground(new Color(200, 200, 80));
        });
        bus.suscribir(Evento.BLOQUEAR_CARTAS, cbBloquear);
        limpiezas.add(() -> bus.desuscribir(Evento.BLOQUEAR_CARTAS, cbBloquear));

        // Turno del enemigo → animación daño en doctor (loop mientras dura)
        Consumer<Object> cbTurnoEnemigo = d -> SwingUtilities.invokeLater(() -> {
            lblTurno.setText("💢 Turno enemigo");
            lblTurno.setForeground(new Color(240, 80, 80));
            panelSprites.setAnimacionJugador("doctor_danio", false); // loop
            panelSprites.setMensajeCentral("💢 El enemigo ataca...");
        });
        bus.suscribir(Evento.TURNO_ENEMIGO, cbTurnoEnemigo);
        limpiezas.add(() -> bus.desuscribir(Evento.TURNO_ENEMIGO, cbTurnoEnemigo));

        // Turno del jugador → rehabilitar cartas y volver a idle
        Consumer<Object> cbTurnoJugador = d -> SwingUtilities.invokeLater(() -> {
            lblTurno.setText("🎲 Tu turno");
            lblTurno.setForeground(new Color(80, 220, 80));
            panelSprites.setAnimacionJugador("doctor_idle", false); // idle en bucle
            // Restaurar sprite del enemigo activo
            Enemigo actual = controlador.getEnemigo();
            if (actual != null && actual.estaVivo()) {
                panelSprites.setAnimacionEnemigo(
                    GestorRecursos.getInstancia()
                        .getAnimacionKeyPorTipoEnemigo(actual.getTipo()), false);
            }
            panelSprites.setMensajeCentral("Selecciona una carta");
            actualizarMano();
            habilitarCartas(true);
        });
        bus.suscribir(Evento.TURNO_JUGADOR, cbTurnoJugador);
        limpiezas.add(() -> bus.desuscribir(Evento.TURNO_JUGADOR, cbTurnoJugador));

        // Enemigo atacó → log con info de escudo
        Consumer<Object> cbAtaco = d -> SwingUtilities.invokeLater(() -> {
            int danio = (int) d;
            String nombre = controlador.getEnemigo() != null
                    ? controlador.getEnemigo().getNombre() : "Enemigo";
            int escudo = controlador.getJugador().obtenerEscudo();
            String extra = (danio == 0) ? " (🛡️ bloqueado totalmente)"
                         : escudo > 0   ? " (🛡️ escudo activo)"
                         : "";
            log("💢 " + nombre + " atacó — daño real: " + danio + extra);
        });
        bus.suscribir(Evento.ENEMIGO_ATACO, cbAtaco);
        limpiezas.add(() -> bus.desuscribir(Evento.ENEMIGO_ATACO, cbAtaco));

        // Enemigo derrotado
        Consumer<Object> cbDerrotado = d -> SwingUtilities.invokeLater(() -> {
            String nombre = (String) d;
            log("☠️ ¡" + nombre + " fue derrotado!");
            panelSprites.setAnimacionEnemigoMuerte();
        });
        bus.suscribir(Evento.ENEMIGO_DERROTADO, cbDerrotado);
        limpiezas.add(() -> bus.desuscribir(Evento.ENEMIGO_DERROTADO, cbDerrotado));

        // Nuevo enemigo entra al campo
        Consumer<Object> cbNuevoEnemigo = d -> SwingUtilities.invokeLater(() -> {
            Enemigo e = (Enemigo) d;
            lblNombreEnemigo.setText(e.getNombre());
            lblVidaEnemigo.setText("💀 " + e.obtenerVida());
            log("⚠️ ¡Ahora ataca " + e.getNombre() + "!");
            panelSprites.cambiarEnemigo(
                GestorRecursos.getInstancia()
                    .getAnimacionKeyPorTipoEnemigo(e.getTipo()));
        });
        bus.suscribir(Evento.NUEVO_ENEMIGO, cbNuevoEnemigo);
        limpiezas.add(() -> bus.desuscribir(Evento.NUEVO_ENEMIGO, cbNuevoEnemigo));

        // Fin del combate
        Consumer<Object> cbFin = d -> SwingUtilities.invokeLater(() -> {
            boolean gano = (boolean) d;
            if (gano) {
                panelSprites.setAnimacionEnemigoMuerte();
                panelSprites.setMensajeCentral("🎉 ¡VICTORIA!");
                log("=== ¡VICTORIA! ===");
            } else {
                panelSprites.setAnimacionJugadorMuerte();
                panelSprites.setMensajeCentral("💀 Derrota...");
                log("=== DERROTA ===");
            }
            habilitarCartas(false);
            JOptionPane.showMessageDialog(this,
                gano ? "🎉 ¡VICTORIA! Derrotaste a todos los enemigos."
                     : "💀 Derrota... Fuiste vencido.");
            EventBus.getInstancia().publicar(Evento.MOSTRAR_MENU);
        });
        bus.suscribir(Evento.COMBATE_TERMINADO, cbFin);
        limpiezas.add(() -> bus.desuscribir(Evento.COMBATE_TERMINADO, cbFin));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        limpiezas.forEach(Runnable::run);
    }

    private void actualizarMano() {
        panelMano.removeAll();
        for (Carta c : controlador.getJugador().getMazo().getMano()) {
            JButton btn = new JButton(c.getNombre());
            btn.setFont(new Font("Monospaced", Font.BOLD, 13));
            btn.setBackground(colorCarta(c));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> controlador.usarCarta(c));
            panelMano.add(btn);
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    private Color colorCarta(Carta c) {
        if (c instanceof CartaAtaque)  return new Color(140, 50,  50);  // rojo
        if (c instanceof CartaDefensa) return new Color(50, 130,  60);  // verde
        if (c instanceof CartaEfecto)  return new Color(40,  80, 160);  // azul
        return new Color(60, 80, 120);
    }

    private void habilitarCartas(boolean hab) {
        for (Component comp : panelMano.getComponents()) comp.setEnabled(hab);
    }

    private void log(String msg) {
        areaLog.append(msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
}
