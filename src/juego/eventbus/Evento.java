package juego.eventbus;

public enum Evento {
    INICIAR_COMBATE,
    CARTA_USADA,
    TURNO_JUGADOR,
    TURNO_ENEMIGO,
    ENEMIGO_ATACO,
    VIDA_ACTUALIZADA,
    COMBATE_TERMINADO,
    ENEMIGO_DERROTADO,
    NUEVO_ENEMIGO,
    BLOQUEAR_CARTAS,    // deshabilita botones mientras corre el turno
    MOSTRAR_MENU,
    MOSTRAR_COMBATE
}
