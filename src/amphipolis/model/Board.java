package amphipolis.model;

import amphipolis.AreaType;
import java.util.List;
import java.util.Map;

/**
 * Represents the game board and its areas.
 * Invariant: areas contains keys for all AreaType values.
 */
public class Board implements java.io.Serializable {

    private final Map<AreaType, List<Tile>> areas;
    private int landslideCount = 0;

    /**
     * Creates an empty board with all areas initialized.
     * @pre areas != null
     * @post getAreas() != null
     */
    public Board(Map<AreaType, List<Tile>> areas) {
        this.areas = areas;
    }

    public Map<AreaType, List<Tile>> getAreas() {
        return areas;
    }

    /**
     * Initializes the board with starting tiles from the bag.
     * @param bag non-null bag
     * @pre bag != null
     */
    public void initialize(Bag bag) {
        moveFirstOccurrenceToBoard(bag, MosaicTile.class);
        moveFirstOccurrenceToBoard(bag, StatueTile.class);
        moveFirstOccurrenceToBoard(bag, AmphoraTile.class);
        moveFirstOccurrenceToBoard(bag, SkeletonTile.class);
    }

    /**
     * Categorizes and places a tile into the correct board area based on its specific type.
     * <p>
     * Also increments the {@code landslideCount} if the placed tile is a Landslide,
     * which checks against the game-over condition.
     * </p>
     *
     * @param tile The {@link Tile} to be added to the board.
     */
    public void placeTile(Tile tile) {
        if (tile instanceof MosaicTile) {
            areas.get(AreaType.MOSAIC_AREA).add(tile);
        } else if (tile instanceof StatueTile) {
            areas.get(AreaType.STATUE_AREA).add(tile);
        } else if (tile instanceof AmphoraTile) {
            areas.get(AreaType.AMPHORA_AREA).add(tile);
        } else if (tile instanceof SkeletonTile) {
            areas.get(AreaType.SKELETON_AREA).add(tile);
        } else if (tile instanceof LandslideTile) {
            areas.get(AreaType.LANDSLIDE_AREA).add(tile);
            landslideCount++; // Αυξάνουμε τον μετρητή
        }
    }


    /**
     * Searches the bag for the first tile matching the specified type, transfers it to the board, and stops.
     *
     * @param bag  The source bag to search.
     * @param type The class of the tile to find (e.g., MosaicTile.class).
     */
    private void moveFirstOccurrenceToBoard(Bag bag, Class<? extends Tile> type) {
        List<Tile> bagTiles = bag.getTiles();

        for (int i = 0; i < bagTiles.size(); i++) {
            Tile t = bagTiles.get(i);
            // Ελέγχουμε αν το πλακίδιο είναι του τύπου που ψάχνουμε
            if (type.isInstance(t)) {
                bagTiles.remove(i); // Το αφαιρούμε από τη σακούλα
                placeTile(t);       // Το βάζουμε στο ταμπλό
                return;             // Τελειώσαμε για αυτόν τον τύπο
            }
        }
    }


    /**
     * @return true if all landslide positions are filled and game should end.
     */
    public boolean isGameOver() {
        return landslideCount >= 16;
    }

    /**
     * Returns tiles in a specific area.
     * @param area non-null area
     * @return non-null list of tiles (possibly empty)
     * @pre area != null
     * @post result != null
     */
    public List<Tile> getTilesInArea(AreaType area) {
        return areas.get(area);
    }
}

