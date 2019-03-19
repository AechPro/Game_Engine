package core.UI;

import java.awt.Graphics2D;
import java.util.ArrayList;

import core.util.FileIOHandler;
import core.UI.DisplayPanel;
import core.camera.Camera;
public abstract class State 
{
	protected ArrayList<Command> comms;
	protected String nextState, next, prev;
	protected String name;
	protected FileIOHandler IOHandler;
	protected DisplayPanel displayPanel;
	protected Camera camera;
	public State(Camera cam)
	{
		name = "";
		camera = cam;
		nextState = null;
		next = null;
		prev = null;
		comms = new ArrayList<Command>();
		IOHandler = new FileIOHandler();
		displayPanel = null;
		init();
	}
	public abstract void init();
	public abstract void onEnter();
	public abstract void onExit();
	public abstract void stateUpdate();
	
	public void update() 
	{
		if(displayPanel != null) 
		{
			displayPanel.update();
		}
		stateUpdate();
	}

	public void render(Graphics2D g) 
	{
		if(displayPanel != null)
		{
			displayPanel.render(g);
		}
	}
	public void setNext(String s) {next = s;}
	public void setPrev(String s) {prev = s;}
	public DisplayPanel getDisplayPanel() {return displayPanel;}
	public void setNextState(String s) {nextState = s;}
	public String getNextState() {return nextState;}
}
