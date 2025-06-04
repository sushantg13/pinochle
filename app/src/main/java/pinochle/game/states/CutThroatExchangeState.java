package pinochle.game.states;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import pinochle.Pinochle;

import java.util.ArrayList;
import java.util.List;

public class CutThroatExchangeState implements GameState {
    private Pinochle context;

    @Override
    public void setContext(Pinochle context) {
        this.context = context;
    }

    @Override
    public void execute() {
        context.setStatusText("Cut-throat mode: Exchanging cards.");

        // 1. Dealer (bid loser) flips top two cards from stockpile
        Hand stockpile = context.getPack();
        Hand faceUpCards = new Hand(context.getDeck());
        for (int i = 0; i < 2; i++) {
            if (!stockpile.isEmpty()) {
                Card card = stockpile.get(0);
                card.removeFromHand(false);
                faceUpCards.insert(card, false);
            }
        }
        // Make these cards visible on the GUI (this part requires GUI logic in Pinochle.java)
        context.showFaceUpCards(faceUpCards);

        // 2. Non-dealer (bid winner) chooses one card
        int bidWinnerIndex = context.getBidWinPlayerIndex();
        int bidLoserIndex = (bidWinnerIndex + 1) % 2;

        Card chosenCard = context.letPlayerChooseCard(bidWinnerIndex, faceUpCards);
        chosenCard.removeFromHand(false);
        context.getHands()[bidWinnerIndex].insert(chosenCard, true);


        // 3. Dealer takes the remaining card
        Card remainingCard = faceUpCards.get(0);
        remainingCard.removeFromHand(false);
        context.getHands()[bidLoserIndex].insert(remainingCard, true);
        context.hideFaceUpCards(faceUpCards);


        // 4. The rest of the stockpile is drawn automatically, winner first
        while (!stockpile.isEmpty()) {
            // Winner draws
            if (!stockpile.isEmpty()) {
                stockpile.get(0).removeFromHand(false);
                context.getHands()[bidWinnerIndex].insert(stockpile.get(0), true);
            }
            // Loser draws
            if (!stockpile.isEmpty()) {
                stockpile.get(0).removeFromHand(false);
                context.getHands()[bidLoserIndex].insert(stockpile.get(0), true);

            }
        }
        context.getHands()[0].sort(Hand.SortType.SUITPRIORITY, true);
        context.getHands()[1].sort(Hand.SortType.SUITPRIORITY, true);


        // 5. Each player discards down to 12 cards
        context.letPlayerDiscard(context.HUMAN_PLAYER_INDEX);
        context.letPlayerDiscard(context.COMPUTER_PLAYER_INDEX);


        // 6. Transition to the Melding state
        context.setState(new MeldingState());
    }
}