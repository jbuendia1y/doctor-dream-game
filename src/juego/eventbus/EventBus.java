package juego.eventbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {

    private static final EventBus INSTANCIA = new EventBus();
    private final Map<Evento, List<Consumer<Object>>> suscriptores = new HashMap<>();

    private EventBus() {
    }

    public static EventBus getInstancia() {
        return INSTANCIA;
    }

    public void suscribir(Evento evento, Consumer<Object> callback) {
        suscriptores.computeIfAbsent(evento, k -> new ArrayList<>()).add(callback);
    }

    public void desuscribir(Evento evento, Consumer<Object> callback) {
        List<Consumer<Object>> lista = suscriptores.get(evento);
        if (lista != null) {
            lista.remove(callback);
        }
    }

    public void publicar(Evento evento, Object datos) {
        List<Consumer<Object>> lista = suscriptores.get(evento);
        if (lista != null) {
            for (Consumer<Object> cb : lista) {
                cb.accept(datos);
            }
        }
    }

    public void publicar(Evento evento) {
        publicar(evento, null);
    }
}
