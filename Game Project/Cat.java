import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Cat {
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean isRight;

    public Cat(int x, int y, int width, int height, boolean isRight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isRight = isRight;
    }

    public void move() {
        x += isRight ? 3 : -3;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g, Image leftCat, Image rightCat) {
        g.drawImage(isRight ? rightCat : leftCat, x, y, width, height, null);
    }
}
