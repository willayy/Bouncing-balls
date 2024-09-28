package src;
import src.LinAlg.Vector2d;
import src.LinAlg.LinAlg;

/**
 * The physics model.
 * 
 * This class is where you should implement your bouncing balls model.
 * 
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 * 
 * @author Simon Robillard
 *
 */
public class Model {

	// The gravitational acceleration consants on earth (specifically Sweden).
	private static final double G = 9.82;

	private double areaWidth, areaHeight;
	
	private Boolean ballClipping, gravity, debugInfo;

	public Ball [] balls;

	public Model(Ball[] balls, double width, double height, boolean ballClipping, boolean gravity, boolean debugInfo) {

		this.areaWidth = width;

		this.areaHeight = height;

		this.balls = balls;

		this.ballClipping = ballClipping;

		this.gravity = gravity;

		this.debugInfo = debugInfo;

	}

	public void step(double deltaT) {
		
		for (Ball b : balls) {

			if (debugInfo) {
				
				printDebugInfo(b);

			}

			// Save the old position of the ball. To fix clipping issues.
			double oldX = b.x;
			double oldY = b.y;

			// Update position by using eulers formula.
			applyEulersFormula(b, deltaT);

			if (!ballClipping) {
				
				// (Maybe) Morph the balls position if the ball is clipping another ball.
				applyAntiBallClipping(b);
				
			}

			// (Maybe) Morph y,x speed if the balls hits eachOther during this step.
			applyBallCollisions(b);
			
			// (Maybe) Morph y,x speed if the balls hits a wall during this step.
			applyWallCollisons(oldX, oldY, b);

			if (gravity) {

				// Morph y speed of ball by applying gravitational acceleration.
				applyGravity(b, deltaT);

			}

		}

	}

	// Prints debug information about the ball.
	private void printDebugInfo(Ball b) {

		String vx = String.format("%.4f", b.vx);

		String vy = String.format("%.4f", b.vy);

		String x = String.format("%.4f", b.x);

		String y = String.format("%.4f", b.y);

		System.out.printf("Ball %d: x=%s, y=%s, vx=%s, vy=%s %n", b.id, x, y, vx, vy);

	}

	// Applies anti-clipping to the ball, IFF the ball is clipping another ball.
	private void applyAntiBallClipping(Ball b) {
		
		for (Ball other: balls) {

			if (other == b) {
				continue;
			}

			double distance = euclideanDistance(b.x, b.y, other.x, other.y);

			double amountClipped = b.radius + other.radius - distance;

			// If the balls are clipping
			if (amountClipped > 0) {
				
				// Calculate the angle between the two balls by using the arctan function.
				double angle = angleBetween(b, other);

				// Calculate the amount of x and y to move the to make them touch.
				double dx = Math.cos(angle) * amountClipped / 2;
				double dy = Math.sin(angle) * amountClipped / 2;
				
				// Move the balls so that they are only touching.
				b.x -= dx;
				b.y -= dy;
				other.x += dx;
				other.y += dy;

			}

		}

	}

	// Applies the collision between two balls IFF they collide.
	private void applyBallCollisions(Ball b) {
		
		for (Ball other : balls) {

			// Skip the ball if it is the same as the ball we are currently checking.
			if (other == b) {
				continue;
			}

			double distance = euclideanDistance(b.x, b.y, other.x, other.y);

			// If the balls are colliding
			if (distance <= b.radius + other.radius + 0.001) {
				
				// Ball b

				Vector2d bV = new Vector2d(b.vx, b.vy);

				double m1 = 2 * other.mass / (b.mass + other.mass);

				Vector2d n1 = new Vector2d(b.x - other.x, b.y - other.y);

				Vector2d dv1 = new Vector2d(b.vx - other.vx, b.vy - other.vy);

				double dot1 = LinAlg.vDot(n1, dv1);

				double nMag1 = LinAlg.vMag(n1);

				Vector2d bVafter = LinAlg.vSub(bV, LinAlg.vMul(n1 ,m1 * dot1 / (nMag1 * nMag1)));

				// Ball other

				Vector2d oV = new Vector2d(other.vx, other.vy);

				double m2 = 2 * b.mass / (b.mass + other.mass);

				Vector2d n2 = new Vector2d(other.x - b.x, other.y - b.y);

				Vector2d dv2 = new Vector2d(other.vx - b.vx, other.vy - b.vy);

				double dot2 = LinAlg.vDot(n2, dv2);

				double nMag2 = LinAlg.vMag(n2);

				Vector2d oVafter = LinAlg.vSub(oV, LinAlg.vMul(n2 ,m2 * dot2 / (nMag2 * nMag2)));

				// Assign new velocities to the balls.

				b.vx = bVafter.x;

				b.vy = bVafter.y;

				other.vx = oVafter.x;

				other.vy = oVafter.y;

			}

		}

	}

	// Calculates the angle between two balls. The angle is a radian between -pi and pi
	private double angleBetween(Ball b, Ball other) {

		return Math.atan2(other.y-b.y,other.x-b.x);

	}

	// Applies the eulers formula to the ball. Morphs the position of the ball according to its speed.
	private void applyEulersFormula(Ball b, double deltaT) {

		// compute new position according to the speed of the ball
		b.x += deltaT * b.vx;
		b.y += deltaT * b.vy;

	}

	// Applies gravity to the vertical accelration of the ball
	private void applyGravity(Ball b, double deltaT) {

		// If the ball isnt at the floor gravitational de-acceleration should apply.
		if (b.y > b.radius) {

			b.vy -= deltaT * G;

		}

	}

	// Applies wall collisons, IFF the ball collides with the wall.
	private void applyWallCollisons(double oldX, double oldY, Ball b) {

		// detect collision with the border
		if (b.x <= b.radius || b.x >= areaWidth - b.radius) {

			b.x = oldX; // If the ball is outside the area, move it back to the old position.

			b.vx *= -1; // change direction of ball if the ball hits the left or right wall.
		}
		
		if (b.y <= b.radius || b.y >= areaHeight - b.radius) {

			b.y = oldY; // If the ball is outside the area, move it back to the old position.

			b.vy *= -1; // change direction of ball if the ball hits the upper or lower wall.

		}
		
	}

	// This method calculates the euclidean distance between two Vector2ds.
	private double euclideanDistance(double x1, double y1, double x2, double y2) {

		double dx = x1 - x2;

		double dy = y1 - y2;

		// Calculate the distance between two Vector2ds using the pythagorean theorem.
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}
	
}
