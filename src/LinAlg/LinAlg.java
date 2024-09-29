package src.linalg;

// Linear algebra utility class.
public class LinAlg {
    
    // Hide implicit public constructor.
    private LinAlg() {
        
    }

	// This method calculates the euclidean distance between two Vector2ds.
	public static double euclideanDistance(double x1, double y1, double x2, double y2) {

		double dx = x1 - x2;

		double dy = y1 - y2;

		// Calculate the distance between two Vector2ds using the pythagorean theorem.
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}


	// the dot product of two vectors.
    public static double vDot(Vector2d v1, Vector2d v2) {

		return v1.x * v2.x + v1.y * v2.y;

	}

	// The magnitude of a vector.	
	public static double vMag(Vector2d v) {

		return Math.sqrt(v.x * v.x + v.y * v.y);

	}

	// Multiply a vector by a scalar.
	public static Vector2d vMul(Vector2d v, double d) {

		return new Vector2d(v.x * d, v.y * d);

	}

	// Subtract a vector from another vector, return .
	public static Vector2d vSub(Vector2d v1, Vector2d v2) {

		return new Vector2d(v1.x - v2.x, v1.y - v2.y);

	}

	// Add two vectors together.
	public static Vector2d vAdd(Vector2d v1, Vector2d v2) {

		return new Vector2d(v1.x + v2.x, v1.y + v2.y);

	}
	

}
