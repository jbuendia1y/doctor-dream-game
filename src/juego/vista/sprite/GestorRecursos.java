package juego.vista.sprite;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class GestorRecursos {

    private static GestorRecursos instancia;
    private final Map<String, BufferedImage[]> animaciones = new HashMap<>();
    private final Map<String, BufferedImage> imagenes = new HashMap<>();

    private GestorRecursos() {
        cargarTodo();
    }

    public static GestorRecursos getInstancia() {
        if (instancia == null) {
            instancia = new GestorRecursos();
        }
        return instancia;
    }

    private void cargarTodo() {
        animaciones.put("doctor_idle", cargarFrames("DOCTOR_ESTADOS/", "Doctor animado0", 4, ".png"));
        animaciones.put("doctor_danio", cargarFrames("DOCTOR_ESTADOS/", "DoctorDañado0", 3, ".png"));
        animaciones.put("doctor_muerte", cargarFrames("DOCTOR_ESTADOS/", "muerteDoctor0", 9, ".png"));

        animaciones.put("doctor_ataque", cargarFrames("DOCTOR_CARTAS/", "AtackDoctor0", 7, ".png"));
        animaciones.put("doctor_defensa", cargarFrames("DOCTOR_CARTAS/", "DefenseDoctor0", 7, ".png"));
        animaciones.put("doctor_boost", cargarFrames("DOCTOR_CARTAS/", "Boost Doctor0", 7, ".png"));

        animaciones.put("virus", cargarFrames("ENEMIGOS/", "Virus", 4, ".png", 1));
        animaciones.put("moquillo", cargarFrames("ENEMIGOS/", "Moquillo0", 5, ".png"));
        animaciones.put("moco_mutado", cargarFrames("ENEMIGOS/", "MocoMutadoAnimado0", 5, ".png"));
        animaciones.put("boss", cargarFrames("ENEMIGOS/", "boss10", 9, ".png"));

        imagenes.put("mapa1", cargarImagen("MAPAS/mapa1.png"));
    }

    private BufferedImage[] cargarFrames(String dir, String prefix, int count, String suffix) {
        return cargarFrames(dir, prefix, count, suffix, 0);
    }

    private BufferedImage[] cargarFrames(String dir, String prefix, int count, String suffix, int offset) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            String path = dir + prefix + (i + offset) + suffix;
            frames[i] = cargarImagen(path);
        }
        return frames;
    }

    private BufferedImage cargarImagen(String path) {
        InputStream is = getClass().getResourceAsStream("/juego/recursos/" + path);
        if (is != null) {
            try {
                return ImageIO.read(is);
            } catch (IOException e) {
            }
        }
        try {
            return ImageIO.read(new File("src/juego/recursos/" + path));
        } catch (IOException e) {
            System.err.println("No se pudo cargar: " + path);
            return null;
        }
    }

    public BufferedImage[] getAnimacion(String key) {
        return animaciones.get(key);
    }

    public BufferedImage getImagen(String key) {
        return imagenes.get(key);
    }

    public String getAnimacionKeyPorTipoEnemigo(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "virus" -> "virus";
            case "moquillo" -> "moquillo";
            case "moco mutado" -> "moco_mutado";
            case "boss" -> "boss";
            default -> "virus";
        };
    }
}
