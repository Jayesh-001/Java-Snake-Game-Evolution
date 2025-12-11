package com.snakegame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1200;
    static final int SCREEN_HEIGHT = 800;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    
    boolean running = false;
    boolean inMenu = true; // Start in Menu mode
    
    Timer timer;
    Random random;
    
    // To store top scores to display
    ArrayList<String> topScores = new ArrayList<>();

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        // We do NOT call startGame() here anymore. We wait for user input.
    }

    public void startGame() {
        newApple();
        running = true;
        inMenu = false;
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        
        // Reset snake position
        for(int i=0; i<bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (inMenu) {
            drawMenu(g);
        } else if (running) {
            // Draw Apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw Snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // Draw Score
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            drawGameOver(g);
        }
    }

    public void drawMenu(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String text = "SNAKE GAME";
        g.drawString(text, (SCREEN_WIDTH - metrics.stringWidth(text)) / 2, SCREEN_HEIGHT / 3);

        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        String text2 = "Press SPACE to Start";
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString(text2, (SCREEN_WIDTH - metrics2.stringWidth(text2)) / 2, SCREEN_HEIGHT / 2);
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] = y[0] - UNIT_SIZE; break;
            case 'D': y[0] = y[0] + UNIT_SIZE; break;
            case 'L': x[0] = x[0] - UNIT_SIZE; break;
            case 'R': x[0] = x[0] + UNIT_SIZE; break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // checks borders
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            handleGameOver();
        }
    }
    
    public void handleGameOver() {
        // 1. Ask for name
        String name = JOptionPane.showInputDialog(this, "Game Over! Score: " + applesEaten + "\nEnter Name:");
        if (name == null || name.trim().isEmpty()) {
            name = "Anonymous";
        }
        
        // 2. Save to MySQL
        GameDatabase.saveScore(name, applesEaten);
        
        // 3. Update the Top 5 list from MySQL
        topScores = GameDatabase.getTopScores();
    }

    public void drawGameOver(Graphics g) {
        // Game Over Text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, 100);

        // Current Score
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        g.drawString("Your Score: " + applesEaten, (SCREEN_WIDTH - getFontMetrics(g.getFont()).stringWidth("Your Score: " + applesEaten)) / 2, 180);

        // Display Leaderboard
        g.setColor(Color.yellow);
        g.drawString("--- TOP 5 ---", (SCREEN_WIDTH - getFontMetrics(g.getFont()).stringWidth("--- TOP 5 ---")) / 2, 250);
        
        int yPos = 300;
        for (String s : topScores) {
            g.drawString(s, (SCREEN_WIDTH - getFontMetrics(g.getFont()).stringWidth(s)) / 2, yPos);
            yPos += 50;
        }

        // Restart Prompt
        g.setColor(Color.gray);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String msg = "Press SPACE to Restart";
        g.drawString(msg, (SCREEN_WIDTH - getFontMetrics(g.getFont()).stringWidth(msg)) / 2, SCREEN_HEIGHT - 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
                case KeyEvent.VK_SPACE:
                    // If in Menu or Game Over, SPACE starts the game
                    if (inMenu || !running) {
                        startGame();
                    }
                    break;
            }
        }
    }
}