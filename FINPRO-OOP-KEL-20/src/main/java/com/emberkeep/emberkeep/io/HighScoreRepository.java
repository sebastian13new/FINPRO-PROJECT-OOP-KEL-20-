package com.emberkeep.emberkeep.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreRepository {
    private final Path filePath;

    public HighScoreRepository(Path filePath) {
        this.filePath = filePath;
    }

    /** Lokasi file skor di game asli: C:\\Users\\<nama>\\.emberkeep\\highscores.csv */
    public static Path defaultLocation() {
        return Path.of(System.getProperty("user.home"), ".emberkeep", "highscores.csv");
    }

    public List<HighScoreEntry> loadAll() {
        List<HighScoreEntry> result = new ArrayList<>();
        if (!Files.exists(filePath)) return result;
        try {
            for (String line : Files.readAllLines(filePath)) {
                if (line.isBlank()) continue;
                try {
                    result.add(HighScoreEntry.fromCsvLine(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("[HighScore] Baris rusak dilewati: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("[HighScore] Gagal membaca file: " + e.getMessage());
        }
        return result;
    }

    public void save(HighScoreEntry entry) {
        List<HighScoreEntry> all = loadAll();
        all.add(entry);
        List<String> lines = new ArrayList<>();
        for (HighScoreEntry e : all) lines.add(e.toCsvLine());
        try {
            Path parent = filePath.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("[HighScore] Gagal menyimpan file: " + e.getMessage());
        }
    }

    public List<HighScoreEntry> getTopScores(int limit) {
        List<HighScoreEntry> all = loadAll();
        all.sort(Comparator.comparingInt(HighScoreEntry::getScore).reversed()
                .thenComparingInt(HighScoreEntry::getTimeSeconds));
        return all.subList(0, Math.min(limit, all.size()));
    }
}
