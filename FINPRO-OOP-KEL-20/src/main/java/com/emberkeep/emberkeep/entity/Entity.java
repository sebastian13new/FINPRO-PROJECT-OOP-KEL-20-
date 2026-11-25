package com.emberkeep.emberkeep.entity;

import com.emberkeep.emberkeep.level.World;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/** Induk semua objek di game: Player, monster, item, peti, boss, dan lainnya. */
public abstract class Entity {
    protected double x;
    protected double y;
    protected final double width;
    protected final double height;
    private boolean alive = true;

    protected Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update(World world, double dt);
    public abstract void render(GraphicsContext gc);

    public Rectangle2D getBounds() { return new Rectangle2D(x, y, width, height); }
    public boolean intersects(Entity other) { return getBounds().intersects(other.getBounds()); }

    public double getCenterX() { return x + width / 2; }
    public double getCenterY() { return y + height / 2; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAlive() { return alive; }
    public void destroy() { alive = false; }
}
