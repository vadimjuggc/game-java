package com.mygame.game.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {

    private MediaPlayer hitSound;      // звук при получении урона
    private MediaPlayer attackSound;   // звук при атаке

    public SoundManager() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            // Загружаем звук удара (игрок получает урон)
            URL hitUrl = getClass().getResource("/sounds/ддед.mp3");
            if (hitUrl != null) {
                Media hitMedia = new Media(hitUrl.toString());
                hitSound = new MediaPlayer(hitMedia);
                System.out.println("Звук удара загружен");
            } else {
                System.out.println("Звук удара не найден: /sounds/hit.wav");
            }

            URL attackUrl = getClass().getResource("/sounds/arsen-audio.mp3");
            if (attackUrl != null) {
                Media attackMedia = new Media(attackUrl.toString());
                attackSound = new MediaPlayer(attackMedia);
                System.out.println("Звук атаки загружен");
            } else {
                System.out.println("Звук атаки не найден: /sounds/attack.wav");
            }
            // ======================================

        } catch (Exception e) {
            System.out.println("Ошибка загрузки звука: " + e.getMessage());
        }
    }

    public void playHitSound() {
        if (hitSound != null) {
            hitSound.stop();
            hitSound.seek(javafx.util.Duration.ZERO);
            hitSound.play();
        }
    }

    // ========== НОВЫЙ МЕТОД ==========
    public void playAttackSound() {
        if (attackSound != null) {
            attackSound.stop();
            attackSound.seek(javafx.util.Duration.ZERO);
            attackSound.play();
        }
    }
    // =================================
}