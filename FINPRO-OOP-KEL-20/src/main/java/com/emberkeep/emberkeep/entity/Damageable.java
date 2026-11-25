package com.emberkeep.emberkeep.entity;

import com.emberkeep.emberkeep.level.World;

/** Objek yang bisa dipukul pedang Julia (monster, boss). */
public interface Damageable {
    void takeHit(int damage, World world);
}
