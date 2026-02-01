package amphipolis.model;

import amphipolis.controller.GameController;

/**
 * Assistant card: player takes 1 tile from any area.
 */
public class AssistantCard extends Card {

    public AssistantCard(Player owner) {
        super(owner);
    }

    @Override
    public void use(GameController controller) {
        super.use(controller);
        controller.applyAssistant();
    }
}

