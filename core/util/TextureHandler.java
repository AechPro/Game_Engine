package core.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;


import core.map.tiles.Tile;

public class TextureHandler 
{


	public static HashMap<String,BufferedImage> loadedTextures;
	public static HashMap<String,BufferedImage[]> loadedAnimations;

	public static BufferedImage[] loadAnimation(String folderLocation, double angle, double[] scale)
	{
		if(loadedAnimations == null)
		{
			loadedAnimations = new HashMap<String,BufferedImage[]>();
		}
		if(loadedAnimations.containsKey(folderLocation))
		{
			return loadedAnimations.get(folderLocation);
		}
		System.out.println(folderLocation);
		File[] files = new File(folderLocation).listFiles();
		BufferedImage[] frames = new BufferedImage[files.length];
		String path = "";
		for(int i=0;i<files.length;i++)
		{
			path = folderLocation+"/"+files[i].getName();
			loadTexture(path,scale);
			frames[i] = rotateTexture(path,angle,scale);
		}
		loadedAnimations.put(folderLocation,frames);
		return frames;

	}
	public static BufferedImage[] loadAnimationFromSpriteSheet(String sheetLocation, String animKey, int[] pixelInstructions, double angle, double[] scale)
	{
		try 
		{
			String key = sheetLocation+animKey;
			if(loadedAnimations == null)
			{
				loadedAnimations = new HashMap<String,BufferedImage[]>();
			}
			if(loadedAnimations.containsKey(key))
			{
				return loadedAnimations.get(key);
			}

			BufferedImage sheet = ImageIO.read(new File(sheetLocation));
			int offsetX = pixelInstructions[SHEET_OFFSET_X];
			int offsetY = pixelInstructions[SHEET_OFFSET_Y];
			int squishX = pixelInstructions[SHEET_SQUISH_X];
			int squishY = pixelInstructions[SHEET_SQUISH_Y];
			int squishWidth = pixelInstructions[SHEET_SQUISH_WIDTH];
			int squishHeight = pixelInstructions[SHEET_SQUISH_HEIGHT];
			int stepX = pixelInstructions[SHEET_STEP_X];
			int stepY = pixelInstructions[SHEET_STEP_Y];
			int subWidth = pixelInstructions[SHEET_SUBIMAGE_WIDTH];
			int subHeight = pixelInstructions[SHEET_SUBIMAGE_HEIGHT];

			int sw = (int)sheet.getWidth();
			int sh = (int)sheet.getHeight();
			sheet = zeroPad(sheet);
			
			int w = 1;
			int h = 1;

			if(stepX > 0) 
			{
				w = 1 + (sheet.getWidth()/stepX);
			}
			if(stepY>0)
			{
				h = 1 + (sheet.getHeight()/stepY);
			}
			
			BufferedImage spriteSet[] = new BufferedImage[w*h+1];
			BufferedImage subImage;
			int n = 0;
			for(int y=offsetY;y<sh;y+=stepY)
			{
				for(int x=offsetX; x < sw; x += stepX)
				{
					//System.out.println("\nLOOKING AT ("+(x+squishX)+","+ (y+squishY)+") -> "
						//	+ "("+ (subWidth-squishX-squishWidth)+","+(subHeight-squishY-squishHeight)+")");

					subImage = sheet.getSubimage(x+squishX, y+squishY, subWidth-squishX-squishWidth, subHeight-squishY-squishHeight);
					//System.out.println(subImage);
					if(subImage != null)
					{
						spriteSet[n] = resizeTexture(subImage, (int)(Tile.TILE_SIZE/1), (int)(Tile.TILE_SIZE/1));
						spriteSet[n] = scaleTexture(spriteSet[n], scale);
						n++;
						//ImageIO.write(subImage,"png",new File("resources/textures/frame_"+n+".png"));
					}
					if(stepX == 0) {break;}
				}
				if(stepY == 0) {break;}
			}

			loadedAnimations.put(key,spriteSet);
			return spriteSet;
		} 
		catch (Exception e) {e.printStackTrace();}
		return null;
	}

	public static BufferedImage rotateTexture(String key, double angle, double[] scale)
	{
		if(loadedTextures == null || !loadedTextures.containsKey(key))
		{
			System.out.println(loadedTextures.containsKey(key));
			return null;
		}
		BufferedImage img = loadedTextures.get(key);
		BufferedImage rotated = new BufferedImage((int)Math.round(img.getWidth()*scale[0]),(int)Math.round(img.getHeight()*scale[1]),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)rotated.getGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2.setRenderingHints(rh);
		g2.rotate(angle,rotated.getWidth()/2,rotated.getHeight()/2);
		g2.drawImage(img,0,0,rotated.getWidth(),rotated.getHeight(),null);
		g2.dispose();
		return rotated;
	}

	public static BufferedImage resizeTexture(BufferedImage img, int desiredWidth, int desiredHeight)
	{
		double aspectRatio = (double)img.getWidth()/img.getHeight();
		double[] scale = new double[] {(double)desiredWidth/img.getWidth(), (double)desiredHeight/(img.getHeight()*aspectRatio)};
		return scaleTexture(img, scale);
	}
	public static BufferedImage scaleTexture(BufferedImage img, double[] scale)
	{
		//System.out.println("Scaling...");
		BufferedImage scaledImage = new BufferedImage((int)Math.round(img.getWidth()*scale[0]),(int)Math.round(img.getHeight()*scale[1]),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)scaledImage.getGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2.setRenderingHints(rh);
		g2.drawImage(img,0,0,scaledImage.getWidth(), scaledImage.getHeight(), null);
		g2.dispose();
		return scaledImage;
	}
	public static BufferedImage scaleTexture(String key, double[] scale)
	{
		if(loadedTextures == null || !loadedTextures.containsKey(key))
		{
			return null;
		}
		BufferedImage scaled = scaleTexture(loadedTextures.get(key),scale);
		return scaled;
	}
	public static BufferedImage loadTexture(String fileName, double[] scale)
	{
		return loadTexture(fileName,fileName,scale);
	}
	public static BufferedImage loadTexture(String fileName, String key, double[] scale)
	{

		BufferedImage texture = null;
		if(key != null)
		{
			if(loadedTextures == null)
			{
				loadedTextures = new HashMap<String,BufferedImage>();
			}
			if(!loadedTextures.containsKey(key))
			{
				texture = FileIOHandler.loadImage(fileName);
				loadedTextures.put(key, texture);
			}
			else
			{
				texture = loadedTextures.get(key);
			}
		}

		if(scale[0] == 1.0 && scale[1] == 1.0)
		{
			return texture;
		}

		BufferedImage scaled = scaleTexture(texture,scale);
		return scaled;
	}

	public static BufferedImage zeroPad(BufferedImage sheet)
	{
		int remX = sheet.getWidth()%Tile.TILE_SIZE;
		int remY = sheet.getHeight()%Tile.TILE_SIZE;
		if(remX == 0 && remY == 0)
		{
			return sheet;
		}
		remX=32;
		remY=32;
		
		BufferedImage n = new BufferedImage((int)(sheet.getWidth()+remX), (int)(sheet.getHeight()+remY), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)n.getGraphics();
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0,0,(int)n.getWidth(), (int)n.getHeight());
		g.drawImage(sheet, 0, 0, (int)sheet.getWidth(), (int)sheet.getHeight(), null);
		g.dispose();
		return n;
	}
	public static BufferedImage compressTexture(BufferedImage texture)
	{
		return MathUtil.convertRGBAToIndexed(texture);
	}
	public static void putTexture(BufferedImage tex, String key)
	{
		if(loadedTextures == null)
		{
			loadedTextures = new HashMap<String,BufferedImage>();
		}
		if(!loadedTextures.containsKey(key))
		{
			loadedTextures.put(key, tex);
		}
	}

	public static int SHEET_OFFSET_X = 0;
	public static int SHEET_OFFSET_Y = 1;
	public static int SHEET_SQUISH_X = 2;
	public static int SHEET_SQUISH_Y = 3;
	public static int SHEET_SQUISH_WIDTH = 4;
	public static int SHEET_SQUISH_HEIGHT = 5;
	public static int SHEET_STEP_X = 6;
	public static int SHEET_STEP_Y = 7;
	public static int SHEET_SUBIMAGE_WIDTH = 8;
	public static int SHEET_SUBIMAGE_HEIGHT = 9;

	public static int NUM_SHEET_INSTRUCTIONS = 10;
}
