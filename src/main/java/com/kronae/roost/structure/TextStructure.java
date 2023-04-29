package com.kronae.roost.structure;

public class TextStructure implements RoostStructure {
    private final int x;
    private final int y;
    private final String content;
    public TextStructure(int x, int y, String content) {
        this.x = x;
        this.y = y;
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "TextStructure{" +
                "x=" + x +
                ", y=" + y +
                ", content='" + content + '\'' +
                '}';
    }
}
