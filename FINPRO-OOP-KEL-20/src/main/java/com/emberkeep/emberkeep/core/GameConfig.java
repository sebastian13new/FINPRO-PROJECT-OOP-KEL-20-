package com.emberkeep.emberkeep.core;

/** Angka-angka tetap yang dipakai di seluruh game. */
public final class GameConfig {
    public static final int TILE = 32;
    public static final int COLS = 30;
    public static final int ROWS = 20;
    public static final int WIDTH = TILE * COLS;   // 960 piksel
    public static final int HEIGHT = TILE * ROWS;  // 640 piksel
    public static final String TITLE = "Emberkeep";

    private GameConfig() { }
}
