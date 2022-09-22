package kings.gui;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    protected static void switchScene(Control event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxml)));
        Stage stage = (Stage) event.getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("EntranceScene.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Kings in the Corner");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}