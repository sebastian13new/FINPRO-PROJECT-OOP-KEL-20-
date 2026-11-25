package com.emberkeep.emberkeep.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class PausedState implements GameState {
    private final GameState previous;

    public PausedState(GameState previous) {
        this.previous = previous;
    }

    @Override
    public void enter(GameEngine engine) { }

    @Override
    public void update(GameEngine engine, double dt) {
        if (engine.getKeyboard().isJustPressed(KeyCode.P)) {
            engine.resumeState(previous);
        }
    }

    @Override
    public void render(GameEngine engine, GraphicsContext gc) {
        previous.render(engine, gc);
        gc.setFill(Color.rgb(0, 0, 0, 0.55));
        gc.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.web("#f2b544"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 48));
        gc.fillText("PAUSE", GameConfig.WIDTH / 2.0, GameConfig.HEIGHT / 2.0);
        gc.setFill(Color.web("#f3eeff"));
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("Tekan P untuk lanjut", GameConfig.WIDTH / 2.0, GameConfig.HEIGHT / 2.0 + 40);
        gc.setTextAlign(TextAlignment.LEFT);
    }
}
