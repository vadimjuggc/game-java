package com.mygame.game;

import com.mygame.game.ui.MainMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    private Stage primaryStage;
    private GameWorld gameWorld;
    private GameLoop gameLoop;
    private Scene mainMenuScene;
    private MainMenu mainMenu;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        mainMenu = new MainMenu();
        mainMenuScene = mainMenu.createMenuScene(stage, this::startGame);

        stage.setTitle("Cinder Soul");
        stage.setScene(mainMenuScene);
        stage.show();
    }

    private void startGame() {
        if (mainMenu != null) {
            mainMenu.stopMenuMusic();
        }

        Pane root = new Pane();
        root.setPrefSize(800, 600);

        gameWorld = new GameWorld(root, () -> {
            if (gameLoop != null) {
                gameLoop.stop();
            }
            com.mygame.game.utils.SoundManager.getInstance().stopBackgroundMusic();
            gameWorld = null;
            gameLoop = null;
            if (mainMenu != null) {
                mainMenu.playMenuMusic();
            }
            primaryStage.setScene(mainMenuScene);
        });

        gameLoop = new GameLoop(gameWorld);
        Scene gameScene = new Scene(root);
        gameLoop.setupKeyHandlers(gameScene);

        primaryStage.setScene(gameScene);
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}