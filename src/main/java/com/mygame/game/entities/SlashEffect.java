package com.mygame.game.entities;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SlashEffect {

    private Canvas canvas;
    private double lifetime;
    private double maxLifetime = 0.15;
    private Pane parent;
    private double x, y;
    private boolean facingRight;

    public SlashEffect(Pane parent, double x, double y, boolean facingRight) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.facingRight = facingRight;
        this.lifetime = maxLifetime;

        canvas = new Canvas(60, 60);
        canvas.setLayoutX(x - 30);
        canvas.setLayoutY(y - 30);
        canvas.setMouseTransparent(true);

        parent.getChildren().add(canvas);
        draw(1.0);
    }

    private void draw(double alpha) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 60, 60);

        gc.setStroke(Color.rgb(255, 255, 255, alpha));
        gc.setLineWidth(3);

        double startAngle = facingRight ? -60 : 120;
        double arcExtent = facingRight ? 120 : -120;

        gc.strokeArc(5, 5, 50, 50, startAngle, arcExtent,
                javafx.scene.shape.ArcType.OPEN);

        gc.setStroke(Color.rgb(200, 220, 255, alpha * 0.5));
        gc.setLineWidth(6);
        gc.strokeArc(8, 8, 44, 44, startAngle, arcExtent,
                javafx.scene.shape.ArcType.OPEN);
    }

    public boolean update(double deltaTime) {
        lifetime -= deltaTime;
        double alpha = Math.max(0, lifetime / maxLifetime);
        draw(alpha);

        if (lifetime <= 0) {
            parent.getChildren().remove(canvas);
            return false;
        }
        return true;
    }
}