package com.emberkeep.emberkeep.level;

public enum TileType {
    FLOOR(false),
    WALL(true),
    PIT(false),
    CRACKED(false);

    private final boolean solid;

    TileType(boolean solid) { this.solid = solid; }

    public boolean isSolid() { return solid; }
}
