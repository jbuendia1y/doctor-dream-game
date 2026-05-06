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


    /**
     * @return the pilaRobo
     */
    public List<Carta> getPilaRobo() {
        return pilaRobo;
    }

    /**
     * @param pilaRobo the pilaRobo to set
     */
    public void setPilaRobo(List<Carta> pilaRobo) {
        this.pilaRobo = pilaRobo;
    }

    /**
     * @return the pilaDescarte
     */
    public Stack<Carta> getPilaDescarte() {
        return pilaDescarte;
    }

    /**
     * @param pilaDescarte the pilaDescarte to set
     */
    public void setPilaDescarte(Stack<Carta> pilaDescarte) {
        this.pilaDescarte = pilaDescarte;
    }

    /**
     * @return the mano
     */
    public Queue<Carta> getMano() {
        return mano;
    }

    /**
     * @param mano the mano to set
     */
    public void setMano(Queue<Carta> mano) {
        this.mano = mano;
    }

    
    public Mazo() {
        pilaRobo = new ArrayList<>();
        pilaDescarte = new Stack<>();
        mano = new LinkedList<>();
    }

    public void barajar() {
        Collections.shuffle(getPilaRobo());
    }

    public Carta robar() {
        if (getPilaRobo().isEmpty()) {
            while (!pilaDescarte.isEmpty()) {
                getPilaRobo().add(getPilaDescarte().pop());
            }
            barajar();
        }

        if (!pilaRobo.isEmpty()) {
            Carta carta = getPilaRobo().remove(0);
            getMano().add(carta);
            return carta;
        }

        return null;
    }

    public void descartar(Carta carta) {
        if (getMano().contains(carta)) {
            getMano().remove(carta);
            getPilaDescarte().push(carta);
        }
    }
}