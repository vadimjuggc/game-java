package com.mygame.game.entities;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DarkParticle {

    private Circle circle;
    private double x, y;
    private double velocityX, velocityY;
    private double lifetime;
    private double maxLifetime;
    private Pane parent;

    public DarkParticle(Pane parent, double x, double y) {
        this.parent = parent;
        this.x = x + (Math.random() * 16 - 8);
        this.y = y + (Math.random() * 16 - 8);

        this.velocityX = (Math.random() * 20 - 10);
        this.velocityY = -10 - Math.random() * 25;

        this.maxLifetime = 0.6 + Math.random() * 0.4;
        this.lifetime = maxLifetime;

        double size = 1.5 + Math.random() * 2.5;
        circle = new Circle(size);

        int variant = (int)(Math.random() * 3);
        if (variant == 0) circle.setFill(Color.rgb(60, 0, 80));
        else if (variant == 1) circle.setFill(Color.rgb(30, 0, 50));
        else circle.setFill(Color.rgb(80, 0, 100));

        circle.setCenterX(this.x);
        circle.setCenterY(this.y);
        parent.getChildren().add(circle);
    }

    public boolean update(double deltaTime) {
        velocityY -= 15 * deltaTime;
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;

        lifetime -= deltaTime;
        double alpha = Math.max(0, lifetime / maxLifetime);

        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setOpacity(alpha);
        circle.setRadius(circle.getRadius() * (0.995));

        if (lifetime <= 0) {
            parent.getChildren().remove(circle);
            return false;
        }
        return true;
    }
}