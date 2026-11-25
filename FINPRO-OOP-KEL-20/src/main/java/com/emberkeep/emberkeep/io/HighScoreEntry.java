package com.emberkeep.emberkeep.io;

/**
 * Satu data skor: nama pemain, skor, dan lama waktu bermain (detik).
 */
public class HighScoreEntry {
    private final String playerName;
    private final int score;
    private final int timeSeconds;

    public HighScoreEntry(String playerName, int score, int timeSeconds) {
        this.playerName = playerName;
        this.score = score;
        this.timeSeconds = timeSeconds;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getTimeSeconds() { return timeSeconds; }

    /** Ubah jadi satu baris CSV, contoh: "Julia,1500,95" */
    public String toCsvLine() {
        return playerName + "," + score + "," + timeSeconds;
    }

    /** Ubah satu baris CSV jadi objek. Melempar exception kalau formatnya salah. */
    public static HighScoreEntry fromCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Format baris salah: " + line);
        }
        String name = parts[0].trim();
        int score = Integer.parseInt(parts[1].trim());
        int time = Integer.parseInt(parts[2].trim());
        return new HighScoreEntry(name, score, time);
    }
}