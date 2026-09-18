import java.awt.Image;
import java.awt.Rectangle;

public class FireWork {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;

    public FireWork(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void reset(int x, int y, Image img) {
        this.x = x;
        this.y = y;
        this.img = img;
    }
}
