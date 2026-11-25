package com.emberkeep.emberkeep.entity;

import com.emberkeep.emberkeep.core.EventType;
import com.emberkeep.emberkeep.core.GameEvent;
import com.emberkeep.emberkeep.core.GameManager;
import com.emberkeep.emberkeep.level.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Door extends Entity implements Interactable {

    public Door(double x, double y) {
        super(x, y, 32, 32);
    }

    @Override
    public double getInteractRange() { return 52; }   // pintu ada di dalam dinding

    @Override
    public void interact(Player player, World world) {
        GameManager game = GameManager.getInstance();
        if (player.getInventory().useKey()) {
            game.getEventBus().publish(new GameEvent(EventType.KEYS_CHANGED, player.getInventory().getKeys()));
            game.showMessage("Pintu terbuka!");
            world.requestNextRoom();
        } else {
            game.showMessage("Pintu terkunci. Cari kuncinya dulu!");
        }
    }

    @Override
    public void update(World world, double dt) { }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#5c3119"));
        gc.fillRect(x + 2, y + 2, width - 4, height - 4);
        gc.setFill(Color.web("#f2b544"));
        gc.fillOval(x + 6, y + 14, 5, 5);    // gagang
        gc.setFill(Color.BLACK);
        gc.fillRect(x + 7, y + 20, 3, 5);    // lubang kunci
    }
}
