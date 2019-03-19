package editor.UI.packs.areaSelected.state;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import core.UI.Command;
import core.UI.State;
import core.UI.UIStateMachine;
import core.camera.Camera;
import core.map.tiles.Tile;
import core.map.tiles.*;
import core.util.MathUtil;
import core.util.RectangleSelection;
import editor.Main;
import editor.UI.commands.RestorePreviousMapCommand;
import editor.UI.components.AreaCopy;
import editor.UI.components.TileComponent;
import editor.UI.packs.areaSelected.commands.CopyAreaSelectionCommand;
import editor.UI.packs.areaSelected.commands.CreateCircularAreaCommand;
import editor.UI.packs.areaSelected.commands.DeleteAreaSelectionCommand;
import editor.UI.packs.areaSelected.commands.FillAreaSelectionCommand;
import editor.UI.packs.areaSelected.commands.GiveTargetCommand;
import editor.UI.packs.areaSelected.commands.HandleDragCommand;
import editor.UI.packs.areaSelected.commands.HandleMotionCommand;
import editor.UI.packs.areaSelected.commands.ReleaseAreaSelectionCommand;
import editor.UI.packs.areaSelected.commands.SetTargetCommand;
import editor.UI.packs.areaSelected.panel.AreaSelectDisplayPanel;
import editor.UI.packs.copySelected.state.CopySelectedState;
import editor.UI.packs.idle.state.IdleState;
import editor.level.Map;

public class AreaSelectState extends State
{
	private TileComponent target;
	private RectangleSelection area;
	private static Map map;
	private boolean canSelectNewArea;
	private Rectangle bounds;
	private AreaCopy copiedArea;
	private ArrayList<Tile> tilesInArea, decorationsInArea;
	private ArrayList<double[]> emptyPositions;
	public AreaSelectState(Camera cam, Map m)
	{
		super(cam);
		map = m;
		Command c = new RestorePreviousMapCommand(map);
		displayPanel.attachCommand("restore_previous_map", c);
	}

	@Override
	public void init() 
	{
		displayPanel = new AreaSelectDisplayPanel(camera.getScreenScale(),(int)camera.getViewPort().getWidth(), (int)camera.getViewPort().getHeight());
		Command c = new DeleteAreaSelectionCommand(this);
		displayPanel.attachCommand("delete_area", c);
		c = new HandleDragCommand(this);
		displayPanel.attachCommand("handle_drag", c);
		c = new CopyAreaSelectionCommand(this);
		displayPanel.attachCommand("copy_area", c);
		c = new FillAreaSelectionCommand(this);
		displayPanel.attachCommand("fill_area", c);
		c = new ReleaseAreaSelectionCommand(this);
		displayPanel.attachCommand("release_area", c);
		c = new HandleMotionCommand(this);
		displayPanel.attachCommand("handle_motion", c);
		c = new SetTargetCommand(this);
		displayPanel.attachCommand("set_target", c);
		c = new GiveTargetCommand(this);
		displayPanel.attachCommand("give_target", c);
		c = new CreateCircularAreaCommand(this);
		displayPanel.attachCommand("create_circular_area", c);

		canSelectNewArea = false;
		copiedArea = new AreaCopy(camera.getScreenScale(),"hello world, I am a copy of an area of tiles.");
		tilesInArea = new ArrayList<Tile>();
		decorationsInArea = new ArrayList<Tile>();
		emptyPositions = new ArrayList<double[]>();

	} 

	@Override
	public void onEnter() 
	{
		nextState = null;
		((AreaSelectDisplayPanel)displayPanel).updateTilePanel();
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

	public void handleDrag(MouseEvent e)
	{
		if(canSelectNewArea)
		{
			area.deSelect();
			canSelectNewArea = false;
		}
		select(e.getX(), e.getY());
		area.extendTo(e.getX(), e.getY());
	}
	public void handleMotion(MouseEvent e)
	{
		canSelectNewArea = true;
	}

	public void deleteArea(boolean releaseArea)
	{
		map.saveCurrentSet();
		scaleBounds();

		synchronized(map.getTiles())
		{
			scanArea();
			for(Tile t : tilesInArea)
			{
				map.removeTile(t);
			}
			for(Tile t : decorationsInArea)
			{
				map.removeTile(t);
			}
		}
		if(releaseArea)
		{
			System.gc();
			release();
		}
	}
	public void fillArea()
	{
		if(target == null) {return;}

		map.saveCurrentSet();
		scaleBounds();

		synchronized(map.getTiles())
		{
			scanArea();
			
			Tile tileToAdd = map.buildTile(target);
			for(Tile dec : decorationsInArea)
			{
				map.removeTile(dec);
			}
			for(Tile t : tilesInArea)
			{
				tileToAdd = map.buildTile(target);
				tileToAdd.setPos(t.getPos());

				

				if(!tileToAdd.isDecoration())
				{
					map.removeTile(t);
				}

				map.addTile(tileToAdd);
			}

			for(double[] pos : emptyPositions)
			{
				tileToAdd = map.buildTile(target);
				tileToAdd.setPos(pos);
				map.addTile(tileToAdd);
			}

			System.gc();
			release();
		}

	}
	public void createCircularArea()
	{
		if(target == null)
		{
			return;
		}
		long t1 = System.nanoTime();
		map.saveCurrentSet();
		scaleBounds();
		scanArea();

		int originX = (int)bounds.getCenterX();
		int originY = (int)bounds.getCenterY();
				
		int rx = (int)(bounds.getWidth()/2d);
		int ry = (int)(bounds.getHeight()/2d);
		
		//First, check if there is a tile available to position ourselves relative to.
		Tile center = map.findTile(originX, originY);
		
		if(center != null)
		{
			originX = (int)(center.getX() + center.getWidth()/2);
			originY = (int)(center.getY() + center.getHeight()/2);
		}

		Tile tileToAdd = null;
		
		int numTilesReplaced = 0;
		int cx = 0, cy = 0;
		
		synchronized(map.getTiles())
		{
			for(Tile tile : tilesInArea)
			{
				cx = (int)(tile.getX()+tile.getWidth()/2);
				cy = (int)(tile.getY()+tile.getHeight()/2);
				
				if(Math.pow(Math.abs(cx - originX),2)/(rx*rx) + Math.pow(Math.abs(cy - originY), 2)/(ry*ry) > 1)
				{
					continue;
				}
				
				tileToAdd = map.buildTile(target);
				tileToAdd.setPos(tile.getPos());
				
				for(Tile deco : decorationsInArea)
				{
					if(overlapping(deco,tile))
					{
						map.removeTile(deco);
						break;
					}
				}
				
				map.removeTile(tile);
				map.addTile(tileToAdd);
				numTilesReplaced++;
			}
			
			for(double[] emptyPos : emptyPositions)
			{
				cx = (int)(emptyPos[0]+Tile.TILE_SIZE/2);
				cy = (int)(emptyPos[1]+Tile.TILE_SIZE/2);
				
				if(Math.pow(Math.abs(cx - originX),2)/(rx*rx) + Math.pow(Math.abs(cy - originY), 2)/(ry*ry) > 1)
				{
					continue;
				}
				
				tileToAdd = map.buildTile(target);
				tileToAdd.setPos(emptyPos);
				map.addTile(tileToAdd);
				numTilesReplaced++;
			}
			
		}
		System.out.println("PLACED "+numTilesReplaced+" IN "+(System.nanoTime() - t1)/1000000+"ms");
		release();
	}
	public void moveArea()
	{
		map.saveCurrentSet();
		scaleBounds();
		release();
	}
	public void copyArea()
	{
		map.saveCurrentSet();
		scaleBounds();

		scanArea();
		copiedArea.setScale(camera.getScreenScale());
		copiedArea.setTiles(tilesInArea);
		copiedArea.setDecorations(decorationsInArea);
		//copiedArea.buildImage();

		releaseAndCopy();
	}

	public void setTarget(MouseEvent e)
	{
		int[] p = new int[] {e.getX(),e.getY()};
		if(!IdleState.withinBounds(p[0],p[1],0,0)) {return;}

		double[] sc = camera.getScreenScale();
		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();

		p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],tx,ty});
		Tile t = map.findTile(p[0],p[1]);
		if(t == null){return;}

		Tile deco = map.findDecoration(p[0], p[1]);
		if(deco != null) {t = deco;}

		String textureKey = t.getTexturePath();

		int[] pos = new int[] {(int)t.getX(), (int)t.getY()};

		pos = MathUtil.transform(pos, new double[] {sc[0],sc[1],tx,ty,t.getScale()[0],t.getScale()[1],0,0});

		target = new TileComponent(camera.getScreenScale(),textureKey,pos);
		target.rotate(t.getAngle());

	}
	public void scaleBounds()
	{
		bounds = new Rectangle((int)area.getArea().getX(), (int)area.getArea().getY(), (int)area.getArea().getWidth(), (int)area.getArea().getHeight());
		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();
		double[] sc = camera.getScreenScale();
		double[] coords = MathUtil.transform(new double[] {bounds.getX(), bounds.getY()},new double[] {1,1,0,0,sc[0],sc[1],tx,ty});
		double[] dims = MathUtil.transform(new double[] {bounds.getWidth(), bounds.getHeight()}, new double[] {1,1,0,0,sc[0],sc[1],0,0});

		bounds.setBounds((int)coords[0], (int)coords[1], (int)dims[0], (int)dims[1]);
	}
	private void scanArea()
	{
		tilesInArea.clear();
		decorationsInArea.clear();
		emptyPositions.clear();

		int[] start = new int[2];

		start[0] = (int)bounds.getX();
		start[1] = (int)bounds.getY();

		start = map.getNearestTile(start);
		Tile t = null, deco = null;
		for(double i=start[0]+Tile.TILE_SIZE/2;i<bounds.getX()+bounds.getWidth();i+=Tile.TILE_SIZE)
		{
			for(double j=start[1]+Tile.TILE_SIZE/2;j<bounds.getY()+bounds.getHeight();j+=Tile.TILE_SIZE)
			{
				t = map.findTile((int)i,(int)j);
				if(t != null)
				{
					tilesInArea.add(t);
				}
				deco = map.findDecoration((int)i, (int)j);
				if(deco != null)
				{
					decorationsInArea.add(deco);
				}
				else if(t == null)
				{
					emptyPositions.add(new double[]{(i-Tile.TILE_SIZE/2),(j-Tile.TILE_SIZE/2)});
				}
			}
		}
	}
	private boolean overlapping(Tile a, Tile b)
	{
		return (int)a.getPos()[0] == (int)b.getPos()[0] && (int)a.getPos()[1] == (int)b.getPos()[1];
	}
	public void giveTarget(TileComponent t)
	{
		target = t;
	}
	public void select(int x, int y)
	{
		if(area == null)
		{
			area = new RectangleSelection(x,y,1,1);
			((AreaSelectDisplayPanel)displayPanel).setArea(area);
		}
		else if(!area.isSelected())
		{
			area.select(x,y);
		}
		area.setBounds(map.getLevelBoundaryLeft(), 
				map.getLevelBoundaryTop(), 
				(int)(camera.getViewPort().getWidth()-Tile.TILE_SIZE/2),
				(int)(camera.getViewPort().getHeight())-Tile.TILE_SIZE*2);
	}
	public void release()
	{
		area.deSelect();
		target = null;
		nextState = Main.IDLE_STATE;
	}
	public void releaseAndCopy()
	{
		String dir = tilesInArea.get(0).getTexturePath();
		double[] sc = camera.getScreenScale();
		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();
		int[] p = area.getPos();

		p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],tx,ty});

		System.out.println(p[0]+Tile.TILE_SIZE/2 +" | "+p[1]+Tile.TILE_SIZE/2);

		int[] pos = map.getNearestTile(p);
		copiedArea.setPos(new int[] {pos[0],pos[1]});
		pos = MathUtil.transform(pos, new double[] {sc[0],sc[1],tx,ty,sc[0],sc[1],0,0});

		TileComponent selection = new TileComponent(camera.getScreenScale(),dir,pos);
		
		((CopySelectedState)UIStateMachine.getState(Main.COPY_SELECTED_STATE)).select(selection,copiedArea);

		release();
	}
}
