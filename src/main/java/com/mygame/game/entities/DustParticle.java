package com.mygame.game.entities;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DustParticle {

    private Circle circle;
    private Pane parent;
    private double vx;
    private double vy;
    private double lifetime;
    private double maxLifetime;

    public DustParticle(Pane parent, double x, double y) {
        this.parent = parent;

        this.vx = (Math.random() - 0.5) * 40;
        this.vy = -(20 + Math.random() * 50);

        this.maxLifetime = 0.4 + Math.random() * 0.3;
        this.lifetime = maxLifetime;

        double radius = 1.5 + Math.random() * 2;
        circle = new Circle(x + (Math.random() - 0.5) * 16, y, radius);

        int gray = 140 + (int)(Math.random() * 70);
        circle.setFill(Color.rgb(gray, gray - 5, gray - 15, 0.65));

        parent.getChildren().add(circle);
    }

    public boolean update(double deltaTime) {
        lifetime -= deltaTime;

        vx *= 0.92;
        vy += 60 * deltaTime;

        circle.setCenterX(circle.getCenterX() + vx * deltaTime);
        circle.setCenterY(circle.getCenterY() + vy * deltaTime);

        double alpha = Math.max(0, lifetime / maxLifetime);
        circle.setOpacity(alpha);

        circle.setRadius(circle.getRadius() * (0.98));

        if (lifetime <= 0) {
            parent.getChildren().remove(circle);
            return false;
        }
        return true;
    }
}