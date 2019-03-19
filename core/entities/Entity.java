package core.entities;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import core.camera.Camera;
import core.phys.PhysicsObject;
import core.util.MathUtil;
import core.util.TextureHandler;

import java.awt.Graphics2D;

public abstract class Entity extends PhysicsObject
{
	protected BufferedImage img;
	protected int[] apparentPosition;
	protected double[] scale;
	protected String texturePath;
	
	protected Camera camera;
	
	protected boolean OOB;

	public Entity(double[] startPos, double startAngle, Camera cam)
	{
		OOB = false;
		img = null;
		camera = cam;
		origin = new double[]{startPos[0],startPos[1]};
		position = new double[]{startPos[0],startPos[1]};
		previousPosition = new double[]{position[0],position[1]};
		maxVelocity = new double[]{5d,5d};
		apparentPosition = new int[] {(int)position[0], (int)position[1]};
		angle = startAngle;
		width=height=0;
		t = new AffineTransform();
		
		dead = false;
		
		scale = cam.getScreenScale();

		init();
	}
	public abstract void init();
	public abstract void eUpdate();
	public abstract void eRender(Graphics2D g, double interp);
	public void update()
	{
		OOB = !camera.visible(this,0);
		//if(OOB) {return;}
		
		previousPosition[0] = position[0];
		previousPosition[1] = position[1];

		previousVelocity[0] = velocity[0];
		previousVelocity[1] = velocity[1];
		
		color = null;
		for(int i=0;i<2;i++)
		{
			position[i]+=velocity[i];
			velocity[i]+=acceleration[i];
			if(velocity[i]>maxVelocity[i]){velocity[i]=maxVelocity[i];}
			else if(velocity[i]<-maxVelocity[i]){velocity[i]=-maxVelocity[i];}
		}
		
		eUpdate();
	}
	public void render(Graphics2D g, double interp)
	{
		if(OOB) {return;}
		try
		{
			
			//Note: disable rotation. It sucks.
			apparentPosition = MathUtil.lerp(previousPosition, position, interp);
			AffineTransform gt = g.getTransform();
			g.rotate(angle,(int)(apparentPosition[0]*scale[0] + width/2),(int)(apparentPosition[1]*scale[1] + height/2));
			
			g.drawImage(img,(int)(apparentPosition[0]*scale[0]),(int)(apparentPosition[1]*scale[1]), null);
			
			g.setTransform(gt);
			
			eRender(g,interp);
		}
		catch(Exception e){e.printStackTrace();}
	}
	public void rotate(double theta)
	{
		prevAngle = angle;
		angle+=theta;
	}
	public void loadTexture(String filePath)
	{
		texturePath = filePath;
		img = TextureHandler.loadTexture(filePath,scale);
		width = img.getWidth()/scale[0];
		height = img.getHeight()/scale[1];
	}
	public void setTexture(BufferedImage texture, double[] sc)
	{
		setTexture(texture,texturePath,sc);
	}
	public void setTexture(BufferedImage texture,String filePath, double[] sc)
	{
		texturePath = filePath;
		img = texture;
		width = texture.getWidth();
		height = texture.getHeight();
		if(sc[0] != 1.0 || sc[1] != 1.0)
		{
			width/=sc[0];
			height/=sc[1];
		}
	}
}
