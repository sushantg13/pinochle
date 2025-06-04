package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;
import pinochle.Rank;
import pinochle.Suit;

public class AceRunRoyalMarriageMeld implements Meld {
    private static final int POINTS = 230;
    private static final String NAME = "Ace Run + Royal Marriage";

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

        List<Card> meldCards = new ArrayList<>();
        List<Card> tempHand = new ArrayList<>(hand);

        // Required cards: A, T, J, K, K, Q, Q of trump
        Card aceTrump = null;
        Card tenTrump = null;
        Card jackTrump = null;
        List<Card> kingsTrump = new ArrayList<>();
        List<Card> queensTrump = new ArrayList<>();

        String aceTrumpStr = Rank.ACE.getCardLog() + trumpSuit;
        String tenTrumpStr = Rank.TEN.getCardLog() + trumpSuit;
        String jackTrumpStr = Rank.JACK.getCardLog() + trumpSuit;
        String kingTrumpStr = Rank.KING.getCardLog() + trumpSuit;
        String queenTrumpStr = Rank.QUEEN.getCardLog() + trumpSuit;

        for (Card card : tempHand) {
            String cardName = getCardName(card);
            if (cardName.equals(aceTrumpStr) && aceTrump == null) {
                aceTrump = card;
            } else if (cardName.equals(tenTrumpStr) && tenTrump == null) {
                tenTrump = card;
            } else if (cardName.equals(jackTrumpStr) && jackTrump == null) {
                jackTrump = card;
            } else if (cardName.equals(kingTrumpStr) && kingsTrump.size() < 2) {
                kingsTrump.add(card);
            } else if (cardName.equals(queenTrumpStr) && queensTrump.size() < 2) {
                queensTrump.add(card);
            }
        }

        if (aceTrump != null && tenTrump != null && jackTrump != null &&
            kingsTrump.size() == 2 && queensTrump.size() == 2) {
            meldCards.add(aceTrump);
            meldCards.add(tenTrump);
            meldCards.add(jackTrump);
            meldCards.addAll(kingsTrump);
            meldCards.addAll(queensTrump);
            return meldCards;
        }

        return new ArrayList<>();
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
