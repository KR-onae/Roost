package com.kronae.roost.event;

import com.kronae.roost.Window;
import org.jetbrains.annotations.NotNull;

public class WindowDeactivateEvent extends RoostEvent {
    public WindowDeactivateEvent(@NotNull Window window) {
        super(window);
    }
}
