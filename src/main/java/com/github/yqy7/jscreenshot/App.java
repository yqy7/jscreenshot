package com.github.yqy7.jscreenshot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private Scene scene;
    private TrayIcon trayIcon;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setScene(scene);
        stage.show();

        addSystemTray();
        setupTrayIconAction();
    }

    private void setupTrayIconAction() {
        trayIcon.addMouseListener(new TrayIconMouseListenerBase() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    doScreenshot();
                } else {
                    System.out.println("right or other click~" + e.getButton());
                }
            }
        });
    }

    private PopupMenu createPopupMenu() {
        final PopupMenu popup = new PopupMenu();
        MenuItem aboutItem = new MenuItem("About");
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        Menu displayMenu = new Menu("Display");
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");
        MenuItem exitItem = new MenuItem("Exit");
        // Add components to pop-up menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(cb1);
        popup.add(cb2);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(errorItem);
        displayMenu.add(warningItem);
        displayMenu.add(infoItem);
        displayMenu.add(noneItem);
        popup.add(exitItem);
        return popup;
    }

    private void doScreenshot() {
        System.out.println("left click..");

    }

    private void addSystemTray() {
        if (!SystemTray.isSupported()) {
            Log.info("System tray is not support!");
            Platform.exit();
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        trayIcon = new TrayIcon(
            toolkit.getImage(App.class.getClassLoader().getResource("jscreenshot.png")));
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Click to Screenshot!");
        trayIcon.setPopupMenu(createPopupMenu());

        SystemTray systemTray = SystemTray.getSystemTray();
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            Log.info("Add System tray error!");
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}