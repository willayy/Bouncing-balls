package src.LinAlg;

// Linear algebra utility class.
public class LinAlg {
    
    // Hide implicit public constructor.
    private LinAlg() {
        
    }

    public static double vDot(Vector2d v1, Vector2d v2) {

		return v1.x * v2.x + v1.y * v2.y;

	}

	public static double vMag(Vector2d v) {

		return Math.sqrt(v.x * v.x + v.y * v.y);

	}

	public static Vector2d vMul(Vector2d v, double d) {

		return new Vector2d(v.x * d, v.y * d);

	}

	public static Vector2d vSub(Vector2d v1, Vector2d v2) {

		return new Vector2d(v1.x - v2.x, v1.y - v2.y);

	}
	

}
