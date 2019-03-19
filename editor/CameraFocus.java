package editor;

import java.awt.Color;
import java.awt.Graphics2D;

import core.phys.PhysicsObject;

public class CameraFocus extends PhysicsObject
{
	public CameraFocus(double[] start)
	{
		origin[0] = start[0];
		origin[1] = start[1];
		position[0] = start[0];
		position[1] = start[1];
		previousPosition[0] = start[0];
		previousPosition[1] = start[1];
		velocity = null;
		acceleration = null;
		collidable = false;
	}
	public void update()
	{
		previousPosition[0] = position[0];
		previousPosition[1] = position[1];
	}
	public void render(Graphics2D g, double delta)
	{
		g.setColor(Color.RED);
		g.fillRect((int)position[0], (int)position[1], 10,10);
	}
}