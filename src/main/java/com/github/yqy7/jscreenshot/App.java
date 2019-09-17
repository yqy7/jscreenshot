package com.github.yqy7.jscreenshot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.*;

/**
 * JavaFX App
 */
public class App extends Application {
    public static App app;

    private TrayIcon trayIcon;
    private Stage primaryStage;
    private Scene primaryScene;
    private PrimaryController primaryController;

    @Override
    public void start(Stage stage) throws IOException {
        app = this;

        this.primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        primaryScene = new Scene(fxmlLoader.load(), 640, 480);
        primaryController = fxmlLoader.getController();
        System.out.println("primaryController" + primaryController);
        stage.setScene(primaryScene);
        stage.setFullScreen(true);

        // 退出最后一个窗口不关闭Application
        Platform.setImplicitExit(false);

        addSystemTray();
        setupTrayIconAction();
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

    private void setupTrayIconAction() {
        trayIcon.addMouseListener(new TrayIconMouseListenerBase() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Platform.runLater(() -> {
                        doScreenshot();
                    });
                }
            }
        });
    }

    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();

        MenuItem aboutBtn = new MenuItem("About");
        aboutBtn.addActionListener(e -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("About JScreenshot!");
                alert.setHeaderText(null);

                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("about.fxml"));
                try {
                    alert.getDialogPane().setContent(fxmlLoader.load());
                } catch (IOException ex) {
                    alert.setContentText(ex.getMessage());
                }

                alert.showAndWait();
            });
        });
        popup.add(aboutBtn);

        popup.addSeparator();

        MenuItem quitBtn = new MenuItem("Quit");
        quitBtn.addActionListener(e -> {
            Platform.exit();
        });
        popup.add(quitBtn);

        return popup;
    }

    private void doScreenshot() {
        Robot robot = new Robot();
        Point2D mousePosition = robot.getMousePosition();
        ObservableList<Screen> screens = Screen.getScreensForRectangle(mousePosition.getX(),
            mousePosition.getY(), 0, 0);
        if (screens.size() == 0) {
            System.out.println("screens size is 0!");
            return;
        } else {
            System.out.println("screens size is " + screens.size());
        }

        Screen screen = screens.get(0);
        Rectangle2D bounds = screen.getBounds();
        int width = (int)(bounds.getWidth() * screen.getOutputScaleX());
        int height = (int)(bounds.getHeight() * screen.getOutputScaleY());
        System.out.println("width: " + width + " - height: " + height);
        WritableImage image = new WritableImage(width, height);
        robot.getScreenCapture(image, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight(), false);
        primaryController.renderImage(image, ((int)bounds.getWidth()), ((int)bounds.getHeight()));
        primaryStage.show();
    }



    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }

}