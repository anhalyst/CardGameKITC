package kings.gui;

import Exceptions.SpielraumExistiertExeption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kings.client.Player;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.util.ResourceBundle;

public class CreateRoomController implements Initializable {
    @FXML private TextField name;
    @FXML private ChoiceBox<Integer> capacity;
    @FXML private ChoiceBox<String> gametype;
    @FXML private ChoiceBox<String> difficulty;
    @FXML private Button back;
    @FXML private Button create;
    @FXML private Label errorText;
    private final Integer[] capacityOption = {2,3,4,5,6};
    private final String[] gametypeOption = {"Mit Spielern", "Mit Bots"};
    private final String[] difficultyOption = {"Einfach", "Schwer"};
    private Player player = PlayerHolder.getInstance().getPlayer();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        capacity.getItems().addAll(capacityOption);
        capacity.setValue(2);
        gametype.getItems().addAll(gametypeOption);
        gametype.setValue("Mit Spielern");
        difficulty.getItems().addAll(difficultyOption);
        difficulty.setValue("Einfach");
    }

    // Difficulty not yet implemented
    @FXML
    protected void onCreateClicked() throws NotBoundException, IOException, SpielraumExistiertExeption {
        String nam = name.getText();
        int cap = capacity.getValue();
        String type = gametype.getValue();
        String diff = difficulty.getValue();
        boolean created;
        if (type.equals("Mit Bots")) {
            int amountBots = cap - 1;
            if (diff.equals("Einfach")) {
                created = player.createGameRoom(player, nam, cap, amountBots, true);
            }
            else {
                created = player.createGameRoom(player, nam, cap, amountBots, false);
            }
        }
        else {
            created = player.createGameRoom(player, nam, cap, 0, true);
        }
        if (created) {
            switchToWaitroom();
        }
        else errorText.setVisible(true);
    }

    @FXML
    protected void onZurueckClicked() throws IOException {
        switchToLobby();
    }

    protected void switchToLobby() throws IOException {
        Main.switchScene(back, "LobbyScene.fxml");
    }

    private void switchToWaitroom() throws IOException {
        Main.switchScene(create, "WaitroomScene.fxml");
    }

}
