package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.List;

public interface Meld {
    /**
     * Checks if the given hand contains this meld.
     * @param hand The list of cards in the player's hand.
     * @param trumpSuit The current trump suit.
     * @return A list of cards that form the meld, or null if the meld is not present.
     *         The returned list contains the specific cards from the hand that make up the meld.
     */
    List<Card> checkMeld(List<Card> hand, String trumpSuit);

    /**
     * Gets the points awarded for this meld.
     * @return The point value of the meld.
     */
    int getPoints();

    /**
     * Gets the name of the meld.
     * @return The name of the meld.
     */
    String getName();

    /**
     * Returns the cards that constitute this meld from the provided hand.
     * This method should identify the actual Card objects from the hand.
     * @param hand The player's hand.
     * @param trumpSuit The trump suit.
     * @return A list of Card objects from the hand that form the meld, or an empty list if not found.
     */
    List<Card> getMeldCards(List<Card> hand, String trumpSuit);
}
