package src;
import src.linalg.LinAlg;
import src.linalg.Vector2d;

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
				
				/*
				* Our calculation of the transfer of velocity between two balls with (possibly)
				* different masses in 2d space is built on the formula for calulating the transfer of velocity
				* in 1d space which itself is derived from the conservation of momentum and kinetic energy.
				* 
				* The difference when you do it in 2d is simply that you find the velocity in the direction of the
				* normal vector (the vector between the center of the  two balls) and then just use the same formula
				* as in 1d space. The velocity in the direction of the tangent does not change since the balls dont exert
				* any force in that direction. 
				* 
				* We chose a linear algebra approach since it was more intative to us, linear algebra is also a very powerful
				* tool for solving problems in physics and computer science since the computer can work with vectors
				* very efficinetly (however this is a bit trivial in this assignment since we arent using hardware acceleration anyways).
				* Its worth noting that you can solve this problem using equivalently using trigonometry.
				*
				* We also want to say that we know this caluclation can be made much shorter and much more "single expression-y" but
				* we chose to make it more verbose to make it easier to understand and imrpove readability. Since this task
				* is about understanding how to model physics rather than maximizing performance we think this is a good tradeoff.
				*/

				// Velocity (before the collision) of the two balls as vectors.
				Vector2d u1 = new Vector2d(b.vx, b.vy);
				Vector2d u2 = new Vector2d(other.vx, other.vy);

				// Mass of the two balls.
				double m1 = b.mass;
				double m2 = other.mass;

				// Find the normal vector (the vector between the two balls).
				Vector2d n = new Vector2d(other.x - b.x, other.y - b.y);

				// Normalize the normal vector to the normal unit vector.
				n.normalize();

				// Find the tangent vector, this is easily done by rotating the normal vector 90 degrees.
				Vector2d t = new Vector2d(-n.y, n.x);

				// Find the scalar velocity in the normal direction.
				double u1n = LinAlg.vDot(u1, n);
				double u2n = LinAlg.vDot(u2, n);

				// Find the scalar velocity in the tangent direction.
				double u1t = LinAlg.vDot(u1, t);
				double u2t = LinAlg.vDot(u2, t);

				// The scalar velocity in the tangent direction does not change.
				double v1t = u1t;
				double v2t = u2t;

				/* Find the scalar velocity in the normal direction after the
				collision using the formula for velocity transfer in 1d space. */
				
				double v1n = velocityAfterCollision(u1n, u2n, m1, m2);
				double v2n = velocityAfterCollision(u2n, u1n, m2, m1);

				/* Now that we have our scalar velocities we can use them to scale the 
				* normal and tangent vectors to get the final velocity vectors. In the 
				* normal and tangent directions */

				Vector2d v1nVector = LinAlg.vMul(n, v1n);
				Vector2d v1tVector = LinAlg.vMul(t, v1t);
				Vector2d v2nVector = LinAlg.vMul(n, v2n);
				Vector2d v2tVector = LinAlg.vMul(t, v2t);

				// Now add the normal and tangent vectors to get a single total velocity vector for each ball.
				Vector2d v1 = LinAlg.vAdd(v1nVector, v1tVector);
				Vector2d v2 = LinAlg.vAdd(v2nVector, v2tVector);

				// Set the new velocities of the balls.
				b.vx = v1.x;
				b.vy = v1.y;
				other.vx = v2.x;
				other.vy = v2.y;
				
			}

		}

	}


	// Calculates the velocity of a ball after a collision with another ball in 1d space.
	private double velocityAfterCollision(double u1, double u2, double m1, double m2) {

		return (u1 * (m1 - m2) + 2 * m2 * u2) / (m1 + m2);

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
