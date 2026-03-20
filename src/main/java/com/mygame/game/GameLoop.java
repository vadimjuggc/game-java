package com.mygame.game;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

public class GameLoop extends AnimationTimer{
    private long lastUpdate = 0;
    private GameWorld gameWorld;
    private Set<KeyCode> keysPressed = new HashSet<>();

    public GameLoop(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            keysPressed.add(event.getCode());
            // Для отладки
            if (event.getCode() == KeyCode.P) {
                System.out.println("Игрок позиция: " +
                        gameWorld.getPlayer().getX() + ", " + gameWorld.getPlayer().getY());
            }
        });

        scene.setOnKeyReleased(event -> {
            keysPressed.remove(event.getCode());
        });
    }

    @Override
    public void handle(long now) {
        if (lastUpdate == 0) {
            lastUpdate = now;
            return;
        }

        double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
        lastUpdate = now;

        // Передаем нажатые клавиши игроку
        gameWorld.getPlayer().handleInput(keysPressed);

        // Обновляем мир
        gameWorld.update(deltaTime);
    }
}
