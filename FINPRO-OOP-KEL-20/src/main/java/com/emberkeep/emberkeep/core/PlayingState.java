package com.emberkeep.emberkeep.core;

import com.emberkeep.emberkeep.entity.Player;
import com.emberkeep.emberkeep.input.Command;
import com.emberkeep.emberkeep.input.InputHandler;
import com.emberkeep.emberkeep.level.GudangTuaLoader;
import com.emberkeep.emberkeep.level.JembatanRuntuhLoader;
import com.emberkeep.emberkeep.level.RoomLoader;
import com.emberkeep.emberkeep.level.SinggasanaLoader;
import com.emberkeep.emberkeep.level.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import java.util.List;

public class PlayingState implements GameState {
    private final List<RoomLoader> rooms = List.of(
            new GudangTuaLoader(), new JembatanRuntuhLoader(), new SinggasanaLoader());
    private int roomIndex;
    private Player player;
    private InputHandler input;

    @Override
    public void enter(GameEngine engine) {
        input = new InputHandler(engine.getKeyboard());
        player = new Player(0, 0);
        roomIndex = 0;
        rooms.get(roomIndex).load(engine.getWorld(), player);
        player.publishStatus();
    }

    @Override
    public void update(GameEngine engine, double dt) {
        if (engine.getKeyboard().isJustPressed(KeyCode.P)) {
            engine.changeState(new PausedState(this));
            return;
        }
        World world = engine.getWorld();
        for (Command command : input.handleInput()) {
            command.execute(player, world, dt);
        }
        GameManager.getInstance().tick(dt);
        world.update(dt);

        if (world.isNextRoomRequested() && roomIndex < rooms.size() - 1) {
            roomIndex++;
            rooms.get(roomIndex).load(world, player);
        }
        if (player.isDead()) {
            engine.changeState(new MenuState());   // diganti layar ending di Fase 7
        }
    }

    @Override
    public void render(GameEngine engine, GraphicsContext gc) {
        engine.getWorld().render(gc);
    }
}
