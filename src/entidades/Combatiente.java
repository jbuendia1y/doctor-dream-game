package entidades;

public interface Combatiente {
    void recibirDanio(int danio);
    boolean estaVivo();
    int obtenerVida();
}
