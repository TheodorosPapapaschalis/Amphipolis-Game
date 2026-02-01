package amphipolis.model;

import java.util.List;
import java.util.Collections;

/**
 * Bag holding all tiles at the start of the game.
 * Invariant: tiles != null and tiles contains no null elements.
 */
public class Bag implements java.io.Serializable {

    private final List<Tile> tiles;

    /**
     * Creates an empty bag. Tiles are added in initialize().
     * @post tiles.isEmpty()
     */
    public Bag(List<Tile> tiles) {
        this.tiles = tiles;
        // Ανακατεύουμε τα πλακίδια εδώ για να είναι τυχαία η σειρά τραβήγματος
        Collections.shuffle(this.tiles);
    }

    /**
     * Draws (removes and returns) a random tile from the bag.
     * @return the drawn tile, or null if the bag is empty
     * @post (result == null && isEmpty()) || (result != null)
     */
    public Tile drawTile() {
        if (isEmpty()) {
            return null;
        }
        // Αφαιρούμε και επιστρέφουμε το πρώτο στοιχείο της λίστας
        return tiles.remove(0);
    }

    /**
     * @return true if there is no tile left in the bag
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    /**
     * @return an unmodifiable view of tiles for testing/inspection
     */
    public List<Tile> getTiles() {
        return tiles;
    }
}

