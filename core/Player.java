package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import core.anims.Animation;
import core.items.ArmorSet;
import core.phys.PhysicsObject;
import core.util.MathUtil;
import core.util.TextureHandler;

public class Player extends PhysicsObject
{
	public static final int FACING_UP = 0;
	public static final int FACING_DOWN = 1;
	public static final int FACING_LEFT = 2;
	public static final int FACING_RIGHT = 3;
	private double theta;

	private ArmorSet armor;

	private Animation[] walking;
	private double[] scale;

	private int[] apparentLocation;
	private int animationPointer;
	private boolean leftPressed, rightPressed, upPressed, downPressed;
	private int reskinRequest;
	private int renderX, renderY;

	public Player(double[] startPos, double startAngle, double[] sc) 
	{
		origin = new double[] {startPos[0],startPos[1]};
		position = new double[]{startPos[0],startPos[1]};
		theta = startAngle;
		scale = sc;
		init();
	}

	public void init() 
	{
		projectionPriority = 0;
		velocity = new double[2];
		acceleration = new double[]{0d,0d};
		maxVelocity = new double[]{3d,3d};

		apparentLocation = new int[] {0,0};

		previousPosition = new double[]{position[0],position[1]};

		t = new AffineTransform();

		initAnims();
		animationPointer = FACING_UP;
		upperExtension = -15;
		lowerExtension = -15;
		leftExtension = -2;
		rightExtension = -5;
		//armor.setShirt(path+"TORSO_leather_armor_shirt_white.png", frameCycle);

		width = walking[0].getWidth()/scale[0];
		height = walking[0].getHeight()/scale[1];
	}

	public void update()
	{
		if(rightPressed && !upPressed && !downPressed)
		{
			acceleration[0] = 1;
			animationPointer = FACING_RIGHT;
		}
		else if(leftPressed && !upPressed && !downPressed)
		{
			animationPointer = FACING_LEFT;
			acceleration[0] = -1;
		}
		else if(upPressed && !leftPressed && !rightPressed)
		{
			animationPointer = FACING_UP;
			acceleration[1] = -1;
		}
		else if(downPressed && !leftPressed && !rightPressed)
		{
			animationPointer = FACING_DOWN;
			acceleration[1] = 1;
		}
		if(!rightPressed && !leftPressed)
		{
			acceleration[0] = 0;
			velocity[0] = 0;
		}
		if(!upPressed && !downPressed)
		{
			acceleration[1] = 0;
			velocity[1] = 0;
		}
		if(reskinRequest == 1)
		{
			initAnims();
			reskinRequest = 0;
		}
		else if(reskinRequest == 2)
		{
			initSecondAnims();
			reskinRequest = 0;
		}

		previousPosition[0] = position[0];
		previousPosition[1] = position[1];

		previousVelocity[0] = velocity[0];
		previousVelocity[1] = velocity[1];

		for(int i=0;i<2;i++)
		{
			position[i]+=velocity[i];
			velocity[i]+=acceleration[i];
			if(velocity[i]>maxVelocity[i]){velocity[i]=maxVelocity[i];}
			else if(velocity[i]<-maxVelocity[i]){velocity[i]=-maxVelocity[i];}
		}
		walking[animationPointer].update();
		armor.update(animationPointer);
	}
	public void render(Graphics2D g, double interp)
	{
		try
		{
			apparentLocation = MathUtil.lerp(previousPosition,position,interp);
			renderX = (int)(apparentLocation[0]*scale[0]);
			renderY = (int)(apparentLocation[1]*scale[1]);
			if(!rightPressed && !leftPressed && !upPressed && !downPressed)
			{
				g.drawImage(walking[animationPointer].getFrame(0),renderX,renderY,null);
				armor.render(g, 0, renderX, renderY, animationPointer);
			}
			else
			{
				walking[animationPointer].render(g,renderX,renderY,false);
				armor.render(g, walking[animationPointer].getFramePointer(),renderX, renderY, animationPointer);
			}
		}
		catch(Exception e){e.printStackTrace();}
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_D){rightPressed = true;}
		if(e.getKeyCode() == KeyEvent.VK_A){leftPressed = true;}
		if(e.getKeyCode() == KeyEvent.VK_W){upPressed = true;}
		if(e.getKeyCode() == KeyEvent.VK_S){downPressed = true;}
		if(e.getKeyCode() == KeyEvent.VK_1)
		{
			reskinRequest = 1;
		}
		if(e.getKeyCode() == KeyEvent.VK_2)
		{
			reskinRequest = 2;
		}
	}
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_D){rightPressed = false;}
		if(e.getKeyCode() == KeyEvent.VK_A){leftPressed = false;}
		if(e.getKeyCode() == KeyEvent.VK_W){upPressed = false;}
		if(e.getKeyCode() == KeyEvent.VK_S){downPressed = false;}
	}
	public double[] getPos(boolean scaled)
	{
		if(scaled) {return new double[] {position[0]*scale[0], position[1]*scale[1]};}
		return new double[] {position[0], position[1]};
	}
	public double[] getPreviousPosition(boolean scaled)
	{
		if(scaled) {return new double[] {previousPosition[0]*scale[0], previousPosition[1]*scale[1]};}
		return new double[] {previousPosition[0], previousPosition[1]};
	}
	public void initAnims()
	{
		
		walking = new Animation[4];
		armor = new ArmorSet(theta, scale);
		int frameCycle = 15;
		long start = 0l;
		String path = "resources/textures/Players/Seth/lpc_entry/png/walkcycle/";
		int[] spriteSheetInstructions = new int[TextureHandler.NUM_SHEET_INSTRUCTIONS];

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

		
		
		walking[FACING_UP] = new Animation(path+"BODY_male.png",""+FACING_UP,start,spriteSheetInstructions,theta,scale,frameCycle);
		
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 64;
		walking[FACING_LEFT] = new Animation(path+"BODY_male.png",""+FACING_LEFT,start,spriteSheetInstructions,theta,scale,frameCycle);
		
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 128;
		walking[FACING_DOWN] = new Animation(path+"BODY_male.png",""+FACING_DOWN,start,spriteSheetInstructions,theta,scale,frameCycle);
		
		spriteSheetInstructions[TextureHandler.SHEET_OFFSET_Y] = 192;
		walking[FACING_RIGHT] = new Animation(path+"BODY_male.png",""+FACING_RIGHT,start,spriteSheetInstructions,theta,scale,frameCycle);
		
		
		
		//armor.setBack(path+"BEHIND_quiver.png", frameCycle);
		armor.setBracers(path+"TORSO_leather_armor_bracers.png", frameCycle);
		armor.setHelm(path+"HEAD_leather_armor_hat.png", frameCycle);
		armor.setShirt(path+"TORSO_leather_armor_shirt_white.png", frameCycle);
		armor.setShoulders(path+"TORSO_leather_armor_shoulders.png", frameCycle);
		armor.setBoots(path+"FEET_shoes_brown.png", frameCycle);
		armor.setChest(path+"TORSO_leather_armor_torso.png", frameCycle);
		armor.setLegs(path+"LEGS_plate_armor_pants.png", frameCycle);
		armor.setBelt(path+"BELT_leather.png", frameCycle);
	}
	
	public void initSecondAnims()
	{
		int frameCycle = 15;

		
		String path = "resources/textures/Players/Seth/lpc_entry/png/walkcycle/";
		int[] spriteSheetInstructions = new int[TextureHandler.NUM_SHEET_INSTRUCTIONS];

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
		//armor.setBack(path+"BEHIND_quiver.png", frameCycle);
		armor.setBracers(path+"TORSO_plate_armor_arms_shoulders.png", frameCycle);
		armor.setGloves(path+"HANDS_plate_armor_gloves.png", frameCycle);
		armor.setHelm(path+"HEAD_plate_armor_helmet.png", frameCycle);
		armor.setShirt(path+"TORSO_leather_armor_shirt_white.png", frameCycle);
		armor.setShoulders(path+"TORSO_leather_armor_shoulders.png", frameCycle);
		armor.setBoots(path+"FEET_plate_armor_shoes.png", frameCycle);
		armor.setChest(path+"TORSO_plate_armor_torso.png", frameCycle);
		armor.setLegs(path+"LEGS_plate_armor_pants.png", frameCycle);
		armor.setBelt(path+"BELT_leather.png", frameCycle);
	}
}
