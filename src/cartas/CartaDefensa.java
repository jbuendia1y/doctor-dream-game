package cartas;

import entidades.Combatiente;
import entidades.Jugador;

public class CartaDefensa extends Carta {

    private int escudo;

    public CartaDefensa(String nombre, int costo, int escudo) {
        super(nombre, costo);
        this.escudo = escudo;
    }

    @Override
    public void usar(Jugador usuario, Combatiente objetivo) {
        usuario.agregarEscudo(escudo);
    }
}