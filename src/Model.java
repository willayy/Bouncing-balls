package src;

import java.util.ArrayList;

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
	
	private Boolean antiClipping, gravity;

	public Ball [] balls;

	public Model(Ball[] balls, double width, double height, boolean antiClipping, boolean gravity) {

		this.areaWidth = width;

		this.areaHeight = height;

		this.balls = balls;

		this.antiClipping = antiClipping;

		this.gravity = gravity;

	}

	public void step(double deltaT) {
		
		for (Ball b : balls) {

			// Update position by using eulers formula.
			applyEulersFormula(b, deltaT);
			
			// Since the model works without anti-clipping, we can skip this if the user doesnt want it.
			// Its worth noting that the anti-clipping causes significantly more calculations to be made, which might slow down the simulation.
			if (antiClipping) {
				
				// (Maybe) Morph the balls position if the ball is clipping another ball.
				applyAntiBallClipping(b);

				// (Maybe) Morph the balls position if the ball is clipping a wall.
				applyAntiWallClipping(b);

			}

			// (Maybe) Morph y,x speed if the balls hits eachOther during this step.
			applyBallCollisions(b);
			
			// (Maybe) Morph y,x speed if the balls hits a wall during this step.
			applyWallCollisons(b);
			
			if (gravity) {

				// Morph y speed of ball by applying gravitational acceleration.
				applyGravity(b, deltaT);

			}

		}

	}

	// Applies anti-clipping to the ball, IFF the ball is clipping a wall.
	private void applyAntiWallClipping(Ball b) {

		// If the ball is clipping the left or right wall.
		if (b.x <= b.radius || b.x >= areaWidth - b.radius) {

			// Move the ball so that it is only touching the wall not clipping it.
			b.x = Math.max(b.x, b.radius);

			b.x = Math.min(b.x, areaWidth - b.radius);

		}

		// If the ball is clipping the upper or lower wall.
		if (b.y <= b.radius || b.y >= areaHeight - b.radius) {

			// Move the ball so that it is only touching the wall not clipping it.
			b.y = Math.max(b.y, b.radius);

			b.y = Math.min(b.y, areaHeight - b.radius);

		}

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
			if (distance <= b.radius + other.radius + 0.0001) {

				// Calculate the angle between the two balls.
				double angle = angleBetween(b, other);

				// Our initial variables.
				double m1 = b.mass;
				double m2 = other.mass;

				/* Our initial velocities. But rotated to be parallel (x) and orthogonal (y) to the
				axle of collision. This is equivalent to making the vectors (vx1, vy1) and (vx2, vy2)
				out of the balls x and y velocities and rotating them clockwise using the rotation matrix. */
				double u1x = b.vx * Math.cos(angle) - b.vy * Math.sin(angle);
				double u1y = b.vy * Math.cos(angle) + b.vx * Math.sin(angle);
				double u2x = other.vx * Math.cos(angle) - other.vy * Math.sin(angle);
				double u2y = other.vy * Math.cos(angle) + other.vx * Math.sin(angle);

				/*Total momentum before the collision. These could just be baked in to the calculation below,
				but this improves readability. */
				double ix = m1 * u1x + m2 * u2x;
				double iy = m1 * u1y + m2 * u2y;

				/* Relative velocity before the collision. These could just be baked in to the calculation below,
				but this improves readability. */
				double rx = u2x - u1x;
				double ry = u2y - u1y;
				
				/* Since relative velocity and total momentum is the same after the collision (because its fully elastic),
				we can calculate the new velocities by using this formula we aquired from the system of equations
				given by R and I */
				double v1x = (ix + m2 * rx) / (m1 + m2);
				double v1y = (iy + m2 * ry) / (m1 + m2);

				double v2x = (ix - m1 * rx) / (m1 + m2);
				double v2y = (iy - m1 * ry) / (m1 + m2);
				
				// Rotate the velocities back to the original position before assigning them to the balls.
				b.vx = v1x * Math.cos(-angle) - v1y * Math.sin(-angle);
				b.vy = v1y * Math.cos(-angle) + v1x * Math.sin(-angle);
				other.vx = v2x * Math.cos(-angle) - v2y * Math.sin(-angle);
				other.vy = v2y * Math.cos(-angle) + v2x * Math.sin(-angle);

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
		if (b.y >= b.radius) {

			b.vy -= deltaT * G;

		} 

	}

	// Applies wall collisons, IFF the ball collides with the wall.
	private void applyWallCollisons(Ball b) {

		// detect collision with the border
		if (b.x <= b.radius || b.x >= areaWidth - b.radius) {

			b.vx *= -1; // change direction of ball if the ball hits the left or right wall.
		}
		
		if (b.y <= b.radius || b.y >= areaHeight - b.radius) {

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
