package com.mygame.game.ui;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import java.net.URL;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.Node;

public class MainMenu {

    private MediaPlayer menuMusic;

    public Scene createMenuScene(Stage stage, Runnable startGameCallback) {
        StackPane root = new StackPane();

        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/ui/background_mountian.gif"));
        if (backgroundImage != null) {
            ImageView background = new ImageView(backgroundImage);
            background.setFitWidth(800);
            background.setFitHeight(600);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        } else {
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        }

        Image logoImage = new Image(getClass().getResourceAsStream("/images/ui/mainMenu/CinderSoul.png"));
        ImageView logoView = null;
        if (logoImage != null) {
            logoView = new ImageView(logoImage);
            logoView.setFitWidth(550);
            logoView.setFitHeight(239);
            logoView.setPreserveRatio(true);
        }

        Image playImage = new Image(getClass().getResourceAsStream("/images/ui/mainMenu/play_button.png"));
        ImageView playImageView = new ImageView(playImage);
        playImageView.setFitWidth(170);
        playImageView.setFitHeight(212);
        playImageView.setPreserveRatio(true);

        Button playButton = new Button();
        playButton.setGraphic(playImageView);
        playButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        playButton.setCursor(javafx.scene.Cursor.HAND);

        playButton.setOnMouseEntered(e -> {
            playButton.setStyle("-fx-effect: dropshadow(gaussian, gray, 20, 0.8, 0, 0); -fx-background-color: transparent;");
            playButton.setScaleX(1.1);
            playButton.setScaleY(1.1);
        });
        playButton.setOnMouseExited(e -> {
            playButton.setStyle("-fx-effect: null; -fx-background-color: transparent;");
            playButton.setScaleX(1.0);
            playButton.setScaleY(1.0);
        });

        playButton.setOnAction(e -> {
            stopMenuMusic();
            startGameCallback.run();
        });

        Image exitImage = new Image(getClass().getResourceAsStream("/images/ui/mainMenu/exit_button.png"));
        ImageView exitImageView = new ImageView(exitImage);
        exitImageView.setFitWidth(60);
        exitImageView.setFitHeight(73);
        exitImageView.setPreserveRatio(true);

        Button exitButton = new Button();
        exitButton.setGraphic(exitImageView);
        exitButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        exitButton.setCursor(javafx.scene.Cursor.HAND);

        exitButton.setOnMouseEntered(e -> {
            exitButton.setStyle("-fx-effect: dropshadow(gaussian, red, 15, 0.7, 0, 0); -fx-background-color: transparent;");
            exitButton.setScaleX(1.1);
            exitButton.setScaleY(1.1);
        });
        exitButton.setOnMouseExited(e -> {
            exitButton.setStyle("-fx-effect: null; -fx-background-color: transparent;");
            exitButton.setScaleX(1.0);
            exitButton.setScaleY(1.1);
        });

        exitButton.setOnAction(e -> System.exit(0));

        HBox exitContainer = new HBox();
        exitContainer.setAlignment(Pos.BOTTOM_LEFT);
        exitContainer.setPadding(new Insets(20));
        exitContainer.getChildren().add(exitButton);
        exitContainer.setPickOnBounds(false);

        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        if (logoView != null) {
            centerBox.getChildren().add(logoView);
        }
        centerBox.getChildren().add(playButton);
        centerBox.setPickOnBounds(false);

        root.getChildren().addAll(centerBox, exitContainer);

        playMenuMusic();

        return new Scene(root, 800, 600);
    }

    public void playMenuMusic() {
        try {
            if (menuMusic != null) {
                menuMusic.stop();
                menuMusic.dispose();
                menuMusic = null;
            }

            URL musicUrl = getClass().getResource("/sounds/sprinkles.mp3");
            if (musicUrl != null) {
                Media media = new Media(musicUrl.toString());
                menuMusic = new MediaPlayer(media);
                menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
                menuMusic.setVolume(0.4);
                menuMusic.play();
            }
        } catch (Exception e) {
            System.out.println("Ошибка воспроизведения музыки: " + e.getMessage());
        }
    }

    public void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
        }
    }
}
