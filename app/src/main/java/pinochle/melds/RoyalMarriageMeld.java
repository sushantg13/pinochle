package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pinochle.Rank;
import pinochle.Suit;

public class RoyalMarriageMeld implements Meld {
    private static final int POINTS = 40;
    private static final String NAME = "Royal Marriage";

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
            return new ArrayList<>(); // Royal Marriage requires a trump suit
        }

        List<String> requiredCardStrings = Arrays.asList(
                Rank.KING.getCardLog() + trumpSuit,
                Rank.QUEEN.getCardLog() + trumpSuit
        );

        List<Card> meldCards = new ArrayList<>();
        List<Card> tempHand = new ArrayList<>(hand);

        for (String cardString : requiredCardStrings) {
            Card foundCard = null;
            for (Card cardInHand : tempHand) {
                if (getCardName(cardInHand).equals(cardString)) {
                    foundCard = cardInHand;
                    break;
                }
            }
            if (foundCard != null) {
                meldCards.add(foundCard);
                tempHand.remove(foundCard);
            } else {
                return new ArrayList<>(); // Not all cards found
            }
        }
        return meldCards.size() == requiredCardStrings.size() ? meldCards : new ArrayList<>();
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
