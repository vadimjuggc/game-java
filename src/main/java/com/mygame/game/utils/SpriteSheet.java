package com.mygame.game.utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    private Image sheet;
    private int frameWidth;
    private int frameHeight;
    private List<Image> frames;
    private boolean loaded = false;

    public SpriteSheet(String path, int frameWidth, int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frames = new ArrayList<>();

        System.out.println("Загружаем: " + path);
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println("Файл НЕ НАЙДЕН! Путь: " + path);
            return;
        }
        System.out.println("Файл найден: " + url);

        this.sheet = new Image(url.toString());

        this.sheet.progressProperty().addListener((obs, old, val) -> {
            if (val.doubleValue() == 1.0 && !loaded) {
                loaded = true;
                System.out.println("Картинка загружена. Размер: " + sheet.getWidth() + "x" + sheet.getHeight());
                loadFrames();
                System.out.println("Загружено кадров: " + frames.size());
            }
        });
    }

    public void waitForLoad() {
        while (!loaded && sheet != null && sheet.getProgress() < 1.0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
        if (!loaded && sheet != null && sheet.getProgress() >= 1.0) {
            loaded = true;
            loadFrames();
        }
    }

    private void loadFrames() {
        if (sheet == null) return;

        int columns = (int) (sheet.getWidth() / frameWidth);
        int rows = (int) (sheet.getHeight() / frameHeight);

        System.out.println("Кадров в строке: " + columns + ", строк: " + rows);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                try {
                    WritableImage frame = new WritableImage(
                            sheet.getPixelReader(),
                            col * frameWidth,
                            row * frameHeight,
                            frameWidth,
                            frameHeight
                    );
                    frames.add(frame);
                } catch (Exception e) {
                    System.out.println("Ошибка при нарезке кадра [" + row + "][" + col + "]: " + e.getMessage());
                }
            }
        }
    }

    public Image getFrame(int index) {
        if (!loaded) waitForLoad();
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        }
        return null;
    }

    public List<Image> getFrames(int start, int count) {
        if (!loaded) waitForLoad();
        List<Image> result = new ArrayList<>();
        for (int i = start; i < start + count && i < frames.size(); i++) {
            result.add(frames.get(i));
        }
        return result;
    }

    public int getTotalFrames() {
        if (!loaded) waitForLoad();
        return frames.size();
    }
}