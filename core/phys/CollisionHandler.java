package core.phys;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import core.Player;
import core.camera.Camera;
import core.entities.Entity;
import core.map.TileMap;
import core.map.tiles.Tile;

import java.util.ArrayList;

import core.util.QuadTree;
import java.awt.geom.Rectangle2D;


public class CollisionHandler 
{
	private ArrayList<PhysicsObject> collidables;
	private QuadTree quad;
	private TileMap tm;
	private Rectangle bounds;
	private Camera camera;
	private ArrayList<PhysicsObject> returnObjects;
	private int visionExtension;


	private double cx1;
	private double cx2;
	private double cy1;
	private double cy2;
	private int spdX;
	private int spdY;
	private Rectangle horizHitbox;
	private Rectangle vertHitbox;
	private double[] ratio;
	private double[] sum;
	private int x1,y1,x2,y2,w1,w2,h1,h2;
	private double[] projectionVector;

	public CollisionHandler(ArrayList<PhysicsObject> colls, TileMap t, Camera cam)
	{
		collidables = colls;
		tm = t;
		visionExtension = 100;
		int w = tm.getPixelWidth();
		int h = tm.getPixelHeight();
		bounds = new Rectangle(0,0,w,h);
		quad = new QuadTree(bounds,25,0);
		camera = cam;
		horizHitbox = new Rectangle(0,0,0,0);
		vertHitbox = new Rectangle(0,0,0,0);
		ratio = new double[2];
		sum = new double[2];
		projectionVector = new double[2];
		returnObjects = new ArrayList<PhysicsObject>();
	}
	public void checkCollisions()
	{
		//long t1 = System.nanoTime();
		boolean collisionsDetected = true;
		//collidables.clear();
		for(int i=0;i<collidables.size();i++)
		{

			if(collidables.get(i).isDead())
			{

				collidables.remove(i);
				i--;
				continue;
			}

			if(!collidables.get(i).isCollidable())
			{
				collidables.remove(i);
				i--;
				continue;
			}
		}
		for(int i=0;i<1 && collisionsDetected;i++)
		{
			fillQuad();
			collisionsDetected = performCheck();
		}
	}

	private boolean performCheck()
	{
		boolean collision = false;


		for (int i = 0; i < collidables.size(); i++)
		{
			if(collidables.get(i).getProjectionPriority() < 0) {continue;}
			if(!camera.visible(collidables.get(i), visionExtension)) {continue;}
			returnObjects.clear();
			quad.retrieve(collidables.get(i), returnObjects);
			//	System.out.println(returnObjects.size());
			for (int x = 0; x < returnObjects.size(); x++)
			{
				if(collidables.get(i) == returnObjects.get(x)) {continue;}
				if(intersects(collidables.get(i), returnObjects.get(x)))
				{
					/*if(collidables.get(i).selectiveCollisions())
					{
						if(collidables.get(i).getCollisionPriority() <= returnObjects.get(x).getCollisionPriority())
						{
							continue;
						}
					}*/
					handleCollision(collidables.get(i),returnObjects.get(x));

					collision = intersects(collidables.get(i), returnObjects.get(x));
				}
			}
		}
		return collision;
	}

	private void fillQuad()
	{
		quad.clear();
		for(PhysicsObject a : collidables)
		{
			if(a.selectiveCollisions())
			{
				continue;
			}
			if(camera.visible(a,visionExtension))
			{
				quad.insert(a);
			}
		}
	}
	public void handleCollision(PhysicsObject obj1, PhysicsObject obj2)
	{
		
		Rectangle2D a = obj1.getbbox();
		Rectangle2D b = obj2.getbbox();

		cx1 = a.getX()+a.getWidth()/2;
		cx2 = b.getX()+b.getWidth()/2;
		cy1 = a.getY()+a.getHeight()/2;
		cy2 = b.getY()+b.getHeight()/2;

		spdX = (int)obj1.getMaxVelocity()[0];
		spdY = (int)obj1.getMaxVelocity()[1];

		if(spdX*2 > obj1.getWidth()) {spdX = (int)obj1.getWidth()/2 - 1;}
		if(spdY*2 > obj1.getHeight()) {spdY = (int)obj1.getHeight()/2 - 1;}

		horizHitbox.setBounds((int)a.getX(),(int)(a.getY()+1+spdY),(int)a.getWidth(),(int)(a.getHeight()-2-spdY*2));
		vertHitbox.setBounds((int)(a.getX()+1+spdX),(int)a.getY(),(int)(a.getWidth()-2-spdX*2),(int)a.getHeight());

		projectionVector[0] = (int)Math.round(Math.abs(Math.abs(cx1 - cx2) - a.getWidth()/2 - b.getWidth()/2));
		projectionVector[1] = (int)Math.round(Math.abs(Math.abs(cy1 - cy2) - a.getHeight()/2 - b.getHeight()/2));
		boolean colX = intersects(horizHitbox,b) && projectionVector[0] <= projectionVector[1];
		boolean colY = intersects(vertHitbox,b) && projectionVector[1] <= projectionVector[0];

		collisionResponse(obj1,obj2,projectionVector,colX,colY);
		obj1.handleCollision(obj2);

	}
	public void collisionResponse(PhysicsObject a, PhysicsObject b, double[] projectionVector, boolean colX, boolean colY)
	{
		ratio[0] = 1;
		ratio[1] = 1;
		if(b.getProjectionPriority() >= 0)
		{
			double[] prev = a.getPreviousPosition();
			double sx1 = Math.abs(prev[0] - a.getX());
			double sy1 = Math.abs(prev[1] - a.getY());
			prev = b.getPreviousPosition();
			double sx2 = Math.abs(prev[0] - b.getX());
			double sy2 = Math.abs(prev[1] - b.getY());

			sum[0] = sx1+sx2;
			sum[1] = sy1+sy2;

			//System.out.println(sum[0]+", "+sum[1]);
			if(sum[0] == 0 && sum[1] == 0) // both a and b are standing still
			{
				ratio[0] = 0.5;
				ratio[1] = 0.5;
			}
			else if(sum[0] == 0)
			{
				ratio[0] = 0.0;
				ratio[1] = sy1/sum[1];
			}
			else if(sum[1] == 0)
			{
				ratio[0] = sx1/sum[0];
				ratio[1] = 0.0;
			}
			else
			{
				ratio[0] = sx1/sum[0];
				ratio[1] = sy1/sum[1];
			}
		}

		if(colX && colY)
		{
			projectVertical(a,b,projectionVector[1],ratio[1]);
			projectHorizontal(a,b,projectionVector[0], ratio[0]);
		}
		else if(colY)
		{
			projectVertical(a,b,projectionVector[1],ratio[1]);
		}
		else if(colX)
		{
			projectHorizontal(a,b,projectionVector[0], ratio[0]);
		}
	}
	public void projectVertical(PhysicsObject a, PhysicsObject b, double projection,double ratio)
	{
		x1 = (int)(a.getX());
		y1 = (int)(a.getY());
		y2 = (int)(b.getY());

		h1 = (int)a.getHeight();
		h2 = (int)b.getHeight();

		cy1 = y1+h1/2.0;
		cy2 = y2+h2/2.0;

		if(cy1<cy2)
		{
			a.setVelocity(a.getVelocity()[0],Math.min(a.getVelocity()[1], 0));

			a.setX(x1);
			a.setY(y1-projection*ratio);
		}
		else
		{
			a.setVelocity(a.getVelocity()[0], Math.max(a.getVelocity()[1], 0));

			a.setX(x1);
			a.setY(y1+projection*ratio);
			//b.setPos(new double[]{x2,y2-projection*(1-ratio)});
		}
	}

	public void projectHorizontal(PhysicsObject a, PhysicsObject b, double projection, double ratio)
	{
		x1 = (int)(a.getX());
		x2 = (int)(b.getX());
		y1 = (int)(a.getY());

		w1 = (int)a.getWidth();
		w2 = (int)b.getWidth();

		cx1 = x1+w1/2.0;
		cx2 = x2+w2/2.0;
		if(cx1<cx2)
		{		
			a.setVelocity(Math.min(a.getVelocity()[0], 0),a.getVelocity()[1]);

			a.setX(x1-projection*ratio);
			a.setY(y1);
			//b.setPos(new double[]{x2+projection*(1-ratio),y2});
		}
		else
		{
			a.setVelocity(Math.max(a.getVelocity()[0], 0), a.getVelocity()[1]);

			a.setX(x1+projection*ratio);
			a.setY(y1);
			//b.setPos(new double[]{x2-projection*(1-ratio),y2});
		}
	}
	public void drawQuad(Graphics2D g)
	{
		fillQuad();
		ArrayList<Rectangle> quads = new ArrayList<Rectangle>();
		quad.getNodes(quads);
		double[] sc = camera.getScreenScale();
		g.scale(sc[0], sc[1]);
		g.setColor(Color.YELLOW);
		for(Rectangle r : quads)
		{
			//System.out.println(r.getX()+" "+r.getY()+"ID: "+r.getFrame().getX());
			g.drawRect((int)Math.round(r.getX()), (int)Math.round(r.getY()), (int)Math.round(r.getWidth()), (int)Math.round(r.getHeight()));
		}

		ArrayList<PhysicsObject> returnObjects = new ArrayList<PhysicsObject>();
		g.setColor(Color.GREEN);
		int cx1=0,cx2=0,cy1=0,cy2=0;
		PhysicsObject a=null,b=null;
		for (int i = 0; i < collidables.size(); i++)
		{
			//collidables.get(i).drawHitbox(g);
			a = collidables.get(i);
			//if(a.getProjectionPriority() < 0) {continue;}
			if(!a.isCollidable() || !camera.visible(a, visionExtension)) {continue;}
			if(!(a instanceof Player)){continue;}


			returnObjects.clear();
			quad.retrieve(a,returnObjects);

			for (int x = 0; x < returnObjects.size(); x++)
			{
				if(collidables.get(i) == returnObjects.get(x)) {continue;}
				returnObjects.get(x).drawHitbox(g);
				b = returnObjects.get(x);
				//if(!(b instanceof Player) && !(b instanceof Entity)) {continue;}
				//double d = Math.sqrt(Math.pow(a.getX() - b.getX(),2) + Math.pow(a.getY() - b.getY(), 2));
				if(a instanceof Player)
				{
					g.setColor(Color.YELLOW);
				}
				else if(a instanceof Entity)
				{
					g.setColor(Color.RED);
				}
				if(b instanceof Tile)
				{
					g.setColor(Color.GREEN);
				}
				cx1 = (int)(a.getX()+a.getWidth()/2);
				cy1 = (int)(a.getY()+a.getHeight()/2);
				cx2 = (int)(b.getX()+b.getWidth()/2);
				cy2 = (int)(b.getY()+b.getHeight()/2);
				g.drawLine((int)(cx1), (int)(cy1), (int)(cx2), (int)(cy2));
				//g.fillRect(cx2-5, cy2-5, 10,10);
			}
		}
		g.scale(1/sc[0], 1/sc[1]);
	}
	private boolean intersects(PhysicsObject a, PhysicsObject b)
	{
		return intersects(a.getbbox(), b.getbbox());
		//return intersects(a.getX(), a.getY(), a.getWidth(), a.getHeight(), b.getX(), b.getY(), b.getWidth(), b.getHeight());
	}
	private boolean intersects(Rectangle a, Rectangle2D b)
	{
		return intersects(a.getX(), a.getY(), a.getWidth(), a.getHeight(), b.getX(), b.getY(), b.getWidth(), b.getHeight());
	}
	private boolean intersects(Rectangle2D a, Rectangle2D b)
	{
		return intersects(a.getX(), a.getY(), a.getWidth(), a.getHeight(), b.getX(), b.getY(), b.getWidth(), b.getHeight());
	}
	private boolean intersects(double x1, double y1, double w1, double h1, double x2, double y2, double w2, double h2)
	{
		return x1 <= x2 + w2 && x1 + w1 >= x2 &&
				y1 <= y2 + h2 && y1 + h1 >= y2;
	}
}
