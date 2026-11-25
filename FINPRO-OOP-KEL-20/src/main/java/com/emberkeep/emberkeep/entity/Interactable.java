package com.emberkeep.emberkeep.entity;

import com.emberkeep.emberkeep.level.World;

/** Objek yang bisa diajak berinteraksi dengan tombol E (peti, pintu, obor, cermin). */
public interface Interactable {
    void interact(Player player, World world);

    default double getInteractRange() { return 44; }
}
