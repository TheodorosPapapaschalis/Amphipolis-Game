package amphipolis.model;

import amphipolis.controller.GameController;

/**
 * Archaeologist card.
 */
public class ArchaeologistCard extends Card {

    public ArchaeologistCard(Player owner) {
        super(owner);
    }

    @Override
    public void use(GameController controller) {
        super.use(controller);
        controller.applyArchaeologist();
    }
}

