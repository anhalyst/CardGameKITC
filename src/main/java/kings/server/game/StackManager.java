package kings.server.game;

import javafx.util.Pair;
import kings.client.IClient;
import kings.server.administration.dbController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.security.KeyException;
import java.util.*;
import java.util.stream.Stream;

public class StackManager implements IGameManager {
    private Registry registry;
    private dbController db;
    private ArrayList<Card> cards;
    private ArrayList<IClient> player;
    private int pot;

    // stalls 0 - 3 are tabbed stalls, 4 - 7 are kings stalls
    private ArrayList<Stall> stalls;
    private String name;
    private IClient firstPlayer;

    // is set to true, if a player made a valid move; is set to false, when it is the turn of the next player
    private boolean madeValidMove;

    // the amount of chips for one played hand
    private HashMap<IClient, Integer> playerChips;
    // the amount of chips during the whole game (until someone reaches 100 points)
    private HashMap<IClient, Integer> playerOverallChips;

    //looks like <player: <hand, isPlayersTurn>>, to store the players hand and to check if it is the players turn
    LinkedHashMap<IClient, AbstractMap.SimpleEntry<Hand, Boolean>> playerHands;

    /**
     * is executed by the RoomServer
     *
     * creates the cards
     * shuffles them
     * creates the stalls
     * inserts one card to every tabbed stall
     * @param registry the registry, created by the server
     * @param name the name of the GameRoom, that creates this StackManager
     */
    public StackManager(Registry registry, dbController db, String name) {
        this.registry = registry;
        this.db = db;
        this.name = name;
        cards = new ArrayList<>();
        stalls = new ArrayList<>();
        playerOverallChips = new HashMap<>();


        madeValidMove = false;

        setupGame();
    }

    /**
     * is executed by start()
     *
     * iterates over the deck (unused cards) and removes the top seven cards
     * this seven cards are returned (initial cards of the player)
     * @return the seven cards for a player
     */
    private ArrayList<Card> dealCards() {
        // these are the cards, every player gets
        ArrayList<Card> toDeal = new ArrayList<>();

        // remove the last seven cards from the deck and return them
        int cardSize = cards.size()-1;
        for (int i = cardSize; i >= cardSize-6; i--) {
            toDeal.add(cards.get(i));

            cards.remove(i);
        }

        return toDeal;

    }

    /**
     * is executed by start()
     *
     * calculates the amount of chips per player
     * inserts <player, amountOfChips> in a HashMap
     */
    private void distributeChips() {
        playerChips = new HashMap<>();

        // calculate the amount of chips per player (there are 80 chips in the game)
        int amountPerPlayer = 80 / player.size();

        for (IClient p: player) {
            // insert player, chips in HashMap
            playerChips.put(p, amountPerPlayer);
        }
    }

    /**
     * is executed by the constructor
     *
     * inserts all players with a zero as total score
     */
    private void initializeTotalScoreMap() {
        for (IClient p: player) {
            playerOverallChips.put(p, 0);
        }
    }

    /**
     * is executed by addCard(), when a player has won a game and not more than 100 points
     *
     */
    private void restartGame() throws RemoteException {
        clearGame();
        setupGame();
        distributeChips();
        // rule: at the beginning of the game, every player has to pay one chip
        payChip();
        createPlayerHands(player);
        notifyNewTurn();
    }

    /**
     * is executed by restartGame()
     *
     * clears cards
     * clears stalls
     */
    private void clearGame() {
        // clear the attributes
        cards = new ArrayList<>();
        stalls = new ArrayList<>();
    }

    /**
     * executed by the constructor and restartGame()
     *
     * creates the cards
     * shuffles the cards
     * creates the stalls
     * inserts one card into the tabbed stall
     */
    private void setupGame() {
        // set the pot to zero
        pot = 0;

        // create all cards and add them to the cards array
        initializeCards();

        // showCards();
        //System.out.println("###########shuffle#############");

        // shuffle the cards
        Collections.shuffle(cards);
        //showCards();

        // creates four tabbed stalls and four king stalls, with continuous numerating as name
        createStalls();

        // place one card in every tabbed stall (stalls 0 - 3 are tabbed stalls)
        initializeTabbedStalls();

    }

    /**
     * is executed by the gameRoom
     * gets the list of the players in the gameRoom and creates a new linkedHashMap playerHands
     * playerHands: (IClient : (Hand, boolean)) => (the player : (the cards of the player : the mark if it's the players turn))
     * the first player gets a true mark, so it's his turn
     * adds seven cards to every players hand
     * gives every player in this game remote access
     * @param p the players in the GameRoom
     * @param bots the bots, created by the GameRoom
     */
    public void start(ArrayList<IClient> p, ArrayList<IClient> bots) {
        System.out.println("player in the room:");
        System.out.println(p);
        System.out.println(bots);

        // add the bots to the player list
        if (!bots.isEmpty()) {
            p.addAll(bots);
        }
        player = p;

        // create the hand for every player
        createPlayerHands(p);

        // distribute the 80 chips to the players
        distributeChips();

        // rule: at the beginning of the game, every player has to pay one chip
        payChip();

        initializeTotalScoreMap();
        try {
            connectPlayers();
        }
        catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    private void payChip() {
        // iterate over all player to remove one chip
        for (IClient p: player) {
            // remove one chip from the player
            playerChips.put(p, playerChips.get(p)-1);

            // add one chip to the pot
            incrementPot(1);
        }
    }

    /**
     * is executed by start() and restartGame()
     *
     * creates a hand with seven cards for every player
     * @param p the players in the GameRoom
     */
    private void createPlayerHands(ArrayList<IClient> p) {
        playerHands = new LinkedHashMap<>();
        Hand h;
        boolean firstPlayer = true;

        // create a hand for every player
        for (IClient player: p) {
            h = new Hand(dealCards());

            // the first player, that is added to the hashMap gets a true entry, to mark that it's the players turn
            playerHands.put(player, new AbstractMap.SimpleEntry<>(h, firstPlayer));
            if (firstPlayer) {
                this.firstPlayer = player;
            }

            firstPlayer = false;
        }
    }

    /**
     * is executed by start()
     *
     * iterates over the playerHands linkedHashMap to give every player the remote access to the stackManager object they need
     * @throws NotBoundException
     * @throws RemoteException
     */
    private void connectPlayers() throws NotBoundException, RemoteException {
        // iterate over the player map to connect them to this game manager
        for (IClient player: playerHands.keySet()) {
            player.connectToGameManager(name);
            player.setStarted(true);
        }
    }

    /**
     * checks if a player is a Bot
     * @param player a player to check
     * @return the player is a bot => true; else => false
     */
    private boolean isBot(IClient player) {
        return (player.getClass().toString().equals("class kings.server.game.Bot"));
    }

    /**
     * is executed by the constructor
     *
     * adds the first to every tabbed stall and removes it from the deck
     */
    private void initializeTabbedStalls() {
        // iterate over the first four stalls (tabbed stalls)
        for (int i = 0; i <= 3; i++) {
            // add the first card to the current tabbed stall
            stalls.get(i).add(cards.get(0));

            System.out.println("Tabbed stall initialized: " + i + cards.get(0).getName());

            // remove this card from the deck
            cards.remove(0);
        }

        System.out.println("tabbed stalls now have cards!");
    }

    /**
     * is executed by the constructor
     * creates four tabbed stalls and four kings stalls
     *
     * the four tabbed stalls have the numbers 0 - 3
     * the four kings stalls have the numbers 4 - 7
     */
    private void createStalls() {
        // create four tabbed stalls
        for (int i = 0; i <= 3; i++) {
            //stalls.add((IStall) new Stall(i));
            stalls.add(new Stall(i));
        }

        // create four kings stalls
        for (int i = 4; i <= 7; i++) {
            //stalls.add((IStall) new KingsStall(i));
            stalls.add(new KingsStall(i));
        }
    }

    /**
     * creates an array with the number of the cards
     * creates an array with the symbol of the cards
     * calculates the cross product of the two arrays
     * creates all Card objects with the cross product
     */
    private void initializeCards() {
        // create an arrayList for the number of the cards
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            nums.add(i);
        }

        // create an arrayList for the colors of the cards
        ArrayList<String> colors = new ArrayList<>();
        colors.add("hearts");
        colors.add("spades");
        colors.add("diamonds");
        colors.add("clubs");

        // create the cross product of the two arrays and create new cards
        for (String color: colors) {
            for (int num: nums) {
                cards.add(new Card(num, color));
            }
        }

    }

    /**
     * is used to move one stack to another
     * removes the cards from the stall they come
     *
     * checks if the player is in charge of a king (kings have to be played immediately)
     * turn is allowed: add the cards to the stall, where the player wants them to be
     * else: add the cards back to the stall, where they are coming from
     * @param fromStall the index of the stall to move
     * @param toStall the index of the stall, where the fromStall should be moved to
     * @return valid move => true; else => false
     */
    public synchronized boolean changeStack(IClient player, int fromStall, int toStall) {
        System.out.println("CHANGE STACK START: from, to" + fromStall + toStall);

        // if the fromStall is a kings stall => moving is not allowed
        if (fromStall >= 4) {
            System.out.println("CHANGE STACK END [MOVING KINGS STALL VIOLATION]");
            return false;
        }


        // if the hand of the player contains a king => he is not allowed to use this card
        // kings have to be played immediately
        if (containsKings(player)) {
            System.out.println("CHANGE STACK END [KING RULE VIOLATION]");
            return false;
        }

        // if the fromStall was initialized with a king, it is allowed to move it to a kings stall (else: see next case)
        /*
        if (fromStall <= 3 && toStall >= 4 && stalls.get(fromStall).lastCard().getNumber() == 13) {
            // if NOT( fromStall = tabbedStall AND toStall = kingsStall AND the card on the tabbedStall is a king (=> there is only one card at the tabbed stall))
            System.out.println("CHANGE STACK END [RULE VIOLATION, MOVING A KING]");
            return false;
        }


         */

        // has to be a try block, because if the fromStall is empty, there is no lastCard()
        try {
            if (stalls.get(fromStall).lastCard().getNumber() != 13) {
                // if the toStall is empty, the turn is unnecessary
                if (stalls.get(toStall).lastCard() == null && stalls.get(fromStall).firstCard().getNumber() != 13) {
                    System.out.println("CHANGE STACK END [RULE VIOLATION]");
                    return false;
                }
            }
        }
        catch (NullPointerException e) {

            System.out.println("the player tried to move an empty stall");
            return false;
        }

        // remove all cards from the stall
        ArrayList<Card> cards = stalls.get(fromStall).remove();
        // add the cards to the stall, where the player wants to move them to
        boolean allowed = stalls.get(toStall).add(cards);

        // if the turn was not allowed => undo the turn and add it to the fromStack again
        if (!allowed) {
            System.out.println("###### store cards back again #####");
            stalls.get(fromStall).add(cards);
            System.out.println("CHANGE STACK END [RULE VIOLATION]");
            return false;
        }

        System.out.println("CHANGE STACK END [WORKED]");
        madeValidMove = true;
        return true;

    }

    /**
     * this method is remotely executed by the player
     * tries to add a card from the players hand to a stall
     *
     * checks if the player is in the game
     * checks if the player is in charge of a king and does not play it (kings have to be played immediately)
     * checks if the player is really in charge of the card
     * tries to add the card to the stall (see: Stall.add())
     * checks if the player has no more card in his hand (if true => the game ends)
     * @param player the player that wants to play a card
     * @param c the card to play
     * @param stallIndex the index of the stall, where the card should be placed at
     * @return valid turn => true; else => false
     */
    public synchronized boolean addCard(IClient player, Card c, int stallIndex) throws RemoteException {
        System.out.println("try to add a card");
        // if the player is not in this game => he can not add cards
        if (playerHands.get(player) == null) {
            System.out.println("player not in the game");
            return false;
        }

        // if it is the players turns
        if (playerHands.get(player).getValue()) {
            System.out.println("it's the turn of: " + player);
        }
        else {
            System.out.println("it's NOT the turn of: " + player);
            return false;
        }

        // if the hand of the player contains a king AND the card he wants to play is not a king => he is not allowed to use this card
        // kings have to be played immediately
        if (containsKings(player) && c.getNumber() != 13) {
            return false;
        }

        // if the player is really in charge of the card => go on
        for (Card card: playerHands.get(player).getKey().getCards()) {
            if (card.getName().equals(c.getName())) {


                // if it is the players turns
                //if (playerHands.get(player).getValue()) {
                //    System.out.println("it's the turn of: " + player);

                    // if it was a valid turn: true; else: false
                    if (stalls.get(stallIndex).add(c)) {
                        // the turn was valid

                        // remove the played card from the hand of the player
                        updateHand(player, c);



                        if (checkVictory(player)) {
                            System.out.println("the player has won a hand");
                            //updateLeaderboard(player);
                            //endGame();
                            payChipsForCards(player);
                            //restartGame();

                            if (totalScoreReached(player)) {
                                System.out.println("the player has reached the total score");
                                System.out.println(playerOverallChips);
                                updateLeaderboard(player);
                                endGame();
                                return false;
                            }
                            else {
                                restartGame();
                                return true;
                            }
                        }


                        madeValidMove = true;
                        return true;
                    }
                    else {
                        // the turn was not valid
                        return false;
                    }
                //}

                //return true;
            }

        }


        System.out.println("something went wrong, maybe you are not in charge of the card");
        return false;
    }

    /**
     * is executed by addCard()
     *
     * if a player has reached a total score of 100 points, the player has won the whole game
     * @param winner the winning player
     * @return winner has reached at least 100 points => true; else => false
     */
    private boolean totalScoreReached(IClient winner) {
        try {
            System.out.println("THE TOTAL SCORES:");
            System.out.println(playerOverallChips);
            return (playerOverallChips.get(winner) >= 100);
        }
        catch (NullPointerException e) {
            // the player is not in the playerOverallChips => the player has not won (because he would have been inserted after winning)
            return false;
        }
    }

    /**
     * is executed by addCard()
     *
     * rule: if a player has won, the other players have to pay one chip for every remaining card in their hand
     * update the playerOverallChips (total score of this game) of the winning player
     * @param winner the winning player
     */
    private void payChipsForCards(IClient winner) {
        for (IClient p: player) {
            if (p != winner) {
                // increment the pot for the amount of cards in a players hand
                incrementPot(getCards(p).size());
            }
        }

        // update the total score of the winner
        // if the player has not won before, he is not in the HashMap => merge :)
        playerOverallChips.merge(winner, pot, Integer::sum);
/*
        if (playerOverallChips.get(winner) != null) {
            // increment the old score by the pot
            playerOverallChips.put(winner, playerOverallChips.get(winner) + pot);
        }
        else {
            // if the player has not won before, he is not in the HashMap
            playerOverallChips.put(winner, pot);
        }*/
    }

    private void updateLeaderboard(IClient player) {
        // if the player is not a bot => update the leaderboard
        if (!isBot(player)) {
            try {
                db.updateScore(player.getName(), 0);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * is executed by addCard()
     *
     * iterates over all players in the room and disconnects them from GameManager
     */
    private void endGame(){
        for (IClient player: playerHands.keySet()) {
            try {
                player.setGameEnded(true);
                player.disconnectFromGameManager();
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * is executed by addCard()
     *
     * removes a card from the hand of the player
     * a check, if the player is in charge of the card, is already done by addCard()
     * @param player the player that has played a card
     * @param c the card, the player played
     */
    private void updateHand(IClient player, Card c) {
        System.out.println("remove card from players hand");
        // removes a card from the hand of the player
        playerHands.get(player).getKey().removeCard(c);
    }

    /**
     * is executed by addCard()
     *
     * checks if the player, that played a card, has no more cards in his hand
     * true => the player has won and the game ends
     * false => the game goes on
     * @param player the player, that might have won
     * @return player has won => true; else => false
     */
    private boolean checkVictory(IClient player) {
        // if the player has no cards => the player has won
        return playerHands.get(player).getKey().getCards().size() == 0;
    }

    /**
     * is executed by setupForEnding()
     * returns a card from the deck
     *
     * stores a card in a temporary variable and removes it from the deck
     * @return the drawn Card
     */
    private Card drawCard() {
        Card tmpCard = cards.get(0);
        cards.remove(0);

        return tmpCard;
    }


    /**
     * this method is used by endTurn()
     *
     * rule: kings have to played immediately
     * => if the hand of the player contains a king he is not allowed to end his turn
     * rule: the player has to draw a card at the end of his turn
     * rule: if the player draws a king, he must play it before ending his turn
     * => player draws a card; check if it's a king; if it is => search for an empty kings stall and add it there
     * else: add the card to the players hand
     * @param player the player that wants to end his turn
     * @return the player is allowed to end his turn => true; else => false
     */
    private boolean setupForEnding(IClient player) throws RemoteException {
        // if the hand of the player contains at least one king => he is not allowed to end his turn
        if (containsKings(player)) {
            return false;
        }

        System.out.println("REMAINING CARDS:");
        System.out.println(cards);
        // draw a card
        Card drawnCard = drawCard();


        // add the drawn card to the players hand
        playerHands.get(player).getKey().addCard(drawnCard);

        // if the card is king => move it immediately to an empty kings stall and do not add it to the players hand
        if (drawnCard.getNumber() == 13) {

            // iterate over the kings stalls
            for (int i = 4; i <= 7; i++) {
                // search for an empty kings stall and add the card to this stall
                if (addCard(player, drawnCard, i)) {
                    return true;
                }
            }
        }




        return true;
    }

    /**
     * this method is remotely executed by the client
     * player wants to end his turn
     *
     * check if the player is allowed to end his turn (see: setupForEnding())
     * iterate over the playerHands linkedHashMap, to find the following player of the current player, to signal the next player that it's his move
     * in the loop, we are checking, if it even was the players move, that is executing this method
     * @param player the player that wants to end his turn
     * @return the player is allowed to end his turn => true; else => false
     * @throws RemoteException
     */
    public synchronized boolean endTurn(IClient player) throws RemoteException {
        System.out.println("END TURN");
        Bot b;


        // throws an exception, if there is no card to draw anymore
        try {
            if (!setupForEnding(player)) {
                System.out.println("player can not end it's turn, because of a drawn card");
                return false;
            }
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("[endTurn()] no more cards in the draw deck");
            //notify players that a new turn has started
            //notifyNewTurn();
            //return true;
        }

        Iterator<Map.Entry<IClient, AbstractMap.SimpleEntry<Hand, Boolean>>> it = playerHands.entrySet().iterator();

        Map.Entry<IClient, AbstractMap.SimpleEntry<Hand, Boolean>> playerEntry;

        // while a next entry in the map exists: check the next entry
        while (it.hasNext()) {

            // get the next player
            playerEntry = it.next();

            // if the player is in the map and it is the players turn
            if (player.getName().equals(playerEntry.getKey().getName()) && playerEntry.getValue().getValue()) {

                // mark the players turn as ended
                playerHands.get(player).setValue(false);

                // if the player, that wants to end his turn has made no move, he has to pay a chip
                checkPaying(player);

                // has to be in a try block, because when it was the turn of the last player in the map, we need the first one to update
                try {
                    // get the next player
                    playerEntry = it.next();
                }
                catch (NoSuchElementException e) {
                    //System.out.println("it was the last players turn, we got you :)");
                    //System.out.println("going on with the first player...");


                    // the first player in the linked list was stored in the start method
                    playerHands.get(firstPlayer).setValue(true);



                    System.out.println("turn ended");

                    // check if the firstPlayer is a bot
                    if (isBot(firstPlayer)) {
                        b = (Bot) firstPlayer;
                        b.botTurn();
                    }

                    //notify players that a new turn has started
                    notifyNewTurn();
                    return true;
                }
                // mark the next player as the current player
                playerEntry.getValue().setValue(true);

                System.out.println("turn ended");

                // check if the firstPlayer is a bot
                if (isBot(playerEntry.getKey())) {
                    b = (Bot) playerEntry.getKey();
                    b.botTurn();
                }

                //notify players that a new turn has started
                notifyNewTurn();
                return true;

            }
        }


        System.out.println("player can not end it's turn (it may was not your turn)");
        return false;
    }

    private void checkPaying(IClient p) {
        // if the player made no valid turn => he has to pay one chip
        if (!madeValidMove) {
            // remove one chip from the player
            playerChips.put(p, playerChips.get(p)-1);
            // add one chip to the pot
            incrementPot(1);
        }
    }

    @Override
    public ArrayList<Card> getLastCardsEachStall(IClient player) throws RemoteException {
        ArrayList<Card> topCards = new ArrayList<>();
        for (Stall s : stalls){
            topCards.add(s.lastCard());
        }
        return topCards;
    }

    @Override
    public ArrayList<Card> getFirstCardsEachStall(IClient player) throws RemoteException {
        ArrayList<Card> bottomCards = new ArrayList<>();
        for (Stall s : stalls){
            bottomCards.add(s.firstCard());
        }
        return bottomCards;
    }

    /**
     * this method is executed everytime, when a player wants to take a turn or end his move
     * rule: if the player is in charge of a king, he must immediately play it
     *
     * get the hand of the player and iterate over it to see, if there is a king
     * @param player the player to check, if he has a king
     * @return the player has a king => true; else => false
     */
    private boolean containsKings(IClient player) {
        // get the current cards of the player
        ArrayList<Card> cards = playerHands.get(player).getKey().getCards();

        // iterate over the cards, to check if there is a king
        for (Card c: cards) {
            // there is a king
            if (c.getNumber() == 13) {
               return true;
            }
        }

        return false;
    }

    /**
     * this method is remotely executed by the player
     * return the hand of a player
     * @param player the player that wants his cards
     * @return the cards of the player
     */
    public ArrayList<Card> getCards(IClient player) {
        return playerHands.get(player).getKey().getCards();
    }

    @Override
    public LinkedHashMap<String, Boolean> turnInfo() throws RemoteException{
        LinkedHashMap<String, Boolean> result = new LinkedHashMap<>();
        playerHands.forEach((key, value) -> {
            try {
                result.put(key.getName(), value.getValue());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    @Override
    public boolean isMyTurn(IClient player) throws RemoteException{
        return playerHands.get(player).getValue();
    }

    private void notifyNewTurn() throws RemoteException {
        System.out.println("THE CHIPS OF THE PLAYERS AFTER A TURN:");
        System.out.println(playerChips);
        madeValidMove = false;
        for (IClient p: playerHands.keySet()) {
            p.setNewTurn(true);
        }
    }

    private void incrementPot(int amount) {
        pot+=amount;
    }

    //Pair : fst - current chips, snd - total points
    @Override
    public LinkedHashMap<String, Pair<Integer, Integer>> chipsInfo() throws RemoteException {
        LinkedHashMap<String, Pair<Integer, Integer>> result = new LinkedHashMap<>();
        for (IClient p1: playerChips.keySet()) {
                String name1 = p1.getName();
                result.put(name1, new Pair<>(playerChips.get(p1), playerOverallChips.get(p1)));
        }
        return result;
    }

    @Override
    public int getPot() throws RemoteException {
        return pot;
    }
}
