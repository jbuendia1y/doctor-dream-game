package cartas;

import entidades.Combatiente;
import entidades.Jugador;

public class CartaEfecto extends Carta {

    private int bonoDanio;

    public CartaEfecto(String nombre, int costo, int bonoDanio) {
        super(nombre, costo);
        this.bonoDanio = bonoDanio;
    }

    @Override
    public void usar(Jugador usuario, Combatiente objetivo) {
        usuario.agregarBonoDanio(bonoDanio);
    }
}