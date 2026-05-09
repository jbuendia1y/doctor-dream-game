package juego.vista.sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import juego.controlador.ControladorCombate;

public class PanelSpritesCentro extends JPanel {

    private final ControladorCombate controlador;
    private final GestorRecursos recursos;
    private final Animacion animJugador;
    private final Animacion animEnemigo;
    private String mensajeCentral = "⚔️ COMBATE ⚔️";
    private BufferedImage mapaFondo;
    private boolean jugadorMuerto = false;
    private boolean enemigoMuerto = false;

    public PanelSpritesCentro(ControladorCombate controlador) {
        this.controlador = controlador;
        this.recursos = GestorRecursos.getInstancia();

        animJugador = new Animacion(recursos.getAnimacion("doctor_idle"));
        animJugador.reproducirEnBucle();

        String keyEnemigo = recursos.getAnimacionKeyPorTipoEnemigo(controlador.getEnemigo().getTipo());
        BufferedImage[] framesEnemigo = recursos.getAnimacion(keyEnemigo);
        if (framesEnemigo == null) {
            framesEnemigo = recursos.getAnimacion("virus");
        }
        animEnemigo = new Animacion(framesEnemigo);
        animEnemigo.reproducirEnBucle();

        mapaFondo = recursos.getImagen("mapa1");

        setBackground(new Color(15, 25, 40));

        Timer timer = new Timer(120, e -> {
            boolean terminoJugador = animJugador.actualizar();
            boolean terminoEnemigo = animEnemigo.actualizar();

            if (terminoJugador && !jugadorMuerto) {
                animJugador.setFrames(recursos.getAnimacion("doctor_idle"));
                animJugador.reproducirEnBucle();
            }
            if (terminoEnemigo && !enemigoMuerto) {
                String key = recursos.getAnimacionKeyPorTipoEnemigo(
                        controlador.getEnemigo().getTipo());
                BufferedImage[] frames = recursos.getAnimacion(key);
                if (frames == null) {
                    frames = recursos.getAnimacion("virus");
                }
                animEnemigo.setFrames(frames);
                animEnemigo.reproducirEnBucle();
            }

            repaint();
        });
        timer.start();
    }

    public void setAnimacionJugador(String key, boolean looping) {
        if (jugadorMuerto) return;
        BufferedImage[] frames = recursos.getAnimacion(key);
        if (frames != null) {
            animJugador.setFrames(frames);
            if (looping) {
                animJugador.reproducirEnBucle();
            } else {
                animJugador.reproducirUnaVez();
            }
        }
    }

    public void setAnimacionEnemigo(String key, boolean looping) {
        if (enemigoMuerto) return;
        BufferedImage[] frames = recursos.getAnimacion(key);
        if (frames != null) {
            animEnemigo.setFrames(frames);
            if (looping) {
                animEnemigo.reproducirEnBucle();
            } else {
                animEnemigo.reproducirUnaVez();
            }
        }
    }

    public void setAnimacionJugadorMuerte() {
        jugadorMuerto = true;
        BufferedImage[] frames = recursos.getAnimacion("doctor_muerte");
        if (frames != null) {
            animJugador.setFrames(frames);
            animJugador.reproducirUnaVez();
        }
    }

    public void setAnimacionEnemigoMuerte() {
        enemigoMuerto = true;
        String key = recursos.getAnimacionKeyPorTipoEnemigo(controlador.getEnemigo().getTipo());
        BufferedImage[] frames = recursos.getAnimacion(key);
        if (frames != null) {
            animEnemigo.setFrames(frames);
            animEnemigo.reproducirUnaVez();
        }
    }

    public void setMensajeCentral(String msg) {
        this.mensajeCentral = msg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        if (mapaFondo != null) {
            g2.drawImage(mapaFondo, 0, 0, w, h, null);
        }

        int spriteSize = Math.min(h - 60, 220);
        int py = (h - spriteSize) / 2;
        int centerX = w / 2;

        BufferedImage frameJugador = animJugador.getFrame();
        if (frameJugador != null) {
            int px = centerX / 2 - spriteSize / 2;
            g2.drawImage(frameJugador, px, py, spriteSize, spriteSize, null);
        } else {
            SpriteSheet.dibujarJugador(g2, centerX / 2 - spriteSize / 2, py, spriteSize);
        }

        BufferedImage frameEnemigo = animEnemigo.getFrame();
        if (frameEnemigo != null) {
            int ex = centerX + centerX / 2 - spriteSize / 2;
            g2.drawImage(frameEnemigo, ex, py, spriteSize, spriteSize, null);
        } else {
            SpriteSheet.dibujarEnemigo(g2, centerX + centerX / 2 - spriteSize / 2, py, spriteSize);
        }

        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.setColor(new Color(255, 200, 60));
        String vs = "VS";
        int vsX = centerX - g2.getFontMetrics().stringWidth(vs) / 2;
        int vsY = py + spriteSize / 2 - 20;
        g2.drawString(vs, vsX, vsY);

        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.setColor(new Color(200, 220, 240));
        int msgX = centerX - g2.getFontMetrics().stringWidth(mensajeCentral) / 2;
        g2.drawString(mensajeCentral, msgX, vsY + 30);
    }
}
