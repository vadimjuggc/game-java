package com.mygame.game;

import com.mygame.game.entities.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import com.mygame.game.utils.SoundManager;
import com.mygame.game.ui.GameUI;

import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

public class GameWorld {

    private static final int WIN_SCORE = 300;

    private static final double VIEW_W = 800;
    private static final double VIEW_H = 600;

    private Pane root;
    private Pane pauseOverlay;
    private Pane shakePane;
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
    private Rectangle vignetteOverlay;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private Runnable onMainMenuCallback;
    private boolean paused = false;
    private double cameraX = 0;
    private int comboCount = 0;
    private int lastPlayerHealth = 100;
    private double footstepTimer = 0;
    private static final double FOOTSTEP_INTERVAL = 0.32;
    private int comboMultiplier = 1;
    private List<SlashEffect> slashEffects = new ArrayList<>();
    private double comboTimer = 0;
    private static final double COMBO_TIMEOUT = 3.0;
    private List<BloodParticle> bloodParticles = new ArrayList<>();
    private List<DarkParticle> darkParticles = new ArrayList<>();
    private double idleParticleTimer = 0;

    private Rectangle swordBarBg;
    private Rectangle swordBar;
    private static final double SWORD_BAR_W = 30;
    private static final double SWORD_BAR_H = 4;
    private double shakeDuration = 0;
    private double shakeIntensity = 0;

    public GameWorld(Pane root, Runnable onMainMenuCallback) {
        this.root = root;
        this.onMainMenuCallback = onMainMenuCallback;
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();

        root.setPrefSize(VIEW_W, VIEW_H);
        root.setClip(new javafx.scene.shape.Rectangle(VIEW_W, VIEW_H));

        shakePane = new Pane();
        gamePane = new Pane();
        shakePane.getChildren().add(gamePane);
        root.getChildren().add(shakePane);
        shakePane.setClip(new javafx.scene.shape.Rectangle(VIEW_W, VIEW_H));

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

        swordBarBg = new Rectangle(SWORD_BAR_W, SWORD_BAR_H);
        swordBarBg.setFill(Color.rgb(40, 40, 40, 0.8));
        swordBarBg.setArcWidth(3);
        swordBarBg.setArcHeight(3);
        swordBarBg.setOpacity(0);

        swordBar = new Rectangle(SWORD_BAR_W, SWORD_BAR_H);
        swordBar.setFill(Color.SILVER);
        swordBar.setArcWidth(3);
        swordBar.setArcHeight(3);
        swordBar.setOpacity(0);

        gamePane.getChildren().addAll(swordBarBg, swordBar);

        vignetteOverlay = new Rectangle(VIEW_W, VIEW_H);
        vignetteOverlay.setFill(new RadialGradient(
                0, 0,
                0.5, 0.5,
                0.7,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.rgb(150, 0, 0, 0.0))
        ));
        vignetteOverlay.setMouseTransparent(true);
        root.getChildren().add(vignetteOverlay);

        this.gameUI = new GameUI(root);

        for (int i = 0; i < 5; i++) {
            spawnEnemy();
        }
    }

    public void showDamageNumber(int damage, double x, double y, boolean isPlayer) {
        new DamageNumber(gamePane, damage, x, y, isPlayer);
    }

    public void spawnSlash(double x, double y, boolean facingRight) {
        slashEffects.add(new SlashEffect(gamePane, x, y, facingRight));
    }

    public void spawnBlood(double x, double y) {
        int count = 6 + random.nextInt(6);
        for (int i = 0; i < count; i++) {
            bloodParticles.add(new BloodParticle(gamePane, x, y));
        }
    }

    private void spawnIdleParticles() {
        darkParticles.add(new DarkParticle(gamePane,
                player.getX() + player.getWidth() / 2,
                player.getY() + player.getHeight()
        ));
    }

    public void setKeysPressed(Set<KeyCode> keys) {
        this.keysPressed = keys;
    }

    public void update(double deltaTime) {
        if (gameOver || gameWon) return;
        if (paused) return;
        if (comboCount > 0) {
            comboTimer += deltaTime;
            if (comboTimer >= COMBO_TIMEOUT) {
                comboCount = 0;
                comboMultiplier = 1;
                comboTimer = 0;
                gameUI.resetCombo();
            }
        }

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

        if (shakeDuration > 0) {
            shakeDuration -= deltaTime;
            double offsetX = (random.nextDouble() * 2 - 1) * shakeIntensity;
            double offsetY = (random.nextDouble() * 2 - 1) * shakeIntensity;
            shakePane.setTranslateX(offsetX);
            shakePane.setTranslateY(offsetY);
        } else {
            shakeDuration = 0;
            shakePane.setTranslateX(0);
            shakePane.setTranslateY(0);
        }

        gamePane.setTranslateX(-cameraX);
        gamePane.setTranslateY(0);

        gameUI.updateWeapon(player.isBowEquipped());

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            if (enemy.isDead()) {
                comboCount++;
                comboTimer = 0;
                comboMultiplier = Math.min(comboCount, 5);
                int points = 10 * comboMultiplier;
                spawnBlood(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2);
                startShake(0.1, 3);
                gameUI.addScore(points);
                gameUI.showCombo(comboCount, comboMultiplier);
                SoundManager.getInstance().playComboSound(comboCount);

                if (gameUI.getScore() >= WIN_SCORE) {
                    gameWon = true;
                    enemyIterator.remove();
                    gamePane.getChildren().remove(enemy.getSprite());
                    showWinScreen();
                    return;
                }

                if (random.nextDouble() < 0.15) {
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

        darkParticles.removeIf(p -> !p.update(deltaTime));
        if (player.isMoving()) {
            footstepTimer += deltaTime;
            if (footstepTimer >= FOOTSTEP_INTERVAL) {
                footstepTimer = 0;
                SoundManager.getInstance().playFootstep();
            }
        } else {
            footstepTimer = 0;
        }
        bloodParticles.removeIf(p -> !p.update(deltaTime));
        slashEffects.removeIf(s -> !s.update(deltaTime));

        if (player.isIdle() && player.isOnGround()) {
            idleParticleTimer += deltaTime;
            if (idleParticleTimer >= 0.12) {
                idleParticleTimer = 0;
                spawnIdleParticles();
            }
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
                showDamageNumber(-10, player.getX(), player.getY() - 20, false);
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

        int currentHealth = player.getHealth();
        if (currentHealth < lastPlayerHealth) {
            spawnBlood(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
            startShake(0.2, 6);
        }
        lastPlayerHealth = currentHealth;

        int hp = player.getHealth();
        if (hp < 30) {
            double pulse = 0.5 + 0.25 * Math.sin(System.currentTimeMillis() / 200.0);
            vignetteOverlay.setFill(new RadialGradient(
                    0, 0, 0.5, 0.5, 0.7, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.TRANSPARENT),
                    new Stop(1, Color.rgb(160, 0, 0, pulse))
            ));
        } else if (hp < 60) {
            vignetteOverlay.setFill(new RadialGradient(
                    0, 0, 0.5, 0.5, 0.7, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.TRANSPARENT),
                    new Stop(1, Color.rgb(120, 0, 0, 0.35))
            ));
        } else {
            vignetteOverlay.setFill(new RadialGradient(
                    0, 0, 0.5, 0.5, 0.7, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.TRANSPARENT),
                    new Stop(1, Color.rgb(0, 0, 0, 0.0))
            ));
        }

        double ratio = player.getAttackCooldownRatio();
        double barX = player.getX() - 3;
        double barY = player.getY() - 10;

        swordBarBg.setX(barX);
        swordBarBg.setY(barY);
        swordBar.setX(barX);
        swordBar.setY(barY);
        swordBar.setWidth(SWORD_BAR_W * ratio);

        if (player.isBowEquipped() || ratio >= 1.0) {
            swordBarBg.setOpacity(Math.max(0, swordBarBg.getOpacity() - 0.05));
            swordBar.setOpacity(Math.max(0, swordBar.getOpacity() - 0.05));
        } else {
            swordBarBg.setOpacity(1.0);
            swordBar.setOpacity(1.0);
        }

        if (ratio < 0.4) {
            swordBar.setFill(Color.rgb(180, 50, 50));
        } else if (ratio < 0.7) {
            swordBar.setFill(Color.ORANGE);
        } else {
            swordBar.setFill(Color.SILVER);
        }

        gameUI.updateHealth(player.getHealth());
        gameUI.updateArrows(player.getArrowsLeft());

        if (player.isDead() && !gameOver) {
            gameOver = true;
            showGameOverScreen();
        }
    }

    public void startShake(double duration, double intensity) {
        shakeDuration = duration;
        shakeIntensity = intensity;
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

        shakePane = new Pane();
        gamePane = new Pane();
        shakePane.getChildren().add(gamePane);
        root.getChildren().add(shakePane);
        shakePane.setClip(new javafx.scene.shape.Rectangle(VIEW_W, VIEW_H));

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

        swordBarBg = new Rectangle(SWORD_BAR_W, SWORD_BAR_H);
        swordBarBg.setFill(Color.rgb(40, 40, 40, 0.8));
        swordBarBg.setArcWidth(3);
        swordBarBg.setArcHeight(3);
        swordBarBg.setOpacity(0);

        swordBar = new Rectangle(SWORD_BAR_W, SWORD_BAR_H);
        swordBar.setFill(Color.SILVER);
        swordBar.setArcWidth(3);
        swordBar.setArcHeight(3);
        swordBar.setOpacity(0);

        gamePane.getChildren().addAll(swordBarBg, swordBar);

        vignetteOverlay = new Rectangle(VIEW_W, VIEW_H);
        vignetteOverlay.setFill(new RadialGradient(
                0, 0,
                0.5, 0.5,
                0.7,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.rgb(150, 0, 0, 0.0))
        ));
        vignetteOverlay.setMouseTransparent(true);
        root.getChildren().add(vignetteOverlay);

        gameUI = new GameUI(root);

        for (int i = 0; i < 5; i++) {
            spawnEnemy();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void showWinScreen() {
        SoundManager.getInstance().stopBackgroundMusic();
        SoundManager.getInstance().playYouWinSound();
        gameUI.showWinScreen(() -> restart(), () -> {
                    SoundManager.getInstance().stopBackgroundMusic();
                    if (onMainMenuCallback != null) onMainMenuCallback.run();
                }
        );
    }

    private void showGameOverScreen() {
        SoundManager.getInstance().stopBackgroundMusic();
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.7));
        delay.setOnFinished(e -> SoundManager.getInstance().playGameOverSound());
        delay.play();

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
            double entityBottom = entity.getY() + entity.getHeight();
            double entityTop = entity.getY();
            double entityLeft = entity.getX();
            double entityRight = entity.getX() + entity.getWidth();

            double platTop = platform.getY();
            double platBottom = platform.getY() + platform.getHeight();
            double platLeft = platform.getX();
            double platRight = platform.getX() + platform.getWidth();

            boolean horizontalOverlap = entityRight > platLeft && entityLeft < platRight;

            if (horizontalOverlap &&
                    oldY + entity.getHeight() <= platTop + 5 &&
                    entityBottom >= platTop) {
                if (entity instanceof Player) {
                    ((Player) entity).landOnPlatform(platTop);
                } else if (entity instanceof Enemy) {
                    ((Enemy) entity).landOnPlatform(platTop);
                }
                onPlatform = true;
                break;
            }

            if (horizontalOverlap &&
                    oldY >= platBottom - 5 &&
                    entityTop <= platBottom) {
                entity.setPosition(entity.getX(), platBottom);
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

        if (entity.getY() + entity.getHeight() >= Level.WORLD_HEIGHT) {
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

            if (player.canDealDamage() &&
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
        double[][] platformList = {
                {100, 430}, {200, 430}, {580, 430}, {680, 430},
                {340, 380}, {440, 380}, {450, 330}, {530, 330},
                {850, 430}, {900, 430}, {1050, 360}, {1200, 420},
                {1350, 310}, {1500, 430},
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