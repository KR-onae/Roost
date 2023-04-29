package com.kronae.roost.event;

import com.kronae.roost.Window;
import org.jetbrains.annotations.NotNull;

public class WindowActivateEvent extends RoostEvent {
    public WindowActivateEvent(@NotNull Window window) {
        super(window);
    }
}
