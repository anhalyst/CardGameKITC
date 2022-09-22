package kings.server.game;

import java.util.ArrayList;

/**
 * Hand: the cards of the player
 */
public class Hand {
    private ArrayList<Card> cards;

    public Hand(ArrayList<Card> cards) {
        this.cards = new ArrayList<>();

        // after shuffeling, the player gets his cards
        this.cards.addAll(cards);
    }

    public ArrayList<Card> getCards() {
        // return a lexicographically sorted copy of the cards
        ArrayList<Card> copy = new ArrayList<>(cards);
        copy.sort((card1, card2) -> card1.getName().compareToIgnoreCase(card2.getName()));
        return copy;
    }


    protected void removeCard(Card c) {
        // iterate over all cards in the hand
        for (Card card: cards) {
            // if the played card c is in the hand => remove it
            if (card.getName().equals(c.getName())) {
                cards.remove(card);
                return;
            }
        }

    }

    public void addCard(Card c) {
        this.cards.add(c);
    }
}
