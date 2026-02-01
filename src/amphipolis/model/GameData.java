package amphipolis.model;

import java.io.Serializable;
import java.util.List;

/**
 * A serializable container class used to encapsulate the entire game state for saving and loading purposes.
 */
public class GameData implements Serializable {

    private static final long serialVersionUID = 1L;
    public Board board;
    public Bag bag;
    public List<Player> players;
    public int currentPlayerIndex;
    public boolean isSoloMode;
    public Player thief;

    /**
     * Constructs a snapshot of the current game state.
     *
     * @param board            The current game board.
     * @param bag              The current state of the tile bag.
     * @param players          The list of participating players.
     * @param idx              The index of the current player.
     * @param isSolo           True if the game is in Solo Mode.
     * @param thief            The automated opponent (if in Solo Mode).
     */
    public GameData(Board board, Bag bag, List<Player> players, int idx, boolean isSolo, Player thief) {
        this.board = board;
        this.bag = bag;
        this.players = players;
        this.currentPlayerIndex = idx;
        this.isSoloMode = isSolo;
        this.thief = thief;
    }
}
