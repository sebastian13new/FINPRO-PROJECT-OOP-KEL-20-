package com.emberkeep.emberkeep.entity;

public class Inventory {
    private boolean sword;
    private int keys;

    public boolean hasSword() { return sword; }
    public void giveSword() { sword = true; }

    public int getKeys() { return keys; }
    public void addKey() { keys++; }

    /** Memakai satu kunci. Hasilnya false kalau tidak punya kunci. */
    public boolean useKey() {
        if (keys <= 0) return false;
        keys--;
        return true;
    }
}
