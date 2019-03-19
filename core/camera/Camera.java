package core.camera;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import core.Player;
import core.map.tiles.Tile;
import core.phys.PhysicsObject;
import core.util.MathUtil;

public class Camera
{
	private PhysicsObject p;
	private final int winWidth, winHeight;
	private int[] delta;
	private int[] pos;
	private double[] scale;
	private AffineTransform t;
	private Rectangle viewPort;
	private int viewPortExpansion;
	private double x, y, width, height;
	private int startX, startY;
	private Rectangle bounds;
	private int leftBound, rightBound, topBound, botBound;
	private int[] dims;
	
	public Camera(int _x, int _y, int windowWidth, int windowHeight, double[] sc, int vpex, PhysicsObject subject)
	{
		winWidth = windowWidth;
		winHeight = windowHeight;
		startX = _x;
		startY = _y;
		scale = sc;
		p = subject;
		delta = new int[2];
		t = new AffineTransform();
		viewPortExpansion = vpex;
		viewPort = new Rectangle(startX,startY,winWidth,winHeight);
		bounds = new Rectangle(0,0,0,0);
		dims = new int[] {(int)p.getWidth(),(int)p.getHeight()};
	}
	public Camera(int windowWidth, int windowHeight, double[] sc, int vpex, PhysicsObject subject)
	{
		winWidth = windowWidth;
		winHeight = windowHeight;
		scale = sc;
		p = subject;
		delta = new int[2];
		t = new AffineTransform();
		viewPortExpansion = vpex;
		startX = 0;
		startY = 0;
		viewPort = new Rectangle(0,0,winWidth,winHeight);
		bounds = new Rectangle(0,0,0,0);
		dims = new int[] {(int)p.getWidth(),(int)p.getHeight()};
	}
	public void update()
	{
		x = viewPort.getX() - (viewPortExpansion+t.getTranslateX())/scale[0];
		y = viewPort.getY() - (viewPortExpansion+t.getTranslateY())/scale[1];
		width = viewPort.getWidth() + 2*(viewPortExpansion)/scale[0];
		height = viewPort.getHeight() + 2*(viewPortExpansion)/scale[1];
		bounds.setBounds((int)x, (int)y, (int)width, (int)height);
	}
	public boolean visible(double ax, double ay, double aw, double ah, int exp)
	{
		return ax <= x + width + 2 * exp && ax + aw >= x - exp &&
				ay <= y + height + 2 * exp && ay + ah >= y - exp;
	}
	public boolean visible(Rectangle r, int exp)
	{
		return visible(r.getX(), r.getY(), r.getWidth(), r.getHeight(), exp);
	}
	public boolean visible(PhysicsObject r,int exp)
	{
		return visible(r.getX(), r.getY(), r.getWidth(), r.getHeight(), exp);
	}
	public boolean inBounds(double ax, double ay, double aw, double ah)
	{
		//System.out.println("Checking x,y: ("+(int)x+","+(int)y+") w,h: ("+(int)width+","+(int)height+")");
		//System.out.println("Against x,y: ("+ax+","+ay+") w,h: ("+aw+","+ah+")");
		if(ax+aw >= x && ax <= width)
		{
			if(ay+ah >= y && ay <= height)
			{
				return true;
			}
		}
		return false;
	}
	public boolean inBounds(PhysicsObject a)
	{
		return inBounds(a.getX(),a.getY(),a.getWidth(),a.getHeight());
	}
	
	public boolean inBounds(Rectangle a)
	{
		return inBounds(a.getX(),a.getY(),a.getWidth(),a.getHeight());
	}
	public Rectangle getBounds()
	{
		return bounds;
	}
	public Rectangle getViewPort()
	{
		return viewPort;
	}
	
	public int getViewPortExpansion() {return viewPortExpansion;}
	public PhysicsObject getSubject() {return p;}
	
	public void lockDeltaToBounds(int[] pos, int[] dims)
	{
		if(botBound <= 0 || rightBound <= 0) {return;}
		if(pos[0] - dims[0]/2 + viewPort.getWidth()/2 > rightBound*scale[0])
		{
			delta[0] = (int)(viewPort.getWidth()/2 - (rightBound*scale[0] - viewPort.getWidth()/2) - 8*scale[0]);
		}
		else if(pos[0] + dims[0]/2 < leftBound+ viewPort.getWidth()/2)
		{
			delta[0] = leftBound + dims[0]/2;
		}
		
		if(pos[1] + dims[1]/2 + viewPort.getHeight()/2 - Tile.TILE_SIZE*2*scale[1] > botBound*scale[1])
		{
			delta[1] = (int)(viewPort.getHeight()/2 - (botBound*scale[1] - viewPort.getHeight()/2) - (Tile.TILE_SIZE*2 - 8)*scale[1]);
		}
		else if(pos[1] - dims[1]/2 < topBound + viewPort.getHeight()/2)
		{
			delta[1] = topBound - dims[1]/2;
		}
	}
	
	public AffineTransform computeTransform(double frameDelta, boolean bounded)
	{
		if(p instanceof Player)
		{
			pos = MathUtil.lerp(((Player)p).getPreviousPosition(true), ((Player)p).getPos(true), frameDelta);
		}
		else
		{
			pos = MathUtil.lerp(p.getPreviousPosition(), p.getPos(), frameDelta);
		}
		
		delta[0] = (int)(viewPort.getWidth()/2 - pos[0]);
		delta[1] = (int)(viewPort.getHeight()/2 -  pos[1]);
		if(bounded) {lockDeltaToBounds(pos,dims);}
		
		t.setToTranslation(delta[0], delta[1]);
		return t;
	}
	public AffineTransform getTransform() {return t;}
	public double[] getScreenScale() {return new double[] {scale[0],scale[1]};}
	public void setScreenScale(double[] sc) 
	{
		scale[0] = sc[0];
		scale[1] = sc[1];
		width = viewPort.getWidth() + 2*(viewPortExpansion)/scale[0];
		height = viewPort.getHeight() + 2*(viewPortExpansion)/scale[1];
	}
	public void setLevelBounds(int lb, int rb, int tb, int bb)
	{
		leftBound = lb;
		rightBound = rb;
		topBound = tb;
		botBound = bb;
	}
}
