package pinochle.game.states;
import ch.aplu.jgamegrid.Actor;
import pinochle.Pinochle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameOverState implements GameState {
    private Pinochle context;

    @Override
    public void setContext(Pinochle context) {
        this.context = context;
    }

    @Override
    public void execute() {
        for (int i = 0; i < context.nbPlayers; i++) context.updateScore(i);
        int maxScore = 0;
        for (int i = 0; i < context.nbPlayers; i++) if (context.getScores()[i] > maxScore) maxScore = context.getScores()[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < context.nbPlayers; i++) if (context.getScores()[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        context.addActor(new Actor("sprites/gameover.gif"), context.getTextLocation());
        context.setStatusText(winText);
        context.refresh();
        context.addEndOfGameToLog(winners);

        // Stop the game loop
        context.stopGame();
    }
}