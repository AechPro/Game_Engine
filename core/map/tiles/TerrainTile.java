package core.map.tiles;

import java.awt.Graphics2D;

public class TerrainTile extends Tile
{
	public TerrainTile(int[] startPos, double angle, String textureLocation, double[] sc)
	{
		super(startPos, angle, textureLocation, sc);
	}
	public TerrainTile(Tile other)
	{
		super(other);
	}
	@Override
	public void init() 
	{
		//loadTexture("resources/textures/tiles/tile"+ID+".png");
	}

	@Override
	public void tUpdate() 
	{
	}

	@Override
	public void tRender(Graphics2D g) 
	{
	}
}
