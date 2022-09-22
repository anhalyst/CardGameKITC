package kings.server.game;

import java.util.ArrayList;

public class KingsStall extends Stall implements IStall {

    public KingsStall(int n) {
        super(n);
    }

    protected boolean checkAddingConstraints(Card c) {
        System.out.println("KINGS STALL ############################### KINGS STALL");
        // if there is no card in the stall => the player can add whatever he wants
        if (lastCard() != null) {
/*
            // if the number of the last card is an ace, the player can't append an other card
            if (lastCard().getNumber() == 13) {
                return false;
            }
*/
            // if the color of last card on the stack is equal to the new card, it can't be added
            if (lastCard().isBlack() == c.isBlack()) {
                return false;
            }

            // if the number of the last card +1 is unequal to the number of the new card, it can't be added
            if (lastCard().getNumber() - 1 != c.getNumber()) {
                return false;
            }
        }
        else {
            // the stall is empty, so the new card has to be a king
            if (c.getNumber() != 13) {
                // the new card is no king => card can't be added
                return false;
            }

        }

        // no constraint was violated
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
    /*
    protected synchronized boolean add(ArrayList<Card> cards) {
        System.out.println("KINGS STALLL ################################################## KINGS STALL");
        // check constraints for the first card of the new array (the array itself is already checked)

        if (cards.size() == 0) {
            // the stall, where the cards are coming from was empty, so there are no cards
            return false;
        }

        if (checkAddingConstraints(cards.get(0))) {


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
    /*
    protected synchronized boolean add(Card c) {

        // no constraint was violated => add the card
        if (checkAddingConstraints(c)) {
            cards.add(c);
            return true;
        }

        return false;
    }

    */
}
