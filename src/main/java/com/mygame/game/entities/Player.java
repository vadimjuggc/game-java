package com.mygame.game.entities;

import com.mygame.game.utils.FrameAnimation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import com.mygame.game.utils.SoundManager;


public class Player extends Entity {

    private static final double SPEED = 250;
    private static final double JUMP_FORCE = -500;
    private static final double GRAVITY = 800;
    private static final double WIDTH = 24;
    private static final double HEIGHT = 32;

    private boolean movingLeft, movingRight;
    private boolean onGround = false;
    private double velocityY = 0;

    private int health = 100;
    private boolean attacking = false;
    private int attackCooldown = 0;
    private int attackDamage = 25;
    private List<Arrow> arrows = new ArrayList<>();


    // Анимации игрока
    private FrameAnimation idleRightAnimation;
    private FrameAnimation walkRightAnimation;
    private FrameAnimation attackRightAnimation;
    private FrameAnimation hurtRightAnimation;

    private FrameAnimation idleLeftAnimation;
    private FrameAnimation walkLeftAnimation;
    private FrameAnimation attackLeftAnimation;
    private FrameAnimation hurtLeftAnimation;
    private FrameAnimation currentAnimation;

    // Оружие
    private final double SHOOT_DELAY = 0.6;
    private int arrowsLeft = 10; // начальное количество стрел
    private int maxArrows = 10;
    private double shootCooldown = 0;
    private ImageView weaponSprite;
    private FrameAnimation bowIdleAnimation;
    private FrameAnimation bowShootAnimation;
    private FrameAnimation currentWeaponAnimation;
    private boolean isBowEquipped = true;
    private boolean facingRight = true;
    private enum State { IDLE, WALKING, ATTACKING, HURT }
    private State currentState = State.IDLE;
    public List<Arrow> getArrows() { return arrows; }
    public void removeArrow(Arrow arrow) { arrows.remove(arrow); }
    public int getArrowsLeft() { return arrowsLeft; }
    public void addArrows(int amount) {
        arrowsLeft = Math.min(arrowsLeft + amount, maxArrows);
    }

    public void shootArrow() {
        if (arrowsLeft <= 0) {
            System.out.println("Нет стрел! arrowsLeft=" + arrowsLeft);
            return;
        }

        System.out.println("shootArrow вызван, вызываем звук");
        SoundManager.getInstance().playBowShootSound();
        System.out.println("Звук вызван");

        double directionX = facingRight ? 1 : -1;
        double directionY = 0;
        Arrow arrow = new Arrow(x + (facingRight ? WIDTH : -10), y + HEIGHT/2, directionX, directionY);
        arrows.add(arrow);
        arrowsLeft--;
        System.out.println("Стрела создана, осталось стрел: " + arrowsLeft);
    }

    public Player(double startX, double startY) {
        super(loadPlaceholderImage(), startX, startY, WIDTH, HEIGHT);

        // Оружие
        weaponSprite = new ImageView();
        weaponSprite.setFitWidth(14);
        weaponSprite.setPreserveRatio(true);

        loadAnimations();
        loadBowAnimations();

        currentAnimation = idleRightAnimation;
        if (currentAnimation != null) currentAnimation.play();

        currentWeaponAnimation = bowIdleAnimation;
        if (currentWeaponAnimation != null) currentWeaponAnimation.play();
    }

    public ImageView getWeaponSprite() {
        return weaponSprite;
    }

    private void loadAnimations() {
        List<Image> idleRightFrames = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            idleRightFrames.add(loadImage("/images/player/idle/Idle_East_" + i + ".png"));
        }
        List<Image> idleLeftFrames = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            idleLeftFrames.add(loadImage("/images/player/idle/Idle_West_" + i + ".png"));
        }

        List<Image> walkRightFrames = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            walkRightFrames.add(loadImage("/images/player/walk/Walk_East_" + i + ".png"));
        }
        List<Image> walkLeftFrames = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            walkLeftFrames.add(loadImage("/images/player/walk/Walk_West_" + i + ".png"));
        }

        List<Image> attackRightFrames = new ArrayList<>();
        attackRightFrames.add(loadImage("/images/player/attack/Attack_East.png"));
        List<Image> attackLeftFrames = new ArrayList<>();
        attackLeftFrames.add(loadImage("/images/player/attack/Attack_West.png"));

        List<Image> hurtRightFrames = new ArrayList<>();
        hurtRightFrames.add(loadImage("/images/player/hurt/Hurt_West.png"));
        List<Image> hurtLeftFrames = new ArrayList<>();
        hurtLeftFrames.add(loadImage("/images/player/hurt/Hurt_East.png"));

        idleRightAnimation = new FrameAnimation(idleRightFrames, sprite);
        walkRightAnimation = new FrameAnimation(walkRightFrames, sprite);
        attackRightAnimation = new FrameAnimation(attackRightFrames, sprite);
        hurtRightAnimation = new FrameAnimation(hurtRightFrames, sprite);

        idleLeftAnimation = new FrameAnimation(idleLeftFrames, sprite);
        walkLeftAnimation = new FrameAnimation(walkLeftFrames, sprite);
        attackLeftAnimation = new FrameAnimation(attackLeftFrames, sprite);
        hurtLeftAnimation = new FrameAnimation(hurtLeftFrames, sprite);

        idleRightAnimation.setFrameDuration(0.15);
        walkRightAnimation.setFrameDuration(0.1);
        idleLeftAnimation.setFrameDuration(0.15);
        walkLeftAnimation.setFrameDuration(0.1);

        attackRightAnimation.setFrameDuration(0.1);
        attackLeftAnimation.setFrameDuration(0.1);
        hurtRightAnimation.setFrameDuration(0.1);
        hurtLeftAnimation.setFrameDuration(0.1);

        System.out.println("Анимации игрока загружены");
    }

    private void loadBowAnimations() {
        weaponSprite.setFitWidth(14);
        weaponSprite.setPreserveRatio(true);
        List<Image> idleFrames = new ArrayList<>();
        idleFrames.add(loadImage("/images/weapon/bow/Bow-1.png"));

        List<Image> shootFrames = new ArrayList<>();
        for (int i = 2; i <= 8; i++) {
            shootFrames.add(loadImage("/images/weapon/bow/Bow-" + i + ".png"));
        }

        bowIdleAnimation = new FrameAnimation(idleFrames, weaponSprite);
        bowShootAnimation = new FrameAnimation(shootFrames, weaponSprite);

        bowShootAnimation.setLoop(false);
        bowIdleAnimation.setFrameDuration(0.1);
        bowShootAnimation.setFrameDuration(0.06);
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

    public void handleInput(Set<KeyCode> keysPressed) {
        movingLeft = keysPressed.contains(KeyCode.A);
        movingRight = keysPressed.contains(KeyCode.D);

        if (keysPressed.contains(KeyCode.SPACE) && onGround) {
            velocityY = JUMP_FORCE;
            onGround = false;
        }
        if (keysPressed.contains(KeyCode.Q) && !attacking && shootCooldown <= 0) {
            shootArrow();
            shootCooldown = SHOOT_DELAY;
            if (isBowEquipped) {
                currentWeaponAnimation = bowShootAnimation;
                bowShootAnimation.reset();
                bowShootAnimation.play();
            }
        }
        if (keysPressed.contains(KeyCode.E) && attackCooldown <= 0 && !attacking) {
            attacking = true;
            attackCooldown = 20;
            currentState = State.ATTACKING;

            if (facingRight) {
                currentAnimation = attackRightAnimation;
                attackRightAnimation.reset();
                attackRightAnimation.play();
            } else {
                currentAnimation = attackLeftAnimation;
                attackLeftAnimation.reset();
                attackLeftAnimation.play();
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        if (shootCooldown > 0) {
            shootCooldown -= deltaTime;
        }
        if (movingRight && !movingLeft) {
            facingRight = true;
        } else if (movingLeft && !movingRight) {
            facingRight = false;
        }

        if (facingRight) {
            weaponSprite.setScaleX(-1);
        } else {
            weaponSprite.setScaleX(1);
        }
        double weaponOffsetX;
        double weaponOffsetY;

        if (movingLeft || movingRight) {
            weaponOffsetX = movingRight ? 12 : -6;
            weaponOffsetY = 14;
        } else {
            weaponOffsetX = facingRight ? 12 : -3;
            weaponOffsetY = 13;
        }

        weaponSprite.setX(x + weaponOffsetX);
        weaponSprite.setY(y + weaponOffsetY);

        if (!onGround && !attacking && currentState != State.HURT) {
            if (movingRight && currentAnimation != walkRightAnimation) {
                currentAnimation = walkRightAnimation;
                walkRightAnimation.play();
            } else if (movingLeft && currentAnimation != walkLeftAnimation) {
                currentAnimation = walkLeftAnimation;
                walkLeftAnimation.play();
            }
        }

        if (!attacking && currentState != State.HURT) {
            if (movingRight) {
                if (currentState != State.WALKING) {
                    currentState = State.WALKING;
                    currentAnimation = walkRightAnimation;
                    walkRightAnimation.play();
                    idleRightAnimation.stop();
                }
            } else if (movingLeft) {
                if (currentState != State.WALKING) {
                    currentState = State.WALKING;
                    currentAnimation = walkLeftAnimation;
                    walkLeftAnimation.play();
                    idleLeftAnimation.stop();
                }
            } else {
                if (currentState != State.IDLE) {
                    currentState = State.IDLE;
                    if (facingRight) {
                        currentAnimation = idleRightAnimation;
                        idleRightAnimation.play();
                        walkRightAnimation.stop();
                    } else {
                        currentAnimation = idleLeftAnimation;
                        idleLeftAnimation.play();
                        walkLeftAnimation.stop();
                    }
                }
            }
        }

        // Движение
        if (movingLeft) x -= SPEED * deltaTime;
        if (movingRight) x += SPEED * deltaTime;

        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;

        if (x < 0) x = 0;
        if (x > 800 - WIDTH) x = 800 - WIDTH;

        setPosition(x, y);

        // Атака
        if (attackCooldown > 0) {
            attackCooldown--;
            if (attackCooldown == 0) {
                attacking = false;
                currentState = State.IDLE;

                if (facingRight) {
                    currentAnimation = idleRightAnimation;
                    idleRightAnimation.play();
                } else {
                    currentAnimation = idleLeftAnimation;
                    idleLeftAnimation.play();
                }

                if (isBowEquipped && bowShootAnimation.isFinished()) {
                    currentWeaponAnimation = bowIdleAnimation;
                    bowIdleAnimation.play();
                }
            }
        }

        // Обновление анимаций
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
        }
        if (currentWeaponAnimation != null) {
            currentWeaponAnimation.update(deltaTime);
        }
    }

    public void takeDamage(int damage) {
        System.out.println("takeDamage вызван");
        health -= damage;
        if (health < 0) health = 0;

        currentState = State.HURT;
        if (facingRight) {
            currentAnimation = hurtRightAnimation;
            hurtRightAnimation.reset();
            hurtRightAnimation.play();
        } else {
            currentAnimation = hurtLeftAnimation;
            hurtLeftAnimation.reset();
            hurtLeftAnimation.play();
        }

        sprite.setOpacity(0.5);
        weaponSprite.setOpacity(0.5);
        PauseTransition pause = new PauseTransition(Duration.millis(200));
        pause.setOnFinished(e -> {
            sprite.setOpacity(1.0);
            weaponSprite.setOpacity(1.0);
            if (!attacking) {
                currentState = State.IDLE;
                if (facingRight) {
                    currentAnimation = idleRightAnimation;
                    idleRightAnimation.play();
                } else {
                    currentAnimation = idleLeftAnimation;
                    idleLeftAnimation.play();
                }
            }
        });
        pause.play();
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

    public boolean isDead() { return health <= 0; }
    public boolean isAttacking() { return attacking; }
    public int getAttackDamage() { return attackDamage; }
    public int getHealth() { return health; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }

    public javafx.geometry.Bounds getAttackBounds() {
        double attackX = facingRight ? x + WIDTH : x - 30;
        double attackY = y;
        double attackWidth = 30;
        double attackHeight = HEIGHT;
        return new javafx.geometry.BoundingBox(attackX, attackY, attackWidth, attackHeight);
    }
}