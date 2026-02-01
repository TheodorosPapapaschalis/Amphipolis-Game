package amphipolis.view;

import amphipolis.model.Board;
import amphipolis.model.Player;
import java.util.List;

/**
 * View interface for the game (MVC).
 * Invariant: implementations should not be null-safe for parameters.
 */
public interface GameView {

    /**
     * Updates the entire board display.
     * @param board non-null board state
     * @pre board != null
     */
    void updateBoard(Board board);

    /**
     * Shows which player has the current turn.
     * @param player non-null player
     * @pre player != null
     */
    void showCurrentPlayer(Player player);

    /**
     * Shows a message to the user.
     * @param message non-null message
     * @pre message != null
     */
    void showMessage(String message);

    /**
     * Shows final scores and winner.
     * @param players non-null list of players
     * @pre players != null && !players.isEmpty()
     */
    void showGameOver(List<Player> players);

    /**
     * Updates the visual timer display to show the remaining time for the current turn.
     *
     * @param seconds The remaining time in seconds to display.
     */
    void updateTimer(int seconds);

    /**
     * Refreshes the information panel to reflect the current state of the specified player.
     * <p>
     * This method is responsible for updating UI elements such as:
     * <ul>
     * <li>The displayed player name.</li>
     * <li>The character card buttons (enabling/disabling them based on whether they have been used).</li>
     * <li>The player's current stash or score (if applicable).</li>
     * </ul>
     * It should be called at the start of a turn and whenever a player performs an action
     * that changes their state (e.g., using a card).
     * </p>
     *
     * @param player The {@link Player} object whose data will be displayed on the panel.
     */
    void updateInfoPanel(Player player);
}

