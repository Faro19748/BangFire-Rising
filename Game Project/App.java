import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BangFire");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 1050);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new MainMenu(frame));
            frame.setVisible(true);
        });
    }

    public static void startGame() {
        int boardWidth = 800;
        int boardHeight = 1050;

        JFrame frame = new JFrame("BangFire Rising");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Color.BLACK);
        BangFire bangFire = new BangFire();
        bangFire.setPreferredSize(new Dimension(boardWidth, boardHeight));
        container.add(bangFire);
        frame.setContentPane(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        bangFire.requestFocus();
    }
}
