package com.kronae.testroost;

import com.kronae.roost.EventListener;
import com.kronae.roost.Window2D;
import com.kronae.roost.event.EventHandler;
import com.kronae.roost.event.WindowActivateEvent;
import com.kronae.roost.event.WindowDeactivateEvent;

public class L2 implements EventListener {
    private final Window2D window;
    public L2(Window2D window, int x) {
        this.window = window;
    }

    @EventHandler
    public void onActivate(WindowActivateEvent event) {
        System.out.println("L2: A");
    }
    @EventHandler
    public void onDeactivate(WindowDeactivateEvent event) {
        System.out.println("L2: D");
    }
}
