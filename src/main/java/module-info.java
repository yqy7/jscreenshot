module com.github.yqy7.jscreenshot {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    opens com.github.yqy7.jscreenshot to javafx.fxml;
    exports com.github.yqy7.jscreenshot;
}