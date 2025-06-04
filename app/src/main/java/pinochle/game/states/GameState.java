package pinochle.game.states;

import pinochle.Pinochle;

public interface GameState {
    /**
     * Sets the context (the main game object) for the state.
     * This allows the state to access and modify the game's data.
     * @param context The Pinochle game instance.
     */
    void setContext(Pinochle context);

    /**
     * Executes the logic for this specific phase of the game.
     * This method will also be responsible for transitioning to the next state.
     */
    void execute();
}