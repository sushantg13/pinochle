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
        // This is where you will add the logic for Task 3.
        boolean isCutThroat = Boolean.parseBoolean(context.getProperties().getProperty("mode.cutthroat", "false"));

        if (isCutThroat) {
            // If we were implementing Task 3 now, we would go to CutThroatState
            // For now, we go to Trump Selection
            // context.setState(new CutThroatExchangeState());
            context.setState(new TrumpSelectionState());
        } else {
            context.setState(new TrumpSelectionState());
        }
    }
}