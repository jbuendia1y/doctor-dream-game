package cartas;

import entidades.Combatiente;
import entidades.Jugador;

public class CartaAtaque extends Carta {

    private int danio;

    public CartaAtaque(String nombre, int costo, int danio) {
        super(nombre, costo);
        this.danio = danio;
    }

    @Override
    public void usar(Jugador usuario, Combatiente objetivo) {
        int total = danio + usuario.obtenerBonoDanio();
        objetivo.recibirDanio(total);
    }
}