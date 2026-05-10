package com.mygame.game.entities;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BloodParticle {

    private Circle circle;
    private double x, y;
    private double velocityX, velocityY;
    private double lifetime;
    private double maxLifetime;
    private Pane parent;

    public BloodParticle(Pane parent, double x, double y) {
        this.parent = parent;
        this.x = x;
        this.y = y;

        double angle = Math.random() * Math.PI * 2;
        double speed = 40 + Math.random() * 120;
        this.velocityX = Math.cos(angle) * speed;
        this.velocityY = Math.sin(angle) * speed - 80;

        this.maxLifetime = 0.4 + Math.random() * 0.3;
        this.lifetime = maxLifetime;

        double size = 2 + Math.random() * 3;
        circle = new Circle(size);
        circle.setFill(Color.rgb(160, 0, 0));
        circle.setCenterX(x);
        circle.setCenterY(y);

        parent.getChildren().add(circle);
    }

    public boolean update(double deltaTime) {
        velocityY += 400 * deltaTime; // грав крови
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;

        lifetime -= deltaTime;
        double alpha = Math.max(0, lifetime / maxLifetime);

        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setOpacity(alpha);

        if (lifetime <= 0) {
            parent.getChildren().remove(circle);
            return false;
        }
        return true;
    }
}