package com.mygame.game.entities;

import javafx.scene.shape.Rectangle;

public abstract class Entity {
    protected double x,y;
    protected double width, height;
    protected Rectangle rectangle;

    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.rectangle = new Rectangle(x,y,width,height);
    }

    public abstract void update(double deltaTime);

    public Rectangle getRectangle() {
        return rectangle;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
        rectangle.setX(this.x);
        rectangle.setY(this.y);
    }

    public boolean collidesWith(Entity other) {
        return rectangle.getBoundsInParent().intersects(
                other.rectangle.getBoundsInParent()
        );
    }

}
