import java.awt.*;

public class Player {
    private int posX, posY, width, height, velocityY;
    private Image image;

    public Player(int posX, int posY, int width, int height, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;
        this.velocityY = 0;
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getVelocityY() { return velocityY; }
    public Image getImage() { return image; }

    public void setPosX(int posX) { this.posX = posX; }
    public void setPosY(int posY) { this.posY = posY; }
    public void setVelocityY(int velocityY) { this.velocityY = velocityY; }
}