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
import cartas.CartaCuracion;
import cartas.CartaDefensa;
import cartas.CartaEfecto;
import entidades.Enemigo;
import juego.controlador.ControladorCombate;
import juego.eventbus.EventBus;
import juego.eventbus.Evento;
import juego.vista.sprite.PanelSpritesCentro;

public class PanelCombate extends JPanel {

    private final ControladorCombate controlador;
    private final JLabel lblVidaJugador;
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

        JPanel panelHUD = new JPanel(new GridLayout(1, 4, 10, 0));
        panelHUD.setBackground(new Color(25, 35, 55));

        lblVidaJugador = new JLabel("\u2764\uFE0F " + controlador.getJugador().obtenerVida(), SwingConstants.CENTER);
        lblEscudo = new JLabel("\uD83D\uDEE1\uFE0F " + controlador.getJugador().obtenerEscudo(), SwingConstants.CENTER);
        lblBono = new JLabel("\u2694\uFE0F +" + controlador.getJugador().obtenerBonoDanio(), SwingConstants.CENTER);
        lblTurno = new JLabel("Tu turno", SwingConstants.CENTER);

        Font hudFont = new Font("Monospaced", Font.BOLD, 16);
        lblVidaJugador.setFont(hudFont);
        lblEscudo.setFont(hudFont);
        lblBono.setFont(hudFont);
        lblTurno.setFont(hudFont);

        lblVidaJugador.setForeground(new Color(80, 220, 80));
        lblEscudo.setForeground(new Color(80, 200, 255));
        lblBono.setForeground(new Color(255, 220, 80));
        lblTurno.setForeground(new Color(200, 200, 200));

        JLabel lblNombreJugador = new JLabel(controlador.getJugador().getNombre(), SwingConstants.CENTER);
        lblNombreJugador.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblNombreJugador.setForeground(new Color(100, 200, 255));

        JPanel panelJugador = new JPanel(new GridLayout(2, 1));
        panelJugador.setOpaque(false);
        panelJugador.add(lblNombreJugador);
        panelJugador.add(lblVidaJugador);

        JPanel panelBuff = new JPanel(new GridLayout(2, 1));
        panelBuff.setOpaque(false);
        panelBuff.add(lblEscudo);
        panelBuff.add(lblBono);

        panelHUD.add(panelJugador);
        panelHUD.add(panelBuff);
        panelHUD.add(lblTurno);

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
        scrollLog.setBorder(javax.swing.BorderFactory.createTitledBorder("\uD83D\uDCCB Bit\u00e1cora"));
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
            lblVidaJugador.setText("\u2764\uFE0F " + controlador.getJugador().obtenerVida());
            lblEscudo.setText("\uD83D\uDEE1\uFE0F " + controlador.getJugador().obtenerEscudo());
            lblBono.setText("\u2694\uFE0F +" + controlador.getJugador().obtenerBonoDanio());
            panelSprites.actualizarHPs();
        });
        bus.suscribir(Evento.VIDA_ACTUALIZADA, cbVida);
        limpiezas.add(() -> bus.desuscribir(Evento.VIDA_ACTUALIZADA, cbVida));

        Consumer<Object> cbTurnoJugador = datos -> SwingUtilities.invokeLater(() -> {
            lblTurno.setText("\uD83C\uDFB2 Tu turno");
            lblTurno.setForeground(new Color(80, 220, 80));
            panelSprites.setAnimacionJugador("doctor_idle", true);
            panelSprites.reiniciarEnemigos();
            panelSprites.seleccionarPrimerEnemigoVivo();
            panelSprites.setMensajeCentral("Selecciona una carta");
            actualizarMano();
            habilitarCartas(true);
        });
        bus.suscribir(Evento.TURNO_JUGADOR, cbTurnoJugador);
        limpiezas.add(() -> bus.desuscribir(Evento.TURNO_JUGADOR, cbTurnoJugador));

        Consumer<Object> cbTurnoEnemigo = datos -> SwingUtilities.invokeLater(() -> {
            Enemigo e = (Enemigo) datos;
            lblTurno.setText("\uD83D\uDCA2 " + e.getNombre());
            lblTurno.setForeground(new Color(240, 80, 80));
            panelSprites.setAnimacionJugador("doctor_danio", false);
            panelSprites.setMensajeCentral("\uD83D\uDCA2 " + e.getNombre() + " ataca...");
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
            } else if (c instanceof CartaEfecto || c instanceof CartaCuracion) {
                panelSprites.setAnimacionJugador("doctor_boost", false);
            }
            panelSprites.setMensajeCentral("\u25B6 " + c.getNombre());
            areaLog.append("\u25B6 " + controlador.getJugador().getNombre()
                    + " us\u00f3: " + c.getNombre() + "\n");
        });
        bus.suscribir(Evento.CARTA_USADA, cbCartaUsada);
        limpiezas.add(() -> bus.desuscribir(Evento.CARTA_USADA, cbCartaUsada));

        Consumer<Object> cbEnemigoAtaco = datos -> SwingUtilities.invokeLater(() -> {
            Object[] arr = (Object[]) datos;
            Enemigo e = (Enemigo) arr[0];
            int danio = (int) arr[1];
            areaLog.append("\uD83D\uDCA2 " + e.getNombre()
                    + " atac\u00f3 por " + danio + " de da\u00f1o\n");
        });
        bus.suscribir(Evento.ENEMIGO_ATACO, cbEnemigoAtaco);
        limpiezas.add(() -> bus.desuscribir(Evento.ENEMIGO_ATACO, cbEnemigoAtaco));

        Consumer<Object> cbCombateFin = datos -> SwingUtilities.invokeLater(() -> {
            boolean gano = (boolean) datos;
            if (gano) {
                panelSprites.setAnimacionEnemigoMuerteTodos();
                panelSprites.setMensajeCentral("\uD83C\uDF89 \u00a1VICTORIA!");
            } else {
                panelSprites.setAnimacionJugadorMuerte();
                panelSprites.setMensajeCentral("\uD83D\uDC80 Derrota...");
            }
            areaLog.append("=== " + (gano ? "VICTORIA" : "DERROTA") + " ===\n");
            JOptionPane.showMessageDialog(this,
                    gano ? "\uD83C\uDF89 \u00a1VICTORIA! Derrotaste a todos los enemigos."
                            : "\uD83D\uDC80 Derrota... Fuiste vencido.");
            EventBus.getInstancia().publicar(Evento.MOSTRAR_MENU);
        });
        bus.suscribir(Evento.COMBATE_TERMINADO, cbCombateFin);
        limpiezas.add(() -> bus.desuscribir(Evento.COMBATE_TERMINADO, cbCombateFin));
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
            btn.addActionListener(e -> {
                Enemigo objetivo = panelSprites.getEnemigoSeleccionado();
                if (objetivo == null) {
                    for (Enemigo en : controlador.getEnemigos()) {
                        if (en.estaVivo()) {
                            objetivo = en;
                            break;
                        }
                    }
                }
                controlador.usarCarta(c, objetivo);
            });
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
