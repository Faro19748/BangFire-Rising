import java.awt.Image;
import java.awt.Rectangle;

public class Obstacle {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;
    public boolean passed = false;
    public int dy;
    public int dx;

    public Obstacle(Image img) {
        this.img = img;
    }

    public Obstacle(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
