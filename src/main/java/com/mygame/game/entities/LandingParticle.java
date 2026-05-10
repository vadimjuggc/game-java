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
        this.maxLifetime = 0.25 + Math.random() * 0.15;
        this.lifetime = maxLifetime;

        double angle = (Math.PI / (total - 1)) * index;
        double distance = 8 + Math.random() * 10;

        double ex = x + Math.cos(angle) * distance;
        double ey = y + Math.sin(angle) * 3;

        ellipse = new Ellipse(ex, ey, 3 + Math.random() * 2, 1.5);

        int gray = 120 + (int)(Math.random() * 60);
        ellipse.setFill(Color.rgb(gray, gray, gray, 0.7));

        scaleSpeed = 1.5 + Math.random();
        parent.getChildren().add(ellipse);
    }

    public boolean update(double deltaTime) {
        lifetime -= deltaTime;
        double alpha = Math.max(0, lifetime / maxLifetime);

        ellipse.setRadiusX(ellipse.getRadiusX() + scaleSpeed * deltaTime * 10);
        ellipse.setOpacity(alpha);

        if (lifetime <= 0) {
            parent.getChildren().remove(ellipse);
            return false;
        }
        return true;
    }
}