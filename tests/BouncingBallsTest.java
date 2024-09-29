package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import org.junit.Test;
import src.Ball;
import src.Model;

public class BouncingBallsTest {
    
    // Tolerance for the test.
    // This is mainly used to counteract the floating point arithmetic errors.
    private static final double d = 0.01;

    @Test
    // Test the lateral collision of a moving and a stationary ball with the same radius and mass.
    public void testLateralCollision() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 5, 1, 0, 0.5, 1);

        balls[1] = new Ball(6, 5, 0, 0, 0.5, 1);

        Model model = new Model(balls, 10, 10, false, false);

        /* The balls should collide and the first one should stop,
         the second one should continue moving with the same speed as the first one.
        Gravity is disabled, so the balls should not travel in the y-axis at all. */
        model.step(1);

        // Assert that the transfer of speed has happened.
        assertEquals(0, balls[0].vx, d);

        assertEquals(1, balls[1].vx, d);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, d);

        assertEquals(0, balls[1].vy, d);

    }
    
    @Test
    // Test the lateral collison of two balls moving towards each otehr with teh same speed, radius and mass.
    public void testLateralCollisionSameSpeed() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 5, 1, 0, 0.5, 1);

        balls[1] = new Ball(6, 5, -1, 0, 0.5, 1);

        Model model = new Model(balls, 10, 10, false, false);

        /* The balls should collide and both should have their speed inverted.
        Gravity is disabled, so the balls should not travel in the y-axis at all. */
        model.step(1);

        // Assert that the transfer of speed has happened.
        assertEquals(-1, balls[0].vx, d);

        assertEquals(1, balls[1].vx, d);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, d);

        assertEquals(0, balls[1].vy, d);

    }

    @Test
    // Test the lateral collision between two balls of different mass but with the same radius. where the heavier one is stationary.
    public void testLateralCollisionDifferentMass() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal radius, one is stationary and very heavy, the other one is moving towards towards the heavier ball.
        balls[0] = new Ball(4, 5, 1, 0, 0.5, 1);

        balls[1] = new Ball(6, 5, 0, 0, 0.5, 1000);

        Model model = new Model(balls, 10, 10, false, false);

        // The balls should collide and the first one should stop, the second one should continue moving with the same speed as the first one.
        // Gravity is disabled, so the balls should not travel in the y-axis at all. 
        model.step(1);

        /*  Assert that the transfer of speed has happened. There is a tolerance of 0.1
        because the the speed isnt perfectly inverted to the smaller ball. When 
        the mass of the bigger ball approaches infinity the transfer will get closer
        and closer to 1,0 -> -1,0. But at the ratio of 1 : 1000 the smaller ball
        actuallyu gives the big ball a tiny amount of speed. */
        assertEquals(-1, balls[0].vx, d);

        assertEquals(0, balls[1].vx, d);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, d);

        assertEquals(0, balls[1].vy, d);

    }

    @Test
    // Test the diagonal collision of a ball moiing at 45 degrees towards a stationary ball.
    public void testDiagonalCollision() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 4, 1, 1, 1, 1);

        balls[1] = new Ball(6, 6, 0, 0, 1, 1);

        Model model = new Model(balls, 10, 10, false, false);

        // The balls should collide and the first one should stop, the second one should continue moving with the same speed as the first one.
        model.step(1);

        // Assert that the transfer of speed has happened.
        assertEquals(0, balls[0].vx, d);

        assertEquals(1, balls[1].vx, d);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, d);

        assertEquals(1, balls[1].vy, d);

    }

    @Test
    // Test if a ball dropped from a height will bounce back up but not reach the same height.
    public void testDropBall() {

        Ball[] balls = new Ball[1];

        balls[0] = new Ball(5, 5, 0, 0, 0.25, 1);

        Model model = new Model(balls, 10, 10, true, false);

        // Step the model onnce 
        model.step(0.1);

        // Let the model run until the ball bounces back up to the height of 5.
        for (int i = 0; balls[0].y < 5; i++) {

            model.step(0.1);

            if (i > 1000000) {
                // If the ball has not bounced back up to the height of 5 after 1000000 steps, the test fails.
                assertTrue(false);
            }

        }
        
        // Assert that the ball has bounced back up an reached the same height as it was dropped from.
        assertTrue(true);

    }

    @Test
    // Test if kinetic energy is conserved in a collision between two balls.
    public void testKineticEnergyConservationSimple() {

        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, one is stationary and the other one is moving towards it.
        balls[0] = new Ball(4, 5, 1, 0, 0.5, 1);

        balls[1] = new Ball(6, 5, 0, 0, 0.5, 1);

        Model model = new Model(balls, 10, 10, false, false);

        // Calculate the kinetic energy of the system before and after the collision.
        double kineticEnergyBefore = kineticEnergy(balls[0]) + kineticEnergy(balls[1]);

        // The collision happening when model is stepped.
        model.step(1);

        double kineticEnergyAfter = kineticEnergy(balls[0]) + kineticEnergy(balls[1]);

        // Assert that the kinetic energy is conserved.
        assertEquals(kineticEnergyBefore, kineticEnergyAfter, d);

    }

    @Test
    // Test if kinetic energy is conserved when the ball clips another ball and antiBallClipping is used.
    public void testKineticEnergyConservationAdvanced() {

        Ball[] balls = new Ball[2];

        // Two balls in the model
        balls[0] = new Ball(4, 5, 1, -1, 0.15, 1);

        balls[1] = new Ball(5.1, 5, 1, 1, 0.30, 8);

        Model model = new Model(balls, 10, 10, false, false);

        // Calculate the kinetic energy of the system at the start of the simulatioin.
        double kineticEnergyBefore = kineticEnergy(balls[0]) + kineticEnergy(balls[1]);

        // Let the model run for a while.
        for (int i = 0; i < 100000; i++) {

            model.step(1);

        }

        // Calculate the kinetic energy of the system after running the simulation.
        double kineticEnergyAfter = kineticEnergy(balls[0]) + kineticEnergy(balls[1]);

        // Assert that the kinetic energy is conserved.
        assertEquals(kineticEnergyBefore, kineticEnergyAfter, d);

    }

    // Helper method to calculate the kinetic energy of a ball.
    private double kineticEnergy(Ball b) {

        return 0.5 * b.mass * (Math.pow(b.vx, 2) + Math.pow(b.vy, 2));

    }

}
