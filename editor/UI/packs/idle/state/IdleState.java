package editor.UI.packs.idle.state;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import core.UI.Command;
import core.UI.State;
import core.UI.UIStateMachine;
import core.camera.Camera;
import core.map.tiles.Tile;
import core.util.MathUtil;
import editor.Main;
import editor.UI.commands.RestorePreviousMapCommand;
import editor.UI.components.TileComponent;
import editor.UI.packs.areaSelected.state.AreaSelectState;
import editor.UI.packs.idle.commands.*;
import editor.UI.packs.idle.panel.IdleDisplayPanel;
import editor.UI.packs.tileSelected.state.TileSelectedState;
import editor.level.Map;

public class IdleState extends State
{
	private static Map map;
	private boolean dragStart;
	private double scaleValue;
	private double scaleIncrement;
	public IdleState(Camera cam, Map m) 
	{
		super(cam);
		map = m;
		Command c = new RestorePreviousMapCommand(map);
		displayPanel.attachCommand("restore_previous_map", c);
		c = new SaveMapCommand(map);
		displayPanel.attachCommand("save_map", c);
	}

	@Override
	public void init() 
	{
		displayPanel = new IdleDisplayPanel(camera.getScreenScale(), (int)camera.getViewPort().getWidth(), (int)camera.getViewPort().getHeight());
		Command c = new SelectTileCommand(this);
		displayPanel.attachCommand("select_tile", c);
		c = new PickupTileCommand(this);
		displayPanel.attachCommand("pickup_tile", c);
		c = new HandleScrollbarCommand(this);
		displayPanel.attachCommand("handle_scrollbar", c);
		c = new ZoomInCommand(this);
		displayPanel.attachCommand("zoom_in", c);
		c = new ZoomOutCommand(this);
		displayPanel.attachCommand("zoom_out", c);
		c = new HandleDragCommand(this);
		displayPanel.attachCommand("handle_drag", c);
		c = new DeleteTileCommand(this);
		displayPanel.attachCommand("delete_tile", c);
		c = new HandleMotionCommand(this);
		displayPanel.attachCommand("handle_motion", c);
		c = new PasteAreaCommand(this);
		displayPanel.attachCommand("paste_area", c);
		scaleValue = 1.0;
		scaleIncrement = 0.25;
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
		dragStart = false;
	}
	public void handleDrag(MouseEvent e)
	{
		if(SwingUtilities.isRightMouseButton(e))
		{
			if(!dragStart)
			{
				dragStart = true;
				map.saveCurrentSet();
			}
			deleteTile(e);
		}
		else if(SwingUtilities.isLeftMouseButton(e))
		{
			if(withinBounds(e.getX(), e.getY(),0,0))
			{
				((AreaSelectState)UIStateMachine.getState(Main.AREA_SELECT_STATE)).select(e.getX(),e.getY());
				nextState = Main.AREA_SELECT_STATE;
			}
		}
	}
	public void pickupTile(MouseEvent e)
	{
		map.saveCurrentSet();
		int[] p = new int[] {e.getX(),e.getY()};
		if(!withinBounds(p[0],p[1],0,0)) {return;}

		double[] sc = camera.getScreenScale();
		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();
		
		p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],tx,ty});
		Tile t = map.findTile(p[0],p[1]);
		if(t == null){return;}
		
		Tile deco = map.findDecoration(p[0], p[1]);
		if(deco != null) {t = deco;}
		
		String textureKey = t.getTexturePath();
		map.removeTile(t);
		
		int[] pos = new int[] {(int)t.getX(), (int)t.getY()};

		pos = MathUtil.transform(pos, new double[] {sc[0],sc[1],tx,ty,t.getScale()[0],t.getScale()[1],0,0});

		TileComponent selection = new TileComponent(camera.getScreenScale(),textureKey,pos);
		selection.rotate(t.getAngle());
		
		((TileSelectedState)UIStateMachine.getState(Main.TILE_SELECTED_STATE)).selectTile(selection);
		nextState = Main.TILE_SELECTED_STATE;
		map.saveCurrentSet();
	}
	public void selectTile(TileComponent t)
	{
		((TileSelectedState)UIStateMachine.getState(Main.TILE_SELECTED_STATE)).selectTile(t);
		nextState = Main.TILE_SELECTED_STATE;
	}
	public void zoomIn()
	{
		double[] scale = camera.getScreenScale();
		
		if(scaleValue >= 5){return;}
		
		scaleValue+=scaleIncrement;
		scale[0]=scaleValue;
		scale[1]=scaleValue;
		
		camera.setScreenScale(scale);
		map.reScale();
		((IdleDisplayPanel)displayPanel).reScale(scale);
		
		System.out.println("Zoomed in!");
	}
	public void zoomOut()
	{
		double[] scale = camera.getScreenScale();
		
		if(scaleValue <= 0.25){return;}
		
		scaleValue-=scaleIncrement;
		scale[0]=scaleValue;
		scale[1]=scaleValue;
		
		camera.setScreenScale(scale);
		map.reScale();
		((IdleDisplayPanel)displayPanel).reScale(scale);
		
		System.out.println("Zoomed out!");
	}
	public void pasteArea()
	{
		nextState = Main.COPY_SELECTED_STATE;
	}
	public void handleScrollbar(double delta, boolean vertical)
	{
		int mapH = map.getPixelHeight();
		int mapW = map.getPixelWidth();
		if(vertical)
		{
			double orig = camera.getSubject().getOrigin()[1];
			double track = mapH - orig*2 + Tile.TILE_SIZE*2;
			camera.getSubject().setY(orig + track*delta);
		}
		else
		{
			double orig = camera.getSubject().getOrigin()[0];
			double track = mapW - orig*2 + Tile.TILE_SIZE*2;
			camera.getSubject().setX(orig + track*delta);
		}
	}
	public void deleteTile(MouseEvent e)
	{
		double tx = camera.getTransform().getTranslateX();
		double ty = camera.getTransform().getTranslateY();
		double[] sc = camera.getScreenScale();
		int[] p = new int[] {e.getX(),e.getY()};
		if(withinBounds(p[0],p[1],0,0))
		{
			p = MathUtil.transform(p, new double[] {1,1,0,0,sc[0],sc[1],tx,ty});
			
			if(map.removeDecoration(p[0],p[1])) {return;}
			map.removeTile(p[0], p[1]);
		}
	}
	public static boolean withinBounds(int x, int y, int w, int h)
	{
		int bl = map.getLevelBoundaryLeft();
		int br = map.getLevelBoundaryRight();
		int bt = map.getLevelBoundaryTop();
		int bb = map.getLevelBoundaryBottom();

		return x > bl && x+w < br && y > bt && y+h < bb;
	}
}
