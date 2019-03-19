package core.util;

import java.awt.Rectangle;

public class RectangleSelection 
{
	private boolean selected;
	private int[] origin;
	private int[] pos;
	private int width,height;
	private Rectangle area, bounds;
	public RectangleSelection(int origX, int origY, int w, int h)
	{
		width = w;
		height = h;
		origin = new int[] {origX, origY};
		pos = new int[] {origX,origY};
		selected = true;
		area = new Rectangle(pos[0],pos[1],w,h);
		bounds = new Rectangle(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
	}
	public void extendTo(int x, int y)
	{
		if(!selected) {return;}
		
		if(x > origin[0])
		{
			pos[0] = origin[0];
			width = (int)Math.min(Math.abs(bounds.getWidth() - origin[0]), Math.abs(x - origin[0]));
		}
		else
		{
			pos[0] = (int)Math.max(x,bounds.getX());
			width = (int)Math.min(Math.abs(bounds.getX() - origin[0]), Math.abs(x - origin[0]));
		}
		if(y > origin[1])
		{
			pos[1] = origin[1];
			height = (int)Math.min(Math.abs(bounds.getHeight() - origin[1]), Math.abs(y - origin[1]));

		}
		else
		{
			pos[1] = (int)Math.max(y,bounds.getY());;
			height = (int)Math.min(Math.abs(bounds.getY() - origin[1]), Math.abs(y - origin[1]));

		}
		
		area.setBounds(pos[0],pos[1],width,height);
	}
	
	public void setBounds(int x, int y, int w, int h)
	{
		if(bounds == null)
		{
			bounds = new Rectangle();
		}
		bounds.setBounds(x,y,w,h);
	}
	public void select(int x, int y)
	{
		origin[0] = x;
		origin[1] = y;
		area = new Rectangle(x,y,1,1);
		selected = true;
	}
	public void deSelect()
	{
		//origin[0] = origin[1] = pos[0] = pos[1] = width = height = 0;
		//bounds = null;
		selected = false;
	}
	public boolean isSelected()
	{
		return selected;
	}
	public Rectangle getArea()
	{
		return area;
	}
	public int[] getPos()
	{
		return pos;
	}
}
