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
    private MediaPlayer swordSwingSound;
    private MediaPlayer swordHitSound;

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
            URL hitUrl = getClass().getResource("/sounds/character/hurt/hurt_sound.mp3");
            if (hitUrl != null) {
                Media hitMedia = new Media(hitUrl.toString());
                hitSound = new MediaPlayer(hitMedia);
            }

            URL attackUrl = getClass().getResource("/sounds/slime/slime_hit_sound.mp3");
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
            URL swordSwingUrl = getClass().getResource("/sounds/weapons/sword/sword-sound.mp3");
            if (swordSwingUrl != null) {
                Media swingMedia = new Media(swordSwingUrl.toString());
                swordSwingSound = new MediaPlayer(swingMedia);
            }

            URL swordHitUrl = getClass().getResource("/sounds/weapons/sword/violent-sword-sound.mp3");
            if (swordHitUrl != null) {
                Media hitMedia = new Media(swordHitUrl.toString());
                swordHitSound = new MediaPlayer(hitMedia);
            }
        } catch (Exception e) {
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


    public void playSwordSwingSound() {
        if (swordSwingSound != null) {
            swordSwingSound.stop();
            swordSwingSound.seek(javafx.util.Duration.ZERO);
            swordSwingSound.play();
        }
    }

    public void playSwordHitSound() {
        if (swordHitSound != null) {
            swordHitSound.stop();
            swordHitSound.seek(javafx.util.Duration.ZERO);
            swordHitSound.play();
        }
    }
    public void playBowShootSound() {
        if (bowShootSound != null) {
            bowShootSound.setVolume(1.0);
            bowShootSound.stop();
            bowShootSound.seek(javafx.util.Duration.ZERO);
            bowShootSound.play();
        }
    }

    public void restartBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.play();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
}