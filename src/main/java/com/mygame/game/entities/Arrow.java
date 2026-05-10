package com.mygame.game.entities;

import javafx.scene.image.Image;
import com.mygame.game.Level;
import javafx.scene.image.ImageView;
import javafx.geometry.Bounds;

public class Arrow {
        private ImageView sprite;
        private double x, y;
        private double velocityX, velocityY;
        private double width = 18;
        private int damage = 15;
        private boolean active = true;

        public Arrow(double startX, double startY, double directionX, double directionY) {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/weapon/bow/arrow/arrow.png"));
            sprite = new ImageView(arrowImage);
            sprite.setFitWidth(width);
            sprite.setPreserveRatio(true);

            this.x = startX;
            this.y = startY;
            this.velocityX = directionX * 500; // скорость полёта
            this.velocityY = directionY * 500;

            sprite.setX(x);
            sprite.setY(y);

            double angle = Math.atan2(directionY, directionX);
            sprite.setRotate(Math.toDegrees(angle));
        }

    public void update(double deltaTime) {
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
        sprite.setX(x);
        sprite.setY(y);

        if (x < -50 || x > Level.WORLD_WIDTH + 50 || y < -50 || y > Level.WORLD_HEIGHT + 50) {
            active = false;
        }
    }

        public Bounds getBounds() {
            return sprite.getBoundsInParent();
        }

        public ImageView getSprite() { return sprite; }
        public boolean isActive() { return active; }
        public int getDamage() { return damage; }
    }

