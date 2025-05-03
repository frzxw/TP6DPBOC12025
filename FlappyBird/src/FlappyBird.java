import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int frameWidth = 360;
    int frameHeight = 640;

    Image backgroundImage, birdImage, lowerPipeImage, upperPipeImage;
    Player player;
    ArrayList<Pipe> pipes = new ArrayList<>();

    Timer gameLoop;
    Timer pipeSpawner;

    int gravity = 1;
    boolean gameStarted = false;
    boolean gameOver = false;
    boolean paused = false;
    int score = 0;

    int pipeWidth = 64;
    int pipeHeight = 512;
    int baseVelocityX = -4; // Kecepatan dasar pipa

    private boolean showScoreAnimation = false;
    private int scoreAnimationTimer = 0;

    public FlappyBird() {
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.blue);

        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        player = new Player(frameWidth / 8, frameHeight / 2, 34, 24, birdImage);

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        pipeSpawner = new Timer(1500, e -> placePipes());
        pipeSpawner.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Score: " + score, 10, 30);
        
        // Menampilkan animasi skor
        if (showScoreAnimation) {
            g.setColor(new Color(255, 215, 0)); // Warna emas
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("+1", player.getPosX() + 40, player.getPosY() - 10);
        }

        if (!gameStarted) {
            g.setColor(new Color(0, 0, 0, 120)); // Hitam semi-transparan
            g.fillRect(0, 0, frameWidth, frameHeight);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Flappy Bird", 85, frameHeight / 3);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Tekan SPACE untuk terbang", 70, frameHeight / 2);
            g.drawString("P untuk jeda/lanjut", 100, frameHeight / 2 + 30);
            g.drawString("Tekan SPACE untuk mulai", 70, frameHeight / 2 + 80);
        }

        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Game Over", 60, frameHeight / 2);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Tekan R untuk Restart", 60, frameHeight / 2 + 50);
        }
        
        if (paused && !gameOver) {
            g.setColor(new Color(0, 0, 0, 150)); // Hitam semi-transparan
            g.fillRect(0, 0, frameWidth, frameHeight);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("JEDA", 120, frameHeight / 2);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Tekan P untuk Lanjut", 80, frameHeight / 2 + 50);
        }
    }

    public void move() {
        if (!gameOver) {
            player.setVelocityY(player.getVelocityY() + gravity);
            player.setPosY(player.getPosY() + player.getVelocityY());

            // Game over jika pemain menyentuh dasar layar
            if (player.getPosY() > frameHeight - player.getHeight()) {
                gameOver = true;
                gameLoop.stop();
                pipeSpawner.stop();
            }
            
            // Mencegah pemain terbang melewati batas atas layar
            if (player.getPosY() < 0) {
                player.setPosY(0);
                player.setVelocityY(0);
            }

            // Gunakan iterator untuk menghapus pipa yang sudah keluar layar dengan aman
            ArrayList<Pipe> pipesToRemove = new ArrayList<>();
            for (Pipe pipe : pipes) {
                pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

                // Hapus pipa yang sudah keluar layar
                if (pipe.getPosX() + pipe.getWidth() < 0) {
                    pipesToRemove.add(pipe);
                }

                // Tambah skor hanya untuk upperPipe dengan umpan balik visual
                if (!pipe.passed && pipe.getImage() == upperPipeImage &&
                        pipe.getPosX() + pipe.getWidth() < player.getPosX()) {
                    pipe.passed = true;
                    score += 1;
                    showScoreAnimation = true;
                    scoreAnimationTimer = 20; // Tampilkan animasi selama 20 frame
                    System.out.println("Skor: " + score); // cetak debug opsional
                }

                if (isColliding(player, pipe)) {
                    gameOver = true;
                    gameLoop.stop();
                    pipeSpawner.stop();
                }
            }
            
            // Hapus pipa yang sudah keluar layar
            pipes.removeAll(pipesToRemove);

            // Perbarui timer animasi skor
            if (showScoreAnimation) {
                scoreAnimationTimer--;
                if (scoreAnimationTimer <= 0) {
                    showScoreAnimation = false;
                }
            }
        }
    }

    public void placePipes() {
        int gap = 150;
        
        // Randomisasi ketinggian pipa yang lebih baik - hindari menempatkan pipa terlalu tinggi atau terlalu rendah
        int minY = -pipeHeight + 50; // Posisi minimum (memastikan sebagian pipa terlihat)
        int maxY = -150; // Posisi maksimum (memastikan cukup ruang di bawah)
        int randomY = minY + (int)(Math.random() * (maxY - minY));
        
        // Hitung kecepatan berdasarkan skor (meningkat setiap 5 poin)
        int currentVelocity = baseVelocityX - (score / 5);
        
        // Batasi kecepatan maksimum untuk mencegah gameplay yang mustahil
        if (currentVelocity < -10) {
            currentVelocity = -10;
        }
        
        Pipe top = new Pipe(frameWidth, randomY, pipeWidth, pipeHeight, upperPipeImage);
        top.setVelocityX(currentVelocity);
        
        Pipe bottom = new Pipe(frameWidth, randomY + pipeHeight + gap, pipeWidth, pipeHeight, lowerPipeImage);
        bottom.setVelocityX(currentVelocity);

        pipes.add(top);
        pipes.add(bottom);
    }

    public boolean isColliding(Player p, Pipe pipe) {
        Rectangle r1 = new Rectangle(p.getPosX(), p.getPosY(), p.getWidth(), p.getHeight());
        Rectangle r2 = new Rectangle(pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight());
        return r1.intersects(r2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && gameStarted) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
            } else if (!gameOver && !paused) {
                player.setVelocityY(-10);
            }
        }
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
        }
        if (e.getKeyCode() == KeyEvent.VK_P && !gameOver && gameStarted) {
            paused = !paused;
            if (paused) {
                pipeSpawner.stop();
            } else {
                pipeSpawner.start();
            }
        }
    }

    public void resetGame() {
        player.setPosY(frameHeight / 2);
        player.setVelocityY(0);
        pipes.clear();
        gameOver = false;
        gameStarted = false;
        score = 0;
        gameLoop.start();
        pipeSpawner.start();
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}