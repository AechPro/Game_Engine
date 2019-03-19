package editor.UI.packs.tileSelected.state;

import java.awt.Color;
import java.awt.event.MouseEvent;

import core.UI.Command;
import core.UI.State;
import core.camera.Camera;
import core.map.tiles.Tile;
import core.util.MathUtil;
import editor.Main;
import editor.UI.commands.RestorePreviousMapCommand;
import editor.UI.components.TileComponent;
import editor.UI.packs.idle.state.IdleState;
import editor.UI.packs.tileSelected.commands.DeselectTileCommand;
import editor.UI.packs.tileSelected.commands.HandleDragCommand;
import editor.UI.packs.tileSelected.commands.HandleMotionCommand;
import editor.UI.packs.tileSelected.commands.PlaceTileCommand;
import editor.UI.packs.tileSelected.commands.RotateTileCommand;
import editor.UI.packs.tileSelected.panel.TileSelectedDisplayPanel;
import editor.level.Map;

public class TileSelectedState extends State
{
	private TileComponent selectedTile;
	private static Map map;
	public TileSelectedState(Camera cam, Map m) 
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
		Command c = new HandleDragCommand(this);
		displayPanel.attachCommand("handle_drag", c);
		
		c = new HandleMotionCommand(this);
		displayPanel.attachCommand("handle_motion", c);
		
		c = new DeselectTileCommand(this);
		displayPanel.attachCommand("deselect_tile", c);
		
		c = new PlaceTileCommand(this);
		displayPanel.attachCommand("place_tile", c);
		
		c = new RotateTileCommand(this);
		displayPanel.attachCommand("rotate_tile", c);
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
	
	public void handleDrag(MouseEvent e, boolean randomRotate)
	{
		if(trackToMouse(e))
		{
			if(randomRotate)
			{
				int num1 = (int)Math.round(Math.random()*100);
				int num2 = (int)Math.round(Math.random()*100);
				int num3 = num1 ^ num2;
				num1 = (int)Math.round(Math.random()*Math.random()*num3);
				for(int i=0;i<num1;i++)
				{
					rotateTile();
				}
				//System.out.println(num1);
				
			}
			placeTile(false);
			
		}
	}
	
	
	public boolean trackToMouse(MouseEvent e)
	{
		if(selectedTile == null){return false;}
		
		int x = e.getX();
		int y = e.getY();

		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();
		double[] sc = camera.getScreenScale();
		int[] p = new int[] {x-Tile.TILE_SIZE/2,y-Tile.TILE_SIZE/2};
		
		if(IdleState.withinBounds(p[0],p[1],0,0))
		{
			p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],0,0});
			if(selectedTile.isOOB())
			{
				p = MathUtil.transform(p, new double[] {sc[0],sc[1],0,0,sc[0],sc[1],tx,ty});
				
				int[] pos = map.getNearestTile(p);
				System.out.println("nearest tile pos: "+pos[0]+" "+pos[1]);
				pos = MathUtil.transform(pos, new double[] {sc[0],sc[1],tx,ty,sc[0],sc[1],0,0});
				System.out.println("setting selected tile to: "+pos[0]+" "+pos[1]);
				selectedTile.setPos(pos);
				selectedTile.OOB(false);
				
				p = MathUtil.transform(new int[] {x-Tile.TILE_SIZE/2,y-Tile.TILE_SIZE/2}, new double[] {1,1,0,0,sc[0],sc[1],0,0});
				System.out.println("moving selected tile to: "+p[0]+" "+p[1]);
				selectedTile.moveTo(p[0],p[1]);
			}
			else
			{
				selectedTile.moveTo(p[0],p[1]);
			}
			
			return true;
		}
		
		p[0] = x-Tile.TILE_SIZE/2;
		p[1] = y-Tile.TILE_SIZE/2;
		
		p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],0,0});
		
		selectedTile.OOB(true);
		selectedTile.setPos(new int[] {p[0],p[1]});
		
		return false;
	}
	public void placeTile(boolean deselect)
	{	
		if(selectedTile == null)
		{
			return;
		}
		int[] pos = selectedTile.getPos();
		double[] sc = camera.getScreenScale();
		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();
		
		pos = MathUtil.transform(pos, new double[] {sc[0],sc[1],0,0,1,1,0,0});

		if(IdleState.withinBounds(pos[0], pos[1], selectedTile.getWidth(), selectedTile.getHeight()))
		{
			pos = selectedTile.getPos();
			pos = MathUtil.transform(pos, new double[] {sc[0],sc[1],0,0,sc[0],sc[1],tx,ty});
			
			Tile t = map.findTile(pos[0]+1,pos[1]+1);
			
			if(t == null)
			{
				int[] old = selectedTile.getPos();
				selectedTile.setPos(pos);
				//System.out.println(pos[0]+" "+pos[1]);
				map.addTile(selectedTile);
				
				if(deselect) {deselectTile();}
				else {selectedTile.setPos(old);}
			}
			else if(selectedTile.isDecoration() && !t.isDecoration())
			{
				
				if(map.findDecoration(pos[0]+1, pos[1]+1) == null)
				{
					int[] old = selectedTile.getPos();
					selectedTile.setPos(pos);
					map.addTile(selectedTile);
					
					if(deselect) {deselectTile();}
					else {selectedTile.setPos(old);}
				}
			}
		}
		else
		{
			System.out.println("("+pos[0]+","+pos[1]+") out of bounds!");
			System.out.println("Boundary: ("+map.getLevelBoundaryRight()+","+map.getLevelBoundaryBottom()+")");
		}
	}
	public void rotateTile()
	{
		selectedTile.rotate(Math.PI/2);
	}
	public void deselectTile()
	{
		displayPanel.removeComponent(selectedTile);
		selectedTile = null;
		nextState = Main.IDLE_STATE;
	}
	public void selectTile(TileComponent t)
	{
		if(selectedTile != null) {return;}
		
		selectedTile = t;
		selectedTile.setOutlineColor(Color.YELLOW);
		displayPanel.addComponent(t);
	}
}
