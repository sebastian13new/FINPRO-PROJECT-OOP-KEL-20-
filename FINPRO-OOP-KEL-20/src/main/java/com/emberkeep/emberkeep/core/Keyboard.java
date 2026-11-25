package com.emberkeep.emberkeep.core;

import javafx.scene.input.KeyCode;

import java.util.EnumSet;
import java.util.Set;

/** Mencatat tombol yang sedang ditekan dan yang baru saja ditekan di frame ini. */
public class Keyboard {
    private final Set<KeyCode> down = EnumSet.noneOf(KeyCode.class);
    private final Set<KeyCode> justPressed = EnumSet.noneOf(KeyCode.class);

    public void press(KeyCode code) {
        if (down.add(code)) justPressed.add(code);
    }

    public void release(KeyCode code) { down.remove(code); }
    public boolean isDown(KeyCode code) { return down.contains(code); }
    public boolean isJustPressed(KeyCode code) { return justPressed.contains(code); }
    public void endFrame() { justPressed.clear(); }
}
