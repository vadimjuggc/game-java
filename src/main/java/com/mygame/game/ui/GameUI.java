package com.mygame.game.ui;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameUI {

    private Label scoreLabel;
    private Label arrowsLabel;
    private Label weaponLabel;
    private Label healthValueLabel;
    private Rectangle healthBar;
    private Rectangle healthBarBg;
    private int score = 0;
    private Label comboLabel;
    private int comboCount = 0;
    private Rectangle arrowBar;
    private static final int MAX_ARROWS = 10;

    private static final int MAX_HEALTH = 100;
    private static final double BAR_WIDTH = 120;
    private static final double BAR_HEIGHT = 18;

    private Pane root;

    public GameUI(Pane root) {
        this.root = root;

        ImageView heartLabel = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/ui/battle_icons/heart_icon.png")));
        heartLabel.setFitWidth(32);
        heartLabel.setFitHeight(32);
        heartLabel.setLayoutX(8);
        heartLabel.setLayoutY(8);

        healthBarBg = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        healthBarBg.setFill(Color.rgb(60, 0, 0, 0.8));
        healthBarBg.setArcWidth(8);
        healthBarBg.setArcHeight(8);
        healthBarBg.setLayoutX(38);
        healthBarBg.setLayoutY(13);

        healthBar = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        healthBar.setFill(Color.LIMEGREEN);
        healthBar.setArcWidth(8);
        healthBar.setArcHeight(8);
        healthBar.setLayoutX(38);
        healthBar.setLayoutY(13);

        healthValueLabel = new Label("100");
        healthValueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        healthValueLabel.setTextFill(Color.WHITE);
        healthValueLabel.setLayoutX(38 + BAR_WIDTH / 2 - 12);
        healthValueLabel.setLayoutY(13);

        ImageView scoreIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/battle_icons/score_icon.png")));
        scoreIcon.setFitHeight(40);
        scoreIcon.setPreserveRatio(true);
        scoreIcon.setLayoutX(8);
        scoreIcon.setLayoutY(48);

        scoreLabel = new Label("0");
        scoreLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(Color.color(0.85, 0.75, 0.4));
        scoreLabel.setStyle("-fx-effect: dropshadow(gaussian, #000000, 4, 0.8, 0, 0);");
        scoreLabel.setLayoutX(75);
        scoreLabel.setLayoutY(53);

        ImageView arrowIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/battle_icons/arrow_icon.png")));
        arrowIcon.setFitWidth(32);
        arrowIcon.setFitHeight(32);
        arrowIcon.setLayoutX(8);
        arrowIcon.setLayoutY(88);

        Rectangle arrowBarBg = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        arrowBarBg.setFill(Color.rgb(0, 0, 60, 0.8));
        arrowBarBg.setArcWidth(8);
        arrowBarBg.setArcHeight(8);
        arrowBarBg.setLayoutX(38);
        arrowBarBg.setLayoutY(93);

        arrowBar = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        arrowBar.setFill(Color.CORNFLOWERBLUE);
        arrowBar.setArcWidth(8);
        arrowBar.setArcHeight(8);
        arrowBar.setLayoutX(38);
        arrowBar.setLayoutY(93);

        arrowsLabel = new Label("10");
        arrowsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        arrowsLabel.setTextFill(Color.WHITE);
        arrowsLabel.setLayoutX(38 + BAR_WIDTH / 2 - 8);
        arrowsLabel.setLayoutY(93);

        weaponLabel = new Label("⚔️ Меч");
        weaponLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        weaponLabel.setTextFill(Color.WHITE);
        weaponLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 2 5 2 5;");
        weaponLabel.setLayoutX(10);
        weaponLabel.setLayoutY(125);

        comboLabel = new Label("");
        comboLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 28));
        comboLabel.setTextFill(Color.ORANGERED);
        comboLabel.setStyle("-fx-effect: dropshadow(gaussian, #000000, 6, 0.9, 0, 0);");
        comboLabel.setLayoutX(800 / 2.0 - 60);
        comboLabel.setLayoutY(80);
        comboLabel.setVisible(false);

        root.getChildren().addAll(heartLabel, healthBarBg, healthBar, healthValueLabel,
                arrowIcon, arrowBarBg, arrowBar, arrowsLabel,
                scoreIcon, scoreLabel,
                weaponLabel, comboLabel);
    }

    public void updateHealth(int health) {
        double ratio = Math.max(0, (double) health / MAX_HEALTH);
        healthBar.setWidth(BAR_WIDTH * ratio);
        healthValueLabel.setText(String.valueOf(health));

        if (health < 30) {
            healthBar.setFill(Color.RED);
        } else if (health < 60) {
            healthBar.setFill(Color.ORANGE);
        } else {
            healthBar.setFill(Color.LIMEGREEN);
        }
    }

    public void addScore(int points) {
        score += points;
        scoreLabel.setText(String.valueOf(score));
    }

    public void updateWeapon(boolean isBow) {
        if (isBow) {
            weaponLabel.setText("🏹 Лук");
        } else {
            weaponLabel.setText("⚔️ Меч");
        }
    }

    public void updateArrows(int arrowsLeft) {
        double ratio = Math.max(0, (double) arrowsLeft / MAX_ARROWS);
        arrowBar.setWidth(BAR_WIDTH * ratio);
        arrowsLabel.setText(String.valueOf(arrowsLeft));

        if (arrowsLeft == 0) {
            arrowBar.setFill(Color.RED);
        } else if (arrowsLeft <= 3) {
            arrowBar.setFill(Color.ORANGE);
        } else {
            arrowBar.setFill(Color.CORNFLOWERBLUE);
        }
    }

    public int getScore() {
        return score;
    }

    public void showCombo(int combo, int multiplier) {
        comboLabel.setVisible(true);
        if (combo == 1) {
            comboLabel.setText("");
            comboLabel.setVisible(false);
        } else if (multiplier >= 3) {
            comboLabel.setText("x" + multiplier + " COMBO " + combo + "!");
            comboLabel.setTextFill(Color.GOLD);
        } else {
            comboLabel.setText("x" + multiplier + " COMBO " + combo);
            comboLabel.setTextFill(Color.ORANGERED);
        }
    }

    public void resetCombo() {
        comboLabel.setVisible(false);
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