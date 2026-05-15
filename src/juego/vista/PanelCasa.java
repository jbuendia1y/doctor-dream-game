package juego.vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import juego.controlador.ControladorCombate;
import juego.eventbus.EventBus;
import juego.eventbus.Evento;
import juego.modelo.Nivel;
import juego.vista.sprite.Animacion;
import juego.vista.sprite.GestorRecursos;
import juego.vista.sprite.SpriteSheet;

/**
 * Escenario de la casa del doctor.
 * El doctor puede moverse libremente y al acercarse a la cama
 * puede presionar E para dormir e ir al combate onírico.
 */
public class PanelCasa extends JPanel implements KeyListener {

    private static final int DOCTOR_SIZE = 140;
    private static final int DOCTOR_Y = 340;
    private static final int VELOCIDAD = 8;
    // Zona de la cama en la imagen de fondo (~300,650 con ~600x300)
    private static final int CAMA_ZONA_X = 250;
    private static final int CAMA_ZONA_Y = 400;
    private static final int CAMA_ZONA_W = 700;
    private static final int CAMA_ZONA_H = 250;
    private static final int TICKS_POR_FRAME = 6;

    private final Nivel nivel;
    private final Timer timer;

    private BufferedImage fondo;
    private Animacion animDoctor;
    private int doctorX = 1050;
    private boolean moviendoIzquierda = false;
    private boolean moviendoDerecha = false;
    private int tickAnimacion = 0;
    private boolean durmiendo = false;
    private int fundidoAlpha = 0;
    private boolean transicionando = false;

    public PanelCasa(Nivel nivel) {
        this.nivel = nivel;

        this.fondo = GestorRecursos.getInstancia().getImagen("casa_fondo");
        BufferedImage[] doctorFrames = GestorRecursos.getInstancia().getAnimacion("doctor_idle");
        this.animDoctor = new Animacion(doctorFrames != null ? doctorFrames : new BufferedImage[0]);
        this.animDoctor.reproducirEnBucle();

        setFocusable(true);
        addKeyListener(this);

        // --- FIX FOCO: HierarchyListener + click ---
        addHierarchyListener(e -> {
            if (isShowing()) {
                requestFocusInWindow();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        timer = new Timer(40, e -> actualizar());
        timer.start();

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void actualizar() {
        if (transicionando) {
            fundidoAlpha += 12;
            if (fundidoAlpha >= 255) {
                timer.stop();
                fundidoAlpha = 255;
                ControladorCombate ctrl = new ControladorCombate();
                ctrl.iniciarCombate();
                EventBus.getInstancia().publicar(Evento.MOSTRAR_COMBATE, ctrl);
            }
            repaint();
            return;
        }

        if (moviendoIzquierda) {
            doctorX = Math.max(0, doctorX - VELOCIDAD);
        }
        if (moviendoDerecha) {
            doctorX = Math.min(getWidth() - DOCTOR_SIZE, doctorX + VELOCIDAD);
        }

        tickAnimacion++;
        if (tickAnimacion >= TICKS_POR_FRAME) {
            tickAnimacion = 0;
            animDoctor.actualizar();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        // --- Fondo ---
        if (fondo != null) {
            g2.drawImage(fondo, 0, 0, w, h, null);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(60, 50, 70),
                    w, h, new Color(100, 80, 110));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(80, 65, 50));
            g2.fillRect(0, h - 80, w, 80);

            // Ventana
            g2.setColor(new Color(60, 70, 90));
            g2.fillRect(50, 60, 120, 150);
            g2.setColor(new Color(80, 100, 140, 100));
            g2.fillRect(55, 65, 110, 140);
            g2.setColor(new Color(40, 40, 50));
            g2.drawRect(50, 60, 120, 150);
            g2.drawLine(110, 60, 110, 210);
            g2.drawLine(50, 135, 170, 135);

            // Cuadro en la pared
            g2.setColor(new Color(60, 50, 40));
            g2.fillRect(700, 100, 80, 60);
            g2.setColor(new Color(100, 90, 70));
            g2.drawRect(700, 100, 80, 60);
        }

        // --- Cama (solo se dibuja si no hay fondo, porque la del fondo ya la tiene) ---
        if (fondo == null) {
            dibujarCama(g2);
        }

        // --- Doctor ---
        BufferedImage frameDoctor = animDoctor.getFrame();
        if (frameDoctor != null) {
            g2.drawImage(frameDoctor, doctorX, DOCTOR_Y, DOCTOR_SIZE, DOCTOR_SIZE, null);
        } else {
            SpriteSheet.dibujarJugador(g2, doctorX, DOCTOR_Y, DOCTOR_SIZE);
        }

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        String docLabel = "Dr. Dream";
        int docLabelX = doctorX + (DOCTOR_SIZE - g2.getFontMetrics().stringWidth(docLabel)) / 2;
        g2.drawString(docLabel, docLabelX, DOCTOR_Y + DOCTOR_SIZE + 15);

        // --- Prompt para dormir ---
        if (!transicionando && !durmiendo && estaEnRangoCama()) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2.setColor(new Color(255, 255, 100, 200 + (int) (55 * Math.sin(System.currentTimeMillis() / 300.0))));
            String dormir = "Presiona E para dormir  [E]";
            FontMetrics fm = g2.getFontMetrics();
            int dormirX = (w - fm.stringWidth(dormir)) / 2;
            g2.drawString(dormir, dormirX, h - 100);

            g2.setColor(new Color(255, 255, 100, 100));
            g2.drawLine(dormirX, h - 95, dormirX + fm.stringWidth(dormir), h - 95);
        }

        // --- Fundido a negro ---
        if (transicionando || durmiendo) {
            g2.setColor(new Color(0, 0, 0, Math.min(fundidoAlpha, 255)));
            g2.fillRect(0, 0, w, h);

            if (fundidoAlpha < 255) {
                g2.setFont(new Font("Monospaced", Font.BOLD, 20));
                g2.setColor(new Color(255, 255, 200, Math.min(fundidoAlpha * 2, 200)));
                String zzz = "Durmiendo...";
                FontMetrics fm = g2.getFontMetrics();
                int zzzX = (w - fm.stringWidth(zzz)) / 2;
                g2.drawString(zzz, zzzX, h / 2);
            }
        }
    }

    private void dibujarCama(Graphics2D g2) {
        int bedX = CAMA_ZONA_X + 40;
        int bedY = CAMA_ZONA_Y + 20;
        int bedW = CAMA_ZONA_W - 80;
        int bedH = CAMA_ZONA_H - 40;
        int bedCenterX = bedX + bedW / 2;

        g2.setColor(new Color(60, 45, 35));
        g2.fillRoundRect(bedX, bedY, bedW, bedH, 8, 8);

        g2.setColor(new Color(200, 190, 180));
        g2.fillRoundRect(bedX + 5, bedY + 5, bedW - 10, bedH - 15, 6, 6);

        g2.setColor(new Color(230, 220, 210));
        g2.fillRoundRect(bedX + 10, bedY + 8, 70, bedH - 35, 8, 8);

        g2.setColor(new Color(220, 210, 200));
        g2.fillRoundRect(bedX + 10, bedY + bedH - 35, bedW - 20, 20, 4, 4);

        g2.setColor(new Color(50, 35, 25));
        g2.fillRoundRect(bedX - 8, bedY - 12, bedW + 16, 16, 6, 6);

        g2.setColor(new Color(70, 55, 45));
        g2.fillOval(bedX + 10, bedY - 10, 12, 12);
        g2.fillOval(bedCenterX - 6, bedY - 10, 12, 12);
        g2.fillOval(bedX + bedW - 22, bedY - 10, 12, 12);
    }

    private boolean estaEnRangoCama() {
        // El doctor está en la zona de la cama (rectángulo aproximado)
        int doctorCX = doctorX + DOCTOR_SIZE / 2;
        int doctorCY = DOCTOR_Y + DOCTOR_SIZE / 2;
        return doctorCX >= CAMA_ZONA_X
                && doctorCX <= CAMA_ZONA_X + CAMA_ZONA_W
                && doctorCY >= CAMA_ZONA_Y
                && doctorCY <= CAMA_ZONA_Y + CAMA_ZONA_H;
    }

    private void dormir() {
        if (transicionando || durmiendo) return;
        durmiendo = true;
        transicionando = true;
        fundidoAlpha = 0;
    }

    // --- KeyListener ---

    @Override
    public void keyPressed(KeyEvent e) {
        if (transicionando) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> moviendoIzquierda = true;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> moviendoDerecha = true;
            case KeyEvent.VK_E -> {
                if (estaEnRangoCama()) {
                    dormir();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> moviendoIzquierda = false;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> moviendoDerecha = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (timer != null) {
            timer.stop();
        }
    }
}
