package com.mygame.game.entities;

import com.mygame.game.GameWorld;
import com.mygame.game.Level;
import com.mygame.game.utils.SoundManager;
import javafx.scene.image.Image;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Ghost extends Entity {

    private static final double SPEED = 60;
    private static final double WIDTH = 18;
    private static final double HEIGHT = 22;

    private Player target;
    private GameWorld gameWorld;
    private SoundManager soundManager;

    private int health = 25;
    private int damage = 10;
    private double attackRange = 20;
    private double attackCooldown = 0;
    private boolean dead = false;
    private boolean hurt = false;
    private double hurtTimer = 0;

    private double waveTimer = 0;
    private static final double WAVE_AMPLITUDE = 25;
    private static final double WAVE_SPEED = 3.0;

    private double pulseTimer = 0;

    public Ghost(double startX, double startY, Player player, GameWorld gameWorld) {
        super(loadGhostImage(), startX, startY, WIDTH, HEIGHT);
        this.target = player;
        this.gameWorld = gameWorld;

        sprite.setFitWidth(WIDTH);
        sprite.setFitHeight(HEIGHT);
        sprite.setOpacity(0.88);
    }

    private static Image loadGhostImage() {
        try {
            return new Image(Ghost.class.getResourceAsStream("/images/enemies/ghost/ghost.png"));
        } catch (Exception e) {
            System.out.println("Ghost image not loaded");
            return null;
        }
    }

    public void setSoundManager(SoundManager sm) {
        this.soundManager = sm;
    }

    @Override
    public void update(double deltaTime) {
        if (dead) return;

        if (attackCooldown > 0) attackCooldown -= deltaTime;
        if (hurtTimer > 0) {
            hurtTimer -= deltaTime;
            if (hurtTimer <= 0) hurt = false;
        }

        if (target != null && !target.isDead()) {
            double dx = target.getX() + target.getWidth() / 2 - (x + WIDTH / 2);
            double dy = target.getY() + target.getHeight() / 2 - (y + HEIGHT / 2);
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > attackRange) {
                double nx = dx / dist;
                double ny = dy / dist;

                waveTimer += deltaTime * WAVE_SPEED;
                double wave = Math.sin(waveTimer) * WAVE_AMPLITUDE * deltaTime;
                double perpX = -ny * wave;
                double perpY = nx * wave;

                x += nx * SPEED * deltaTime + perpX;
                y += ny * SPEED * deltaTime + perpY;

                sprite.setScaleX(dx > 0 ? -1 : 1);
            } else {

                if (attackCooldown <= 0) {
                    attackCooldown = 1.5;
                    target.takeDamage(damage);
                    if (soundManager != null) soundManager.playHitSound();
                }
            }
        }

        pulseTimer += deltaTime * 2.0;
        if (!hurt) {
            double opacity = 0.78 + 0.1 * Math.sin(pulseTimer);
            sprite.setOpacity(opacity);
        }

        x = Math.max(0, Math.min(x, Level.WORLD_WIDTH - WIDTH));
        y = Math.max(0, Math.min(y, Level.WORLD_HEIGHT - HEIGHT));

        setPosition(x, y);
    }

    public void takeDamage(int damage) {
        return;
    }

    public void takeMeleeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;

        if (gameWorld != null) {
            gameWorld.showDamageNumber(damage, x, y - 20, false);
        }

        hurt = true;
        hurtTimer = 0.4;

        sprite.setOpacity(0.9);
        PauseTransition pause = new PauseTransition(Duration.millis(150));
        pause.setOnFinished(e -> sprite.setOpacity(0.7));
        pause.play();

        if (health <= 0) {
            dead = true;
            sprite.setVisible(false);
        }
    }

    public boolean isDead() { return dead; }
    public int getHealth() { return health; }
}