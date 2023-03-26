package com.kronae.testroost;

import com.kronae.roost.EventListener;
import com.kronae.roost.Window2D;
import com.kronae.roost.event.EventHandler;
import com.kronae.roost.event.WindowDeactivateEvent;
import com.kronae.roost.exception.AlreadyOpenException;
import com.kronae.roost.structure.SquareStructure;

import java.lang.reflect.InvocationTargetException;

public class TestWindow extends Window2D {
    public TestWindow() {}
    public void run() throws AlreadyOpenException {
        setup(false);

        addScript(new MainScript());
        setSize(1600, 900);
        setTitle("Wow");
        setResizable(false);
        addSquare(new SquareStructure(50, 50, 50, 50));
//        addEventListener(new L1(this), constructor -> {
//            try {
//                return constructor.newInstance(TestWindow.this);
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        });
        addEventListener(new EventListener() {
            @EventHandler
            public void asdf(WindowDeactivateEvent event) {
                System.out.println(event);
            }

        });
//        addEventListener(new L2(this, 123), constructor -> {
//            try {
//                return constructor.newInstance(TestWindow.this, 123);
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        });
        open();
    }
}
