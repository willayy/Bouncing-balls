package src;

/*
 * 
 * A simple implementation of a point in 2D space that can be used to represent the position of a ball.
 * 
 * This Point class accepts double values which is why we cant use the awt.Point class.
 * 
 */
public class Point {
    
    public double x;
    public double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }
    
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    @Override
    public boolean equals(Object o) {

        if (o.getClass() != this.getClass()) {

            return false;

        } else {
            
            Point p = (Point) o;

            return this.x == p.x && this.y == p.y;

        }
        
    }
    
    @Override
    public int hashCode() {

        return (int) (x * 1000 + y);

    }

}
