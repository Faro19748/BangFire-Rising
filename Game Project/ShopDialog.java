import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class ShopDialog {
    private static final Color SHOP_PANEL_COLOR = new Color(0xFF, 0xE9, 0xAD);
    private static final Color TEXT_BROWN = new Color(0x7A, 0x4A, 0x21);
    private static final Color BUTTON_BROWN = new Color(0x9A, 0x62, 0x2E);
    private static final Color UPGRADE_BUTTON_HOVER_COLOR = new Color(0xB7, 0x7A, 0x3E);
    private static final Color UPGRADE_BUTTON_PRESSED_COLOR = new Color(0x6A, 0x3D, 0x18);

    private boolean isUpgradeUIOpen = false;
    private int upgradeLevel = 0;
    private final int maxUpgradeLevel = 5;
    private final int[] upgradeCosts = {50, 100, 150, 200, 250};

    public boolean isOpen() {
        return isUpgradeUIOpen;
    }

    public void setOpen(boolean open) {
        this.isUpgradeUIOpen = open;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(int level) {
        this.upgradeLevel = level;
    }

    public int getMaxUpgradeLevel() {
        return maxUpgradeLevel;
    }

    public void show(BangFire game) {
        if (isUpgradeUIOpen) return;
        isUpgradeUIOpen = true;

        JDialog upgradeFrame = new JDialog((Frame) null, "Shop - Upgrade", true);
        upgradeFrame.setUndecorated(true);
        upgradeFrame.setLayout(null);
        int width = 500;
        int height = 500;
        upgradeFrame.setSize(width, height);
        upgradeFrame.setLocationRelativeTo(null);
        upgradeFrame.getContentPane().setBackground(SHOP_PANEL_COLOR);

        upgradeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isUpgradeUIOpen = false;
                game.requestFocus();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                isUpgradeUIOpen = false;
                game.requestFocus();
            }
        });

        JLabel title = new JLabel("Upgrade Bang Fire");
        title.setForeground(TEXT_BROWN);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(130, 30, 300, 30);
        upgradeFrame.add(title);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(BUTTON_BROWN);
        closeButton.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
        closeButton.setFocusPainted(false);
        closeButton.setBounds(400, 20, 80, 30);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(UPGRADE_BUTTON_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(BUTTON_BROWN);
            }
        });
        closeButton.addActionListener(e -> upgradeFrame.dispose());
        upgradeFrame.add(closeButton);

        JProgressBar upgradeBar = new JProgressBar(0, maxUpgradeLevel);
        upgradeBar.setValue(upgradeLevel);
        upgradeBar.setStringPainted(true);
        upgradeBar.setForeground(BUTTON_BROWN);
        upgradeBar.setBackground(Color.WHITE);
        upgradeBar.setString("0%");
        upgradeBar.setBounds(100, 100, 300, 30);
        upgradeFrame.add(upgradeBar);

        JButton upgradeButton = new JButton("+");
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.setBackground(BUTTON_BROWN);
        upgradeButton.setBorderPainted(true);
        upgradeButton.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
        upgradeButton.setFocusPainted(false);
        upgradeButton.setFont(new Font("Arial", Font.BOLD, 24));
        upgradeButton.setBounds(200, 160, 100, 50);
        addButtonAnimation(upgradeButton);
        upgradeFrame.add(upgradeButton);

        JLabel cashLabel = new JLabel("Cash: " + game.getCash());
        cashLabel.setFont(new Font("Arial", Font.BOLD, 18));
        cashLabel.setForeground(TEXT_BROWN);
        cashLabel.setBounds(width / 2 - 40, 215, 200, 30);
        upgradeFrame.add(cashLabel);

        JLabel costLabel = new JLabel();
        costLabel.setFont(new Font("Arial", Font.BOLD, 18));
        costLabel.setForeground(Color.RED);
        costLabel.setBounds(330, 170, 200, 30);
        upgradeFrame.add(costLabel);

        if (upgradeLevel < maxUpgradeLevel) {
            costLabel.setText("Price: " + upgradeCosts[upgradeLevel]);
        } else {
            costLabel.setText("Max Upgrade");
        }

        JLabel messageLabel = new JLabel("");
        messageLabel.setBounds(200, 240, 300, 30);
        messageLabel.setForeground(TEXT_BROWN);
        upgradeFrame.add(messageLabel);

        upgradeButton.addActionListener(e -> {
            if (upgradeLevel >= maxUpgradeLevel) {
                messageLabel.setForeground(TEXT_BROWN);
                messageLabel.setText("Max Upgrade Reached!");
                costLabel.setText("Max Upgrade");
                return;
            }

            int cost = upgradeCosts[upgradeLevel];

            if (game.getCash() >= cost) {
                game.setCash(game.getCash() - cost);
                upgradeLevel++;
                game.setMaxFuel(game.getMaxFuel() + 30);
                game.setFuel(game.getMaxFuel());
                upgradeBar.setValue(upgradeLevel);
                game.getSoundManager().moneySound.playOnce();
                cashLabel.setText("   Cash: " + game.getCash());

                messageLabel.setForeground(TEXT_BROWN);
                messageLabel.setText("Upgrade Success!");

                if (upgradeLevel < maxUpgradeLevel) {
                    costLabel.setText("Price: " + upgradeCosts[upgradeLevel]);
                } else {
                    costLabel.setText("Max Upgrade");
                }
                game.repaint();
            } else {
                messageLabel.setForeground(TEXT_BROWN);
                messageLabel.setText("   Cash Not enough!!");
            }
        });

        upgradeFrame.setVisible(true);
    }

    private void addButtonAnimation(JButton button) {
        final Color[] targetColor = {BUTTON_BROWN};
        Timer animationTimer = new Timer(15, null);
        animationTimer.addActionListener(e -> {
            Color currentColor = button.getBackground();
            int red = moveColorChannel(currentColor.getRed(), targetColor[0].getRed());
            int green = moveColorChannel(currentColor.getGreen(), targetColor[0].getGreen());
            int blue = moveColorChannel(currentColor.getBlue(), targetColor[0].getBlue());
            button.setBackground(new Color(red, green, blue));

            if (currentColor.equals(targetColor[0])) {
                animationTimer.stop();
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                targetColor[0] = UPGRADE_BUTTON_HOVER_COLOR;
                animationTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                targetColor[0] = BUTTON_BROWN;
                animationTimer.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                targetColor[0] = UPGRADE_BUTTON_PRESSED_COLOR;
                animationTimer.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                targetColor[0] = button.contains(e.getPoint())
                    ? UPGRADE_BUTTON_HOVER_COLOR
                    : BUTTON_BROWN;
                animationTimer.start();
            }
        });
    }

    private int moveColorChannel(int current, int target) {
        if (current == target) return current;
        int difference = target - current;
        return current + Integer.signum(difference) * Math.max(1, Math.abs(difference) / 4);
    }
}
