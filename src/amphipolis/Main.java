package amphipolis;

import amphipolis.controller.GameController;
import amphipolis.model.*;
import amphipolis.view.SwingGameView;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        SwingGameView view = new SwingGameView();

        try {
            // 1. Ερώτηση για τον αριθμό παικτών
            Object[] options = {"1 Player (Solo)", "4 Players"};
            int choice = JOptionPane.showOptionDialog(view,
                    "Choose Game Mode:",
                    "Amphipolis Setup",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            boolean isSoloMode = (choice == 0); // 0 = 1 Player, 1 = 4 Players

            // 2. Δημιουργία Ταμπλό
            Map<AreaType, List<Tile>> areas = new HashMap<>();
            for (AreaType type : AreaType.values()) {
                areas.put(type, new ArrayList<>());
            }
            Board board = new Board(areas);

            // 3. Δημιουργία Πλακιδίων
            List<Tile> allTiles = createAllTiles();


            moveOneTileToBoard(allTiles, MosaicTile.class, board);

            moveOneTileToBoard(allTiles, StatueTile.class, board);

            moveOneTileToBoard(allTiles, AmphoraTile.class, board);

            moveOneTileToBoard(allTiles, SkeletonTile.class, board);

            // Ειδική διαχείριση για Solo Mode (8 Κατολισθήσεις στην αρχή)
            List<Tile> startingLandslides = new ArrayList<>();
            if (isSoloMode) {
                // Βρίσκουμε και αφαιρούμε 8 κατολισθήσεις από τη στίβα
                int found = 0;
                java.util.Iterator<Tile> iter = allTiles.iterator();
                while (iter.hasNext() && found < 8) {
                    Tile t = iter.next();
                    if (t instanceof LandslideTile) {
                        startingLandslides.add(t);
                        iter.remove();
                        found++;
                    }
                }
            }

            Bag bag = new Bag(allTiles);

            // 4. Δημιουργία Παικτών
            List<Player> players = new ArrayList<>();

            if (isSoloMode) {
                players.add(createPlayerWithCards("Player 1", PlayerColor.YELLOW));
            } else {
                // Κλασικό παιχνίδι 4 παικτών
                players.add(createPlayerWithCards("Player 1", PlayerColor.YELLOW));
                players.add(createPlayerWithCards("Player 2", PlayerColor.RED));
                players.add(createPlayerWithCards("Player 3", PlayerColor.BLUE));
                players.add(createPlayerWithCards("Player 4", PlayerColor.BLACK));
            }
            Player thief = isSoloMode ? new Player("Thief", PlayerColor.BLACK, new ArrayList<>(), new ArrayList<>()) : null;

            // 5. Δημιουργία Controller
            GameController controller = new GameController(board, bag, players, view);

            // Ρυθμίσεις Solo Mode στον Controller
            if (isSoloMode) {
                controller.setupSoloMode(thief, startingLandslides);
            }

            // 6. Σύνδεση και Έναρξη
            view.setController(controller);
            controller.startGame();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for the first instance of a specific tile type in the provided list,
     * removes it, and places it on the game board.
     * <p>
     * This helper method is used during the game setup to place one starting tile
     * of each category onto the board.
     * </p>
     *
     * @param tiles The source list of tiles (e.g., the bag) to search through.
     * @param type  The class representing the specific type of tile to find (e.g., {@code MosaicTile.class}).
     * @param board The game board where the tile will be placed if found.
     * @post The {@code tiles} list size decreases by 1 if a tile of the specified type is found.
     */
    private static void moveOneTileToBoard(List<Tile> tiles, Class<?> type, Board board) {
        for (int i = 0; i < tiles.size(); i++) {
            if (type.isInstance(tiles.get(i))) {
                board.placeTile(tiles.remove(i)); // Αφαίρεση από λίστα, τοποθέτηση στο Board
                return;
            }
        }
    }


    /**
     * Factory method that creates a new player and initializes their starting hand.
     * <p>
     * This method instantiates a {@link Player} with the specified name and color,
     * initializes an empty collection of tiles, and populates the player's hand
     * with one instance of each character card (Assistant, Archaeologist, Digger,
     * Professor, and Programmer).
     * </p>
     *
     * @param name  The display name of the player.
     * @param color The {@link PlayerColor} assigned to the player.
     * @return A new {@link Player} instance initialized with a full set of cards.
     */
    private static Player createPlayerWithCards(String name, PlayerColor color) {
        List<Card> cards = new ArrayList<>();
        Player player = new Player(name, color, new ArrayList<>(), cards);
        cards.add(new AssistantCard(player));
        cards.add(new ArchaeologistCard(player));
        cards.add(new DiggerCard(player));
        cards.add(new ProfessorCard(player));
        cards.add(new ProgrammerCard(player));
        return player;
    }


    /**
     * Generates the complete collection of tiles required for the game "Amphipolis".
     * <p>
     * This method creates a list containing all 135 tiles as specified in the game components:
     * <ul>
     * <li><b>27 Mosaics:</b> 9 Green, 9 Red, 9 Yellow.</li>
     * <li><b>24 Statues:</b> 12 Caryatids, 12 Sphinxes.</li>
     * <li><b>30 Amphoras:</b> 5 of each color (Blue, Brown, Red, Green, Yellow, Purple).</li>
     * <li><b>30 Skeletons:</b> 10 Big Top, 10 Big Bottom, 5 Small Top, 5 Small Bottom.</li>
     * <li><b>24 Landslides:</b> Used for the entrance track.</li>
     * </ul>
     * These tiles are typically passed to the {@link Bag} constructor to initialize the game.
     * </p>
     *
     * @return A list containing all the {@link Tile} objects for a new game session.
     */
    private static List<Tile> createAllTiles() {
        List<Tile> tiles = new ArrayList<>();

        // 27 Μωσαϊκά (9 από κάθε χρώμα)
        for (int i=0; i<9; i++) {
            tiles.add(new MosaicTile(AmphoraColor.GREEN));
            tiles.add(new MosaicTile(AmphoraColor.RED));
            tiles.add(new MosaicTile(AmphoraColor.YELLOW));
        }

        // 24 Αγάλματα (12 Καρυάτιδες, 12 Σφίγγες)
        for (int i=0; i<12; i++) {
            tiles.add(new StatueTile(StatueType.CARYATID));
            tiles.add(new StatueTile(StatueType.SPHINX));
        }

        // 30 Αμφορείς (5 από κάθε χρώμα - 6 χρώματα)
        // Μπλε, Καφέ, Κόκκινο, Πράσινο, Κίτρινο, Μωβ
        for (int i=0; i<5; i++) {
            tiles.add(new AmphoraTile(AmphoraColor.BLUE));
            tiles.add(new AmphoraTile(AmphoraColor.BROWN));
            tiles.add(new AmphoraTile(AmphoraColor.RED));
            tiles.add(new AmphoraTile(AmphoraColor.GREEN));
            tiles.add(new AmphoraTile(AmphoraColor.YELLOW));
            tiles.add(new AmphoraTile(AmphoraColor.PURPLE));
        }

        // 30 Σκελετοί (10 Μεγάλοι Πάνω, 10 Μεγάλοι Κάτω, 5 Μικροί Πάνω, 5 Μικροί Κάτω)
        for (int i=0; i<10; i++) {
            tiles.add(new SkeletonTile(true, true));
            tiles.add(new SkeletonTile(true, false));
        }
        for (int i=0; i<5; i++) {
            tiles.add(new SkeletonTile(false, true));
            tiles.add(new SkeletonTile(false, false));
        }

        // 24 Κατολισθήσεις
        for (int i=0; i<24; i++) {
            tiles.add(new LandslideTile());
        }

        return tiles;
    }
}