import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

public class SettingsDialog {
    private static final Color PANEL_COLOR = new Color(0xFF, 0xE9, 0xAD);
    private static final Color TEXT_BROWN = new Color(0x7A, 0x4A, 0x21);
    private static final Color BUTTON_BROWN = new Color(0x9A, 0x62, 0x2E);
    private static final Color BUTTON_HOVER_BROWN = new Color(0xB7, 0x7A, 0x3E);

    private boolean isSettingsDialogOpen = false;

    public boolean isOpen() {
        return isSettingsDialogOpen;
    }

    public void setOpen(boolean open) {
        this.isSettingsDialogOpen = open;
    }

    public void show(BangFire game) {
        if (isSettingsDialogOpen || (game.getShopDialog() != null && game.getShopDialog().isOpen())) {
            return;
        }
        isSettingsDialogOpen = true;

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(game);
        JDialog settingsDialog = new JDialog(parentFrame, "Settings", true);
        settingsDialog.setUndecorated(true);
        settingsDialog.setSize(350, 500);
        settingsDialog.setLayout(new GridBagLayout());
        settingsDialog.getContentPane().setBackground(PANEL_COLOR);
        settingsDialog.setLocationRelativeTo(game);

        settingsDialog.setModal(true);
        settingsDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(PANEL_COLOR);
        contentPanel.setFocusable(true);
        contentPanel.requestFocusInWindow();
        settingsDialog.add(contentPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Effect Volume
        JLabel effectLabel = createStyledLabel("Effect Volume");
        settingsDialog.add(effectLabel, gbc);

        SoundManager soundManager = game.getSoundManager();
        JSlider effectSlider = new JSlider(0, 100, (int) (soundManager.getEffectVolume() * 100));
        styleSlider(effectSlider);
        settingsDialog.add(effectSlider, gbc);

        // BGM Volume
        JLabel bgmLabel = createStyledLabel("Background Music Volume");
        settingsDialog.add(bgmLabel, gbc);

        JSlider bgmSlider = new JSlider(0, 100, (int) (soundManager.getBgmVolume() * 100));
        styleSlider(bgmSlider);
        settingsDialog.add(bgmSlider, gbc);

        // Slider listeners
        effectSlider.addChangeListener(e -> {
            float newVolume = effectSlider.getValue() / 100f;
            soundManager.setEffectVolume(newVolume);
        });

        bgmSlider.addChangeListener(e -> {
            float newVolume = bgmSlider.getValue() / 100f;
            soundManager.setBgmVolume(newVolume);
        });

        // Main Menu button
        JButton menuButton = new JButton("Main Menu");
        styleButton(menuButton);
        menuButton.addActionListener(e -> {
            settingsDialog.dispose();
            game.returnToMainMenu();
        });
        settingsDialog.add(menuButton, gbc);

        // Close button
        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> settingsDialog.dispose());
        settingsDialog.add(closeButton, gbc);

        settingsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                isSettingsDialogOpen = false;
                game.requestFocus();
            }
        });

        settingsDialog.setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(TEXT_BROWN);
        return label;
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new java.awt.Dimension(200, 50));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(BUTTON_BROWN);
        button.setForeground(Color.WHITE);
        button.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_BROWN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BROWN);
            }
        });
    }

    private void styleSlider(JSlider slider) {
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(PANEL_COLOR);
        slider.setForeground(TEXT_BROWN);

        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BUTTON_BROWN);
                g2d.fillRoundRect(trackRect.x, trackRect.y + (trackRect.height - 8) / 2,
                        trackRect.width, 8, 4, 4);
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BUTTON_HOVER_BROWN);
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });
    }
}
