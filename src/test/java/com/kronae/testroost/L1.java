package com.kronae.testroost;

import com.kronae.roost.EventListener;
import com.kronae.roost.Window2D;
import com.kronae.roost.event.EventHandler;
import com.kronae.roost.event.WindowActivateEvent;
import com.kronae.roost.event.WindowDeactivateEvent;

public class L1 implements EventListener {
    private final Window2D window;
    public L1(Window2D window) {
        this.window = window;
    }

    @EventHandler
    public void onActivate(WindowActivateEvent event) {
        System.out.println("L1: A");
    }
    @EventHandler
    public void onDeactivate(WindowDeactivateEvent event) {
        System.out.println("L1: D");
    }
}
