package kings.gui;

import Exceptions.SpielraumVollExeption;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kings.client.Player;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class LobbyController implements Initializable {

    @FXML private ScrollPane chat_scroll_pane;
    @FXML private ScrollPane room_scroll_pane;
    @FXML private VBox chat_box;
    @FXML private VBox room_box;
    @FXML private Button send_button;
    @FXML private TextField message_box;
    @FXML private Button createRoomButton;
    @FXML private Button refresh;
    @FXML private Button delete_acc;
    @FXML private Button change_pwd;
    @FXML private Button help, agb, logout;
    @FXML private Text t01, t02, t11, t12, t21, t22, t31, t32, t41, t42;

    private Player player = PlayerHolder.getInstance().getPlayer();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            player.updateRoomList();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
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

        room_scroll_pane.setFitToWidth(true);
        room_box.heightProperty().addListener((observable, oldValue, newValue) -> room_scroll_pane.setVvalue((Double) newValue));

        refresh.setOnAction(event1 -> {
            room_box.getChildren().clear();
            try {
                displayScore();
                for (Map.Entry<String, String> room : player.getGameRoomList()) {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    Text text = new Text("Raum " + room.getKey() + room.getValue());
                    Button button = new Button("Beitreten");
                    button.setOnAction(event2 -> {
                        try {
                            player.enterGameRoom(room.getKey());
                            switchToGameroom();
                        } catch (IOException | NotBoundException e) {
                            e.printStackTrace();
                        }
                        catch (SpielraumVollExeption e) {
                            displayInfo("Raum ist suuuuuuuuper voll jetzt!");
                            event2.consume();
                        }
                    });
                    hbox.getChildren().add(button);
                    hbox.getChildren().add(text);
                    room_box.getChildren().add(hbox);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        refresh.fire();

        delete_acc.setOnAction(event -> {
            try {
                if (popup_delete()) {
                    switchToEntranceRoom();
                    player.deleteAccount();
                }
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
        });

        change_pwd.setOnAction(event -> {
            ArrayList<String> passwords = popup_pwd();
            if (passwords.isEmpty()) return;
            boolean query = false;
            try {
                query = player.updatePassword(passwords.get(0), passwords.get(1));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (!query) {
                displayInfo("Falsches Passwort");
            }
        });

        help.setOnAction(event -> {
            try {
                switchToHelpScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        logout.setOnAction(event -> {
            try {
                Main.switchScene(logout, "EntranceScene.fxml");
                player.logout();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotBoundException ignore){

            }
        });

        agb.setOnAction(event -> displayInfo("""
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed euismod et lorem et tristique. Vestibulum vitae velit eu purus eleifend tincidunt quis ut leo. Proin convallis nisl nec felis finibus volutpat. Nunc ac efficitur libero, eu pretium leo. Morbi rutrum nunc a ligula sodales, et vestibulum odio dapibus. Vestibulum vel accumsan lacus, in consequat arcu. Suspendisse ut elit urna. Ut sed dolor consectetur, viverra leo scelerisque, vestibulum lorem. Nunc pellentesque quam at auctor volutpat. Maecenas ullamcorper leo ex, consequat vulputate ipsum sollicitudin a. Fusce diam massa, lobortis in ornare sed, accumsan id tellus.

                Fusce a sapien est. Phasellus commodo erat in hendrerit condimentum. Nulla facilisi. Donec pretium, tortor id vestibulum auctor, risus dolor aliquet odio, sit amet congue mi velit non ante. Phasellus quis massa fermentum, dapibus odio ac, dictum justo. Ut varius justo nisi, vitae tristique mauris malesuada in. Nam tincidunt eros in vulputate cursus. Etiam in luctus neque. Phasellus mollis diam nec iaculis condimentum. Quisque tristique ultricies magna eget laoreet. Integer quis commodo sapien. Nam pharetra ornare viverra. Nulla nisl nulla, mollis a scelerisque at, tincidunt at leo. Aenean placerat turpis sit amet lorem viverra, ut condimentum purus mattis.

                Aliquam eget turpis sit amet massa rutrum interdum. Nullam aliquet bibendum lorem venenatis varius. Fusce vehicula, elit nec bibendum accumsan, risus turpis lacinia ex, in sagittis tellus metus nec lacus. Aliquam enim ipsum, fermentum quis suscipit ac, condimentum nec nisl. Aliquam sagittis est metus, non efficitur enim tempor nec. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Etiam at sapien quis dolor laoreet tristique ut ac tortor. Nulla lacus neque, euismod vitae lorem eget, ultrices eleifend odio. Nunc mauris libero, fermentum in ultricies eget, lobortis id urna. Etiam tincidunt odio id mi interdum congue. Nulla consectetur lacus eu augue condimentum, vel dignissim quam elementum. Maecenas mollis pellentesque ipsum eu laoreet. Sed bibendum congue pellentesque. Sed congue risus interdum mollis commodo. Nullam molestie condimentum egestas.

                Phasellus molestie finibus magna, vitae vestibulum lacus ultricies eu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Quisque tincidunt velit non nibh tempus consectetur. Nullam finibus lobortis est nec accumsan. Proin dignissim, tellus at finibus ullamcorper, massa mi mollis dolor, sed varius massa erat sit amet dui. Donec diam arcu, consectetur at tempus id, laoreet vitae nisi. Aenean in velit mattis, suscipit risus quis, semper nisi. Mauris et lacinia felis. Praesent vulputate aliquet hendrerit. Aliquam erat volutpat.

                Proin bibendum fringilla tortor non interdum. Nulla ante turpis, suscipit eget nisl nec, vulputate porttitor erat. Sed posuere id risus at tincidunt. Praesent eleifend, sapien et aliquam rutrum, lorem nulla consequat odio, a volutpat turpis nulla ut magna. Maecenas diam turpis, elementum ac sem ut, iaculis ornare est. Donec et mattis lorem, molestie placerat nulla. Vivamus dui justo, scelerisque ac maximus in, efficitur gravida mauris. Morbi at nibh ultricies, cursus est congue, aliquet lectus. Cras sed feugiat urna, suscipit malesuada ante. Cras porttitor venenatis facilisis. Curabitur aliquam, nulla a vehicula aliquet, est nibh eleifend lorem, id vulputate mauris mi nec mi. Donec rutrum lorem quis ante varius tempor. Ut malesuada, elit non dapibus finibus, risus diam convallis nunc, sit amet ornare nibh nisi et leo. Suspendisse quis leo congue, blandit diam eget, gravida est."""));
    }

    private void displayInfo(String message) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Info");
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        Label label = new Label(message);
        Button button = new Button("OK");
        button.setOnAction(event -> stage.close());
        layout.getChildren().addAll(label, button);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
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

    @FXML
    public void onCreateRoomClicked() throws IOException {
        Main.switchScene(createRoomButton, "CreateRoomScene.fxml");
    }

    private void switchToGameroom() throws IOException {
        Main.switchScene(createRoomButton, "WaitroomScene.fxml");
    }

    private boolean popup_delete() throws IOException {
        AtomicBoolean answer = new AtomicBoolean(false);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Warnung");
        stage.setMinWidth(250);
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));
        Label label = new Label("Möchtest du das Konto wirklich löschen?");
        Button ja = new Button("Ja");
        ja.setOnAction(event -> {
            answer.set(true);
            stage.close();
        });
        Button nein = new Button("Nein");
        nein.setOnAction(event -> {
            answer.set(false);
            stage.close();
        });
        layout.getChildren().add(label);
        layout.getChildren().add(ja);
        layout.getChildren().add(nein);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
        return answer.get();
    }

    private ArrayList<String> popup_pwd() {
        ArrayList<String> answer = new ArrayList<>();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Passwort ändern");
        stage.setMinWidth(250);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        Label old_label = new Label("Altes Passwort eingeben: ");
        grid.add(old_label, 0, 0);

        PasswordField old_pwdField = new PasswordField();
        grid.add(old_pwdField, 1, 0);

        Label new_label = new Label("Neues Passwort eingeben: ");
        grid.add(new_label, 0, 1);

        PasswordField new_pwdField = new PasswordField();
        grid.add(new_pwdField, 1, 1);

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);

        Button confirm = new Button("Bestätigen");
        confirm.setOnAction(event -> {
            String old_pwd = old_pwdField.getText();
            answer.add(old_pwd);
            String new_pwd = new_pwdField.getText();
            answer.add(new_pwd);
            stage.close();
        });

        Button cancel = new Button("Abbrechen");
        cancel.setOnAction(event -> stage.close());

        hBox.getChildren().addAll(confirm, cancel);

        layout.getChildren().addAll(grid, hBox);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
        return answer;
    }

    private void displayScore() throws RemoteException {
        ArrayList<Map.Entry<String, Integer>> scoreList = player.topHighScore();
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0 -> {
                    Map.Entry<String, Integer> entry = scoreList.get(i);
                    t01.setText(entry.getKey());
                    t02.setText(String.valueOf(entry.getValue()));
                }
                case 1 -> {
                    Map.Entry<String, Integer> entry = scoreList.get(i);
                    t11.setText(entry.getKey());
                    t12.setText(String.valueOf(entry.getValue()));
                }
                case 2 -> {
                    Map.Entry<String, Integer> entry = scoreList.get(i);
                    t21.setText(entry.getKey());
                    t22.setText(String.valueOf(entry.getValue()));
                }
                case 3 -> {
                    Map.Entry<String, Integer> entry = scoreList.get(i);
                    t31.setText(entry.getKey());
                    t32.setText(String.valueOf(entry.getValue()));
                }
                case 4 -> {
                    Map.Entry<String, Integer> entry = scoreList.get(i);
                    t41.setText(entry.getKey());
                    t42.setText(String.valueOf(entry.getValue()));
                }
            }
        }
    }

    private void switchToEntranceRoom() throws IOException {
        Main.switchScene(delete_acc, "AnmeldungScene.fxml");
    }

    private void switchToHelpScene() throws IOException {
        Main.switchScene(delete_acc, "HelpScene.fxml");
    }
}
