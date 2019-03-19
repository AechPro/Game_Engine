package core.level;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import core.UI.UIStateMachine;
import core.camera.Camera;
import core.entities.Entity;
import core.map.TileMap;
import core.phys.CollisionHandler;
import core.phys.PhysicsObject;

public abstract class Level 
{
	protected TileMap map;
	protected ArrayList<Entity> entities;
	protected ArrayList<PhysicsObject> collidableObjects;
	protected PhysicsObject player;
	protected UIStateMachine userInterface;
	protected CollisionHandler collisionHandler;
	protected Camera camera;
	protected AffineTransform prevGfxTransform;
	protected AffineTransform scale;
	protected AffineTransform current;
	
	protected int mapWidth,mapHeight;
	
	public Level(PhysicsObject p, Camera cam, double[] resScale)
	{
		player = p;
		camera = cam;
		scale = new AffineTransform();
		if(resScale != null)
		{
			scale.scale(resScale[0], resScale[1]);
		}
		
		current = new AffineTransform();
		init();
		loadMap();
		loadEntities();
		initCollisionHandler();
		camera.setLevelBounds(0, map.getPixelWidth(), 0, map.getPixelHeight());
	}
	public void init()
	{
		map = new TileMap(camera);
		collidableObjects = new ArrayList<PhysicsObject>();
		entities = new ArrayList<Entity>();
		userInterface = new UIStateMachine(camera.getScreenScale());
	}
	public void initCollisionHandler()
	{
		collidableObjects = new ArrayList<PhysicsObject>();
		if(map.getTiles() != null)
		{
			collidableObjects.addAll(map.getTiles());
		}
		if(entities != null)
		{
			collidableObjects.addAll(entities);
		}
		
		if(player != null && player.isCollidable())
		{
			collidableObjects.add(player);
		}
		
		
		collisionHandler = new CollisionHandler(collidableObjects,map, camera);
	}
	public void update()
	{
		synchronized(entities)
		{
			for(int i=0,stop=entities.size();i<stop;i++)
			{
				if(entities.get(i).isDead())
				{
					entities.remove(i);
					i--;
					stop--;
					continue;
				}
				entities.get(i).update();
			}
		}
		
		player.update();
		if(userInterface != null) 
		{
			userInterface.update();
		}
		synchronized(entities)
		{
			collisionHandler.checkCollisions();
			camera.update();
			if(map != null)
			{
				map.update();
			}
			
			levelUpdate();
		}
		
	}
	public void render(Graphics2D g, double delta)
	{
		current = camera.computeTransform(delta,true);
		prevGfxTransform = g.getTransform();
		g.setTransform(current);
		
		map.renderBackground(g);
		for(int i=0,stop=entities.size();i<stop;i++)
		{
			entities.get(i).render(g,delta);
		}
		player.render(g,delta);
		
		levelRender(g,delta);
		
		map.renderForeground(g);
	    //collisionHandler.drawQuad(g);
		
		g.setTransform(scale);
		if(userInterface != null)
		{
			userInterface.render(g);
		}
		
		
		g.setTransform(prevGfxTransform);
	}
	
	public TileMap getMap() {return map;}
	
	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
	public abstract void keyTyped(KeyEvent e);
	public abstract void loadMap();
	public abstract void loadEntities();
	public abstract void levelUpdate();
	public abstract void levelRender(Graphics2D g, double delta);
}
