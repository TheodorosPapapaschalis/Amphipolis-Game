package amphipolis.model;

import amphipolis.StatueType;
import amphipolis.TileType;

/**
 * Statue tile: caryatid or sphinx.
 * Invariant: type != null.
 */
public class StatueTile extends Tile {

    private final StatueType statueType;

    /**
     * Creates a statue tile.
     * @param statueType non-null statue type
     * @pre statueType != null
     * @post getStatueType() == statueType
     */
    public StatueTile(StatueType statueType) {
        super(TileType.STATUE);
        this.statueType = statueType;
    }

    public StatueType getStatueType() {
        return statueType;
    }
}

