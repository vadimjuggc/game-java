package com.mygame.game.entities;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class Platform {

    public enum State { SOLID, BLINKING, GONE, RESPAWNING }

    private Canvas canvas;
    private Rectangle rectangle;
    private double x, y, width, height;
    private static Image textureImage = null;

    private boolean vanishing = false;
    private State state = State.SOLID;
    private double stateTimer = 0;

    private static final double BLINK_DURATION = 1.5;
    private static final double GONE_DURATION = 2.5;
    private static final double RESPAWN_DURATION = 1.0;
    private static final double BLINK_SPEED = 8.0;

    public Platform(double x, double y, double width, double height) {
        this(x, y, width, height, false);
    }

    public Platform(double x, double y, double width, double height, boolean vanishing) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vanishing = vanishing;

        rectangle = new Rectangle(x, y, width, height);
        rectangle.setVisible(false);

        canvas = new Canvas(width, height);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);

        if (textureImage == null) {
            try {
                textureImage = new Image(
                        Platform.class.getResourceAsStream("/images/textures/level.png")
                );
            } catch (Exception e) {
                System.out.println("Текстура платформы не загружена");
            }
        }

        drawTexture(1.0);
    }

    private void drawTexture(double opacity) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setGlobalAlpha(opacity);

        if (textureImage != null && !textureImage.isError()) {
            double tileW = textureImage.getWidth();
            double tileH = textureImage.getHeight();
            for (double dx = 0; dx < width; dx += tileW) {
                double drawW = Math.min(tileW, width - dx);
                for (double dy = 0; dy < height; dy += tileH) {
                    double drawH = Math.min(tileH, height - dy);
                    gc.drawImage(textureImage, 0, 0, drawW, drawH, dx, dy, drawW, drawH);
                }
            }
        } else {
            gc.setFill(javafx.scene.paint.Color.rgb(80, 60, 40));
            gc.fillRect(0, 0, width, height);
        }

        gc.setGlobalAlpha(1.0);
    }

    public void update(double deltaTime, boolean playerOnTop) {
        if (!vanishing) return;

        switch (state) {
            case SOLID:
                if (playerOnTop) {
                    state = State.BLINKING;
                    stateTimer = 0;
                }
                break;

            case BLINKING:
                stateTimer += deltaTime;
                double blink = 0.4 + 0.6 * Math.abs(Math.sin(stateTimer * BLINK_SPEED));
                drawTexture(blink);

                if (stateTimer >= BLINK_DURATION) {
                    state = State.GONE;
                    stateTimer = 0;
                    canvas.setVisible(false);
                }
                break;

            case GONE:
                stateTimer += deltaTime;
                if (stateTimer >= GONE_DURATION) {
                    state = State.RESPAWNING;
                    stateTimer = 0;
                    canvas.setVisible(true);
                }
                break;

            case RESPAWNING:
                stateTimer += deltaTime;
                double alpha = stateTimer / RESPAWN_DURATION;
                drawTexture(Math.min(alpha, 1.0));
                if (stateTimer >= RESPAWN_DURATION) {
                    state = State.SOLID;
                    stateTimer = 0;
                    drawTexture(1.0);
                }
                break;
        }
    }

    public boolean isSolid() {
        return state == State.SOLID || state == State.BLINKING;
    }

    public boolean isVanishing() { return vanishing; }
    public State getState() { return state; }
    public Canvas getCanvas() { return canvas; }
    public Rectangle getRectangle() { return rectangle; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public boolean isPlayerAbove(Player player) {
        boolean above = player.getY() + player.height <= 5 && player.getY() + player.height >= y - 5;
        boolean withinX = player.getX() + player.width > x && player.getX() < x + width;
        return above && withinX;
    }
}