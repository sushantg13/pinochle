package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pinochle.Rank;
import pinochle.Suit;

public class CommonMarriageMeld implements Meld {
    private static final int POINTS = 20;
    private static final String NAME = "Common Marriage";

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
    public List<Card> getMeldCards(List<Card> hand, String trumpSuitOutput) {
        // Iterate through all standard suits (SPADES, HEARTS, DIAMONDS, CLUBS)
        // The Suit enum has duplicates like SPADESTWO, we need to handle this.
        // Let's get unique suit short hands.
        List<String> uniqueSuitShortHands = new ArrayList<>();
        if (!uniqueSuitShortHands.contains(Suit.SPADES.getSuitShortHand())) uniqueSuitShortHands.add(Suit.SPADES.getSuitShortHand());
        if (!uniqueSuitShortHands.contains(Suit.HEARTS.getSuitShortHand())) uniqueSuitShortHands.add(Suit.HEARTS.getSuitShortHand());
        if (!uniqueSuitShortHands.contains(Suit.DIAMONDS.getSuitShortHand())) uniqueSuitShortHands.add(Suit.DIAMONDS.getSuitShortHand());
        if (!uniqueSuitShortHands.contains(Suit.CLUBS.getSuitShortHand())) uniqueSuitShortHands.add(Suit.CLUBS.getSuitShortHand());


        for (String currentSuitShortHand : uniqueSuitShortHands) {
            if (trumpSuitOutput != null && currentSuitShortHand.equals(trumpSuitOutput)) {
                continue; // Skip trump suit for Common Marriage
            }

            String kingString = Rank.KING.getCardLog() + currentSuitShortHand;
            String queenString = Rank.QUEEN.getCardLog() + currentSuitShortHand;

            Card kingCard = null;
            Card queenCard = null;
            List<Card> tempHand = new ArrayList<>(hand); // Use a copy for finding pairs within this suit

            for (Card card : tempHand) {
                String cardName = getCardName(card);
                if (cardName.equals(kingString)) {
                    kingCard = card;
                } else if (cardName.equals(queenString)) {
                    queenCard = card;
                }
            }

            if (kingCard != null && queenCard != null) {
                // Found a common marriage
                List<Card> meldCards = new ArrayList<>();
                meldCards.add(kingCard);
                meldCards.add(queenCard);
                return meldCards; // Return the first one found
            }
        }
        return new ArrayList<>(); // No common marriage found
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
