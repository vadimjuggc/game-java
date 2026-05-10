package com.mygame.game.entities;

import com.mygame.game.GameWorld;
import com.mygame.game.Level;
import com.mygame.game.utils.FrameAnimation;
import com.mygame.game.utils.SoundManager;
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
    private SoundManager soundManager;
    private GameWorld gameWorld;
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

    private double currentPlatformLeft = 0;
    private double currentPlatformRight = Level.WORLD_WIDTH;
    private boolean hasPlatformInfo = false;

    public boolean isAttacking() {
        return attacking;
    }

    public void setSoundManager(SoundManager sm) {
        this.soundManager = sm;
    }

    public Enemy(double startX, double startY, Player player, GameWorld gameWorld) {
        super(loadPlaceholderImage(), startX, startY, WIDTH, HEIGHT);
        this.target = player;
        this.gameWorld = gameWorld;
        this.health = 40;

        loadAnimations();
        currentAnimation = idleAnimation;
        if (currentAnimation != null) { currentAnimation.play(); }
    }

    public void setCurrentPlatformBounds(double left, double right) {
        this.currentPlatformLeft = left;
        this.currentPlatformRight = right;
        this.hasPlatformInfo = true;
    }

    private void loadAnimations() {
        List<Image> idleFrames = new ArrayList<>();
        idleFrames.add(loadImage("/images/enemies/slime/idle/slime1.png"));

        List<Image> walkFrames = new ArrayList<>();
        for (int i = 2; i <= 8; i++) {
            walkFrames.add(loadImage("/images/enemies/slime/walk/slime" + i + ".png"));
        }
        List<Image> attackFrames = new ArrayList<>();
        for (int i = 9; i <= 13; i++) {
            attackFrames.add(loadImage("/images/enemies/slime/attack/slime" + i + ".png"));
        }
        List<Image> hurtFrames = new ArrayList<>();
        for (int i = 14; i <= 17; i++) {
            hurtFrames.add(loadImage("/images/enemies/slime/hurt/slime" + i + ".png"));
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
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        if (hurtTimer > 0) {
            hurtTimer -= deltaTime;
            if (hurtTimer <= 0) {
                hurt = false;
            }
        }

        if (attacking) {
            if (!damageDealt && attackAnimation.getCurrentFrameIndex() >= attackHitFrame) {
                damageDealt = true;
                if (target != null && !target.isDead()) {
                    target.takeDamage(damage);
                    if (soundManager != null) {
                        soundManager.playHitSound();
                        soundManager.playSlimeAttackSound();
                    }
                }
            }

            if (attackAnimation.isFinished()) {
                attacking = false;
                attackCooldown = 1.2;
                currentAnimation = idleAnimation;
                idleAnimation.play();
            }
        }

        if (!attacking && !hurt && target != null && !target.isDead()) {
            double dx = target.getX() - x;
            double distance = Math.abs(dx);
            double dy = target.getY() - y;

            boolean playerBelow = (target.getY() + target.getHeight()) < (y - 40);

            if (playerBelow && onGround && hasPlatformInfo) {
                if (dx > 0) {
                    x += SPEED * deltaTime;
                    facingRight = true;
                    if (x + WIDTH >= currentPlatformRight) {
                        x = currentPlatformRight - WIDTH + 1;
                    }
                } else {
                    x -= SPEED * deltaTime;
                    facingRight = false;
                    if (x <= currentPlatformLeft) {
                        x = currentPlatformLeft - 1;
                    }
                }
                currentAnimation = walkAnimation;
            } else if (distance < attackRange && Math.abs(dy) < HEIGHT + 10 && attackCooldown <= 0) {
                attacking = true;
                damageDealt = false;
                currentAnimation = attackAnimation;
                attackAnimation.reset();
                attackAnimation.play();
            } else if (distance > 15) {
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

        if (facingRight) {
            sprite.setScaleX(-1);
        } else {
            sprite.setScaleX(1);
        }

        velocityY += GRAVITY * deltaTime;
        y += velocityY * deltaTime;
        setPosition(x, y);

        if (y + HEIGHT > Level.WORLD_HEIGHT) {
            y = Level.WORLD_HEIGHT - HEIGHT;
            velocityY = 0;
            onGround = true;
            setPosition(x, y);
        }

        if (hurt && hurtAnimation.isFinished()) {
            hurt = false;
            if (!attacking) {
                currentAnimation = idleAnimation;
                idleAnimation.play();
            }
        }

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

        if (gameWorld != null) {
            gameWorld.showDamageNumber(damage, x, y - 20, false);
        }

        hurt = true;
        hurtTimer = 0.5;

        if (!attacking) {
            currentAnimation = hurtAnimation;
            hurtAnimation.reset();
            hurtAnimation.play();
        }

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
