package juego.vista;

import java.util.List;

/**
 * Gestiona la secuencia de diálogos tipo RPG.
 * Cada nivel define su propia lista de strings y el DialogoManager
 * se encarga de avanzar y saber cuándo terminó.
 */
public class DialogoManager {

    private final List<String> dialogos;
    private int indice;
    private boolean terminado;

    public DialogoManager(List<String> dialogos) {
        this.dialogos = dialogos;
        this.indice = 0;
        this.terminado = false;
    }

    /**
     * Avanza al siguiente diálogo.
     * @return el texto del diálogo actual, o null si ya terminó.
     */
    public String siguiente() {
        if (terminado) {
            return null;
        }
        String actual = dialogos.get(indice);
        indice++;
        if (indice >= dialogos.size()) {
            terminado = true;
        }
        return actual;
    }

    /**
     * Obtiene el texto del diálogo actual sin avanzar.
     */
    public String getActual() {
        if (indice < dialogos.size()) {
            return dialogos.get(indice);
        }
        return null;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public int getIndice() {
        return indice;
    }

    public int getTotal() {
        return dialogos.size();
    }

    public void reiniciar() {
        indice = 0;
        terminado = false;
    }
}
