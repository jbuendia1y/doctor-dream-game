package entidades;

public class Enemigo extends Entidad{
    
    private String tipo;
    
    public Enemigo(String nombre, int vidaMaxima, String tipo) {
        super(nombre, vidaMaxima);
        this.tipo=tipo;
    }
    
    public void atacar(Combatiente objetivo){
        int danio=10;
        objetivo.recibirDanio(danio);
    }
    
    public String getTipo(){
        return tipo;
    }
    
    
}
