package cartas;

import entidades.Combatiente;
import entidades.Jugador;

public abstract class Carta {
    protected String nombre;
    protected int costo;

    public Carta(String nombre, int costo) {
        this.nombre = nombre;
        this.costo = costo;
    }

    public abstract void usar(Jugador usuario, Combatiente objetivo);

    public String getNombre() {
        return nombre;
    }
}