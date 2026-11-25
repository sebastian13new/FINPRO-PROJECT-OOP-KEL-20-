package com.emberkeep.emberkeep.input;

import com.emberkeep.emberkeep.entity.Player;
import com.emberkeep.emberkeep.level.World;

public class DashCommand implements Command {
    @Override
    public void execute(Player player, World world, double dt) {
        player.dash();
    }
}
