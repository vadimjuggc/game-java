package com.mygame.game.entities;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class Platform {
    private Canvas canvas;
    private Rectangle rectangle;
    private double x, y, width, height;

    private static Image textureImage = null;

    public Platform(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

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

        drawTexture();
    }

    private void drawTexture() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (textureImage != null && !textureImage.isError()) {
            double tileW = textureImage.getWidth();
            double tileH = textureImage.getHeight();

            for (double dx = 0; dx < width; dx += tileW) {
                double drawW = Math.min(tileW, width - dx);
                for (double dy = 0; dy < height; dy += tileH) {
                    double drawH = Math.min(tileH, height - dy);
                    gc.drawImage(textureImage,
                            0, 0, drawW, drawH,
                            dx, dy, drawW, drawH
                    );
                }
            }
        } else {
            gc.setFill(javafx.scene.paint.Color.rgb(80, 60, 40));
            gc.fillRect(0, 0, width, height);
            gc.setStroke(javafx.scene.paint.Color.rgb(50, 40, 30));
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, width, height);
        }
    }

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