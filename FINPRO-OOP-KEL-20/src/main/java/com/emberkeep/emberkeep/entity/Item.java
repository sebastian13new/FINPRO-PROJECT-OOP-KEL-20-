package com.emberkeep.emberkeep.entity;

import com.emberkeep.emberkeep.core.EventType;
import com.emberkeep.emberkeep.core.GameEvent;
import com.emberkeep.emberkeep.core.GameManager;
import com.emberkeep.emberkeep.level.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Barang di lantai. Otomatis terambil saat disentuh Julia. */
public class Item extends Entity {
    private final ItemType type;
    private double time;

    public Item(double x, double y, ItemType type) {
        super(x + 8, y + 8, 16, 16);
        this.type = type;
    }

    @Override
    public void update(World world, double dt) {
        time += dt;
        Player player = world.getFirst(Player.class);
        if (player != null && intersects(player)) {
            applyTo(player);
            destroy();
        }
    }

    private void applyTo(Player player) {
        GameManager game = GameManager.getInstance();
        switch (type) {
            case POWER -> {
                player.addPower(1);
                game.showMessage("Power naik!");
            }
            case KEY -> {
                player.getInventory().addKey();
                game.getEventBus().publish(new GameEvent(EventType.KEYS_CHANGED, player.getInventory().getKeys()));
                game.showMessage("Kamu mendapat KUNCI!");
            }
            case HEART -> player.heal(1);
        }
        game.addScore(50);
    }

    @Override
    public void render(GraphicsContext gc) {
        double bob = Math.sin(time * 4) * 2;   // efek melayang naik turun
        double ix = x;
        double iy = y + bob;
        switch (type) {
            case POWER -> {
                gc.setFill(Color.web("#ff7a3c"));
                gc.fillRect(ix, iy, width, height);
                gc.setFill(Color.web("#ffe9a8"));
                gc.fillRect(ix + 5, iy + 4, 6, 8);
            }
            case KEY -> {
                gc.setFill(Color.web("#f2b544"));
                gc.fillOval(ix, iy, 9, 9);
                gc.fillRect(ix + 7, iy + 3, 9, 3);
                gc.fillRect(ix + 13, iy + 3, 3, 7);
            }
            case HEART -> {
                gc.setFill(Color.web("#e8455a"));
                gc.fillOval(ix, iy, 9, 9);
                gc.fillOval(ix + 7, iy, 9, 9);
                gc.fillPolygon(new double[]{ix, ix + 16, ix + 8}, new double[]{iy + 5, iy + 5, iy + 16}, 3);
            }
        }
    }

    public ItemType getType() { return type; }
}
