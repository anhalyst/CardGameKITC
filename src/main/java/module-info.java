module gui.gui {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.rmi;
        requires java.sql;
        requires org.junit.jupiter.api;


        opens kings.gui to javafx.fxml;
        exports kings.gui;
        exports kings.server.chat;
        exports kings.client;
        exports kings.server.administration;
        exports kings.server.game;
        exports kings.server.room;
}