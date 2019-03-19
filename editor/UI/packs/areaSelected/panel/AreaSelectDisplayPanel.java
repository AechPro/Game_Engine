package editor.UI.packs.areaSelected.panel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import core.UI.Command;
import core.UI.Component;
import core.UI.DisplayPanel;
import core.UI.components.StaticComponent;
import core.util.RectangleSelection;
import core.util.TextureHandler;
import editor.UI.components.ButtonComponent;
import editor.UI.components.TileComponent;
import editor.UI.components.TilePanelComponent;
import editor.UI.packs.areaSelected.commands.*;
import editor.UI.packs.idle.panel.IdleDisplayPanel;

public class AreaSelectDisplayPanel extends DisplayPanel
{
	private RectangleSelection area;
	private ArrayList<TilePanelComponent> tilePanels;
	private ArrayList<ButtonComponent> buttons;
	private int currentPanel;
	private int componentID;
	private boolean shiftPressed, ctrlPressed;
	
	public AreaSelectDisplayPanel(double[] sc, int w, int h) 
	{
		super(sc, w, h);
	}

	@Override
	public void initComponents() 
	{
		String path = "resources/Editor/textures/";
		elements = new ArrayList<Component>();
		componentID = 0;
		currentPanel = 0;

		double[] scale = new double[]{1.0,1.0};

		background = new StaticComponent(scale,path+"UI/frame/frame.png");
		tilePanels = IdleDisplayPanel.tilePanels;

		buildButtons(scale);
		
	}
	
	public void setArea(RectangleSelection newArea)
	{
		area = newArea;
	}
	
	@Override
	public void panelRender(Graphics2D g) 
	{
		if(tilePanels != null && tilePanels.get(currentPanel) != null)
		{
			tilePanels.get(currentPanel).render(g);
		}
		if(area != null)
		{
			g.setColor(Color.YELLOW);
			g.draw(area.getArea());
		}
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
			tilePanels.get(currentPanel).handleMouseInput(e);
			TileComponent cpy = tilePanels.get(currentPanel).getSelectedTile();
			if(cpy != null)
			{
				cpy.scaleTo(componentScale);
				commands.get("give_target").execute(new Object[] {cpy});
			}
			else
			{
				commands.get("set_target").execute(new Object[] {e});
			}
			for(int i=0;i<buttons.size();i++)
			{
				buttons.get(i).handleMouseInput(e);
			}
			if(!shiftPressed)
			{
				commands.get("fill_area").execute(null);
			}
			else
			{
				commands.get("create_circular_area").execute(null);
			}
		}
	}

	@Override
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
		if(e.getKeyCode() == KeyEvent.VK_C)
		{
			if(ctrlPressed)
			{
				commands.get("copy_area").execute(null);
			}
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
		if(e.getKeyChar() == '\n')
		{
			commands.get("release_area").execute(null);
		}
		if(e.getKeyChar() == '\b')
		{
			commands.get("delete_area").execute(null);
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
		commands.get("handle_drag").execute(new Object[] {e});
	}
	public void updateTilePanel()
	{
		tilePanels = IdleDisplayPanel.tilePanels;
		currentPanel = IdleDisplayPanel.currentPanel;
	}
	public void loadPanel()
	{
		IdleDisplayPanel.loadPanel();
		updateTilePanel();
	}
	public void nextTileGrid()
	{
		if(currentPanel+1 >= tilePanels.size()){return;}
		currentPanel++;
	}
	public void prevTileGrid()
	{
		if(currentPanel-1 < 0){return;}
		currentPanel--;
	}
	
	private void buildButtons(double[] scale)
	{
		Command comm = null;
		ButtonComponent button = null;
		buttons = new ArrayList<ButtonComponent>();

		int[] p = new int[] {34,656};
		int w = 27;
		int h = 27;

		w*=scale[0];
		h*=scale[1];
		p[0]*=scale[0];
		p[1]*=scale[1];

		comm = new PrevTileGridCommand(this);
		button = new ButtonComponent(scale, comm, w, h,""+(componentID++));

		button.setPos(p);

		buttons.add(button);
		elements.add(button);

		p[0] = 34+96;
		p[1] = 656;
		w = 27;
		h = 27;

		w*=scale[0];
		h*=scale[1];
		p[0]*=scale[0];
		p[1]*=scale[1];

		comm = new NextTileGridCommand(this);
		button = new ButtonComponent(scale, comm, w, h,""+(componentID++));

		button.setPos(p);

		buttons.add(button);
		elements.add(button);

		p[0] = 64+32;
		p[1] = 0;
		w = 64+32;
		h = 30;

		w*=scale[0];
		h*=scale[1];
		p[0]*=scale[0];
		p[1]*=scale[1];

		comm = new LoadTilePanelCommand(this);
		button = new ButtonComponent(scale, comm, w, h,""+(componentID++));
		BufferedImage tex = TextureHandler.loadTexture("resources/Editor/textures/UI/frame/MenuButtonLoadSet.png", scale);
		button.setTexture(tex,scale);

		button.setPos(p);
		//button.setOutlineColor(Color.YELLOW);

		buttons.add(button);
		elements.add(button);
	}

}
