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
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;

/**
 * JavaFX App
 */
public class App extends Application {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

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

        primaryStage.setScene(primaryScene);
        primaryStage.setAlwaysOnTop(true);

        // 退出最后一个窗口不关闭Application
        Platform.setImplicitExit(false);

        addSystemTray();
        setupTrayIconAction();
        // 显示一下是outputScale变正常
        primaryStage.show();
        primaryStage.hide();
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
                System.out.println(e.getButton());
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

        // 截图按钮
        MenuItem screenshotBtn = new MenuItem("Screenshot");
        screenshotBtn.addActionListener(e -> {
            Platform.runLater(() -> {
                doScreenshot();
            });
        });
        popup.add(screenshotBtn);

        // 关于按钮
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

        // 分割线
        popup.addSeparator();

        // 退出按钮
        MenuItem quitBtn = new MenuItem("Quit");
        quitBtn.addActionListener(e -> {
            Platform.exit();
        });
        popup.add(quitBtn);

        return popup;
    }

    private void doScreenshot() {
        Image image = getScreenImage();
        double fitWidth = image.getWidth() / primaryStage.getOutputScaleX();
        double fitHeight = image.getHeight() / primaryStage.getOutputScaleY();
        primaryController.renderImage(image,
            fitWidth, fitHeight);
        primaryStage.setWidth(fitWidth);
        primaryStage.setHeight(fitHeight);
        primaryStage.show();
    }

    private Image getScreenImage() {
        if (Util.isMac()) {
            return getScreenImageMac();
        } else {
            return getScreenImageOther();
        }
    }

    private Image getScreenImageOther() {
        Robot robot = new Robot();
        Point2D mousePosition = robot.getMousePosition();
        ObservableList<Screen> screens = Screen.getScreensForRectangle(mousePosition.getX(),
            mousePosition.getY(), 0, 0);
        Screen screen = screens.get(0);
        Rectangle2D bounds = screen.getBounds();
        int width = (int)(bounds.getWidth() * screen.getOutputScaleX());
        int height = (int)(bounds.getHeight() * screen.getOutputScaleY());
        WritableImage image = new WritableImage(width, height);
        robot.getScreenCapture(image, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight(), false);
        return image;
    }

    private Image getScreenImageMac() {
        try {
            String exe = "/usr/sbin/screencapture";
            String imageFilePath = Util.TMP_DIRECTORY.toAbsolutePath().toString() + "/" + UUID.randomUUID() + ".png";
            logger.info("imageFilePath: " + imageFilePath);

            ProcessBuilder processBuilder = new ProcessBuilder(exe, "-i", "-x", imageFilePath);
            Process process = processBuilder.start();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                return null;
            }
            logger.info("exitValue: " + exitValue);

            InputStream inputStream = new FileInputStream(imageFilePath);
            Image image = new Image(inputStream);
            return image;
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    public static void main(String[] args) {

        launch();
    }

}