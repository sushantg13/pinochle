package pinochle.bidding;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import pinochle.Rank;
import pinochle.Suit;
import pinochle.melds.Meld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SmartBiddingStrategy implements BiddingStrategy {
    private final Random random; // For tie-breaking in trump suit selection

    public SmartBiddingStrategy(long seed) {
        this.random = new Random(seed);
    }
    
    public SmartBiddingStrategy() {
        this.random = new Random();
    }

    @Override
    public int determineBid(Hand hand, int currentBid, boolean isFirstBidder, List<Meld> meldEvaluators, String preDeterminedTrumpSuit) {
        // Determine the computer's assumed trump suit for internal calculations
        String assumedTrumpSuit = determineLikelyTrumpSuit(hand.getCardList());
        
        // Calculate meld score based on the assumed trump suit
        int meldScore = calculateMeldScore(hand.getCardList(), meldEvaluators, assumedTrumpSuit);

        if (isFirstBidder) {
            // Opening bid is equal to the total meld score of its hand.
            // The return value should be the actual bid amount.
            return meldScore;
        } else {
            // Subsequent bids:
            // Prepare to raise by 20 if 6+ cards in the same suit, else 10.
            int bidIncrease = (countMaxCardsInSuit(hand.getCardList()) >= 6) ? 20 : 10;
            
            int potentialNewBid = currentBid + bidIncrease;

            // Calculate the maximum of:
            // 1. Total card value of the majority suit (assumed trump).
            // 2. Total card value of the suit with most Aces, 10s, and Kings.
            int majoritySuitValue = calculateSuitCardValue(hand.getCardList(), assumedTrumpSuit);
            int highCardPowerSuitValue = calculateHighCardPowerSuitValue(hand.getCardList());
            
            int additionalValue = Math.max(majoritySuitValue, highCardPowerSuitValue);

            // Bid only if new bid <= meld score + additionalValue. Otherwise, pass.
            if (potentialNewBid <= meldScore + additionalValue) {
                return bidIncrease; // Return the amount to increase by
            } else {
                return 0; // Pass
            }
        }
    }

    private int calculateMeldScore(List<Card> handCards, List<Meld> meldEvaluators, String trumpSuit) {
        int score = 0;
        List<Card> availableCards = new ArrayList<>(handCards);

        for (Meld meldStrategy : meldEvaluators) {
            List<Card> foundMeldCards = meldStrategy.getMeldCards(availableCards, trumpSuit);
            if (foundMeldCards != null && !foundMeldCards.isEmpty()) {
                score += meldStrategy.getPoints();
                for (Card cardToRemove : foundMeldCards) {
                    availableCards.remove(cardToRemove);
                }
            }
        }
        return score;
    }

    private String determineLikelyTrumpSuit(List<Card> handCards) {
        Map<String, Integer> suitCounts = new HashMap<>();
        Map<String, List<Card>> cardsBySuit = new HashMap<>();

        for (Suit s : Suit.values()) { // Consider only standard suits
             if (s.getSuitShortHand().length() == 1) { // Filter out "SPADESTWO" etc.
                suitCounts.put(s.getSuitShortHand(), 0);
                cardsBySuit.put(s.getSuitShortHand(), new ArrayList<>());
            }
        }

        for (Card card : handCards) {
            Suit suit = (Suit) card.getSuit();
            String shortHand = suit.getSuitShortHand();
            if (suitCounts.containsKey(shortHand)) {
                suitCounts.put(shortHand, suitCounts.get(shortHand) + 1);
                cardsBySuit.get(shortHand).add(card);
            }
        }

        int maxCount = 0;
        List<String> majoritySuits = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : suitCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                majoritySuits.clear();
                majoritySuits.add(entry.getKey());
            } else if (entry.getValue() == maxCount) {
                majoritySuits.add(entry.getKey());
            }
        }

        if (majoritySuits.isEmpty()) { // Should not happen with a valid hand
            // Default to a random suit from standard suits if hand is empty or unusual
            List<String> allSuits = suitCounts.keySet().stream().collect(Collectors.toList());
            if(allSuits.isEmpty()) return Suit.SPADES.getSuitShortHand(); // Absolute fallback
            return allSuits.get(random.nextInt(allSuits.size()));
        }
        
        // If tie, randomly choose one
        return majoritySuits.get(random.nextInt(majoritySuits.size()));
    }

    private int countMaxCardsInSuit(List<Card> handCards) {
        Map<String, Integer> suitCounts = new HashMap<>();
         for (Suit s : Suit.values()) {
            if (s.getSuitShortHand().length() == 1) {
                 suitCounts.put(s.getSuitShortHand(), 0);
            }
        }
        for (Card card : handCards) {
            Suit suit = (Suit) card.getSuit();
            String shortHand = suit.getSuitShortHand();
            if(suitCounts.containsKey(shortHand)){
                suitCounts.put(shortHand, suitCounts.get(shortHand) + 1);
            }
        }
        return suitCounts.values().stream().max(Integer::compareTo).orElse(0);
    }

    private int calculateSuitCardValue(List<Card> handCards, String suitShortHand) {
        int value = 0;
        for (Card card : handCards) {
            Suit s = (Suit) card.getSuit();
            if (s.getSuitShortHand().equals(suitShortHand)) {
                Rank r = (Rank) card.getRank();
                // Using trick-taking points as per specification for "card value"
                if (r == Rank.NINE && s.getSuitShortHand().equals(suitShortHand)) { // Assuming suitShortHand is trump for this context
                    // This method is called with assumedTrumpSuit, so this check is fine.
                    // However, the spec for "total card value of the majority suit" doesn't specify if it's trump.
                    // Let's assume standard scoring for this calculation, and trump logic is separate.
                    // Re-evaluating: "total card value of the majority suit". This implies standard values.
                    // The "9 of Trump is 10 points" is for end-game scoring.
                    // For bidding evaluation, it's safer to use base values.
                    value += r.getScoreValue(); // Standard score value
                } else {
                    value += r.getScoreValue();
                }
            }
        }
        return value;
    }

    private int calculateHighCardPowerSuitValue(List<Card> handCards) {
        Map<String, Integer> suitPowerScores = new HashMap<>();
         for (Suit s : Suit.values()) {
            if (s.getSuitShortHand().length() == 1) {
                suitPowerScores.put(s.getSuitShortHand(), 0);
            }
        }

        for (Card card : handCards) {
            Suit suit = (Suit) card.getSuit();
            Rank rank = (Rank) card.getRank();
            String shortHand = suit.getSuitShortHand();

            if(suitPowerScores.containsKey(shortHand)){
                if (rank == Rank.ACE || rank == Rank.TEN || rank == Rank.KING) {
                    suitPowerScores.put(shortHand, suitPowerScores.get(shortHand) + rank.getScoreValue());
                }
            }
        }
        
        if (suitPowerScores.isEmpty()) return 0;
        return Collections.max(suitPowerScores.values());
    }
}
