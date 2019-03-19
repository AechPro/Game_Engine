package editor.UI.components;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import core.UI.Component;
import core.map.Chunk;
import core.map.tiles.Tile;

public class AreaCopy extends Component
{	
	private ArrayList<Tile> tiles, decorations;
	private Chunk chunk;
	public AreaCopy(double[] sc, String texturePath) 
	{
		super(sc, null, texturePath);
	}
	@Override
	public void componentInit() 
	{
		tiles = new ArrayList<Tile>();
		decorations = new ArrayList<Tile>();
		pos = new int[2];
		
	}
	public void buildImage()
	{
		computeWidth();
		computeHeight();
		chunk = new Chunk(pos[0],pos[1],width/Tile.TILE_SIZE,height/Tile.TILE_SIZE,scale);
		for(Tile t : tiles)
		{
			chunk.addTile(t);
		}
		for(Tile d : decorations)
		{
			chunk.addTile(d);
		}
		chunk.buildImage();
	}
	
	private void computeWidth()
	{
		int w = 0;
		int max = 0;
		for(Tile t : tiles)
		{
			if(t.getPos()[0] > max)
			{
				w += (int)(t.getWidth());
				max = (int)(t.getPos()[0]);
			}
		}
		width = w;
	}
	private void computeHeight()
	{
		int h = 0;
		int max = 0;
		for(Tile t : tiles)
		{
			if(t.getPos()[1] > max)
			{
				h += (int)(t.getHeight());
				max = (int)(t.getPos()[1]);
			}
		}
		height = h;
		
	}
	
	@Override
	public void rotate(double theta)
	{
		chunk.rotate(theta);
		
		for(Tile t : tiles)
		{
			t.setAngle(t.getAngle()+theta);
		}
		
		for(Tile t : decorations)
		{
			t.setAngle(t.getAngle()+theta);
		}
	}
	
	public void setPos(int[] newPos)
	{
		pos = newPos;
		if(chunk != null)
		{
			chunk.setPos(newPos);
		}
	}
	public void setX(int x)
	{
		pos[0] = x;
	}
	public void setY(int y)
	{
		pos[1] = y;
	}
	public int[] getPos()
	{
		return pos;
	}
	public void setTiles(ArrayList<Tile> ts)
	{
		tiles.clear();
		for(Tile t : ts)
		{
			tiles.add(t);
		}
	}
	public void setDecorations(ArrayList<Tile> ds)
	{
		decorations.clear();
		for(Tile t : ds)
		{
			decorations.add(t);
		}
	}
	public Tile[][] getTiles()
	{
		return chunk.getTiles();
	}
	public Tile[][] getDecorations()
	{
		return chunk.getDecorations();
	}
	@Override
	public void handleMouseInput(MouseEvent e) 
	{
		
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
	@Override
	public void cRender(Graphics2D g) 
	{
		if(chunk != null)
		{
			chunk.renderBackground(g);
			chunk.renderForeground(g);
		}
	}
	public void setScale(double[] sc)
	{
		scale = sc;
	}
	
}
