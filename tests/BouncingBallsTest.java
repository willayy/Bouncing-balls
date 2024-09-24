package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import src.Ball;
import src.Model;

public class BouncingBallsTest {
    
    @Test
    // Test the lateral collision of a moving and a stationary ball with the same radius and mass.
    public void testLateralCollision() {
        
        Ball[] balls = new Ball[2];

        // Two balls of equal mass and radius, moving towards each other at the same speed.
        balls[0] = new Ball(4, 5, 1, 0, 1, 1);

        balls[1] = new Ball(6, 5, 0, 0, 1, 1);

        Model model = new Model(balls, 10, 10, false, false);

        /* The balls should collide and the first one should stop,
         the second one should continue moving with the same speed as the first one.
        Gravity is disabled, so the balls should not travel in the y-axis at all. */
        model.step(1);

        // Assert that the transfer of speed has happened.
        assertEquals(0, balls[0].vx, 0);

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
        balls[0] = new Ball(4, 5, 1, 0, 1, 1);

        balls[1] = new Ball(6, 5, -1, 0, 1, 1);

        Model model = new Model(balls, 10, 10, false, false);

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
        balls[0] = new Ball(4, 5, 1, 0, 1, 1);

        balls[1] = new Ball(6, 5, 0, 0, 1, 1000);

        Model model = new Model(balls, 10, 10, false, false);

        // The balls should collide and the first one should stop, the second one should continue moving with the same speed as the first one.
        // Gravity is disabled, so the balls should not travel in the y-axis at all. 
        model.step(1);

        /*  Assert that the transfer of speed has happened. There is a tolerance of 0.1
        because the the speed isnt perfectly inverted to the smaller ball. When 
        the mass of the bigger ball approaches infinity the transfer will get closer
        and closer to 1,0 -> -1,0. But at the ratio of 1 : 1000 the smaller ball
        actuallyu gives the big ball a tiny amount of speed. */
        assertEquals(-1, balls[0].vx, 0.1);

        assertEquals(0, balls[1].vx, 0.1);
        
        // Assert that the balls have not moved in the y-axis, this collision should only affect the x-axis.
        assertEquals(0, balls[0].vy, 0);

        assertEquals(0, balls[1].vy, 0);

    }

}
