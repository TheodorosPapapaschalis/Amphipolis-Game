package amphipolis.model;

import amphipolis.TileType;

/**
 * Landslide tile placed in the entrance area.
 */
public class LandslideTile extends Tile {

    /**
     * Creates a landslide tile.
     * @post getType() == TileType.LANDSLIDE
     */
    public LandslideTile() {
        super(TileType.LANDSLIDE);
    }
}

