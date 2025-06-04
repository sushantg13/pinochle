package pinochle.game.states;

import pinochle.Pinochle;

public class FinalScoringState implements GameState {
    private Pinochle context;

    @Override
    public void setContext(Pinochle context) {
        this.context = context;
    }

    @Override
    public void execute() {
        context.updateTrickScore();
        // Transition to the final state
        context.setState(new GameOverState());
    }
}