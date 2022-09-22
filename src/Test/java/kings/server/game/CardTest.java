package kings.server.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    Card card1 = new Card(4, "hearts");
    Card card2 = new Card(6, "clubs");
    Card card3 = new Card(3, "spades");
    Card card4 = new Card(13, "diamonds");

    @Test
    void getName() {
        assert(card1.getName().equals("hearts4"));
        assert(card2.getName().equals("clubs6"));
        assert(card3.getName().equals("spades3"));
        assert(card4.getName().equals("diamonds13"));
    }

    @Test
    void getNumber() {
        assert(card1.getNumber()==4);
        assertTrue(card2.getNumber()==6);
        assertTrue(card3.getNumber()==3);
        assertTrue(card4.getNumber()==13);
    }

    @Test
    void isBlack() {
        assertFalse(card1.isBlack());
        assertFalse(card4.isBlack());
        assertTrue(card2.isBlack());
        assertTrue(card3.isBlack());
    }

    @Test
    void getColor() {
        assert(card1.getColor()=="hearts");
    }
}