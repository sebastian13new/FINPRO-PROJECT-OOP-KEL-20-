package com.emberkeep.emberkeep.input;

import com.emberkeep.emberkeep.entity.Player;
import com.emberkeep.emberkeep.level.World;

public class AttackCommand implements Command {
    @Override
    public void execute(Player player, World world, double dt) {
        player.attack(world);
    }
}
