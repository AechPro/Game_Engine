package core.map.tiles;

import java.awt.Graphics2D;

public class WallTile extends Tile
{

	public WallTile(int[] startPos, double angle, String textureLocation, double[] sc) 
	{
		super(startPos, angle, textureLocation, sc);
	}
	public WallTile(Tile other)
	{
		super(other);
	}
	@Override
	public void init() 
	{
		//loadTexture("resources/textures/tiles/tile"+ID+".png");
		collidable = true;
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
