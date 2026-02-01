package amphipolis.model;

import amphipolis.TileType;

/**
 * Abstract base class for all tiles in the game.
 * Invariant: type != null.
 */
public abstract class Tile implements java.io.Serializable {

    private final TileType type;

    /**
     * Creates a tile of the given type.
     * @param type non-null tile type
     * @pre type != null
     * @post getType() == type
     */
    protected Tile(TileType type) {
        this.type = type;
    }

    /**
     * Returns the type of this tile.
     * @return the tile type
     * @post result != null
     */
    public TileType getType() {
        return type;
    }
}

