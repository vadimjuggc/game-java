package com.mygame.game.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {

    private static SoundManager instance;
    private MediaPlayer clickSound;
    private MediaPlayer hitSound;
    private MediaPlayer attackSound;
    private MediaPlayer bowShootSound;
    private MediaPlayer backgroundMusic;
    private MediaPlayer swordSwingSound;
    private MediaPlayer swordHitSound;
    private MediaPlayer healSound;
    private MediaPlayer slimeAttackSound;
    private MediaPlayer gameOverSound;
    private MediaPlayer youWinSound;
    private MediaPlayer doubleKillSound;
    private MediaPlayer tripleKillSound;
    private MediaPlayer ultraKillSound;

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

            URL clickUrl = getClass().getResource("/sounds/ui_sounds/click.mp3");
            if (clickUrl != null) {
                Media clickMedia = new Media(clickUrl.toString());
                clickSound = new MediaPlayer(clickMedia);
            }

            URL doubleUrl = getClass().getResource("/sounds/ui_sounds/double_kill.mp3");
            if (doubleUrl != null) {
                doubleKillSound = new MediaPlayer(new Media(doubleUrl.toString()));
            }

            URL tripleUrl = getClass().getResource("/sounds/ui_sounds/triple_kill.mp3");
            if (tripleUrl != null) {
                tripleKillSound = new MediaPlayer(new Media(tripleUrl.toString()));
            }

            URL ultraUrl = getClass().getResource("/sounds/ui_sounds/ultra_kill.mp3");
            if (ultraUrl != null) {
                ultraKillSound = new MediaPlayer(new Media(ultraUrl.toString()));
            }

            URL slimeAttackUrl = getClass().getResource("/sounds/slime/slime_attack_sound.mp3");
            if (slimeAttackUrl != null) {
                Media slimeAttackMedia = new Media(slimeAttackUrl.toString());
                slimeAttackSound = new MediaPlayer(slimeAttackMedia);
            }

            URL youWinUrl = getClass().getResource("/sounds/ui_sounds/congratulations.mp3");
            if (youWinUrl != null) {
                Media youWinMedia = new Media(youWinUrl.toString());
                youWinSound = new MediaPlayer(youWinMedia);
            }

            URL attackUrl = getClass().getResource("/sounds/slime/slime_hit_sound.mp3");
            if (attackUrl != null) {
                Media attackMedia = new Media(attackUrl.toString());
                attackSound = new MediaPlayer(attackMedia);
            }

            URL healUrl = getClass().getResource("/sounds/character/heal/heal_sound.mp3");
            if (healUrl != null) {
                Media healMedia = new Media(healUrl.toString());
                healSound = new MediaPlayer(healMedia);
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

            URL gameOverUrl = getClass().getResource("/sounds/ui_sounds/game_over_sound.mp3");
            if (gameOverUrl != null) {
                Media gameOverMedia = new Media(gameOverUrl.toString());
                gameOverSound = new MediaPlayer(gameOverMedia);
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

    public void playHealSound() {
        if (healSound != null) {
            healSound.stop();
            healSound.seek(javafx.util.Duration.ZERO);
            healSound.play();
        }
    }

    public void playSlimeAttackSound() {
        if (slimeAttackSound != null) {
            slimeAttackSound.stop();
            slimeAttackSound.seek(javafx.util.Duration.ZERO);
            slimeAttackSound.play();
        }
    }

    public void playComboSound(int combo) {
        MediaPlayer sound = null;
        if (combo >= 5) sound = ultraKillSound;
        else if (combo == 3 || combo == 4) sound = tripleKillSound;
        else if (combo == 2) sound = doubleKillSound;

        if (sound != null) {
            sound.stop();
            sound.seek(javafx.util.Duration.ZERO);
            sound.play();
        }
    }

    public void playClickSound() {
        if (clickSound != null) {
            clickSound.stop();
            clickSound.seek(javafx.util.Duration.ZERO);
            clickSound.play();
        }
    }

    public void playAttackSound() {
        if (attackSound != null) {
            attackSound.stop();
            attackSound.seek(javafx.util.Duration.ZERO);
            attackSound.play();
        }
    }

    public void playYouWinSound() {
        if (youWinSound != null) {
            youWinSound.stop();
            youWinSound.seek(javafx.util.Duration.ZERO);
            youWinSound.play();
        }
    }

    public void playGameOverSound() {
        if (gameOverSound != null) {
            gameOverSound.stop();
            gameOverSound.seek(javafx.util.Duration.ZERO);
            gameOverSound.play();
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