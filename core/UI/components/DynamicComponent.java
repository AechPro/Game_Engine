package core.UI.components;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import core.UI.Component;

public class DynamicComponent extends Component
{
	private boolean selected;
	public DynamicComponent(double[] sc, Component p, String id){super(sc,p,id);}
	public DynamicComponent(double[] sc, String texturePath){super(sc,texturePath);}
	
	public void update()
	{
		
	}
	@Override
	public void handleMouseInput(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		if(selected)
		{
			setCoords(x,y);
		}
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			if(selected)
			{
				selected = false;
			}
			else 
			{
				selected = true;
				setCoords(x,y);
			}
		}
	}
	@Override
	public void handleKeyInput(KeyEvent e) 
	{
	}
	private void setCoords(int x, int y)
	{
		if(x >= pos[0] && x <= pos[0]+width)
		{
			if(y >= pos[1] && y <= pos[1]+height)
			{
				pos[0] = x;
				pos[1] = y;
			}
		}
	}
	@Override
	public Component copy() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void cRender(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentInit() {
		// TODO Auto-generated method stub
		
	}
}
