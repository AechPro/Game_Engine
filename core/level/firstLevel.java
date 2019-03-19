package core.level;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import core.Player;
import core.camera.Camera;
import core.entities.*;

public class firstLevel extends Level
{
	private ArrayList<Ball> balls;
	private ArrayList<Block> blocks;
	public firstLevel(Player p, Camera cam, double[] scale) 
	{
		super(p,cam,scale);
		//player.setPos(new double[] {map.getPixelWidth()-128, map.getPixelHeight()-256});
		player.setPos(new double[] {map.getPixelWidth()/2, map.getPixelHeight()-128});
	}

	@Override
	public void loadMap()
	{
		//map.loadMap("resources/tileMap.txt");
		map.loadMap("resources/saved_level.txt");
		mapWidth=map.getPixelWidth();
		mapHeight=map.getPixelHeight();
	}

	@Override
	public void loadEntities() 
	{
		balls = new ArrayList<Ball>();
		blocks = new ArrayList<Block>();
		Block block;
		Ball ball;
		int w;
		int h;
		double[] start;
		
		for(int i=0;i<0;i++)
		{
			w = (int)Math.round((10 + Math.random()*(100-10)));
			h = (int)Math.round((10 + Math.random()*(100-10)));
			start = new double[] {Math.random()*mapWidth + 64,Math.random()*mapHeight + 64};
			block = new Block(w,h,start,0,i,camera);
			blocks.add(block);
		}
		
		for(int i=0;i<0;i++)
		{
			start = new double[] {Math.random()*mapWidth + 64,Math.random()*mapHeight + 64};
			ball = new Ball(start,0.0,camera);
			balls.add(ball);
		}
		
		entities.addAll(blocks);
		entities.addAll(balls);
	}

	@Override
	public void levelUpdate() 
	{
		updateBalls();
	}

	@Override
	public void levelRender(Graphics2D g, double delta) 
	{
		
	}
	private void updateBalls()
	{
		if(balls.size() == 0) {return;}
		
		if(Math.random() <= 1.2)
		{
			int idx = (int)Math.round(Math.random()*(balls.size()-1));
			balls.get(idx).setX(Math.random()*mapWidth + 64);
			balls.get(idx).setY(Math.random()*mapHeight + 64);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		((Player)player).keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		((Player)player).keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
	}
}
