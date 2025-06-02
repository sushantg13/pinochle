package pinochle.bidding;

import ch.aplu.jcardgame.Hand;
import pinochle.melds.Meld; // Import Meld if needed by the interface, though not used in this random strategy
import java.util.List;
import java.util.Random;

public class RandomBiddingStrategy implements BiddingStrategy {
    private final Random random;

    public RandomBiddingStrategy(long seed) {
        // It's good practice to allow seeding for deterministic behavior if needed,
        // or use a default Random() if no seed is provided.
        // The original Pinochle class used a static Random with a fixed seed.
        // We'll replicate that behavior if a specific seed is desired for this strategy.
        this.random = new Random(seed);
    }

    public RandomBiddingStrategy() {
        // Default constructor uses a new Random instance
        this.random = new Random();
    }

    @Override
    public int determineBid(Hand hand, int currentBid, boolean isFirstBidder, List<Meld> meldEvaluators, String trumpSuit) {
        // This strategy doesn't use hand, meldEvaluators, or trumpSuit for its decision.
        // It returns the bid *value* (10 or 20), not the total new bid.
        // Or 0 to pass.
        // The original logic was:
        // int randomBidBase = random.nextInt(3); // 0, 1, or 2
        // bidValue = randomBidBase * 10; // 0, 10, or 20
        // This bidValue was then *added* to currentBid in Pinochle.java if not 0.
        // If it was 0, it meant pass.

        int randomChoice = random.nextInt(3); // 0 for pass, 1 for 10, 2 for 20
        if (randomChoice == 0) {
            return 0; // Pass
        } else if (randomChoice == 1) {
            return 10; // Bid 10 more (or 10 if first bidder)
        } else {
            return 20; // Bid 20 more (or 20 if first bidder)
        }
    }
}
