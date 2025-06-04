package pinochle.game.states;

import pinochle.Pinochle;

public class TrumpSelectionState implements GameState {
    private Pinochle context;

    @Override
    public void setContext(Pinochle context) {
        this.context = context;
    }

    @Override
    public void execute() {
        // This method now encapsulates trump selection logic.
        context.askForTrumpCard();

        // Transition to the next state
        context.setState(new MeldingState());
    }
}