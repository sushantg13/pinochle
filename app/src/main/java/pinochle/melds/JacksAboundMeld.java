package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pinochle.Rank;
import pinochle.Suit;

public class JacksAboundMeld implements Meld {
    private static final int POINTS = 400;
    private static final String NAME = "Jacks Abound";

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
        Map<String, List<Card>> jacksBySuit = new HashMap<>();
        jacksBySuit.put(Suit.SPADES.getSuitShortHand(), new ArrayList<>());
        jacksBySuit.put(Suit.HEARTS.getSuitShortHand(), new ArrayList<>());
        jacksBySuit.put(Suit.DIAMONDS.getSuitShortHand(), new ArrayList<>());
        jacksBySuit.put(Suit.CLUBS.getSuitShortHand(), new ArrayList<>());

        String jackRankStr = Rank.JACK.getCardLog();

        for (Card card : hand) {
            if (((Rank) card.getRank()).getCardLog().equals(jackRankStr)) {
                String suitShortHand = ((Suit) card.getSuit()).getSuitShortHand();
                if (jacksBySuit.containsKey(suitShortHand) && jacksBySuit.get(suitShortHand).size() < 2) {
                    jacksBySuit.get(suitShortHand).add(card);
                }
            }
        }

        boolean allSuitsHaveTwoJacks = true;
        List<Card> meldCards = new ArrayList<>();
        for (Map.Entry<String, List<Card>> entry : jacksBySuit.entrySet()) {
            if (entry.getValue().size() < 2) {
                allSuitsHaveTwoJacks = false;
                break;
            }
            meldCards.addAll(entry.getValue()); // Add the two jacks found for this suit
        }

        if (allSuitsHaveTwoJacks && meldCards.size() == 8) {
            return meldCards;
        }

        return new ArrayList<>(); // Jacks Abound not found
    }
}
