package com.mygame.game;

import com.mygame.game.entities.Platform;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private static final double PLAYER_HEIGHT = 40;
    private List<Platform> platforms;
    private double startX, startY;

    public Level() {
        platforms = new ArrayList<>();
        createLevel();
    }

    private void createLevel() {
        platforms = new ArrayList<>();

        // Пол
        platforms.add(new Platform(0, 550, 800, 50));

        // Платформы относительно пола
        // Формула: y = 550 - (высота_над_полом)

        // Низкие платформы (на 100 пикселей выше пола)
        platforms.add(new Platform(100, 550 - 80, 120, 20));   // y = 450
        platforms.add(new Platform(580, 550 - 80, 120, 20));   // y = 450

        // Средние платформы (на 150 пикселей выше пола)
        platforms.add(new Platform(340, 550 - 150, 120, 20));   // y = 400

        // Высокие платформы (на 200 пикселей выше пола)
        platforms.add(new Platform(450, 550 - 200, 100, 20));   // y = 350

        // Стартовая позиция (игрок стоит на полу)
        startX = 100;
        startY = 550 - PLAYER_HEIGHT;  // 510
    }

    public void addToPane(Pane root) {
        for (Platform platform : platforms) {
            root.getChildren().add(platform.getRectangle());
        }
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public double getStartX() { return startX; }
    public double getStartY() { return startY; }
}
