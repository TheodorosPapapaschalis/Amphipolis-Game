package amphipolis.test;

import amphipolis.AmphoraColor;
import amphipolis.model.Player;
import amphipolis.model.Tile;
import amphipolis.model.MosaicTile;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import amphipolis.PlayerColor;

public class PlayerTest {

    @Test
    public void testAddTileToStash() {
        // 1. Δημιουργία Παίκτη
        Player player = new Player(
                "TestPlayer",
                PlayerColor.YELLOW,
                new ArrayList<>(),
                new ArrayList<>()
        );

        // 2. Ελέγχουμε ότι αρχικά δεν έχει πλακίδια
        assertEquals("Ο παίκτης πρέπει να ξεκινάει με 0 πλακίδια", 0, player.getCollectedTiles().size());

        // 3. Του δίνουμε ένα πλακίδιο
        Tile t = new MosaicTile(AmphoraColor.BROWN);
        player.addTile(t);

        // 4. Ελέγχουμε ότι τώρα έχει 1 πλακίδιο
        assertEquals("Ο παίκτης πρέπει να έχει 1 πλακίδιο", 1, player.getCollectedTiles().size());

        // 5. Ελέγχουμε ότι είναι το ίδιο πλακίδιο
        assertEquals("Το πλακίδιο στη συλλογή πρέπει να είναι αυτό που προσθέσαμε", t, player.getCollectedTiles().get(0));
    }
}
