package amphipolis.controller;

import amphipolis.AreaType;
import amphipolis.model.Bag;
import amphipolis.model.Board;
import amphipolis.model.Card;
import amphipolis.model.Player;
import amphipolis.view.GameView;
import amphipolis.model.Tile;
import java.util.List;
import javax.swing.Timer;
import amphipolis.model.LandslideTile;
import java.util.ArrayList;
import amphipolis.model.GameData;
import java.io.*;
import javax.swing.JOptionPane;
import amphipolis.model.AssistantCard;
import amphipolis.model.ProfessorCard;
import amphipolis.model.DiggerCard;
import amphipolis.model.ArchaeologistCard;
import amphipolis.model.ProgrammerCard;
import java.util.Map;
import java.util.HashMap;
import amphipolis.StatueType;



/**
 * Controller coordinating model and view.
 * Invariant: players.size() >= 1, 0 <= currentPlayerIndex < players.size().
 */
public class GameController {

    private Board board;
    private Bag bag;
    private List<Player> players;
    private final GameView view;
    private int currentPlayerIndex;
    private AreaType lastPickedArea = null;


    private boolean bonusMoveActive = false;
    private int bonusTilesAllowed = 0;
    private AreaType restrictionArea = null;       // Περιοχή περιορισμού
    private boolean isInclusive = false;


    private transient Timer turnTimer;
    private int secondsLeft = 30;

    private boolean isSoloMode = false;
    private Player thief; // Ο αντίπαλος
    private List<Tile> startingLandslides;

    private Map<Player, AreaType> programmerPendingMoves = new HashMap<>();

    private Map<Player, AreaType> playerHistory = new HashMap<>();

    private Card activeCard = null;

    /**
     * Creates a game controller.
     * @param board non-null board
     * @param bag non-null bag
     * @param players non-null, non-empty list of players
     * @param view non-null view
     * @pre board != null && bag != null && players != null && !players.isEmpty() && view != null
     * @post getCurrentPlayer() == players.get(0)
     */
    public GameController(Board board, Bag bag, List<Player> players, GameView view) {
        this.board = board;
        this.bag = bag;
        this.players = players;
        this.view = view;
        this.currentPlayerIndex = 0;


        turnTimer = new Timer(1000, e -> {
            secondsLeft--;
            view.updateTimer(secondsLeft);

            if (secondsLeft <= 0) {
                // Ο χρόνος τελείωσε!
                turnTimer.stop();
                view.showMessage("Time's up for " + getCurrentPlayer().getName() + "!");
                nextPlayer(); // Αυτόματη αλλαγή σειράς
            }
        });
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }


    /**
     * Configures the game controller for Single Player (Solo) mode.
     * <p>
     * This method is called during the game setup phase if the user selects the 1-player option.
     * It activates the solo mode flag, assigns the automated opponent ("The Thief"), and
     * prepares the specific set of landslide tiles that must be placed on the board's entrance
     * before the game begins.
     * </p>
     *
     * @param thief              The {@link Player} object representing the automated opponent (The Thief).
     * @param startingLandslides A list of {@link Tile} objects (specifically Landslide tiles) to be placed
     * on the board's entrance area at the start of the game, as per solo rules.
     */
    public void setupSoloMode(Player thief, List<Tile> startingLandslides) {
        this.isSoloMode = true;
        this.thief = thief;
        this.startingLandslides = startingLandslides;
    }


    /**
     * Saves the current game state to a local file ("amphipolis_save.ser").
     * <p>
     * The method temporarily pauses the game timer during the save process
     * and resumes it once the operation is complete.
     * </p>
     */
    public void saveGame() {
        // Παύση χρόνου κατά την αποθήκευση
        if (turnTimer != null) turnTimer.stop();

        try {
            // Δημιουργία του αντικειμένου που θα αποθηκεύσουμε
            GameData data = new GameData(board, bag, players, currentPlayerIndex, isSoloMode, thief);

            // Αποθήκευση σε αρχείο "savegame.ser"
            FileOutputStream fileOut = new FileOutputStream("amphipolis_save.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.close();
            fileOut.close();

            view.showMessage("Game Saved Successfully!");
        } catch (IOException i) {
            i.printStackTrace();
            view.showMessage("Error saving game: " + i.getMessage());
        }

        // Συνέχιση χρόνου
        if (turnTimer != null) turnTimer.start();
    }


    /**
     * Loads a previously saved game state from the local file system.
     * <p>
     * This method attempts to deserialize a {@link GameData} object from "amphipolis_save.ser".
     * If successful, it restores the board, players, and game settings, refreshes the UI,
     * and restarts the turn timer. If loading fails, the current game continues.
     * </p>
     */
    public void loadGame() {
        // Παύση του τωρινού παιχνιδιού
        if (turnTimer != null) turnTimer.stop();

        GameData data = null;
        try {
            FileInputStream fileIn = new FileInputStream("amphipolis_save.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            data = (GameData) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            view.showMessage("No save file found or error loading!");
            if (turnTimer != null) turnTimer.start(); // Συνεχίζουμε το τωρινό αν αποτύχει
            return;
        }

        // Ανάκτηση δεδομένων
        this.board = data.board;
        this.bag = data.bag;
        this.players = data.players;
        this.currentPlayerIndex = data.currentPlayerIndex;
        this.isSoloMode = data.isSoloMode;
        this.thief = data.thief;

        // Ενημέρωση του View με τα παλιά δεδομένα
        view.updateBoard(this.board);
        view.showCurrentPlayer(getCurrentPlayer());

        // Επανεκκίνηση του Timer
        resetAndStartTimer();

        view.showMessage("Game Loaded Successfully!");
    }


    /**
     * Starts a new game: initializes board, updates view, selects first player.
     * @pre players.size() >= 1
     */
    public void startGame() {
        board.initialize(bag);

        // 2. Αν είναι Solo Mode, τοποθετούμε τα 8 πλακίδια κατολισθήσεων στην είσοδο
        if (isSoloMode && startingLandslides != null) {
            for (Tile t : startingLandslides) {
                board.placeTile(t); // Θα πάνε αυτόματα στην Landslide Area
            }
            view.showMessage("SOLO MODE: 8 Landslide tiles placed in entrance.");
        }

        view.updateBoard(board);
        resetAndStartTimer();
        view.showCurrentPlayer(getCurrentPlayer());
        view.showMessage("Game Started!");
    }

    /**
     * Resets the turn timer to the initial value (30 seconds) and restarts the countdown.
     */
    private void resetAndStartTimer() {
        if (turnTimer.isRunning()) {
            turnTimer.stop();
        }
        secondsLeft = 30;
        view.updateTimer(secondsLeft);
        turnTimer.start();
    }


    /**
     * Calculates and assigns score points for both statue types (Sphinxes and Caryatids)
     * based on the comparative scoring rules (majority/minority).
     */
    private void calculateStatuePoints() {
        assignPointsForStatueType(StatueType.SPHINX);
        assignPointsForStatueType(StatueType.CARYATID);
    }


    /**
     * Distributes points to players for a specific statue category based on quantity comparison.
     * <p>
     * Awards 6 points for the most statues, 0 for the fewest, and 3 for any amount in between.
     * </p>
     *
     * @param type The {@link StatueType} (Sphinx or Caryatid) to evaluate.
     */
    private void assignPointsForStatueType(StatueType type) {
        Map<Player, Integer> counts = new HashMap<>();
        List<Player> allParticipants = new ArrayList<>(players);
        if (isSoloMode && thief != null) allParticipants.add(thief);

        int maxVal = -1;
        int minVal = 1000;

        for (Player p : allParticipants) {
            int c = p.getStatueCount(type);
            counts.put(p, c);
            if (c > maxVal) maxVal = c;
            if (c < minVal) minVal = c;
        }

        if (maxVal == 0) return;

        for (Player p : allParticipants) {
            int c = counts.get(p);

            if (c == maxVal) {
                p.addStatuePoints(6);
            }
            else if (c == minVal) {
                p.addStatuePoints(0);
            }
            else {
                p.addStatuePoints(3);
            }
        }

    }

    /**
     * Finalizes the game session by stopping the timer, calculating statue points,
     * and displaying the final results (Winner or Solo Win/Loss) to the user.
     */
    public void handleGameOver() {
        turnTimer.stop();// Σταματάμε το ρολόι

        calculateStatuePoints();

        if (isSoloMode) {
            // Solo Mode: Σύγκριση με τον Κλέφτη
            int playerScore = players.get(0).calculateScore();
            int thiefScore = thief.calculateScore();

            String resultMsg = "GAME OVER!\n\n" +
                    "Your Score: " + playerScore + "\n" +
                    "Thief's Score: " + thiefScore + "\n\n";

            if (playerScore > thiefScore) {
                resultMsg += "YOU WIN! You defeated the Thief!";
            } else {
                resultMsg += "YOU LOSE! The Thief escaped with more treasures.";
            }

            view.showMessage(resultMsg);

            // Εμφάνιση λίστας για το τελικό παράθυρο
            List<Player> allStats = new ArrayList<>(players);
            allStats.add(thief);
            view.showGameOver(allStats);

        } else {
            // Multiplayer Mode
            Player winner = getWinner();
            view.showMessage("🎉 GAME OVER! The Winner is " + winner.getName() + " with " + winner.calculateScore() + " points!");
            view.showGameOver(players);
        }
    }

    /**
     * Handles the action where the current player draws tiles from the bag.
     * @pre !bag.isEmpty()
     */
    public void handleDrawTiles() {
        // Πρώτα τραβάει 4 πλακίδια
        for (int i = 0; i < 4; i++) {
            if (bag.isEmpty()) break;
            Tile drawnTile = bag.drawTile();

            // Έλεγχος Κατολίσθησης για Solo Mode
            if (isSoloMode && drawnTile instanceof LandslideTile) {
                board.placeTile(drawnTile);
                view.updateBoard(board);
                view.showMessage("Landslide! Thief attacks!");
                thiefStealsEverything();
                nextPlayer();
                return;
            }

            board.placeTile(drawnTile);

            // Έλεγχος Τέλους Παιχνιδιού (16 Κατολισθήσεις)
            if (drawnTile instanceof LandslideTile && board.isGameOver()) {
                view.updateBoard(board);
                view.showMessage("The Entrance is blocked (16 Landslides)! Game Over!");
                handleGameOver();
                return;
            }
        }
        view.updateBoard(board);
        view.showMessage("Tiles drawn. Pick up to 2 tiles from ONE area.");
    }

    /**
     * Executes the Thief's stealing action in Solo Mode.
     * <p>
     * Clears all tiles from the four main sorting areas (Mosaics, Statues, Amphoras, Skeletons)
     * and transfers them to the Thief's collection.
     * </p>
     */
    private void thiefStealsEverything() {
        // Περιοχές που κλέβει ο κλέφτης (Όλες εκτός από την είσοδο)
        AreaType[] stealAreas = {
                AreaType.MOSAIC_AREA,
                AreaType.STATUE_AREA,
                AreaType.AMPHORA_AREA,
                AreaType.SKELETON_AREA
        };

        int stolenCount = 0;

        for (AreaType area : stealAreas) {
            List<Tile> tiles = board.getTilesInArea(area);
            // Προσθέτουμε τα πλακίδια στον Κλέφτη
            while (!tiles.isEmpty()) {
                Tile t = tiles.remove(0);
                thief.addTile(t);
                stolenCount++;
            }
        }

        view.updateBoard(board);
        view.showMessage("The Thief stole " + stolenCount + " tiles!");
    }


    /**
     * Resets the timer to 30 seconds and starts the countdown for the current turn.
     */
    private void startTurnTimer() {
        if (turnTimer.isRunning()) {
            turnTimer.stop();
        }
        secondsLeft = 30; // Reset στα 30 δευτερόλεπτα
        view.updateTimer(secondsLeft); // Ενημέρωση UI άμεσα
        turnTimer.start();
    }

    /**
     * Handles taking tiles from a specific area.
     * @param area area chosen
     * @param count number of tiles to take (1..2)
     * @pre area != null && (count == 1 || count == 2)
     */
    public void handleTakeTilesFromArea(AreaType area, int count) {
        Player current = getCurrentPlayer();
        List<Tile> areaTiles = board.getTilesInArea(area);
        AreaType lastArea = playerHistory.get(current);

        if (activeCard instanceof ProgrammerCard) {
            programmerPendingMoves.put(current, area);
            finishCardUsage(activeCard);

            view.showMessage("Προγραμματιστής: Επιλογή καταγράφηκε! Θα πάρεις πλακίδια στον επόμενο γύρο.");
            nextPlayer();
            return;
        }

        if (areaTiles.isEmpty()) {
            view.showMessage("Η περιοχή είναι άδεια!");
            return;
        }

        if (activeCard instanceof AssistantCard) {
            Tile t = areaTiles.remove(0);
            current.addTile(t);

            playerHistory.put(current, area);

            finishCardUsage(activeCard);
            view.updateBoard(board);
            nextPlayer();
            return;
        }

        if (activeCard instanceof ArchaeologistCard) {
            if (area == lastArea) {
                view.showMessage("Αρχαιολόγος: Δεν μπορείς να πάρεις από την ίδια περιοχή (" + lastArea + ")!");
                return;
            }

            int toTake = Math.min(2, areaTiles.size());
            for (int i = 0; i < toTake; i++) {
                current.addTile(areaTiles.remove(0));
            }

            playerHistory.put(current, area);
            finishCardUsage(activeCard);
            view.updateBoard(board);
            nextPlayer();
            return;
        }

        if (activeCard instanceof DiggerCard) {
            if (area != lastArea) {
                view.showMessage("Εκσκαφέας: Πρέπει να πάρεις ΜΟΝΟ από την προηγούμενη περιοχή σου (" + lastArea + ")!");
                return;
            }

            int toTake = Math.min(2, areaTiles.size());
            for (int i = 0; i < toTake; i++) {
                current.addTile(areaTiles.remove(0));
            }

            finishCardUsage(activeCard);
            view.updateBoard(board);
            nextPlayer();
            return;
        }


        if (activeCard == null) {
            if (area == AreaType.LANDSLIDE_AREA) {
                view.showMessage("Δεν μπορείς να πάρεις από την Είσοδο (Landslide) χωρίς κάρτα!");
                return;
            }

            Tile t = areaTiles.remove(0);
            current.addTile(t);

            moveRemainingToLandslide(areaTiles);

            playerHistory.put(current, area);

            view.updateBoard(board);
            nextPlayer();
        }
    }


    /**
     * Handles using a card by the current player.
     * @param card non-null card owned by current player
     * @pre card != null && card.getOwner() == getCurrentPlayer() && !card.isUsed()
     */
    public void handleUseCard(Card card) {
        Player current = getCurrentPlayer();

        if (activeCard != null) {
            view.showMessage("Έχεις ήδη ενεργή κάρτα!");
            return;
        }
        if (card.isUsed()) {
            view.showMessage("Η κάρτα έχει ήδη χρησιμοποιηθεί!");
            return;
        }

        if (card instanceof ProfessorCard) {
            AreaType forbiddenArea = playerHistory.get(current);
            int collected = 0;

            for (AreaType area : AreaType.values()) {
                if (area == AreaType.LANDSLIDE_AREA || area == forbiddenArea) continue;

                List<Tile> tiles = board.getTilesInArea(area);
                if (!tiles.isEmpty()) {
                    current.addTile(tiles.remove(0));
                    collected++;
                }
            }

            finishCardUsage(card);
            view.updateBoard(board);
            view.showMessage("Καθηγητής: Μάζεψες " + collected + " πλακίδια από τις άλλες περιοχές!");
            nextPlayer(); // Τέλος γύρου
            return;
        }
        this.activeCard = card;
        view.showMessage("Ενεργή Κάρτα: " + card.getClass().getSimpleName() + ". Διάλεξε περιοχή!");

    }

    /**
     * Advances to the next player's turn.
     * @post currentPlayerIndex advanced circularly
     */
    public void nextPlayer() {
        if (isGameOver()) {
            turnTimer.stop();
            view.showGameOver(players);
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Player current = getCurrentPlayer();
        if (isSoloMode && current.getName().equalsIgnoreCase("Thief")) {
            turnTimer.stop();

            performThiefAction();

            nextPlayer();
            return;
        }

        if (programmerPendingMoves.containsKey(current)) {
            AreaType targetArea = programmerPendingMoves.get(current);
            List<Tile> tiles = board.getTilesInArea(targetArea);

            view.showMessage("🤖 Ο Προγραμματιστής εκτελείται! Τραβάει 2 πλακίδια από " + targetArea);

            int count = 0;
            while (!tiles.isEmpty() && count < 2) {
                current.addTile(tiles.remove(0));
                count++;
            }

            programmerPendingMoves.remove(current);
            view.updateBoard(board);

            playerHistory.put(current, targetArea);
        }

        startTurnTimer();

        view.showCurrentPlayer(getCurrentPlayer());
        view.showMessage("It is now " + getCurrentPlayer().getName() + "'s turn.");

    }

    /**
     * Finalizes the usage of the specified character card.
     * <p>
     * This helper method performs three key actions to complete a card's lifecycle:
     * 1. Marks the card object as "used" so it cannot be activated again during this game/turn.
     * 2. Resets the {@code activeCard} reference to {@code null}, signifying no card is currently active.
     * 3. Triggers a UI update on the info panel to visually disable the card's button.
     * </p>
     *
     * @param card The {@link Card} object that has just been successfully used.
     */
    private void finishCardUsage(Card card) {
        card.setUsed(true);
        activeCard = null;
        view.updateInfoPanel(getCurrentPlayer());
    }

    /**
     * Transfers all remaining tiles from a specific area to the Landslide (Entrance) area.
     * <p>
     * This method implements the core game mechanic where unselected tiles do not remain
     * in their original location but "fall" into the Landslide area. It moves the tile objects
     * from the source list to the Landslide list, clears the source list, and notifies the user via the view.
     * </p>
     *
     * @param sourceList The list of tiles remaining in the area the player just interacted with (e.g., the Mosaic area list).
     */
    private void moveRemainingToLandslide(List<Tile> sourceList) {
        if (!sourceList.isEmpty()) {
            List<Tile> landslide = board.getTilesInArea(AreaType.LANDSLIDE_AREA);
            landslide.addAll(sourceList);
            sourceList.clear();
            view.showMessage("Τα υπόλοιπα πλακίδια έπεσαν στην Είσοδο!");
        }
    }

    /**
     * Executes the automated turn logic for the "Thief" opponent (Solo Mode).
     * <p>
     * This method locates the Thief player instance and iterates through the four main collection areas
     * (Mosaic, Statue, Amphora, Skeleton), removing all available tiles to simulate theft.
     * Note that the Landslide area is explicitly excluded from this action.
     * <br>
     * It concludes by updating the visual board state and displaying a dialog box informing the user
     * of the total number of tiles stolen.
     * </p>
     */
    private void performThiefAction() {
        Player thief = null;
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase("Thief")) {
                thief = p;
                break;
            }
        }

        if (thief == null) return;

        int stolenCount = 0;

        // Λίστα με τις περιοχές που κλέβει (όλες εκτός από την LANDSLIDE)
        AreaType[] areasToSteal = {
                AreaType.MOSAIC_AREA,
                AreaType.STATUE_AREA,
                AreaType.AMPHORA_AREA,
                AreaType.SKELETON_AREA
        };

        for (AreaType area : areasToSteal) {
            // Παίρνουμε τη λίστα της περιοχής
            List<Tile> tiles = board.getTilesInArea(area);

            if (tiles != null && !tiles.isEmpty()) {
                stolenCount += tiles.size();
                tiles.clear(); // Αδειάζουμε τη λίστα
            }
        }

        // Ενημέρωση View
        view.updateBoard(board);

        javax.swing.JOptionPane.showMessageDialog(null,
                "The Thief strikes!\nHe stole " + stolenCount + " tiles!",
                "Thief Action",
                javax.swing.JOptionPane.WARNING_MESSAGE);
    }


    /**
     * Activates the Assistant card ability.
     * <p>
     * Allows the player to pick 1 additional tile from any area without restrictions.
     * </p>
     */
    public void applyAssistant() {
        this.bonusMoveActive = true;
        this.bonusTilesAllowed = 1;
        this.restrictionArea = null;
        view.showMessage("Assistant: Pick 1 tile from ANY area.");
    }

    /**
     * Activates the Archaeologist card ability.
     * <p>
     * Allows the player to pick up to 2 additional tiles from any area EXCEPT the one
     * they selected during their standard move.
     * </p>
     */
    public void applyArchaeologist() {
        this.bonusMoveActive = true;
        this.bonusTilesAllowed = 2;
        this.restrictionArea = lastPickedArea;
        this.isInclusive = false; // EXCLUSIVE (Όχι από αυτή)
        view.showMessage("Archaeologist: Pick 2 tiles EXCEPT from " + lastPickedArea);
    }

    /**
     * Activates the Digger card ability.
     * <p>
     * Allows the player to pick up to 2 additional tiles ONLY from the area
     * they selected during their standard move.
     * </p>
     */
    public void applyDigger() {
        this.bonusMoveActive = true;
        this.bonusTilesAllowed = 2;
        this.restrictionArea = lastPickedArea;
        this.isInclusive = true; // INCLUSIVE (Μόνο από αυτή)
        view.showMessage("Digger: Pick 2 tiles ONLY from " + lastPickedArea);
    }

    /**
     * Activates the Professor card ability.
     * <p>
     * Automatically collects one tile from every area EXCEPT the one selected
     * during the player's standard move.
     * </p>
     */
    public void applyProfessor() {
        AreaType[] allAreas = {AreaType.MOSAIC_AREA, AreaType.STATUE_AREA, AreaType.AMPHORA_AREA, AreaType.SKELETON_AREA};

        int count = 0;
        for (AreaType area : allAreas) {
            if (area == lastPickedArea) continue; // Skip την περιοχή που διάλεξε νωρίτερα

            List<Tile> tiles = board.getTilesInArea(area);
            if (!tiles.isEmpty()) {
                getCurrentPlayer().addTile(tiles.remove(0));
                count++;
            }
        }
        view.updateBoard(board);
        view.showMessage("Professor collected " + count + " tiles from other areas!");
    }

    /**
     * Activates the Programmer card ability.
     * <p>
     * Prompts the player to select an area now. At the start of their NEXT turn,
     * they will automatically draw 2 tiles from that selected area.
     * </p>
     */
    public void applyProgrammer() {
        // Εμφάνιση παραθύρου επιλογής στον χρήστη
        AreaType[] options = {AreaType.MOSAIC_AREA, AreaType.STATUE_AREA, AreaType.AMPHORA_AREA, AreaType.SKELETON_AREA};

        int choice = JOptionPane.showOptionDialog(null,
                "Choose an area for your NEXT turn:",
                "Programmer Ability",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice >= 0) {
            AreaType selected = options[choice];
            programmerPendingMoves.put(getCurrentPlayer(), selected);
            view.showMessage("Programmer set! You will draw from " + selected + " next turn.");
        }
    }

    /**
     * Checks if the game is over.
     * @return true if board.isGameOver()
     */
    public boolean isGameOver() {
        return board.isGameOver();
    }

    /**
     * Calculates and returns the winner.
     * @return winner player, or null if game not finished
     */
    public Player getWinner() {
        if (!isGameOver()) return null;

        Player winner = null;
        int maxScore = -1;

        for (Player p : players) {
            int score = p.calculateScore();
            if (score > maxScore) {
                maxScore = score;
                winner = p;
            }
        }
        return winner;
    }

}

