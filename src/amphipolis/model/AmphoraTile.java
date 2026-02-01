package amphipolis.model;

import amphipolis.AmphoraColor;
import amphipolis.TileType;

/**
 * Amphora tile with a specific color.
 * Invariant: color != null.
 */
public class AmphoraTile extends Tile {

    private final AmphoraColor color;

    /**
     * Creates an amphora tile with the given color.
     * @param color non-null amphora color
     * @pre color != null
     * @post getColor() == color
     */
    public AmphoraTile(AmphoraColor color) {
        super(TileType.AMPHORA);
        this.color = color;
    }

    /**
     * @return the color of this amphora tile
     * @post result != null
     */
    public AmphoraColor getColor() {
        return color;
    }
}
