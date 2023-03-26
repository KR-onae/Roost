package com.kronae.testroost;

import com.kronae.roost.exception.AlreadyOpenException;

public class Main {
    public static TestWindow asdfTestWindow;
    public static void main(String[] args) throws AlreadyOpenException {
        asdfTestWindow = new TestWindow();
        asdfTestWindow.run();
    }
}
