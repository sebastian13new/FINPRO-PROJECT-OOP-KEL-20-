package com.emberkeep.emberkeep.core;

import com.emberkeep.emberkeep.level.TileType;
import com.emberkeep.emberkeep.level.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

/** Versi awal: hanya ruangan kosong berdinding. Diganti di Fase 3, 4, dan 7. */
public class PlayingState implements GameState {

    @Override
    public void enter(GameEngine engine) {
        World world = engine.getWorld();
        world.clear();
        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                boolean edge = row == 0 || col == 0 || row == GameConfig.ROWS - 1 || col == GameConfig.COLS - 1;
                world.setTile(col, row, edge ? TileType.WALL : TileType.FLOOR);
            }
        }
    }

    @Override
    public void update(GameEngine engine, double dt) {
        if (engine.getKeyboard().isJustPressed(KeyCode.P)) {
            engine.changeState(new PausedState(this));
            return;
        }
        GameManager.getInstance().tick(dt);
        engine.getWorld().update(dt);
    }

    @Override
    public void render(GameEngine engine, GraphicsContext gc) {
        engine.getWorld().render(gc);
    }
}
