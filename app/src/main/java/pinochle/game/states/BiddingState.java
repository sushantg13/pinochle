package pinochle.game.states;

import pinochle.Pinochle;

public class BiddingState implements GameState {
    private Pinochle context;

    @Override
    public void setContext(Pinochle context) {
        this.context = context;
    }

    @Override
    public void execute() {
        // This method now encapsulates all bidding logic.
        context.askForBid();

        // After bidding is complete, transition to the next state.
        // THIS IS THE NEW LOGIC
        boolean isCutThroat = Boolean.parseBoolean(context.getProperties().getProperty("mode.cutthroat", "false"));

        if (isCutThroat) {
            // If cut-throat mode is on, go to the exchange state next.
            context.setState(new CutThroatExchangeState());
        } else {
            // Otherwise, proceed as normal to trump selection.
            context.setState(new TrumpSelectionState());
        }
    }
}