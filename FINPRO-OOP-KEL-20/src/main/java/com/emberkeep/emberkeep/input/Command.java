package com.emberkeep.emberkeep.input;

import com.emberkeep.emberkeep.entity.Player;
import com.emberkeep.emberkeep.level.World;

/** Satu perintah dari pemain. */
public interface Command {
    void execute(Player player, World world, double dt);
}
