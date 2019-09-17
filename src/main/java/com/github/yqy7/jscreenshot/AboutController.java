package com.github.yqy7.jscreenshot;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;

public class AboutController {

    public void openUrl(ActionEvent actionEvent) {
        App.app.getHostServices().showDocument(((Hyperlink)actionEvent.getSource()).getText());
    }
}
