package com.mygame.game.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Item {

    public enum ItemType { HEALTH }

    private ItemType type;
    private double x, y;
    private ImageView sprite;
    private boolean collected = false;

    private static final double SIZE = 20;

    public Item(ItemType type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;

        Image img = loadImage();
        sprite = new ImageView(img);
        sprite.setFitWidth(SIZE);
        sprite.setFitHeight(SIZE);
        sprite.setPreserveRatio(false);
        sprite.setX(x);
        sprite.setY(y);
    }

    private Image loadImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/items/health_potion.png"));
        } catch (Exception e) {
            return null;
        }
    }

    public void update(double deltaTime) {
    }

    public boolean checkPickup(Player player) {
        if (collected) return false;
        double px = player.getX(), py = player.getY();
        double pw = player.getWidth(), ph = player.getHeight();
        boolean hit = px < x + SIZE && px + pw > x && py < y + SIZE && py + ph > y;
        if (hit) {
            collected = true;
            sprite.setVisible(false);
        }
        return hit;
    }

    public void applyEffect(Player player) {
        if (type == ItemType.HEALTH) {
            player.heal(20);
        }
    }

    public boolean isCollected() { return collected; }
    public ImageView getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
}
