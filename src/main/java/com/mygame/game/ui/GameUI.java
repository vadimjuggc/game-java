package com.mygame.game.ui;

import com.mygame.game.utils.SoundManager;
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
    private Label timerLabel;
    private Label killsLabel;
    private int comboCount = 0;
    private Rectangle arrowBar;
    private static final int MAX_ARROWS = 10;
    private ImageView weaponPopupIcon;
    private Label weaponPopupLabel;
    private javafx.animation.FadeTransition weaponFade;
    private int kills = 0;

    private static final int MAX_HEALTH = 100;
    private static final double BAR_WIDTH = 120;
    private static final double BAR_HEIGHT = 18;

    private Pane root;

    public GameUI(Pane root) {
        this.root = root;

        ImageView heartLabel = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/battle_icons/heart_icon.png")));
        heartLabel.setFitWidth(32);
        heartLabel.setFitHeight(32);
        heartLabel.setLayoutX(8);
        heartLabel.setLayoutY(8);

        healthBarBg = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        healthBarBg.setFill(Color.rgb(30, 10, 10, 0.85));
        healthBarBg.setArcWidth(8);
        healthBarBg.setArcHeight(8);
        healthBarBg.setLayoutX(48);
        healthBarBg.setLayoutY(13);

        healthBar = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        healthBar.setFill(Color.rgb(160, 20, 20));
        healthBar.setArcWidth(8);
        healthBar.setArcHeight(8);
        healthBar.setLayoutX(48);
        healthBar.setLayoutY(13);

        healthValueLabel = new Label("100");
        healthValueLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 13));
        healthValueLabel.setTextFill(Color.rgb(220, 200, 200));
        healthValueLabel.setStyle("-fx-effect: dropshadow(gaussian, #000000, 3, 0.8, 0, 0);");
        healthValueLabel.setLayoutX(48 + BAR_WIDTH / 2 - 12);
        healthValueLabel.setLayoutY(13);

        ImageView arrowIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/battle_icons/arrow_icon.png")));
        arrowIcon.setFitWidth(32);
        arrowIcon.setFitHeight(32);
        arrowIcon.setLayoutX(8);
        arrowIcon.setLayoutY(48);

        Rectangle arrowBarBg = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        arrowBarBg.setFill(Color.rgb(10, 10, 30, 0.85));
        arrowBarBg.setArcWidth(8);
        arrowBarBg.setArcHeight(8);
        arrowBarBg.setLayoutX(48);
        arrowBarBg.setLayoutY(53);

        arrowBar = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        arrowBar.setFill(Color.rgb(50, 60, 120));
        arrowBar.setArcWidth(8);
        arrowBar.setArcHeight(8);
        arrowBar.setLayoutX(48);
        arrowBar.setLayoutY(53);

        arrowsLabel = new Label("10");
        arrowsLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 13));
        arrowsLabel.setTextFill(Color.rgb(200, 200, 220));
        arrowsLabel.setStyle("-fx-effect: dropshadow(gaussian, #000000, 3, 0.8, 0, 0);");
        arrowsLabel.setLayoutX(48 + BAR_WIDTH / 2 - 8);
        arrowsLabel.setLayoutY(53);

        ImageView scoreIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/battle_icons/score_icon.png")));
        scoreIcon.setFitHeight(40);
        scoreIcon.setPreserveRatio(true);
        scoreIcon.setLayoutX(8);
        scoreIcon.setLayoutY(88);

        scoreLabel = new Label("0");
        scoreLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 18));
        scoreLabel.setTextFill(Color.color(0.7, 0.6, 0.3));
        scoreLabel.setStyle("-fx-effect: dropshadow(gaussian, #000000, 4, 0.8, 0, 0);");
        scoreLabel.setLayoutX(75);
        scoreLabel.setLayoutY(98);

        comboLabel = new Label("");
        comboLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 28));
        comboLabel.setTextFill(Color.rgb(180, 50, 50));
        comboLabel.setStyle("-fx-effect: dropshadow(gaussian, #000000, 6, 0.9, 0, 0);");
        comboLabel.setLayoutX(800 / 2.0 - 60);
        comboLabel.setLayoutY(80);
        comboLabel.setVisible(false);

        weaponPopupIcon = new ImageView();
        weaponPopupIcon.setFitWidth(32);
        weaponPopupIcon.setFitHeight(32);
        weaponPopupIcon.setLayoutX(800 / 2.0 - 70);
        weaponPopupIcon.setLayoutY(520);
        weaponPopupIcon.setOpacity(0);

        weaponPopupLabel = new Label("⚔️ Меч");
        weaponPopupLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 22));
        weaponPopupLabel.setTextFill(Color.rgb(200, 180, 120));
        weaponPopupLabel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.35); " +
                        "-fx-padding: 6 16 6 16; " +
                        "-fx-background-radius: 8;"
        );
        weaponPopupLabel.setLayoutX(800 / 2.0 - 60);
        weaponPopupLabel.setLayoutY(120);
        weaponPopupLabel.setOpacity(0);

        timerLabel = new Label("⏱ 0:00");
        timerLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 20));
        timerLabel.setTextFill(Color.rgb(210, 200, 170));
        timerLabel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.25); " +
                        "-fx-padding: 4 12 4 12; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, #000000, 5, 0.8, 0, 0);"
        );
        timerLabel.setLayoutX(800 - 120);
        timerLabel.setLayoutY(10);

        killsLabel = new Label("☠ 0");
        killsLabel.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 20));
        killsLabel.setTextFill(Color.rgb(200, 80, 80));
        killsLabel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.25); " +
                        "-fx-padding: 4 12 4 12; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, #000000, 5, 0.8, 0, 0);"
        );
        killsLabel.setLayoutX(800 - 120);
        killsLabel.setLayoutY(44);

        root.getChildren().addAll(
                heartLabel, healthBarBg, healthBar, healthValueLabel,
                arrowIcon, arrowBarBg, arrowBar, arrowsLabel,
                scoreIcon, scoreLabel,
                weaponPopupLabel, comboLabel, timerLabel, killsLabel);
    }

    public void updateHealth(int health) {
        double ratio = Math.max(0, (double) health / MAX_HEALTH);
        healthBar.setWidth(BAR_WIDTH * ratio);
        healthValueLabel.setText(String.valueOf(health));

        if (health < 30) {
            healthBar.setFill(Color.rgb(100, 10, 10));
        } else if (health < 60) {
            healthBar.setFill(Color.rgb(130, 15, 15));
        } else {
            healthBar.setFill(Color.rgb(160, 20, 20));
        }
    }

    public void addScore(int points) {
        score += points;
        scoreLabel.setText(String.valueOf(score));
    }

    public void updateWeapon(boolean isBow) {
    }

    public void showWeaponPopup(boolean isBow) {
        if (weaponFade != null) weaponFade.stop();

        weaponPopupLabel.setText(isBow ? "🏹 Лук" : "⚔️ Меч");
        weaponPopupLabel.setOpacity(1.0);

        weaponFade = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.5), weaponPopupLabel);
        weaponFade.setFromValue(1.0);
        weaponFade.setToValue(0.0);
        weaponFade.setDelay(javafx.util.Duration.seconds(0.8));
        weaponFade.play();
    }

    public void updateArrows(int arrowsLeft) {
        double ratio = Math.max(0, (double) arrowsLeft / MAX_ARROWS);
        arrowBar.setWidth(BAR_WIDTH * ratio);
        arrowsLabel.setText(String.valueOf(arrowsLeft));

        if (arrowsLeft == 0) {
            arrowBar.setFill(Color.rgb(80, 10, 10));
        } else if (arrowsLeft <= 3) {
            arrowBar.setFill(Color.rgb(80, 40, 10));
        } else {
            arrowBar.setFill(Color.rgb(50, 60, 120));
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

        ImageView winLabel = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/you_win.png")));
        winLabel.setFitWidth(556);
        winLabel.setFitHeight(143);
        winLabel.setPreserveRatio(false);
        winLabel.setLayoutX(800 / 2.0 - 278);
        winLabel.setLayoutY(600 / 2.0 - 160);

        ImageView restartBtn = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/win_restart.png")));
        restartBtn.setFitWidth(240);
        restartBtn.setFitHeight(90);
        restartBtn.setLayoutX(800 / 2.0 - 120);
        restartBtn.setLayoutY(600 / 2.0 + 50);
        restartBtn.setCursor(javafx.scene.Cursor.HAND);
        restartBtn.setOnMouseEntered(e -> restartBtn.setOpacity(0.75));
        restartBtn.setOnMouseExited(e -> restartBtn.setOpacity(1.0));
        restartBtn.setOnMouseClicked(e -> {
            SoundManager.getInstance().playClickSound();
            onRestart.run();
        });

        ImageView menuBtn = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/win_main_menu.png")));
        menuBtn.setFitWidth(240);
        menuBtn.setFitHeight(90);
        menuBtn.setLayoutX(800 / 2.0 - 120);
        menuBtn.setLayoutY(600 / 2.0 + 155);
        menuBtn.setCursor(javafx.scene.Cursor.HAND);
        menuBtn.setOnMouseEntered(e -> menuBtn.setOpacity(0.75));
        menuBtn.setOnMouseExited(e -> menuBtn.setOpacity(1.0));
        menuBtn.setOnMouseClicked(e -> {
            SoundManager.getInstance().playClickSound();
            onMainMenu.run();
        });
        overlay.getChildren().addAll(winLabel, restartBtn, menuBtn);
        root.getChildren().add(overlay);
    }

    public void updateTimer(double totalSeconds) {
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;
        timerLabel.setText(String.format("⏱ %d:%02d", minutes, seconds));
    }

    public void addKill() {
        kills++;
        killsLabel.setText("☠ " + kills);
        killsLabel.setTextFill(Color.rgb(255, 120, 120));
        javafx.animation.PauseTransition flash = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
        flash.setOnFinished(e -> killsLabel.setTextFill(Color.rgb(200, 80, 80)));
        flash.play();
    }

    public int getKills() {
        return kills;
    }
}