package com.github.yqy7.jscreenshot;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PrimaryController {

    @FXML
    public Canvas canvas;

    public void renderImage(Image image, double fitWidth, double fitHeight) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

        canvas.setWidth(fitWidth);
        canvas.setHeight(fitHeight);
        gc.drawImage(image, 0, 0, fitWidth, fitHeight);
    }
}
