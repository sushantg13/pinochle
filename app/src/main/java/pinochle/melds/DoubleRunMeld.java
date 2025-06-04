package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pinochle.Rank;
import pinochle.Suit;

public class DoubleRunMeld implements Meld {
    private static final int POINTS = 1500;
    private static final String NAME = "Double Run";

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
            return new ArrayList<>();
        }

        Map<String, List<Card>> runCardsInHand = new HashMap<>();
        String[] ranksForRun = {
            Rank.ACE.getCardLog(), Rank.TEN.getCardLog(), Rank.KING.getCardLog(),
            Rank.QUEEN.getCardLog(), Rank.JACK.getCardLog()
        };

        for (String rankStr : ranksForRun) {
            runCardsInHand.put(rankStr + trumpSuit, new ArrayList<>());
        }

        for (Card card : hand) {
            Suit cardSuit = (Suit) card.getSuit();
            if (cardSuit.getSuitShortHand().equals(trumpSuit)) {
                Rank cardRank = (Rank) card.getRank();
                String cardName = cardRank.getCardLog() + trumpSuit;
                if (runCardsInHand.containsKey(cardName) && runCardsInHand.get(cardName).size() < 2) {
                    runCardsInHand.get(cardName).add(card);
                }
            }
        }

        List<Card> meldCards = new ArrayList<>();
        boolean doubleRunFound = true;
        for (String rankStr : ranksForRun) {
            String cardKey = rankStr + trumpSuit;
            if (runCardsInHand.get(cardKey).size() < 2) {
                doubleRunFound = false;
                break;
            }
            meldCards.addAll(runCardsInHand.get(cardKey)); // Add the two cards of this rank
        }

        if (doubleRunFound && meldCards.size() == 10) { // 5 ranks * 2 cards each
            return meldCards;
        }

        return new ArrayList<>(); // Double Run not found
    }
}
