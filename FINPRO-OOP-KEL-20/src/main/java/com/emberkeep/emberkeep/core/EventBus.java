package com.emberkeep.emberkeep.core;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Papan pengumuman: yang berlangganan akan dikabari saat ada kejadian. */
public class EventBus {
    private final Map<EventType, List<Consumer<GameEvent>>> listeners = new EnumMap<>(EventType.class);

    public void subscribe(EventType type, Consumer<GameEvent> listener) {
        listeners.computeIfAbsent(type, t -> new ArrayList<>()).add(listener);
    }

    public void publish(GameEvent event) {
        List<Consumer<GameEvent>> list = listeners.get(event.getType());
        if (list == null) return;
        for (Consumer<GameEvent> listener : new ArrayList<>(list)) {
            listener.accept(event);
        }
    }

    public void clear() {
        listeners.clear();
    }
}
