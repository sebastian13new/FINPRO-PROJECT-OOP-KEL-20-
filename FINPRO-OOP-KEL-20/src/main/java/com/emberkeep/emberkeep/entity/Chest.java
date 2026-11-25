package com.emberkeep.emberkeep.entity;

import com.emberkeep.emberkeep.core.EventType;
import com.emberkeep.emberkeep.core.GameEvent;
import com.emberkeep.emberkeep.core.GameManager;
import com.emberkeep.emberkeep.level.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Chest extends Entity implements Interactable {
    public enum Content { SWORD, KEY }

    private final Content content;
    private boolean opened;

    public Chest(double x, double y, Content content) {
        super(x + 4, y + 8, 24, 20);
        this.content = content;
    }

    @Override
    public void interact(Player player, World world) {
        if (opened) return;
        opened = true;
        GameManager game = GameManager.getInstance();
        if (content == Content.SWORD) {
            player.getInventory().giveSword();
            game.getEventBus().publish(new GameEvent(EventType.SWORD_OBTAINED));
            game.showMessage("Kamu menemukan PEDANG! Tekan J untuk menyerang.");
        } else {
            world.spawn(new Item(x - 4, y + 16, ItemType.KEY));
            game.showMessage("Sebuah kunci terjatuh dari peti!");
        }
    }

    @Override
    public void update(World world, double dt) { }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#8a4b2a"));
        gc.fillRect(x, y + 6, width, height - 6);
        if (opened) {
            gc.setFill(Color.web("#5c3119"));
            gc.fillRect(x, y - 4, width, 8);          // tutup terbuka
        } else {
            gc.setFill(Color.web("#a8613a"));
            gc.fillRect(x, y, width, 8);              // tutup tertutup
            gc.setFill(Color.web("#f2b544"));
            gc.fillRect(x + width / 2 - 2, y + 4, 4, 6); // gembok
        }
    }
}
