package amphipolis.model;

import amphipolis.controller.GameController;

/**
 * Professor card.
 */
public class ProfessorCard extends Card {

    public ProfessorCard(Player owner) {
        super(owner);
    }

    @Override
    public void use(GameController controller) {
        super.use(controller);
        controller.applyProfessor();
    }
}

