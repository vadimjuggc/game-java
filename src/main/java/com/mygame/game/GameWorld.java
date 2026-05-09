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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Random;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.mygame.game.entities.Arrow;
import com.mygame.game.entities.DamageNumber;
import javafx.scene.control.Button;

public class GameWorld {

    private Pane root;
    private Pane pauseOverlay;
    private Player player;
    private List<Enemy> enemies;
    private Level level;
    private GameUI gameUI;
    private Random random = new Random();
    private ImageView background;
    private double spawnTimer = 0;
    private boolean gameOver = false;
    private Runnable onMainMenuCallback;
    private boolean paused = false;
    private VBox pauseMenu;

    public GameWorld(Pane root, Runnable onMainMenuCallback) {
        this.root = root;
        this.onMainMenuCallback = onMainMenuCallback;
        this.enemies = new ArrayList<>();

        Image bgImage = new Image(getClass().getResourceAsStream("/images/ui/battle/snowmountians.png"));
        background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);
        background.setPreserveRatio(false);
        root.getChildren().add(background);

        level = new Level();
        level.addToPane(root);

        this.player = new Player(level.getStartX(), level.getStartY(), this);
        root.getChildren().add(player.getSprite());
        root.getChildren().add(player.getWeaponSprite());
        this.gameUI = new GameUI(root);

        spawnEnemy();
    }

    public void showDamageNumber(int damage, double x, double y, boolean isPlayer) {
        new DamageNumber(root, damage, x, y, isPlayer);
    }

    public void update(double deltaTime) {
        if (gameOver) return;
        if (paused) return;

        double oldY = player.getY();
        player.update(deltaTime);
        checkPlatformCollisions(player, oldY);

        gameUI.updateWeapon(player.isBowEquipped());

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

        Image bgImage = new Image(getClass().getResourceAsStream("/images/ui/battle/snowmountians.png"));
        background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);
        background.setPreserveRatio(false);
        root.getChildren().add(background);

        level = new Level();
        level.addToPane(root);

        player = new Player(level.getStartX(), level.getStartY(), this);
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

    private void showPauseMenu() {
        pauseOverlay = new Pane();
        pauseOverlay.setPrefSize(800, 600);
        pauseOverlay.setLayoutX(0);
        pauseOverlay.setLayoutY(0);

        Image bgImage = new Image(getClass().getResourceAsStream("/images/ui/pause/pause_bg.png"));
        if (bgImage != null) {
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(800);
            bgView.setFitHeight(600);
            bgView.setPreserveRatio(false);
            pauseOverlay.getChildren().add(bgView);
        } else {
            pauseOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        }

        ImageView pausedImage = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/pause/pause.png")));
        pausedImage.setPreserveRatio(true);
        pausedImage.setFitWidth(315);
        pausedImage.setFitHeight(150);
        pausedImage.setLayoutX(800/2 - 315/2);
        pausedImage.setLayoutY(600/2 - 100);

        ImageView resumeButton = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/pause/resume.png")));
        resumeButton.setFitWidth(178);
        resumeButton.setFitHeight(80);
        resumeButton.setLayoutX(800/2 - 178/2);
        resumeButton.setLayoutY(600/2 + 30);
        resumeButton.setCursor(javafx.scene.Cursor.HAND);
        resumeButton.setOnMouseEntered(e -> resumeButton.setStyle("-fx-effect: dropshadow(gaussian, white, 15, 0.7, 0, 0);"));
        resumeButton.setOnMouseExited(e -> resumeButton.setStyle("-fx-effect: null;"));
        resumeButton.setOnMouseClicked(e -> {
            SoundManager.getInstance().playClickSound();
            togglePause();
        });

        ImageView menuButton = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/pause/main_menu_button.png")));
        menuButton.setFitWidth(218);
        menuButton.setFitHeight(80);
        menuButton.setLayoutX(800/2 - 218/2);
        menuButton.setLayoutY(600/2 + 120);
        menuButton.setCursor(javafx.scene.Cursor.HAND);
        menuButton.setOnMouseEntered(e -> menuButton.setStyle("-fx-effect: dropshadow(gaussian, white, 15, 0.7, 0, 0);"));
        menuButton.setOnMouseExited(e -> menuButton.setStyle("-fx-effect: null;"));
        menuButton.setOnMouseClicked(e -> {
            SoundManager.getInstance().playClickSound();
            paused = false;
            hidePauseMenu();
            SoundManager.getInstance().stopBackgroundMusic();
            if (onMainMenuCallback != null) {
                onMainMenuCallback.run();
            }
        });

        pauseOverlay.getChildren().addAll(pausedImage, resumeButton, menuButton);
        root.getChildren().add(pauseOverlay);
    }

    private void hidePauseMenu() {
        if (pauseOverlay != null) {
            root.getChildren().remove(pauseOverlay);
            pauseOverlay = null;
        }
    }

    public void togglePause() {
        paused = !paused;
        if (paused) {
            showPauseMenu();
        } else {
            hidePauseMenu();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    private void checkCombatCollisions() {
        Iterator<Enemy> iterator = enemies.iterator();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            if (player.isAttacking() &&
                    player.getAttackBounds().intersects(enemy.getSprite().getBoundsInParent())) {
                enemy.takeDamage(player.getAttackDamage());
                if (!player.isBowEquipped()) {
                    SoundManager.getInstance().playSwordHitSound();
                } else {
                    SoundManager.getInstance().playAttackSound();
                }
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

        Enemy enemy = new Enemy(x, y, player, this);
        enemy.setSoundManager(SoundManager.getInstance());
        enemies.add(enemy);
        root.getChildren().add(enemy.getSprite());
    }

    public Player getPlayer() { return player; }
    public GameUI getGameUI() { return gameUI; }
}