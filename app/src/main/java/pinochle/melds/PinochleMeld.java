package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;
import pinochle.Rank;
import pinochle.Suit;

public class PinochleMeld implements Meld {
    private static final int POINTS = 40;
    private static final String NAME = "Pinochle";

    @Override
    public List<Card> checkMeld(List<Card> hand, String trumpSuit) {
        // Trump suit is not relevant for this specific meld's card composition
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
        String jackOfDiamondsStr = Rank.JACK.getCardLog() + Suit.DIAMONDS.getSuitShortHand();
        String queenOfSpadesStr = Rank.QUEEN.getCardLog() + Suit.SPADES.getSuitShortHand();

        Card jackOfDiamonds = null;
        Card queenOfSpades = null;

        List<Card> tempHand = new ArrayList<>(hand); // Use a copy to allow removal for checking

        for (Card card : tempHand) {
            String cardName = getCardName(card);
            if (jackOfDiamonds == null && cardName.equals(jackOfDiamondsStr)) {
                jackOfDiamonds = card;
            } else if (queenOfSpades == null && cardName.equals(queenOfSpadesStr)) {
                queenOfSpades = card;
            }
            if (jackOfDiamonds != null && queenOfSpades != null) {
                break; // Found both
            }
        }

        if (jackOfDiamonds != null && queenOfSpades != null) {
            List<Card> meldCards = new ArrayList<>();
            meldCards.add(jackOfDiamonds);
            meldCards.add(queenOfSpades);
            return meldCards;
        }

        return new ArrayList<>(); // Pinochle not found
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
