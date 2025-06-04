package pinochle.bidding;

import ch.aplu.jcardgame.Hand;
import pinochle.Pinochle; // Added for context, might be needed for meld evaluators or other game state
import java.util.List;
import pinochle.melds.Meld;


public interface BiddingStrategy {
    /**
     * Determines the bid amount for the computer player.
     * @param hand The computer player's hand.
     * @param currentBid The current highest bid (0 if computer is first bidder).
     * @param isFirstBidder Whether the computer is the first bidder.
     * @param meldEvaluators A list of meld evaluators to calculate potential meld scores.
     * @param trumpSuit The current trump suit (can be null if not yet determined, e.g., for initial meld calculation).
     * @return The bid amount (a value like 10, 20, etc., representing the *increase* over currentBid, or the total bid if firstBidder. 0 if passing).
     */
    int determineBid(Hand hand, int currentBid, boolean isFirstBidder, List<Meld> meldEvaluators, String trumpSuit);
}
