package com.mygame.game;

import com.mygame.game.entities.Platform;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private List<Platform> platforms;
    private double startX, startY;

    public Level ()
    {
       platforms = new ArrayList<>();
       createLevel();
    }

    private void createLevel()
    {
        platforms.add(new Platform(0, 550, 800, 50));

        // Платформа слева вверху
        platforms.add(new Platform(100, 400, 150, 20));

        // Платформа справа вверху
        platforms.add(new Platform(550, 400, 150, 20));

        // Средняя платформа
        platforms.add(new Platform(300, 250, 200, 20));

        // Стартовая позиция игрока
        startX = 100;
        startY = 500;
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
