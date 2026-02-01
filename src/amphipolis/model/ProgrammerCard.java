package amphipolis.model;

import amphipolis.controller.GameController;

/**
 * Programmer card.
 */
public class ProgrammerCard extends Card {

    public ProgrammerCard(Player owner) {
        super(owner);
    }

    @Override
    public void use(GameController controller) {
        super.use(controller);
        controller.applyProgrammer();
    }
}

