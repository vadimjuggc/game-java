package com.mygame.game.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameUI {

    private Label healthLabel;
    private Label scoreLabel;
    private Label arrowsLabel;
    private Label weaponLabel;
    private int score = 0;

    public GameUI(Pane root) {
        healthLabel = new Label("❤ Здоровье: 100");
        healthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5 10 5 10;");
        healthLabel.setLayoutX(10);
        healthLabel.setLayoutY(10);

        scoreLabel = new Label("⭐ Очки: 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5 10 5 10;");
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(50);

        arrowsLabel = new Label("🏹 Стрелы: 10");
        arrowsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        arrowsLabel.setTextFill(Color.WHITE);
        arrowsLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5 10 5 10;");
        arrowsLabel.setLayoutX(10);
        arrowsLabel.setLayoutY(90);

        weaponLabel = new Label("⚔️ Меч");
        weaponLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        weaponLabel.setTextFill(Color.WHITE);
        weaponLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 2 5 2 5;");
        weaponLabel.setLayoutX(10);
        weaponLabel.setLayoutY(130);

        root.getChildren().addAll(healthLabel, scoreLabel, arrowsLabel, weaponLabel);
    }

    public void updateHealth(int health) {
        healthLabel.setText("❤ Здоровье: " + health);

        if (health < 30) {
            healthLabel.setTextFill(Color.RED);
        } else if (health < 60) {
            healthLabel.setTextFill(Color.ORANGE);
        } else {
            healthLabel.setTextFill(Color.WHITE);
        }
    }

    public void addScore(int points) {
        score += points;
        scoreLabel.setText("⭐ Очки: " + score);
    }

    public void updateWeapon(boolean isBow) {
        if (isBow) {
            weaponLabel.setText("🏹 Лук");
        } else {
            weaponLabel.setText("⚔️ Меч");
        }
    }

    public void updateArrows(int arrowsLeft) {
        arrowsLabel.setText("🏹 Стрелы: " + arrowsLeft);
        if (arrowsLeft == 0) {
            arrowsLabel.setTextFill(Color.RED);
        } else {
            arrowsLabel.setTextFill(Color.WHITE);
        }
    }

    public int getScore() {
        return score;
    }
}