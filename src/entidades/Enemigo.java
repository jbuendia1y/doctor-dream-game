package entidades;

public class Enemigo extends Entidad {

    private String tipo;

    public Enemigo(String nombre, int vidaMaxima, String tipo) {
        super(nombre, vidaMaxima);
        this.tipo = tipo;
    }

    public void atacar(Combatiente objetivo) {
        objetivo.recibirDanio(calcularDanio());
    }

    /**
     * Cada tipo de enemigo tiene un rango de daño diferente.
     * Moquillo = enemigo básico, poco daño.
     * Moco Mutado = daño medio.
     * Boss = daño alto.
     * Virus = daño bajo (default).
     */
    public int calcularDanio() {
        // Daño reducido: 3 enemigos por turno, jugador tiene 100 HP
        // Ronda típica: ~8+10+14 = ~32 daño, manejable con escudos
        return switch (tipo.toLowerCase()) {
            case "moquillo"    -> 4 + (int)(Math.random() * 5);  // 4-8
            case "moco mutado" -> 7 + (int)(Math.random() * 6);  // 7-12
            case "boss"        -> 11 + (int)(Math.random() * 7); // 11-17
            default            -> 4 + (int)(Math.random() * 5);  // Virus: 4-8
        };
    }

    public String getTipo() {
        return tipo;
    }
}
