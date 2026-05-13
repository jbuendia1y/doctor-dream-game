package juego.vista.sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;
import entidades.Enemigo;
import juego.controlador.ControladorCombate;

public class PanelSpritesCentro extends JPanel {

    private final ControladorCombate controlador;
    private final GestorRecursos recursos;
    private final Animacion animJugador;
    private final Animacion[] animEnemigos;
    private final Enemigo[] enemigosData;
    private final boolean[] enemigosMuertos;
    private String mensajeCentral = "\u2694\uFE0F COMBATE \u2694\uFE0F";
    private BufferedImage mapaFondo;
    private boolean jugadorMuerto = false;
    private int enemigoSeleccionado = 0;

    public PanelSpritesCentro(ControladorCombate controlador) {
        this.controlador = controlador;
        this.recursos = GestorRecursos.getInstancia();

        animJugador = new Animacion(recursos.getAnimacion("doctor_idle"));
        animJugador.reproducirEnBucle();

        List<Enemigo> enemigos = controlador.getEnemigos();
        int n = enemigos.size();
        animEnemigos = new Animacion[n];
        enemigosData = new Enemigo[n];
        enemigosMuertos = new boolean[n];

        for (int i = 0; i < n; i++) {
            enemigosData[i] = enemigos.get(i);
            String key = recursos.getAnimacionKeyPorTipoEnemigo(enemigos.get(i).getTipo());
            BufferedImage[] frames = recursos.getAnimacion(key);
            if (frames == null) frames = recursos.getAnimacion("virus");
            animEnemigos[i] = new Animacion(frames);
            animEnemigos[i].reproducirEnBucle();
        }

        mapaFondo = recursos.getImagen("mapa1");

        setBackground(new Color(15, 25, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                int idx = getEnemigoIndexAt(me.getX(), me.getY());
                if (idx >= 0 && !enemigosMuertos[idx]) {
                    enemigoSeleccionado = idx;
                    repaint();
                }
            }
        });

        Timer timer = new Timer(120, e -> {
            boolean terminoJugador = animJugador.actualizar();

            for (int i = 0; i < animEnemigos.length; i++) {
                boolean termino = animEnemigos[i].actualizar();
                if (termino && !enemigosMuertos[i]) {
                    String key = recursos.getAnimacionKeyPorTipoEnemigo(enemigosData[i].getTipo());
                    BufferedImage[] frames = recursos.getAnimacion(key);
                    if (frames == null) frames = recursos.getAnimacion("virus");
                    animEnemigos[i].setFrames(frames);
                    animEnemigos[i].reproducirEnBucle();
                }
            }

            if (terminoJugador && !jugadorMuerto) {
                animJugador.setFrames(recursos.getAnimacion("doctor_idle"));
                animJugador.reproducirEnBucle();
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
            if (looping) animJugador.reproducirEnBucle();
            else animJugador.reproducirUnaVez();
        }
    }

    public void setAnimacionEnemigoPorIndice(int index, String key, boolean looping) {
        if (index < 0 || index >= animEnemigos.length || enemigosMuertos[index]) return;
        BufferedImage[] frames = recursos.getAnimacion(key);
        if (frames != null) {
            animEnemigos[index].setFrames(frames);
            if (looping) animEnemigos[index].reproducirEnBucle();
            else animEnemigos[index].reproducirUnaVez();
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

    public void setAnimacionEnemigoMuerteTodos() {
        for (int i = 0; i < animEnemigos.length; i++) {
            enemigosMuertos[i] = true;
            animEnemigos[i].setFrames(recursos.getAnimacion(
                    recursos.getAnimacionKeyPorTipoEnemigo(enemigosData[i].getTipo())));
            animEnemigos[i].reproducirUnaVez();
        }
    }

    public void reiniciarEnemigos() {
        for (int i = 0; i < animEnemigos.length; i++) {
            if (!enemigosMuertos[i]) {
                String key = recursos.getAnimacionKeyPorTipoEnemigo(enemigosData[i].getTipo());
                BufferedImage[] frames = recursos.getAnimacion(key);
                if (frames == null) frames = recursos.getAnimacion("virus");
                animEnemigos[i].setFrames(frames);
                animEnemigos[i].reproducirEnBucle();
            }
        }
    }

    public void actualizarHPs() {
        repaint();
    }

    public void seleccionarPrimerEnemigoVivo() {
        for (int i = 0; i < enemigosData.length; i++) {
            if (enemigosData[i].estaVivo()) {
                enemigoSeleccionado = i;
                repaint();
                return;
            }
        }
    }

    public Enemigo getEnemigoSeleccionado() {
        if (enemigoSeleccionado >= 0 && enemigoSeleccionado < enemigosData.length
                && enemigosData[enemigoSeleccionado].estaVivo()) {
            return enemigosData[enemigoSeleccionado];
        }
        for (Enemigo e : enemigosData) {
            if (e.estaVivo()) return e;
        }
        return null;
    }

    public void setMensajeCentral(String msg) {
        this.mensajeCentral = msg;
    }

    private int getEnemigoIndexAt(int mx, int my) {
        int[] xs = new int[animEnemigos.length];
        int[] ys = new int[animEnemigos.length];
        int[] sizes = new int[animEnemigos.length];
        calcularPosicionesTriangulo(xs, ys, sizes);
        for (int i = 0; i < animEnemigos.length; i++) {
            if (mx >= xs[i] && mx <= xs[i] + sizes[i] && my >= ys[i] && my <= ys[i] + sizes[i]) {
                return i;
            }
        }
        return -1;
    }

    private void calcularPosicionesTriangulo(int[] outX, int[] outY, int[] outSize) {
        int w = getWidth();
        int h = getHeight();
        int playerSize = Math.min(h - 80, 200);
        int minionSize = Math.max(playerSize / 3, 60);
        int bossSize = (int) (minionSize * 1.3);
        int gap = 10;

        int gx = (int) (w * 0.52);
        int frontWidth = 3 * minionSize + 2 * gap;
        int totalH = bossSize + gap * 2 + minionSize;
        int gy = (h - totalH) / 2;

        // BOSS atras, centrado
        outSize[0] = bossSize;
        outX[0] = gx + (frontWidth - bossSize) / 2;
        outY[0] = gy;

        // Esbirros al frente en triangulo
        int frontY = gy + bossSize + gap + 5;
        for (int i = 1; i < 4; i++) {
            outSize[i] = minionSize;
            outX[i] = gx + (i - 1) * (minionSize + gap);
            outY[i] = frontY;
        }
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

        int playerSize = Math.min(h - 80, 200);
        int hpBarH = 10;
        int py = (h - playerSize) / 2;
        int px = (int) (w * 0.1);

        BufferedImage frameJugador = animJugador.getFrame();
        if (frameJugador != null) {
            g2.drawImage(frameJugador, px, py, playerSize, playerSize, null);
        } else {
            SpriteSheet.dibujarJugador(g2, px, py, playerSize);
        }

        int hpBarY = py + playerSize + 5;
        int vidaJug = controlador.getJugador().obtenerVida();
        int vidaMaxJug = controlador.getJugador().getVidaMaxima();
        g2.setColor(new Color(60, 30, 30));
        g2.fillRect(px, hpBarY, playerSize, hpBarH);
        g2.setColor(new Color(80, 220, 80));
        g2.fillRect(px, hpBarY, playerSize * vidaJug / vidaMaxJug, hpBarH);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 9));
        String hpText = vidaJug + "/" + vidaMaxJug;
        int hpTextX = px + (playerSize - g2.getFontMetrics().stringWidth(hpText)) / 2;
        g2.drawString(hpText, hpTextX, hpBarY + hpBarH - 1);

        int[] exs = new int[animEnemigos.length];
        int[] eys = new int[animEnemigos.length];
        int[] eSizes = new int[animEnemigos.length];
        calcularPosicionesTriangulo(exs, eys, eSizes);

        for (int i = 0; i < animEnemigos.length; i++) {
            int ex = exs[i];
            int ey = eys[i];
            int sz = eSizes[i];

            if (i == enemigoSeleccionado && !enemigosMuertos[i]) {
                g2.setColor(new Color(255, 215, 0, 80));
                g2.fillRect(ex - 3, ey - 3, sz + 6, sz + 6);
                g2.setColor(new Color(255, 215, 0));
                g2.drawRect(ex - 3, ey - 3, sz + 6, sz + 6);
            }

            BufferedImage frame = animEnemigos[i].getFrame();
            if (frame != null) {
                g2.drawImage(frame, ex, ey, sz, sz, null);
            } else {
                SpriteSheet.dibujarEnemigo(g2, ex, ey, sz);
            }

            int ehpBarY = ey + sz + 3;
            int evida = enemigosData[i].obtenerVida();
            int evidaMax = enemigosData[i].getVidaMaxima();
            g2.setColor(new Color(60, 30, 30));
            g2.fillRect(ex, ehpBarY, sz, hpBarH);
            if (evida > 0) {
                g2.setColor(new Color(240, 80, 80));
                g2.fillRect(ex, ehpBarY, sz * evida / evidaMax, hpBarH);
            } else {
                g2.setColor(new Color(100, 30, 30));
                g2.fillRect(ex, ehpBarY, sz, hpBarH);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 8));
            String ehpText = evida + "/" + evidaMax;
            int ehpTextX = ex + (sz - g2.getFontMetrics().stringWidth(ehpText)) / 2;
            g2.drawString(ehpText, ehpTextX, ehpBarY + hpBarH - 1);

            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            g2.setColor(new Color(200, 200, 220));
            int nameX = ex + (sz - g2.getFontMetrics().stringWidth(enemigosData[i].getNombre())) / 2;
            g2.drawString(enemigosData[i].getNombre(), nameX, ey - 5);
        }

        int centerX = w / 2;
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.setColor(new Color(255, 200, 60));
        String vs = "VS";
        int vsX = (int) (w * 0.44);
        vsX = centerX - g2.getFontMetrics().stringWidth(vs) / 2;
        int vsY = h / 2 - 20;
        g2.drawString(vs, vsX, vsY);

        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.setColor(new Color(200, 220, 240));
        int msgX = centerX - g2.getFontMetrics().stringWidth(mensajeCentral) / 2;
        g2.drawString(mensajeCentral, msgX, vsY + 30);
    }
}
