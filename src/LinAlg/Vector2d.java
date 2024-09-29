package src.linalg;

/*
 * 
 * A simple implementation of a Vector2d in 2D space that can be used to represent the position of a ball.
 * 
 * This Vector2d class accepts double values which is why we cant use the awt.Vector2d class.
 * 
 */
public class Vector2d {
    
    public double x;

    public double y;
    
    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2d(Vector2d p) {
        this.x = p.x;
        this.y = p.y;
    }

    // Normalize the vector.
    public void normalize() {
        
        double mag = LinAlg.vMag(this);
        
        x /= mag;
        
        y /= mag;
        
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    @Override
    public boolean equals(Object o) {

        if (o.getClass() != this.getClass()) {

            return false;

        } else {
            
            Vector2d p = (Vector2d) o;

            return this.x == p.x && this.y == p.y;

        }
        
    }
    
    @Override
    public int hashCode() {

        return (int) (x * 1000 + y);

    }

}
