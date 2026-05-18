package RecyClash;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

class MenuPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel cards;
    private final JLabel lblScore;
    private final JLabel lblRecord;
    private int lastScore;
    private int recordScore;

    public MenuPanel(CardLayout cl, JPanel cards) {
        this.lastScore = 0;
        this.recordScore = 0;
        this.cardLayout = cl;
        this.cards = cards;

        setBackground(new Color(18, 32, 47));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(80));

        JLabel title = new JLabel("RecyClash");
        title.setFont(new Font("Segoe UI", Font.BOLD, 70));
        title.setForeground(new Color(236, 240, 241));
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(40));

        lblScore = new JLabel("Última Pontuação: 0");
        lblScore.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        lblScore.setForeground(Color.WHITE);
        lblScore.setAlignmentX(CENTER_ALIGNMENT);
        add(lblScore);

        lblRecord = new JLabel("Recorde: 0");
        lblRecord.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        lblRecord.setForeground(new Color(241, 196, 15));
        lblRecord.setAlignmentX(CENTER_ALIGNMENT);
        add(lblRecord);

        add(Box.createVerticalStrut(40));

        JButton btnPlay = createButton("Jogar");
        btnPlay.setAlignmentX(CENTER_ALIGNMENT);
        btnPlay.addActionListener(e -> {
            cardLayout.show(this.cards, "GAME");
            GamePanel gamePanel = (GamePanel) this.cards.getComponent(1);
            gamePanel.startGame();
            gamePanel.requestFocusInWindow();
        });
        add(btnPlay);

        add(Box.createVerticalStrut(20));

        JButton btnExit = createButton("Sair");
        btnExit.setAlignmentX(CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));
        add(btnExit);
    }

    public void updateScores(int lastScore) {
        this.lastScore = lastScore;
        if (lastScore > recordScore) {
            recordScore = lastScore;
        }
        lblScore.setText("Última Pontuação: " + lastScore);
        lblRecord.setText("Recorde: " + recordScore);
    }

    private JButton createButton(String text) {
        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(41, 128, 185));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(250, 60));

        // efeito hover
        final Color normal = new Color(41, 128, 185);
        final Color hover  = new Color(52, 152, 219);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(normal); }
        });

        return btn;
    }
}
