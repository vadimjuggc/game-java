package com.mygame.game.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;

public class Animation {

    private List<Image> frames;
    private ImageView target;
    private int currentFrame = 0;
    private double frameTime = 0;
    private double frameDuration = 0.1; // 100 мс на кадр
    private boolean playing = true;

    public Animation(List<Image> frames, ImageView target) {
        this.frames = frames;
        this.target = target;
        if (!frames.isEmpty()) {
            target.setImage(frames.get(0));
        }
    }

    public void update(double deltaTime) {
        if (!playing || frames.isEmpty()) return;

        frameTime += deltaTime;

        if (frameTime >= frameDuration) {
            frameTime = 0;
            currentFrame = (currentFrame + 1) % frames.size();
            target.setImage(frames.get(currentFrame));
        }
    }

    public void setFrameDuration(double seconds) {
        this.frameDuration = seconds;
    }

    public void play() {
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public void reset() {
        currentFrame = 0;
        if (!frames.isEmpty()) {
            target.setImage(frames.get(0));
        }
    }

    public List<Image> getFrames() {
        return frames;
    }
}