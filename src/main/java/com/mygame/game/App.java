package com.mygame.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends  Application{
    @Override
    public void start(Stage stage) {
        // Корневой контейнер
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        // Игровой мир
        GameWorld gameWorld = new GameWorld(root);

        // Сцена
        Scene scene = new Scene(root);

        // Игровой цикл
        GameLoop gameLoop = new GameLoop(gameWorld);
        gameLoop.setupKeyHandlers(scene);

        // Окно
        stage.setTitle("Dead Cells Lite");
        stage.setScene(scene);
        stage.show();

        // Запуск
        gameLoop.start();
    }

    static void main(String[] args) {
        launch(args);
    }
}
