package cartas;

import entidades.Combatiente;
import entidades.Jugador;

public class CartaCuracion extends Carta {

    private int curacion;

    public CartaCuracion(String nombre, int costo, int curacion) {
        super(nombre, costo);
        this.curacion = curacion;
    }

    @Override
    public void usar(Jugador usuario, Combatiente objetivo) {
        usuario.curarse(this.curacion);
    }
}