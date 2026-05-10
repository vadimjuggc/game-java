package com.mygame.game.entities;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class LandingParticle {

    private Ellipse ellipse;
    private double lifetime;
    private double maxLifetime;
    private double scaleSpeed;
    private Pane parent;

    public LandingParticle(Pane parent, double x, double y, int index, int total) {
        this.parent = parent;
        this.maxLifetime = 0.35 + Math.random() * 0.2;
        this.lifetime = maxLifetime;

        double angle = (Math.PI / (total - 1)) * index;
        double distance = 12 + Math.random() * 14;

        double ex = x + Math.cos(angle) * distance;
        double ey = y + Math.sin(angle) * 4;

        // крупнее
        ellipse = new Ellipse(ex, ey, 5 + Math.random() * 3, 2.5);

        int gray = 130 + (int)(Math.random() * 50);
        ellipse.setFill(Color.rgb(gray, gray - 10, gray - 20, 0.75));

        scaleSpeed = 2.5 + Math.random() * 1.5;
        parent.getChildren().add(ellipse);
    }

    public boolean update(double deltaTime) {
        lifetime -= deltaTime;
        double alpha = Math.max(0, lifetime / maxLifetime);

        ellipse.setRadiusX(ellipse.getRadiusX() + scaleSpeed * deltaTime * 12);
        ellipse.setRadiusY(ellipse.getRadiusY() + scaleSpeed * deltaTime * 3);
        ellipse.setOpacity(alpha);

        if (lifetime <= 0) {
            parent.getChildren().remove(ellipse);
            return false;
        }
        return true;
    }
}