package juego.modelo;

import java.util.List;

/**
 * Interfaz base para cada nivel del juego.
 * Cada nivel define su paciente, diálogos y recursos.
 */
public interface Nivel {

    String getNombrePaciente();

    List<String> getDialogos();

    /** Ruta relativa a src/juego/recursos/ para el fondo del consultorio. */
    String getFondoConsultorio();

    /** Ruta relativa a src/juego/recursos/ para el fondo de la casa. */
    String getFondoCasa();

    /** Key para GestorRecursos del sprite del paciente, o null si no tiene. */
    String getSpritePaciente();
}
