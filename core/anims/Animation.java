package core.anims;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.Main;
import core.UI.Command;
import core.UI.commands.AnimationCycleCommand;
import core.util.TextureHandler;

public class Animation 
{
	private BufferedImage[] frames;
	private int currentFrame;
	private int width, height;
	private int frameRate;
	private Command cycleCommand;
	public Animation(String folderPath, long start, double angle, double[] scale, int cycleRate)
	{
		frames = TextureHandler.loadAnimation(folderPath, angle, scale);
		frameRate = cycleRate;
		init();
	}
	public Animation(String folderPath, String animationKey, long start, int[] sheetInstructions, double angle, double[] scale, int cycleRate)
	{
		frames = TextureHandler.loadAnimationFromSpriteSheet(folderPath, animationKey, sheetInstructions, angle, scale);
		frameRate = cycleRate;
		init();
	}
	
	public void init()
	{
		width = frames[0].getWidth();
		height = frames[0].getHeight();
		int count = 0;
		boolean trimList = false;
		for(int i=0;i<frames.length;i++)
		{
			if(frames[i] == null)
			{
				trimList = true;
				count++;
			}
		}
		if(trimList)
		{
			BufferedImage[] trimmedFrames = new BufferedImage[frames.length - count];
			for(int i=0;i<frames.length;i++)
			{
				if(frames[i] != null)
				{
					trimmedFrames[i] = frames[i];
				}
			}
			frames = trimmedFrames;
		}
		cycleCommand = new AnimationCycleCommand(this);
		int key = Main.timingManager.findClosestTimer(frameRate);
		Main.timingManager.attachCommand(cycleCommand, key);
	}
	public void update()
	{
		
	}
	public void render(Graphics2D g, int x, int y, boolean reversed)
	{
		if(reversed)
		{
			g.drawImage(frames[currentFrame], x+width, y, -width, height, null);
		}
		else
		{
			g.drawImage(frames[currentFrame], x, y, null);
		}
	}
	public BufferedImage getNextFrame()
	{
		if(frames[currentFrame] == null)
		{
			return frames[currentFrame-1];
		}
		return frames[currentFrame];
	}
	public void incrementFrame()
	{
		if(currentFrame+1 >= frames.length) 
		{
			currentFrame = 0;
		}
		else
		{
			currentFrame++;
		}
	}
	public int getFramePointer() 
	{
		return currentFrame;
	}
	public void setRandomFrame()
	{
		currentFrame = Main.rng.nextInt(frames.length);
	}
	public BufferedImage getFrame(int i)
	{
		return frames[i];
	}
	public int getWidth() 
	{
		return width;
	}
	public int getHeight()
	{
		return height;
	}
}
