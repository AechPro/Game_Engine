package editor.UI.packs.idle.panel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import core.UI.Command;
import core.UI.Component;
import core.UI.DisplayPanel;
import core.UI.components.StaticComponent;
import core.util.FileIOHandler;
import core.util.IntegerComparator;
import core.util.TextureHandler;
import editor.UI.components.ButtonComponent;
import editor.UI.components.ScrollbarComponent;
import editor.UI.components.TileComponent;
import editor.UI.components.TilePanelComponent;
import editor.UI.packs.idle.commands.LoadTilePanelCommand;
import editor.UI.packs.idle.commands.NextTileGridCommand;
import editor.UI.packs.idle.commands.PrevTileGridCommand;
import editor.level.Map;

public class IdleDisplayPanel extends DisplayPanel 
{
	
	public static ArrayList<String> imageFilePaths;

	private ScrollbarComponent[] scrollbars;
	private static int componentID;
	public static int currentPanel;
	private ArrayList<ButtonComponent> buttons;
	public static ArrayList<TilePanelComponent> tilePanels;
	private static final JFileChooser fileChooser = new JFileChooser();
	
	protected boolean ctrlPressed;

	public IdleDisplayPanel(double[] sc, int w, int h) 
	{
		super(sc, w, h);
	}

	@Override
	public void initComponents() 
	{
		
		int[] pos;
		String path = "resources/Editor/textures/";
		elements = new ArrayList<Component>();
		buttons = new ArrayList<ButtonComponent>();
		tilePanels = new ArrayList<TilePanelComponent>();
		componentID = 0;

		double[] scale = new double[]{1.0,1.0};

		background = new StaticComponent(scale,path+"UI/frame/frame.png");
		
		imageFilePaths = new ArrayList<String>();
		FileIOHandler.getFilePathsFrom("resources/textures/tiles/grass",".png",imageFilePaths);
		
		buildPanel(scale);

		buildButtons(scale);

		scrollbars = new ScrollbarComponent[2];

		//horizontal
		pos = new int[]{162,height-66};
		scrollbars[0] = new ScrollbarComponent(scale,pos, width, true,""+(componentID++));
		elements.add(scrollbars[0]);

		//vertical
		pos = new int[]{width-38,32};
		scrollbars[1] = new ScrollbarComponent(scale,pos, height, false,""+(componentID++));
		elements.add(scrollbars[1]);
	}

	@Override
	public void panelRender(Graphics2D g) 
	{
		tilePanels.get(currentPanel).render(g);
	}
	
	public void reScale(double[] scale)
	{
		componentScale = scale;
		double delta = 0;
		for(int i=0;i<scrollbars.length;i++)
		{
			delta = scrollbars[i].getDelta();
			commands.get("handle_scrollbar").execute(new Object[]{delta,scrollbars[i].getVertical()});
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
		synchronized(elements)
		{
			if(SwingUtilities.isLeftMouseButton(e))
			{
				tilePanels.get(currentPanel).handleMouseInput(e);
				TileComponent cpy = tilePanels.get(currentPanel).getSelectedTile();
				if(cpy != null)
				{
					cpy.scaleTo(componentScale);
					commands.get("select_tile").execute(new Object[] {cpy});
					return;
				}
				for(int i=0;i<buttons.size();i++)
				{
					buttons.get(i).handleMouseInput(e);
				}
				commands.get("pickup_tile").execute(new Object[]{e});
			}
			else if(SwingUtilities.isRightMouseButton(e))
			{
				commands.get("delete_tile").execute(new Object[] {e});
			}
		}
	
	}
	@Override
	public void mouseMoved(MouseEvent e) 
	{
		commands.get("handle_motion").execute(new Object[]{e});
		for(int i=0;i<scrollbars.length;i++)
		{
			scrollbars[i].setSelected(false);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		double delta = 0;
		boolean scrollbarSelected = false;
		for(int i=0;i<scrollbars.length;i++)
		{
			scrollbars[i].handleMouseInput(e);
			if(scrollbars[i].isSelected()) {scrollbarSelected = true;}

			delta = scrollbars[i].getDelta();
			commands.get("handle_scrollbar").execute(new Object[]{delta,scrollbars[i].getVertical()});
		}
		if(!scrollbarSelected)
		{
			commands.get("handle_drag").execute(new Object[] {e});
		}
		
	}
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = true;
		}
		if(ctrlPressed)
		{
			if(e.getKeyCode() == KeyEvent.VK_Z)
			{
				commands.get("restore_previous_map").execute(null);
			}
			if(e.getKeyCode() == KeyEvent.VK_S)
			{
				commands.get("save_map").execute(new Object[] {"resources/saved_level.txt"});
			}
			if(e.getKeyCode() == KeyEvent.VK_V)
			{
				commands.get("paste_area").execute(null);
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
		if(e.getKeyChar() == 'i')
		{
			commands.get("zoom_in").execute(null);
		}
		else if(e.getKeyChar() == 'o')
		{
			commands.get("zoom_out").execute(null);
		}
	}

	public static void loadPanel()
	{
		fileChooser.setCurrentDirectory(new File(Map.BASE_TILE_DIRECTORY));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			imageFilePaths.clear();
			File f = fileChooser.getSelectedFile();
			FileIOHandler.getFilePathsFrom(f.getPath(),".png", imageFilePaths);
			buildPanel(new double[] {1,1});
		}
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

	private static void buildPanel(double[] scale)
	{
		tilePanels = new ArrayList<TilePanelComponent>();
		int gridStart = 14;
		int[] pos = new int[] {0,0};
		Component c = null;
		
		ArrayList<TileComponent> allTiles = new ArrayList<TileComponent>();
		TilePanelComponent panel = new TilePanelComponent(scale, gridStart,""+(componentID++));
		
		tilePanels.add(panel);
		ArrayList<Integer> ints = new ArrayList<Integer>();
		
		Color color = null;
		for(int i=0;i<imageFilePaths.size();i++)
		{
			if(imageFilePaths.get(i).contains("spritesheet"))
			{
				continue;
			}
			
			c = new TileComponent(scale,imageFilePaths.get(i),pos);
			
			if(imageFilePaths.get(i).contains(Map.TERRAIN_TILE_DIRECTORY_EXT))
			{
				color = Color.GREEN;
			}
			else if(imageFilePaths.get(i).contains(Map.WALL_TILE_DIRECTORY_EXT))
			{
				color = Color.RED;
			}
			else if(imageFilePaths.get(i).contains(Map.DECORATION_TILE_DIRECTORY_EXT))
			{
				color = Color.YELLOW;
			}
			
			c.setOutlineColor(color);
			allTiles.add((TileComponent)c);
			ints.add(((TileComponent)c).getIntID());
		}

		/*
		 * Add tiles to component panels sorted numerically from least to greatest. This is 
		 * unnecessary and inefficient, I just wanted to do it quickly.
		 */

		ints.sort(new IntegerComparator());
		for(int i=0;i<ints.size();i++)
		{
			for(int j=0;j<allTiles.size();j++)
			{
				//System.out.println(ints.get(i)+" "+allTiles.get(j).getIntID());
				if(allTiles.get(j).getIntID() == ints.get(i))
				{
					if(!panel.addTile(allTiles.get(j)))
					{
						panel = new TilePanelComponent(scale, gridStart,""+(componentID++));
						panel.addTile(allTiles.get(j));
						tilePanels.add(panel);
					}
					break;
				}
			}
		}
		
		currentPanel = 0;

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
