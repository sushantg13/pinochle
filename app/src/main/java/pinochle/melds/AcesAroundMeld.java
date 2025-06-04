package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pinochle.Rank;
import pinochle.Suit;

public class AcesAroundMeld implements Meld {
    private static final int POINTS = 100;
    private static final String NAME = "Aces Around";

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
        Card aceSpades = null;
        Card aceHearts = null;
        Card aceDiamonds = null;
        Card aceClubs = null;

        String aceSpadesStr = Rank.ACE.getCardLog() + Suit.SPADES.getSuitShortHand();
        String aceHeartsStr = Rank.ACE.getCardLog() + Suit.HEARTS.getSuitShortHand();
        String aceDiamondsStr = Rank.ACE.getCardLog() + Suit.DIAMONDS.getSuitShortHand();
        String aceClubsStr = Rank.ACE.getCardLog() + Suit.CLUBS.getSuitShortHand();

        List<Card> tempHand = new ArrayList<>(hand);
        List<Card> foundAces = new ArrayList<>();

        for (Card card : tempHand) {
            String cardName = getCardName(card);
            if (aceSpades == null && cardName.equals(aceSpadesStr)) {
                aceSpades = card;
                foundAces.add(card);
            } else if (aceHearts == null && cardName.equals(aceHeartsStr)) {
                aceHearts = card;
                foundAces.add(card);
            } else if (aceDiamonds == null && cardName.equals(aceDiamondsStr)) {
                aceDiamonds = card;
                foundAces.add(card);
            } else if (aceClubs == null && cardName.equals(aceClubsStr)) {
                aceClubs = card;
                foundAces.add(card);
            }
        }

        if (aceSpades != null && aceHearts != null && aceDiamonds != null && aceClubs != null) {
            // Ensure we are returning distinct cards if there were duplicates in hand (though logic above picks first)
            // The foundAces list already handles this by adding cards as they are found.
            return foundAces;
        }

        return new ArrayList<>(); // Aces Around not found
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getCardLog() + suit.getSuitShortHand();
    }
}
