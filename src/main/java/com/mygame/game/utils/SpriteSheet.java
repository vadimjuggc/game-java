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

    public SpriteSheet(String path, int frameWidth, int frameHeight) {
        // Загружаем картинку через getResource (правильный способ)
        this.sheet = new Image(getClass().getResourceAsStream(path));
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frames = new ArrayList<>();

        loadFrames();
    }

    private void loadFrames() {
        int columns = (int) (sheet.getWidth() / frameWidth);
        int rows = (int) (sheet.getHeight() / frameHeight);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                WritableImage frame = new WritableImage(
                        sheet.getPixelReader(),
                        col * frameWidth,
                        row * frameHeight,
                        frameWidth,
                        frameHeight
                );
                frames.add(frame);
            }
        }
    }

    public Image getFrame(int index) {
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        }
        return null;
    }

    // ========== НОВЫЙ МЕТОД ==========
    public List<Image> getFrames(int start, int count) {
        List<Image> result = new ArrayList<>();
        for (int i = start; i < start + count && i < frames.size(); i++) {
            result.add(frames.get(i));
        }
        return result;
    }
    // =================================

    public int getTotalFrames() {
        return frames.size();
    }
}
