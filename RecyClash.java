package RecyClash;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

class GamePanel extends JPanel
        implements ActionListener, KeyListener, MouseMotionListener {

    // dimensões do jogador
    private static final int PLAYER_WIDTH  = 80;
    private static final int PLAYER_HEIGHT = 80;
    private static final int PLAYER_Y      = 505;
    private static final int PLAYER_SPEED  = 10;

    // dimensões dos itens
    private static final int ITEM_WIDTH  = 40;
    private static final int ITEM_HEIGHT = 40;

    // velocidades e pontos
    private static final int SPEED_SLOW   = 3;
    private static final int SPEED_MEDIUM = 5;
    private static final int SPEED_FAST   = 7;
    private static final int POINTS_SLOW   = 100;
    private static final int POINTS_MEDIUM = 125;
    private static final int POINTS_FAST   = 150;

    // tempos e dificuldade
    private static final int BASE_GENERATION_INTERVAL    = 1200;
    private static final int DIFFICULTY_INCREASE_INTERVAL = 30;
    private static final int MESSAGE_DISPLAY_DURATION    = 1500;

    private final CardLayout cardLayout;
    private final JPanel cards;
    private final MenuPanel menuPanel;

    private Timer gameTimer;
    private Timer generateTimer;
    private long gameStartTime;

    private int playerX;
    private int playerLives;
    private int score;
    private final List<Trash> trashList;
    private final Random random;

    private boolean leftPressed  = false;
    private boolean rightPressed = false;
    private boolean paused       = false;

    private String centerMessage = "";
    private long messageStartTime;

    private int currentGenerationInterval;
    private double currentSpeedMultiplier;
    private int lastDifficultyIncreaseTime;

    public GamePanel(CardLayout cl, JPanel cards, MenuPanel menu) {
        this.cardLayout = cl;
        this.cards = cards;
        this.menuPanel = menu;

        setPreferredSize(new Dimension(RecyClash.WIDTH, RecyClash.HEIGHT));
        setBackground(new Color(20, 30, 48));
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);

        random = new Random();
        trashList = new ArrayList<>();
        playerX = 360;
        currentGenerationInterval = BASE_GENERATION_INTERVAL;
        currentSpeedMultiplier = 0.5;
    }

    public void startGame() {
        resetGame();
        gameStartTime = System.currentTimeMillis();
        lastDifficultyIncreaseTime = 0;
        paused = false;
        gameTimer = new Timer(20, this);
        generateTimer = new Timer(currentGenerationInterval, e -> generateTrash());
        gameTimer.start();
        generateTimer.start();
    }

    private void resetGame() {
        score = 0;
        playerLives = 3;
        trashList.clear();
        playerX = 360;
        currentGenerationInterval = BASE_GENERATION_INTERVAL;
        currentSpeedMultiplier = 1.0;
    }

    private void generateTrash() {
        if (paused) return;
        if (trashList.size() >= 4) return;

        boolean recyclable = random.nextBoolean();
        Trash.SpeedType speedType = pickSpeedType();
        int x = random.nextInt(RecyClash.WIDTH - ITEM_WIDTH);
        trashList.add(new Trash(x, 0, recyclable, speedType));
    }

    private Trash.SpeedType pickSpeedType() {
        int chance = random.nextInt(100);
        if (chance < 50) return Trash.SpeedType.SLOW;
        if (chance < 85) return Trash.SpeedType.MEDIUM;
        return Trash.SpeedType.FAST;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused) {
            repaint();
            return;
        }
        updateGame();
        repaint();
    }

    private void updateGame() {
        if (leftPressed) {
            playerX -= PLAYER_SPEED;
            if (playerX < 0) playerX = 0;
        }
        if (rightPressed) {
            playerX += PLAYER_SPEED;
            if (playerX > RecyClash.WIDTH - PLAYER_WIDTH)
                playerX = RecyClash.WIDTH - PLAYER_WIDTH;
        }

        Iterator<Trash> it = trashList.iterator();
        while (it.hasNext()) {
            Trash t = it.next();
            t.move(currentSpeedMultiplier);

            if (t.getY() > RecyClash.HEIGHT) {
                it.remove();
                if (t.isRecyclable()) {
                    playerLives--;
                    showMessage("Você deixou de reciclar!");
                    if (playerLives <= 0) endGame();
                }
                continue;
            }

            if (t.getBounds().intersects(
                    new Rectangle(playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT))) {
                if (t.isRecyclable()) {
                    int points = switch (t.getSpeedType()) {
                        case SLOW   -> POINTS_SLOW;
                        case MEDIUM -> POINTS_MEDIUM;
                        case FAST   -> POINTS_FAST;
                    };
                    score += points;
                    int timeBonus = (int) (getElapsedTime() / 5.0);
                    score += timeBonus;
                } else {
                    playerLives--;
                    showMessage("Isso não é reciclável!");
                    if (playerLives <= 0) endGame();
                }
                it.remove();
            }
        }

        int elapsed = (int) getElapsedTime();
        if (elapsed / DIFFICULTY_INCREASE_INTERVAL > lastDifficultyIncreaseTime) {
            lastDifficultyIncreaseTime = elapsed / DIFFICULTY_INCREASE_INTERVAL;
            score += 50;
            currentSpeedMultiplier = Math.min(currentSpeedMultiplier + 0.5, 3.0);
            int newInterval = Math.max(600, currentGenerationInterval - 100);
            if (newInterval != currentGenerationInterval) {
                currentGenerationInterval = newInterval;
                generateTimer.setDelay(currentGenerationInterval);
            }
        }
    }

    private void showMessage(String msg) {
        centerMessage = msg;
        messageStartTime = System.currentTimeMillis();
    }

    private double getElapsedTime() {
        return (System.currentTimeMillis() - gameStartTime) / 1000.0;
    }

    private void endGame() {
        gameTimer.stop();
        generateTimer.stop();
        paused = true;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog((Component) this,
                    "Fim de Jogo!\nPontuação: " + score
                            + "\nTempo: " + getFormattedTime(),
                    "Fim de Jogo", JOptionPane.INFORMATION_MESSAGE);
            menuPanel.updateScores(score);
            cardLayout.show(cards, "MENU");
        });
    }

    private String getFormattedTime() {
        double elapsed = getElapsedTime();
        int minutes = (int) (elapsed / 60.0);
        int seconds = (int) (elapsed % 60.0);
        int centiseconds = (int) ((elapsed - (int) elapsed) * 100.0);
        return String.format("%02d:%02d,%02d", minutes, seconds, centiseconds);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gp = new GradientPaint(
                0f, 0f, new Color(10, 25, 47),
                0f, getHeight(), new Color(27, 42, 68));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        drawPlayer(g2d, playerX, PLAYER_Y);

        for (Trash t : trashList) {
            t.draw(g2d);
        }

        drawInfoPanel(g2d);

        if (!centerMessage.isEmpty()) {
            long now = System.currentTimeMillis();
            if (now - messageStartTime < MESSAGE_DISPLAY_DURATION) {
                drawCenteredMessage(g2d, centerMessage);
            } else {
                centerMessage = "";
            }
        }

        if (paused) drawPauseOverlay(g2d);
    }

    private void drawPlayer(Graphics2D g, int x, int y) {
        g.setColor(new Color(70, 130, 180));
        g.fillRoundRect(x, y + 15, PLAYER_WIDTH, PLAYER_HEIGHT - 15, 20, 20);
        g.setColor(new Color(100, 160, 200));
        g.fillRoundRect(x + 10, y, PLAYER_WIDTH - 20, 20, 15, 15);
        g.setColor(new Color(30, 70, 120));
        g.fillRect(x + 15, y + 25, PLAYER_WIDTH - 30, PLAYER_HEIGHT - 40);
    }

    private void drawInfoPanel(Graphics2D g) {
        int margin = 15;
        int panelWidth = 270;
        int panelHeight = 90;
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(margin, margin, panelWidth, panelHeight, 25, 25);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("Pontuação: " + score, margin + 20, margin + 30);
        g.drawString("Tempo: " + getFormattedTime(), margin + 20, margin + 60);

        int heartX = margin + 20;
        int heartY = margin + 65;
        for (int i = 0; i < playerLives; i++) {
            drawHeart(g, heartX + i * 30, heartY, 18, Color.RED);
        }
    }

    private void drawHeart(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        int[] xs = { x, x + size / 2, x + size, x + size * 3 / 4,
                     x + size / 2, x + size / 4 };
        int[] ys = { y + size / 4, y, y + size / 4, y + size * 3 / 4,
                     y + size, y + size * 3 / 4 };
        g.fillPolygon(xs, ys, xs.length);
    }

    private void drawCenteredMessage(Graphics2D g, String msg) {
        g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(msg);
        int height = fm.getHeight();
        int x = (getWidth() - width) / 2;
        int y = getHeight() / 2 + height / 2;
        g.setColor(new Color(255, 255, 255, 220));
        g.drawString(msg, x, y);
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Segoe UI", Font.BOLD, 50));
        String pauseText = "Jogo Pausado";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(pauseText);
        g.setColor(Color.WHITE);
        g.drawString(pauseText, (getWidth() - textWidth) / 2, getHeight() / 2 - 20);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        String resumeText = "Pressione ESC para Retomar";
        textWidth = g.getFontMetrics().stringWidth(resumeText);
        g.drawString(resumeText, (getWidth() - textWidth) / 2, getHeight() / 2 + 20);

        String menuText = "Pressione M para Voltar ao Menu";
        textWidth = g.getFontMetrics().stringWidth(menuText);
        g.drawString(menuText, (getWidth() - textWidth) / 2, getHeight() / 2 + 60);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (paused) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) togglePause();
            else if (e.getKeyCode() == KeyEvent.VK_M) quitToMenu();
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> leftPressed = true;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_ESCAPE                -> togglePause();
        }
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            gameTimer.stop();
            generateTimer.stop();
        } else {
            gameTimer.start();
            generateTimer.start();
            centerMessage = "";
        }
        repaint();
    }

    private void quitToMenu() {
        gameTimer.stop();
        generateTimer.stop();
        paused = false;
        menuPanel.updateScores(score);
        cardLayout.show(cards, "MENU");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> leftPressed = false;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightPressed = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (paused) return;
        playerX = e.getX() - PLAYER_WIDTH / 2;
        if (playerX < 0) playerX = 0;
        if (playerX > RecyClash.WIDTH - PLAYER_WIDTH)
            playerX = RecyClash.WIDTH - PLAYER_WIDTH;
    }

    @Override public void mouseDragged(MouseEvent e) { }
}
