package kings.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import kings.client.Player;
import kings.server.game.Card;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameroomController implements Initializable {
    @FXML private VBox players_box;
    @FXML private VBox chat_box;
    @FXML private ScrollPane chat_scroll_pane;
    @FXML private TextField message_box;
    @FXML private Button send_button;
    @FXML private Label room_name;
    @FXML private Button quit_button;
    @FXML private ScrollPane card_scroll_pane;
    @FXML private HBox card_box;
    @FXML private GridPane grid_pane;
    @FXML private Button end_turn_button;
    @FXML private Label punkte;
    @FXML private Label chips;
    @FXML private Label pot;
    @FXML private Label infotext;
    @FXML private ImageView front_img_stall_0;
    @FXML private ImageView front_img_stall_1;
    @FXML private ImageView front_img_stall_2;
    @FXML private ImageView front_img_stall_3;
    @FXML private ImageView front_img_stall_4;
    @FXML private ImageView front_img_stall_5;
    @FXML private ImageView front_img_stall_6;
    @FXML private ImageView front_img_stall_7;
    @FXML private ImageView img_deck;
    @FXML private ImageView img_stall_0;
    @FXML private ImageView img_stall_1;
    @FXML private ImageView img_stall_2;
    @FXML private ImageView img_stall_3;
    @FXML private ImageView img_stall_4;
    @FXML private ImageView img_stall_5;
    @FXML private ImageView img_stall_6;
    @FXML private ImageView img_stall_7;
    private Player player = PlayerHolder.getInstance().getPlayer();
    private SimpleListProperty<Map.Entry<String, String>> actionMap = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

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

        card_scroll_pane.setFitToHeight(true);
        card_box.widthProperty().addListener(((observable, oldValue, newValue) -> card_scroll_pane.setHvalue((Double) newValue)));
        card_box.setMouseTransparent(false);

        try {
            displayCards(getCards());
            displayStalls(getStalls());
            displayPlayers(getPlayers());
            displayMyChipsInfo(getMyChipsInfo());
            displayPot(getPot());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        actionMap.addListener(((observable, oldValue, newValue) -> {
            System.out.println(actionMap);
            int size = actionMap.size();
            try {
                if (!player.isMyTurn()) {
                    displayInfo("Du bist nicht dran!");
                    return;
                }
                if (size % 2 == 0 && size != 0) {
                    Map.Entry<String, String> fst = actionMap.get(size - 2);
                    Map.Entry<String, String> snd = actionMap.get(size - 1);
                    String fstValue = fst.getValue();
                    String sndValue = snd.getValue();
                    String fstKey = fst.getKey();
                    String sndKey = snd.getKey();
                    if (!fstValue.equals("Hand") && !sndValue.equals("Hand")) {
                        int fstStall = Integer.parseInt(fstValue);
                        int sndStall = Integer.parseInt(sndValue);
                        if ("4567".contains(fstValue) && "0123".contains(sndValue)) {
                            displayInfo("King stall darf nicht in tabbed stall verschieben werden!");
                        } else {
                            if (player.changeStack(fstStall, sndStall)) {
                                displayCards(getCards());
                                displayStalls(getStalls());
                            } else {
                                displayInfo("Invalider Zug! Hast du vielleicht noch einen König im Hand?");
                            }
                        }
                    } else if (fstValue.equals("Hand") && !sndValue.equals("Hand")) {
                        for (Card c : player.getCards()) {
                            if (c.getName().equals(fstKey)) {
                                if (player.addCard(c, Integer.parseInt(sndValue))) {
                                    displayCards(getCards());
                                    displayStalls(getStalls());
                                } else {
                                    displayInfo("Die Karte " + fstKey + " kann nicht nach " + sndKey + " eingefügt werden! Hast du vielleicht noch einen König im Hand?");
                                }
                            }
                        }
                    } else {
                        displayInfo("Invalider Zug! Hast du vielleicht noch einen König im Hand?");
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }));

        end_turn_button.setOnAction(event -> {
            try {
                if (!player.isMyTurn()) {
                    displayInfo("Du bist nicht dran!");
                    return;
                }
                boolean endedTurn = player.endTurn();
                if (!endedTurn) displayInfo("Dein Zug kann noch nicht beendet werden, hast du vielleicht noch einen König im Hand?");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        player.newTurnProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
//                actionMap.get().clear();
                try {
                    ArrayList<VBox> hand = getCards();
                    ArrayList<Pair<Image, String>> images = getStalls();
                    ArrayList<HBox> players = getPlayers();
                    Pair<Integer, Integer> pair = getMyChipsInfo();
                    int p = getPot();
                    boolean isMyTurn = player.isMyTurn();
                    Platform.runLater(() -> {
                        try {
                            infotext.setText(isMyTurn ? "Du bist dran!" : "Du bist nicht dran!");
                            displayCards(hand);
                            displayStalls(images);
                            displayPlayers(players);
                            displayMyChipsInfo(pair);
                            displayPot(p);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                    player.setNewTurn(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    try {
                        player.setGameEnded(true);
                    } catch (RemoteException ex) {
                        e.printStackTrace();
                    }
                }
            }
        });

        quit_button.setOnAction(event -> {
            try {
                player.leaveGameRoom();
                Main.switchScene(quit_button, "LobbyScene.fxml");
            } catch (NotBoundException | IOException e) {
                e.printStackTrace();
            }
        });

        player.gameEndedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                VBox layout = new VBox(15);
                layout.setPadding(new Insets(10));
                layout.setAlignment(Pos.CENTER);
                Label label = new Label("Jemand hat gewonnen!");
                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Info");

                    Button button = new Button("OK");
                    button.setOnAction(event -> {
                        stage.close();
                        Platform.runLater(() -> {
                            try {
                                Main.switchScene(quit_button, "LobbyScene.fxml");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                    layout.getChildren().addAll(label, button);
                    Scene scene = new Scene(layout);
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.showAndWait();
                });
            }
        });

//        quit_button.getScene().getWindow().setOnCloseRequest(event -> {
//            try {
//                player.leaveGameRoom();
//            } catch (NotBoundException | RemoteException e) {
//                e.printStackTrace();
//            }
//        });
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

    private ArrayList<Pair<Image, String>> getStalls() throws RemoteException {
        // 0 -> 7: End of stalls; 8 -> 15: Front of stalls
        ArrayList<Pair<Image, String>> result = new ArrayList<>();
        ArrayList<Card> back_stalls = player.getLastCardsEachStall();
        ArrayList<Card> front_stalls = player.getFirstCardsEachStall();
        for (int i = 0; i < 8; i++) {
            Card c = back_stalls.get(i);
            if (c != null) {
                String card_name = c.getName();
                String location = "cards/" + card_name + ".png";
                Image image = new Image(String.valueOf(Main.class.getResource(location)));
                result.add(new Pair<>(image, card_name));
            }
            else result.add(new Pair<>(null, "null"));
        }
        for (int i = 0; i < 8; i++) {
            Card c = front_stalls.get(i);
            if (c != null) {
                String card_name = c.getName();
                String location = "cards/" + card_name + ".png";
                Image image = new Image(String.valueOf(Main.class.getResource(location)));
                result.add(new Pair<>(image, card_name));
            }
            else result.add(new Pair<>(null, "null"));
        }
        return result;
    }

    private void displayStalls(ArrayList<Pair<Image, String>> images) throws RemoteException {
        for (int i = 0; i < 16; i++) {
            Image image = images.get(i).getKey();
            String card_name = images.get(i).getValue();
            switch (i) {
                case 0 -> {
                    img_stall_0.setImage(image);
                    img_stall_0.setId(card_name);
                    img_stall_0.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 0");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "0"));
                    });
                }
                case 1 -> {
                    img_stall_1.setImage(image);
                    img_stall_1.setId(card_name);
                    img_stall_1.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 1");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "1"));
                    });
                }
                case 2 -> {
                    img_stall_2.setImage(image);
                    img_stall_2.setId(card_name);
                    img_stall_2.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 2");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "2"));
                    });
                }
                case 3 -> {
                    img_stall_3.setImage(image);
                    img_stall_3.setId(card_name);
                    img_stall_3.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 3");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "3"));
                    });
                }
                case 4 -> {
                    img_stall_4.setImage(image);
                    img_stall_4.setId(card_name);
                    img_stall_4.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 4");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "4"));
                    });
                }
                case 5 -> {
                    img_stall_5.setImage(image);
                    img_stall_5.setId(card_name);
                    img_stall_5.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 5");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "5"));
                    });
                }
                case 6 -> {
                    img_stall_6.setImage(image);
                    img_stall_6.setId(card_name);
                    img_stall_6.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 6");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "6"));
                    });
                }
                case 7 -> {
                    img_stall_7.setImage(image);
                    img_stall_7.setId(card_name);
                    img_stall_7.setOnMouseClicked(event -> {
                        ImageView source = (ImageView) event.getSource();
                        String name = source.getId();
                        infotext.setText(name + " geclickt");
                        System.out.println("Clicked on " + name + " in stall 7");
                        actionMap.add(new AbstractMap.SimpleEntry<>(name, "7"));
                    });
                }
                case 8 -> {
                    if (!img_stall_0.getId().equals(card_name)) front_img_stall_0.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_0.setImage(image);
                    else front_img_stall_0.setImage(null);
                }
                case 9 -> {
                    if (!img_stall_1.getId().equals(card_name)) front_img_stall_1.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_1.setImage(image);
                    else front_img_stall_1.setImage(null);
                }
                case 10 -> {
                    if (!img_stall_2.getId().equals(card_name)) front_img_stall_2.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_2.setImage(image);
                    else front_img_stall_2.setImage(null);
                }
                case 11 -> {
                    if (!img_stall_3.getId().equals(card_name)) front_img_stall_3.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_3.setImage(image);
                    else front_img_stall_3.setImage(null);
                }
                case 12 -> {
                    if (!img_stall_4.getId().equals(card_name)) front_img_stall_4.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_4.setImage(image);
                    else front_img_stall_4.setImage(null);
                }
                case 13 -> {
                    if (!img_stall_5.getId().equals(card_name)) front_img_stall_5.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_5.setImage(image);
                    else front_img_stall_5.setImage(null);
                }
                case 14 -> {
                    if (!img_stall_6.getId().equals(card_name)) front_img_stall_6.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_6.setImage(image);
                    else front_img_stall_6.setImage(null);
                }
                case 15 -> {
                    if (!img_stall_7.getId().equals(card_name)) front_img_stall_7.setImage(image);
                    else if (card_name.equals("null")) front_img_stall_7.setImage(image);
                    else front_img_stall_7.setImage(null);
                }
            }
        }
    }

    private ArrayList<VBox> getCards() throws RemoteException {
        ArrayList<VBox> result = new ArrayList<>();
        ArrayList<Card> hand = player.getCards();
        for (Card c: hand) {
            String location = "cards/" + c.getName() + ".png";
            VBox vBox = new VBox();
            ImageView card_img = new ImageView(String.valueOf(Main.class.getResource(location)));
            card_img.setId(c.getName());
            card_img.setOnMouseClicked(event -> {
                ImageView source = (ImageView) event.getSource();
                String card_name = source.getId();
                infotext.setText(card_name + " geclickt");
                System.out.println("Clicked on " + card_name + " on player's hand");
                actionMap.add(new AbstractMap.SimpleEntry<>(card_name, "Hand"));
            });
            card_img.setPreserveRatio(true);
            card_img.fitHeightProperty().bind(card_scroll_pane.minHeightProperty());
            vBox.getChildren().add(card_img);
            result.add(vBox);
        }
        return result;
    }

    private void displayCards(ArrayList<VBox> card_vBoxes) throws RemoteException {
        card_box.getChildren().clear();
        for (VBox vBox: card_vBoxes) {
            card_box.getChildren().add(vBox);
        }
    }

    private ArrayList<HBox> getPlayers() throws RemoteException {
        ArrayList<HBox> result = new ArrayList<>();
        LinkedHashMap<String, Pair<Integer, Integer>> chipsInfo = player.chipsInfo();
        LinkedHashMap<String, Boolean> turnInfo = player.turnInfo();
        TreeSet<String> players = new TreeSet<>(turnInfo.keySet());
        for (String p: players) {
            if (!p.equals(player.getName())) {
                Pair<Integer, Integer> pair = chipsInfo.get(p);
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                String chip = pair != null? String.valueOf(pair.getKey()) : "?";
                String punkte = pair != null? String.valueOf(pair.getValue()) : "?";
                Text text = new Text(p + " - Chips: " + chip + " - Punkte: " + punkte);
                TextFlow textFlow = new TextFlow(text);
                if (turnInfo.get(p)) {
                    text.setFill(Color.RED);
                }
                textFlow.setPadding(new Insets(10));
                hBox.getChildren().add(textFlow);
                result.add(hBox);
            }
            else {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                Text text = new Text(p + " (Du)");
                TextFlow textFlow = new TextFlow(text);
                if (turnInfo.get(p)) {
                    text.setFill(Color.RED);
                }
                textFlow.setPadding(new Insets(10));
                hBox.getChildren().add(textFlow);
                result.add(hBox);
            }
        }
        return result;
    }

    private void displayPlayers(ArrayList<HBox> players_hBoxes) throws RemoteException {
        players_box.getChildren().clear();
        for (HBox hBox: players_hBoxes) {
            players_box.getChildren().add(hBox);
        }
    }

    private Pair<Integer, Integer> getMyChipsInfo() throws RemoteException {
        return player.chipsInfo().get(player.getName());
    }

    private void displayMyChipsInfo(Pair<Integer, Integer> pair) {

        chips.setText("Chips: " + (pair != null ? pair.getKey() : "?"));
        punkte.setText("Punkte: " + (pair != null ? pair.getValue() : "?"));

    }

    private int getPot() throws RemoteException {
        return player.getPot();
    }

    private void displayPot(int p) {
        pot.setText("Pots: " + p);
    }

    private void displayInfo(String message) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Warnung");
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
}
