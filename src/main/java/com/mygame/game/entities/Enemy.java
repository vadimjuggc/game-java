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
    private static final double WIDTH = 20;
    private static final double HEIGHT = 20;

    private Player target;
    private double velocityY = 0;
    private boolean onGround = false;

    private int health;
    private int damage = 15;
    private int attackRange = 35;
    private int attackHitFrame = 3;
    private boolean damageDealt = false;

    private FrameAnimation idleAnimation;
    private FrameAnimation walkAnimation;
    private FrameAnimation attackAnimation;
    private FrameAnimation hurtAnimation;
    private FrameAnimation currentAnimation;
    private boolean facingRight = true;
    private boolean attacking = false;
    private double attackCooldown = 0;
    private boolean hurt = false;
    private double hurtTimer = 0;

    public Enemy(double startX, double startY, Player player) {
        super(loadPlaceholderImage(), startX, startY, WIDTH, HEIGHT);
        this.target = player;
        this.health = 40;

        loadAnimations();

        currentAnimation = idleAnimation;
        if (currentAnimation != null) { currentAnimation.play(); }
    }

    private void loadAnimations() {
        List<Image> idleFrames = new ArrayList<>();
        idleFrames.add(loadImage("/images/enemies/slime/idle/slime1.png"));

        List<Image> walkFrames = new ArrayList<>();
        for (int i = 2; i <=8; i++)
        {
            walkFrames.add(loadImage("/images/enemies/slime/walk/slime" + i +".png"));
        }
        List <Image> attackFrames = new ArrayList<>();
        for (int i = 9; i <= 13; i++)
        {
            attackFrames.add(loadImage("/images/enemies/slime/attack/slime" + i +".png"));
        }
        List <Image> hurtFrames = new ArrayList<>();
        for (int i = 14; i <= 17; i++)
        {
            hurtFrames.add(loadImage("/images/enemies/slime/hurt/slime" + i +".png"));
        }
        idleAnimation = new FrameAnimation(idleFrames, sprite);
        walkAnimation = new FrameAnimation(walkFrames, sprite);
        attackAnimation = new FrameAnimation(attackFrames, sprite);
        hurtAnimation = new FrameAnimation(hurtFrames, sprite);

        idleAnimation.setFrameDuration(0.15);
        walkAnimation.setFrameDuration(0.1);
        attackAnimation.setFrameDuration(0.08);
        hurtAnimation.setFrameDuration(0.1);

        attackAnimation.setLoop(false);
        hurtAnimation.setLoop(false);
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
        // Таймеры для атаки и урона
        if (!hurt && !attacking) {
            if (target != null && !target.isDead()) {
                double dx = target.getX() - x;
                double dy = Math.abs(target.getY() - y);
                double distance = Math.abs(dx);

                if (distance < attackRange && attackCooldown <= 0 && !hurt) {
                    // Начинаем атаку
                    attacking = true;
                    damageDealt = false;
                    currentAnimation = attackAnimation;
                    attackAnimation.reset();
                    attackAnimation.play();
                } else if (distance > 20) {
                    // Движение к игроку
                    if (dx > 0) {
                        x += SPEED * deltaTime;
                        facingRight = true;
                    } else {
                        x -= SPEED * deltaTime;
                        facingRight = false;
                    }
                    currentAnimation = walkAnimation;
                }
            }
        }

// Наносим урон в середине анимации
        if (attacking && !damageDealt && attackAnimation.getCurrentFrameIndex() >= attackHitFrame) {
            damageDealt = true;
            if (target != null) {
                target.takeDamage(damage);
            }
        }

// Проверка окончания атаки
        if (attacking && attackAnimation.isFinished()) {
            attacking = false;
            attackCooldown = 1.0;
            currentAnimation = idleAnimation;
            idleAnimation.play();
        }

        if (facingRight) {
            sprite.setScaleX(1);
        } else {
            sprite.setScaleX(-1);
        }

        // Вертикальное движение
        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;

        setPosition(x, y);

        // Проверка окончания атаки
        if (attacking && attackAnimation.isFinished()) {
            attacking = false;
            currentAnimation = idleAnimation;
            idleAnimation.play();
        }

        // Проверка окончания урона
        if (hurt && hurtAnimation.isFinished()) {
            hurt = false;
            currentAnimation = idleAnimation;
            idleAnimation.play();
        }

        // Обновляем анимацию
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
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

        // Анимация получения урона
        hurt = true;
        hurtTimer = 0.5;
        attacking = false;
        currentAnimation = hurtAnimation;
        hurtAnimation.reset();
        hurtAnimation.play();

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