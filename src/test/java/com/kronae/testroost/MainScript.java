package com.kronae.testroost;

import com.kronae.roost.custom.RoostScript;
import com.kronae.roost.Window2D;
import com.kronae.roost.status.CloseType;
import com.kronae.roost.status.WindowStatus;

import javax.swing.*;

public class MainScript implements RoostScript {
    private Window2D window;
    @Override
    public void open(Window2D window) {
        this.window = window;
        System.out.println("OPEN");
    }

    @Override
    public void update(WindowStatus status) {
//        System.out.println("UPDATE");
    }

    @Override
    public boolean closeQueue() {
        return window.showConfirmDialog("Close Window?", "Are you sure you want to close this window?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    @Override
    public void close(CloseType type) {
        System.out.println("CLOSE: " + type);
    }
}
