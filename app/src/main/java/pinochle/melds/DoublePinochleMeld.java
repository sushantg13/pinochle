package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;
import pinochle.Rank;
import pinochle.Suit;

public class DoublePinochleMeld implements Meld {
    private static final int POINTS = 300;
    private static final String NAME = "Double Pinochle";

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

        List<Card> jacksOfDiamondsFound = new ArrayList<>();
        List<Card> queensOfSpadesFound = new ArrayList<>();
        List<Card> tempHand = new ArrayList<>(hand); // Work with a copy

        // First pass to collect all relevant cards to avoid issues with removing while iterating
        List<Card> potentialJacks = new ArrayList<>();
        List<Card> potentialQueens = new ArrayList<>();

        for (Card card : tempHand) {
            String cardName = getCardName(card);
            if (cardName.equals(jackOfDiamondsStr)) {
                potentialJacks.add(card);
            } else if (cardName.equals(queenOfSpadesStr)) {
                potentialQueens.add(card);
            }
        }

        if (potentialJacks.size() >= 2 && potentialQueens.size() >= 2) {
            List<Card> meldCards = new ArrayList<>();
            meldCards.add(potentialJacks.get(0));
            meldCards.add(potentialJacks.get(1));
            meldCards.add(potentialQueens.get(0));
            meldCards.add(potentialQueens.get(1));
            return meldCards;
        }

        return new ArrayList<>(); // Double Pinochle not found
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
