package editor.UI.components;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import core.UI.Component;
import editor.level.Map;

public class TileComponent extends Component
{
	private boolean selected;
	private boolean outOfBounds;
	private boolean decoration;

	public TileComponent(double[] sc, String texturePath, int[] position)
	{
		super(sc,texturePath);
		setPos(position);
	}
	public TileComponent(double[] sc, Component p, String texturePath){super(sc,p,texturePath);}
	public TileComponent(double[] sc, String texturePath){super(sc,texturePath);}

	@Override
	public void componentInit()
	{
		if(textureLocation == null)
		{
			decoration = false;
		}
		else
		{
			decoration = textureLocation.contains(Map.DECORATION_TILE_DIRECTORY_EXT);
		}
		outOfBounds = false;
	}

	public void update()
	{

	}
	public void moveTo(int x, int y)
	{
		int xDist = Math.abs(x-pos[0]);
		int yDist = Math.abs(y-pos[1]);
		
		if(xDist > 0) 
		{
			//done = false;
			int sign = (x-pos[0])/xDist;
			setPos(new int[] {pos[0]+sign*width*(int)(xDist/width),pos[1]});
		}
		if(yDist > 0)
		{
			//done = false;
			int sign = (y-pos[1])/yDist;
			setPos(new int[] {pos[0],pos[1]+sign*height*(int)(yDist/width)});
		}
	}
	@Override
	public void handleMouseInput(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			if(selected)
			{
				selected = false;
			}
			else 
			{
				selected = true;
				if(x >= pos[0] && x <= pos[0]+width)
				{
					if(y >= pos[1] && y <= pos[1]+height)
					{
						pos[0] = x;
						pos[1] = y;
					}
				}
			}
		}
	}
	public BufferedImage getTexture() {return texture;}
	public void handleMouseMotion(MouseEvent e) 
	{
		if(selected)
		{
			pos[0] = e.getX();
			pos[1] = e.getY();
		}
	}
	@Override
	public void handleKeyInput(KeyEvent e) 
	{
	}
	@Override
	public Component copy() 
	{
		Component c = new TileComponent(scale,null,getTextureLocation());
		c.loadTexture(getTextureLocation());
		c.setPos(getPos());
		return c;
	}
	public int getIntID()
	{
		String id = textureLocation;
		id = id.substring(id.indexOf("tiles")+"tiles".length(),id.length());
		id = id.substring(id.indexOf("tile")+"tile".length(),id.indexOf(".png"));
		return Integer.parseInt(id);
	}
	public void OOB(boolean state) {outOfBounds = state;}
	public boolean isOOB() {return outOfBounds;}
	public void setDecoration(boolean i) 
	{
		decoration = i;
	}
	public boolean isDecoration()
	{
		return decoration;
	}
	@Override
	public void cRender(Graphics2D g) 
	{
		if(outlineColor != null)
		{
			
		}
	}
}
