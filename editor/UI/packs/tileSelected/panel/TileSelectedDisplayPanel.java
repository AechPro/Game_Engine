package editor.UI.packs.tileSelected.panel;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import core.UI.Component;
import core.UI.DisplayPanel;
import core.UI.components.StaticComponent;

public class TileSelectedDisplayPanel extends DisplayPanel
{
	private boolean ctrlPressed,shiftPressed;
	public TileSelectedDisplayPanel(double[] sc, int w, int h) 
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
		if(SwingUtilities.isLeftMouseButton(e))
		{
			commands.get("place_tile").execute(new Object[] {e});
		}
		else if(SwingUtilities.isRightMouseButton(e))
		{
			commands.get("deselect_tile").execute(new Object[] {e});
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) 
	{
		commands.get("handle_motion").execute(new Object[] {e});
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if(SwingUtilities.isLeftMouseButton(e))
		{
			commands.get("handle_drag").execute(new Object[] {e, shiftPressed});
		}
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shiftPressed = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_Z)
		{
			if(ctrlPressed)
			{
				commands.get("restore_previous_map").execute(null);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shiftPressed = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
		if(e.getKeyChar() == 'r')
		{
			commands.get("rotate_tile").execute(null);
		}
	}

	

}
