import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BangFire extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 800;
    int boardHeight = 1050;

    Image BackgroundImg;
    Image BangFireImg;
    Image LeftObjectImg;
    Image RightObjectImg;
    Image LeftCat;
    Image RightCat;
    Image LeftUfoImg;
    Image RightUfoImg;
    Image BangFireImg2;
    Image BangFireImg0;
    Image BangFireImg3;
    Image ManImg;
    Image startImage;
    Image failImage;
    Image endImage;
    ImageIcon boomIcon;

    // Subsystems
    private SoundManager soundManager;
    private ScoreManager scoreManager;
    private ShopDialog shopDialog;
    private SettingsDialog settingsDialog;

    // Player Rocket
    int BangFireX = boardWidth / 2;
    int BangFireY = boardHeight / 2;
    int BangFireWidth = 80;
    int BangFireHeight = 150;
    FireWork FireWork;

    // Obstacles
    int ObjectX = boardWidth;
    int ObjectY = 0;
    int ObjectWidth = 80;
    int ObjectHeight = 60;
    int UfoWidth = 220;
    int UfoHeight = 160;
    int ufoSpawnInterval = 3000;
    long lastUfoSpawnTime = System.currentTimeMillis();
    ArrayList<Obstacle> objects = new ArrayList<>();
    ArrayList<Obstacle> ufoObjects = new ArrayList<>();
    ArrayList<Cat> cats = new ArrayList<>();

    // Physics & Game States
    double VelocityX = -5;
    double VelocityY = 0;
    double gravity = 1;
    Random random = new Random();
    Timer gameLoop;
    Timer placeObjectTimer;
    boolean gameOver = false;
    boolean gameStarted = false;
    boolean aPressed = false;
    double score = 0;
    long startTime;
    boolean spacePressed = false;
    long spaceHeldStartTime = 0;
    long gameStartTime;
    int backgroundY = 0;
    int backgroundScrollSpeed = 3;

    boolean showStartImage = true;
    boolean showFailImage = false;
    boolean showEndImage = false;
    boolean showStartText = true;
    long lastBlinkTime = 0;

    boolean fireworkPowered = false;
    long fireworkPowerStart = 0;
    int nextCatScore = 50;

    boolean explosionFinished = false;
    long explosionStartTime = 0;
    int explosionDuration = 800;
    boolean exploded = false;
    int explosionX, explosionY;

    int maxFuel = 60;
    int fuel = maxFuel;
    long lastFuelDecreaseTime = 0;

    int manY = 800;
    int manSpeed = 3;
    boolean manVisible = true;
    boolean ShopVisible = true;
    JLabel shopImg;

    private boolean showCash = true;
    private boolean showUI = true;
    private JButton settingsButton;

    public BangFire() {
        soundManager = new SoundManager();
        scoreManager = new ScoreManager();
        shopDialog = new ShopDialog();
        settingsDialog = new SettingsDialog();

        init();
        scoreManager.promptPlayerName(this);

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (showEndImage) {
                    soundManager.endSound.stop();
                    returnToMainMenu();
                }
                if (showStartImage) {
                    showStartImage = false;
                    repaint();
                }
            }
        });

        // Shop Icon
        java.net.URL shopUrl = getClass().getResource("/Image/Shop1.png");
        shopImg = new JLabel(shopUrl == null ? null : new ImageIcon(shopUrl));
        add(shopImg);

        shopImg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!shopDialog.isOpen()) {
                    shopDialog.show(BangFire.this);
                }
            }
        });

        // BGM Start
        soundManager.bgmSound.loop();

        // Settings Button
        settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Arial", Font.BOLD, 12));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setBackground(new Color(0x9A, 0x62, 0x2E));
        settingsButton.setBorder(BorderFactory.createRaisedBevelBorder());
        settingsButton.setFocusPainted(false);
        settingsButton.setBounds(boardWidth - 120, 10, 100, 30);
        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                settingsButton.setBackground(new Color(0xB7, 0x7A, 0x3E));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                settingsButton.setBackground(new Color(0x9A, 0x62, 0x2E));
            }
        });
        settingsButton.addActionListener(e -> settingsDialog.show(this));
        add(settingsButton);
    }

    public BangFire(String playerName) {
        this();
        scoreManager.setPlayerName(playerName);
    }

    public void init() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        try {
            BackgroundImg = ImageIO.read(getClass().getResource("/Image/Tall_BG.png"));
            if (BackgroundImg != null) {
                backgroundY = BackgroundImg.getHeight(null) - boardHeight;
            }

            BangFireImg = new ImageIcon(getClass().getResource("/Image/BangFire1.png")).getImage();
            BangFireImg2 = new ImageIcon(getClass().getResource("/Image/BangFire2.png")).getImage();
            BangFireImg0 = new ImageIcon(getClass().getResource("/Image/BangFire0.png")).getImage();
            BangFireImg3 = new ImageIcon(getClass().getResource("/Image/BangFire3.png")).getImage();
            LeftObjectImg = new ImageIcon(getClass().getResource("/Image/BirdLeft.gif")).getImage();
            RightObjectImg = new ImageIcon(getClass().getResource("/Image/BirdRight.gif")).getImage();
            LeftUfoImg = new ImageIcon(getClass().getResource("/Image/Ufo.png")).getImage();
            RightUfoImg = new ImageIcon(getClass().getResource("/Image/Ufo.png")).getImage();
            ManImg = new ImageIcon(getClass().getResource("/Image/Man.png")).getImage();
            LeftCat = new ImageIcon(getClass().getResource("/Image/CatLeft.gif")).getImage();
            RightCat = new ImageIcon(getClass().getResource("/Image/CatRight.gif")).getImage();
            startImage = ImageIO.read(getClass().getResource("/Image/Start.png"));
            failImage = ImageIO.read(getClass().getResource("/Image/Fail.png"));
            endImage = ImageIO.read(getClass().getResource("/Image/End.jpg"));
            boomIcon = new ImageIcon(getClass().getResource("/Image/Boom.gif"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BangFireImg != null) {
            BangFireImg = BangFireImg.getScaledInstance(BangFireWidth, BangFireHeight, Image.SCALE_SMOOTH);
        }
        if (BangFireImg2 != null) {
            BangFireImg2 = BangFireImg2.getScaledInstance(BangFireWidth, BangFireHeight, Image.SCALE_SMOOTH);
        }

        FireWork = new FireWork(BangFireX, BangFireY, BangFireWidth, BangFireHeight, BangFireImg);
        objects = new ArrayList<>();

        placeObjectTimer = new Timer(1500, e -> placeObject());
        placeObjectTimer.start();
        placeObjectTimer.stop();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void spawnUfo() {
        Random rand = new Random();
        int maxY = boardHeight / 2 - UfoHeight;

        if (rand.nextBoolean()) {
            Obstacle leftUfo = new Obstacle(boardWidth, rand.nextInt(maxY), UfoWidth, UfoHeight, LeftUfoImg);
            leftUfo.dy = -5;
            leftUfo.dx = rand.nextInt(5);
            ufoObjects.add(leftUfo);
        } else {
            Obstacle rightUfo = new Obstacle(-UfoWidth, rand.nextInt(maxY), UfoWidth, UfoHeight, RightUfoImg);
            rightUfo.dy = 5;
            rightUfo.dx = rand.nextInt(5);
            ufoObjects.add(rightUfo);
        }
    }

    public void placeObject() {
        if (score <= 200) {
            int spawnRangeY = random.nextInt(boardHeight / 2);
            boolean spawnLeft = random.nextBoolean();

            Obstacle obj = new Obstacle(
                    spawnLeft ? boardWidth : -ObjectWidth,
                    spawnRangeY,
                    ObjectWidth,
                    ObjectHeight,
                    spawnLeft ? LeftObjectImg : RightObjectImg
            );
            obj.dy = random.nextInt(5);
            objects.add(obj);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (showStartImage && startImage != null) {
            g.drawImage(startImage, 0, 0, boardWidth, boardHeight, null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Click to Start", boardWidth - 160, boardHeight - 20);
            return;
        }

        super.paintComponent(g);
        draw(g);

        if (showEndImage && endImage != null) {
            g.drawImage(endImage, 0, 0, boardWidth, boardHeight, null);
        }

        for (Cat cat : cats) {
            cat.draw(g, LeftCat, RightCat);
        }

        if (showFailImage && failImage != null) {
            g.drawImage(failImage, 0, 0, boardWidth, boardHeight, null);
        }

        if (!showEndImage) {
            if (gameOver) {
                settingsButton.setVisible(true);
                showUI = true;
                cats.clear();

                g.setColor(Color.red);
                g.setFont(new Font("Arial", Font.PLAIN, 32));
                FontMetrics metrics = g.getFontMetrics();
                String gameOverText = "Your High is: " + (int) score + " M";
                int x = (boardWidth - metrics.stringWidth(gameOverText)) / 2;
                g.drawString(gameOverText, x, 50);

                String restartText = "Press R to Restart";
                int restartX = (boardWidth - metrics.stringWidth(restartText)) / 2;
                g.drawString(restartText, restartX, 90);
            }
        }

        // Draw UFOs and check collision
        for (int i = 0; i < ufoObjects.size(); i++) {
            Obstacle ufo = ufoObjects.get(i);
            ufo.x += ufo.dy;
            ufo.y += ufo.dx;

            g.drawImage(ufo.img, ufo.x, ufo.y, 174, 90, null);

            Rectangle fireworkRect = FireWork.getBounds();
            Rectangle ufoRect = new Rectangle(ufo.x, ufo.y, ufo.width, ufo.height);

            if (!fireworkPowered && fireworkRect.intersects(ufoRect)) {
                soundManager.damageSound.playOnce();
                fuel -= 40;
                ufoObjects.remove(i);
                i--;
                continue;
            }

            if (ufo.x < -ufo.width || ufo.x > boardWidth + ufo.width) {
                ufoObjects.remove(i);
                i--;
            }
        }

        if (!showEndImage) {
            if (showUI) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                int startX = boardWidth - 780;
                int startY = 30;
                int lineHeight = 20;

                List<String> topScores = scoreManager.getTopScores(3);
                g.drawString("High Scores:", startX, startY);
                for (int i = 0; i < topScores.size(); i++) {
                    g.drawString(topScores.get(i), startX, startY + (i + 1) * lineHeight);
                }
                int spaceAfterHighScores = topScores.size() + 2;
                g.drawString("MyScore:", startX, startY + spaceAfterHighScores * lineHeight);
                g.drawString(scoreManager.getPlayerName() + ": " + scoreManager.getHighScore(),
                        startX, startY + (spaceAfterHighScores + 1) * lineHeight);
            }
        }
    }

    public void draw(Graphics g) {
        if (BackgroundImg != null) {
            int bgHeight = BackgroundImg.getHeight(null);
            if (backgroundY < 0) backgroundY = 0;
            if (backgroundY > bgHeight - boardHeight) backgroundY = bgHeight - boardHeight;

            g.drawImage(BackgroundImg,
                    0, 0, boardWidth, boardHeight,
                    0, backgroundY, boardWidth, backgroundY + boardHeight,
                    null);
        }

        if (showEndImage) {
            soundManager.bgmSound.stop();
            soundManager.endSound.playOnce();
            if (endImage != null) {
                g.drawImage(endImage, 0, 0, boardWidth, boardHeight, null);
            }
            return;
        }

        if (!showEndImage) {
            if (ManImg != null) {
                g.drawImage(ManImg, 500, manY, 180, 200, null);
            }
            shopImg.setBounds(50, manY, 230, 230);
        }

        if (!showEndImage && !exploded) {
            Graphics2D g2d = (Graphics2D) g.create();
            if (!gameStarted) {
                g2d.drawImage(BangFireImg0, BangFireX, BangFireY, 90, 170, null);
            } else {
                double angle = 0;
                if (VelocityX < -1) {
                    angle = -0.2;
                } else if (VelocityX > 1) {
                    angle = 0.2;
                }

                int centerX = FireWork.x + FireWork.width / 2;
                int centerY = FireWork.y + FireWork.height / 2;

                AffineTransform transform = AffineTransform.getRotateInstance(angle, centerX, centerY);
                transform.translate(FireWork.x, FireWork.y);
                g2d.drawImage(FireWork.img, transform, null);
            }
            g2d.dispose();
        } else if (!showEndImage && !explosionFinished && boomIcon != null) {
            g.drawImage(boomIcon.getImage(), explosionX - 160, explosionY - 130, 400, 400, this);
            if (System.currentTimeMillis() - explosionStartTime > explosionDuration) {
                explosionFinished = true;
            }
        }

        if (!showEndImage) {
            for (Obstacle obj : objects) {
                g.drawImage(obj.img, obj.x, obj.y, obj.width, obj.height, null);
            }
        }

        if (!showEndImage) {
            g.setFont(new Font("Arial", Font.PLAIN, 32));
            FontMetrics metrics = g.getFontMetrics();
            String text = (int) score + " M";

            if (gameOver) {
                settingsButton.setVisible(true);
                showUI = true;

                cats.clear();
                g.setColor(Color.red);
                text = "Your High is: " + (int) score + " M";
                int x = (boardWidth - metrics.stringWidth(text)) / 2;
                g.drawString(text, x, 50);

                String restartText = "Press R to Restart";
                int restartX = (boardWidth - metrics.stringWidth(restartText)) / 2;
                g.drawString(restartText, restartX, 90);
            } else {
                g.setColor(Color.white);
                int x = (boardWidth - metrics.stringWidth(text)) / 2;
                g.drawString(text, x, 50);
            }

            if (!gameStarted && !gameOver && showStartText) {
                String startText = "Press SPACEBAR to Start";
                Font startFont = new Font("Sans", Font.BOLD, 36);
                g.setFont(startFont);
                FontMetrics fm = g.getFontMetrics(startFont);
                int startX = (boardWidth - fm.stringWidth(startText)) / 2;
                int startY = boardHeight / 2;
                g.setColor(Color.black);
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dy = -3; dy <= 3; dy++) {
                        if (dx != 0 || dy != 0) {
                            g.drawString(startText, startX + dx, startY + dy);
                        }
                    }
                }
                g.setColor(Color.yellow);
                g.drawString(startText, startX, startY);
            }

            if (gameStarted && !gameOver) {
                int barWidth = 400;
                int barHeight = 20;
                int barX = (boardWidth - barWidth) / 2;
                int barY = boardHeight - barHeight - 30;

                g.setColor(Color.white);
                g.drawRect(barX, barY, barWidth, barHeight);

                int currentWidth = (int) ((fuel / (double) maxFuel) * barWidth);
                g.setColor(Color.red);
                g.fillRect(barX, barY, currentWidth, barHeight);

                g.setColor(Color.white);
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString(": " + fuel, barX + barWidth + 10, barY + barHeight - 5);
            }

            if (showCash) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("Cash: " + scoreManager.getCash() + " Bath", getWidth() / 2 + 150, getHeight() - 20);
            }
        }
    }

    public void move() {
        if (!gameStarted) return;
        backgroundY -= backgroundScrollSpeed;

        if (spacePressed) {
            FireWork.img = fireworkPowered ? BangFireImg3 : BangFireImg2;
            long heldTime = System.currentTimeMillis() - spaceHeldStartTime;
            int strength = (int) (heldTime / 150);
            VelocityY = -1 * (strength + 2);
            VelocityY = Math.max(VelocityY, -10);
        } else {
            FireWork.img = fireworkPowered ? BangFireImg3 : BangFireImg;
            int centerY = boardHeight / 2;
            if (FireWork.y < centerY) {
                VelocityY += gravity - 0.5;
            } else {
                VelocityY = 0;
                FireWork.y = centerY;
            }
        }

        FireWork.x += VelocityX;
        FireWork.y += VelocityY;
        FireWork.y = Math.max(0, Math.min(boardHeight - FireWork.height, FireWork.y));

        long timeSinceStart = System.currentTimeMillis() - gameStartTime;
        if (timeSinceStart >= 1500) {
            if (!spacePressed) {
                if (aPressed) {
                    long heldTime = System.currentTimeMillis() - spaceHeldStartTime;
                    int strength = (int) (heldTime / 150);
                    VelocityX = -1 * (strength + 1);
                    VelocityX = Math.max(VelocityX, -2);
                } else {
                    VelocityX += gravity - 0.3;
                }
            } else {
                VelocityX = 0;
            }
        } else {
            VelocityX = 0;
        }

        FireWork.x += VelocityX;
        FireWork.x = Math.max(0, Math.min(boardWidth - FireWork.width, FireWork.x));

        for (int i = 0; i < objects.size(); i++) {
            Obstacle obj = objects.get(i);
            if (obj.img == LeftObjectImg) {
                obj.x -= 7;
                obj.y += obj.dy;
            } else {
                obj.x += 7;
                obj.y += obj.dy;
            }
            if (obj.x + obj.width < 0 || obj.x > boardWidth || obj.y + obj.height < 0) {
                objects.remove(i);
                i--;
                continue;
            }
            if (!obj.passed && FireWork.x > obj.x + obj.width) {
                obj.passed = true;
            }

            if (!fireworkPowered && collision(FireWork, obj)) {
                soundManager.damageSound.playOnce();
                fuel -= 20;
                if (fuel <= 0 && !exploded) {
                    soundManager.failSound.playOnce();
                    soundManager.fuelBlastSound.playOnce();
                    gameOver = true;
                    exploded = true;
                    explosionX = FireWork.x;
                    explosionY = FireWork.y;
                    explosionStartTime = System.currentTimeMillis();
                }
                objects.remove(i);
                i--;
                continue;
            }
        }

        if (FireWork.x <= 0 || FireWork.x + FireWork.width >= boardWidth) {
            soundManager.failSound.playOnce();
            gameOver = true;
            showFailImage = true;
        }

        if (gameStarted && !gameOver && manVisible && ShopVisible) {
            manY += manSpeed;
            if (manY > boardHeight) {
                manVisible = false;
                ShopVisible = false;
            }
        }
    }

    public void restartGame() {
        settingsButton.setVisible(true);
        gameOver = false;
        gameStarted = false;
        showFailImage = false;
        showEndImage = false;
        exploded = false;
        explosionFinished = false;
        fireworkPowered = false;

        FireWork.reset(BangFireX, BangFireY, BangFireImg);
        VelocityX = -5;
        VelocityY = 0;
        score = 0;
        fuel = maxFuel;
        nextCatScore = 50;

        soundManager.failSound.stop();

        cats.clear();
        objects.clear();
        ufoObjects.clear();
        if (BackgroundImg != null) {
            backgroundY = BackgroundImg.getHeight(null) - boardHeight;
        }

        showUI = true;
        settingsButton.setVisible(true);
        showCash = true;
        manVisible = true;
        ShopVisible = true;
        manY = 800;

        placeObjectTimer.start();
        gameLoop.start();

        soundManager.fireworkLoopSound.stop();
        soundManager.bgmSound.loop();

        repaint();
    }

    public boolean collision(FireWork a, Obstacle b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver) {
            long currentTime = System.currentTimeMillis();
            score = (currentTime - startTime) / 350;
        }

        if (gameStarted) {
            settingsButton.setVisible(false);
            showCash = false;
            showUI = false;
            move();
            long currentTime = System.currentTimeMillis();
            score = (currentTime - startTime) / 350;

            if (!gameOver && currentTime - lastBlinkTime > 500) {
                showStartText = !showStartText;
                lastBlinkTime = currentTime;
            }

            if (score > 200 && currentTime - lastUfoSpawnTime >= ufoSpawnInterval) {
                spawnUfo();
                lastUfoSpawnTime = currentTime;
            }

            // Fuel depletion
            if (!fireworkPowered && currentTime - lastFuelDecreaseTime >= 1000) {
                lastFuelDecreaseTime = currentTime;
                fuel -= spacePressed ? 2 : 1;
                if (fuel <= 0 && !exploded) {
                    soundManager.fuelBlastSound.playOnce();
                    gameOver = true;
                    exploded = true;
                    explosionX = FireWork.x;
                    explosionY = FireWork.y;
                    explosionStartTime = System.currentTimeMillis();
                }
            }

            if ((FireWork.y < -FireWork.height ||
                    FireWork.y > boardHeight ||
                    FireWork.x < -FireWork.width ||
                    FireWork.x > boardWidth) &&
                    !showFailImage) {
                showFailImage = true;
                soundManager.fireworkLoopSound.stop();
                gameOver = true;
                repaint();
            }

            // Win condition score >= 300
            if (score >= 300) {
                gameOver = true;
                showEndImage = true;
                soundManager.fireworkLoopSound.stop();
                placeObjectTimer.stop();
                soundManager.bgmSound.stop();
                gameLoop.stop();

                showUI = false;
                settingsButton.setVisible(false);
                showCash = false;
                cats.clear();
                objects.clear();
                ufoObjects.clear();

                repaint();
                return;
            }
        }

        if (!gameStarted && !gameOver) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBlinkTime >= 400) {
                showStartText = !showStartText;
                lastBlinkTime = currentTime;
            }
        }

        if (gameOver) {
            soundManager.bgmSound.stop();
            ufoObjects.clear();
            settingsButton.setVisible(false);
            showUI = false;
            soundManager.fireworkLoopSound.stop();
            if ((int) score > scoreManager.getHighScore()) {
                scoreManager.setHighScore((int) score);
                scoreManager.updatePlayerHighScore(scoreManager.getPlayerName(), scoreManager.getHighScore());
            }
            placeObjectTimer.stop();
            gameLoop.stop();
        }

        Iterator<Cat> iterator = cats.iterator();
        while (iterator.hasNext()) {
            Cat cat = iterator.next();
            cat.move();
            Rectangle fwBounds = FireWork.getBounds();
            if (fwBounds.intersects(cat.getBounds()) && !fireworkPowered) {
                soundManager.powerSound.playOnce();
                fireworkPowered = true;
                fireworkPowerStart = System.currentTimeMillis();
                if (BangFireImg3 != null) {
                    BangFireImg3 = BangFireImg3.getScaledInstance(BangFireWidth, BangFireHeight, Image.SCALE_SMOOTH);
                }
                FireWork.img = BangFireImg3;
                iterator.remove();
            }
        }

        if (fireworkPowered && System.currentTimeMillis() - fireworkPowerStart > 10000) {
            fireworkPowered = false;
            FireWork.img = BangFireImg;
        }

        if (score >= nextCatScore) {
            int catWidth = 150;
            int catHeight = 130;
            boolean isRight = new Random().nextBoolean();
            int catY = new Random().nextInt(boardHeight / 2);
            cats.add(new Cat(isRight ? 0 : boardWidth, catY, catWidth, catHeight, isRight));
            nextCatScore += 50;
        }

        repaint();

        if (gameOver) {
            scoreManager.addCash((int) score);
            showCash = true;
            placeObjectTimer.stop();
            gameLoop.stop();
        }
    }

    public void returnToMainMenu() {
        soundManager.stopAllSounds();

        Window currentWindow = SwingUtilities.getWindowAncestor(this);
        if (currentWindow != null) {
            currentWindow.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame menuFrame = new JFrame("BangFire");
                menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                menuFrame.setSize(800, 1050);
                menuFrame.setResizable(false);
                menuFrame.setLocationRelativeTo(null);

                MainMenu menu = new MainMenu(menuFrame);
                menuFrame.setContentPane(menu);
                menuFrame.setVisible(true);
                menuFrame.requestFocus();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while returning to main menu: " + e.getMessage());
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (shopDialog.isOpen() || settingsDialog.isOpen()) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            soundManager.boostSound.playOnce();
            if (!gameStarted && !gameOver) {
                showCash = true;
                settingsButton.setVisible(false);
                gameStarted = true;
                showUI = false;
                soundManager.launcherSound.playOnce();
                soundManager.fireworkLoopSound.loop();
                startTime = System.currentTimeMillis();
                gameStartTime = System.currentTimeMillis();
                placeObjectTimer.start();
            } else if (!spacePressed) {
                spacePressed = true;
                spaceHeldStartTime = System.currentTimeMillis();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_A && !aPressed) {
            aPressed = true;
            spaceHeldStartTime = System.currentTimeMillis();
        }

        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) aPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) spacePressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Getters & Setters for Subsystems and Compatibility
    public SoundManager getSoundManager() {
        return soundManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public ShopDialog getShopDialog() {
        return shopDialog;
    }

    public SettingsDialog getSettingsDialog() {
        return settingsDialog;
    }

    public int getCash() {
        return scoreManager.getCash();
    }

    public void setCash(int cash) {
        scoreManager.setCash(cash);
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public void setPlayerName(String name) {
        scoreManager.setPlayerName(name);
    }

    public void promptPlayerName() {
        scoreManager.promptPlayerName(this);
    }

    public void updatePlayerHighScore(String name, int newScore) {
        scoreManager.updatePlayerHighScore(name, newScore);
    }

    public void setEffectVolume(float volume) {
        soundManager.setEffectVolume(volume);
    }

    public void setBgmVolume(float volume) {
        soundManager.setBgmVolume(volume);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BangFire");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 1050);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            MainMenu menu = new MainMenu(frame);
            frame.setContentPane(menu);
            frame.setVisible(true);
        });
    }
}