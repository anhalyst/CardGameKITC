package kings.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML private ScrollPane scroll_pane;
    @FXML private Button ok;
    @FXML private VBox vbox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scroll_pane.setFitToWidth(true);

        Text text = new Text("OBJECT: To win a hand by being the first to get rid of all your cards, and ultimately to accumulate 100 points to win the game.\n" +
                "\n" +
                "SET-UP:\n" +
                "\n" +
                "Distribute the 80 playing chips as equally as possible amongst the number of players.\n" +
                "Shuffle cards thoroughly.\n" +
                "Deal SEVEN cards face down to each player, one at a time from the dealer’s left.\n" +
                "Load control center tray with one card in all four tabbed stalls. Each card must be face up under tab. Do not place cards in Kings stall at this time. If Kings show up under tabbed stalls, they should remain there until first player makes the transfer in his normal play.\n" +
                "Place remaining cards face down over chip pot forming draw deck.\n" +
                "Players are to take cards dealt them and hold in fan position in hand.\n" +
                "Game is now properly set up for play.\n" +
                "\n" +
                "PLAY: To begin each hand, each player is to place one chip in the chip pot. Player to the left of the dealer starts first by placing a playable card or cards into the tabbed stalls or Kings stalls, and/or making any other moves available. Cards played in all stalls must be played in alternate colors. (Red, Black, Red or Black, Red, Black) and must be lower in sequence than the card on which it is played (K, Q, J, 10, 9, etc., to Ace which is low). Card suit is not relevant.\n" +
                "\n" +
                "If a King is dealt to player or drawn from the deck, player must immediately place it in an open Kings stall (Dia. A). The player also has the privilege of moving a King that might be under the tab stall from the initial deal into a Kings stall. Should it be discovered that you are holding a King in your hand without playing same at your first opportunity, you then must pay a three chip penalty to the chip pot.\n" +
                "\n" +
                "Plays to Kings stalls must be started with Kings and cards may not be moved from Kings stalls. You may move all cards in one lot from any tab stall to any other stall provided that the bottom card is playable. When a tab stall is open, you may insert a card of your choice from your hand. On a turn, a player may continue as long as plays are possible, but is not forced to make a play he/she does not wish to (except for forced play of Kings). If a player does not make at least one play, he/she must place one chip into the chip pot. At the end of each turn, player draws the top card from the draw pile. If player neglects to draw a card at the end of their turn, player must pay one chip penalty to the pot. If card is a King it must be played immediately and you do not draw again.\n" +
                "\n" +
                "After one player wins the hand by being the first to get rid of all his/her cards, the other players put one chip in the pot for each of the cards remaining in their hand. The winner of the hand receives all the chips in the pot and records the number of chips for his/her score. Each chip is worth one point. All the chips are then collected to be redistributed for the next hand to be played.\n" +
                "\n" +
                "The first player to accumulate 100 points is the winner of the game.\n" +
                "\n" +
                "NOTE: Cards are placed in the Kings stalls so that they completely cover the card below. (See Dia. A) Cards are placed in the tab stalls so that only the bottom card goes under the tab and additional cards stack up to always leave the bottom and top card exposed. No other cards should be visible.");
        TextFlow textFlow = new TextFlow(text);
        HBox hBox = new HBox();
        hBox.getChildren().add(textFlow);
        vbox.getChildren().add(hBox);
        ok.setOnAction(event -> {
            try {
                Main.switchScene(ok, "LobbyScene.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
