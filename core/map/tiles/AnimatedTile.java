package core.map.tiles;

import java.awt.Graphics2D;

import core.anims.Animation;

public abstract class AnimatedTile extends Tile
{
	protected Animation anim;
	protected String animFolderPath;

	protected long start;
	public abstract void initAnimation();
	public AnimatedTile(String animLocation, long animStart, int[] startPos, double startAngle, String textureLocation, double[] sc) 
	{
		super(startPos, startAngle, textureLocation, sc);
		animFolderPath = animLocation;
		start = animStart;
		initAnimation();
	}

	@Override
	public void init() 
	{
		
	}

	@Override
	public void tUpdate() 
	{
		if(anim != null)
		{
			anim.update();
		}
	}

	@Override
	public void tRender(Graphics2D g) 
	{
		if(anim != null)
		{
			anim.render(g, renderX, renderY, false);
		}
	}
}
