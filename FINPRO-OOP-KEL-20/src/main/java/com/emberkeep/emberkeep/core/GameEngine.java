package com.emberkeep.emberkeep.core;

import com.emberkeep.emberkeep.level.World;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameEngine {
    private final Stage stage;
    private final StackPane root;
    private final GraphicsContext gc;
    private final Keyboard keyboard = new Keyboard();
    private final World world = new World();
    private GameState state;
    private long lastNanos;

    public GameEngine(Stage stage) {
        this.stage = stage;
        Canvas canvas = new Canvas(GameConfig.WIDTH, GameConfig.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root = new StackPane(canvas);

        Scene scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT, Color.BLACK);
        scene.setOnKeyPressed(e -> keyboard.press(e.getCode()));
        scene.setOnKeyReleased(e -> keyboard.release(e.getCode()));

        stage.setTitle(GameConfig.TITLE);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    /** Game loop: dipanggil sekitar 60 kali per detik oleh AnimationTimer. */
    public void start() {
        changeState(new MenuState());
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanos == 0) {
                    lastNanos = now;
                    return;
                }
                double dt = Math.min((now - lastNanos) / 1_000_000_000.0, 0.05);
                lastNanos = now;

                state.update(GameEngine.this, dt);
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
                state.render(GameEngine.this, gc);
                keyboard.endFrame();
            }
        }.start();
        stage.show();
    }

    public void changeState(GameState next) {
        if (state != null) state.exit(this);
        state = next;
        state.enter(this);
    }

    /** Kembali ke state sebelumnya tanpa memanggil enter() lagi (dipakai saat lanjut dari pause). */
    public void resumeState(GameState previous) {
        state = previous;
    }

    public void startNewGame() {
        GameManager.getInstance().reset();
        changeState(new PlayingState());
    }

    public World getWorld() { return world; }
    public Keyboard getKeyboard() { return keyboard; }
    public StackPane getRoot() { return root; }
    public Stage getStage() { return stage; }
}
