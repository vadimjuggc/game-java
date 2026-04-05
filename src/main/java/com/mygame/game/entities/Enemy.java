package com.mygame.game.entities;

import com.mygame.game.utils.FrameAnimation;
import javafx.scene.image.Image;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Entity {

    private static final double SPEED = 80;
    private static final double GRAVITY = 800;
    private static final double WIDTH = 13;
    private static final double HEIGHT = 13;

    private Player target;
    private double velocityY = 0;
    private boolean onGround = false;

    private int health;
    private int damage = 15;

    private FrameAnimation walkAnimation;
    private boolean facingRight = true;

    public Enemy(double startX, double startY, Player player) {
        super(loadPlaceholderImage(), startX, startY, WIDTH, HEIGHT);
        this.target = player;
        this.health = 40;

        loadAnimations();

        if (walkAnimation != null) {
            walkAnimation.play();
        }
    }

    private void loadAnimations() {
        List<Image> walkFrames = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            String path = "/images/enemies/slime/slime_" + i + ".png";
            Image img = loadImage(path);
            if (img != null) {
                walkFrames.add(img);
            }
        }

        if (!walkFrames.isEmpty()) {
            walkAnimation = new FrameAnimation(walkFrames, sprite);
            walkAnimation.setFrameDuration(0.15);
            System.out.println("Анимация слизняка загружена, кадров: " + walkFrames.size());
        } else {
            System.out.println("Не удалось загрузить анимацию слизняка");
        }
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("Не загружено: " + path);
            return null;
        }
    }

    private static Image loadPlaceholderImage() {
        return null;
    }

    @Override
    public void update(double deltaTime) {
        // Горизонтальное движение к игроку
        if (target != null && !target.isDead()) {
            double dx = target.getX() - x;

            if (Math.abs(dx) > 10) {
                if (dx > 0) {
                    x += SPEED * deltaTime;
                    facingRight = true;
                } else {
                    x -= SPEED * deltaTime;
                    facingRight = false;
                }
            }
        }

        // Отражаем спрайт в зависимости от направления
        if (facingRight) {
            sprite.setScaleX(1);
        } else {
            sprite.setScaleX(-1);
        }

        // Вертикальное движение
        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;

        setPosition(x, y);

        // Обновляем анимацию
        if (walkAnimation != null) {
            walkAnimation.update(deltaTime);
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