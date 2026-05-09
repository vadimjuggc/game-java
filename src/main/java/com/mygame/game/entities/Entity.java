package com.mygame.game.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Bounds;

public abstract class Entity {

    protected ImageView sprite;
    protected double x, y;
    protected double width, height;

    public Entity(Image image, double x, double y, double width, double height) {
        this.sprite = new ImageView(image);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        sprite.setFitWidth(width);
        sprite.setFitHeight(height);
        sprite.setPreserveRatio(false);
        sprite.setX(x);
        sprite.setY(y);
    }

    public abstract void update(double deltaTime);

    public ImageView getSprite() { return sprite; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        sprite.setX(x);
        sprite.setY(y);
    }

    public Bounds getBounds() {
        return sprite.getBoundsInParent();
    }

    public void landOnPlatform(double platformY) {
        y = platformY - height;
        setPosition(x, y);
    }

    public void stopVerticalMovement() {
    }
}