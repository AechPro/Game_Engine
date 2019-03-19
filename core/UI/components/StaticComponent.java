package core.UI.components;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import core.UI.Component;

public class StaticComponent extends Component
{
	public StaticComponent(double[] sc, Component p, String id){super(sc,p,id);}
	public StaticComponent(double[] sc, String texturePath){super(sc,texturePath);}
	@Override
	public void handleMouseInput(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleKeyInput(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Component copy() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void cRender(Graphics2D g) {}
	@Override
	public void componentInit() {
		// TODO Auto-generated method stub
		
	}
}
