package com.mygame.game.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {

    private static SoundManager instance;

    private MediaPlayer hitSound;
    private MediaPlayer attackSound;
    private MediaPlayer bowShootSound;
    private MediaPlayer backgroundMusic;

    private SoundManager() {
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            URL hitUrl = getClass().getResource("/sounds/ded.mp3");
            if (hitUrl != null) {
                Media hitMedia = new Media(hitUrl.toString());
                hitSound = new MediaPlayer(hitMedia);
            }

            URL attackUrl = getClass().getResource("/sounds/arsen-audio.mp3");
            if (attackUrl != null) {
                Media attackMedia = new Media(attackUrl.toString());
                attackSound = new MediaPlayer(attackMedia);
            }

            URL bowUrl = getClass().getResource("/sounds/arrow.wav");
            if (bowUrl != null) {
                Media bowMedia = new Media(bowUrl.toString());
                bowShootSound = new MediaPlayer(bowMedia);
            }

            URL bgUrl = getClass().getResource("/sounds/background.mp3");
            if (bgUrl != null) {
                Media bgMedia = new Media(bgUrl.toString());
                backgroundMusic = new MediaPlayer(bgMedia);
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.setVolume(0.3);
                backgroundMusic.play();
            }
        } catch (Exception e) {
            // игнорируем
        }
    }

    public void playHitSound() {
        if (hitSound != null) {
            hitSound.stop();
            hitSound.seek(javafx.util.Duration.ZERO);
            hitSound.play();
        }
    }

    public void playAttackSound() {
        if (attackSound != null) {
            attackSound.stop();
            attackSound.seek(javafx.util.Duration.ZERO);
            attackSound.play();
        }
    }

    public void playBowShootSound() {
        System.out.println("playBowShootSound вызван, bowShootSound=" + bowShootSound);
        if (bowShootSound != null) {
            bowShootSound.setVolume(1.0);  // максимум
            bowShootSound.stop();
            bowShootSound.seek(javafx.util.Duration.ZERO);
            bowShootSound.play();
            System.out.println("Звук проигран");
        } else {
            System.out.println("bowShootSound == NULL, звук не загружен!");
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
}