package com.mygame.game;

import com.mygame.game.entities.Platform;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private static final double PLAYER_HEIGHT = 40;
    public static final double WORLD_WIDTH = 2400;
    public static final double WORLD_HEIGHT = 600;

    private List<Platform> platforms;
    private double startX, startY;

    public Level() {
        platforms = new ArrayList<>();
        createLevel();
    }

    private void createLevel() {
        platforms = new ArrayList<>();

        platforms.add(new Platform(0, 550, 800, 50));
        platforms.add(new Platform(100, 470, 120, 20));
        platforms.add(new Platform(580, 470, 120, 20));
        platforms.add(new Platform(340, 400, 120, 20, true));
        platforms.add(new Platform(450, 350, 100, 20, true));

        platforms.add(new Platform(800, 550, 800, 50));
        platforms.add(new Platform(850, 470, 130, 20));
        platforms.add(new Platform(1050, 400, 120, 20, true));
        platforms.add(new Platform(1200, 460, 100, 20));
        platforms.add(new Platform(1350, 350, 140, 20, true));
        platforms.add(new Platform(1500, 470, 110, 20));

        platforms.add(new Platform(1600, 550, 800, 50));
        platforms.add(new Platform(1650, 460, 120, 20));
        platforms.add(new Platform(1820, 380, 130, 20, true));
        platforms.add(new Platform(1980, 460, 100, 20));
        platforms.add(new Platform(2100, 340, 150, 20, true));
        platforms.add(new Platform(2250, 470, 110, 20));

        startX = 100;
        startY = 550 - PLAYER_HEIGHT;
    }

    public void addToPane(Pane root) {
        for (Platform platform : platforms) {
            root.getChildren().add(platform.getCanvas());
            root.getChildren().add(platform.getRectangle());
        }
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public double getStartX() { return startX; }
    public double getStartY() { return startY; }
}
