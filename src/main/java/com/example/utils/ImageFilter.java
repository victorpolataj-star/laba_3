package com.example.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageFilter {

    public static Image createGrayscale(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double gray = color.getRed() * 0.299 +
                        color.getGreen() * 0.587 +
                        color.getBlue() * 0.114;
                Color grayColor = new Color(gray, gray, gray, color.getOpacity());
                writer.setColor(x, y, grayColor);
            }
        }
        return result;
    }
}
