package com.emberkeep.emberkeep.core;

import javafx.scene.canvas.GraphicsContext;

/** Satu "keadaan" game: menu, sedang main, pause, atau ending. */
public interface GameState {
    void enter(GameEngine engine);
    void update(GameEngine engine, double dt);
    void render(GameEngine engine, GraphicsContext gc);

    default void exit(GameEngine engine) { }
}
