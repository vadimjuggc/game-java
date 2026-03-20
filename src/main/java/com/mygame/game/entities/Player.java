package com.mygame.game.entities;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import java.util.Set;

public class Player extends Entity{
    private static final double SPEED = 250;         // скорость бега
    private static final double JUMP_FORCE = -400;   // сила прыжка (отрицательная = вверх)
    private static final double GRAVITY = 800;
    private static final double WIDTH = 40;
    private static final double HEIGHT = 40;

    private boolean movingLeft, movingRight;
    private boolean onGround = false;  // стоит ли на земле
    private double velocityY = 0;
    private int health = 100;
    private int maxHealth = 100;
    private boolean attacking = false;
    private int attackCooldown = 0;
    private int attackDamage = 25;

    public Player(double startX, double startY) {
        super(startX, startY, WIDTH, HEIGHT);
        rectangle.setFill(javafx.scene.paint.Color.BLUE); // игрок синий
    }

    public void handleInput(Set<KeyCode> keysPressed) {
        movingLeft = keysPressed.contains(KeyCode.A);
        movingRight = keysPressed.contains(KeyCode.D);

        if(keysPressed.contains(KeyCode.SPACE) && onGround)
        {
            velocityY = JUMP_FORCE;
            onGround = false;
        }
        if(keysPressed.contains(KeyCode.E) && attackCooldown <= 0)
        {
            attacking = true;
            attackCooldown = 15;
        }
    }

    @Override
    public void update(double deltaTime) {
        if (movingLeft) x -= SPEED * deltaTime;
        if (movingRight) x += SPEED * deltaTime;

        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;

        if (x < 0) x = 0;
        if (x > 800 - WIDTH) x = 800 - WIDTH;

        // Пока без границ экрана (добавим позже)
        setPosition(x, y);

        if(attackCooldown > 0)
        {
            attackCooldown--;
            if(attackCooldown == 0) attacking = false;
        }
    }

    public void landOnPlatform(double platformY) {
        // Ставим игрока на платформу
        y = platformY - HEIGHT;
        velocityY = 0;
        onGround = true;
        setPosition(x, y);
    }

    public boolean isOnGround () { return onGround; }
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    public int getHealth() {
        return health;
    }
    public void takeDamage(int damage){
        health-=damage;
        if(health<0) health = 0;
        rectangle.setFill(Color.RED);
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        pause.setOnFinished(e -> rectangle.setFill(Color.BLUE));
        pause.play();
    }
    public  boolean isDead(){
        return(health<=0);
    }
    public boolean isAttacking() {return attacking;}
    public int getAttackDamage() {return attackDamage;}

    public javafx.geometry.Bounds getAttackBounds() {
        double attackX = movingRight ? x + WIDTH : x - 30;
        double attackY = y;
        double attackWidth = 30;
        double attackHeight = HEIGHT;
        return new javafx.geometry.BoundingBox(attackX, attackY, attackWidth, attackHeight);
    }
}
