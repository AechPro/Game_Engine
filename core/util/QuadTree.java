package core.util;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import core.phys.PhysicsObject;

public class QuadTree 
{
	private final int OBJECTS_PER_NODE;
	private ArrayList<PhysicsObject> objects;
	private QuadTree[] subNodes;
	private boolean leaf;
	private double x, y, width,height;
	private int subNodeNumber;
	public QuadTree(Rectangle2D r, int objs, int num)
	{
		this(r.getX(),r.getY(),r.getWidth(),r.getHeight(), objs, num);
	}
	public QuadTree(double _x, double _y, double w, double h, int objs, int num)
	{
		leaf = true;
		subNodeNumber = num;
		width=w;
		height=h;
		x = _x;
		y = _y;
		objects = new ArrayList<PhysicsObject>();
		subNodes = new QuadTree[4];
		OBJECTS_PER_NODE = objs;
	}
	public boolean insert(PhysicsObject a)
	{
		boolean success = false;
		if(intersects(a))
		{
			if(leaf)
			{
				if(objects.size()+1 > OBJECTS_PER_NODE)
				{
					split();
					for(int i=0;i<subNodes.length;i++)
					{
						success = subNodes[i].insert(a);
					}	
				}
				else
				{
					if(!objects.contains(a))
					{
						success = objects.add(a);
					}
				}
			}
			else
			{
				for(int i=0;i<subNodes.length;i++)
				{
					success = subNodes[i].insert(a);
				}
			}
		}
		return success;
	}
	public void split()
	{
		double halfW = width/2;
		double halfH = height/2;
		double midPointX = x + halfW;
		double midPointY = y + halfH;
		leaf = false;
		subNodes[0] = new QuadTree(midPointX, midPointY, halfW, halfH, OBJECTS_PER_NODE,subNodeNumber+1);//bottom right
		subNodes[1] = new QuadTree(x,y,halfW, halfH, OBJECTS_PER_NODE,subNodeNumber+1);//top left
		subNodes[2] = new QuadTree(x,midPointY, halfW, halfH, OBJECTS_PER_NODE,subNodeNumber+1);//bottom left
		subNodes[3] = new QuadTree(midPointX, y, halfW, halfH, OBJECTS_PER_NODE,subNodeNumber+1);//top right
		//int old = objects.size();
		for(int i=0;i<subNodes.length;i++)
		{
			for(int j=0;j<objects.size();j++)
			{
				subNodes[i].insert(objects.get(j));
			}
		}
		objects.clear();
		//System.out.println("Inserted "+(old-objects.size())+" out of "+old+" objects at split. contain: "+objects.size());
	}
	public void retrieve(double _x, double _y, double w, double h, ArrayList<PhysicsObject> lst)
	{
		if(intersects(_x,_y,w,h))
		{
			if(leaf)
			{
				for(int i=0;i<objects.size();i++)
				{
					if(!lst.contains(objects.get(i)))
					{
						lst.add(objects.get(i));
					}		
				}
			}
			else
			{
				for(int i=0;i<subNodes.length;i++)
				{
					subNodes[i].retrieve(_x,_y,w,h,lst);
				}
			}
		}
	}

	public void retrieve(Rectangle2D r, ArrayList<PhysicsObject> lst)
	{
		retrieve(r.getX(), r.getY(), r.getWidth(), r.getHeight(),lst);
	}
	public void retrieve(Rectangle r, ArrayList<PhysicsObject> lst)
	{
		retrieve(r.getX(), r.getY(), r.getWidth(), r.getHeight(),lst);
	}
	public void retrieve(PhysicsObject a, ArrayList<PhysicsObject> lst)
	{
		retrieve(a.getbbox(),lst);
	}


	public void clear()
	{
		if(leaf)
		{
			objects.clear();
		}
		else
		{
			for(int i=0;i<subNodes.length;i++)
			{
				subNodes[i].clear();
				subNodes[i] = null;
			}
			leaf = true;
		}
	}
	public ArrayList<PhysicsObject> update()
	{
		ArrayList<PhysicsObject> needsInsertion = new ArrayList<PhysicsObject>();
		update(needsInsertion);
		return needsInsertion;
	}
	public void update(ArrayList<PhysicsObject> needsInsertion)
	{
		if(leaf)
		{
			for(int i=0,stop=objects.size();i<stop;i++)
			{
				if(!contains(objects.get(i)))
				{
					needsInsertion.add(objects.get(i));
					objects.remove(i);
					i--;
					stop--;
				}
			}
		}
		else
		{
			for(int i=0;i<subNodes.length;i++)
			{
				subNodes[i].update(needsInsertion);
			}
		}
	}
	public void remove(PhysicsObject a)
	{
		if(leaf)
		{
			for(int i=0;i<objects.size();i++)
			{
				if(objects.get(i).equals(a))
				{
					objects.remove(i);
				}
			}
		}
		else
		{
			for(int i=0;i<subNodes.length;i++)
			{
				subNodes[i].remove(a);
			}
		}
	}
	public void getNodes(ArrayList<Rectangle> allNodes)
	{
		if(leaf)
		{
			Rectangle r = new Rectangle((int)x,(int)y,(int)width,(int)height);
			allNodes.add(r);
		}
		else
		{
			for(int i=0;i<subNodes.length;i++)
			{
				subNodes[i].getNodes(allNodes);
			}
		}
	}
	public void getAllObjects(ArrayList<PhysicsObject> lst)
	{
		if(leaf)
		{
			//System.out.println("Found leaf with "+objects.size()+" objects");

			for(int i=0;i<objects.size();i++)
			{
				if(!lst.contains(objects.get(i)))
				{
					lst.add(objects.get(i));
				}
			}

		}
		else
		{
			for(int i=0;i<subNodes.length;i++)
			{
				subNodes[i].getAllObjects(lst);
			}
		}
	}
	private boolean contains(PhysicsObject a)
	{
		Rectangle2D bounds = a.getbbox();
		return contains(bounds.getX(),bounds.getY(),bounds.getWidth(),bounds.getHeight());

	}
	private boolean contains(double bx, double by, double bw, double bh)
	{
		double cx = bx+bw/2;
		double cy = by+bh/2;
		return x <= cx && x + width >= cx && y <= cy && y + height >= cy;
	}
	private boolean intersects(PhysicsObject a)
	{
		return intersects(a.getX(),a.getY(),a.getWidth(),a.getHeight());
	}
	private boolean intersects(double ox, double oy, double ow, double oh)
	{
		//return (new Rectangle((int)ox,(int)oy,(int)ow,(int)oh).intersects(new Rectangle((int)x,(int)y,(int)width,(int)height)));
		return ox <= x + width && ox + ow >= x &&
				oy <= y + height && oh + oy >= y;
	}
}
