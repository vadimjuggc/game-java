package com.mygame.game.entities;

import javafx.scene.image.Image;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Enemy extends Entity {

    private static final double SPEED = 80;
    private static final double GRAVITY = 800;
    private static final double WIDTH = 28;
    private static final double HEIGHT = 35;

    private Player target;
    private double velocityY = 0;
    private boolean onGround = false;

    private int health;
    private int damage = 15;

    public Enemy(double startX, double startY, Player player) {
        super(loadEnemyImage(), startX, startY, WIDTH, HEIGHT);
        this.target = player;
        this.health = 40;

        if (sprite.getImage() == null) {
            sprite.setStyle("-fx-background-color: red; -fx-shape: 'M0,0 L28,0 L28,35 L0,35 Z';");
        }
    }

    private static Image loadEnemyImage() {
        return loadImage("/images/enemy.png", WIDTH, HEIGHT);
    }

    @Override
    public void update(double deltaTime) {
        // Горизонтальное движение к игроку
        if (target != null && !target.isDead()) {
            double dx = target.getX() - x;

            if (Math.abs(dx) > 10) {
                if (dx > 0) x += SPEED * deltaTime;
                else x -= SPEED * deltaTime;
            }
        }

        // Вертикальное движение
        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;

        setPosition(x, y);

        // Поворот врага в сторону игрока
        if (target != null) {
            if (target.getX() > x) sprite.setScaleX(1);
            else sprite.setScaleX(-1);
        }
    }

    public void landOnPlatform(double platformY) {
        y = platformY - HEIGHT;
        velocityY = 0;
        onGround = true;
        setPosition(x, y);
    }

    public void stopVerticalMovement() {
        velocityY = 0;
    }

    public void takeDamage(int damage) {
        health -= damage;

        sprite.setOpacity(0.5);
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> sprite.setOpacity(1.0));
        pause.play();

        if (health <= 0) {
            die();
        }
    }

    private void die() {
        sprite.setVisible(false);
    }

    public boolean isDead() { return health <= 0; }
    public int getDamage() { return damage; }
    public int getHealth() { return health; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public double getVelocityY() { return velocityY; }
}