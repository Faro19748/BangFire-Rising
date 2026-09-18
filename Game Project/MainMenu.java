import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class MainMenu extends JPanel {
    private JFrame frame;
    private Image menuBackground;
    private JSlider volumeSlider;
    private Clip bgmClip;
    

    public MainMenu(JFrame frame) {
        this.frame = frame;
        setLayout(null);

        // Load background image
        try {
            java.net.URL menuUrl = getClass().getResource("/Image/Menu.gif");
            if (menuUrl == null) {
                throw new IOException("Image/Menu.gif not found");
            }
            menuBackground = ImageIO.read(menuUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Play button
        JButton playButton = new JButton("Play");
        styleButton(playButton, 300, 400);
        playButton.addActionListener(e -> {
            App.startGame();
            if (bgmClip != null) {
                bgmClip.stop();
                bgmClip.close();
            }
            frame.dispose();
        });

        // How to Play button
        JButton howToPlayButton = new JButton("How to Play");
        styleButton(howToPlayButton, 300, 470);
        howToPlayButton.addActionListener(e -> showHowToPlay());

        // Exit button
        JButton exitButton = new JButton("Exit");
        styleButton(exitButton, 300, 610);
        exitButton.addActionListener(e -> System.exit(0));

        
        add(playButton);
        add(howToPlayButton);
        add(exitButton);

        JButton settingButton = new JButton("Setting");
        styleButton(settingButton, 300, 540); 
        settingButton.setPreferredSize(new Dimension(60,40));
        settingButton.addActionListener(e -> showSettings());

        add(settingButton);
        playMenuMusic();
    }

    private void styleButton(JButton button, int x, int y) {
        button.setBounds(x, y, 200, 50);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(70, 100, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
         // เพิ่มเอฟเฟกต์เมื่อเมาส์โฮเวอร์
        button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            button.setBackground(new Color(90, 130, 210)); // สีเมื่อเมาส์อยู่เหนือปุ่ม
            button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // เปลี่ยนเคอร์เซอร์เป็นรูปมือ
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(new Color(70, 100, 180)); // สีปกติเมื่อเมาส์ออก
        }
      });
    }
        

    private void playMenuMusic() {
    try {
        java.net.URL musicUrl = getClass().getResource("/Sound/Menu.wav");
        if (musicUrl == null) {
            throw new IOException("Sound/Menu.wav not found");
        }
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicUrl);
        bgmClip = AudioSystem.getClip();
        bgmClip.open(audioIn);
        setVolume(0.8f); // ค่าเริ่มต้น: 70%
        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
        e.printStackTrace();
    }

    }


    private void setVolume(float volume) {
    if (bgmClip != null) {
        FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();
        float dB = min + (max - min) * volume;
        gainControl.setValue(dB);
    }

    }

    private void showSettings() {
    volumeSlider = new JSlider(0, 100, 70);
    volumeSlider.setMajorTickSpacing(25);
    volumeSlider.setPaintTicks(true);
    volumeSlider.setPaintLabels(true);

    volumeSlider.addChangeListener(e -> {
        float volume = volumeSlider.getValue() / 100f;
        setVolume(volume);
    });

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JLabel("Music Volume:"), BorderLayout.NORTH);
    panel.add(volumeSlider, BorderLayout.CENTER);

    JOptionPane.showMessageDialog(this, panel, "Settings", JOptionPane.PLAIN_MESSAGE);

    }

    

    private void startGame() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
        // Create and show game window
        JFrame gameFrame = new JFrame("Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 600);
        gameFrame.setLocationRelativeTo(null);
        // gameFrame.add(new GamePanel()); // Add your actual game panel here
        
        gameFrame.setVisible(true);
    }

    private void showHowToPlay() {
        JTextArea textArea = new JTextArea("-Press A to move left\n-PressD to move right\n-Press spacebar to boost\n-Upgrade your BangFire\n-Beware the Bird");
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 18));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (menuBackground != null) {
            g.drawImage(menuBackground, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 1050);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new MainMenu(frame));
        frame.setVisible(true);
    }
}
