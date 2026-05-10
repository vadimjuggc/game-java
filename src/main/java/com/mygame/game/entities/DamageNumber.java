package com.mygame.game.entities;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class DamageNumber {
    private Label label;

    public DamageNumber(Pane root, int damage, double x, double y, boolean isPlayer) {
        if (damage < 0) {
            label = new Label("+" + Math.abs(damage));
            label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            label.setTextFill(Color.LIMEGREEN);
        } else {
            label = new Label("-" + damage);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            if (isPlayer) {
                label.setTextFill(Color.RED);
            } else {
                label.setTextFill(Color.WHITE);
            }
        }

        label.setLayoutX(x);
        label.setLayoutY(y);
        root.getChildren().add(label);

        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(0.8), label);
        moveUp.setByY(-40);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        moveUp.play();
        fadeOut.play();

        fadeOut.setOnFinished(e -> root.getChildren().remove(label));
    }
}