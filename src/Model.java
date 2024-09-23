package src;

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
class Model {

	// The gravitational acceleration consants on earth (specifically Sweden).
	private static final double G = 9.82;

	double areaWidth, areaHeight;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 0, 0, 0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.7, 0, 0, 0.3);
	}

	void step(double deltaT) {
		
		for (Ball b : balls) {

			// (Maybe) Morph y,x speed if the balls hits a wall during this step.
			applyWallCollisons(b);

			// Morph y speed of ball by applying gravitational acceleration.
			applyGravity(b, deltaT);
			
			// (Maybe) Morph y,x speed if the balls hits eachOther during this step.
			applyBallCollisions(b, deltaT);
			
			// Update position by using eulers formula.
			applyEulersFormula(b, deltaT);

		}

	}

	private void applyBallCollisions(Ball b, double deltaT) {
		// TODO: Implement	
	}

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
	private void applyWallCollisons(Ball b) {

		// detect collision with the border
		if (b.x < b.radius || b.x > areaWidth - b.radius) {

			b.vx *= -1; // change direction of ball if the ball hits the left or right wall.
		}
		
		if (b.y < b.radius || b.y > areaHeight - b.radius) {

			b.vy *= -1; // change direction of ball if the ball hits the upper or lower wall.

		}
		
	}

	// This method converts the rectangular coordinates to polar coordinates.
	private Point rectToPolar(double x, double y) {

		// Calculate the radius (cat) using the pythagorean theorem.
		double r = Math.sqrt(x * x + y * y);
		
		/* Since y is the opposite side and x is the adjacent side of the triangle the relationship
		 between them is the tangent of the angle theta. We use the atan2 function to get the angle
		 theta in a range of -pi to pi. */
		double theta = Math.atan2(y, x);

		// Return the polar coordinates in a Point object.
		return new Point(r, theta);
			
	}
	
	// This method converts the polar coordinates to rectangular coordinates.
	private Point polarToRect(double r, double theta){

		double x, y;

		// Calculate the x coordinate using the cosine of the angle theta.
		x = r * Math.cos(theta);

		// Calculate the y coordinate using the sine of the angle theta.
		y = r * Math.sin(theta);

		// Return the rectangular coordinates in a Point object.
		return new Point(x, y);
	}
	
}
