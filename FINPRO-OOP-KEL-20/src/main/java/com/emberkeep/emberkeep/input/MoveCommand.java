package com.emberkeep.emberkeep.input;

import com.emberkeep.emberkeep.entity.Player;
import com.emberkeep.emberkeep.level.World;

public class MoveCommand implements Command {
    private double dx;
    private double dy;

    public void set(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute(Player player, World world, double dt) {
        player.move(dx, dy, world, dt);
    }
}
