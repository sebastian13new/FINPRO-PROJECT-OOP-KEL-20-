package com.emberkeep.emberkeep.level;

import com.emberkeep.emberkeep.entity.Chest;
import com.emberkeep.emberkeep.entity.Door;

public class GudangTuaLoader extends RoomLoader {
    @Override protected String getMapFile() { return "/maps/gudang_tua.tmx"; }
    @Override protected int getRoomIndex() { return 1; }
    @Override protected String getRoomName() { return "Gudang Tua"; }

    @Override
    protected void onObject(World world, String type, double px, double py) {
        switch (type) {
            case "ChestSword" -> world.spawn(new Chest(px, py, Chest.Content.SWORD));
            case "ChestKey" -> world.spawn(new Chest(px, py, Chest.Content.KEY));
            case "Door" -> world.spawn(new Door(px, py));
            default -> System.out.println("[GudangTua] Class objek tidak dikenal: " + type);
        }
    }
}
