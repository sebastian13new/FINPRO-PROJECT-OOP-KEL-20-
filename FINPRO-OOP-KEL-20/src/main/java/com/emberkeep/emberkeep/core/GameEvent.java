package com.emberkeep.emberkeep.core;

/** Satu kabar: jenisnya, satu angka, dan satu teks (keduanya opsional). */
public final class GameEvent {
    private final EventType type;
    private final int value;
    private final String text;

    public GameEvent(EventType type, int value, String text) {
        this.type = type;
        this.value = value;
        this.text = text;
    }

    public GameEvent(EventType type, int value) { this(type, value, null); }
    public GameEvent(EventType type) { this(type, 0, null); }

    public EventType getType() { return type; }
    public int getValue() { return value; }
    public String getText() { return text; }
}
