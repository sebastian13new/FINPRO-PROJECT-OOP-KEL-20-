package com.emberkeep.emberkeep.core;

/** Menyimpan data satu kali permainan: skor, jumlah monster dikalahkan, dan waktu. */
public final class GameManager {
    private static GameManager instance;

    private final EventBus eventBus = new EventBus();
    private long score;
    private int monstersKilled;
    private double elapsedSeconds;

    private GameManager() { }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /** Dipanggil setiap permainan baru dimulai. */
    public void reset() {
        eventBus.clear();
        score = 0;
        monstersKilled = 0;
        elapsedSeconds = 0;
    }

    public void tick(double dt) { elapsedSeconds += dt; }

    public void addScore(long points) {
        score += points;
        eventBus.publish(new GameEvent(EventType.SCORE_CHANGED, (int) score));
    }

    public void addKill() { monstersKilled++; }

    /** Menampilkan pesan singkat di HUD (dipakai banyak class). */
    public void showMessage(String text) {
        eventBus.publish(new GameEvent(EventType.MESSAGE, 0, text));
    }

    public EventBus getEventBus() { return eventBus; }
    public long getScore() { return score; }
    public int getMonstersKilled() { return monstersKilled; }
    public double getElapsedSeconds() { return elapsedSeconds; }
}
