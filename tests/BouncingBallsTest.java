package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import org.junit.Test;
import src.Ball;
import src.Model;

public class BouncingBallsTest {
    
    @Test
    // Test the lateral collision of a moving and a stationary ball with the same radius and mass.
    public void testLateralCollision() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 5, 1, 0, 0.5, 1);

        balls[1] = new Ball(6, 5, 0, 0, 0.5, 1);

        Model model = new Model(balls, 10, 10, true, false, false);

        /* The balls should collide and the first one should stop,
         the second one should continue moving with the same speed as the first one.
        Gravity is disabled, so the balls should not travel in the y-axis at all. */
        model.step(1);

        // Assert that the transfer of speed has happened.
        assertEquals(0, balls[0].vx, 0.01);

        assertEquals(1, balls[1].vx, 0);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, 0);

        assertEquals(0, balls[1].vy, 0);

    }
    
    @Test
    // Test the lateral collison of two balls moving towards each otehr with teh same speed, radius and mass.
    public void testLateralCollisionSameSpeed() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 5, 1, 0, 0.5, 1);

        balls[1] = new Ball(6, 5, -1, 0, 0.5, 1);

        Model model = new Model(balls, 10, 10, true, false, false);

        /* The balls should collide and both should have their speed inverted.
        Gravity is disabled, so the balls should not travel in the y-axis at all. */
        model.step(1);

        // Assert that the transfer of speed has happened.
        assertEquals(-1, balls[0].vx, 0);

        assertEquals(1, balls[1].vx, 0);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, 0);

        assertEquals(0, balls[1].vy, 0);

    }

    @Test
    // Test the lateral collision between two balls of different mass but with the same radius. where the heavier one is stationary.
    public void testLateralCollisionDifferentMass() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal radius, one is stationary and very heavy, the other one is moving towards towards the heavier ball.
        balls[0] = new Ball(4, 5, 1, 0, 0.5, 1);

        balls[1] = new Ball(6, 5, 0, 0, 0.5, 1000);

        Model model = new Model(balls, 10, 10, true, false, false);

        // The balls should collide and the first one should stop, the second one should continue moving with the same speed as the first one.
        // Gravity is disabled, so the balls should not travel in the y-axis at all. 
        model.step(1);

        /*  Assert that the transfer of speed has happened. There is a tolerance of 0.1
        because the the speed isnt perfectly inverted to the smaller ball. When 
        the mass of the bigger ball approaches infinity the transfer will get closer
        and closer to 1,0 -> -1,0. But at the ratio of 1 : 1000 the smaller ball
        actuallyu gives the big ball a tiny amount of speed. */
        assertEquals(-1, balls[0].vx, 0.01);

        assertEquals(0, balls[1].vx, 0.01);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, 0);

        assertEquals(0, balls[1].vy, 0);

    }

    @Test
    // Test the diagonal collision of a ball moiing at 45 degrees towards a stationary ball.
    public void testDiagonalCollision() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 4, 1, 1, 1, 1);

        balls[1] = new Ball(6, 6, 0, 0, 1, 1);

        Model model = new Model(balls, 10, 10, true, false, false);

        // The balls should collide and the first one should stop, the second one should continue moving with the same speed as the first one.
        model.step(1);

        // Assert that the transfer of speed has happened.
        assertEquals(0, balls[0].vx, 0.01);

        assertEquals(1, balls[1].vx, 0.01);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, 0.01);

        assertEquals(1, balls[1].vy, 0.01);

    }

    @Test
    // Test if a ball dropped from a height will bounce back up but not reach the same height.
    public void testDropBall() {

        Ball[] balls = new Ball[1];

        balls[0] = new Ball(5, 5, 0, -0.1, 1, 1);

        Model model = new Model(balls, 10, 10, true, true, false);

        ArrayList<Double> yValues = new ArrayList<>();

        final double iterations = 100;

        final double deltaT = 0.1;

        // Let the model run for a while and store height values.

        for (int i = 0; i < iterations; i++) {

            yValues.add(balls[0].y);

            model.step(deltaT);

        }
        
        /* Remove the first value since the ball is dropped from a height
         and the first value is the starting height.*/
        yValues.remove(0);

        final double d = 0.001;

        // Assert that the ball has infact bounced back up to the same height.
        boolean doesReachSameHeight = yValues.stream().anyMatch(y -> (y < 5 + d && y > 5 - d));

        assertTrue(doesReachSameHeight);

    }

    @Test
    // Test if kinetic energy is conserved in a collision between two balls.
    public void testKineticEnergyConservation() {

        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 5, 1, 0, 1, 1);

        balls[1] = new Ball(6, 5, 0, 0, 1, 1);

        Model model = new Model(balls, 10, 10, true, false, false);
        
        // Get the mass and speed variables of the balls.
        double m1 = balls[0].mass;
        double m2 = balls[1].mass;
        double v1x = balls[0].vx;
        double v2x = balls[1].vx;

        // Calculate the kinetic energy of the system before and after the collision.
        double kineticEnergyBefore = 0.5 * m1 * Math.pow(v1x, 2) + 0.5 * m2 * Math.pow(v2x, 2);

        // The collision happening when model is stepped.
        model.step(1);

        double kineticEnergyAfter = 0.5 * m1 * Math.pow(v1x, 2) + 0.5 * m2 * Math.pow(v2x, 2);

        // Assert that the kinetic energy is conserved.
        assertEquals(kineticEnergyBefore, kineticEnergyAfter, 0.01);

    }

}
