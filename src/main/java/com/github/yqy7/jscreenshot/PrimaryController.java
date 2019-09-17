package com.github.yqy7.jscreenshot;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PrimaryController {

    @FXML
    public ImageView imageView;

    public void renderImage(Image image, int fitWidth, int fitHeight) {
        imageView.setImage(image);
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
    }
}
