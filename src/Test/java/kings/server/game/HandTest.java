package kings.server.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    Card card1 = new Card(4, "hearts");
    Card card2 = new Card(6, "clubs");
    Card card3 = new Card(3, "spades");
    Card card4 = new Card(13, "diamonds");
    Card card5 = new Card(7, "spades");
    ArrayList<Card> cards1 = new ArrayList<>();


    @Test
    void getCards() {

        cards1.add(card1);
        cards1.add(card2);
        cards1.add(card3);
        Hand h1 = new Hand(cards1);
        h1.getCards();

        assertTrue(h1.getCards().size()==3);

    }

    @Test
    void addCard() {
        cards1.add(card1);
        cards1.add(card2);
        cards1.add(card3);
        Hand h1 = new Hand(cards1);
        h1.addCard(card4);
        assertTrue(h1.getCards().contains(card4));
        assertFalse(h1.getCards().contains(card5));
        h1.addCard(card5);
        assertTrue(h1.getCards().contains(card5));

    }


    @Test
    void removeCard() {
        cards1.add(card1);
        cards1.add(card2);
        cards1.add(card3);
        Hand h1 = new Hand(cards1);
        h1.addCard(card4);
        assertTrue(h1.getCards().contains(card4));
        assertFalse(h1.getCards().contains(card5));
        h1.addCard(card5);
        assertTrue(h1.getCards().contains(card5));
        h1.removeCard(card5);
    }
}
