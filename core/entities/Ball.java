package core.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.camera.Camera;
import core.util.TextureHandler;

public class Ball extends Entity
{
	private double angle;
	public Ball(double[] startPos, double startAngle, Camera cam) 
	{
		super(startPos, startAngle, cam);
		setTexture(buildImage(),scale);
	}

	@Override
	public void init() 
	{
		width = height = 20;
		angle = Math.random()*Math.PI;
		acceleration[0] = Math.cos(angle);
		acceleration[1] = 1;
		collidable=true;
		projectionPriority = 1;
		maxVelocity = new double[] {15,15};
	}
	private BufferedImage buildImage()
	{
		BufferedImage image = new BufferedImage((int)width,(int)height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(Color.GREEN);
		g.fillOval(0, 0, (int)width, (int)height);
		g.dispose();
		return TextureHandler.scaleTexture(image, scale);
	}
	@Override
	public void eUpdate() 
	{
		velocity[0]*=0.995;
		acceleration[0] *= 0.5;
	}

	@Override
	public void eRender(Graphics2D g, double interp) 
	{
		
	}

}
