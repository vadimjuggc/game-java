package com.mygame.game.entities;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class DashTrail {

    private ImageView ghost;
    private double lifetime = 0.15;
    private double maxLifetime = 0.15;
    private Pane parent;

    public DashTrail(Pane parent, double x, double y, double w, double h) {
        this.parent = parent;

        ghost = new ImageView();
        ghost.setFitWidth(w);
        ghost.setFitHeight(h);
        ghost.setX(x);
        ghost.setY(y);
        ghost.setOpacity(0.5);

        ghost.setStyle("-fx-blend-mode: multiply;");
        javafx.scene.effect.ColorAdjust ca = new javafx.scene.effect.ColorAdjust();
        ca.setHue(-0.7);
        ca.setBrightness(-0.3);
        ghost.setEffect(ca);

        parent.getChildren().add(ghost);
    }

    public boolean update(double deltaTime) {
        lifetime -= deltaTime;
        double alpha = Math.max(0, lifetime / maxLifetime) * 0.5;
        ghost.setOpacity(alpha);

        if (lifetime <= 0) {
            parent.getChildren().remove(ghost);
            return false;
        }
        return true;
    }
}