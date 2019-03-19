package editor.UI.packs.tileCreator.panel;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import core.UI.Component;
import core.UI.DisplayPanel;
import core.UI.components.StaticComponent;

public class TileCreatorDisplayPanel extends DisplayPanel
{

	public TileCreatorDisplayPanel(double[] sc, int w, int h) 
	{
		super(sc, w, h);
	}

	@Override
	public void initComponents()
	{
		String path = "resources/Editor/textures/";
		elements = new ArrayList<Component>();

		double[] scale = new double[]{1.0,1.0};

		background = new StaticComponent(scale,path+"UI/frame/frame.png");
	}

	@Override
	public void panelRender(Graphics2D g)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
		
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
	}

}
