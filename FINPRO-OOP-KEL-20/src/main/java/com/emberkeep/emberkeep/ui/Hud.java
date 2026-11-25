package com.emberkeep.emberkeep.ui;

import com.emberkeep.emberkeep.core.EventBus;
import com.emberkeep.emberkeep.core.EventType;
import com.emberkeep.emberkeep.core.GameConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/** Tampilan status di atas layar. Datanya diperbarui lewat EventBus, bukan dicek tiap frame. */
public class Hud {
    private int hearts;
    private int maxHearts = 5;
    private int power = 1;
    private boolean ember;
    private int keys;
    private boolean sword;
    private long score;
    private int roomIndex;
    private String roomName = "";
    private int bossPercent = -1;
    private String message;
    private double messageTimer;

    public Hud(EventBus bus) {
        bus.subscribe(EventType.HEARTS_CHANGED, e -> hearts = e.getValue());
        bus.subscribe(EventType.POWER_CHANGED, e -> {
            power = e.getValue();
            ember = "BARA".equals(e.getText());
        });
        bus.subscribe(EventType.KEYS_CHANGED, e -> keys = e.getValue());
        bus.subscribe(EventType.SWORD_OBTAINED, e -> sword = true);
        bus.subscribe(EventType.SCORE_CHANGED, e -> score = e.getValue());
        bus.subscribe(EventType.ROOM_CHANGED, e -> {
            roomIndex = e.getValue();
            roomName = e.getText();
            bossPercent = -1;
        });
        bus.subscribe(EventType.BOSS_HP_CHANGED, e -> bossPercent = e.getValue());
        bus.subscribe(EventType.MESSAGE, e -> {
            message = e.getText();
            messageTimer = 3;
        });
    }

    public void update(double dt) {
        if (messageTimer > 0) messageTimer -= dt;
    }

    public void render(GraphicsContext gc, double elapsedSeconds) {
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, 0, GameConfig.WIDTH, 32);

        for (int i = 0; i < maxHearts; i++) {
            gc.setFill(i < hearts ? Color.web("#e8455a") : Color.web("#4a3d58"));
            gc.fillOval(10 + i * 22, 8, 16, 16);
        }

        int seconds = (int) elapsedSeconds;
        gc.setFill(Color.web("#f3eeff"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        gc.fillText("Power " + power + (ember ? " (BARA x2)" : ""), 130, 21);
        gc.fillText("Kunci " + keys, 320, 21);
        gc.fillText(sword ? "Pedang: ADA" : "Pedang: -", 400, 21);
        gc.fillText(String.format("%02d:%02d", seconds / 60, seconds % 60), 540, 21);
        gc.fillText("Skor " + score, 620, 21);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(roomName, GameConfig.WIDTH - 10, 21);
        gc.setTextAlign(TextAlignment.LEFT);

        if (roomIndex == 3 && bossPercent >= 0) {
            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRect(230, 40, 500, 18);
            gc.setFill(Color.web("#9b59b6"));
            gc.fillRect(232, 42, 496 * bossPercent / 100.0, 14);
            gc.setFill(Color.web("#f3eeff"));
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            gc.fillText("Penjaga Bayangan", 236, 54);
        }

        if (messageTimer > 0 && message != null) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRoundRect(GameConfig.WIDTH / 2.0 - 320, GameConfig.HEIGHT - 70, 640, 40, 12, 12);
            gc.setFill(Color.web("#f2b544"));
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(message, GameConfig.WIDTH / 2.0, GameConfig.HEIGHT - 44);
            gc.setTextAlign(TextAlignment.LEFT);
        }
    }
}
