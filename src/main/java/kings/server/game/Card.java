package kings.server.game;

import java.io.Serializable;

public class Card implements Serializable {
    private int number;
    private String color;
    private String name;

    public Card(int num, String name){
        number = num;
        color = name;

        // name looks like: hearts2 (<color><number>)
        this.name = color + number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public boolean isBlack() {
        // if the card is a spades or clubs, it is a black card
        return (color.equals("spades") || color.equals("clubs"));
    }

    public String getColor(){
        return this.color;
    }

}
