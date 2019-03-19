package core.map.tiles;

import core.anims.Animation;
import core.util.TextureHandler;

public class AnimatedWaterTile extends AnimatedTile
{
	public AnimatedWaterTile(String animLocation, long animStart, int[] startPos, double startAngle, String textureLocation, double[] sc) 
	{
		super(animLocation, animStart, startPos, startAngle, textureLocation, sc);
		collidable = true;
	}

	@Override
	public void initAnimation() 
	{
		int[] spriteSheetInstructions = new int[TextureHandler.NUM_SHEET_INSTRUCTIONS];

		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_X] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_X] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_Y] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_WIDTH] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_HEIGHT] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_STEP_X] = 32;
		spriteSheetInstructions[TextureHandler.SHEET_STEP_Y] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_SUBIMAGE_HEIGHT] = 32;
		spriteSheetInstructions[TextureHandler.SHEET_SUBIMAGE_WIDTH] = 32;
		anim = new Animation(animFolderPath+"/water_sprite_sheet.png","water",start,spriteSheetInstructions,angle,scale,10);

		anim.setRandomFrame();
	}

}
