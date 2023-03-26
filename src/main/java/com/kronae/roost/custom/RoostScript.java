package com.kronae.roost.custom;

import com.kronae.roost.Window2D;
import com.kronae.roost.status.CloseType;
import com.kronae.roost.status.WindowStatus;

public interface RoostScript {
    void open(Window2D window);
    void update(WindowStatus status);
    boolean closeQueue();
    void close(CloseType type);
}
