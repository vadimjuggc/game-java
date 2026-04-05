package com.mygame.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends  Application{
    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        GameWorld gameWorld = new GameWorld(root);

        Scene scene = new Scene(root);

        GameLoop gameLoop = new GameLoop(gameWorld);
        gameLoop.setupKeyHandlers(scene);

        stage.setTitle("Dead Cells Lite");
        stage.setScene(scene);
        stage.show();

        gameLoop.start();
    }

    static void main(String[] args) {
        launch(args);
    }
}
