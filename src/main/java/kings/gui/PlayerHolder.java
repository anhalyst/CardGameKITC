package kings.gui;

import kings.client.Player;

public final class PlayerHolder {
    private Player player;
    private final static PlayerHolder instance = new PlayerHolder();

    private PlayerHolder() {};

    public static PlayerHolder getInstance() {
        return instance;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
