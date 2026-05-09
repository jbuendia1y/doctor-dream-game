package juego.vista.sprite;

import java.awt.image.BufferedImage;

public class Animacion {

    private BufferedImage[] frames;
    private int frameActual;
    private boolean activa;
    private boolean looping;

    public Animacion(BufferedImage[] frames) {
        this.frames = frames;
        this.frameActual = 0;
        this.activa = false;
        this.looping = true;
    }

    public void reproducirUnaVez() {
        frameActual = 0;
        activa = true;
        looping = false;
    }

    public void reproducirEnBucle() {
        frameActual = 0;
        activa = true;
        looping = true;
    }

    public void detener() {
        activa = false;
    }

    public boolean actualizar() {
        if (!activa || frames == null || frames.length == 0) {
            return false;
        }

        frameActual++;
        if (frameActual >= frames.length) {
            if (looping) {
                frameActual = 0;
            } else {
                frameActual = frames.length - 1;
                activa = false;
                return true;
            }
        }
        return false;
    }

    public BufferedImage getFrame() {
        if (frames == null || frames.length == 0) {
            return null;
        }
        if (frameActual >= frames.length) {
            frameActual = frames.length - 1;
        }
        return frames[frameActual];
    }

    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
        this.frameActual = 0;
    }

    public boolean isActiva() {
        return activa;
    }
}
