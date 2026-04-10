package com.mygame.game;

import com.mygame.game.entities.Entity;
import javafx.scene.layout.Pane;
import com.mygame.game.entities.Player;
import com.mygame.game.entities.Enemy;
import com.mygame.game.entities.Platform;
import com.mygame.game.utils.SoundManager;
import com.mygame.game.ui.GameUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameWorld {

    private Pane root;
    private Player player;
    private List<Enemy> enemies;
    private Level level;
    private GameUI gameUI;
    private SoundManager soundManager;  // ← добавить
    private Random random = new Random();
    private double spawnTimer = 0;
    private boolean gameOver = false;

    public GameWorld(Pane root) {
        this.root = root;
        this.enemies = new ArrayList<>();

        this.soundManager = new SoundManager();
        this.root = root;
        this.enemies = new ArrayList<>();

        // Создаем уровень
        level = new Level();
        level.addToPane(root);

        // Создаем игрока на стартовой позиции
        this.player = new Player(level.getStartX(), level.getStartY());
        root.getChildren().add(player.getSprite());
        root.getChildren().add(player.getWeaponSprite());
        this.gameUI = new GameUI(root);

        // Создаем первого врага
        spawnEnemy();
    }

    public void update(double deltaTime) {
        if (gameOver) return;

        // Сохраняем старую позицию для обработки столкновений
        double oldY = player.getY();

        // Обновляем игрока
        player.update(deltaTime);

        // Проверяем столкновения с платформами (для игрока)
        checkPlatformCollisions(player, oldY);

        // Обновляем врагов и проверяем их столкновения с платформами
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            if (enemy.isDead()) {
                gameUI.addScore(10);
                enemyIterator.remove();
                root.getChildren().remove(enemy.getSprite());
                continue;
            }

            double oldEnemyY = enemy.getY();
            enemy.update(deltaTime);
            checkPlatformCollisions(enemy, oldEnemyY);
        }

        // Проверяем столкновения между игроком и врагами
        checkCombatCollisions();

        spawnTimer += deltaTime;
        if (spawnTimer > 5.0) { // каждые 5 секунд
            spawnTimer = 0;
            spawnEnemy();
        }
        gameUI.updateHealth(player.getHealth());
        if (player.isDead() && !gameOver) {
            gameOver = true;
            showGameOverScreen();
            System.out.println("GAME OVER!");
        }
    }

    public void restart() {
        root.getChildren().clear();
        gameOver = false;
        spawnTimer = 0;
        level = new Level();
        level.addToPane(root);
        player = new Player(level.getStartX(), level.getStartY());
        root.getChildren().add(player.getSprite());
        root.getChildren().add(player.getWeaponSprite());
        gameUI = new GameUI(root);
        enemies.clear();
        spawnEnemy();
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    private void showGameOverScreen() {
        // Загружаем картинку Game Over
        Image gameOverImage = new Image(getClass().getResourceAsStream("/images/ui/game_over.png"));
        ImageView gameOverView = new ImageView(gameOverImage);
        gameOverView.setFitWidth(300);
        gameOverView.setPreserveRatio(true);
        gameOverView.setX(800/2 - 150);
        gameOverView.setY(600/2 - 200);

        // Счёт
        Label scoreLabel = new Label("Score: " + gameUI.getScore());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setLayoutX(800/2 - 50);
        scoreLabel.setLayoutY(600/2 + 20);

        // Текст рестарта
        Label restartLabel = new Label("Press R to restart");
        restartLabel.setFont(Font.font("Arial",FontWeight.BOLD, 18));
        restartLabel.setTextFill(Color.BLUEVIOLET);
        restartLabel.setLayoutX(800/2 - 80);
        restartLabel.setLayoutY(600/2 + 60);             // поднял (было +100)

        root.getChildren().addAll(gameOverView, scoreLabel, restartLabel);
    }

    private void checkPlatformCollisions(Entity entity, double oldY) {
        boolean onPlatform = false;

        for (Platform platform : level.getPlatforms()) {
            if (entity.getY() + entity.getHeight() >= platform.getY() &&
                    entity.getY() + entity.getHeight() <= platform.getY() + 10 &&
                    oldY + entity.getHeight() <= platform.getY() && // был выше
                    entity.getX() + entity.getWidth() > platform.getX() &&
                    entity.getX() < platform.getX() + platform.getWidth()) {

                if (entity instanceof Player) {
                    ((Player) entity).landOnPlatform(platform.getY());
                } else if (entity instanceof Enemy) {
                    ((Enemy) entity).landOnPlatform(platform.getY());
                }
                onPlatform = true;
                break;
            }

            if (entity.getY() <= platform.getY() + platform.getHeight() &&
                    entity.getY() >= platform.getY() + platform.getHeight() - 10 &&
                    oldY >= platform.getY() + platform.getHeight() && // был ниже
                    entity.getX() + entity.getWidth() > platform.getX() &&
                    entity.getX() < platform.getX() + platform.getWidth()) {

                entity.setPosition(entity.getX(), platform.getY() + platform.getHeight());

                if (entity instanceof Player) {
                    ((Player) entity).stopVerticalMovement();
                } else if (entity instanceof Enemy) {
                    ((Enemy) entity).stopVerticalMovement();
                }
            }
        }

        if (!onPlatform) {
            if (entity instanceof Player) {
                ((Player) entity).setOnGround(false);
            } else if (entity instanceof Enemy) {
                ((Enemy) entity).setOnGround(false);
            }
        }

        if (entity.getY() + entity.getHeight() > 600) {
            if (entity instanceof Player) {
                ((Player) entity).landOnPlatform(600);
            } else if (entity instanceof Enemy) {
                ((Enemy) entity).landOnPlatform(600);
            }
        }
    }

    private void checkCombatCollisions() {
        Iterator<Enemy> iterator = enemies.iterator();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            // Проверяем атаку игрока
            if (player.isAttacking() &&
                    player.getAttackBounds().intersects(enemy.getSprite().getBoundsInParent())) {
                enemy.takeDamage(player.getAttackDamage());
                soundManager.playAttackSound();
                System.out.println("Попал по врагу! Осталось здоровья: " + enemy.getHealth());
            }

            // Проверяем столкновение игрока с врагом
            if (player.getSprite().getBoundsInParent().intersects(
                    enemy.getSprite().getBoundsInParent())) {

                player.takeDamage(enemy.getDamage());
                soundManager.playHitSound();
                System.out.println("Игрок получил урон! Здоровье: " + player.getHealth());

                double dx = enemy.getX() - player.getX();
                if (dx > 0) enemy.setPosition(enemy.getX() + 25, enemy.getY());
                else enemy.setPosition(enemy.getX() - 25, enemy.getY());
            }
        }
    }

    private void spawnEnemy() {
       double platformList[][] = {{ 100, 430 }, { 200, 430 }, { 580, 430 }, { 680, 430 }, { 340, 380 }, { 440, 380 }, { 450, 330 }, { 530, 330 }};
        double x = platformList[random.nextInt(8)][0];
        double y = platformList[random.nextInt(8)][1];
        Enemy enemy = new Enemy(x, y, player);
        enemies.add(enemy);
        root.getChildren().add(enemy.getSprite());

        System.out.println("Появился новый враг!");
    }

    public Player getPlayer() { return player; }
    public GameUI getGameUI() { return gameUI; }
}
