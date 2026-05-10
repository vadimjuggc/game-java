package com.mygame.game.entities;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DarkProjectile {

    private Circle circle;
    private Pane parent;
    private double vx;
    private double x, y;
    private boolean active = true;
    private static final double SPEED = 180;
    private static final double RADIUS = 5;
    private int damage = 10;

    public DarkProjectile(Pane parent, double x, double y, double directionX) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.vx = directionX * SPEED;

        circle = new Circle(x, y, RADIUS);
        circle.setFill(Color.rgb(40, 0, 60));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(120, 0, 180, 0.9));
        glow.setRadius(10);
        circle.setEffect(glow);

        parent.getChildren().add(circle);
    }

    public void update(double deltaTime) {
        if (!active) return;

        x += vx * deltaTime;
        circle.setCenterX(x);
        circle.setCenterY(y);

        if (x < 0 || x > 2400) {
            deactivate();
        }
    }

    public void deactivate() {
        active = false;
        parent.getChildren().remove(circle);
    }

    public boolean isActive() { return active; }
    public int getDamage() { return damage; }

    public javafx.geometry.Bounds getBounds() {
        return circle.getBoundsInParent();
    }
}