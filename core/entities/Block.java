package core.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.camera.Camera;
import core.util.TextureHandler;

public class Block extends Entity
{
	private int ticker;
	public Block(int w, int h, double[] startPos, double startAngle, int num, Camera cam)
	{
		super(startPos, startAngle, cam);
		width = Math.max(5, w);
		height = Math.max(5, h);
		setTexture(buildImage(),scale);

	}
	@Override
	public void init() 
	{
		ticker = 0;
		projectionPriority = 1;

	}
	private BufferedImage buildImage()
	{
		BufferedImage image = new BufferedImage((int)width,(int)height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(Color.RED);
		g.fillRect(0, 0, (int)width, (int)height);
		g.dispose();
		return TextureHandler.scaleTexture(image, scale);
	}

	@Override
	public void eUpdate() 
	{
		//rotate(Math.random()*Math.random());

		if(ticker>=100)
		{
			acceleration = new double[]{Math.random(),Math.random()};
			if(Math.random()>0.5){acceleration[0]*=-1;}
			if(Math.random()>0.5){acceleration[1]*=-1;}
			ticker=0;
		}
		ticker++;
	}

	@Override
	public void eRender(Graphics2D g, double interp)
	{
		
		
	}
}
