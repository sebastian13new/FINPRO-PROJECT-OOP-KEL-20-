package com.emberkeep.emberkeep.input;

import com.emberkeep.emberkeep.core.Keyboard;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

/** Menerjemahkan tombol keyboard menjadi daftar Command. */
public class InputHandler {
    private final Keyboard keyboard;
    private final MoveCommand move = new MoveCommand();
    private final Command attack = new AttackCommand();
    private final Command dash = new DashCommand();
    private final Command interact = new InteractCommand();
    private final List<Command> commands = new ArrayList<>();

    public InputHandler(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public List<Command> handleInput() {
        commands.clear();

        double dx = 0;
        double dy = 0;
        if (keyboard.isDown(KeyCode.W) || keyboard.isDown(KeyCode.UP)) dy -= 1;
        if (keyboard.isDown(KeyCode.S) || keyboard.isDown(KeyCode.DOWN)) dy += 1;
        if (keyboard.isDown(KeyCode.A) || keyboard.isDown(KeyCode.LEFT)) dx -= 1;
        if (keyboard.isDown(KeyCode.D) || keyboard.isDown(KeyCode.RIGHT)) dx += 1;
        move.set(dx, dy);
        commands.add(move);

        if (keyboard.isJustPressed(KeyCode.J) || keyboard.isJustPressed(KeyCode.SPACE)) commands.add(attack);
        if (keyboard.isJustPressed(KeyCode.K)) commands.add(dash);
        if (keyboard.isJustPressed(KeyCode.E)) commands.add(interact);
        return commands;
    }
}
