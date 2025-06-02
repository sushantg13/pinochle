package pinochle.melds;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pinochle.Rank;
import pinochle.Suit;

public class AceRunExtraQueenMeld implements Meld {
    private static final int POINTS = 190;
    private static final String NAME = "Ace Run + Extra Queen";

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

        List<String> runCardsStrings = Arrays.asList(
                Rank.ACE.getCardLog() + trumpSuit,
                Rank.TEN.getCardLog() + trumpSuit,
                Rank.KING.getCardLog() + trumpSuit,
                Rank.QUEEN.getCardLog() + trumpSuit,
                Rank.JACK.getCardLog() + trumpSuit
        );
        String extraQueenString = Rank.QUEEN.getCardLog() + trumpSuit;

        List<Card> meldCards = new ArrayList<>();
        List<Card> tempHand = new ArrayList<>(hand);
        boolean runFound = false;
        List<Card> runCardsFound = new ArrayList<>();

        // Check for the run first
        for (String cardString : runCardsStrings) {
            Card foundCard = null;
            for (Card cardInHand : tempHand) {
                if (getCardName(cardInHand).equals(cardString)) {
                    foundCard = cardInHand;
                    break;
                }
            }
            if (foundCard != null) {
                runCardsFound.add(foundCard);
            } else {
                return new ArrayList<>(); // Run not complete
            }
        }

        // If run is found, remove its cards from tempHand before looking for extra queen
        if (runCardsFound.size() == runCardsStrings.size()){
            runFound = true;
            for(Card runCard : runCardsFound){
                // Crucial: ensure we are removing the exact card instances from tempHand
                // that were identified for the run.
                boolean removed = tempHand.remove(runCard);
                // if (!removed) { /* This would indicate a logic flaw or issue with card equality */ }
            }
            meldCards.addAll(runCardsFound);
        } else {
             return new ArrayList<>(); // Should not happen
        }

        // Check for the extra Queen from the remaining cards
        Card extraQueen = null;
        for (Card cardInHand : tempHand) {
            if (getCardName(cardInHand).equals(extraQueenString)) {
                extraQueen = cardInHand;
                break;
            }
        }

        if (runFound && extraQueen != null) {
            meldCards.add(extraQueen);
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
