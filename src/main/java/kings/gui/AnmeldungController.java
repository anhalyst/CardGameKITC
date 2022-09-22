package kings.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import kings.client.Player;

import java.io.IOException;
import java.rmi.NotBoundException;

public class AnmeldungController {
    @FXML private Button anmelden;
    @FXML private Button back;
    @FXML private Text infoText;
    @FXML private TextField ipaddress;
    @FXML private TextField benutzername;
    @FXML private PasswordField passwort;

    @FXML
    protected void onAnmeldenClicked() throws IOException, NotBoundException {
        String ip = ipaddress.getText();
        String name = benutzername.getText();
        String pwd = passwort.getText();

        Player player = new Player(name, ip);

        if (name.isBlank() || pwd.isBlank()) {
            infoText.setText("Name or Password should not be blank");
            infoText.setVisible(true);
        }
        else if (!player.login(pwd)){
            infoText.setText("Wrong credentials, or this account does not exist");
            infoText.setVisible(true);
        }

        else {
            PlayerHolder playerHolder = PlayerHolder.getInstance();
            playerHolder.setPlayer(player);
            switchToLobby();
        }
    }

    @FXML
    protected void onBackClicked() throws IOException {
        Main.switchScene(back, "EntranceScene.fxml");
    }

    protected void switchToLobby() throws IOException {
        Main.switchScene(anmelden, "LobbyScene.fxml");
    }

}