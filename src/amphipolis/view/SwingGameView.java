package amphipolis.view;

import amphipolis.AreaType;
import amphipolis.controller.GameController;
import amphipolis.model.*;
import amphipolis.model.Tile;
import amphipolis.StatueType;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;

public class SwingGameView extends JFrame implements GameView {

    private GameController controller;
    private JLayeredPane boardPane;
    private JPanel infoPanel;
    private JPanel cardsPanel;
    private JPanel playerStashPanel;
    private JLabel lblPlayerName;
    private JTextArea msgArea;
    private JLabel backgroundLabel;
    private JLabel lblTimer;
    private Clip currentMusicClip;


    private static final int BOARD_WIDTH = 900;
    private static final int HEIGHT = 700;

    public SwingGameView() {
        setTitle("Amphipolis - The Game");
        setSize(BOARD_WIDTH + 350, HEIGHT); // +350 pixels πλάτος για το δεξί πάνελ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);// Κλειδώνουμε το μέγεθος για να μη χαλάνε οι συντεταγμένες

        initMenuBar();

        // 1. Αριστερό μέρος: Ταμπλό
        initBoardPane();

        // 2. Δεξί μέρος: Πληροφορίες
        initInfoPanel();

        setLocationRelativeTo(null); // Κεντράρισμα στην οθόνη
        setVisible(true);
    }

    /**
     * Initializes the main menu bar containing the "Game Option" menu.
     * <p>
     * Sets up menu items for Saving, Loading, and Exiting the application,
     * connecting them to the respective controller actions.
     * </p>
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game Option"); // Όπως ζητάει η εκφώνηση

        // Επιλογή SAVE
        JMenuItem saveItem = new JMenuItem("Save Game");
        saveItem.addActionListener(e -> {
            if (controller != null) controller.saveGame();
        });

        // Επιλογή LOAD
        JMenuItem loadItem = new JMenuItem("Load Game");
        loadItem.addActionListener(e -> {
            if (controller != null) controller.loadGame();
        });

        // Επιλογή EXIT
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Initializes the main graphical board area.
     * <p>
     * Sets up the {@link JLayeredPane}, loads the background image, scales it to fit
     * the defined window dimensions, and places it at the lowest layer (Layer 0).
     * </p>
     */
    private void initBoardPane() {
        boardPane = new JLayeredPane();
        boardPane.setPreferredSize(new Dimension(BOARD_WIDTH, HEIGHT));
        boardPane.setLayout(null);

        // Φόρτωση Background
        ImageIcon bgIcon = loadIcon("background.png");

        if (bgIcon != null) {
            Image img = bgIcon.getImage().getScaledInstance(BOARD_WIDTH, HEIGHT, Image.SCALE_SMOOTH);
            backgroundLabel = new JLabel(new ImageIcon(img));
            backgroundLabel.setBounds(0, 0, BOARD_WIDTH, HEIGHT);
        } else {
            // Fallback αν δεν βρεθεί η εικόνα
            backgroundLabel = new JLabel("Background not found");
            backgroundLabel.setBackground(new Color(210, 180, 140));
            backgroundLabel.setOpaque(true);
            backgroundLabel.setBounds(0, 0, BOARD_WIDTH, HEIGHT);
            backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        boardPane.add(backgroundLabel, Integer.valueOf(0)); // Layer 0 (Πίσω)
        add(boardPane, BorderLayout.CENTER);
    }

    /**
     * Initializes the side information panel containing game controls and status displays.
     * <p>
     * Sets up the layout and adds components for the current player's name, character cards,
     * action buttons (Draw, End Turn), tile collection (stash), game message log, and the turn timer.
     * </p>
     */
    public void initInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(350, HEIGHT));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(new Color(255, 250, 205)); // Lemon Chiffon χρώμα (πιο φωτεινό)

        // Όνομα Παίκτη
        lblPlayerName = new JLabel("Player: -");
        lblPlayerName.setFont(new Font("Serif", Font.BOLD, 28));
        lblPlayerName.setForeground(new Color(204, 153, 0)); // Χρυσό χρώμα
        lblPlayerName.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(lblPlayerName);
        infoPanel.add(Box.createVerticalStrut(20));

        // Κάρτες
        JLabel lblCards = new JLabel("Character Cards");
        lblCards.setFont(new Font("Arial", Font.BOLD, 14));
        lblCards.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(lblCards);

        cardsPanel = new JPanel(new GridLayout(2, 2, 8, 8)); // Λίγο μεγαλύτερο κενό
        cardsPanel.setMaximumSize(new Dimension(300, 280)); // Περισσότερος χώρος για κάρτες
        cardsPanel.setBackground(new Color(255, 250, 205));
        infoPanel.add(cardsPanel);

        infoPanel.add(Box.createVerticalStrut(30));

        // Κουμπιά
        JButton btnDraw = new JButton("Draw Tiles");
        btnDraw.setFont(new Font("Arial", Font.BOLD, 18));
        btnDraw.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDraw.addActionListener(e -> {
            if (controller != null) controller.handleDrawTiles();
        });
        infoPanel.add(btnDraw);

        infoPanel.add(Box.createVerticalStrut(15));

        JButton btnEndTurn = new JButton("End Turn");
        btnEndTurn.setFont(new Font("Arial", Font.BOLD, 18));
        btnEndTurn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEndTurn.addActionListener(e -> {
            if (controller != null) controller.nextPlayer();
        });
        infoPanel.add(btnEndTurn);

        // Stash
        infoPanel.add(Box.createVerticalStrut(30));
        JLabel lblStash = new JLabel("My Collection:");
        lblStash.setFont(new Font("Arial", Font.BOLD, 14));
        lblStash.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(lblStash);

        playerStashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerStashPanel.setPreferredSize(new Dimension(300, 200));
        playerStashPanel.setBackground(Color.WHITE);
        playerStashPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        infoPanel.add(playerStashPanel);

        // Messages
        msgArea = new JTextArea(4, 20);
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoPanel.add(new JScrollPane(msgArea));

        add(infoPanel, BorderLayout.EAST);


        infoPanel.add(lblPlayerName);
        infoPanel.add(Box.createVerticalStrut(10));

        //ΠΡΟΣΘΗΚΗ TIMER LABEL
        lblTimer = new JLabel("Time: 30");
        lblTimer.setFont(new Font("Arial", Font.BOLD, 20));
        lblTimer.setForeground(Color.BLACK);
        lblTimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(lblTimer);

        infoPanel.add(Box.createVerticalStrut(20));

        Player currentPlayer = null;
        if (controller != null) {
            currentPlayer = controller.getCurrentPlayer();
        }

        if (currentPlayer != null) {
            for (Card card : currentPlayer.getCards()) {
                JButton cardBtn = new JButton();
                cardBtn.setIcon(getIconForCard(card));

                // Αν η κάρτα έχει ήδη χρησιμοποιηθεί, την κάνουμε disable
                if (card.isUsed()) {
                    cardBtn.setEnabled(false);
                }

                cardBtn.addActionListener(e -> {
                    // Όταν πατηθεί, καλούμε τον Controller
                    controller.handleUseCard(card);
                });

                cardsPanel.add(cardBtn);
            }
        }
    }

    @Override
    public void updateInfoPanel(Player player) {
        if (player == null) return;

        // 1. Ενημέρωση Ονόματος
        lblPlayerName.setText("Player: " + player.getName());

        // 2. Ενημέρωση Καρτών (Σβήνουμε τα παλιά κουμπιά και βάζουμε τα σωστά)
        cardsPanel.removeAll();

        for (Card c : player.getCards()) {
            JButton cardBtn = new JButton();

            cardBtn.setIcon(getIconForCard(c));

            if (c.isUsed()) {
                cardBtn.setEnabled(false);
            } else {
                cardBtn.setEnabled(true);
                cardBtn.addActionListener(e -> {
                    if (controller != null) {
                        controller.handleUseCard(c);
                    }
                });
            }

            cardsPanel.add(cardBtn);
        }

        // 3. Ενημέρωση γραφικών για να φανούν οι αλλαγές
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /**
     * Helper method to load an image resource.
     * <p>
     * Attempts to find the image first in the classpath (for production/JAR),
     * and falls back to the local file system (for development) if not found.
     * </p>
     *
     * @param filename The name of the image file (e.g., "card_assistant.png").
     * @return An {@link ImageIcon} if found, or {@code null} if the file is missing.
     */
    private ImageIcon loadIcon(String filename) {
        // Δοκιμή φόρτωσης εικόνας
        URL url = getClass().getResource("/images/" + filename);
        if (url != null) return new ImageIcon(url);

        String path = "src/resources/images/" + filename;
        if (new File(path).exists()) {
            return new ImageIcon(path);
        }

        System.err.println("Could not load image: " + filename);
        return null;
    }

    /**
     * Resolves and generates the appropriate visual icon for a specific {@link Tile}.
     * <p>
     * Determines the correct asset filename based on the tile's runtime type (Mosaic, Amphora, Statue, etc.)
     * and its attributes (color, size, part). The image is loaded and scaled to 55x55 pixels
     * to fit the board layout.
     * </p>
     *
     * @param t The tile object to visualize.
     * @return A scaled {@link ImageIcon}, or {@code null} if the image file cannot be found.
     */
    private ImageIcon getIconForTile(Tile t) {
        String filename = "tile_back.png";

        if (t instanceof MosaicTile) {
            MosaicTile m = (MosaicTile) t;
            String color = m.getColor().toString().toLowerCase();
            filename = "mosaic_" + color + ".png";
        }
        else if (t instanceof AmphoraTile) {
            AmphoraTile a = (AmphoraTile) t;
            String color = a.getColor().toString().toLowerCase();
            filename = "amphora_" + color + ".png";
        }
        else if (t instanceof StatueTile) {
            StatueTile s = (StatueTile) t;
            StatueType typeEnum = s.getStatueType();
            String type = typeEnum.toString().toLowerCase();

            if (type.contains("caryatid")) {
                filename = "caryatid.png";
            } else if (type.contains("sphinx")) {
                filename = "sphinx.png";
            } else {
                filename = type + ".png";
            }
        }
        else if (t instanceof SkeletonTile) {
            SkeletonTile s = (SkeletonTile) t;
            String size = s.isLargeSkeleton() ? "big" : "small";
            String part = s.isUpperPart() ? "top" : "bottom";
            filename = "skeleton_" + size + "_" + part + ".png";
        }
        else if (t instanceof LandslideTile) {
            filename = "landslide.png";
        }

        ImageIcon icon = loadIcon(filename);
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("MISSING IMAGE: " + filename + " for tile type: " + t.getClass().getSimpleName());
        }
        return null;
    }

    /**
     * Retrieves and scales the specific image asset for a given character card.
     * <p>
     * Derives the filename from the card's class name (e.g., maps "Programmer" to "coder")
     * and resizes the image to 80x110 pixels to fit the info panel.
     * </p>
     *
     * @param c The {@link Card} object to visualize.
     * @return A scaled {@link ImageIcon}, or {@code null} if the asset is missing.
     */
    private ImageIcon getIconForCard(Card c) {
        String name = c.getClass().getSimpleName().replace("Card", "").toLowerCase();
        if (name.contains("programmer")) name = "coder";

        ImageIcon icon = loadIcon(name + ".png");
        if (icon == null) icon = loadIcon(name + ".PNG");

        if (icon != null) {
            // Μέγεθος κάρτας στο δεξί πάνελ
            Image img = icon.getImage().getScaledInstance(80, 110, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    /**
     * Manages the background audio transition for the current turn.
     * <p>
     * Stops any currently playing music to prevent overlapping, resolves the specific audio file
     * based on the player's name (e.g., "Player 1" maps to "Player1.wav"), and plays it in a continuous loop.
     * </p>
     *
     * @param playerName The name of the player, used to determine which audio file to load.
     */
    private void playTurnMusic(String playerName) {
        // 1. Σταματάμε την προηγούμενη μουσική αν υπάρχει
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
            currentMusicClip.close();
        }

        try {

            String filename = playerName.replace(" ", "") + ".wav";

            File soundFile = new File("src/resources/sounds/" + filename);

            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                currentMusicClip = AudioSystem.getClip();
                currentMusicClip.open(audioIn);

                // 2. Ξεκινάμε τη μουσική σε λούπα (για όση ώρα κρατάει ο γύρος)
                currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                currentMusicClip.start();
            } else {
                System.err.println("Sound file not found: " + filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBoard(Board board) {
        Component[] comps = boardPane.getComponentsInLayer(1);
        for (Component c : comps) {
            boardPane.remove(c);
        }

        drawArea(board.getTilesInArea(AreaType.MOSAIC_AREA), 90, 80, AreaType.MOSAIC_AREA);

        drawArea(board.getTilesInArea(AreaType.STATUE_AREA), 650, 80, AreaType.STATUE_AREA);

        drawEntranceArea(board.getTilesInArea(AreaType.LANDSLIDE_AREA), 360, 310);

        drawArea(board.getTilesInArea(AreaType.AMPHORA_AREA), 90, 480, AreaType.AMPHORA_AREA);

        drawArea(board.getTilesInArea(AreaType.SKELETON_AREA), 650, 480, AreaType.SKELETON_AREA);

        boardPane.repaint();
    }

    /**
     * Visually renders a list of tiles onto the game board at the specified coordinates.
     * <p>
     * Creates clickable buttons for each tile and arranges them in a staggered grid layout
     * (shifting right, wrapping to a new row every 4 tiles) to ensure all tiles in the stack remain visible.
     * </p>
     *
     * @param tiles The list of {@link Tile} objects to display.
     * @param x     The starting X coordinate on the board.
     * @param y     The starting Y coordinate on the board.
     * @param area  The {@link AreaType} these tiles belong to (used for click handling).
     */
    private void drawArea(List<Tile> tiles, int x, int y, AreaType area) {
        int offsetX = 0;
        int offsetY = 0;
        int count = 0;

        for (Tile t : tiles) {
            JButton tileBtn = createTileButton(t, area);

            // Τοποθέτηση: τα βάζουμε λίγο κλιμακωτά για να φαίνονται
            tileBtn.setBounds(x + offsetX, y + offsetY, 55, 55);
            boardPane.add(tileBtn, Integer.valueOf(1));

            offsetX += 60; // Μετατόπιση δεξιά
            // Αν μαζευτούν πολλά, αλλάζουμε σειρά για να μην βγουν έξω
            if (++count % 4 == 0) {
                offsetX = 0;
                offsetY += 20;
            }
        }
    }

    /**
     * Visually renders the tiles located in the Entrance (Landslide) area.
     * <p>
     * Unlike other areas, these tiles are arranged in a structured 4-column grid layout
     * (wrapping to a new row every 4 tiles) to neatly display up to 16 tiles.
     * </p>
     *
     * @param tiles  The list of {@link Tile} objects in the entrance.
     * @param startX The starting X coordinate.
     * @param startY The starting Y coordinate.
     */
    private void drawEntranceArea(List<Tile> tiles, int startX, int startY) {
        int col = 0;
        int row = 0;

        for (Tile t : tiles) {
            JButton tileBtn = createTileButton(t, AreaType.LANDSLIDE_AREA);

            // Τοποθέτηση σε πλέγμα 4x4 (γιατί χωράει 16 πλακίδια max)
            int x = startX + (col * 50);
            int y = startY + (row * 50);

            tileBtn.setBounds(x, y, 50, 50);
            boardPane.add(tileBtn, Integer.valueOf(1));

            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Creates a clickable button component representing a specific game tile.
     * <p>
     * Configures the visual appearance (icon or text fallback) and attaches an action listener
     * that triggers the controller to handle the tile selection from the given area when clicked.
     * </p>
     *
     * @param t    The {@link Tile} object to visualize.
     * @param area The {@link AreaType} where this tile is currently located.
     * @return The configured {@link JButton} ready for display.
     */
    private JButton createTileButton(Tile t, AreaType area) {
        JButton tileBtn = new JButton();
        ImageIcon icon = getIconForTile(t);
        if (icon != null) {
            tileBtn.setIcon(icon);
            tileBtn.setContentAreaFilled(false);
            tileBtn.setBorderPainted(false);
        } else {
            tileBtn.setText(t.getClass().getSimpleName().substring(0,2));
            tileBtn.setBackground(Color.LIGHT_GRAY);
        }

        tileBtn.addActionListener(e -> {
            if (controller != null) controller.handleTakeTilesFromArea(area, 1);
        });
        return tileBtn;
    }

    @Override
    public void showCurrentPlayer(Player player) {
        lblPlayerName.setText("Player: " + player.getName());

        // Χρώματα
        String cStr = player.getColor().toString();
        Color c = Color.BLACK;
        if (cStr.contains("YELLOW")) c = new Color(218, 165, 32); // Goldenrod
        else if (cStr.contains("RED")) c = new Color(178, 34, 34); // Firebrick
        else if (cStr.contains("BLUE")) c = new Color(25, 25, 112); // MidnightBlue
        lblPlayerName.setForeground(c);

        // Κάρτες
        cardsPanel.removeAll();
        for (Card card : player.getCards()) {
            JButton cardBtn = new JButton();
            ImageIcon cardIcon = getIconForCard(card);

            if (cardIcon != null) cardBtn.setIcon(cardIcon);
            else cardBtn.setText(card.getClass().getSimpleName().replace("Card", ""));

            if (card.isUsed()) {
                cardBtn.setEnabled(false);
            } else {
                cardBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                cardBtn.addActionListener(e -> {
                    if (controller != null) controller.handleUseCard(card);
                });
            }
            cardsPanel.add(cardBtn);
        }

        // Stash
        playerStashPanel.removeAll();
        for (Tile t : player.getCollectedTiles()) {
            JLabel tLbl = new JLabel();
            ImageIcon icon = getIconForTile(t);
            if (icon != null) {
                // Πολύ μικρό εικονίδιο για τη συλλογή
                Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                tLbl.setIcon(new ImageIcon(img));
            } else {
                tLbl.setText("T");
                tLbl.setOpaque(true);
                tLbl.setBackground(Color.GRAY);
            }
            playerStashPanel.add(tLbl);
        }

        infoPanel.revalidate();
        infoPanel.repaint();

        playTurnMusic(player.getName());

        infoPanel.revalidate();
        infoPanel.repaint();
    }


    @Override
    public void showMessage(String message) {
        msgArea.append(message + "\n");
        msgArea.setCaretPosition(msgArea.getDocument().getLength());
    }

    @Override
    public void showGameOver(List<Player> players) {
        if (currentMusicClip != null) {
            currentMusicClip.stop();
            currentMusicClip.close();
        }

        StringBuilder sb = new StringBuilder("GAME OVER!\n\nFINAL SCORES:\n------------------\n");
        players.sort((p1, p2) -> p2.calculateScore() - p1.calculateScore());

        for (Player p : players) {
            sb.append(String.format("%-10s : %d points\n", p.getName(), p.calculateScore()));
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Game Finished", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    @Override
    public void updateTimer(int seconds) {
        lblTimer.setText("Time: " + seconds);

        if (seconds <= 5) {
            lblTimer.setForeground(Color.RED);
        } else {
            lblTimer.setForeground(Color.BLACK);
        }
    }
}