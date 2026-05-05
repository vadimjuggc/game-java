package com.mygame.game.entities;

import javafx.scene.image.Image;
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

            // Поворачиваем стрелу в направлении полёта
            double angle = Math.atan2(directionY, directionX);
            sprite.setRotate(Math.toDegrees(angle));
        }

        public void update(double deltaTime) {
            x += velocityX * deltaTime;
            y += velocityY * deltaTime;
            sprite.setX(x);
            sprite.setY(y);

            // Удаляем, если вылетела за пределы экрана
            if (x < -50 || x > 850 || y < -50 || y > 650) {
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

