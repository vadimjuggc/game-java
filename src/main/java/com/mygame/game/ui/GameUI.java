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

    private Pane root;

    public GameUI(Pane root) {
        this.root = root;

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

    public void showWinScreen(Runnable onRestart, Runnable onMainMenu) {
        Pane overlay = new Pane();
        overlay.setPrefSize(800, 600);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.75);");

        Label winLabel = new Label("YOU WIN!");
        winLabel.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        winLabel.setTextFill(Color.GOLD);
        winLabel.setLayoutX(800 / 2.0 - 180);
        winLabel.setLayoutY(600 / 2.0 - 120);

        Label scoreInfo = new Label("Итоговый счёт: " + score);
        scoreInfo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreInfo.setTextFill(Color.WHITE);
        scoreInfo.setLayoutX(800 / 2.0 - 110);
        scoreInfo.setLayoutY(600 / 2.0 - 30);

        Label restartBtn = new Label("[ Restart ]");
        restartBtn.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        restartBtn.setTextFill(Color.LIGHTGREEN);
        restartBtn.setLayoutX(800 / 2.0 - 85);
        restartBtn.setLayoutY(600 / 2.0 + 40);
        restartBtn.setCursor(javafx.scene.Cursor.HAND);
        restartBtn.setOnMouseEntered(e -> restartBtn.setTextFill(Color.GREEN));
        restartBtn.setOnMouseExited(e -> restartBtn.setTextFill(Color.LIGHTGREEN));
        restartBtn.setOnMouseClicked(e -> onRestart.run());

        Label menuBtn = new Label("[ Main Menu ]");
        menuBtn.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        menuBtn.setTextFill(Color.LIGHTYELLOW);
        menuBtn.setLayoutX(800 / 2.0 - 105);
        menuBtn.setLayoutY(600 / 2.0 + 100);
        menuBtn.setCursor(javafx.scene.Cursor.HAND);
        menuBtn.setOnMouseEntered(e -> menuBtn.setTextFill(Color.YELLOW));
        menuBtn.setOnMouseExited(e -> menuBtn.setTextFill(Color.LIGHTYELLOW));
        menuBtn.setOnMouseClicked(e -> onMainMenu.run());

        overlay.getChildren().addAll(winLabel, scoreInfo, restartBtn, menuBtn);
        root.getChildren().add(overlay);
    }
}
