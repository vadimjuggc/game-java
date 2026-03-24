package com.mygame.game.entities;

import com.mygame.game.utils.SpriteSheet;
import com.mygame.game.utils.Animation;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.Set;

public class Player extends Entity {

    private static final double SPEED = 250;
    private static final double JUMP_FORCE = -500;
    private static final double GRAVITY = 800;
    private static final double WIDTH = 32;
    private static final double HEIGHT = 40;

    private boolean movingLeft, movingRight;
    private boolean onGround = false;
    private double velocityY = 0;

    private int health = 100;
    private int maxHealth = 100;
    private boolean attacking = false;
    private int attackCooldown = 0;
    private int attackDamage = 25;

    // Анимации
    private Animation idleAnimation;
    private Animation walkAnimation;
    private Animation attackAnimation;
    private Animation currentAnimation;

    private enum State { IDLE, WALKING, ATTACKING }
    private State currentState = State.IDLE;

    public Player(double startX, double startY) {
        super(loadPlaceholderImage(), startX, startY, WIDTH, HEIGHT);
        loadAnimations();

        // Если картинка не загрузилась, используем заглушку
        if (sprite.getImage() == null) {
            sprite.setStyle("-fx-background-color: blue; -fx-shape: 'M0,0 L32,0 L32,40 L0,40 Z';");
        }

        currentAnimation = idleAnimation;
        if (currentAnimation != null) currentAnimation.play();
    }

    private void loadAnimations() {
        try {
            // Правильный путь: файл должен быть в src/main/resources/images/player.png
            SpriteSheet sheet = new SpriteSheet("/images/player.png", (int)WIDTH, (int)HEIGHT);

            // ВРЕМЕННО: используем первый кадр для всех анимаций
            idleAnimation = new Animation(sheet.getFrames(0, 1), sprite);
            walkAnimation = new Animation(sheet.getFrames(0, 1), sprite);
            attackAnimation = new Animation(sheet.getFrames(0, 1), sprite);

            idleAnimation.setFrameDuration(0.1);
            walkAnimation.setFrameDuration(0.1);
            attackAnimation.setFrameDuration(0.08);

        } catch (Exception e) {
            System.out.println("Не удалось загрузить спрайт-лист: " + e.getMessage());
            // Создаём пустые анимации, чтобы избежать NullPointerException
            idleAnimation = new Animation(java.util.Collections.emptyList(), sprite);
            walkAnimation = new Animation(java.util.Collections.emptyList(), sprite);
            attackAnimation = new Animation(java.util.Collections.emptyList(), sprite);
        }
    }

    private static Image loadPlaceholderImage() {
        return null;
    }

    public void handleInput(Set<KeyCode> keysPressed) {
        movingLeft = keysPressed.contains(KeyCode.A);
        movingRight = keysPressed.contains(KeyCode.D);

        if (keysPressed.contains(KeyCode.SPACE) && onGround) {
            velocityY = JUMP_FORCE;
            onGround = false;
        }

        if (keysPressed.contains(KeyCode.E) && attackCooldown <= 0 && !attacking) {
            attacking = true;
            attackCooldown = 15;
            currentState = State.ATTACKING;
            currentAnimation = attackAnimation;
            if (attackAnimation != null) {
                attackAnimation.reset();
                attackAnimation.play();
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        // Обновляем состояние
        if (!attacking) {
            if (movingLeft || movingRight) {
                if (currentState != State.WALKING) {
                    currentState = State.WALKING;
                    currentAnimation = walkAnimation;
                    if (walkAnimation != null) {
                        walkAnimation.play();
                        if (idleAnimation != null) idleAnimation.stop();
                    }
                }
            } else {
                if (currentState != State.IDLE) {
                    currentState = State.IDLE;
                    currentAnimation = idleAnimation;
                    if (idleAnimation != null) {
                        idleAnimation.play();
                        if (walkAnimation != null) walkAnimation.stop();
                    }
                }
            }
        }

        // Горизонтальное движение
        if (movingLeft) x -= SPEED * deltaTime;
        if (movingRight) x += SPEED * deltaTime;

        // Вертикальное движение
        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;

        // Границы экрана
        if (x < 0) x = 0;
        if (x > 800 - WIDTH) x = 800 - WIDTH;

        setPosition(x, y);

        // Атака
        if (attackCooldown > 0) {
            attackCooldown--;
            if (attackCooldown == 0) {
                attacking = false;
                currentState = State.IDLE;
                currentAnimation = idleAnimation;
                if (idleAnimation != null) {
                    idleAnimation.reset();
                    idleAnimation.play();
                }
                if (attackAnimation != null) attackAnimation.stop();
            }
        }

        // Обновляем текущую анимацию
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
        }

        // Поворот спрайта в зависимости от направления
        if (movingRight) {
            sprite.setScaleX(1);
        } else if (movingLeft) {
            sprite.setScaleX(-1);
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
        if (health < 0) health = 0;

        sprite.setOpacity(0.5);
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> sprite.setOpacity(1.0));
        pause.play();
    }

    public boolean isDead() { return health <= 0; }
    public boolean isAttacking() { return attacking; }
    public int getAttackDamage() { return attackDamage; }
    public int getHealth() { return health; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }

    public javafx.geometry.Bounds getAttackBounds() {
        double attackX = movingRight ? x + WIDTH : x - 30;
        double attackY = y;
        double attackWidth = 30;
        double attackHeight = HEIGHT;
        return new javafx.geometry.BoundingBox(attackX, attackY, attackWidth, attackHeight);
    }
}