package kings.server.game;

import java.util.ArrayList;

public class Stall implements IStall {
    private int stallNumber;
    private ArrayList<Card> cards;


    public Stall(int num) {
        stallNumber = num;
        cards = new ArrayList<>();
    }

    /**
     * executed everytime, when a card has to be added to a stall
     *
     * if there is no lastCard => adding any card is allowed
     * if the lastCard is an ace => no more card can be added
     * if the colors of the lastCard and the new card are equal => card can not be added
     * if the number of the lastCard+1 is unequal to the number of the new card => card can not be added
     * @param c
     * @return
     */
    protected synchronized boolean checkAddingConstraints(Card c) {
        System.out.println("##### checking constraints #####");
        // if there is no card in the stall => the player can add whatever he wants
        if (lastCard() != null) {
            /*
            System.out.println("lastCard():" + lastCard().getName());
            System.out.println("new card:" + c.getName());
             */



            // if the number of the last card is an ace, the player can't append an other card
            if (lastCard().getNumber() == 1) {
                return false;
            }

            // if the color of last card on the stack is equal to the new card, it can't be added
            if (lastCard().isBlack() == c.isBlack()) {
                return false;
            }

            // if the number of the last card +1 is unequal to the number of the new card, it can't be added
            if (lastCard().getNumber() - 1 != c.getNumber()) {
                return false;
            }
        }
        //@TODO prevent the player from playing a king into the tabbed stall

        // no constraint was violated
        System.out.println("##### no constraint was violated #####");
        return true;
    }


    /**
     * is executed by the stackManager
     * tries to move the cards from another stall to this one
     *
     * checks if there are cards to add
     * checks if the constraints are violated
     * if not => add all cards
     * @param cards
     * @return
     */
    protected synchronized boolean add(ArrayList<Card> cards) {
        // check constraints for the first card of the new array (the array itself is already checked)

        if (cards.size() == 0) {
            // the stall, where the cards are coming from was empty, so there are no cards
            return false;
        }

        if (checkAddingConstraints(cards.get(0))) {
            /*
            System.out.println("#### adding cards to a stack:");
            try {
                System.out.println("lastCard:" + lastCard().getName());

            } catch (NullPointerException e) {
                System.out.println("lastCard:" + lastCard());
            }
            System.out.println("top card:" + cards.get(0).getName());
*/

            addCardArray(cards);
            return true;
        }

        return false;
    }

    /**
     * is executed by the stackManager
     * tries to add one card of the player to this stall
     *
     * checks if no constraints are violated
     * if not => add the card
     * @param c
     * @return
     */
    protected synchronized boolean add(Card c) {

        // no constraint was violated => add the card
        if (checkAddingConstraints(c)) {
            cards.add(c);
            return true;
        }

        return false;
    }

    /**
     * is executed by add(ArrayList (Card) cards)
     *
     * adds the new cards to this stall
     * @param cards
     */
    protected void addCardArray(ArrayList<Card> cards) {
        // add all new cards to the cards of this stall
        this.cards.addAll(cards);
    }

    /**
     * is executed by the stackManager, when a stall is moved to another
     *
     * removes all cards from the stall and returns them
     * @return
     */
    protected ArrayList<Card> remove() {
        // clone the card array
        ArrayList<Card> tmpList = new ArrayList<>(cards);

        // empty the card deck
        cards = new ArrayList<>();

        return tmpList;

    }

    /**
     * is executed everytime, when one or multiple cards are added, to check constraints
     *
     * returns the top card on the stall
     * @return
     */
    protected Card lastCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() -1);
    }

    /**
     * returns bottom card on the stall
     * @return
     */
    protected Card firstCard(){
        if(cards.isEmpty()){
            return null;
        }
        return cards.get(0);
    }
}
