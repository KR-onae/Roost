package com.kronae.roost.structure;

public class SquareStructure implements RoostStructure {
    private int lx;
    private int ly;
    private int sx;
    private int sy;
    public SquareStructure(int locationX, int locationY, int sizeX, int sizeY) {
        move(locationX, locationY);
        resize(sizeX, sizeY);
    }
    public void move(int locationX, int locationY) {
        lx = locationX;
        ly = locationY;
    }
    public void resize(int sizeX, int sizeY) {
        sx = sizeX;
        sy = sizeY;
    }
    // GETTER
    public int getLocationX() {
        return lx;
    }
    public int getLocationY() {
        return ly;
    }
    public int getSizeX() {
        return sx;
    }
    public int getSizeY() {
        return sy;
    }
}
