package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Animated JPanel drawing the bouncing balls. No modifications are needed in this class.
 *
 * @author Simon Robillard
 *
 */
@SuppressWarnings("serial")
public final class Animator extends JPanel implements ActionListener {

	// Drawing scale
	private static final double pixelsPerMeter = 200;

	//Physical model
	private Model model;

	//Timer that triggers redrawing
	private Timer timer;

	//Time interval between redrawing, also used as time step for the model
	private double deltaT;

	public Animator(int pixelWidth, int pixelHeight, int fps) {

		super(true);

		this.timer = new Timer(1000 / fps, this);

		this.deltaT = 1.0 / fps;

		Ball[] balls = new Ball[1];

		balls[0] = new Ball(1, 0.2, 0, 9, 0.2, 1);

		//balls[1] = new Ball(2, 2, 2, 2, 0.2, 1);

		//balls[2] = new Ball(3, 2, 2, 2, 0.2, 1);

		this.model = new Model(balls, pixelWidth / pixelsPerMeter, pixelHeight / pixelsPerMeter, true, true, true);

		this.setOpaque(false);

		this.setPreferredSize(new Dimension(pixelWidth, pixelHeight));

	}

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
			Ellipse2D.Double e = new Ellipse2D.Double(

				x * pixelsPerMeter,

				this.getHeight() - (y * pixelsPerMeter),

				b.radius * 2 * pixelsPerMeter, b.radius * 2 * pixelsPerMeter

			);

			g2.fill(e);
		}

		Toolkit.getDefaultToolkit().sync();
	}

    @Override
    public void actionPerformed(ActionEvent e) {

    	model.step(deltaT);

    	this.repaint();

    }

}
