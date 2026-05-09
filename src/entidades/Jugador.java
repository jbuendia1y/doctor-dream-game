package entidades;
import mazo.Mazo;
import cartas.Carta;

public class Jugador extends Entidad{
    private int bonoDanio;
    private int escudo;
    private int mana;
    private Mazo mazo;
    
    public Jugador(String nombre, int vidaMaxima, Mazo mazo){
        super(nombre, vidaMaxima);
        this.mazo=mazo;
        this.bonoDanio=0;
    }

    public void agregarMana(int mana){
        this.mana = mana;
    }

    public int obtenerMana(){
        return mana;
    }

    public void agregarEscudo(int escudo){
        this.escudo += escudo;
    }

    public int obtenerEscudo(){
        return escudo;
    }
    
    public void agregarBonoDanio(int cantidad){
        bonoDanio += cantidad;
    }
    
    public int obtenerBonoDanio(){
        return bonoDanio;
    }
    
    public void usarCarta(Carta carta, Combatiente objetivo){
        carta.usar(this, objetivo);
    }
    
    public Mazo getMazo(){
        return mazo;
    }
}
