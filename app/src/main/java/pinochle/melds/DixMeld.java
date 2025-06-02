package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pinochle.Rank;
import pinochle.Suit;

public class DixMeld implements Meld {
    private static final int POINTS = 10;
    private static final String NAME = "Dix";

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
        if (trumpSuit == null || trumpSuit.isEmpty()) {
            return new ArrayList<>(); // Dix requires a trump suit
        }

        String dixCardString = Rank.NINE.getCardLog() + trumpSuit;

        for (Card card : hand) {
            if (getCardName(card).equals(dixCardString)) {
                return Collections.singletonList(card);
            }
        }
        return new ArrayList<>(); // Dix not found
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        // Assuming Rank.NINE.getCardLog() gives the correct string representation for '9'
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
