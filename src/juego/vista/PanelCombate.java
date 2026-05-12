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
    private final JPanel panelMano;
    private final JTextArea areaLog;
    private final PanelSpritesCentro panelSprites;
    private final List<Runnable> limpiezas = new ArrayList<>();

    public PanelCombate(ControladorCombate controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 25, 40));

        JPanel panelHUD = new JPanel(new GridLayout(1, 5, 10, 0));
        panelHUD.setBackground(new Color(25, 35, 55));

        lblVidaJugador = new JLabel("❤️ " + controlador.getJugador().obtenerVida(), SwingConstants.CENTER);
        lblEscudo = new JLabel("🛡️ " + controlador.getJugador().obtenerEscudo(), SwingConstants.CENTER);
        lblBono = new JLabel("⚔️ +" + controlador.getJugador().obtenerBonoDanio(), SwingConstants.CENTER);
        lblVidaEnemigo = new JLabel("💀 " + controlador.getEnemigo().obtenerVida(), SwingConstants.CENTER);
        lblTurno = new JLabel("Tu turno", SwingConstants.CENTER);

        Font hudFont = new Font("Monospaced", Font.BOLD, 16);
        lblVidaJugador.setFont(hudFont);
        lblEscudo.setFont(hudFont);
        lblBono.setFont(hudFont);
        lblVidaEnemigo.setFont(hudFont);
        lblTurno.setFont(hudFont);

        lblVidaJugador.setForeground(new Color(80, 220, 80));
        lblEscudo.setForeground(new Color(80, 200, 255));
        lblBono.setForeground(new Color(255, 220, 80));
        lblVidaEnemigo.setForeground(new Color(240, 80, 80));
        lblTurno.setForeground(new Color(200, 200, 200));

        JLabel lblNombreJugador = new JLabel(controlador.getJugador().getNombre(), SwingConstants.CENTER);
        lblNombreJugador.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblNombreJugador.setForeground(new Color(100, 200, 255));
        JLabel lblNombreEnemigo = new JLabel(controlador.getEnemigo().getNombre(), SwingConstants.CENTER);
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

        panelSprites = new PanelSpritesCentro(controlador);
        add(panelSprites, BorderLayout.CENTER);

        areaLog = new JTextArea(6, 22);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setBackground(new Color(10, 15, 30));
        areaLog.setForeground(new Color(180, 200, 220));
        areaLog.setLineWrap(true);
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(javax.swing.BorderFactory.createTitledBorder("📋 Bitácora"));
        scrollLog.setPreferredSize(new Dimension(280, 0));
        add(scrollLog, BorderLayout.EAST);

        panelMano = new JPanel();
        panelMano.setBackground(new Color(20, 30, 50));
        add(panelMano, BorderLayout.SOUTH);

        actualizarMano();
        suscribirEventos();
    }

    private void suscribirEventos() {
        EventBus bus = EventBus.getInstancia();

        Consumer<Object> cbVida = datos -> SwingUtilities.invokeLater(() -> {
            lblVidaJugador.setText("❤️ " + controlador.getJugador().obtenerVida());
            lblVidaEnemigo.setText("💀 " + controlador.getEnemigo().obtenerVida());
            lblEscudo.setText("🛡️ " + controlador.getJugador().obtenerEscudo());
            lblBono.setText("⚔️ +" + controlador.getJugador().obtenerBonoDanio());
        });
        bus.suscribir(Evento.VIDA_ACTUALIZADA, cbVida);
        limpiezas.add(() -> bus.desuscribir(Evento.VIDA_ACTUALIZADA, cbVida));

        Consumer<Object> cbTurnoJugador = datos -> SwingUtilities.invokeLater(() -> {
            lblTurno.setText("🎲 Tu turno");
            lblTurno.setForeground(new Color(80, 220, 80));
            panelSprites.setAnimacionJugador("doctor_idle", true);
            panelSprites.setAnimacionEnemigo(
                    recursosEnemigoKey(), true);
            panelSprites.setMensajeCentral("Selecciona una carta");
            actualizarMano();
            habilitarCartas(true);
        });
        bus.suscribir(Evento.TURNO_JUGADOR, cbTurnoJugador);
        limpiezas.add(() -> bus.desuscribir(Evento.TURNO_JUGADOR, cbTurnoJugador));

        Consumer<Object> cbTurnoEnemigo = datos -> SwingUtilities.invokeLater(() -> {
            lblTurno.setText("💢 Turno enemigo");
            lblTurno.setForeground(new Color(240, 80, 80));
            panelSprites.setAnimacionJugador("doctor_danio", false);
            panelSprites.setAnimacionEnemigo(
                    recursosEnemigoKey(), false);
            panelSprites.setMensajeCentral("💢 El enemigo ataca...");
            habilitarCartas(false);
        });
        bus.suscribir(Evento.TURNO_ENEMIGO, cbTurnoEnemigo);
        limpiezas.add(() -> bus.desuscribir(Evento.TURNO_ENEMIGO, cbTurnoEnemigo));

        Consumer<Object> cbCartaUsada = datos -> SwingUtilities.invokeLater(() -> {
            Carta c = (Carta) datos;
            habilitarCartas(false);
            if (c instanceof CartaAtaque) {
                panelSprites.setAnimacionJugador("doctor_ataque", false);
            } else if (c instanceof CartaDefensa) {
                panelSprites.setAnimacionJugador("doctor_defensa", false);
            } else if (c instanceof CartaEfecto) {
                panelSprites.setAnimacionJugador("doctor_boost", false);
            }
            panelSprites.setMensajeCentral("▶ " + c.getNombre());
            areaLog.append("▶ " + controlador.getJugador().getNombre()
                    + " usó: " + c.getNombre() + "\n");
        });
        bus.suscribir(Evento.CARTA_USADA, cbCartaUsada);
        limpiezas.add(() -> bus.desuscribir(Evento.CARTA_USADA, cbCartaUsada));

        Consumer<Object> cbEnemigoAtaco = datos -> SwingUtilities.invokeLater(() -> {
            int danio = (int) datos;
            areaLog.append("💢 " + controlador.getEnemigo().getNombre()
                    + " atacó por " + danio + " de daño\n");
        });
        bus.suscribir(Evento.ENEMIGO_ATACO, cbEnemigoAtaco);
        limpiezas.add(() -> bus.desuscribir(Evento.ENEMIGO_ATACO, cbEnemigoAtaco));

        Consumer<Object> cbCombateFin = datos -> SwingUtilities.invokeLater(() -> {
            boolean gano = (boolean) datos;
            if (gano) {
                panelSprites.setAnimacionEnemigoMuerte();
                panelSprites.setMensajeCentral("🎉 ¡VICTORIA!");
            } else {
                panelSprites.setAnimacionJugadorMuerte();
                panelSprites.setMensajeCentral("💀 Derrota...");
            }
            areaLog.append("=== " + (gano ? "VICTORIA" : "DERROTA") + " ===\n");
            JOptionPane.showMessageDialog(this,
                    gano ? "🎉 ¡VICTORIA! Derrotaste al enemigo."
                            : "💀 Derrota... Fuiste vencido.");
            EventBus.getInstancia().publicar(Evento.MOSTRAR_MENU);
        });
        bus.suscribir(Evento.COMBATE_TERMINADO, cbCombateFin);
        limpiezas.add(() -> bus.desuscribir(Evento.COMBATE_TERMINADO, cbCombateFin));
    }

    private String recursosEnemigoKey() {
        return GestorRecursos.getInstancia()
                .getAnimacionKeyPorTipoEnemigo(controlador.getEnemigo().getTipo());
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
            btn.setBackground(new Color(60, 80, 120));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> controlador.usarCarta(c));
            panelMano.add(btn);
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    private void habilitarCartas(boolean habilitado) {
        for (Component comp : panelMano.getComponents()) {
            comp.setEnabled(habilitado);
        }
    }
}
