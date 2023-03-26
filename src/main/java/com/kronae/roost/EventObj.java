package com.kronae.roost;

import com.kronae.roost.event.RoostEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EventObj {
    public final @NotNull ArrayList<Method> listeners;
    public @NotNull EventConstructorArgumentSetter setter;
    public EventObj(@NotNull ArrayList<Method> listener) {
        this.listeners = listener;
        setter = constructor -> {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }
    public EventObj(@NotNull ArrayList<Method> listener, @NotNull EventConstructorArgumentSetter setter) {
        this.listeners = listener;
        this.setter   = setter  ;
    }
}
