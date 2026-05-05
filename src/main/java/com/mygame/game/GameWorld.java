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
import com.mygame.game.entities.Arrow;

public class GameWorld {

    private Pane root;
    private Player player;
    private List<Enemy> enemies;
    private Level level;
    private GameUI gameUI;
    private Random random = new Random();
    private ImageView background;
    private double spawnTimer = 0;
    private boolean gameOver = false;

    public GameWorld(Pane root) {
         Image bgImage = new Image(getClass().getResourceAsStream("/images/ui/battle/snowmountians.png"));
            background = new ImageView(bgImage);
            background.setFitWidth(800);
            background.setFitHeight(600);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        this.root = root;
        this.enemies = new ArrayList<>();

        level = new Level();
        level.addToPane(root);


        this.player = new Player(level.getStartX(), level.getStartY());
        root.getChildren().add(player.getSprite());
        root.getChildren().add(player.getWeaponSprite());
        this.gameUI = new GameUI(root);

        spawnEnemy();
    }

    public void update(double deltaTime) {
        if (gameOver) return;

        double oldY = player.getY();
        player.update(deltaTime);
        checkPlatformCollisions(player, oldY);
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

        for (Arrow arrow : player.getArrows()) {
            if (arrow.getSprite().getParent() == null) {
                root.getChildren().add(arrow.getSprite());
            }
        }

        Iterator<Arrow> arrowIterator = player.getArrows().iterator();
        while (arrowIterator.hasNext()) {
            Arrow arrow = arrowIterator.next();
            arrow.update(deltaTime);

            if (!arrow.isActive()) {
                arrowIterator.remove();
                root.getChildren().remove(arrow.getSprite());
                continue;
            }

            boolean hit = false;
            for (Enemy enemy : enemies) {
                if (arrow.getBounds().intersects(enemy.getSprite().getBoundsInParent())) {
                    enemy.takeDamage(arrow.getDamage());
                    SoundManager.getInstance().playAttackSound();
                    arrowIterator.remove();
                    root.getChildren().remove(arrow.getSprite());
                    hit = true;
                    break;
                }
            }
            if (hit) continue;
        }

        checkCombatCollisions();

        spawnTimer += deltaTime;
        if (spawnTimer > 5.0) {
            spawnTimer = 0;
            spawnEnemy();
        }

        gameUI.updateHealth(player.getHealth());
        gameUI.updateArrows(player.getArrowsLeft());

        if (player.isDead() && !gameOver) {
            gameOver = true;
            showGameOverScreen();
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

    public boolean isGameOver() {
        return gameOver;
    }

    private void showGameOverScreen() {
        Image gameOverImage = new Image(getClass().getResourceAsStream("/images/ui/game_over.png"));
        ImageView gameOverView = new ImageView(gameOverImage);
        gameOverView.setFitWidth(300);
        gameOverView.setPreserveRatio(true);
        gameOverView.setX(800/2 - 150);
        gameOverView.setY(600/2 - 200);

        Label scoreLabel = new Label("Score: " + gameUI.getScore());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setLayoutX(800/2 - 50);
        scoreLabel.setLayoutY(600/2 + 20);

        Label restartLabel = new Label("Press R to restart");
        restartLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        restartLabel.setTextFill(Color.BLUEVIOLET);
        restartLabel.setLayoutX(800/2 - 80);
        restartLabel.setLayoutY(600/2 + 60);

        root.getChildren().addAll(gameOverView, scoreLabel, restartLabel);
    }

    private void checkPlatformCollisions(Entity entity, double oldY) {
        boolean onPlatform = false;

        for (Platform platform : level.getPlatforms()) {
            if (entity.getY() + entity.getHeight() >= platform.getY() &&
                    entity.getY() + entity.getHeight() <= platform.getY() + 10 &&
                    oldY + entity.getHeight() <= platform.getY() &&
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
                    oldY >= platform.getY() + platform.getHeight() &&
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

            if (player.isAttacking() &&
                    player.getAttackBounds().intersects(enemy.getSprite().getBoundsInParent())) {
                enemy.takeDamage(player.getAttackDamage());
                SoundManager.getInstance().playAttackSound();
            }

            if (player.getSprite().getBoundsInParent().intersects(enemy.getSprite().getBoundsInParent())) {
                double dx = enemy.getX() - player.getX();

                if (Math.abs(dx) > 0.01) {
                    double overlap = (player.getWidth() + enemy.getWidth()) / 2 - Math.abs(dx);
                    if (overlap > 0) {
                        double moveX = (dx > 0 ? overlap : -overlap) * 0.6;
                        enemy.setPosition(enemy.getX() + moveX, enemy.getY());
                    }
                }
            }
        }
    }

    private void spawnEnemy() {
        double[][] platformList = {{100, 430}, {200, 430}, {580, 430}, {680, 430},
                {340, 380}, {440, 380}, {450, 330}, {530, 330}};
        int index = random.nextInt(platformList.length);
        double x = platformList[index][0];
        double y = platformList[index][1];

        Enemy enemy = new Enemy(x, y, player);
        enemy.setSoundManager(SoundManager.getInstance());
        enemies.add(enemy);
        root.getChildren().add(enemy.getSprite());
    }

    public Player getPlayer() { return player; }
    public GameUI getGameUI() { return gameUI; }
}