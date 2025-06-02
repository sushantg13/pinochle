package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import java.util.stream.Collectors; // Not used in this file after previous edits
import pinochle.Rank;
import pinochle.Suit;

public class TenToAceRunMeld implements Meld {
    private static final int POINTS = 150;
    private static final String NAME = "Ten to Ace Run";

    @Override
    public List<Card> checkMeld(List<Card> hand, String trumpSuit) {
        return getMeldCards(hand, trumpSuit);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Card> getMeldCards(List<Card> hand, String trumpSuit) {
        List<String> requiredCardStrings = Arrays.asList(
                Rank.ACE.getCardLog() + trumpSuit,
                Rank.TEN.getCardLog() + trumpSuit,
                Rank.KING.getCardLog() + trumpSuit,
                Rank.QUEEN.getCardLog() + trumpSuit,
                Rank.JACK.getCardLog() + trumpSuit
        );

        List<Card> meldCards = new ArrayList<>();
        List<Card> tempHand = new ArrayList<>(hand);

        for (String cardString : requiredCardStrings) {
            Card foundCard = null;
            for (Card card : tempHand) {
                if (getCardName(card, trumpSuit).equals(cardString)) {
                    foundCard = card;
                    break;
                }
            }
            if (foundCard != null) {
                meldCards.add(foundCard);
                tempHand.remove(foundCard); // Ensure each card is used once for this meld
            } else {
                return new ArrayList<>(); // Not all cards found
            }
        }
        return meldCards.size() == requiredCardStrings.size() ? meldCards : new ArrayList<>();
    }

    private String getCardName(Card card, String currentTrumpSuit) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        // Use currentTrumpSuit to determine if the card's suit matches the trump for logging purposes if necessary,
        // but for general card naming, just use its own suit.
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
