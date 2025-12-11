package com.snakegame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;             // Import for File
import javax.sound.sampled.*;    // Import for Sound

public class GamePanel extends JPanel implements ActionListener {

    // ... (All your existing variables remain the same) ...
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
    boolean inMenu = true;
    Timer timer;
    Random random;
    ArrayList<String> topScores = new ArrayList<>();

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    }

    // ... (startGame, paintComponent, draw, drawMenu, newApple, move methods are same) ...
    // ... PASTE YOUR EXISTING METHODS HERE ...
    
    // I am only writing the StartGame/Paint etc to save space, 
    // BUT DO NOT DELETE THEM from your code!
    
    public void startGame() {
        newApple();
        running = true;
        inMenu = false;
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        for(int i=0; i<bodyParts; i++) { x[i] = 0; y[i] = 0; }
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    public void paintComponent(Graphics g) { super.paintComponent(g); draw(g); }

    public void draw(Graphics g) {
        if (inMenu) drawMenu(g);
        else if (running) {
             g.setColor(Color.red);
             g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
             for (int i = 0; i < bodyParts; i++) {
                 if (i == 0) { g.setColor(Color.green); g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); }
                 else { g.setColor(new Color(45, 180, 0)); g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); }
             }
             g.setColor(Color.white);
             g.setFont(new Font("Ink Free", Font.BOLD, 40));
             FontMetrics metrics = getFontMetrics(g.getFont());
             g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else drawGameOver(g);
    }
    
    public void drawMenu(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        g.drawString("Press SPACE to Start", 400, 400); 
    }
    
    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) { x[i] = x[i - 1]; y[i] = y[i - 1]; }
        switch (direction) {
            case 'U': y[0] = y[0] - UNIT_SIZE; break;
            case 'D': y[0] = y[0] + UNIT_SIZE; break;
            case 'L': x[0] = x[0] - UNIT_SIZE; break;
            case 'R': x[0] = x[0] + UNIT_SIZE; break;
        }
    }

    // --- HERE IS THE MODIFIED METHOD ---
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            
            // NEW: Play Sound!
            playSound("eat.wav");
            
            newApple();
        }
    }
    
    // --- NEW HELPER METHOD FOR SOUND ---
    public void playSound(String soundFileName) {
        try {
            // Open an audio input stream.
            File soundFile = new File(soundFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
            
        } catch (Exception e) {
            // If file is not found, we just ignore it so the game doesn't crash
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    // ... (checkCollisions, handleGameOver, drawGameOver, actionPerformed, KeyAdapter remain the same) ...
    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        
        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // Check if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // Check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            
            // NEW: Play the death sound BEFORE the popup appears
            playSound("die.wav");
            
            handleGameOver();
        }
    }
    public void handleGameOver() {
        String name = JOptionPane.showInputDialog(this, "Game Over! Score: " + applesEaten + "\nEnter Name:");
        if (name == null || name.trim().isEmpty()) name = "Anonymous";
        GameDatabase.saveScore(name, applesEaten);
        topScores = GameDatabase.getTopScores();
    }
    
    public void drawGameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 70));
        g.drawString("Game Over", 400, 100);
        // ... (rest of your drawGameOver logic)
        int yPos = 250;
        for (String s : topScores) { g.drawString(s, 350, yPos); yPos += 55; }
        g.drawString("Press SPACE to Restart", 200, 600);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) { move(); checkApple(); checkCollisions(); }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
                case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
                case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
                case KeyEvent.VK_SPACE: if (inMenu || !running) startGame(); break;
            }
        }
    }
}