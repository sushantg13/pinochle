package pinochle.bidding;

import pinochle.melds.Meld;
import java.util.List;
import java.util.Properties;

public class BiddingStrategyFactory {

    /**
     * Creates a BiddingStrategy based on the provided game properties.
     * @param properties The game properties, used to check for smart bidding enablement.
     * @param meldEvaluators A list of meld evaluators, passed to the SmartBiddingStrategy.
     * @param seed A seed for random number generation, used by RandomBiddingStrategy and potentially SmartBiddingStrategy for tie-breaking.
     * @return An instance of BiddingStrategy (either SmartBiddingStrategy or RandomBiddingStrategy).
     */
    public static BiddingStrategy createBiddingStrategy(Properties properties, List<Meld> meldEvaluators, long seed) {
        boolean smartBiddingEnabled = Boolean.parseBoolean(properties.getProperty("players.0.smartbids", "false"));

        if (smartBiddingEnabled) {
            return new SmartBiddingStrategy(seed); // SmartBiddingStrategy also takes the seed for its internal Random
        } else {
            return new RandomBiddingStrategy(seed);
        }
    }
}
