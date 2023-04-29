package com.kronae.roost.structure;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageStructure implements RoostStructure {
    private final int lx;
    private final int ly;
    private final int sx;
    private final int sy;
    private final BufferedImage image;
    public ImageStructure(int locationX, int locationY, URL imageURL) throws IOException {
        this.lx = locationX;
        this.ly = locationY;
        this.sx = -1;
        this.sy = -1;
        this.image = ImageIO.read(imageURL);
    }
    public ImageStructure(int locationX, int locationY, ImageInputStream imageInputStream) throws IOException {
        this.lx = locationX;
        this.ly = locationY;
        this.sx = -1;
        this.sy = -1;
        this.image = ImageIO.read(imageInputStream);
    }
    public ImageStructure(int locationX, int locationY, BufferedImage image) throws IOException {
        this.lx = locationX;
        this.ly = locationY;
        this.sx = -1;
        this.sy = -1;
        this.image = image;
    }
    public ImageStructure(int locationX, int locationY, int sizeX, int sizeY, URL imageURL) throws IOException {
        this.lx = locationX;
        this.ly = locationY;
        this.sx = sizeX;
        this.sy = sizeY;
        this.image = ImageIO.read(imageURL);
    }
    public ImageStructure(int locationX, int locationY, int sizeX, int sizeY, ImageInputStream imageInputStream) throws IOException {
        this.lx = locationX;
        this.ly = locationY;
        this.sx = sizeX;
        this.sy = sizeY;
        this.image = ImageIO.read(imageInputStream);
    }
    public ImageStructure(int locationX, int locationY, int sizeX, int sizeY, BufferedImage image) {
        this.lx = locationX;
        this.ly = locationY;
        this.sx = sizeX;
        this.sy = sizeY;
        this.image = image;
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
    public BufferedImage getImage() {
        return image;
    }
}
