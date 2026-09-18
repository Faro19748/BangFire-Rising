import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ScoreManager {
    public static final String PLAYER_DATA_FILE = "players.txt";

    private String playerName = "";
    private int highScore = 0;
    private int cash = 0;

    public ScoreManager() {
    }

    public ScoreManager(String playerName) {
        this.playerName = playerName;
        loadHighScore();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        loadHighScore();
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public void addCash(int amount) {
        this.cash += amount;
    }

    public void loadHighScore() {
        if (playerName == null || playerName.trim().isEmpty()) {
            return;
        }
        File file = new File(PLAYER_DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(playerName + ":")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        highScore = Integer.parseInt(parts[1]);
                    }
                    break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public boolean promptPlayerName(Component parent) {
        String input = null;
        try {
            while (input == null || input.trim().isEmpty()) {
                input = JOptionPane.showInputDialog(null, "Enter your name:", "Welcome to BangFire", JOptionPane.PLAIN_MESSAGE);
                if (input == null) {
                    Window window = SwingUtilities.getWindowAncestor(parent);
                    if (window != null) {
                        window.dispose();
                    }
                    MainMenu.main(null);
                    return false;
                } else if (input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Name cannot be empty.");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty.");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        playerName = input.trim();
        loadHighScore();

        JOptionPane.showMessageDialog(null, "Welcome, " + playerName + "! Highest score: " + highScore);

        File file = new File(PLAYER_DATA_FILE);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(playerName + ":0");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void updatePlayerHighScore(String name, int newScore) {
        File inputFile = new File(PLAYER_DATA_FILE);
        File tempFile = new File("players_temp.txt");
        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(name + ":")) {
                    writer.write(name + ":" + newScore);
                    writer.newLine();
                    found = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }

            if (!found) {
                writer.write(name + ":" + newScore);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.err.println("Failed to update score file.");
        }
    }

    public List<String> getTopScores(int n) {
        List<String> topScores = new ArrayList<>();
        File file = new File(PLAYER_DATA_FILE);
        if (!file.exists()) {
            return topScores;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(PLAYER_DATA_FILE));
            List<PlayerScore> playerScores = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    playerScores.add(new PlayerScore(name, score));
                }
            }

            // Sort descending
            playerScores.sort((a, b) -> Integer.compare(b.score, a.score));

            for (int i = 0; i < Math.min(n, playerScores.size()); i++) {
                PlayerScore ps = playerScores.get(i);
                topScores.add(ps.name + ": " + ps.score);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return topScores;
    }
}
