package com.emberkeep.emberkeep.level;

import com.emberkeep.emberkeep.core.EventType;
import com.emberkeep.emberkeep.core.GameConfig;
import com.emberkeep.emberkeep.core.GameEvent;
import com.emberkeep.emberkeep.core.GameManager;
import com.emberkeep.emberkeep.entity.Player;

public abstract class RoomLoader {

    /** Template method: urutan memuat ruangan selalu sama untuk semua ruangan. */
    public final void load(World world, Player player) {
        world.clear();
        world.setRoomIndex(getRoomIndex());
        world.setRoomName(getRoomName());

        // 1. Baca file Tiled, salin jenis petak ke World
        TmxMap map = TmxMap.load(getMapFile());
        world.setVisualMap(map);
        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                world.setTile(col, row, map.getTileType(col, row));
            }
        }

        // 2. Buat objek dari layer Objects
        for (TmxMap.MapObject object : map.getObjects()) {
            double px = snap(object.x());
            double py = snap(object.y());
            if ("Player".equals(object.type())) {
                double sx = px + (GameConfig.TILE - player.getWidth()) / 2;
                double sy = py + (GameConfig.TILE - player.getHeight()) / 2;
                world.setSpawn(sx, sy);
                player.setPosition(sx, sy);
                world.spawn(player);
            } else {
                onObject(world, object.type(), px, py);
            }
        }

        // 3. Selesaikan, lalu kabarkan ruangan baru
        world.flushPending();
        GameManager.getInstance().getEventBus()
                .publish(new GameEvent(EventType.ROOM_CHANGED, getRoomIndex(), getRoomName()));
        onRoomLoaded(world);
    }

    /** Membulatkan posisi objek Tiled ke pojok petak terdekat. */
    private static double snap(double value) {
        return Math.round(value / GameConfig.TILE) * (double) GameConfig.TILE;
    }

    // ===== Langkah yang WAJIB diisi tiap ruangan =====
    protected abstract String getMapFile();
    protected abstract int getRoomIndex();
    protected abstract String getRoomName();

    // ===== Langkah yang BOLEH diubah (hook) =====
    protected void onObject(World world, String type, double px, double py) { }

    protected void onRoomLoaded(World world) { }
}
