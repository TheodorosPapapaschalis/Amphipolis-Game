package amphipolis.model;

import amphipolis.controller.GameController;

/**
 * Abstract card of a character.
 * Invariant: owner != null.
 */
public abstract class Card implements java.io.Serializable {

    private final Player owner;
    private boolean used;

    /**
     * Creates a card for the given owner.
     * @param owner non-null player
     * @pre owner != null
     * @post getOwner() == owner && !isUsed()
     */
    protected Card(Player owner) {
        this.owner = owner;
        this.used = false;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Uses this card effect on the game.
     * @param controller non-null controller
     * @pre !isUsed() && controller != null
     * @post isUsed()
     */
    public void use(GameController controller) {
        this.used = true;
    }
}

