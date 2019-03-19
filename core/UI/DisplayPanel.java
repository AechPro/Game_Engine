package core.UI;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class DisplayPanel 
{
	protected HashMap<String,Command> commands;
	protected ArrayList<Component> elements;
	protected Component background;
	protected int width;
	protected int height;
	protected double[] componentScale;
	public DisplayPanel(double[] sc, int w, int h)
	{
		componentScale = sc;
		commands = new HashMap<String, Command>();
		width = w;
		height = h;
		initComponents();
	}
	public synchronized void update()
	{
		if(background != null) {background.update();}
		if(elements != null)
		{
			synchronized(elements)
			{
				for(Component c : elements)
				{
					c.update();
				}
			}
		}
	}
	public void render(Graphics2D g)
	{
		if(background != null) {background.render(g);}
		if(elements != null)
		{
			synchronized(elements)
			{
				for(Component c : elements)
				{
					c.render(g);
				}
			}
		}
		panelRender(g);
	}
	public void elementNotification(String name, String[] args)
	{
		Command com = commands.get(name);
		com.execute(args);
	}
	public void attachCommand(String ID, Command c)
	{
		if(commands == null)
		{
			commands = new HashMap<String, Command>();
		}
		commands.put(ID,c);
	}
	public void addComponent(Component c)
	{
		if(elements == null) {elements = new ArrayList<Component>();}
		elements.add(c);
	}
	public void removeComponent(Component c) 
	{
		if(elements.contains(c)) {elements.remove(c);}
	}
	public void removeComponent(int i) {elements.remove(i);}
	
	public abstract void initComponents();
	public abstract void panelRender(Graphics2D g);
	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseReleased(MouseEvent e);
	public abstract void mouseClicked(MouseEvent e);
	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
	public abstract void keyTyped(KeyEvent e);
	public abstract void mouseMoved(MouseEvent e);
	public abstract void mouseDragged(MouseEvent e);
}
