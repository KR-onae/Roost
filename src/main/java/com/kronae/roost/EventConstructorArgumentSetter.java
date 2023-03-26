package com.kronae.roost;

import java.lang.reflect.Constructor;

public interface EventConstructorArgumentSetter {
    Object run(Constructor<?> constructor);
}
