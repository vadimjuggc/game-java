package com.mygame.game.entities;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class Platform {
    private Rectangle rectangle;
    private double x, y, width, height;

    public Platform(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.rectangle = new Rectangle(x, y, width, height);
        this.rectangle.setFill(Color.GRAY);
        this.rectangle.setStroke(Color.DARKGRAY); // обводка
        this.rectangle.setStrokeWidth(2);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public boolean isPlayerAbove(Player player)
    {
        boolean above = player.getY() + player.height <= 5 &&  player.getY() + player.height >= y - 5;
        boolean withinX = player.getX() + player.width > x && player.getX() < x + width;
        return above && withinX;
    }
}
