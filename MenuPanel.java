package RecyClash;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

class Trash {

    public enum SpeedType { SLOW, MEDIUM, FAST }

    private static final int SIZE = 40;

    private int x;
    private int y;
    private final boolean recyclable;
    private final SpeedType speedType;
    private final Color color;

    public Trash(int x, int y, boolean recyclable, SpeedType speedType) {
        this.x = x;
        this.y = y;
        this.recyclable = recyclable;
        this.speedType = speedType;
        this.color = determineColor(recyclable, speedType);
    }

    private Color determineColor(boolean recyclable, SpeedType type) {
        if (recyclable) {
            switch (type) {
                case SLOW:   return new Color(46, 204, 113);
                case MEDIUM: return new Color(39, 174, 96);
                case FAST:   return new Color(26, 188, 156);
            }
        } else {
            switch (type) {
                case SLOW:   return new Color(231, 76, 60);
                case MEDIUM: return new Color(192, 57, 43);
                case FAST:   return new Color(255, 165, 0);
            }
        }
        return recyclable ? Color.GREEN : Color.RED;
    }

    public void move(double speedMultiplier) {
        int speed = switch (speedType) {
            case SLOW   -> 3;
            case MEDIUM -> 5;
            case FAST   -> 7;
        };
        y += (int) (speed * speedMultiplier);
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval(x, y, SIZE, SIZE);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x, y, SIZE, SIZE);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public int getY() {
        return y;
    }

    public boolean isRecyclable() {
        return recyclable;
    }

    public SpeedType getSpeedType() {
        return speedType;
    }
}
