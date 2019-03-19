package core.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class FileIOHandler 
{
	private static BufferedImage[][] spriteSet;
	public static BufferedImage loadImage(String filePath)
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File(filePath));
			return img;
		}
		catch(Exception e)
		{
			System.out.println("Failed to load texture!\nFile path: "+filePath);
			e.printStackTrace();
		}
		return null;
	}
	public static void getFilePathsFrom(String dir, String ext, ArrayList<String> paths)
	{
		File[] files = new File(dir).listFiles();
		for(File f : files)
		{
			String name = dir+"/"+f.getName();
			name = name.replaceAll("\\\\", "/");
			if(f.isFile() && f.getName().contains(ext))
			{
				paths.add(name);
			}
			else if(f.isDirectory())
			{
				getFilePathsFrom(name,ext,paths);
			}
		}
	}
	public static BufferedImage getSprite(int x, int y)
	{
		if(spriteSet == null)
		{
			loadSpriteSet();
		}
		return spriteSet[x][y];
	}
	private static void loadSpriteSet()
	{
		try 
		{
			int stepX = 64;
			int stepY = 64;
			BufferedImage sheet = ImageIO.read(new File("resources/spritesheet.png"));
			spriteSet = new BufferedImage[1 + sheet.getWidth()/stepX][1 + sheet.getHeight()/stepY];
			int n = 0;
			for (int y = 0; y < sheet.getHeight(); y += stepY) 
			{
				if(y+stepY > sheet.getHeight()) {continue;}
				for(int x=0; x < sheet.getWidth(); x += stepX)
				{
					if(x+stepX > sheet.getWidth()) {continue;}
					spriteSet[x/stepX][y/stepY] = sheet.getSubimage(x, y, stepX, stepY);
					if(spriteSet[x/stepX][y/stepY] != null)
					{
						n++;
						ImageIO.write(spriteSet[x/stepX][y/stepY],"png",new File("resources/textures/Players/Seth/body_"+n+".png"));
					}
				}
			}
		} 
		catch (Exception e) {e.printStackTrace();}
		
	}
}
