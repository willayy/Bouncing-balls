package src;

import java.text.DecimalFormat;

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

				System.out.println(distance);
				// Calculate the angle between the two balls.
				double angle = angleBetween(b, other);

				System.out.println(
					"angle between balls: " + angle
				);
				// The mass of the balls.
				double m1 = b.mass;
				double m2 = other.mass;

				// Our initial velocities in vector format.
				Vector2d v1 = new Vector2d(b.vx, b.vy);
				Vector2d v2 = new Vector2d(other.vx, other.vy);
				
				/* Rotate the vectors so they are parallel (x) and orthogonal (y) to the
				axle of collision. This is equivalent to making the vectors (vx1, vy1) and (vx2, vy2)
				out of the balls x and y velocities and rotating them clockwise using the rotation matrix. */

				System.out.println(b.vx);
				System.out.println(b.vy);
				System.out.println(other.vx);
				System.out.println(other.vy);
				System.out.println("-----------------");

				rotateVector(v1, angle);
				rotateVector(v2, angle);
				
				// Calculate the new velocities of the balls after the collision.
				computeVelocityTransfer(v1, v2, m1, m2);

				// Rotate the vectors back to the original positions.
				rotateVector(v1, angle);
				rotateVector(v2, angle);
				
				// Assign new velocities to the balls.
				b.vx = v1.x;
				b.vy = v1.y;
				other.vx = v2.x;
				other.vy = v2.y;
				System.out.println(b.vx);
				System.out.println(b.vy);
				System.out.println(other.vx);
				System.out.println(other.vy);
				System.out.println("-----------------");

			}

		}

	}

	// Computes the velocity transfer between two balls after a collision with velocity v1 and v2 (vectors) and mass m1 and m2.
	private void computeVelocityTransfer(Vector2d v1, Vector2d v2, double m1, double m2) {

		/*Total momentum before the collision. These could just be baked in to the calculation below,
		but this improves readability. */
		double ix = m1 * v1.x + m2 * v2.x;
		double iy = m1 * v1.y + m2 * v2.y;

		/* Relative velocity before the collision. These could just be baked in to the calculation below,
		but this improves readability. */
		double rx = v2.x - v1.x;
		double ry = v2.y - v1.y;
		
		/* Since relative velocity and total momentum is the same after the collision (because its fully elastic),
		we can calculate the new velocities by using this formula we aquired from the system of equations
		given by R and I */
		System.out.println(v1.x);
		System.out.println(v1.y);
		System.out.println(v2.x);
		System.out.println(v2.y);

		double v1x = v1.x;
		double v1y = v1.y;
		double v2x = v2.x;
		double v2y = v2.y;

        v1.x = ((m1 - m2)/(m1 + m2))* v1x + (2 * m2 / (m1 + m2)) * v2x;
        v1.y = ((m1 - m2)/(m1 + m2))* v1x + (2 * m2 / (m1 + m2)) * v2y;
        v2.x = (2 * m1 / (m1 + m2)) * v1x - ((m1 - m2)/(m1 + m2)) * v2x;	
        v2.y = (2 * m1 / (m1 + m2)) * v1y - ((m1 - m2)/(m1 + m2)) * v2y;


		System.out.println("the velocitie after calc but before rotation");
		System.out.println(v1.x);
		System.out.println(v1.y);
		System.out.println(v2.x);
		System.out.println(v2.y);
		System.out.println("-----------------");

	}

	// Rotates a vector by a given angle in radians. This is equivalent to multiplying the vector with a rotation matrix.
	private void rotateVector(Vector2d v, double angle) {

		double x = v.x;

		double y = v.y;

		v.x = x * Math.cos(angle) + y * Math.sin(angle);

		v.y = - x * Math.sin(angle) + y * Math.cos(angle);

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
