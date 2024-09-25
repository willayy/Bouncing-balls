package src;

/*
 * 
 * A simple implementation of a Vector2d in 2D space that can be used to represent the position of a ball.
 * 
 * This Vector2d class accepts double values which is why we cant use the awt.Vector2d class.
 * 
 */
class Vector2d {
    
    protected double x;

    protected double y;
    
    protected Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    protected Vector2d(Vector2d p) {
        this.x = p.x;
        this.y = p.y;
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
