package core.map.tiles;

import java.awt.Graphics2D;

public class DecorationTile extends Tile
{

	public DecorationTile(int[] startPos, double startAngle, String textureLocation, double[] sc) 
	{
		super(startPos, startAngle, textureLocation, sc);
	}
	public DecorationTile(Tile decoration)
	{
		super(decoration);
	}
	@Override
	public void init() 
	{
		decoration = true;
		collidable = false;
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
