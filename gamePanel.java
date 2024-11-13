import java.awt.event.*;

import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.util.Arrays;

public class gamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNIT = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE;
    static final int DELAY = 300;
    final int x[] = new int[GAME_UNIT];
    final int y[] = new int[GAME_UNIT];

    int bodyparts = 6;
    int applesEaten;
    int applex;
    int appley;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    JButton restartButton;

    gamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MykeyAdapter());
        StartGame();
    }

    public void StartGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        removeRestartButton();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Corrected method call
        draw(g);
    }

    public void newApple() {
        // Implement logic to place a new apple on the screen
        applex = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appley = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(applex, appley, UNIT_SIZE, UNIT_SIZE);
            for (int i = 0; i < bodyparts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                g.setColor(Color.red);
                g.setFont(new Font("INK FREE", Font.BOLD, 20));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                        g.getFont().getSize());
            }
        } else {
            gameover(g);
        }
    }

    public void move() {
        // Implement movement logic for the snake
        for (int i = bodyparts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            default:
                break;
        }
    }

    public void checkApple() {
        // Implement logic to check if the snake has eaten an apple
        if ((x[0] == applex) && (y[0] == appley)) {
            bodyparts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // Implement collision detection logic

        // this checks if head collides with body
        for (int i = bodyparts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // this checks if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        // top border
        if (y[0] < 0) {
            running = false;
        }
        // bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameover(Graphics g) {
        // To display Score
        g.setColor(Color.BLUE);
        g.setFont(new Font("INK FREE", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2,
                g.getFont().getSize());
        // Game over text
        g.setColor(Color.red);
        g.setFont(new Font("INK FREE", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (SCREEN_WIDTH - metrics2.stringWidth("GAME OVER")) / 2, SCREEN_HEIGHT / 2);

        addRestartButton();
    }

    private void addRestartButton() {
        if (restartButton == null) {
            restartButton = new JButton("Restart") {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.WHITE);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("INK FREE", Font.BOLD, 20));
                    FontMetrics metrics = g.getFontMetrics(g.getFont());
                    int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                    int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                    g.drawString(getText(), x, y);
                }
            };
            restartButton.setFont(new Font("INK FREE", Font.BOLD, 20));
            restartButton.setFocusable(false);
            restartButton.setBounds((SCREEN_WIDTH - 150) / 2, SCREEN_HEIGHT / 2 + 50, 150, 50);
            restartButton.setContentAreaFilled(false);
            restartButton.setBorderPainted(false);
            restartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeRestartButton();
                    applesEaten = 0;
                    bodyparts = 6;
                    direction = 'R';
                    Arrays.fill(x, 0);
                    Arrays.fill(y, 0);
                    StartGame();
                    repaint();
                }
            });
        }
        this.add(restartButton);
        this.setLayout(null);
    }

    private void removeRestartButton() {
        if (restartButton != null) {
            this.remove(restartButton);
            this.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Implement action that occurs on each Timer tick
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MykeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // Implement logic to handle key presses for snake movement
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    // Entry point to start the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        gamePanel panel = new gamePanel();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
