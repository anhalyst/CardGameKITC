package kings.server.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StallTest {

    Card card1 = new Card(1, "hearts");
    Card card2 = new Card(6, "clubs");
    Card card3 = new Card(3, "spades");
    Card card4 = new Card(5, "diamonds");
    Card card5 = new Card(2, "hearts");

    /**
     * Test to add the list of cards into the Stall when no constraints is violated
     */
    @Test
    void add() {
        ArrayList<Card> cards1 = new ArrayList<>();
        Stall s = new Stall(1);
        assertFalse(s.add(cards1));     // cards size =0
        cards1.add(card1);
        cards1.add(card2);
        //   System.out.println(s.lastCard());
        assertTrue(s.add(cards1));





    }

    @Test
    void testAdd() {
        ArrayList<Card> cards2 = new ArrayList<>();
        Stall st = new Stall(1);
        st.add(cards2);
        assertFalse(st.add(cards2));
        cards2.add(card1);
        cards2.add(card2);
        st.add(cards2);
        assertTrue(st.lastCard().getNumber()==6);
        st.add(card4);
        assertTrue(st.lastCard().getNumber()==5);







    }

    @Test
    void remove() {
        ArrayList<Card> cards2 = new ArrayList<>();
        Stall st = new Stall(1);
        st.add(cards2);
        assertFalse(st.add(cards2));
        cards2.add(card1);
        cards2.add(card2);
        st.add(cards2);
        st.add(card4);
        assertTrue(st.remove().size()==3);







    }

    @Test
    void lastCard() {
        ArrayList<Card> cards2 = new ArrayList<>();
        Stall st = new Stall(1);
        st.add(cards2);
        assertFalse(st.add(cards2));
        cards2.add(card1);
        cards2.add(card2);
        st.add(cards2);
        st.add(card4);

        assertTrue(st.lastCard()==card4);
    }

    @Test
    void checkAddingConstraints() {
        Stall s = new Stall(1);
        s.checkAddingConstraints(card1);
    }

    @Test
    void addCardArray() {
        ArrayList a = new ArrayList();
        Stall s = new Stall(1);
        s.addCardArray(a);
    }

    @Test
    void firstCard() {
        Stall s = new Stall(1);
        s.firstCard();
    }
}