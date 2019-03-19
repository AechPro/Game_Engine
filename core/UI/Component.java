package core.UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import core.util.FileIOHandler;
import core.util.TextureHandler;

public abstract class Component
{
	protected BufferedImage texture;
	protected int width,height;
	protected int[] pos;
	protected Component parent;
	protected String textureLocation;
	protected AffineTransform t;
	protected AffineTransform gfxPrev;
	protected Color outlineColor;
	protected double angle;
	protected double[] scale;
	public Component(double[] sc, Component p, String texturePath)
	{
		texture = null;
		parent = p;
		scale = sc;
		textureLocation = texturePath;
		init();
	}
	public Component(double[] sc, String texturePath)
	{
		parent = null;
		scale = sc;
		loadTexture(texturePath);
		init();
	}
	
	public abstract void handleMouseInput(MouseEvent e);
	public abstract void handleKeyInput(KeyEvent e);
	public abstract void update();
	public abstract Component copy();
	public abstract void cRender(Graphics2D g);
	public abstract void componentInit();
	
	public void init()
	{
		t = new AffineTransform();
		if(texture != null)
		{
			width=(int)(texture.getWidth()/scale[0]);
			height=(int)(texture.getHeight()/scale[1]);
		}
		else{width=height=0;}
		
		if(parent == null) {pos = new int[] {0,0};}
		else
		{
			pos = new int[]
			{
				parent.getX() + parent.getWidth(),
				parent.getY()
			};
		}
		t.translate(pos[0], pos[1]);
		
		componentInit();
	}
	public void scaleTo(double[] sc)
	{
		if(scale[0] == sc[0] && scale[1] == sc[1])
		{
			return;
		}
		scale = sc;
		setTexture(TextureHandler.loadTexture(textureLocation, sc),sc);
	}
	public void loadTexture(String path)
	{
		if(path == null)
		{
			return;
		}
		textureLocation = path;
		BufferedImage img = TextureHandler.loadTexture(path, scale);
		setTexture(img,scale);
	}
	public void setTexture(BufferedImage tex,double[] sc) 
	{
		if(tex == null) {texture = null; return;}
		texture = tex;
		width = (int)(texture.getWidth()/sc[0]);
		height = (int)(texture.getHeight()/sc[1]);
	}
	public boolean intersects(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		return intersects(x,y);
	}
	public boolean intersects(int x, int y)
	{
		if(x >= pos[0] && x <= pos[0]+width)
		{
			if(y >= pos[1] && y <= pos[1]+height)
			{
				return true;
			}
		}
		return false;
	}
	public void render(Graphics2D g) 
	{
		try
		{
			g.drawImage(texture,t,null);
			if(outlineColor != null)
			{
				g.setColor(outlineColor);
				int thickness = 2;
				Stroke oldStroke = g.getStroke();
				g.setStroke(new BasicStroke(thickness));
				g.drawRect((int)(pos[0]*scale[0]), (int)(scale[1]*pos[1]), (int)(width*scale[0]), (int)(scale[1]*height));
				g.setStroke(oldStroke);
			}
			cRender(g);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void setPos(int[] position) 
	{
		pos = new int[] {position[0], position[1]};
		t.setToTranslation(pos[0]*scale[0], pos[1]*scale[1]);
		t.rotate(angle,scale[0]*width/2,scale[1]*height/2);
	}
	public void rotate(double theta)
	{
		angle+=theta;
		if(angle >= 2*Math.PI) 
		{
			angle -= 2*Math.PI;
		}
		t.setToTranslation(pos[0]*scale[0], pos[1]*scale[1]);
		t.rotate(angle,scale[0]*width/2,scale[1]*height/2);
	}
	
	public double getAngle()
	{
		return angle;
	}
	public void setAngle(double a)
	{
		angle = a;
		t.setToTranslation(pos[0]*scale[0], pos[1]*scale[1]);
		t.rotate(angle,scale[0]*width/2,scale[1]*height/2);
	}
	public void setOutlineColor(Color color){outlineColor = color;}
	
	public int[] getPos() {return new int[] {pos[0],pos[1]};}
	public int getX() {return pos[0];}
	public int getY() {return pos[1];}
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public Color getOutlineColor(){return outlineColor;}
	public String getTextureLocation() {return textureLocation;}
}
