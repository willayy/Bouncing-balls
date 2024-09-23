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
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3);
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

	private void applyEulersFormula(Ball b, double deltaT) {
		// compute new position according to the speed of the ball
		b.x += deltaT * b.vx;
		b.y += deltaT * b.vy;
	}

	private void applyBallCollisions(Ball b, double deltaT) {
		// TODO: Implement
	}

	// Applies gravity to the vertical accelration of the ball
	private void applyGravity(Ball b, double deltaT) {

		/* If the ball isnt at the floor gravity should apply.
		 Physically this isn't 100% correct since gravity still applies even when an object is on
		 the floor, but for our model this makes things a lot simpler. */
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
	
	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		
		Ball(double x, double y, double vx, double vy, double r) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius;
	}
}
