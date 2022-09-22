package kings.server.game;

import kings.client.IClient;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;

// NullPointerExceptions are often thrown because of a won game
public class Bot implements IClient {
    private StackManager sm;
    private String name;
    private boolean ezMode;

    private ArrayList<Card> hand;

    public Bot(StackManager stackManager, String name, boolean ezMode) {
        sm = stackManager;
        this.name = name;
        this.ezMode = ezMode;
    }

    /**
     * is executed by endTurn of the StackManager
     *
     * executes a series of turns
     */
    public void botTurn() throws RemoteException {
        System.out.println("it is the turn of the machines");
        play();
    }

    protected void beautifulCardPrinter() {
        System.out.println("cards of the bot:");
        for (Card c: hand) {
            System.out.println(c.getName());
        }
    }

    /**
     * is executed by botTurn
     *
     * gets the cards
     * tries to play the kings
     * get the cards again
     * chooses between ezMode and hardMode
     * ends the turn
     */
    private void play() throws RemoteException {
        // get current cards
        hand = sm.getCards(this);

        beautifulCardPrinter();

        // play all kings of the hand
        playKings();

        // get current cards
        try {
            hand = sm.getCards(this);
        }
        catch (NullPointerException e) {
            System.out.println("the king was my last card");
            return;
        }

        try {
            if (ezMode) {
                playMinCards();
            } else {
                playMaxCards();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("i have won, after playingMIN/MAXCards");
        }


        try {
            sm.endTurn(this);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("err with ending the turn; maybe i have won");
        }

    }

    /**
     * executed by play()
     *
     * only tries to merge one stall to another and adds a card to the now empty stall
     * and tries to play one card
     */
    private void playMinCards() {
        try {
            ezMergeStalls();
            tryAddingCards();
        }
        catch (NullPointerException e) {
            System.out.println("i have won");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * is executed by play()
     *
     * tries to merge one stall to another and adds a card to the now empty stall
     * tries to add one card to a stall and goes on with step 1
     *
     * the loop ends when merging AND adding a card does not work
     *
     */
    private void playMaxCards() throws RemoteException {
        // checks if a card was added
        boolean added = true;

        // checks if a stall were merged
        boolean merged = mergeStalls();

        // if merging stalls OR adding a card has worked => try merging and adding again :)
        while (merged || added) {

            try {
                // try to add a card
                added = tryAddingCards();
                hand = sm.getCards(this);
            }
            catch (NullPointerException e) {
                System.out.println("the machines have won the game... wait for more");
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // try to merge one stall to another and add a card to the now empty stall
            merged = mergeStalls();
        }

        System.out.println("[BOT] AMOUNT OF CARDS: " + hand.size());
    }

    /**
     * is executed by playMaxCards() and playMinCards()
     *
     * tries to add one card of the hand to any stall
     * @return
     */
    private boolean tryAddingCards() throws RemoteException {
        // iterate over all stalls
        for (int s = 0; s <= 7; s++) {
            // iterate over all cards in the hand
            for (Card c: hand) {
                // try to add a card
                if (sm.addCard(this, c, s)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * is executed by playMinCards()
     *
     * tries to merge a stall to another
     * if it worked => add a card to the now empty stall
     */
    private void ezMergeStalls() throws RemoteException {
        int stallIndex = combineStalls();

        if (stallIndex != -1) {
            sm.addCard(this, hand.get(0), stallIndex);
        }
    }

    /**
     * is executed by playMaxCards()
     *
     * tries to merge one stall to another
     * if it worked => get the longest coherent card sequence and add it to the now empty stall
     * @return
     */
    private boolean mergeStalls() throws RemoteException {
        // if at least one stall was merged to another => merged = true
        boolean merged = false;

        // try to combine the stalls
        int stallIndex = combineStalls();

        while (stallIndex != -1) {
            merged = true;
            // sort the cards
            ArrayList<Deque<Card>> sortedCards = sortCards();

            // get largest sorted deque, to append it to the now empty stall
            Deque<Card> largestDeque = largestDeque(sortedCards);

            for (Card c: largestDeque) {
                // add a card to the now empty stall
                sm.addCard(this, c, stallIndex);
            }

            try {
                hand = sm.getCards(this);
            }
            catch (NullPointerException e) {
                System.out.println("i have won");
                return false;
            }

            // try to combine the stalls again
            stallIndex = combineStalls();

        }

        return merged;
    }

    /**
     * is executed by mergeStalls()
     *
     * gets a list of deques and returns the largest deque
     * @param list
     * @return
     */
    private Deque<Card> largestDeque(ArrayList<Deque<Card>> list) {
        Deque<Card> largestSoFar = new ArrayDeque<>();

        for (Deque<Card> d: list) {
            if (d.size() > largestSoFar.size()) {
                largestSoFar = d;
            }
        }

        return largestSoFar;
    }

    /**
     * is executed by sortCards()
     *
     * checks if the card j is a follower of the card i according to the rules
     *
     * if i has a different color then j and the number of i is equal to the number of j-1 => i is a follower of j
     *
     * @param i
     * @param j
     * @return
     */
    private boolean sortingConstrain(Card i, Card j) {
        // if the cards have different colors AND the number of the predecessor -1 is equal to the number of the new card
        return i.isBlack() != j.isBlack() && i.getNumber() -1 == j.getNumber();
    }


    /**
     * is executed by mergeStalls()
     *
     * iterates over the hand and checks if a card fits in front or at the end of a coherent card sequence
     *
     * returns all cards of the hand in sorted deques
     * @return
     */
    private ArrayList<Deque<Card>> sortCards() {
        System.out.println("amount of cards:" + hand.size());
        // creates a list of sorted cards
        ArrayList<Deque<Card>> sortedHand = new ArrayList<>();


        // begin with one card
        for (int i = 0; i < hand.size(); i++) {
            Deque<Card> tmpDeque = new ArrayDeque<>();

/*
            if (hand.get(i) == null) {
                System.out.println("already removed this card");
                continue;
            }

 */
            tmpDeque.addFirst(hand.get(i));

            // check if another card fits in front or after the chosen card
            for (int j = 0; j < hand.size(); j++) {
                /*
                if (hand.get(j) == null) {
                    System.out.println("already removed this card");
                    continue;
                }
                 */

                // if a card can be appended to the deque => remove the card from the hand
                if (sortingConstrain(tmpDeque.getLast(), hand.get(j))) {
                    tmpDeque.addLast(hand.get(j));
                    hand.remove(hand.get(j));
                }
                // if a card can be inserted in front of the deque => remove the card from the hand
                else if (sortingConstrain(hand.get(j), tmpDeque.getFirst())) {
                    tmpDeque.addFirst(hand.get(j));
                    hand.remove(hand.get(j));
                }


            }

            // add the sorted part to the list of sorted parts
            sortedHand.add(tmpDeque);
        }

        System.out.println("sorted cards");
        System.out.println(sortedHand);

        return sortedHand;
    }

    /**
     * is executed by mergeStalls() and ezMergeStalls()
     *
     * tries to merge one stall to another
     * if it worked => return the index of the now empty stall
     * else => return -1
     * @return
     */
    private int combineStalls() {
        // iterate over all stalls
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                if (i != j) {
                    if (sm.changeStack(this, i, j)) {
                        System.out.println("########################################## combining worked");

                        // return the index of the now empty stall
                        return i;
                    }
                }
            }
        }
        System.out.println("########################################## combining did NOT work");
        return -1;
    }

    /**
     * is executed by play()
     *
     * iterate over the hand to find the kings
     * search for an empty kings stall and play the king
     */
    private void playKings() throws RemoteException {
        // iterate over all cards in the hand
        for (Card card: hand) {
            // check if the card is a king
            if (card.getNumber() == 13) {
                System.out.println("the bot played a king");
                // indices of kings stalls: 4 - 7
                int kingsStallIndex = 4;

                // try to add a card to a kings stall; if the kings stall is not empty, addCard() will return false => iterate over kingsStalls
                while (!sm.addCard(this, card, kingsStallIndex)) {
                    kingsStallIndex++;
                }
            }
        }
    }

    @Override
    public Map.Entry<String, String> receiveMsg(Map.Entry<String, String> msg) throws RemoteException {
        return null;
    }

    @Override
    public void newMessage() throws RemoteException {

    }

    @Override
    public void enterEntranceRoom() throws RemoteException {

    }

    @Override
    public void connectToChatRoom(String currentRoom) throws RemoteException, NotBoundException {

    }

    @Override
    public void connectToGameManager(String currentRoom) throws RemoteException, NotBoundException {

    }

    @Override
    public void disconnectFromGameManager() throws RemoteException {
        sm = null;
        hand = null;
        name = null;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public void storeRoom(Map.Entry<String, String> roomInfo) throws RemoteException {

    }

    @Override
    public String getCurrentRoom() throws RemoteException {
        return null;
    }

    @Override
    public void setCurrentRoom(String newRoom) throws RemoteException {

    }

    @Override
    public void setStarted(boolean value) throws RemoteException {

    }

    @Override
    public void setNewTurn(boolean value) throws RemoteException {

    }

    @Override
    public void setGameEnded(boolean value) throws RemoteException {

    }

    @Override
    public void logout() throws RemoteException, NotBoundException {

    }
}
