package core.items;

import java.awt.Graphics2D;
import java.util.ArrayList;

import core.Player;
import core.anims.Animation;
import core.util.TextureHandler;

public class ArmorSet 
{
	private Animation[] chest, legs, helm, gloves, boots, shirt, bracers, back, shoulders, belt;
	private double theta;
	private double[] scale;
	private int[] spriteSheetInstructions;
	private ArrayList<Animation[]> animations;
	public ArmorSet(double angle, double[] sc)
	{
		chest = new Animation[4];
		helm = new Animation[4];
		legs = new Animation[4];
		gloves = new Animation[4];
		boots = new Animation[4];
		shirt = new Animation[4];
		bracers = new Animation[4];
		back = new Animation[4];
		shoulders = new Animation[4];
		belt = new Animation[4];
		theta = angle;
		scale = sc;
		animations = new ArrayList<Animation[]>();
		spriteSheetInstructions = new int[TextureHandler.NUM_SHEET_INSTRUCTIONS];
		resetInstructions();
	}
	public void update(int direction)
	{
		for(Animation[] anim : animations)
		{
			updateAnim(anim,direction);
		}
	}
	public void render(Graphics2D g, int x, int y, int direction)
	{
		for(Animation[] anim : animations)
		{
			renderAnim(g,anim,x,y,direction);
		}
	}
	
	public void render(Graphics2D g, int frame, int x, int y, int direction)
	{
		for(Animation[] anim : animations)
		{
			renderFrame(g,anim,frame,x,y,direction);
		}
	}
	private void renderFrame(Graphics2D g, Animation[] anim, int frame, int x, int y, int direction)
	{
		if(anim[direction] != null)
		{
			g.drawImage(anim[direction].getFrame(frame),x,y,null);
		}
	}
	private void renderAnim(Graphics2D g, Animation[] anim, int x, int y, int direction)
	{
		if(anim[direction] != null) 
		{
			anim[direction].render(g, x, y, false);
		}
	}
	private void updateAnim(Animation[] anim, int direction)
	{
		if(anim[direction] != null)
		{
			anim[direction].update();
		}
	}
	public void setChest(String fileLocation, int frameCycle)
	{
		fillAnimation(chest,fileLocation,frameCycle);
	}
	public void setLegs(String fileLocation, int frameCycle)
	{
		fillAnimation(legs,fileLocation,frameCycle);
	}
	public void setHelm(String fileLocation, int frameCycle)
	{
		fillAnimation(helm,fileLocation,frameCycle);
	}
	public void setGloves(String fileLocation, int frameCycle)
	{
		fillAnimation(gloves,fileLocation,frameCycle);
	}
	public void setBoots(String fileLocation, int frameCycle)
	{
		fillAnimation(boots,fileLocation,frameCycle);
	}
	public void setShirt(String fileLocation, int frameCycle)
	{
		fillAnimation(shirt,fileLocation,frameCycle);
	}
	public void setBracers(String fileLocation, int frameCycle)
	{
		fillAnimation(bracers,fileLocation,frameCycle);
	}
	public void setBack(String fileLocation, int frameCycle)
	{
		fillAnimation(back,fileLocation,frameCycle);
	}
	public void setShoulders(String fileLocation, int frameCycle)
	{
		fillAnimation(shoulders,fileLocation,frameCycle);
	}
	public void setBelt(String fileLocation, int frameCycle)
	{
		fillAnimation(belt,fileLocation,frameCycle);
	}
	public void fillAnimation(Animation[] anim, String fileLocation, int frameCycle)
	{
		anim[Player.FACING_UP] = new Animation(fileLocation,""+Player.FACING_UP,0,spriteSheetInstructions,theta,scale,frameCycle);
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 64;
		anim[Player.FACING_LEFT] = new Animation(fileLocation,""+Player.FACING_LEFT,0,spriteSheetInstructions,theta,scale,frameCycle);
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 128;
		anim[Player.FACING_DOWN] = new Animation(fileLocation,""+Player.FACING_DOWN,0,spriteSheetInstructions,theta,scale,frameCycle);
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 192;
		anim[Player.FACING_RIGHT] = new Animation(fileLocation,""+Player.FACING_RIGHT,0,spriteSheetInstructions,theta,scale,frameCycle);
		if(!animations.contains(anim))
		{
			animations.add(anim);
		}
		resetInstructions();
	}
	private void resetInstructions()
	{
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_X] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_X] = 13;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_Y] = 10;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_WIDTH] = 13;
		spriteSheetInstructions[TextureHandler.SHEET_SQUISH_HEIGHT] = 1;
		spriteSheetInstructions[TextureHandler.SHEET_STEP_X] = 64;
		spriteSheetInstructions[TextureHandler.SHEET_STEP_Y] = 0;
		spriteSheetInstructions[TextureHandler.SHEET_SUBIMAGE_HEIGHT] = 64;
		spriteSheetInstructions[TextureHandler.SHEET_SUBIMAGE_WIDTH] = 64;
	}
}
