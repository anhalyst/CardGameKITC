package kings.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class EntranceController {

    @FXML Button anmelden, registrieren;

    @FXML
    protected void onAnmeldenClicked() throws IOException {
        Main.switchScene(anmelden, "AnmeldungScene.fxml");
    }

    @FXML
    public void onRegistrierenClicked() throws IOException {
        Main.switchScene(registrieren, "RegistrierungScene.fxml");
    }
}
