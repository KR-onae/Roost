package com.kronae.roost.event;

import com.kronae.roost.Window;
import org.jetbrains.annotations.NotNull;

public class KeyReleaseEvent extends RoostEvent {
    private final char key;
    private final int keyCode;
    private final boolean isActionKey;
    public KeyReleaseEvent(@NotNull Window window, char key, int keyCode, boolean isActionKey) {
        super(window);
        this.key = key;
        this.keyCode = keyCode;
        this.isActionKey = isActionKey;
    }
    public char getKey() {
        return key;
    }
    public int getKeyCode() {
        return keyCode;
    }
    public boolean isActionKey() {
        return isActionKey;
    }
}
