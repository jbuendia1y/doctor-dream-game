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

    // ── FLAG clave: mientras corre una animación de acción (ataque/defensa/boost)
    //    el timer NO resetea a idle. Solo vuelve a idle cuando termina esa acción.
    private boolean animacionAccionJugadorActiva = false;
    private boolean animacionAccionEnemigoActiva = false;

    public PanelSpritesCentro(ControladorCombate controlador) {
        this.controlador = controlador;
        this.recursos = GestorRecursos.getInstancia();

        animJugador = new Animacion(recursos.getAnimacion("doctor_idle"));
        animJugador.reproducirEnBucle();

        String keyEnemigo = recursos.getAnimacionKeyPorTipoEnemigo(
                controlador.getEnemigosActivos().get(0).getTipo());
        BufferedImage[] framesEnemigo = recursos.getAnimacion(keyEnemigo);
        if (framesEnemigo == null) framesEnemigo = recursos.getAnimacion("virus");
        animEnemigo = new Animacion(framesEnemigo);
        animEnemigo.reproducirEnBucle();

        mapaFondo = recursos.getImagen("mapa1");
        setBackground(new Color(15, 25, 40));

        // Timer a 100ms — avanza ambas animaciones
        Timer timer = new Timer(100, e -> {
            boolean terminoJugador = animJugador.actualizar();
            boolean terminoEnemigo = animEnemigo.actualizar();

            // Si terminó una animación de ACCIÓN del jugador → volver a idle
            if (terminoJugador && animacionAccionJugadorActiva && !jugadorMuerto) {
                animacionAccionJugadorActiva = false;
                animJugador.setFrames(recursos.getAnimacion("doctor_idle"));
                animJugador.reproducirEnBucle();
            }

            // Si terminó una animación de acción del enemigo → volver a idle del enemigo
            if (terminoEnemigo && animacionAccionEnemigoActiva && !enemigoMuerto) {
                animacionAccionEnemigoActiva = false;
                String key = recursos.getAnimacionKeyPorTipoEnemigo(
                        controlador.getEnemigosActivos().get(0).getTipo());
                BufferedImage[] frames = recursos.getAnimacion(key);
                if (frames == null) frames = recursos.getAnimacion("virus");
                animEnemigo.setFrames(frames);
                animEnemigo.reproducirEnBucle();
            }

            repaint();
        });
        timer.start();
    }

    /**
     * Cambia la animación del jugador.
     * @param key      clave de la animación
     * @param esAccion true = ataque/defensa/boost (se reproduce UNA VEZ y vuelve a idle)
     *                 false = idle/daño en bucle continuo
     */
    public void setAnimacionJugador(String key, boolean esAccion) {
        if (jugadorMuerto) return;
        BufferedImage[] frames = recursos.getAnimacion(key);
        if (frames == null) return;

        animJugador.setFrames(frames);
        if (esAccion) {
            // Acción de un solo disparo: reproducir una vez, luego volver a idle
            animacionAccionJugadorActiva = true;
            animJugador.reproducirUnaVez();
        } else {
            // Estado continuo (idle, daño en bucle)
            animacionAccionJugadorActiva = false;
            animJugador.reproducirEnBucle();
        }
    }

    public void setAnimacionEnemigo(String key, boolean esAccion) {
        if (enemigoMuerto) return;
        BufferedImage[] frames = recursos.getAnimacion(key);
        if (frames == null) return;

        animEnemigo.setFrames(frames);
        if (esAccion) {
            animacionAccionEnemigoActiva = true;
            animEnemigo.reproducirUnaVez();
        } else {
            animacionAccionEnemigoActiva = false;
            animEnemigo.reproducirEnBucle();
        }
    }

    public void setAnimacionJugadorMuerte() {
        jugadorMuerto = true;
        animacionAccionJugadorActiva = false;
        BufferedImage[] frames = recursos.getAnimacion("doctor_muerte");
        if (frames != null) {
            animJugador.setFrames(frames);
            animJugador.reproducirUnaVez();
        }
    }

    public void setAnimacionEnemigoMuerte() {
        enemigoMuerto = true;
        animacionAccionEnemigoActiva = false;
        // Muestra el último frame congelado del enemigo (se queda quieto al morir)
        animEnemigo.detener();
    }

    /** Cambia las animaciones al siguiente enemigo (cuando uno muere y entra el siguiente) */
    public void cambiarEnemigo(String keyNuevoEnemigo) {
        enemigoMuerto = false;
        animacionAccionEnemigoActiva = false;
        BufferedImage[] frames = recursos.getAnimacion(keyNuevoEnemigo);
        if (frames == null) frames = recursos.getAnimacion("virus");
        animEnemigo.setFrames(frames);
        animEnemigo.reproducirEnBucle();
    }

    public void setMensajeCentral(String msg) {
        this.mensajeCentral = msg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        if (mapaFondo != null) {
            g2.drawImage(mapaFondo, 0, 0, w, h, null);
        }

        int spriteSize = Math.min(h - 60, 220);
        int py = (h - spriteSize) / 2;
        int centerX = w / 2;

        // Jugador — lado izquierdo
        BufferedImage frameJugador = animJugador.getFrame();
        int jx = centerX / 2 - spriteSize / 2;
        if (frameJugador != null) {
            g2.drawImage(frameJugador, jx, py, spriteSize, spriteSize, null);
        } else {
            SpriteSheet.dibujarJugador(g2, jx, py, spriteSize);
        }

        // Enemigo — lado derecho
        BufferedImage frameEnemigo = animEnemigo.getFrame();
        int ex = centerX + centerX / 2 - spriteSize / 2;
        if (frameEnemigo != null) {
            g2.drawImage(frameEnemigo, ex, py, spriteSize, spriteSize, null);
        } else {
            SpriteSheet.dibujarEnemigo(g2, ex, py, spriteSize);
        }

        // VS central
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.setColor(new Color(255, 200, 60));
        String vs = "VS";
        int vsX = centerX - g2.getFontMetrics().stringWidth(vs) / 2;
        int vsY = py + spriteSize / 2 - 20;
        g2.drawString(vs, vsX, vsY);

        // Mensaje de acción
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.setColor(new Color(200, 220, 240));
        int msgX = centerX - g2.getFontMetrics().stringWidth(mensajeCentral) / 2;
        g2.drawString(mensajeCentral, msgX, vsY + 30);
    }
}
