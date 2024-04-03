package Classic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create the main frame
                JFrame frame = new JFrame("Snake Game");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 450); 
                frame.setLocationRelativeTo(null);

                // Create the game panel
                GamePanel gamePanel = new GamePanel();
                frame.add(gamePanel);

                frame.setVisible(true);
            }
        });
    }

    static class GamePanel extends JPanel implements ActionListener {
        private final int SCREEN_WIDTH = 400;
        private final int SCREEN_HEIGHT = 400;
        private final int UNIT_SIZE = 20;
        private final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
        private int snakeBodyParts = 3; 
        private char direction = 'R';
        private int score = 0; 
        private int delay = 200; 
        private int[] snakeX = new int[GAME_UNITS];
        private int[] snakeY = new int[GAME_UNITS];
        private int foodX;
        private int foodY;
        private Color foodColor;
        private boolean inGame = true;
        private Timer timer;

        public GamePanel() {
            // Set panel dimensions and appearance
            this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            this.setBackground(Color.black);
            this.setFocusable(true);
            this.addKeyListener(new MyKeyAdapter());
            startGame();
        }

        public void startGame() {
            // Initialize snake position and food placement
            for (int i = 0; i < snakeBodyParts; i++) {
                snakeX[i] = 100 - i * UNIT_SIZE;
                snakeY[i] = 100;
            }
            placeFood();
            timer = new Timer(delay, this);
            timer.start();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (inGame) {
                // Draw snake and food
                draw(g);
                drawScore(g); 
            } else {
                // Draw game over message
                gameOver(g);
            }
        }

        public void draw(Graphics g) {
            // Draw food
            g.setColor(foodColor);
            g.fillRect(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake body
            for (int i = 0; i < snakeBodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.pink); 
                } else {
                    g.setColor(Color.white); 
                }
                g.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
            }
        }

        public void drawScore(Graphics g) {
            // Draw score on the screen
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);
        }

        public void move() {
            // Move the snake
            for (int i = snakeBodyParts; i > 0; i--) {
                snakeX[i] = snakeX[i - 1];
                snakeY[i] = snakeY[i - 1];
            }
            switch (direction) {
                case 'U':
                    snakeY[0] -= UNIT_SIZE;
                    break;
                case 'D':
                    snakeY[0] += UNIT_SIZE;
                    break;
                case 'L':
                    snakeX[0] -= UNIT_SIZE;
                    break;
                case 'R':
                    snakeX[0] += UNIT_SIZE;
                    break;
            }
            checkBorderCollision();
            checkFoodCollision();
            checkSelfCollision(); 
        }

        public void placeFood() {
            // Place food randomly on the screen
            int cols = SCREEN_WIDTH / UNIT_SIZE;
            int rows = SCREEN_HEIGHT / UNIT_SIZE;
            double rand = Math.random();
            if (score < 100) {
                foodColor = Color.yellow;
                foodX = (int) (Math.random() * cols) * UNIT_SIZE;
                foodY = (int) (Math.random() * rows) * UNIT_SIZE;
            } else if (score >= 100 && score < 300) {
                foodColor = Color.blue;
                foodX = (int) (Math.random() * cols) * UNIT_SIZE;
                foodY = (int) (Math.random() * rows) * UNIT_SIZE;
            } else {
                foodColor = Color.red;
                foodX = (int) (Math.random() * cols) * UNIT_SIZE;
                foodY = (int) (Math.random() * rows) * UNIT_SIZE;
            }
        }

        public void checkBorderCollision() {
            // Check if snake hits the border
            if (snakeX[0] < 0) snakeX[0] = SCREEN_WIDTH - UNIT_SIZE; 
            if (snakeX[0] >= SCREEN_WIDTH) snakeX[0] = 0; 
            if (snakeY[0] < 0) snakeY[0] = SCREEN_HEIGHT - UNIT_SIZE; 
            if (snakeY[0] >= SCREEN_HEIGHT) snakeY[0] = 0; 
        }

        public void checkSelfCollision() {
            // Check if snake collides with itself
            for (int i = snakeBodyParts; i > 0; i--) {
                if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                    inGame = false; 
                }
            }
        }

        public void checkFoodCollision() {
            // Check if snake eats the food
            if (snakeX[0] == foodX && snakeY[0] == foodY) {
                snakeBodyParts++;
                if (foodColor.equals(Color.yellow)) {
                    score += 10;
                } else if (foodColor.equals(Color.blue)) {
                    score += 20;
                } else {
                    score += 50;
                }
                updateDelay(); 
                placeFood();
            }
        }

        public void updateDelay() {
            // Update game speed based on score
            if (score >= 100 && score < 300) {
                delay = 160; 
            } else if (score >= 300) {
                delay = 120; 
            }
            timer.setDelay(delay); 
        }

        public void actionPerformed(ActionEvent e) {
            if (inGame) {
                move();
                repaint();
            }
        }

        private void gameOver(Graphics g) {
            // Display game over message
            String msg = "Game Over";
            Font font = new Font("Arial", Font.BOLD, 20);
            FontMetrics metrics = getFontMetrics(font);

            g.setColor(Color.white);
            g.setFont(font);
            g.drawString(msg, (SCREEN_WIDTH - metrics.stringWidth(msg)) / 2, SCREEN_HEIGHT / 2);
            
            String scoreMsg = "Score: " + score;
            g.drawString(scoreMsg, (SCREEN_WIDTH - metrics.stringWidth(scoreMsg)) / 2, SCREEN_HEIGHT / 2 + 30);
        }

        private class MyKeyAdapter extends KeyAdapter {
            public void keyPressed(KeyEvent e) {
                // Handle keyboard input to change snake direction
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R')
                            direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L')
                            direction = 'R';
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D')
                            direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U')
                            direction = 'D';
                        break;
                }
            }
        }
    }
}
