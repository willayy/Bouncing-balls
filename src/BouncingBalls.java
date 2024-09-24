package src;
import javax.swing.JFrame;

public final class BouncingBalls {
    
    public static void main(String[] args) {

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                Animator anim = new Animator(800, 600, 60);

                JFrame frame = new JFrame("Bouncing balls");

            	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            	frame.add(anim);

            	frame.pack();

            	frame.setLocationRelativeTo(null);

            	frame.setVisible(true);

            	anim.start();
            }

        });
    }

}
