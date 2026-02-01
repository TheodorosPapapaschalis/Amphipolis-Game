package amphipolis.test;

import amphipolis.AmphoraColor;
import amphipolis.model.Bag;
import amphipolis.model.Tile;
import amphipolis.model.MosaicTile;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class BagTest {

    @Test
    public void testDrawTile() {
        // 1. Προετοιμασία: Φτιάχνουμε μια λίστα με 10 πλακίδια
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tiles.add(new MosaicTile(AmphoraColor.BLUE));
        }

        // 2. Δημιουργία της Σακούλας
        Bag bag = new Bag(tiles);

        // 3. Ελέγχουμε ότι η σακούλα δεν είναι άδεια στην αρχή
        assertFalse("Η σακούλα δεν πρέπει να είναι άδεια αρχικά", bag.isEmpty());

        // 4. Τραβάμε ένα πλακίδιο
        Tile drawnTile = bag.drawTile();

        // 5. Ελέγχουμε ότι πήραμε όντως πλακίδιο (δεν είναι null)
        assertNotNull("Το πλακίδιο που τραβήξαμε δεν πρέπει να είναι null", drawnTile);
    }

    @Test
    public void testBagEmptiesCorrectly() {
        // 1. Φτιάχνουμε σακούλα με 1 μόνο πλακίδιο
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new MosaicTile(AmphoraColor.BROWN));
        Bag bag = new Bag(tiles);

        // 2. Τραβάμε το μοναδικό πλακίδιο
        bag.drawTile();

        // 3. Τώρα η σακούλα πρέπει να είναι άδεια
        assertTrue("Η σακούλα πρέπει να είναι άδεια αφού τραβήξαμε το τελευταίο πλακίδιο", bag.isEmpty());

        // 4. Αν ξανατραβήξουμε, πρέπει να επιστρέψει null
        assertNull("Αν τραβήξουμε από άδεια σακούλα, πρέπει να πάρουμε null", bag.drawTile());
    }
}
