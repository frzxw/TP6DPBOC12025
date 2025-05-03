import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu extends JFrame {
    private int menuWidth = 360;
    private int menuHeight = 640;
    private Image backgroundImage;

    public Menu() {
        // Setup JFrame
        this.setTitle("Flappy Bird Menu");
        this.setSize(menuWidth, menuHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        // Load background image
        try {
            backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
            backgroundImage = null;
        }

        // Create main panel with custom painting
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw background
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, menuWidth, menuHeight, null);
                } else {
                    g.setColor(new Color(135, 206, 250)); // Light sky blue if image not available
                    g.fillRect(0, 0, menuWidth, menuHeight);
                }

                // Draw title
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 42));
                FontMetrics fm = g.getFontMetrics();
                String title = "Flappy Bird";
                int titleX = (menuWidth - fm.stringWidth(title)) / 2;
                g.drawString(title, titleX, 150);
            }
        };
        mainPanel.setLayout(null); // Using null layout for absolute positioning

        // Create buttons
        JButton startButton = createButton("Start Game", menuWidth / 2 - 100, 250);
        JButton exitButton = createButton("Exit", menuWidth / 2 - 100, 330);

        // Add action listeners
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the menu
                startGame();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit application
            }
        });

        // Add buttons to panel
        mainPanel.add(startButton);
        mainPanel.add(exitButton);

        // Add panel to frame
        this.add(mainPanel);
        this.setVisible(true);
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 50);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setBackground(new Color(252, 216, 85)); // Gold color
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 182, 24)); // Darker gold on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(252, 216, 85)); // Back to normal
            }
        });

        return button;
    }

    private void startGame() {
        // Create and display the game using App
        new App();
    }
}