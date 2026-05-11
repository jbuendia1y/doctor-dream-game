package juego.vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.Timer;
import juego.controlador.ControladorMenu;

/**
 * PanelMenu — Menú principal rediseñado.
 * Fondo oscuro propio (no el mapa del juego), partículas flotantes
 * estilo células, título grande, botones: Jugar / Instrucciones / Salir.
 */
public class PanelMenu extends JPanel {

    private final ControladorMenu controlador;

    // ── Nubes nocturnas (partículas) ──────────────────────────────
    private static final int N = 30;
    private final float[] px  = new float[N];
    private final float[] py  = new float[N];
    private final float[] pvx = new float[N];
    private final float[] pvy = new float[N];
    private final float[] pr  = new float[N];
    private final Color[] pc  = new Color[N];

    // ── Botones ───────────────────────────────────────────────────
    private static final String[] LABELS = { "⚔️  Iniciar Combate", "❓  Instrucciones", "✕  Salir" };
    private static final Color[]  COLORES = {
        new Color(40, 130, 70),    // verde
        new Color(40, 100, 180),   // azul
        new Color(150, 45, 45)     // rojo
    };
    private final int[] BY = new int[3];
    private static final int BW = 300, BH = 52;
    private int hover = -1;

    // ── Estado instrucciones ──────────────────────────────────────
    private boolean verInstrucciones = false;

    public PanelMenu(ControladorMenu controlador) {
        this.controlador = controlador;
        setBackground(new Color(5, 10, 35));
        inicializarParticulas();

        // Timer animación 50ms (~20fps para el fondo)
        new Timer(50, e -> {
            moverParticulas();
            repaint();
        }).start();

        // Mouse hover
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                if (verInstrucciones) return;
                hover = -1;
                int bx = getWidth() / 2 - BW / 2;
                for (int i = 0; i < 3; i++) {
                    if (e.getX() >= bx && e.getX() <= bx + BW
                            && e.getY() >= BY[i] && e.getY() <= BY[i] + BH) {
                        hover = i; break;
                    }
                }
            }
        });

        // Mouse click
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (verInstrucciones) {
                    // Clic en "Volver"
                    int bx = getWidth() / 2 - 80;
                    int by = getHeight() - 110;
                    if (e.getX() >= bx && e.getX() <= bx + 160
                            && e.getY() >= by && e.getY() <= by + 40) {
                        verInstrucciones = false;
                    }
                    return;
                }
                int bx = getWidth() / 2 - BW / 2;
                for (int i = 0; i < 3; i++) {
                    if (e.getX() >= bx && e.getX() <= bx + BW
                            && e.getY() >= BY[i] && e.getY() <= BY[i] + BH) {
                        switch (i) {
                            case 0 -> controlador.iniciarCombate();
                            case 1 -> verInstrucciones = true;
                            case 2 -> System.exit(0);
                        }
                        return;
                    }
                }
            }
        });
    }

    // ── Nubes nocturnas ───────────────────────────────────────────
    private void inicializarParticulas() {
        // Nubes: tamaños grandes, movimiento muy lento hacia la derecha
        for (int i = 0; i < N; i++) {
            px[i]  = (float)(Math.random() * 1100);
            py[i]  = (float)(Math.random() * 420);          // solo en la mitad superior
            pvx[i] = (float)(0.08 + Math.random() * 0.18); // siempre hacia la derecha
            pvy[i] = (float)(Math.random() * 0.06 - 0.03); // ligero vaivén vertical
            pr[i]  = (float)(28 + Math.random() * 55);     // nubes grandes
            // Tonos: blanco-azulado semitransparente para cielo nocturno
            int alpha = 18 + (int)(Math.random() * 28);
            pc[i]  = new Color(180 + (int)(Math.random() * 40),
                               200 + (int)(Math.random() * 30),
                               240, alpha);
        }
    }

    private void moverParticulas() {
        int w = getWidth(), h = getHeight();
        for (int i = 0; i < N; i++) {
            px[i] += pvx[i];
            py[i] += pvy[i];
            // Las nubes salen por la derecha y vuelven por la izquierda
            if (px[i] - pr[i] > w + 60) px[i] = -pr[i] * 2;
            // Rebote suave vertical
            if (py[i] < 0 || py[i] > h * 0.65f) pvy[i] = -pvy[i];
        }
    }

    // ── Render ────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // ── Fondo: cielo azul noche con degradado ─────────────────
        // Parte superior: azul muy oscuro → azul medianoche en la mitad
        java.awt.GradientPaint gradiente = new java.awt.GradientPaint(
            0, 0,   new Color(5, 10, 35),      // azul muy oscuro arriba
            0, h,   new Color(15, 35, 80)      // azul medianoche abajo
        );
        g2.setPaint(gradiente);
        g2.fillRect(0, 0, w, h);

        // ── Estrellas (puntos fijos, dibujadas una vez con posición fija) ──
        dibujarEstrellas(g2, w, h);

        // ── Nubes (partículas grandes blanco-azuladas) ─────────────
        for (int i = 0; i < N; i++) {
            int r = (int) pr[i];
            // Cada "nube" = 3 óvalos superpuestos desplazados para forma de nube
            g2.setColor(pc[i]);
            g2.fillOval((int)px[i] - r,        (int)py[i] - r/2,      r*2,   r);
            g2.fillOval((int)px[i] - r/2,      (int)py[i] - r*3/4,    r,     r*3/4);
            g2.fillOval((int)px[i] + r/4,      (int)py[i] - r*2/3,    r*3/4, r*2/3);
        }

        if (verInstrucciones) {
            dibujarInstrucciones(g2, w, h);
        } else {
            dibujarTitulo(g2, w, h);
            dibujarBotones(g2, w, h);
            dibujarCreditos(g2, w, h);
        }
    }

    // Posiciones fijas de estrellas (generadas una sola vez con semilla fija)
    private static final int[][] ESTRELLAS;
    static {
        java.util.Random rng = new java.util.Random(42); // semilla fija → siempre igual
        ESTRELLAS = new int[90][3]; // [x%, y%, brillo]
        for (int i = 0; i < 90; i++) {
            ESTRELLAS[i][0] = rng.nextInt(100);
            ESTRELLAS[i][1] = rng.nextInt(70);   // solo en el 70% superior
            ESTRELLAS[i][2] = 80 + rng.nextInt(120); // brillo 80-200
        }
    }

    private void dibujarEstrellas(Graphics2D g2, int w, int h) {
        for (int[] s : ESTRELLAS) {
            int sx = s[0] * w / 100;
            int sy = s[1] * h / 100;
            int br = s[2];
            g2.setColor(new Color(br, br, Math.min(255, br + 40), 200));
            g2.fillOval(sx, sy, 2, 2);
        }
    }

    private void dibujarTitulo(Graphics2D g2, int w, int h) {
        // Línea decorativa superior
        g2.setColor(new Color(60, 140, 220, 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(w / 2 - 220, h / 4 - 10, w / 2 + 220, h / 4 - 10);
        g2.setStroke(new BasicStroke(1f));

        // Ícono DNA decorativo
        g2.setFont(new Font("Monospaced", Font.PLAIN, 36));
        g2.setColor(new Color(100, 200, 255, 180));
        String dna = "🎲";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(dna, w / 2 - fm.stringWidth(dna) / 2, h / 4 + 30);

        // Título principal
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        fm = g2.getFontMetrics();
        String titulo = "DOCTOR DREAM";
        // Sombra
        g2.setColor(new Color(0, 40, 100, 140));
        g2.drawString(titulo, w / 2 - fm.stringWidth(titulo) / 2 + 3, h / 4 + 85);
        // Texto principal
        g2.setColor(new Color(100, 200, 255));
        g2.drawString(titulo, w / 2 - fm.stringWidth(titulo) / 2, h / 4 + 82);

        // Subtítulo
        g2.setFont(new Font("Monospaced", Font.ITALIC, 18));
        fm = g2.getFontMetrics();
        String sub = "Guardianes del Cuerpo";
        g2.setColor(new Color(120, 220, 160));
        g2.drawString(sub, w / 2 - fm.stringWidth(sub) / 2, h / 4 + 112);

        // Descripción pequeña
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g2.getFontMetrics();
        String desc = "Juego educativo de combate por turnos con cartas";
        g2.setColor(new Color(140, 160, 200));
        g2.drawString(desc, w / 2 - fm.stringWidth(desc) / 2, h / 4 + 136);

        // Línea decorativa inferior
        g2.setColor(new Color(60, 140, 220, 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(w / 2 - 220, h / 4 + 148, w / 2 + 220, h / 4 + 148);
        g2.setStroke(new BasicStroke(1f));
    }

    private void dibujarBotones(Graphics2D g2, int w, int h) {
        int bx     = w / 2 - BW / 2;
        int startY = h / 2 + 30;
        int sep    = 68;

        for (int i = 0; i < 3; i++) {
            BY[i] = startY + i * sep;
            boolean isHover = (hover == i);
            Color base = COLORES[i];
            Color fill = isHover
                    ? base.brighter()
                    : new Color(base.getRed(), base.getGreen(), base.getBlue(), 200);

            // Fondo
            g2.setColor(fill);
            g2.fillRoundRect(bx, BY[i], BW, BH, 14, 14);

            // Borde
            g2.setColor(isHover ? Color.WHITE : new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(isHover ? 2f : 1f));
            g2.drawRoundRect(bx, BY[i], BW, BH, 14, 14);
            g2.setStroke(new BasicStroke(1f));

            // Texto
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, isHover ? 17 : 16));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(LABELS[i],
                    bx + (BW - fm.stringWidth(LABELS[i])) / 2,
                    BY[i] + BH / 2 + 6);
        }
    }

    private void dibujarCreditos(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(new Color(80, 100, 140));
        String cr = "AED · Aldhair  |  Joaquín  |  Paolo  |  Dens";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(cr, w / 2 - fm.stringWidth(cr) / 2, h - 14);
    }

    private void dibujarInstrucciones(Graphics2D g2, int w, int h) {
        // Panel oscuro
        g2.setColor(new Color(5, 10, 25, 225));
        g2.fillRoundRect(80, 50, w - 160, h - 100, 18, 18);
        g2.setColor(new Color(60, 120, 200));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(80, 50, w - 160, h - 100, 18, 18);
        g2.setStroke(new BasicStroke(1f));

        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.setColor(new Color(100, 200, 255));
        g2.drawString("¿Cómo Jugar?", 110, 92);

        String[] lineas = {
            "▸ El combate es por TURNOS — nadie se mueve.",
            "▸ Orden:  Tú → Moquillo → Moco Mutado → Boss → repite",
            "▸ En tu turno, haz clic en una CARTA para usarla.",
            "  · Ataque   → daño directo al enemigo activo",
            "  · Defensa  → te da escudo que absorbe el próximo daño",
            "  · Efecto   → te da bono de daño permanente",
            "▸ Cada turno robas 1 carta nueva.",
            "▸ Los enemigos atacan automáticamente en su turno.",
            "▸ El escudo 🛡️ absorbe el daño antes de bajar tu vida.",
            "▸ ¡Derrota al Moquillo, Moco Mutado y Boss para ganar!"
        };

        g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g2.setColor(new Color(190, 210, 240));
        int y = 130;
        for (String l : lineas) {
            g2.drawString(l, 110, y);
            y += 36;
        }

        // Botón volver
        int bx = w / 2 - 80, by = h - 110;
        g2.setColor(new Color(50, 90, 170, 210));
        g2.fillRoundRect(bx, by, 160, 40, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        String volver = "◀ Volver";
        g2.drawString(volver, bx + (160 - fm.stringWidth(volver)) / 2, by + 26);
    }
}
