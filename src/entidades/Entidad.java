package entidades;

public abstract class Entidad implements Combatiente{
    protected String nombre;
    protected int vida;
    protected int vidaMaxima;
    
    public Entidad(String nombre, int vidaMaxima){
        this.nombre=nombre;
        this.vidaMaxima=vidaMaxima;
        this.vida=vidaMaxima;
    }
    
    @Override
    public void recibirDanio(int danio){
        // El escudo absorbe primero (solo Jugador tiene escudo, pero la lógica es polimórfica)
        int danioReal = danio;
        if (this instanceof entidades.Jugador j) {
            int escudoActual = j.obtenerEscudo();
            if (escudoActual > 0) {
                int absorcion = Math.min(escudoActual, danioReal);
                j.reducirEscudo(absorcion);
                danioReal -= absorcion;
            }
        }
        vida -= danioReal;
        if(vida < 0){
            vida = 0;
        }
    }
    
    @Override
    public boolean estaVivo(){
        return vida>0;
    }
    
    @Override
    public int obtenerVida(){
        return vida;
    }
    
    public String getNombre(){
        return nombre;
    }
}
