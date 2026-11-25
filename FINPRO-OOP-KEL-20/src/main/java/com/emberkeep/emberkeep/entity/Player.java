package com.emberkeep.emberkeep.entity;

import com.emberkeep.emberkeep.core.EventBus;
import com.emberkeep.emberkeep.core.EventType;
import com.emberkeep.emberkeep.core.GameEvent;
import com.emberkeep.emberkeep.core.GameManager;
import com.emberkeep.emberkeep.core.SoundManager;
import com.emberkeep.emberkeep.level.TileType;
import com.emberkeep.emberkeep.level.World;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends Entity {
    private static final double SPEED = 150;
    private static final double DASH_MULTIPLIER = 3.2;
    private static final double ATTACK_REACH = 28;

    private final Inventory inventory = new Inventory();
    private final int maxHearts = 5;
    private int hearts = maxHearts;
    private int power = 1;
    private boolean emberSword = false;

    private double facingX = 0;
    private double facingY = 1;
    private double attackCooldown;
    private double slashTimer;
    private double dashTimer;
    private double dashCooldown;
    private double invulnTimer;

    public Player(double x, double y) {
        super(x, y, 22, 26);
    }

    // ===== Aksi (dipanggil oleh Command) =====
    public void move(double dx, double dy, World world, double dt) {
        if (dx == 0 && dy == 0) return;
        double length = Math.hypot(dx, dy);
        dx /= length;
        dy /= length;
        facingX = dx;
        facingY = dy;

        double speed = SPEED * (dashTimer > 0 ? DASH_MULTIPLIER : 1);
        double nextX = x + dx * speed * dt;
        if (!world.collidesWithWall(new Rectangle2D(nextX, y, width, height))) x = nextX;
        double nextY = y + dy * speed * dt;
        if (!world.collidesWithWall(new Rectangle2D(x, nextY, width, height))) y = nextY;
    }

    public void attack(World world) {
        if (!inventory.hasSword()) {
            GameManager.getInstance().showMessage("Kamu belum punya pedang!");
            return;
        }
        if (attackCooldown > 0) return;
        attackCooldown = 0.35;
        slashTimer = 0.12;
        SoundManager.getInstance().playSfx("sfx_sword.wav");

        Rectangle2D box = getAttackBox();
        for (Entity e : world.getAll(Entity.class)) {
            if (e != this && e instanceof Damageable target && box.intersects(e.getBounds())) {
                target.takeHit(getDamage(), world);
            }
        }
    }

    public void dash() {
        if (dashCooldown > 0) return;
        dashTimer = 0.18;
        dashCooldown = 0.8;
        invulnTimer = Math.max(invulnTimer, 0.18);
    }

    public void interact(World world) {
        Interactable nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity e : world.getAll(Entity.class)) {
            if (e instanceof Interactable target) {
                double d = Math.hypot(e.getCenterX() - getCenterX(), e.getCenterY() - getCenterY());
                if (d <= target.getInteractRange() && d < best) {
                    best = d;
                    nearest = target;
                }
            }
        }
        if (nearest != null) nearest.interact(this, world);
    }

    // ===== Update tiap frame =====
    @Override
    public void update(World world, double dt) {
        attackCooldown = Math.max(0, attackCooldown - dt);
        slashTimer = Math.max(0, slashTimer - dt);
        dashTimer = Math.max(0, dashTimer - dt);
        dashCooldown = Math.max(0, dashCooldown - dt);
        invulnTimer = Math.max(0, invulnTimer - dt);

        // Saat dash, Julia melompati lubang
        if (dashTimer <= 0 && world.tileAtPixel(getCenterX(), getCenterY()) == TileType.PIT) {
            fall(world);
        }
    }

    private void fall(World world) {
        hearts = Math.max(0, hearts - 1);
        setPosition(world.getSpawnX(), world.getSpawnY());
        invulnTimer = 1.5;
        bus().publish(new GameEvent(EventType.PLAYER_FELL));
        bus().publish(new GameEvent(EventType.HEARTS_CHANGED, hearts));
    }

    public void takeDamage(int amount) {
        if (invulnTimer > 0 || isDead()) return;
        hearts = Math.max(0, hearts - amount);
        invulnTimer = 1.0;
        bus().publish(new GameEvent(EventType.PLAYER_HURT));
        bus().publish(new GameEvent(EventType.HEARTS_CHANGED, hearts));
    }

    public void heal(int amount) {
        hearts = Math.min(maxHearts, hearts + amount);
        bus().publish(new GameEvent(EventType.HEARTS_CHANGED, hearts));
    }

    public void addPower(int amount) {
        power = Math.min(5, power + amount);
        publishPower();
    }

    public void setEmberSword(boolean emberSword) {
        this.emberSword = emberSword;
        publishPower();
    }

    /** Mengabarkan semua status awal ke HUD (dipanggil saat permainan dimulai). */
    public void publishStatus() {
        bus().publish(new GameEvent(EventType.HEARTS_CHANGED, hearts));
        publishPower();
        bus().publish(new GameEvent(EventType.KEYS_CHANGED, inventory.getKeys()));
        if (inventory.hasSword()) bus().publish(new GameEvent(EventType.SWORD_OBTAINED));
    }

    private void publishPower() {
        bus().publish(new GameEvent(EventType.POWER_CHANGED, power, emberSword ? "BARA" : null));
    }

    private Rectangle2D getAttackBox() {
        if (Math.abs(facingX) >= Math.abs(facingY)) {
            double ax = facingX > 0 ? x + width : x - ATTACK_REACH;
            return new Rectangle2D(ax, y - 4, ATTACK_REACH, height + 8);
        }
        double ay = facingY > 0 ? y + height : y - ATTACK_REACH;
        return new Rectangle2D(x - 4, ay, width + 8, ATTACK_REACH);
    }

    private EventBus bus() { return GameManager.getInstance().getEventBus(); }

    public int getDamage() { return power * (emberSword ? 2 : 1); }
    public boolean isDead() { return hearts <= 0; }
    public int getHearts() { return hearts; }
    public int getMaxHearts() { return maxHearts; }
    public int getPower() { return power; }
    public boolean hasEmberSword() { return emberSword; }
    public Inventory getInventory() { return inventory; }

    @Override
    public void render(GraphicsContext gc) {
        // Berkedip saat kebal
        if (invulnTimer > 0 && ((int) (invulnTimer * 20)) % 2 == 0) return;

        gc.setFill(Color.web("#e86a92"));            // gaun
        gc.fillRect(x + 3, y + 11, width - 6, height - 11);
        gc.setFill(Color.web("#f6d2b8"));            // kepala
        gc.fillOval(x + 5, y + 1, 12, 12);
        gc.setFill(Color.web("#f2b544"));            // mahkota
        gc.fillRect(x + 5, y - 2, 12, 4);

        if (slashTimer > 0) {                          // efek tebasan pedang
            Rectangle2D b = getAttackBox();
            gc.setFill(emberSword ? Color.web("#ff9a3c", 0.8) : Color.web("#d9e3f0", 0.8));
            gc.fillRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
        }
    }
}
