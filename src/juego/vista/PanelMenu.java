package juego.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import juego.controlador.ControladorMenu;
import juego.guardado.GestorGuardado;
import juego.vista.sprite.GestorRecursos;

public class PanelMenu extends JPanel {

    private final BufferedImage fondo;

    public PanelMenu(ControladorMenu controlador) {
        this.fondo = GestorRecursos.getInstancia().getImagen("menu_fondo");

        setLayout(new BorderLayout());

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);

        JLabel descripcion = new JLabel("Un juego de combate por turnos con cartas", SwingConstants.CENTER);
        descripcion.setFont(new Font("Monospaced", Font.PLAIN, 14));
        descripcion.setForeground(new Color(180, 200, 220));
        panelBotones.add(descripcion);

        String textoBoton = GestorGuardado.existePartida() ? "Continuar" : "⚔️  Iniciar Partida";
        JButton btnIniciar = new JButton(textoBoton);
        btnIniciar.setFont(new Font("Monospaced", Font.BOLD, 18));
        btnIniciar.setBackground(new Color(50, 120, 80));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);

        if (GestorGuardado.existePartida()) {
            btnIniciar.addActionListener(e -> controlador.continuarPartida());
        } else {
            btnIniciar.addActionListener(e -> controlador.iniciarPartida());
        }

        panelBotones.add(btnIniciar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
