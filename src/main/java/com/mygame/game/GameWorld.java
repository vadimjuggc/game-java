package com.mygame.game;

import com.mygame.game.entities.Entity;
import com.mygame.game.entities.Item;
import com.mygame.game.entities.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import com.mygame.game.entities.Player;
import com.mygame.game.entities.Enemy;
import com.mygame.game.utils.SoundManager;
import com.mygame.game.ui.GameUI;

import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.mygame.game.entities.Arrow;
import com.mygame.game.entities.DamageNumber;
import javafx.scene.control.Label;

public class GameWorld {

    private static final int WIN_SCORE = 300;

    private static final double VIEW_W = 800;
    private static final double VIEW_H = 600;

    private Pane root;
    private Pane pauseOverlay;
    private Pane gamePane;
    private Player player;
    private List<Enemy> enemies;
    private List<Item> items;
    private Level level;
    private Set<KeyCode> keysPressed = new HashSet<>();
    private GameUI gameUI;
    private Random random = new Random();
    private ImageView background;
    private double spawnTimer = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private Runnable onMainMenuCallback;
    private boolean paused = false;
    private double cameraX = 0;

    public GameWorld(Pane root, Runnable onMainMenuCallback) {
        this.root = root;
        this.onMainMenuCallback = onMainMenuCallback;
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();

        root.setPrefSize(VIEW_W, VIEW_H);
        root.setClip(new javafx.scene.shape.Rectangle(VIEW_W, VIEW_H));

        gamePane = new Pane();

        Image bgImage = new Image(getClass().getResourceAsStream("/images/backgrounds/long_bg_loop.gif"));
        background = new ImageView(bgImage);
        background.setFitWidth(Level.WORLD_WIDTH);
        background.setFitHeight(VIEW_H);
        background.setPreserveRatio(false);
        gamePane.getChildren().add(background);

        level = new Level();
        level.addToPane(gamePane);

        this.player = new Player(level.getStartX(), level.getStartY(), this);
        gamePane.getChildren().add(player.getSprite());
        gamePane.getChildren().add(player.getWeaponSprite());

        root.getChildren().add(gamePane);

        this.gameUI = new GameUI(root);

        for (int i = 0; i < 5; i++) {
            spawnEnemy();
        }
    }

    public void showDamageNumber(int damage, double x, double y, boolean isPlayer) {
        new DamageNumber(gamePane, damage, x, y, isPlayer);
    }

    public void setKeysPressed(Set<KeyCode> keys) {
        this.keysPressed = keys;
    }

    public void update(double deltaTime) {
        if (gameOver || gameWon) return;
        if (paused) return;

        int arrowsBefore = player.getArrowsLeft();
        player.handleInput(keysPressed);
        int arrowsAfter = player.getArrowsLeft();

        if (arrowsAfter < arrowsBefore) {
            List<Arrow> arrows = player.getArrows();
            if (!arrows.isEmpty()) {
                Arrow newest = arrows.get(arrows.size() - 1);
                if (newest.getSprite().getParent() == null) {
                    gamePane.getChildren().add(newest.getSprite());
                }
            }
        }

        double oldY = player.getY();
        player.update(deltaTime);
        checkPlatformCollisions(player, oldY);

        double targetCamX = player.getX() + player.getWidth() / 2 - VIEW_W / 2;
        targetCamX = Math.max(0, Math.min(targetCamX, Level.WORLD_WIDTH - VIEW_W));
        cameraX = targetCamX;

        background.setX(cameraX * 0.4);
        gamePane.setTranslateX(-cameraX);

        gameUI.updateWeapon(player.isBowEquipped());

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            if (enemy.isDead()) {
                gameUI.addScore(10);

                if (gameUI.getScore() >= WIN_SCORE) {
                    gameWon = true;
                    enemyIterator.remove();
                    gamePane.getChildren().remove(enemy.getSprite());
                    showWinScreen();
                    return;
                }

                if (random.nextDouble() < 0.30) {
                    spawnItem(enemy.getX(), enemy.getY());
                }

                enemyIterator.remove();
                gamePane.getChildren().remove(enemy.getSprite());
                continue;
            }

            updateEnemyPlatformInfo(enemy);

            double oldEnemyY = enemy.getY();
            enemy.update(deltaTime);
            checkPlatformCollisions(enemy, oldEnemyY);
        }

        Iterator<Arrow> arrowIterator = player.getArrows().iterator();
        while (arrowIterator.hasNext()) {
            Arrow arrow = arrowIterator.next();
            arrow.update(deltaTime);

            if (!arrow.isActive()) {
                arrowIterator.remove();
                gamePane.getChildren().remove(arrow.getSprite());
                continue;
            }

            boolean hit = false;
            for (Enemy enemy : enemies) {
                if (arrow.getBounds().intersects(enemy.getSprite().getBoundsInParent())) {
                    enemy.takeDamage(arrow.getDamage());
                    SoundManager.getInstance().playAttackSound();
                    arrowIterator.remove();
                    gamePane.getChildren().remove(arrow.getSprite());
                    hit = true;
                    break;
                }
            }
            if (hit) continue;
        }

        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.checkPickup(player)) {
                item.applyEffect(player);
                itemIterator.remove();
                gamePane.getChildren().remove(item.getSprite());
            }
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

    private void spawnItem(double x, double y) {
        Item item = new Item(Item.ItemType.HEALTH, x, y);
        items.add(item);
        gamePane.getChildren().add(item.getSprite());
    }

    private void updateEnemyPlatformInfo(Enemy enemy) {
        for (Platform platform : level.getPlatforms()) {
            double ey = enemy.getY() + enemy.getHeight();
            boolean onThisPlatform =
                    ey >= platform.getY() - 8 &&
                            ey <= platform.getY() + 8 &&
                            enemy.getX() + enemy.getWidth() > platform.getX() &&
                            enemy.getX() < platform.getX() + platform.getWidth();
            if (onThisPlatform) {
                enemy.setCurrentPlatformBounds(
                        platform.getX(),
                        platform.getX() + platform.getWidth()
                );
                return;
            }
        }
        enemy.setCurrentPlatformBounds(0, Level.WORLD_WIDTH);
    }

    public void restart() {
        root.getChildren().clear();
        gameOver = false;
        gameWon = false;
        spawnTimer = 0;
        cameraX = 0;
        enemies.clear();
        items.clear();

        root.setClip(new javafx.scene.shape.Rectangle(VIEW_W, VIEW_H));

        gamePane = new Pane();

        Image bgImage = new Image(getClass().getResourceAsStream("/images/backgrounds/long_bg_loop.gif"));
        background = new ImageView(bgImage);
        background.setFitWidth(Level.WORLD_WIDTH);
        background.setFitHeight(VIEW_H);
        background.setPreserveRatio(false);
        gamePane.getChildren().add(background);

        level = new Level();
        level.addToPane(gamePane);

        player = new Player(level.getStartX(), level.getStartY(), this);
        gamePane.getChildren().add(player.getSprite());
        gamePane.getChildren().add(player.getWeaponSprite());

        root.getChildren().add(gamePane);

        gameUI = new GameUI(root);

        for (int i = 0; i < 5; i++) {
            spawnEnemy();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void showWinScreen() {
        gameUI.showWinScreen(
                () -> restart(),
                () -> {
                    SoundManager.getInstance().stopBackgroundMusic();
                    if (onMainMenuCallback != null) onMainMenuCallback.run();
                }
        );
    }

    private void showGameOverScreen() {
        Image gameOverImage = new Image(getClass().getResourceAsStream("/images/ui/game_over.png"));
        double targetHeight = 300;
        double scale = targetHeight / gameOverImage.getHeight();
        double scaledWidth = gameOverImage.getWidth() * scale;

        ImageView gameOverView = new ImageView(gameOverImage);
        gameOverView.setFitWidth(scaledWidth);
        gameOverView.setFitHeight(targetHeight);
        gameOverView.setPreserveRatio(false);
        gameOverView.setX(VIEW_W / 2 - scaledWidth / 2);
        gameOverView.setY(VIEW_H / 2 - targetHeight / 2 - 40);

        Label scoreLabel = new Label("Score: " + gameUI.getScore());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setLayoutX(VIEW_W / 2 - 50);
        scoreLabel.setLayoutY(VIEW_H / 2 + targetHeight / 2 - 20);

        Label restartLabel = new Label("Press R to restart");
        restartLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        restartLabel.setTextFill(Color.BLUEVIOLET);
        restartLabel.setLayoutX(VIEW_W / 2 - 80);
        restartLabel.setLayoutY(VIEW_H / 2 + targetHeight / 2 + 20);

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

        if (entity.getY() + entity.getHeight() > Level.WORLD_HEIGHT) {
            if (entity instanceof Player) {
                ((Player) entity).landOnPlatform(Level.WORLD_HEIGHT);
            } else if (entity instanceof Enemy) {
                ((Enemy) entity).landOnPlatform(Level.WORLD_HEIGHT);
            }
        }
    }

    private void showPauseMenu() {
        pauseOverlay = new Pane();
        pauseOverlay.setPrefSize(VIEW_W, VIEW_H);
        pauseOverlay.setLayoutX(0);
        pauseOverlay.setLayoutY(0);

        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/images/ui/pause/pause_bg.png"));
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(VIEW_W);
            bgView.setFitHeight(VIEW_H);
            bgView.setPreserveRatio(false);
            pauseOverlay.getChildren().add(bgView);
        } catch (Exception e) {
            pauseOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        }

        ImageView pausedImage = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/pause/pause.png")));
        pausedImage.setPreserveRatio(true);
        pausedImage.setFitWidth(315);
        pausedImage.setFitHeight(150);
        pausedImage.setLayoutX(VIEW_W / 2 - 315 / 2);
        pausedImage.setLayoutY(VIEW_H / 2 - 100);

        ImageView resumeButton = new ImageView(new Image(getClass().getResourceAsStream("/images/ui/pause/resume.png")));
        resumeButton.setFitWidth(178);
        resumeButton.setFitHeight(80);
        resumeButton.setLayoutX(VIEW_W / 2 - 178 / 2);
        resumeButton.setLayoutY(VIEW_H / 2 + 30);
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
        menuButton.setLayoutX(VIEW_W / 2 - 218 / 2);
        menuButton.setLayoutY(VIEW_H / 2 + 120);
        menuButton.setCursor(javafx.scene.Cursor.HAND);
        menuButton.setOnMouseEntered(e -> menuButton.setStyle("-fx-effect: dropshadow(gaussian, white, 15, 0.7, 0, 0);"));
        menuButton.setOnMouseExited(e -> menuButton.setStyle("-fx-effect: null;"));
        menuButton.setOnMouseClicked(e -> {
            SoundManager.getInstance().playClickSound();
            paused = false;
            hidePauseMenu();
            SoundManager.getInstance().stopBackgroundMusic();
            if (onMainMenuCallback != null) onMainMenuCallback.run();
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
        // Спавн-точки на всех зонах расширенного уровня
        double[][] platformList = {
                // Центральная зона
                {100, 430}, {200, 430}, {580, 430}, {680, 430},
                {340, 380}, {440, 380}, {450, 330}, {530, 330},
                // Правая зона
                {850, 430}, {900, 430}, {1050, 360}, {1200, 420},
                {1350, 310}, {1500, 430},
                // Дальняя зона
                {1650, 420}, {1720, 420}, {1820, 340}, {1980, 420},
                {2100, 300}, {2250, 430}
        };
        int index = random.nextInt(platformList.length);
        double x = platformList[index][0];
        double y = platformList[index][1];

        Enemy enemy = new Enemy(x, y, player, this);
        enemy.setSoundManager(SoundManager.getInstance());
        enemies.add(enemy);
        gamePane.getChildren().add(enemy.getSprite());
    }

    public Player getPlayer() { return player; }
    public GameUI getGameUI() { return gameUI; }
}
