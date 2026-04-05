package com.mygame.game.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;

public class FrameAnimation {

    private List<Image> frames;
    private ImageView target;
    private int currentFrame = 0;
    private double frameTime = 0;
    private double frameDuration = 0.1;
    private boolean playing = true;
    private boolean looping = true;
    private boolean finished = false;

    public FrameAnimation(List<Image> frames, ImageView target) {
        this.frames = frames;
        this.target = target;
        if (!frames.isEmpty() && frames.get(0) != null) {
            target.setImage(frames.get(0));
        }
    }

    public void update(double deltaTime) {
        if (!playing || frames.isEmpty()) return;

        frameTime += deltaTime;

        if (frameTime >= frameDuration) {
            frameTime = 0;
            currentFrame++;

            if (currentFrame >= frames.size()) {
                if (looping) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.size() - 1;
                    playing = false;
                    finished = true;
                }
            }

            Image nextFrame = frames.get(currentFrame);
            if (nextFrame != null) {
                target.setImage(nextFrame);
            }
        }
    }

    public void setFrameDuration(double seconds) {
        this.frameDuration = seconds;
    }

    public void setLoop(boolean loop) {
        this.looping = loop;
    }

    public void play() {
        playing = true;
        finished = false;
    }

    public void stop() {
        playing = false;
    }

    public void reset() {
        currentFrame = 0;
        frameTime = 0;
        finished = false;
        playing = true;
        if (!frames.isEmpty() && frames.get(0) != null) {
            target.setImage(frames.get(0));
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFrameImmediately(int index) {
        if (index >= 0 && index < frames.size() && frames.get(index) != null) {
            target.setImage(frames.get(index));
            currentFrame = index;
        }
    }
}