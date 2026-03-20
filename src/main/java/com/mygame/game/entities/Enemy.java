package com.mygame.game.entities;

import javafx.scene.paint.Color;

public class Enemy extends Entity {

    private static final double SPEED = 80;          // скорость (медленнее игрока)
    private static final double GRAVITY = 800;
    private static final double WIDTH = 30;
    private static final double HEIGHT = 30;

    private Player target;
    private double velocityY = 0;
    private boolean onGround = false;
    private int health;
    private int maxHealth;
    private int damage = 15;

    public Enemy(double startX, double startY, Player player) {
        super(startX, startY, WIDTH, HEIGHT);
        this.target = player;
        this.health = 40;
        this.maxHealth = 40;
        rectangle.setFill(Color.RED);
    }

    @Override
    public void update(double deltaTime) {
        if (target != null && !target.isDead()) {
            double dx = target.getX() - x;

            if (Math.abs(dx) > 10) { // не подходить слишком близко
                if (dx > 0) x += SPEED * deltaTime;
                else x -= SPEED * deltaTime;
            }
        }
        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;
        setPosition(x, y);
    }

    public void landOnPlatform(double platformY) {
        y = platformY - HEIGHT;
        velocityY = 0;
        onGround = true;
        setPosition(x, y);
    }

    public void takeDamage(int damage) {
        health-=damage;
        rectangle.setFill(Color.ORANGE);
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        pause.setOnFinished(e -> rectangle.setFill(Color.RED));
        pause.play();
        if(health<=0) die();
    }

    public int getHealth() {
        return health;
    }

    private void die() {
        rectangle.setVisible(false);
        // анимация смерти
    }

    public boolean isDead() { return health<=0;}
    public int getDamage() { return damage;}

    public double getVelocityY() {
        return velocityY;
    }
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
