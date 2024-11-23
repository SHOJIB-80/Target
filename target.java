import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ShootingGame extends JPanel implements ActionListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int PLAYER_SIZE = 50;
    private final int ENEMY_SIZE = 30;
    private final int BULLET_SIZE = 10;
    private final int BULLET_SPEED = 5;
    private final int ENEMY_SPEED = 2;
    private final int PLAYER_SPEED = 5;
    private final int NUM_ENEMIES = 10;
    private final int MAX_LIVES = 3;
    private final int FPS = 60;

    private Timer timer;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private int score;
    private int lives;

    public ShootingGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e);
            }
        });

        player = new Player(WIDTH / 2, HEIGHT - 100);
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        score = 0;
        lives = MAX_LIVES;

        for (int i = 0; i < NUM_ENEMIES; i++) {
            enemies.add(new Enemy(Math.random() * (WIDTH - ENEMY_SIZE), Math.random() * (HEIGHT - ENEMY_SIZE)));
        }

        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.move();
        for (Bullet bullet : bullets) {
            bullet.move();
        }
        for (Bullet bullet : bullets) {
            for (int i = 0; i < enemies.size(); i++) {
                if (enemies.get(i).isHit(bullet)) {
                    enemies.remove(i);
                    score++;
                    bullets.remove(bullet);
                    break;
                }
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.move();
            if (enemy.isOutOfBounds()) {
                enemies.remove(i);
                lives--;
            }
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ShootingGame());
        frame.pack();
        frame.setVisible(true);
    }

    private class Player {
        private int x;
        private int y;
        private boolean shooting;
        private boolean moveLeft, moveRight, moveUp, moveDown;

        public Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, PLAYER_SIZE, PLAYER_SIZE);
        }

        public void move() {
            if (moveLeft && x > 0) {
                x -= PLAYER_SPEED;
            }
            if (moveRight && x < WIDTH - PLAYER_SIZE) {
                x += PLAYER_SPEED;
            }
            if (moveUp && y > 0) {
                y -= PLAYER_SPEED;
            }
            if (moveDown && y < HEIGHT - PLAYER_SIZE) {
                y += PLAYER_SPEED;
            }

            if (shooting) {
                if (bullets.size() < 100) {
                    bullets.add(new Bullet(x + PLAYER_SIZE / 2 - BULLET_SIZE / 2, y));
                }
            }
        }

        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_LEFT) {
                moveLeft = true;
            } else if (code == KeyEvent.VK_RIGHT) {
                moveRight = true;
            } else if (code == KeyEvent.VK_UP) {
                moveUp = true;
            } else if (code == KeyEvent.VK_DOWN) {
                moveDown = true;
            } else if (code == KeyEvent.VK_SPACE) {
                shooting = true;
            }
        }

        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_LEFT) {
                moveLeft = false;
            } else if (code == KeyEvent.VK_RIGHT) {
                moveRight = false;
            } else if (code == KeyEvent.VK_UP) {
                moveUp = false;
            } else if (code == KeyEvent.VK_DOWN) {
                moveDown = false;
            } else if (code == KeyEvent.VK_SPACE) {
                shooting = false;
            }
        }
    }

    private class Enemy {
        private int x;
        private int y;

        public Enemy(double x, double y) {
            this.x = (int) x;
            this.y = (int) y;
        }

        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillRect(x, y, ENEMY_SIZE, ENEMY_SIZE);
        }

        public void move() {
            x -= ENEMY_SPEED;
            if (x < 0) {
                x = WIDTH - ENEMY_SIZE;
            }
        }

        public boolean isHit(Bullet bullet) {
            return bullet.getX() >= x && bullet.getX() <= x + ENEMY_SIZE && bullet.getY() >= y && bullet.getY() <= y + ENEMY_SIZE;
        }

        public boolean isOutOfBounds() {
            return x < 0;
        }
    }

    private class Bullet {
        private int x;
        private int y;

        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void draw(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, BULLET_SIZE, BULLET_SIZE);
        }

        public void move() {
            y -= BULLET_SPEED;
        }

        public boolean isOutOfBounds() {
            return y < 0;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
