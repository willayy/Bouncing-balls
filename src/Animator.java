package src;

import java.awt.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import src.Model.Ball;

/**
 * Animated JPanel drawing the bouncing balls. No modifications are needed in this class.
 *
 * @author Simon Robillard
 *
 */
@SuppressWarnings("serial")
public final class Animator extends JPanel implements ActionListener {

	public Animator(int pixelWidth, int pixelHeight, int fps) {
		super(true);
		this.timer = new Timer(1000 / fps, this);
		this.deltaT = 1.0 / fps;
		this.model = new Model(pixelWidth / pixelsPerMeter, pixelHeight / pixelsPerMeter);
		this.setOpaque(false);
		this.setPreferredSize(new Dimension(pixelWidth, pixelHeight));
	}

	/**
	 * Drawing scale
	 */
	private static final double pixelsPerMeter = 200;

	/**
	 * Physical model
	 */
	private Model model;

	/**
	 * Timer that triggers redrawing
	 */
	private Timer timer;

	/**
	 * Time interval between redrawing, also used as time step for the model
	 */
	private double deltaT;

	public void start() {
		timer.start();
	}

	public void stop() {
    	timer.stop();
    }

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// clear the canvas
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		// draw balls
		g2.setColor(Color.RED);
		for (Ball b : model.balls) {
			double x = b.x - b.radius;
			double y = b.y + b.radius;
			// paint balls (y-coordinates are inverted)
			Ellipse2D.Double e = new Ellipse2D.Double(x * pixelsPerMeter, this.getHeight() - (y * pixelsPerMeter),
					b.radius * 2 * pixelsPerMeter, b.radius * 2 * pixelsPerMeter);
			g2.fill(e);
		}
		Toolkit.getDefaultToolkit().sync();
	}

    @Override
    public void actionPerformed(ActionEvent e) {
    	model.step(deltaT);
    	this.repaint();
    }

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
