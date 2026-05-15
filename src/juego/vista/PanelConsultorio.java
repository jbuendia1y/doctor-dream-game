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
import juego.eventbus.EventBus;
import juego.eventbus.Evento;
import juego.modelo.Nivel;
import juego.vista.sprite.Animacion;
import juego.vista.sprite.GestorRecursos;
import juego.vista.sprite.SpriteSheet;

/**
 * Escenario del consultorio médico.
 * El doctor aparece a la derecha y puede moverse con flechas/A-D.
 * Al acercarse al paciente se dispara la secuencia de diálogos.
 * Los diálogos usan recursos gráficos de DIALOGOS/ para los recuadros.
 */
public class PanelConsultorio extends JPanel implements KeyListener {

    private enum Estado { MOVIMIENTO, DIALOGO, TRANSICION }

    // Constantes de movimiento
    private static final int DOCTOR_SIZE = 140;
    private static final int DOCTOR_Y = 300;
    private static final int VELOCIDAD = 8;
    private static final int PACIENTE_X = 170;
    private static final int PACIENTE_Y = 300;
    private static final int PACIENTE_SIZE = 140;
    private static final int PROXIMIDAD = 200;
    private static final int TICKS_POR_FRAME = 6;

    // Constantes del diálogo
    private static final int REC_AVATAR_W = 238;
    private static final int REC_AVATAR_H = 250;
    private static final int REC_TEXTO_W = 726;
    private static final int REC_TEXTO_H = 250;
    private static final int REC_DIALOGO_TOTAL_W = REC_AVATAR_W + REC_TEXTO_W; // 964
    private static final int AVATAR_SIZE = 162;
    private static final int AVATAR_OFFSET_X = 51;
    private static final int AVATAR_OFFSET_Y = 44;
    private static final int TEXTO_OFFSET_X = 22;
    private static final int TEXTO_OFFSET_Y = 69;
    private static final int TEXTO_MAX_W = 630;
    private static final int TEXTO_MAX_H = 100;

    private final Nivel nivel;
    private final DialogoManager dialogoManager;
    private final Timer timer;

    // Recursos de diálogo
    private final BufferedImage recAvatarImg;
    private final BufferedImage recTextoImg;
    private final BufferedImage avatarGenerico;
    private final BufferedImage avatarDoctor;
    private final BufferedImage avatarNpc1;

    private Estado estado = Estado.MOVIMIENTO;
    private BufferedImage fondo;
    private Animacion animDoctor;
    private int doctorX = 1000;
    private boolean moviendoIzquierda = false;
    private boolean moviendoDerecha = false;
    private String dialogoActual;
    private String speakerActual = "";
    private int tickAnimacion = 0;

    public PanelConsultorio(Nivel nivel) {
        this.nivel = nivel;
        this.dialogoManager = new DialogoManager(nivel.getDialogos());

        this.fondo = GestorRecursos.getInstancia().getImagen("consultorio_fondo");
        BufferedImage[] doctorFrames = GestorRecursos.getInstancia().getAnimacion("doctor_idle");
        this.animDoctor = new Animacion(doctorFrames != null ? doctorFrames : new BufferedImage[0]);
        this.animDoctor.reproducirEnBucle();

        // Recursos de diálogo
        this.recAvatarImg = GestorRecursos.getInstancia().getImagen("dialogo_recuadro_avatar");
        this.recTextoImg = GestorRecursos.getInstancia().getImagen("dialogo_recuadro_texto");
        this.avatarGenerico = GestorRecursos.getInstancia().getImagen("dialogo_avatar_generico");
        this.avatarDoctor = GestorRecursos.getInstancia().getImagen("dialogo_avatar_doctor");
        this.avatarNpc1 = GestorRecursos.getInstancia().getImagen("dialogo_avatar_npc1");

        setFocusable(true);
        addKeyListener(this);

        addHierarchyListener(e -> {
            if (isShowing()) {
                requestFocusInWindow();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                if (estado == Estado.DIALOGO && e.getY() > getHeight() - 280) {
                    avanzarDialogo();
                }
            }
        });

        timer = new Timer(40, ev -> actualizar());
        timer.start();

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void actualizar() {
        switch (estado) {
            case MOVIMIENTO -> {
                if (moviendoIzquierda) {
                    doctorX = Math.max(0, doctorX - VELOCIDAD);
                }
                if (moviendoDerecha) {
                    doctorX = Math.min(getWidth() - DOCTOR_SIZE, doctorX + VELOCIDAD);
                }
                if (doctorX <= PACIENTE_X + PROXIMIDAD && !dialogoManager.isTerminado()) {
                    doctorX = Math.min(doctorX, PACIENTE_X + PROXIMIDAD);
                    moviendoIzquierda = false;
                    moviendoDerecha = false;
                    estado = Estado.DIALOGO;
                    siguienteDialogo();
                }
            }
            case DIALOGO, TRANSICION -> {
                // espera input
            }
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
            GradientPaint gp = new GradientPaint(0, 0, new Color(200, 225, 245),
                    w, h, new Color(160, 190, 215));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(180, 200, 180));
            g2.fillRect(0, h - 80, w, 80);
            g2.setColor(new Color(150, 180, 160));
            g2.drawLine(0, h - 80, w, h - 80);
        }

        // --- Paciente ---
        dibujarPaciente(g2, PACIENTE_X, PACIENTE_Y, PACIENTE_SIZE);

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

        String pacLabel = nivel.getNombrePaciente();
        int pacLabelX = PACIENTE_X + (PACIENTE_SIZE - g2.getFontMetrics().stringWidth(pacLabel)) / 2;
        g2.drawString(pacLabel, pacLabelX, PACIENTE_Y + PACIENTE_SIZE + 15);

        // --- Diálogo ---
        if (estado == Estado.DIALOGO || estado == Estado.TRANSICION) {
            dibujarDialogo(g2, w, h);
        }

        // --- Hint de proximidad ---
        if (estado == Estado.MOVIMIENTO && !dialogoManager.isTerminado()
                && doctorX <= PACIENTE_X + PROXIMIDAD + 80) {
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g2.setColor(new Color(255, 255, 200, 200));
            String hint = "Acércate al paciente...";
            int hintX = (w - g2.getFontMetrics().stringWidth(hint)) / 2;
            g2.drawString(hint, hintX, h - 280);
        }
    }

    // ============================================================
    //  DIBUJO DEL PACIENTE
    // ============================================================

    private void dibujarPaciente(Graphics2D g2, int x, int y, int size) {
        String spriteKey = nivel.getSpritePaciente();
        BufferedImage[] sprite = spriteKey != null
                ? GestorRecursos.getInstancia().getAnimacion(spriteKey) : null;

        if (sprite != null && sprite.length > 0 && sprite[0] != null) {
            g2.drawImage(sprite[0], x, y, size, size, null);
            return;
        }

        int cx = x + size / 2;

        g2.setColor(new Color(100, 150, 200));
        g2.fillRoundRect(x + size / 6, y + size / 3, size * 2 / 3, size / 2, 8, 8);

        g2.setColor(new Color(255, 210, 170));
        int cabezaR = size / 5;
        g2.fillOval(cx - cabezaR, y + size / 8, cabezaR * 2, cabezaR * 2);

        g2.setColor(new Color(160, 160, 160));
        g2.fillArc(cx - cabezaR, y + size / 8, cabezaR * 2, cabezaR * 2 / 3, 0, 180);

        g2.setColor(new Color(60, 60, 80));
        g2.fillRect(x + size / 4, y + size * 5 / 6, size / 5, size / 6);
        g2.fillRect(x + size * 2 / 4, y + size * 5 / 6, size / 5, size / 6);

        g2.setColor(new Color(255, 210, 170));
        g2.fillRoundRect(x + size / 10, y + size / 3, size / 7, size / 4, 4, 4);
        g2.fillRoundRect(x + size - size / 5, y + size / 3, size / 7, size / 4, 4, 4);

        g2.setColor(new Color(255, 150, 150, 80));
        g2.fillOval(cx - cabezaR / 2, y + size / 3, cabezaR, cabezaR / 2);
    }

    // ============================================================
    //  SISTEMA DE DIÁLOGO BASADO EN RECURSOS GRÁFICOS
    // ============================================================

    /**
     * Dibuja el panel de diálogo completo usando los recursos gráficos.
     *
     * Layout:
     * ┌─────────────────────────────────────────────────────┐
     * │  ┌──────────────┐  ┌──────────────────────────┐     │
     * │  │ Recuadro     │  │ Recuadro Texto           │     │
     * │  │ Avatar       │  │ (726x250)                │     │
     * │  │ (238x250)    │  │ Texto en (22,69)         │     │
     * │  │ Avatar en    │  │ max 630x100              │     │
     * │  │ (51,62)      │  │                          │     │
     * │  └──────────────┘  └──────────────────────────┘     │
     * └─────────────────────────────────────────────────────┘
     */
    private void dibujarDialogo(Graphics2D g2, int w, int h) {
        // Centrar el conjunto de recuadros horizontalmente
        int dialogX = (w - REC_DIALOGO_TOTAL_W) / 2;
        int dialogH = 280;    // espacio para 250px + padding
        int dialogY = h - dialogH - 10;

        int recY = dialogY + (dialogH - REC_AVATAR_H) / 2;

        // ---- Recuadro de avatar (izquierda) ----
        if (recAvatarImg != null) {
            int avatarRecX = dialogX;
            g2.drawImage(recAvatarImg, avatarRecX, recY, null);

            // Avatar: 162x162 dentro del recuadro en (51, 62)
            BufferedImage avatar = obtenerAvatarActual();
            if (avatar != null) {
                g2.drawImage(avatar, avatarRecX + AVATAR_OFFSET_X, recY + AVATAR_OFFSET_Y,
                        AVATAR_SIZE, AVATAR_SIZE, null);
            }
        }

        // ---- Recuadro de texto (derecha, pegado al avatar) ----
        int textRecX = dialogX + REC_AVATAR_W;

        if (recTextoImg != null) {
            g2.drawImage(recTextoImg, textRecX, recY, null);

            // Texto dentro del recuadro en (22, 69) desde su origen
            int textX = textRecX + TEXTO_OFFSET_X;
            int textY = recY + TEXTO_OFFSET_Y;

            if (dialogoActual != null) {
                g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
                g2.setColor(new Color(35, 35, 45));
                dibujarTextoEnArea(g2, dialogoActual, textX, textY, TEXTO_MAX_W, TEXTO_MAX_H);
            }
        }

        // ---- Indicadores sobre los recuadros ----
        // Progreso (esquina superior derecha del recuadro de texto)
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(new Color(255, 255, 255, 120));
        String progreso = (dialogoManager.getIndice()) + "/" + dialogoManager.getTotal();
        g2.drawString(progreso, dialogX + REC_DIALOGO_TOTAL_W - 50, dialogY + 22);

        // Avance (abajo del recuadro de texto)
        g2.setFont(new Font("Monospaced", Font.ITALIC, 11));
        String avance = dialogoManager.isTerminado()
                ? "Presiona ENTER para continuar"
                : "Presiona ENTER o clic";
        g2.setColor(new Color(255, 255, 255, 150));
        FontMetrics fm = g2.getFontMetrics();
        int avanceX = dialogX + REC_DIALOGO_TOTAL_W - fm.stringWidth(avance) - 15;
        g2.drawString(avance, avanceX, h - 12);
    }

    /**
     * Obtiene la imagen del avatar según quién habla:
     * - "Tú" → avatar del doctor
     * - "Don Samuel" → avatar NPC1
     * - Otro → avatar genérico (default)
     */
    private BufferedImage obtenerAvatarActual() {
        if ("Tú".equals(speakerActual)) {
            return avatarDoctor;
        }
        if ("Don Samuel".equals(speakerActual)) {
            return avatarNpc1;
        }
        return avatarGenerico;
    }

    /**
     * Dibuja texto con word-wrap dentro de un área definida.
     */
    private void dibujarTextoEnArea(Graphics2D g2, String texto,
                                     int startX, int startY,
                                     int maxW, int maxH) {
        FontMetrics fm = g2.getFontMetrics();
        int lineH = fm.getHeight();
        int y = startY;
        int maxY = startY + maxH;

        String[] palabras = texto.split(" ");
        StringBuilder linea = new StringBuilder();

        for (String palabra : palabras) {
            String prueba = linea.isEmpty() ? palabra : linea + " " + palabra;
            if (fm.stringWidth(prueba) > maxW && !linea.isEmpty()) {
                g2.drawString(linea.toString(), startX, y);
                y += lineH;
                if (y > maxY) return; // no más espacio
                linea = new StringBuilder(palabra);
            } else {
                if (!linea.isEmpty()) linea.append(" ");
                linea.append(palabra);
            }
        }
        if (linea.length() > 0 && y <= maxY) {
            g2.drawString(linea.toString(), startX, y);
        }
    }

    /**
     * Avanza al siguiente diálogo, parseando speaker + texto.
     * Formato esperado: "Speaker: mensaje"
     */
    private void siguienteDialogo() {
        String raw = dialogoManager.siguiente();
        if (raw == null) {
            dialogoActual = null;
            speakerActual = "";
            return;
        }

        int idxSep = raw.indexOf(": ");
        if (idxSep > 0) {
            speakerActual = raw.substring(0, idxSep);
            dialogoActual = raw.substring(idxSep + 2);
        } else {
            speakerActual = "";
            dialogoActual = raw;
        }
    }

    private void avanzarDialogo() {
        if (estado != Estado.DIALOGO) return;

        if (dialogoManager.isTerminado()) {
            estado = Estado.TRANSICION;
            timer.stop();
            EventBus.getInstancia().publicar(Evento.MOSTRAR_CASA, nivel);
            return;
        }

        siguienteDialogo();
    }

    // ============================================================
    //  KEY LISTENER
    // ============================================================

    @Override
    public void keyPressed(KeyEvent e) {
        if (estado == Estado.MOVIMIENTO) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> moviendoIzquierda = true;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> moviendoDerecha = true;
            }
        }
        if (estado == Estado.DIALOGO) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                avanzarDialogo();
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
