package core.map.tiles;

import java.awt.Graphics2D;

import core.anims.Animation;
public class AnimatedGrassTile extends AnimatedTile
{
	public AnimatedGrassTile(String animLocation, long animStart, int[] startPos, double startAngle, String textureLocation, double[] sc) 
	{
		super(animLocation, animStart, startPos, startAngle, textureLocation, sc);
	}

	@Override
	public void initAnimation() 
	{
		anim = new Animation(animFolderPath,start,angle,scale,3);
		anim.setRandomFrame();
	}

}
