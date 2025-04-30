package MicrosofRewardEarnerSystem.MicrosoftRewardEarnerSystem;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class MacroAutomation extends JFrame {
    private JButton startButton, pauseButton, killButton;
    private JLabel statusLabel;
    private JLabel infoLabel;
    private JProgressBar progressBar;
    private volatile boolean paused = false;
    private MacroWorker worker;

    // Total iterations for the macro routine.
    private static final int TOTAL_ITERATIONS = 30;

    // Define the minimum and maximum number of words in a generated phrase.
    private static final int MIN_WORDS = 5;
    private static final int MAX_WORDS = 8;

    // A pool of 90 words.
    private final String[] wordPool = {
            "apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honeydew", "kiwi", "lemon",
            "mango", "nectarine", "orange", "papaya", "quince", "raspberry", "strawberry", "tangerine", "ugli",
            "vanilla",
            "watermelon", "xigua", "yam", "zucchini", "apricot", "blackberry", "cantaloupe", "dragonfruit", "eggfruit",
            "feijoa",
            "guava", "hackberry", "imbe", "jackfruit", "kumquat", "lime", "mulberry", "nutmeg", "olive", "persimmon",
            "quararibea", "rambutan", "soursop", "tamarind", "ugni", "voavanga", "wolfberry", "xylocarp", "yuzu",
            "ziziphus",
            "sun", "moon", "star", "comet", "galaxy", "planet", "sky", "ocean", "river", "mountain",
            "forest", "desert", "island", "valley", "meadow", "rain", "cloud", "storm", "wind", "fire",
            "ice", "shadow", "light", "flame", "dream", "echo", "whisper", "silence", "memory", "time"
    };

    public MacroAutomation() {
        // Make the frame undecorated and non-resizable.
        setUndecorated(true);
        setResizable(false);
        // Set frame size.
        setSize(400, 200);

        // Create a container panel with a plain opaque background.
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(true);
        container.setBackground(Color.DARK_GRAY);
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and style the UI components.
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        killButton = new JButton("Kill");

        // Initially only the Start button is enabled.
        pauseButton.setEnabled(false);
        killButton.setEnabled(false);

        statusLabel = new JLabel("Status: Idle");
        statusLabel.setForeground(Color.WHITE);
        infoLabel = new JLabel("Repetition Chance: Not calculated yet");
        infoLabel.setForeground(Color.WHITE);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(50, 205, 50)); // vibrant green
        progressBar.setBackground(Color.GRAY);

        // Create a panel for buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(killButton);

        // Create a lower panel for info and status.
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.setOpaque(false);
        lowerPanel.add(infoLabel, BorderLayout.NORTH);
        lowerPanel.add(statusLabel, BorderLayout.SOUTH);

        // Assemble the container.
        container.add(buttonPanel, BorderLayout.NORTH);
        container.add(progressBar, BorderLayout.CENTER);
        container.add(lowerPanel, BorderLayout.SOUTH);

        // Add a simple white border for a neat outline.
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);

        // Button action listeners.
        startButton.addActionListener(e -> startMacro());
        pauseButton.addActionListener(e -> togglePause());
        killButton.addActionListener(e -> killMacro());
    }

    /**
     * Generates a random phrase with a word count between MIN_WORDS and MAX_WORDS.
     */
    private String generateRandomPhrase(Random random) {
        int wordsCount = MIN_WORDS + random.nextInt(MAX_WORDS - MIN_WORDS + 1);
        StringBuilder phrase = new StringBuilder();
        for (int i = 0; i < wordsCount; i++) {
            String word = wordPool[random.nextInt(wordPool.length)];
            phrase.append(word);
            if (i < wordsCount - 1) {
                phrase.append(" ");
            }
        }
        return phrase.toString();
    }

    /**
     * Calculates the worst-case repetition probability using MIN_WORDS.
     * Possibility space = (wordPool.length)^(MIN_WORDS).
     */
    private double calculateRepetitionProbabilityWorstCase(int iterations) {
        double N = Math.pow(wordPool.length, MIN_WORDS);
        double pUnique = 1.0;
        for (int i = 0; i < iterations; i++) {
            pUnique *= (N - i) / N;
        }
        return 1.0 - pUnique;
    }

    /**
     * Initiates the macro:o
     * - Calculates and displays the repetition probability.
     * - Sets the window always on top.
     * - Starts the background worker.
     */
    private void startMacro() {
        double repeatProb = calculateRepetitionProbabilityWorstCase(TOTAL_ITERATIONS);
        infoLabel.setText(String.format("Chance of repetition (worst-case, %d-word phrases): %.8f%%", MIN_WORDS,
                repeatProb * 100));

        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        killButton.setEnabled(true);

        // Set window always on top while running.
        this.setAlwaysOnTop(true);

        worker = new MacroWorker();
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });
        worker.execute();
    }

    // Toggles pause/resume.
    private void togglePause() {
        paused = !paused;
        if (paused) {
            pauseButton.setText("Resume");
            statusLabel.setText("Status: Paused");
        } else {
            pauseButton.setText("Pause");
            statusLabel.setText("Status: Running");
        }
    }

    // Immediately stops the macro and exits.
    private void killMacro() {
        if (worker != null) {
            worker.cancel(true);
        }
        setAlwaysOnTop(false);
        System.exit(0);
    }

    /**
     * The MacroWorker uses SwingWorker to run the automation routine in the
     * background.
     * It launches Microsoft Edge and, for each iteration:
     * - Opens a new tab (Ctrl+T),
     * - Generates a random phrase,
     * - Pastes the phrase (using the clipboard),
     * - Presses Enter,
     * - Waits a random time (3–7 seconds),
     * - Closes the tab (Ctrl+W),
     * - And updates progress.
     */
    private class MacroWorker extends SwingWorker<Void, String> {
        @Override
        protected Void doInBackground() throws Exception {
            publish("Opening Microsoft Edge...");
            try {
                // Launch Edge on Arch Linux (ensure 'microsoft-edge-stable' is in your PATH).
                Runtime.getRuntime().exec("microsoft-edge-stable");
            } catch (Exception e) {
                publish("Error opening Edge: " + e.getMessage());
                return null;
            }
            Thread.sleep(5000);

            Robot robot = new Robot();
            robot.setAutoDelay(100);
            Random random = new Random();

            for (int i = 1; i <= TOTAL_ITERATIONS && !isCancelled(); i++) {
                // Check for pause.
                while (paused && !isCancelled()) {
                    Thread.sleep(200);
                }

                publish("Iteration " + i + ": Opening new tab...");
                // Simulate Ctrl+T.
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_T);
                robot.keyRelease(KeyEvent.VK_T);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                Thread.sleep(300);

                // Generate and paste a random phrase.
                String randomPhrase = generateRandomPhrase(random);
                publish("Iteration " + i + ": Pasting '" + randomPhrase + "'...");
                pasteString(robot, randomPhrase);

                // Simulate Enter key.
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                Thread.sleep(300);

                // Wait for a random delay (3–7 seconds).
                int waitTime = 3000 + random.nextInt(4000);
                publish("Iteration " + i + ": Waiting " + (waitTime / 1000) + " seconds...");
                Thread.sleep(waitTime);

                publish("Iteration " + i + ": Closing tab...");
                // Simulate Ctrl+W.
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_W);
                robot.keyRelease(KeyEvent.VK_W);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                Thread.sleep(300);

                // Update progress.
                setProgress(i * 100 / TOTAL_ITERATIONS);
            }
            publish("Macro complete.");
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            if (!chunks.isEmpty()) {
                String latest = chunks.get(chunks.size() - 1);
                statusLabel.setText("Status: " + latest);
            }
        }

        @Override
        protected void done() {
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            killButton.setEnabled(false);
            setAlwaysOnTop(false);
        }
    }

    private void pasteString(Robot robot, String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

        // Simulate Ctrl+V.
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(100);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MacroAutomation frame = new MacroAutomation();
            Point mousePoint = MouseInfo.getPointerInfo().getLocation();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int frameWidth = frame.getWidth();
            int frameHeight = frame.getHeight();
            int x = mousePoint.x;
            int y = mousePoint.y;
            if (x + frameWidth > screenSize.width) {
                x = screenSize.width - frameWidth;
            }
            if (y + frameHeight > screenSize.height) {
                y = screenSize.height - frameHeight;
            }
            frame.setLocation(x, y);
            frame.setVisible(true);
        });
    }
}
