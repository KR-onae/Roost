package com.kronae.roost.event;

import com.kronae.roost.Window;
import org.jetbrains.annotations.NotNull;

public class RoostEvent {
    private final @NotNull Window window;
    public RoostEvent(@NotNull Window window) {
        this.window = window;
    }
    public final @NotNull Window getWindow() {
        return window;
    }
}
