package amphipolis.model;

import amphipolis.AmphoraColor;
import amphipolis.TileType;

/**
 * Mosaic tile with a specific color.
 * Invariant: color != null.
 */
public class MosaicTile extends Tile {

    private final AmphoraColor color;

    /**
     * Creates a mosaic tile with the given color.
     * @param color non-null color
     * @pre color != null
     * @post getColor() == color
     */
    public MosaicTile(AmphoraColor color) {
        super(TileType.MOSAIC);
        this.color = color;
    }

    /**
     * @return the color of this mosaic tile
     * @post result != null
     */
    public AmphoraColor getColor() {
        return color;
    }
}

