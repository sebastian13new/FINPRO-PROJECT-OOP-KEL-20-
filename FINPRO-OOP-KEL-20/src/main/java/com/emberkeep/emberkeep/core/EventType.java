package com.emberkeep.emberkeep.core;

/** Jenis-jenis kejadian yang bisa dikabarkan lewat EventBus. */
public enum EventType {
    HEARTS_CHANGED,
    POWER_CHANGED,
    KEYS_CHANGED,
    SWORD_OBTAINED,
    SCORE_CHANGED,
    ROOM_CHANGED,
    BOSS_HP_CHANGED,
    MONSTER_KILLED,
    PLAYER_HURT,
    PLAYER_FELL,
    MESSAGE,
    GAME_WON
}
