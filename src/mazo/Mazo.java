/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mazo;
import cartas.Carta;

import java.util.*;

public class Mazo {

    private List<Carta> pilaRobo;
    private Stack<Carta> pilaDescarte;
    private Queue<Carta> mano;

    public Mazo() {
        pilaRobo = new ArrayList<>();
        pilaDescarte = new Stack<>();
        mano = new LinkedList<>();
    }

    public void barajar() {
        Collections.shuffle(pilaRobo);
    }

    public Carta robar() {
        if (pilaRobo.isEmpty()) {
            while (!pilaDescarte.isEmpty()) {
                pilaRobo.add(pilaDescarte.pop());
            }
            barajar();
        }

        if (!pilaRobo.isEmpty()) {
            Carta carta = pilaRobo.remove(0);
            mano.add(carta);
            return carta;
        }

        return null;
    }

    public void descartar(Carta carta) {
        if (mano.contains(carta)) {
            mano.remove(carta);
            pilaDescarte.push(carta);
        }
    }
}