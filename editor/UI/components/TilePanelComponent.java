package editor.UI.components;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import core.UI.Component;

public class TilePanelComponent extends Component
{
	private TileComponent[][] grid;
	private int totalTiles;
	private static final int ROWS = 13, COLS = 4;
	private int x,y;
	private int gridStart;
	private TileComponent selectedTile;
	public TilePanelComponent(double[] sc, int gStart, String id)
	{
		super(sc,null,id);
		gridStart = gStart;
		grid = new TileComponent[COLS][ROWS];
		totalTiles = 0;
		x = 0;
		y = 0;
		selectedTile = null;
	}
	public boolean addTile(TileComponent t)
	{
		if(full()) {return false;}
		
		if(x >= COLS)
		{
			x = 0;
			y++;
		}
		if(y >= ROWS)
		{
			return false;
		}
		
		t.setPos(new int[] {gridStart+44*x,(64+gridStart)+(44*y)});
		grid[x][y] = t;
		x++;
		totalTiles++;
		return true;
	}
	public TileComponent getSelectedTile()
	{
		if(selectedTile == null){return null;}
		TileComponent ret = (TileComponent) selectedTile.copy();
		selectedTile = null;
		return ret;
	}
	@Override
	public void cRender(Graphics2D g) 
	{
		for(int i=0;i<COLS;i++)
		{
			for(int j=0;j<ROWS;j++)
			{
				if(grid[i][j] != null)
				{
					grid[i][j].render(g);
				}
			}
		}
	}
	@Override
	public void handleMouseInput(MouseEvent e) 
	{
		for(int i=0;i<COLS;i++)
		{
			for(int j=0;j<ROWS;j++)
			{
				if(grid[i][j] != null)
				{
					if(grid[i][j].intersects(e))
					{
						selectedTile = grid[i][j];
					}
				}
			}
		}
	}

	@Override
	public void handleKeyInput(KeyEvent e) 
	{
	}

	@Override
	public void update() 
	{
	}

	@Override
	public Component copy() 
	{
		return null;
	}
	
	public boolean full()
	{
		return totalTiles >= ROWS*COLS;
	}
	@Override
	public void componentInit() {
		// TODO Auto-generated method stub
		
	}
	
}
