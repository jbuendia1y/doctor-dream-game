package juego.modelo;

import java.util.List;
import juego.modelo.niveles.Nivel1Gripe;

/**
 * Gestor central de niveles del juego.
 * Centraliza la lista de niveles y permite obtenerlos por índice.
 */
public class GestorNiveles {

    private static final List<Nivel> NIVELES = List.of(
        new Nivel1Gripe()
    );

    private GestorNiveles() {
    }

    public static Nivel getNivel(int index) {
        if (index < 0 || index >= NIVELES.size()) {
            return null;
        }
        return NIVELES.get(index);
    }

    public static int totalNiveles() {
        return NIVELES.size();
    }
}
