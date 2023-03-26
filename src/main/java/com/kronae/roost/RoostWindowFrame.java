package com.kronae.roost;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class RoostWindowFrame extends JFrame {
//    private ArrayList<Consumer<Graphics>> consumers;
//    protected RoostWindowFrame() {
//        consumers = new ArrayList<>();
//    }
//    public void paint(Graphics g){
//        super.paint(g);
//        consumers.forEach(consumer -> {
//            consumer.accept(g);
//        });
//    }
//    public void onPaint(Consumer<Graphics> consumer) {
//        consumers.add(consumer);
//    }
    private boolean clear = false;
    private Graphics graphis;
    public void clearWindow() {
        clear = true;
    }
    public void paint(Graphics g){
        if(clear) super.paint(g);
        clear = false;
        graphis = g;
    }
    public Graphics getGraphic() {
        return graphis;
    }
}
