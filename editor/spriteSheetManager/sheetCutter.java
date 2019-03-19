package editor.spriteSheetManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import core.util.TextureHandler;
import editor.level.Map;

public class sheetCutter 
{
	public static void cutAndSave(String dir, String fileName)
	{
		try
		{
			buildDirectoryStructure(dir);
			
			int[] spriteSheetInstructions = new int[TextureHandler.NUM_SHEET_INSTRUCTIONS];

			spriteSheetInstructions[TextureHandler.SHEET_OFFSET_X] = 0;
			spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 0;
			spriteSheetInstructions[TextureHandler.SHEET_SQUISH_X] = 0;
			spriteSheetInstructions[TextureHandler.SHEET_SQUISH_Y] = 0;
			spriteSheetInstructions[TextureHandler.SHEET_SQUISH_WIDTH] = 0;
			spriteSheetInstructions[TextureHandler.SHEET_SQUISH_HEIGHT] = 0;
			spriteSheetInstructions[TextureHandler.SHEET_STEP_X] = 32;
			spriteSheetInstructions[TextureHandler.SHEET_STEP_Y] = 32;
			spriteSheetInstructions[TextureHandler.SHEET_SUBIMAGE_HEIGHT] = 32;
			spriteSheetInstructions[TextureHandler.SHEET_SUBIMAGE_WIDTH] = 32;
			
			BufferedImage[] sprites = TextureHandler.loadAnimationFromSpriteSheet(
					dir+"/"+fileName, "sheet_set", spriteSheetInstructions, 0, new double[] {1.0,1.0});
			
			int itr = 0;
			//ImageIO.write(spriteSet[x/stepX][y/stepY],"png",new File("resources/textures/Players/Seth/body_"+n+".png"));

			System.out.println(sprites.length);
			for(BufferedImage sprite : sprites)
			{
				if(sprite != null && sum(sprite))
				{
					ImageIO.write(sprite, "png", new File(dir+"/tile"+(itr++)+".png"));
				}
			}
			
		} 
		catch(Exception e)
		{
		    e.printStackTrace();
		} 
	}
	private static boolean sum(BufferedImage img)
	{
		double sum =0;
		for(int i=0;i<img.getWidth();i++)
		{
			for(int j=0;j<img.getHeight();j++)
			{
				//System.out.println(img.getRGB(i,j));
				sum+=img.getRGB(i,j);
			}
		}
		//System.out.println(sum);
		if(Math.abs(sum) < 100)
		{
			return false;
		}
		return true;
	}
	private static void makeDir(String dir)
	{
		File d = new File(dir);
		if(d.mkdir())
		{
			System.out.println(dir+" directory created");
	    } 
	    else 
	    {
	        System.out.println("Failed to create directory "+dir);
	    }
	}
	private static void buildDirectoryStructure(String dir) throws Exception
	{
		String anim = Map.ANIMATED_TILE_DIRECTORY_EXT;
		String stat = Map.STATIC_TILE_DIRECTORY_EXT;
		String wall = Map.WALL_TILE_DIRECTORY_EXT;
		String terr = Map.TERRAIN_TILE_DIRECTORY_EXT;
		String deco = Map.DECORATION_TILE_DIRECTORY_EXT;
		
		ArrayList<String> folders = new ArrayList<String>();
		File[] existingFolders = new File(dir).listFiles();
		if(existingFolders == null)
		{
			return;
		}
		
		for(int i=0;i<existingFolders.length;i++)
		{
			if(existingFolders[i].isDirectory())
			{
				folders.add("/"+existingFolders[i].getName().replaceAll("\\\\", "/"));
			}
		}
		
		System.out.println("Checking for directory "+wall);
		if(!folders.contains(wall))
		{
			makeDir(dir+wall);
			makeDir(dir+wall+stat);
			makeDir(dir+wall+anim);
		}
		
		System.out.println("Checking for directory "+terr);
		if(!folders.contains(terr))
		{
			makeDir(dir+terr);
			makeDir(dir+terr+stat);
			makeDir(dir+terr+anim);
		}
		
		System.out.println("Checking for directory "+deco);
		if(!folders.contains(deco))
		{
			makeDir(dir+deco);
			makeDir(dir+deco+stat);
			makeDir(dir+deco+anim);
		}
	}
}
