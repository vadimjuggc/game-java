package com.mygame.game.entities;

import com.mygame.game.GameWorld;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class SkeletonEnemy {

    private static final double WIDTH = 24;
    private static final double HEIGHT = 36;
    private static final double ATTACK_RANGE = 380;
    private static final double MELEE_RANGE = 60;
    private static final double PROJECTILE_COOLDOWN = 2.5;
    private static final double MELEE_COOLDOWN = 3.0;

    private double x, y;
    private int health = 40;
    private boolean dead = false;
    private boolean facingRight = true;

    private ImageView sprite;
    private Image idleImage;
    private Image attackImage;
    private Pane parent;

    private Player target;
    private GameWorld gameWorld;

    private double projectileCooldown = 0;
    private double meleeCooldown = 0;
    private boolean isAttacking = false;

    private Rectangle flailHitbox;
    private boolean flailActive = false;
    private double flailTimer = 0;
    private static final double FLAIL_DURATION = 0.4;
    private int meleeDamage = 25;

    private List<DarkProjectile> projectiles = new ArrayList<>();

    public SkeletonEnemy(Pane parent, double x, double y, Player target, GameWorld gameWorld) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.target = target;
        this.gameWorld = gameWorld;

        idleImage = loadImage("/images/enemies/skeleton/skeleton_idle.gif");
        attackImage = loadImage("/images/enemies/skeleton/skeleton_attack.gif");

        sprite = new ImageView(idleImage);
        sprite.setFitWidth(WIDTH);
        sprite.setFitHeight(HEIGHT);
        sprite.setPreserveRatio(false);
        sprite.setX(x);
        sprite.setY(y);

        flailHitbox = new Rectangle(0, 0, 40, HEIGHT);
        flailHitbox.setFill(Color.TRANSPARENT);
        flailHitbox.setVisible(false);

        parent.getChildren().addAll(sprite, flailHitbox);
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("Не загружено: " + path);
            return null;
        }
    }

    public void update(double deltaTime) {
        if (dead) return;

        if (projectileCooldown > 0) projectileCooldown -= deltaTime;
        if (meleeCooldown > 0) meleeCooldown -= deltaTime;

        double dx = target.getX() - x;
        facingRight = dx > 0;
        sprite.setScaleX(facingRight ? -1 : 1);

        double distance = Math.abs(dx);

        if (distance < MELEE_RANGE && meleeCooldown <= 0 && !isAttacking) {
            startMeleeAttack();
        }
        else if (distance >= MELEE_RANGE && distance < ATTACK_RANGE && projectileCooldown <= 0 && !isAttacking) {
            shootProjectile();
        }

        if (flailActive) {
            flailTimer -= deltaTime;
            double hitboxX = facingRight ? x + WIDTH : x - 40;
            flailHitbox.setX(hitboxX);
            flailHitbox.setY(y);
            flailHitbox.setVisible(true);

            if (flailHitbox.getBoundsInParent().intersects(
                    target.getSprite().getBoundsInParent()) && !target.isInvincible()) {
                target.takeDamage(meleeDamage);
            }

            if (flailTimer <= 0) {
                flailActive = false;
                flailHitbox.setVisible(false);
                isAttacking = false;
                sprite.setImage(idleImage);
            }
        }

        for (DarkProjectile p : projectiles) {
            p.update(deltaTime);
            if (p.isActive() && p.getBounds().intersects(
                    target.getSprite().getBoundsInParent()) && !target.isInvincible()) {
                target.takeDamage(p.getDamage());
                p.deactivate();
            }
        }
        projectiles.removeIf(p -> !p.isActive());
    }

    private void startMeleeAttack() {
        isAttacking = true;
        meleeCooldown = MELEE_COOLDOWN;
        sprite.setImage(attackImage);

        PauseTransition delay = new PauseTransition(Duration.millis(300));
        delay.setOnFinished(e -> {
            flailActive = true;
            flailTimer = FLAIL_DURATION;
        });
        delay.play();
    }

    private void shootProjectile() {
        projectileCooldown = PROJECTILE_COOLDOWN;
        isAttacking = true;
        sprite.setImage(attackImage);

        double dirX = facingRight ? 1 : -1;
        double spawnX = facingRight ? x + WIDTH : x;
        DarkProjectile p = new DarkProjectile(parent, spawnX, y + HEIGHT / 2, dirX);
        projectiles.add(p);

        PauseTransition delay = new PauseTransition(Duration.millis(600));
        delay.setOnFinished(e -> {
            isAttacking = false;
            sprite.setImage(idleImage);
        });
        delay.play();
    }

    public void takeDamage(int damage) {
        if (dead) return;
        health -= damage;
        if (health < 0) health = 0;

        if (gameWorld != null) {
            gameWorld.showDamageNumber(damage, x, y - 20, false);
        }

        sprite.setOpacity(0.5);
        PauseTransition flash = new PauseTransition(Duration.millis(120));
        flash.setOnFinished(e -> sprite.setOpacity(1.0));
        flash.play();

        if (health <= 0) {
            dead = true;
            sprite.setVisible(false);
            flailHitbox.setVisible(false);
            projectiles.forEach(DarkProjectile::deactivate);
        }
    }

    public void removeFromPane() {
        parent.getChildren().remove(sprite);
        parent.getChildren().remove(flailHitbox);
    }

    public boolean isDead() { return dead; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return WIDTH; }
    public double getHeight() { return HEIGHT; }
    public ImageView getSprite() { return sprite; }
}