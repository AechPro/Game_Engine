package editor.UI.packs.copySelected.state;

import java.awt.Color;
import java.awt.event.MouseEvent;
import core.UI.Command;
import core.UI.State;
import core.camera.Camera;
import core.map.tiles.DecorationTile;
import core.map.tiles.TerrainTile;
import core.map.tiles.Tile;
import core.map.tiles.WallTile;
import core.util.MathUtil;
import editor.Main;
import editor.UI.commands.RestorePreviousMapCommand;
import editor.UI.components.AreaCopy;
import editor.UI.components.TileComponent;
import editor.UI.packs.idle.state.IdleState;
import editor.UI.packs.copySelected.commands.*;
import editor.UI.packs.tileSelected.panel.TileSelectedDisplayPanel;
import editor.level.Map;

public class CopySelectedState extends State
{
	private Map map;
	private TileComponent selectedTile;
	private AreaCopy copy;
	public CopySelectedState(Camera cam, Map m) 
	{
		super(cam);
		map = m;
		Command c = new RestorePreviousMapCommand(map);
		displayPanel.attachCommand("restore_previous_map", c);
	}

	@Override
	public void init() 
	{
		displayPanel = new TileSelectedDisplayPanel(camera.getScreenScale(), (int)camera.getViewPort().getWidth(), (int)camera.getViewPort().getHeight());
		Command c = new HandleMotionCommand(this);
		displayPanel.attachCommand("handle_motion", c);
		
		c = new DeselectCopyCommand(this);
		displayPanel.attachCommand("deselect_tile", c);
		
		c = new PlaceCopyCommand(this);
		displayPanel.attachCommand("place_tile", c);
		
		c = new RotateCopyCommand(this);
		displayPanel.attachCommand("rotate_tile", c);
		
		c = new HandleDragCommand(this);
		displayPanel.attachCommand("handle_drag", c);
		
		selectedTile = null;
	}

	@Override
	public void onEnter() 
	{
		nextState = null;
	}

	@Override
	public void onExit() 
	{
	}

	@Override
	public void stateUpdate() 
	{	
		map.update();
	}
	public void handleMotion(MouseEvent e)
	{
		trackToMouse(e);
	}
	
	public void handleDrag(MouseEvent e)
	{
		
	}
	
	public boolean trackToMouse(MouseEvent e)
	{
		return trackToMouse(e.getX(),e.getY());
	}
	public boolean trackToMouse(int x, int y)
	{
		if(copy == null){return false;}
		
		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();
		double[] sc = camera.getScreenScale();
		int[] p = new int[] {x-copy.getWidth()/2,y-copy.getHeight()/2};
		
		if(IdleState.withinBounds(p[0],p[1],0,0))
		{
			p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],0,0});
			if(selectedTile.isOOB())
			{
				p = MathUtil.transform(p, new double[] {sc[0],sc[1],0,0,sc[0],sc[1],tx,ty});
				
				int[] pos = map.getNearestTile(p);
				pos = MathUtil.transform(pos, new double[] {sc[0],sc[1],tx,ty,sc[0],sc[1],0,0});
				
				selectedTile.setPos(pos);
				selectedTile.OOB(false);
				
				p = MathUtil.transform(p, new double[] {sc[0],sc[1],tx,ty,sc[0],sc[1],0,0});
				selectedTile.moveTo(p[0],p[1]);
			}
			else
			{
				selectedTile.moveTo(p[0],p[1]);
			}
			
			copy.setPos(selectedTile.getPos());
			return true;
		}
		
		p[0] = x-copy.getWidth()/2;
		p[1] = y-copy.getHeight()/2;
		
		p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],0,0});
		
		selectedTile.OOB(true);
		selectedTile.setPos(new int[] {p[0],p[1]});
		copy.setPos(selectedTile.getPos());
		return false;
	}
	public void placeCopy(MouseEvent e)
	{	
		if(!trackToMouse(e)){return;}
		
		synchronized(map.getTiles())
		{
			double[] sc = camera.getScreenScale();
			int tx = (int)camera.getTransform().getTranslateX();
			int ty = (int)camera.getTransform().getTranslateY();
			
			Tile[][] tiles = copy.getTiles();
			Tile[][] decorations = copy.getDecorations();
			
			double[] pos = new double[2];
			
			int[] orig = MathUtil.transform(copy.getPos(), new double[] {sc[0],sc[1],0,0,sc[0],sc[1],tx,ty});

			System.out.println(orig[0]+" "+orig[1]);
			Tile t = null, tileToAdd = null;
			for(int i=0;i<tiles.length;i++)
			{
				for(int j=0;j<tiles[i].length;j++)
				{
					pos[0] = orig[0] + Tile.TILE_SIZE*i;
					pos[1] = orig[1] + Tile.TILE_SIZE*j;
					
					t = map.findTile((int)pos[0]+1,(int)pos[1]+1);
					if(t != null)
					{
						map.removeTile(t);
					}
					
					t = map.findDecoration((int)pos[0]+1,(int)pos[1]+1);
					if(t != null)
					{
						map.removeTile(t);
					}
					
					if(tiles[i][j] == null) 
					{
						continue;
					}
					
					
					if(tiles[i][j].isCollidable())
					{
						tileToAdd = new WallTile(tiles[i][j]);
					}
					else
					{
						tileToAdd = new TerrainTile(tiles[i][j]);
					}
					
					tileToAdd.setPos(pos);
					
					map.addTile(tileToAdd);
				}
			}
			for(int i=0;i<decorations.length;i++)
			{
				for(int j=0;j<decorations[i].length;j++)
				{
					if(decorations[i][j] == null) {continue;}
					
					pos[0] = orig[0] + Tile.TILE_SIZE*i;
					pos[1] = orig[1] + Tile.TILE_SIZE*j;
					
					tileToAdd = new DecorationTile(decorations[i][j]);
					tileToAdd.setPos(pos);
					map.addTile(tileToAdd);
				}
			}
		}
		System.gc();
	}
	public void rotateCopy()
	{
		copy.rotate(Math.PI/2);
	}
	public void deselectCopy()
	{
		displayPanel.removeComponent(copy);
		selectedTile = null;
		copy = null;
		nextState = Main.IDLE_STATE;
	}
	public void select(TileComponent t, AreaCopy area)
	{
		if(selectedTile != null) {return;}
		
		selectedTile = t;
		copy = area;
		copy.buildImage();
		
		copy.setOutlineColor(Color.YELLOW);
		displayPanel.addComponent(copy);
		trackToMouse(0,0);
	}
}