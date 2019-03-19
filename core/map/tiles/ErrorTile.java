package core.map.tiles;

import java.awt.Graphics2D;

public class ErrorTile extends Tile 
{
	public ErrorTile(int[] startPos, double angle, double[] sc) 
	{
		super(startPos, angle, "", sc);
		System.out.println("ERROR TILE BEING LOADED");
	}

	@Override
	public void init() 
	{
		//loadTexture("resources/textures/tiles/tile99.png");
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
