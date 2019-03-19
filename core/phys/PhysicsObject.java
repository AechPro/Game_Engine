package core.phys;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public abstract class PhysicsObject 
{
	protected int projectionPriority = 1;
	protected int collisionPriority = 1;
	protected boolean collidable = true;
	protected double width = 1, height = 1;
	protected double[] position = new double[] {0,0};
	protected double[] previousPosition = new double[] {0,0};
	protected double[] previousVelocity = new double[] {0,0};
	protected double[] velocity = new double[] {0,0};
	protected double[] acceleration = new double[] {0,0};
	protected double[] maxVelocity = new double[] {0,0};
	protected AffineTransform transform = new AffineTransform();
	protected AffineTransform rotation = new AffineTransform();
	protected int hitboxExtension = 1;
	protected int upperExtension = 1, lowerExtension = 1, leftExtension = 1, rightExtension = 1;
	protected double angle = 0, prevAngle = 0;
	protected boolean dead = false;
	protected boolean hasSelectiveCollisions = false;
	
	protected double[] origin = new double[] {0,0};
	protected Rectangle2D bbox = new Rectangle2D.Double(0,0,0,0);
	
	protected Color color = null;
	protected AffineTransform t = null;
	
	public abstract void update();
	public abstract void render(Graphics2D g, double delta);
	public void handleCollision(PhysicsObject collider){}

	public void drawHitbox(Graphics2D g)
	{
		Rectangle2D r1 = getbbox();
		Rectangle horizHitbox = new Rectangle((int)r1.getX(),(int)(r1.getY()+1+maxVelocity[1]+1),(int)r1.getWidth(),(int)(r1.getHeight()-2-(maxVelocity[1]+1)*2));
		Rectangle vertHitbox = new Rectangle((int)(r1.getX()+1+maxVelocity[0]+1),(int)r1.getY(),(int)(r1.getWidth()-2-(maxVelocity[1]+1)*2),(int)r1.getHeight());
		g.setColor(Color.YELLOW);
		g.drawRect((int)horizHitbox.getX(),(int)horizHitbox.getY(),(int)horizHitbox.getWidth(),(int)horizHitbox.getHeight());
		g.setColor(Color.RED);
		g.drawRect((int)vertHitbox.getX(),(int)vertHitbox.getY(),(int)vertHitbox.getWidth(),(int)vertHitbox.getHeight());
	}
	
	public void setX(double _x)
	{
		previousPosition[0] = position[0];
		position[0] = _x;
	}
	public void setY(double _y)
	{
		previousPosition[1] = position[1];
		position[1] = _y;
	}
	public void setPos(double[] pos)
	{
		previousPosition[0] = position[0];
		previousPosition[1] = position[1];
		position[0] = pos[0];
		position[1] = pos[1];
	}
	public void setVelocity(double[] v)
	{
		previousVelocity[0] = velocity[0];
		previousVelocity[1] = velocity[1];
		velocity[0] = v[0];
		velocity[1] = v[1];
	}
	public void setVelocity(double vx, double vy)
	{
		previousVelocity[0] = velocity[0];
		previousVelocity[1] = velocity[1];
		velocity[0] = vx;
		velocity[1] = vy;
	}
	public void setAcceleration(double[] a){acceleration = new double[]{a[0],a[1]};}
	public void setProjectionPriority(int i) {projectionPriority=i;}
	public void setCollidable(boolean i){collidable=i;}
	public void setColor(Color c) {color = c;}
	public void kill() {dead = true;}
	
	public boolean isDead() {return dead;}
	
	public double[] getPreviousPosition() {return previousPosition;}
	public Rectangle2D getbbox() 
	{
		bbox.setRect((int)(position[0]-leftExtension+0.0001),(int)(position[1]-upperExtension+0.0001),
        	     (int)(width + rightExtension+0.0001), (int)(height + lowerExtension + 0.0001));
		
		return bbox;
	}
	public double[] getPos(){return position;}
	public double[] getVelocity(){return velocity;}
	public double[] getPreviousVelocity() {return previousVelocity;}
	public double[] getAcceleration(){return acceleration;}
	public double[] getOrigin() {return origin;}
	public double[] getMaxVelocity() {return maxVelocity;}
	public double getX(){return position[0];}
	public double getY(){return position[1];}
	public double getHeight(){return height;}
	public double getWidth(){return width;}
	public Color getColor() {return color;}
	public int getProjectionPriority() {return projectionPriority;}
	public int getCollisionPriority() {return collisionPriority;}
	public boolean isCollidable() {return collidable;}
	public boolean selectiveCollisions() {return hasSelectiveCollisions;}
	public void setPos(double x, double y) 
	{
		setX(x);
		setY(y);
	}
}
