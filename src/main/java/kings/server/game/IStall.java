package kings.server.game;

import java.util.ArrayList;

public interface IStall {
        private boolean checkAddingConstraints(Card c) {
                return false;
        }

        private boolean add(ArrayList<Card> cards) {
                return false;
        }

        private boolean add(Card c) {
                return false;
        }

        private void addCardArray(ArrayList<Card> cards) {

        }
/*
        private ArrayList<Card> remove() {
                return null;
        }

        private Card lastCard() {
                return null;
        }

        private Card firstCard() {
                return null;
        }
*/
}
