package juego.guardado;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Gestor de persistencia de partidas.
 * Guarda/carga en: {user.home}/.doctordream/save.dat
 */
public class GestorGuardado {

    private static final String DIR = System.getProperty("user.home") + "/.doctordream";
    private static final String ARCHIVO = DIR + "/save.dat";

    private GestorGuardado() {
    }

    /**
     * Verifica si existe una partida guardada.
     */
    public static boolean existePartida() {
        return new File(ARCHIVO).exists();
    }

    /**
     * Guarda la partida en disco.
     */
    public static void guardar(DatosPartida datos) {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(datos);
        } catch (IOException e) {
            System.err.println("Error al guardar partida: " + e.getMessage());
        }
    }

    /**
     * Carga la partida desde disco.
     * @return DatosPartida o null si no existe o hay error.
     */
    public static DatosPartida cargar() {
        if (!existePartida()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (DatosPartida) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar partida: " + e.getMessage());
            return null;
        }
    }

    /**
     * Elimina la partida guardada.
     */
    public static void eliminar() {
        File f = new File(ARCHIVO);
        if (f.exists()) {
            f.delete();
        }
    }
}
