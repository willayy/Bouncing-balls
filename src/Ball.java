package src;

/**
 * Simple class describing balls. We moved this class to its own file to encapsulate it's logic and to separate concerns.
 */
public class Ball {
    
    Ball(double x, double y, double vx, double vy, double r) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = r;
    }

    /**
     * Position, speed, and radius of the ball. You may wish to add other attributes.
     */
    double x, y, vx, vy, radius;

}
