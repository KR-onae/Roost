package com.kronae.testroost;

import com.kronae.roost.Window2D;
import com.kronae.roost.structure.TextStructure;

import java.awt.*;

public class TestWindow extends Window2D {
    public TestWindow() {
        super(true);
        setTitle("Game2D");
        setSize(1600, 900);
        setResizable(true);
        setBackgroundColor(Color.GREEN);
//        addStructure(new TextStructure(5, 5, "HI GUYS!"));
        open();
    }
    public static TestWindow window;
    public static void main(String[] args) {
        window = new TestWindow();
    }
}
