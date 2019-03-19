package core.map.tiles;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import core.phys.PhysicsObject;
import core.util.TextureHandler;

public abstract class Tile extends PhysicsObject
{
	protected BufferedImage img,scaledImage;
	protected AffineTransform gfxPrev;
	protected double[] scale;
	protected double renderAngle;
	protected String texturePath;
	protected boolean decoration;
	protected int renderX, renderY;
	
	public Tile(int[] startPos, double startAngle, String textureLocation, double[] sc)
	{
		projectionPriority = -1;
		collisionPriority = -1;
		collidable = false;
		decoration = false;
		img = null;
		width=0;
		height=0;
		renderAngle = startAngle;
		scale = sc;

		position = new double[]{startPos[0],startPos[1]};
		origin = new double[] {startPos[0],startPos[1]};
		previousPosition = new double[] {startPos[0],startPos[1]};
		t = new AffineTransform();
		t.translate(position[0], position[1]);
		
		texturePath = textureLocation;
		
		init();
	}
	
	public Tile(Tile other)
	{
		position = new double[] {other.getPos()[0],other.getPos()[1]};
		origin = new double[] {other.getOrigin()[0],other.getOrigin()[1]};
		projectionPriority = other.getProjectionPriority();
		decoration = other.isDecoration();
		img = other.getTexture();
		width = other.getWidth();
		height = other.getHeight();
		renderAngle = other.getRenderAngle();
		previousPosition = new double[]{other.getPreviousPosition()[0],other.getPreviousPosition()[1]};
		t = new AffineTransform();
		collidable = other.isCollidable();
		scale = new double[] {other.getScale()[0], other.getScale()[1]};
		texturePath = other.getTexturePath();
	}
	
	public abstract void init();
	public abstract void tUpdate();
	public abstract void tRender(Graphics2D g);
	public void update()
	{
		previousPosition[0] = position[0];
		previousPosition[1] = position[1];
		tUpdate();
		
		
	}
	
	public void render(Graphics2D g, double delta)
	{
		try
		{
			if(img != null)
			{
				renderX = (int)Math.round(position[0]*scale[0]);
				renderY = (int)Math.round(scale[1]*position[1]);
				g.drawImage(img,renderX, renderY, null);
			}
			tRender(g);
			if(color != null)
			{
				g.setColor(color);
				g.fillRect(renderX, renderY, (int)(scale[0]*width), (int)(scale[1]*height));
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	public void drawHitbox(Graphics2D g)
	{
		Rectangle2D r1 = getbbox();
		g.setColor(Color.GREEN);
		g.drawRect((int)r1.getX(),(int)r1.getY(),(int)r1.getWidth(),(int)r1.getHeight());
	}
	
	public void loadTexture(String filePath)
	{
		texturePath = filePath;
		img = TextureHandler.loadTexture(filePath,scale);
		width = img.getWidth()/scale[0];
		height = img.getHeight()/scale[1];
	}
	
	public void setTexture(BufferedImage texture, String filePath, double[] sc)
	{
		texturePath = filePath;
		img = texture;
		width = texture.getWidth();
		height = texture.getHeight();
		scale = sc;
		if(sc[0] != 1.0 || sc[1] != 1.0)
		{
			width/=sc[0];
			height/=sc[1];
		}
	}
	
	public void setTexture(BufferedImage texture, double[] sc)
	{
		setTexture(texture,texturePath,sc);
	}
	
	public BufferedImage getTexture() {return img;}
	
	public void setDims(int w, int h)
	{
		width = w;
		height = h;
	}
	public static final int TILE_SIZE = 32;

	public boolean isCollidable() {return collidable;}
	public boolean isDecoration() {return decoration;}
	
	public double[] getScale() {return scale;}
	public double getAngle() {return renderAngle;}
	public void setAngle(double ang) 
	{
		renderAngle = ang;
		setTexture(TextureHandler.rotateTexture(texturePath,ang,scale),scale);
	}
	
	public String getTexturePath(){return texturePath;}
	public double getRenderAngle() {return renderAngle;}
	public void setCollidiable(boolean i){collidable=i;}

	public void setDims(double w, double h) 
	{
		setDims((int)w, (int)h);
		
	}
}
