package com.kronae.roost.event;

import org.jetbrains.annotations.NotNull;

public record EventObj(@NotNull Class<? extends EventListener> clazz, @NotNull Object instance) {
}
