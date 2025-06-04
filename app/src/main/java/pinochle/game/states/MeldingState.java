package pinochle.game.states;

import pinochle.Pinochle;

public class MeldingState implements GameState {
    private Pinochle context;

    @Override
    public void setContext(Pinochle context) {
        this.context = context;
    }

    @Override
    public void execute() {
        // Calculate and apply melding scores for each player
        for (int i = 0; i < context.nbPlayers; i++) {
            context.getScores()[i] = context.calculateMeldingScore(context.getHands()[i].getCardList());
            context.updateScore(i);
            context.delay(context.getDelayTime());
        }
        context.addTrumpInfoToLog();

        // Transition to the next state
        context.setState(new TrickTakingState());
    }
}