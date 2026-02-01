package amphipolis.model;

import amphipolis.controller.GameController;

/**
 * Digger (Excavator) card.
 */

public class DiggerCard extends Card {
    public DiggerCard(Player owner) {
        super(owner);
    }

    @Override
    public void use(GameController controller) {
        super.use(controller);
        controller.applyDigger();
    }

}
