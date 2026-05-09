package juego.vista.sprite;

import cartas.Carta;
import cartas.CartaAtaque;
import cartas.CartaDefensa;
import cartas.CartaEfecto;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class SpriteSheet {

    public static void dibujarJugador(Graphics2D g, int x, int y, int s) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color piel = new Color(255, 210, 170);
        Color bata = new Color(190, 225, 255);
        Color pantalon = new Color(40, 60, 100);

        int cx = x + s / 2;
        int cabezaR = s / 4;
        int cabezaX = cx - cabezaR;

        g.setColor(piel);
        g.fillOval(cabezaX, y, cabezaR * 2, cabezaR * 2);

        g.setColor(Color.WHITE);
        g.fillRect(cx - cabezaR / 2, y - s / 12, cabezaR, s / 14);
        g.fillRect(cx - cabezaR / 2 - s / 24, y - s / 24, cabezaR + s / 12, s / 14);

        int cuerpoX = cx - s / 4;
        int cuerpoY = y + cabezaR * 2;
        int cuerpoW = s / 2;
        int cuerpoH = s / 3;

        g.setColor(bata);
        g.fillRoundRect(cuerpoX, cuerpoY, cuerpoW, cuerpoH, 10, 10);

        g.setColor(piel);
        g.fillRoundRect(x, cuerpoY + s / 16, s / 6, s / 5, 6, 6);
        g.fillRoundRect(x + s - s / 6, cuerpoY + s / 16, s / 6, s / 5, 6, 6);

        g.setColor(pantalon);
        g.fillRect(cuerpoX + s / 16, cuerpoY + cuerpoH - s / 10, s / 5, s / 4);
        g.fillRect(cuerpoX + s / 2 - s / 16, cuerpoY + cuerpoH - s / 10, s / 5, s / 4);

        g.setColor(new Color(80, 180, 255));
        g.fillOval(cx - s / 12, y + s / 8, s / 6, s / 10);
        g.fillOval(cx + s / 16, y + s / 8, s / 6, s / 10);
        g.setColor(new Color(40, 100, 180));
        g.fillOval(cx - s / 16, y + s / 7, s / 14, s / 12);
        g.fillOval(cx + s / 10, y + s / 7, s / 14, s / 12);

        g.setColor(new Color(220, 120, 100));
        g.drawArc(cx - s / 10, y + s / 4, s / 5, s / 8, 0, -180);
    }

    public static void dibujarEnemigo(Graphics2D g, int x, int y, int s) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color base = new Color(160, 30, 70);
        Color oscuro = new Color(100, 15, 40);

        g.setColor(base);
        g.fillOval(x + s / 8, y + s / 6, s * 3 / 4, s * 3 / 4);

        g.setColor(base.darker());
        g.fillOval(x, y + s * 2 / 3, s / 4, s / 5);
        g.fillOval(x + s / 4, y + s * 3 / 4, s / 4, s / 5);
        g.fillOval(x + s / 2, y + s * 3 / 4, s / 4, s / 5);
        g.fillOval(x + s * 3 / 4, y + s * 2 / 3, s / 4, s / 5);

        g.setColor(new Color(255, 60, 60));
        g.fillOval(x + s / 4, y + s / 3, s / 6, s / 6);
        g.fillOval(x + s * 7 / 12, y + s / 3, s / 6, s / 6);
        g.setColor(Color.BLACK);
        g.fillOval(x + s / 4 + s / 24, y + s / 3 + s / 24, s / 12, s / 12);
        g.fillOval(x + s * 7 / 12 + s / 24, y + s / 3 + s / 24, s / 12, s / 12);

        g.setColor(new Color(200, 200, 100));
        g.fillOval(x + s * 3 / 8, y + s * 7 / 12, s / 6, s / 10);

        g.setColor(oscuro);
        g.fillRect(x + s / 8, y, s / 5, s / 6);
        g.fillRect(x + s - s / 3, y, s / 5, s / 8);
    }

    public static void dibujarEsbirro(Graphics2D g, int x, int y, int s) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(60, 160, 60));
        g.fillOval(x + s / 6, y + s / 4, s * 2 / 3, s * 2 / 3);

        g.setColor(new Color(200, 50, 50));
        g.fillOval(x + s / 3, y + s / 3 + s / 12, s / 8, s / 8);
        g.fillOval(x + s / 2, y + s / 3 + s / 12, s / 8, s / 8);

        g.setColor(Color.BLACK);
        g.fillOval(x + s / 3 + s / 24, y + s / 3 + s / 10, s / 16, s / 16);
        g.fillOval(x + s / 2 + s / 24, y + s / 3 + s / 10, s / 16, s / 16);

        g.setColor(new Color(40, 120, 40));
        g.fillRoundRect(x + s / 4, y, s / 2, s / 6, 4, 4);
    }

    public static void dibujarCarta(Graphics2D g, int x, int y, int w, int h, Carta carta) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fondo;
        String tipo = "?";

        if (carta instanceof CartaAtaque) {
            fondo = new Color(180, 40, 40);
            tipo = "⚔️";
        } else if (carta instanceof CartaDefensa) {
            fondo = new Color(40, 100, 180);
            tipo = "🛡️";
        } else if (carta instanceof CartaEfecto) {
            fondo = new Color(40, 150, 80);
            tipo = "✨";
        } else {
            fondo = Color.GRAY;
        }

        g.setColor(fondo);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(fondo.brighter());
        g.drawRoundRect(x, y, w, h, 12, 12);

        g.setFont(new Font("Monospaced", Font.BOLD, w / 4));
        g.setColor(Color.WHITE);
        String label = tipo + " " + carta.getNombre();
        if (g.getFontMetrics().stringWidth(label) > w - 10) {
            label = carta.getNombre().substring(0, Math.min(6, carta.getNombre().length()));
        }
        g.drawString(label, x + 8, y + h / 2 + 5);
    }
}
