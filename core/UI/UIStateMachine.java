package core.UI;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

public class UIStateMachine
{
	private State currentState;
	private double[] scale;
	private AffineTransform scaleTF;
	private static HashMap<String, State> stateMap;
	public UIStateMachine(double[] screenScale)
	{
		stateMap = new HashMap<String, State>();
		scale = screenScale;
		scaleTF = new AffineTransform();
		scaleTF.scale(scale[0], scale[1]);
		currentState = null;
	}
	public void update()
	{
		if(currentState == null) {return;}
		currentState.update();
		if(currentState.getNextState() != null)
		{
			currentState.onExit();
			currentState = stateMap.get(currentState.getNextState());
			currentState.onEnter();
		}
	}
	public void render(Graphics2D g)
	{
		if(currentState == null) {return;}
		currentState.render(g);
	}
	
	public void addState(String key, State s) 
	{
		if(currentState == null) {currentState = s;}
		stateMap.put(key, s);
	}
	
	public void mousePressed(MouseEvent e)
	{
		currentState.getDisplayPanel().mousePressed(e);
	}
	public void mouseReleased(MouseEvent e)
	{
		currentState.getDisplayPanel().mouseReleased(e);
	}
	public void mouseClicked(MouseEvent e)
	{
		currentState.getDisplayPanel().mouseClicked(e);
	}
	public void keyPressed(KeyEvent e)
	{
		currentState.getDisplayPanel().keyPressed(e);
	}
	public void keyReleased(KeyEvent e)
	{
		currentState.getDisplayPanel().keyReleased(e);
	}
	public void keyTyped(KeyEvent e)
	{
		currentState.getDisplayPanel().keyTyped(e);
	}
	public void mouseMoved(MouseEvent e)
	{
		currentState.getDisplayPanel().mouseMoved(e);
	}
	public void mouseDragged(MouseEvent e)
	{
		currentState.getDisplayPanel().mouseDragged(e);
	}
	
	public static State getState(String key)
	{
		return stateMap.get(key);
	}
}