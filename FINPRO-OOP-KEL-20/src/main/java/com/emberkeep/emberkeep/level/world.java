package com.emberkeep.emberkeep.level;

import com.emberkeep.emberkeep.core.GameConfig;
import com.emberkeep.emberkeep.entity.Entity;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Isi satu ruangan: petak-petak peta dan semua objek di dalamnya. */
public class World {
    private static final int TILE = GameConfig.TILE;
    private static final Color FLOOR = Color.web("#2b2238");
    private static final Color FLOOR_LINE = Color.web("#33294a");
    private static final Color WALL = Color.web("#5a4a6e");
    private static final Color WALL_EDGE = Color.web("#7a6894");
    private static final Color PIT_EDGE = Color.web("#14101c");
    private static final Color CRACK = Color.web("#8a7aa8");

    private final TileType[][] tiles = new TileType[GameConfig.ROWS][GameConfig.COLS];
    private final List<Entity> entities = new ArrayList<>();
    private final List<Entity> pending = new ArrayList<>();
    private final Map<Class<?>, Object> services = new HashMap<>();
    private TmxMap visualMap;   // gambar petak dari Tiled (null = pakai warna polos)
    private int roomIndex;
    private String roomName = "";
    private double spawnX;
    private double spawnY;
    private boolean nextRoomRequested;

    public World() {
        clear();
    }

    public void clear() {
        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                tiles[row][col] = TileType.FLOOR;
            }
        }
        entities.clear();
        pending.clear();
        services.clear();
        visualMap = null;
        nextRoomRequested = false;
    }

    // ===== Petak =====
    public TileType getTile(int col, int row) {
        if (col < 0 || row < 0 || col >= GameConfig.COLS || row >= GameConfig.ROWS) return TileType.WALL;
        return tiles[row][col];
    }

    public void setTile(int col, int row, TileType type) {
        if (col < 0 || row < 0 || col >= GameConfig.COLS || row >= GameConfig.ROWS) return;
        tiles[row][col] = type;
    }

    public TileType tileAtPixel(double px, double py) {
        return getTile((int) Math.floor(px / TILE), (int) Math.floor(py / TILE));
    }

    public boolean collidesWithWall(Rectangle2D box) { return anyCorner(box, TileType.WALL); }
    public boolean touchesPit(Rectangle2D box) { return anyCorner(box, TileType.PIT); }

    private boolean anyCorner(Rectangle2D b, TileType type) {
        double right = b.getMaxX() - 0.01;
        double bottom = b.getMaxY() - 0.01;
        return tileAtPixel(b.getMinX(), b.getMinY()) == type
                || tileAtPixel(right, b.getMinY()) == type
                || tileAtPixel(b.getMinX(), bottom) == type
                || tileAtPixel(right, bottom) == type;
    }

    public void setVisualMap(TmxMap map) { this.visualMap = map; }

    // ===== Objek =====
    /** Objek baru masuk antrean dulu, lalu ditambahkan di flushPending(). */
    public void spawn(Entity entity) { pending.add(entity); }

    public void flushPending() {
        entities.addAll(pending);
        pending.clear();
    }

    public <E extends Entity> List<E> getAll(Class<E> type) {
        List<E> result = new ArrayList<>();
        for (Entity e : entities) {
            if (e.isAlive() && type.isInstance(e)) result.add(type.cast(e));
        }
        return result;
    }

    public <E extends Entity> E getFirst(Class<E> type) {
        for (Entity e : entities) {
            if (e.isAlive() && type.isInstance(e)) return type.cast(e);
        }
        return null;
    }

    public void update(double dt) {
        flushPending();
        for (Entity e : new ArrayList<>(entities)) {
            if (e.isAlive()) e.update(this, dt);
        }
        entities.removeIf(e -> !e.isAlive());
        flushPending();
    }

    public void render(GraphicsContext gc) {
        renderTiles(gc);
        for (Entity e : entities) {
            if (e.isAlive()) e.render(gc);
        }
    }

    private void renderTiles(GraphicsContext gc) {
        Image tileset = visualMap != null ? visualMap.getTilesetImage() : null;
        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                double px = col * TILE;
                double py = row * TILE;
                TileType type = tiles[row][col];

                // Gambar dari tileset Tiled, selama petaknya belum berubah (misalnya lantai retak yang runtuh)
                if (tileset != null) {
                    int gid = visualMap.getGid(col, row);
                    if (gid > 0 && type == visualMap.getTileType(col, row)) {
                        gc.drawImage(tileset, visualMap.getSourceX(gid), visualMap.getSourceY(gid), TILE, TILE,
                                px, py, TILE, TILE);
                        continue;
                    }
                }

                switch (type) {
                    case WALL -> {
                        gc.setFill(WALL);
                        gc.fillRect(px, py, TILE, TILE);
                        gc.setStroke(WALL_EDGE);
                        gc.strokeRect(px + 0.5, py + 0.5, TILE - 1, TILE - 1);
                    }
                    case PIT -> {
                        gc.setFill(Color.BLACK);
                        gc.fillRect(px, py, TILE, TILE);
                        gc.setFill(PIT_EDGE);
                        gc.fillRect(px, py, TILE, 4);
                    }
                    case CRACKED -> {
                        gc.setFill(FLOOR);
                        gc.fillRect(px, py, TILE, TILE);
                        gc.setStroke(CRACK);
                        gc.strokeLine(px + 6, py + 8, px + 16, py + 18);
                        gc.strokeLine(px + 16, py + 18, px + 26, py + 12);
                        gc.strokeLine(px + 16, py + 18, px + 14, py + 28);
                    }
                    default -> {
                        gc.setFill(FLOOR);
                        gc.fillRect(px, py, TILE, TILE);
                        gc.setStroke(FLOOR_LINE);
                        gc.strokeRect(px + 0.5, py + 0.5, TILE - 1, TILE - 1);
                    }
                }
            }
        }
    }

    // ===== Layanan tambahan per ruangan (misalnya pool peluru) =====
    public <S> void putService(Class<S> type, S service) { services.put(type, service); }
    public <S> S getService(Class<S> type) { return type.cast(services.get(type)); }

    // ===== Info ruangan =====
    public int getRoomIndex() { return roomIndex; }
    public void setRoomIndex(int roomIndex) { this.roomIndex = roomIndex; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public void setSpawn(double x, double y) {
        spawnX = x;
        spawnY = y;
    }

    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }

    public void requestNextRoom() { nextRoomRequested = true; }
    public boolean isNextRoomRequested() { return nextRoomRequested; }
}
