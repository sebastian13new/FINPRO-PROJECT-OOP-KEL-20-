package com.emberkeep.emberkeep.core;

import com.emberkeep.emberkeep.io.HighScoreEntry;
import com.emberkeep.emberkeep.io.HighScoreRepository;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class MenuState implements GameState {
    private List<HighScoreEntry> topScores = List.of();
    private double time;

    @Override
    public void enter(GameEngine engine) {
        topScores = new HighScoreRepository(HighScoreRepository.defaultLocation()).getTopScores(5);
    }

    @Override
    public void update(GameEngine engine, double dt) {
        time += dt;
        if (engine.getKeyboard().isJustPressed(KeyCode.ENTER)) {
            engine.startNewGame();
        }
    }

    @Override
    public void render(GameEngine engine, GraphicsContext gc) {
        double cx = GameConfig.WIDTH / 2.0;
        gc.setFill(Color.web("#1b1530"));
        gc.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.web("#f2b544"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 64));
        gc.fillText("EMBERKEEP", cx, 180);

        gc.setFill(Color.web("#f3eeff"));
        gc.setFont(Font.font("Monospaced", 20));
        gc.fillText("Kisah Putri Julia dan Kastel Terkutuk", cx, 225);

        if (((int) (time * 2)) % 2 == 0) {
            gc.setFill(Color.web("#e86a92"));
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 22));
            gc.fillText("Tekan ENTER untuk mulai", cx, 300);
        }

        gc.setFill(Color.web("#f2b544"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
        gc.fillText("HIGH SCORE", cx, 380);
        gc.setFill(Color.web("#f3eeff"));
        gc.setFont(Font.font("Monospaced", 18));
        if (topScores.isEmpty()) {
            gc.fillText("Belum ada skor", cx, 415);
        }
        for (int i = 0; i < topScores.size(); i++) {
            HighScoreEntry e = topScores.get(i);
            gc.fillText((i + 1) + ". " + e.getPlayerName() + "   " + e.getScore() + "   (" + e.getTimeSeconds() + " detik)",
                    cx, 415 + i * 28);
        }

        gc.setFill(Color.web("#b9aed6"));
        gc.setFont(Font.font("Monospaced", 14));
        gc.fillText("WASD gerak | J/Spasi serang | K dash | E interaksi | P pause", cx, GameConfig.HEIGHT - 30);
        gc.setTextAlign(TextAlignment.LEFT);
    }
}
