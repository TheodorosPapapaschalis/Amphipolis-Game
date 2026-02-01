package amphipolis.model;

import amphipolis.TileType;

/**
 * Skeleton tile representing upper/lower and large/small skeleton parts.
 * Invariant: true (domain-specific constraints in Phase B).
 */
public class SkeletonTile extends Tile {

    private final boolean upperPart;
    private final boolean largeSkeleton;

    /**
     * Creates a skeleton tile part.
     * @param upperPart true if this is an upper part
     * @param largeSkeleton true if part of large skeleton
     * @post isUpperPart() == upperPart && isLargeSkeleton() == largeSkeleton
     */
    public SkeletonTile(boolean upperPart, boolean largeSkeleton) {
        super(TileType.SKELETON);
        this.upperPart = upperPart;
        this.largeSkeleton = largeSkeleton;
    }

    public boolean isUpperPart() {
        return upperPart;
    }

    public boolean isLargeSkeleton() {
        return largeSkeleton;
    }
}

