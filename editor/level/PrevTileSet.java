package editor.level;

import java.util.ArrayList;

import core.map.tiles.Tile;

public class PrevTileSet 
{
	private ArrayList<Tile> tiles;
	private ArrayList<Tile> decorations;
	public PrevTileSet()
	{
		tiles = new ArrayList<Tile>();
		decorations = new ArrayList<Tile>();
	}
	
	public void setTiles(ArrayList<Tile> tileSet)
	{
		tiles.clear();
		for(Tile t : tileSet)
		{
			tiles.add(t);
		}
	}
	
	public void setDecorations(ArrayList<Tile> tileSet)
	{
		decorations.clear();
		for(Tile t : tileSet)
		{
			decorations.add(t);
		}
	}
	
	public ArrayList<Tile> getTiles()
	{
		ArrayList<Tile> lst = new ArrayList<Tile>();
		for(Tile t : tiles)
		{
			lst.add(t);
		}
		return lst;
	}
	public ArrayList<Tile> getDecorations()
	{
		ArrayList<Tile> lst = new ArrayList<Tile>();
		for(Tile t : decorations)
		{
			lst.add(t);
		}
		return lst;
	}
}
