package com.emberkeep.emberkeep.core;

/**
 * Daftar BGM. Ruang 1 & 2 memakai DUNGEON, Ruang 3 memakai BOSS.
 * MENANG dan KALAH diputar sekali di layar ending.
 */
public enum BgmTrack {
    DUNGEON("bgm_dungeon.mp3", true),
    BOSS("bgm_boss.mp3", true),
    MENANG("bgm_menang.mp3", false),
    KALAH("bgm_kalah.wav", false);

    private final String fileName;
    private final boolean loop;

    BgmTrack(String fileName, boolean loop) {
        this.fileName = fileName;
        this.loop = loop;
    }

    public String getFileName() { return fileName; }
    public boolean isLoop() { return loop; }
}
