package kings.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kings.client.Player;

import java.io.IOException;
import java.rmi.NotBoundException;

public class RegistrierungController {

    @FXML private PasswordField passwort, passwort_repeated;
    @FXML private TextField ipaddress, benutzername;
    @FXML private Button registrieren, back;
    @FXML private Label infoText;

    @FXML
    protected void onRegistrierenClicked() throws IOException, NotBoundException {
        String ip = ipaddress.getText();
        String name = benutzername.getText();
        String pwd = passwort.getText();
        String pwd_rpt = passwort_repeated.getText();

        Player player = new Player(name, ip);

        if (name.isBlank() || pwd.isBlank() || pwd_rpt.isBlank() || !pwd.equals(pwd_rpt)) {
            infoText.setText("Either one field is blank or passwords don't match");
            infoText.setVisible(true);

        } else if (!player.register(pwd)) {
            infoText.setText("Username already taken");
            infoText.setVisible(true);
        }

        else {
            infoText.setText("Sucessfully registered!");
            player.login(pwd);
            infoText.setVisible(true);
            PlayerHolder playerHolder = PlayerHolder.getInstance();
            playerHolder.setPlayer(player);
            Main.switchScene(registrieren, "LobbyScene.fxml");
        }
    }

    @FXML
    public void onBackClicked() throws IOException {
        Main.switchScene(back, "EntranceScene.fxml");
    }

    @FXML
    public void onNBClicked() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Nutzungsbedingungen");
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        Label label = new Label("Lorem ipsum \n Beim Clicken auf Registrieren stimmi der Benutzer mit allen Nutzungsbedingungen zu.");
        Button button = new Button("OK");
        button.setOnAction(event -> stage.close());
        layout.getChildren().addAll(label, button);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
