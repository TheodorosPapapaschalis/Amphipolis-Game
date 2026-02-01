package amphipolis.model;

import amphipolis.PlayerColor;
import java.util.List;
import amphipolis.StatueType;
import java.util.HashMap;
import amphipolis.AmphoraColor;
import java.util.Map;


/**
 * Represents a player in the game.
 * Invariant: name != null, tiles != null, cards != null.
 */
public class Player implements java.io.Serializable {

    private final String name;
    private final PlayerColor color;
    private final List<Tile> collectedTiles;
    private final List<Card> cards;

    private int statuePoints = 0;

    /**
     * Creates a player.
     * @param name non-null name
     * @param color non-null player color
     * @pre name != null && color != null
     * @post getName().equals(name) && getColor() == color
     */
    public Player(String name, PlayerColor color,
                  List<Tile> collectedTiles,
                  List<Card> cards) {
        this.name = name;
        this.color = color;
        this.collectedTiles = collectedTiles;
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public PlayerColor getColor() {
        return color;
    }

    public List<Tile> getCollectedTiles() {
        return collectedTiles;
    }

    public List<Card> getCards() {
        return cards;
    }

    /**
     * Adds a tile to the player's collection.
     * @param tile non-null tile
     * @pre tile != null
     * @post collectedTiles.contains(tile)
     */
    public void addTile(Tile tile) {
        if (tile != null) {
            collectedTiles.add(tile);
        }
    }

    /**
     * Sets the points awarded to the player based on the statue scoring rules.
     * <p>
     * <b>Note:</b> This method overwrites the current value. If calculating points for multiple
     * statue types sequentially, ensure this adds to the total instead of replacing it.
     * </p>
     *
     * @param points The points to assign.
     */
    public void addStatuePoints(int points) {
        this.statuePoints = points;
    }

    /**
     * Counts the total number of collected statues matching the specified type.
     *
     * @param type The {@link StatueType} to count (e.g., SPHINX or CARYATID).
     * @return The quantity of matching statues in the player's collection.
     */
    public int getStatueCount(StatueType type) {
        int count = 0;
        for (Tile t : collectedTiles) {
            if (t instanceof StatueTile) {
                StatueTile s = (StatueTile) t;
                if (s.getStatueType() == type) {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Calculates the total score of the player (mosaics, amphorae, skeletons, statues).
     * @return non-negative score
     * @pre collectedTiles != null
     * @post result >= 0
     */
    public int calculateScore() {
        int totalScore = 0;

        totalScore += calculateMosaicPoints();
        totalScore += calculateSkeletonPoints();
        totalScore += calculateAmphoraPoints();
        totalScore += statuePoints;

        return totalScore;
    }


    /**
     * Calculates points for Mosaic tiles based on set completion rules.
     * <p>
     * Awards <b>4 points</b> for every set of 4 tiles of the same color, and
     * <b>2 points</b> for every set of 4 mixed tiles formed by the remainders.
     * </p>
     *
     * @return The total score derived from mosaics.
     */
    private int calculateMosaicPoints() {
        int green = 0, red = 0, yellow = 0;

        for (Tile t : collectedTiles) {
            if (t instanceof MosaicTile) {
                // Υποθέτουμε ότι έχεις 3 χρώματα μωσαϊκών όπως λέει η εκφώνηση
                AmphoraColor c = ((MosaicTile) t).getColor();
                if (c == AmphoraColor.GREEN) green++;
                else if (c == AmphoraColor.RED) red++;
                else if (c == AmphoraColor.YELLOW) yellow++;
            }
        }

        int score = 0;

        score += (green / 4) * 4;
        score += (red / 4) * 4;
        score += (yellow / 4) * 4;

        int remainingTiles = (green % 4) + (red % 4) + (yellow % 4);
        score += (remainingTiles / 4) * 2;

        return score;
    }


    /**
     * Calculates points for Skeleton tiles by forming complete skeletons and families.
     * <p>
     * Pairs upper and lower parts to form complete entities. Awards <b>6 points</b> for every "Family"
     * (2 Adults + 1 Child) and <b>1 point</b> for any remaining complete skeletons.
     * </p>
     *
     * @return The total score derived from skeleton sets.
     */
    private int calculateSkeletonPoints() {
        int bigTop = 0, bigBottom = 0;
        int smallTop = 0, smallBottom = 0;

        for (Tile t : collectedTiles) {
            if (t instanceof SkeletonTile) {
                SkeletonTile s = (SkeletonTile) t;
                if (s.isLargeSkeleton()) { // Μεγάλος (Ενήλικας)
                    if (s.isUpperPart()) bigTop++; else bigBottom++;
                } else { // Μικρός (Παιδί)
                    if (s.isUpperPart()) smallTop++; else smallBottom++;
                }
            }
        }


        int completeBig = Math.min(bigTop, bigBottom);
        int completeSmall = Math.min(smallTop, smallBottom);

        int score = 0;

        while (completeBig >= 2 && completeSmall >= 1) {
            score += 6;
            completeBig -= 2;
            completeSmall -= 1;
        }

        score += completeBig;
        score += completeSmall;

        return score;
    }

    /**
     * Calculates points for Amphora tiles by grouping them into sets of distinct colors.
     * <p>
     * Uses a greedy algorithm to maximize set sizes. Points are awarded per set:
     * <b>6 colors=6pts, 5=4pts, 4=2pts, 3=1pt</b>. Sets smaller than 3 yield no points.
     * </p>
     *
     * @return The total score derived from amphora collections.
     */
    private int calculateAmphoraPoints() {
        Map<AmphoraColor, Integer> counts = new HashMap<>();

        for (Tile t : collectedTiles) {
            if (t instanceof AmphoraTile) {
                AmphoraColor c = ((AmphoraTile) t).getColor();
                counts.put(c, counts.getOrDefault(c, 0) + 1);
            }
        }

        int score = 0;

        while (true) {
            int distinctColors = 0;
            for (AmphoraColor c : counts.keySet()) {
                if (counts.get(c) > 0) distinctColors++;
            }

            if (distinctColors == 0) break;

            for (AmphoraColor c : counts.keySet()) {
                if (counts.get(c) > 0) {
                    counts.put(c, counts.get(c) - 1);
                }
            }

            if (distinctColors >= 6) score += 6;
            else if (distinctColors == 5) score += 4;
            else if (distinctColors == 4) score += 2;
            else if (distinctColors == 3) score += 1;
        }

        return score;
    }
}

