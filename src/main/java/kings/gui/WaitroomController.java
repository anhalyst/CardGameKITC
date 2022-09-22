package kings.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import kings.client.Player;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class WaitroomController implements Initializable {
    @FXML private Label room_name;
    @FXML private ScrollPane chat_scroll_pane;
    @FXML private VBox chat_box;
    @FXML private Button send_button;
    @FXML private TextField message_box;
    @FXML private VBox player_box;
    @FXML private Button leaveRoomButton;
    @FXML private Button refresh;
    @FXML private Button start;
    private Player player = PlayerHolder.getInstance().getPlayer();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            room_name.setText("Raum " + player.getCurrentRoom());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        chat_scroll_pane.setFitToWidth(true);
        chat_box.heightProperty().addListener((observable, oldValue, newValue) -> chat_scroll_pane.setVvalue((Double) newValue));

        player.newMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue) {
                String name = player.getNewMessage().getValue();
                String msg = player.getNewMessage().getKey();
                handleIncomingMessage(msg, name);
                player.setNewMessage(false);
            }
        });

        send_button.setOnAction(event -> {
            String msg = message_box.getText();
            if (!msg.isEmpty()) {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);

                Text text = new Text(player.getName() + ": " + msg);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setPadding(new Insets(5));
                textFlow.setStyle("-fx-color: rgb(239, 242, 255)");
                textFlow.setStyle("-fx-background-color: rgb(15, 125, 242)");
                text.setFill(Color.color(0.934, 0.945, 0.996));

                hBox.getChildren().add(textFlow);
                chat_box.getChildren().add(hBox);

                try {
                    player.sendMsg(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                message_box.clear();
            }
        });

        leaveRoomButton.setOnAction(event -> {
            try {
                player.leaveGameRoom();
                switchToLobby();
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {

            }
        });

        refresh.setOnAction(event -> {
            player_box.getChildren().clear();
            HBox hBox = new HBox();
            try {
                Text text = new Text(player.getPlayersList());
                hBox.getChildren().add(text);
                player_box.getChildren().add(hBox);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        refresh.fire();

        start.setOnAction(event -> {
            try {
                player.startGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        player.startedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    try {
                        switchToGameroom();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void handleIncomingMessage(String msg, String name) {
        if (!msg.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);

            Text text = new Text(name + ": " + msg);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setPadding(new Insets(5));
            textFlow.setStyle("-fx-background-color: rgb(128, 128, 128)");
            textFlow.setStyle("-fx-color: rgb(233, 233, 235)");

            hBox.getChildren().add(textFlow);
            Platform.runLater(() -> chat_box.getChildren().add(hBox));
        }
    }

    private void switchToLobby() throws IOException {
        Main.switchScene(leaveRoomButton, "LobbyScene.fxml");
    }

    private void switchToGameroom() throws IOException {
        Main.switchScene(start, "GameroomScene.fxml");
    }
}
