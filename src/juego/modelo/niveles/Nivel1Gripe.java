package juego.modelo.niveles;

import java.util.List;
import juego.modelo.Nivel;

/**
 * Nivel 1: Paciente con gripe.
 * Don Samuel llega al consultorio con síntomas clásicos de gripe.
 */
public class Nivel1Gripe implements Nivel {

    @Override
    public String getNombrePaciente() {
        return "Don Samuel";
    }

    @Override
    public List<String> getDialogos() {
        return List.of(
            "Don Samuel: Doctor... no me siento bien...",
            "Don Samuel: Tengo fiebre, tos y me duele la cabeza.",
            "Tú: Déjeme revisarlo... Hmm, tiene todos los síntomas de gripe.",
            "Tú: Le voy a recetar paracetamol y mucho reposo. Nada de trabajo por 3 días.",
            "Don Samuel: Gracias, doctor. Ya me siento mejor solo de verlo."
        );
    }

    @Override
    public String getFondoConsultorio() {
        return "CONSULTORIO/fondo.jpeg";
    }

    @Override
    public String getFondoCasa() {
        return "CASA/fondo.jpg";
    }

    @Override
    public String getSpritePaciente() {
        return "npc1_sprite";
    }
}
