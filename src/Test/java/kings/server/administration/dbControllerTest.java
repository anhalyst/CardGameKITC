package kings.server.administration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class dbControllerTest {

    private dbController dbController;

    @Test
    void insertNewPlayer() {
        dbController = new dbController("db");
        dbController.insertNewPlayer("player","1234");

    }

    @Test
    void deletePlayer() {
        dbController = new dbController("db");
        dbController.deletePlayer("player");
    }

    @Test
    void passwordCheck() {
        dbController = new dbController("db");
        dbController.passwordCheck("palyer", "1234");
    }

    @Test
    void nameAlreadyExists() {
        dbController = new dbController("db");
        dbController.nameAlreadyExists("player");

    }

    @Test
    void topHighScore() {
        dbController = new dbController("db");
        dbController.topHighScore(2);
    }

    @Test
    void playerScore() {
        dbController = new dbController("db");
        dbController.playerScore("player");
    }

    @Test
    void updateScore() {
        dbController = new dbController("db");
        dbController.updateScore("player",2);
    }

    @Test
    void closeConnection() {
        dbController = new dbController("db");
        dbController.closeConnection();
    }

    @Test
    void updatePassword() {
        dbController = new dbController("db");
        dbController.updatePassword("1234", "1234");
    }


}