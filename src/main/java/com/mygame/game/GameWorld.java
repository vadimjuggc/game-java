package com.mygame.game;

import com.mygame.game.entities.Entity;
import javafx.scene.layout.Pane;
import com.mygame.game.entities.Player;
import com.mygame.game.entities.Enemy;
import com.mygame.game.entities.Platform;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

public class GameWorld {

    private Pane root;
    private Player player;
    private List<Enemy> enemies;
    private Level level;
    private Random random = new Random();

    private double spawnTimer = 0;
    private boolean gameOver = false;

    public GameWorld(Pane root) {
        this.root = root;
        this.enemies = new ArrayList<>();

        // Создаем уровень
        level = new Level();
        level.addToPane(root);

        // Создаем игрока на стартовой позиции
        this.player = new Player(level.getStartX(), level.getStartY());
        root.getChildren().add(player.getRectangle());

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
                enemyIterator.remove();
                root.getChildren().remove(enemy.getRectangle());
                continue;
            }

            double oldEnemyY = enemy.getY();
            enemy.update(deltaTime);
            checkPlatformCollisions(enemy, oldEnemyY);
        }

        // Проверяем столкновения между игроком и врагами
        checkCombatCollisions();

        // Спавним новых врагов
        spawnTimer += deltaTime;
        if (spawnTimer > 5.0) { // каждые 5 секунд
            spawnTimer = 0;
            spawnEnemy();
        }

        // Проверяем смерть игрока
        if (player.isDead() && !gameOver) {
            gameOver = true;
            System.out.println("GAME OVER!");
        }
    }

    private void checkPlatformCollisions(Entity entity, double oldY) {
        boolean onPlatform = false;

        for (Platform platform : level.getPlatforms()) {
            // Проверяем, падает ли entity на платформу
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
        }

        // Если не на платформе, entity падает
        if (!onPlatform) {
            if (entity instanceof Player) {
                ((Player) entity).setOnGround(false);
            } else if (entity instanceof Enemy) {
                ((Enemy) entity).setOnGround(false);
            }
        }

        // Не падать ниже пола
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
                    player.getAttackBounds().intersects(enemy.getRectangle().getBoundsInParent())) {
                enemy.takeDamage(player.getAttackDamage());
                System.out.println("Попал по врагу! Осталось здоровья: " + enemy.getHealth());
            }

            // Проверяем столкновение игрока с врагом
            if (player.getRectangle().getBoundsInParent().intersects(
                    enemy.getRectangle().getBoundsInParent())) {

                player.takeDamage(enemy.getDamage());
                System.out.println("Игрок получил урон! Здоровье: " + player.getHealth());

                // Отталкиваем врага
                double dx = enemy.getX() - player.getX();
                if (dx > 0) enemy.setPosition(enemy.getX() + 50, enemy.getY());
                else enemy.setPosition(enemy.getX() - 50, enemy.getY());
            }
        }
    }

    private void spawnEnemy() {
        // Спавним врага где-то на платформе
        double x = 100 + random.nextInt(600);
        double y = 100; // временно, потом скорректируем

        Enemy enemy = new Enemy(x, y, player);
        enemies.add(enemy);
        root.getChildren().add(enemy.getRectangle());

        System.out.println("Появился новый враг!");
    }

    public Player getPlayer() { return player; }
}
