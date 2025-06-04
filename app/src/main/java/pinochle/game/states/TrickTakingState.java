package pinochle.game.states;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import pinochle.Pinochle;

public class TrickTakingState implements GameState {
    private Pinochle context;

    @Override
    public void setContext(Pinochle context) {
        this.context = context;
    }

    @Override
    public void execute() {
        int nextPlayer = context.getBidWinPlayerIndex();
        int numberOfCards = context.getHands()[0].getNumberOfCards();
        context.addPlayerCardsToLog();

        for (int i = 0; i < numberOfCards; i++) {
            context.addRoundInfoToLog(i);
            for (int j = 0; j < context.nbPlayers; j++) {
                Card selected = context.playCardForPlayer(nextPlayer);
                context.addCardPlayedToLog(nextPlayer, selected);
                context.getPlayingArea().insert(selected, true);
                context.getPlayingArea().draw();

                if (context.getPlayingArea().getCardList().size() == 2) {
                    context.delay(context.getDelayTime());
                    int trickWinPlayerIndex = context.checkWinner(nextPlayer);
                    context.transferCardsToWinner(trickWinPlayerIndex);
                    nextPlayer = trickWinPlayerIndex;
                } else {
                    nextPlayer = (nextPlayer + 1) % context.nbPlayers;
                }
            }
        }
        // Transition to the next state
        context.setState(new FinalScoringState());
    }
}