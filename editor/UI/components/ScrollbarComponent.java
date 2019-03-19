package editor.UI.components;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import core.UI.Component;
import core.map.tiles.Tile;

public class ScrollbarComponent extends Component
{
	private int trackLength;
	private int[] start;
	private boolean vertical;
	private int offset;
	private boolean selected;
	/**
	 * 
	 * @param sc The scale to set this component to (keep at 1,1).
	 * @param position The position of this scroll bar.
	 * @param track The width or height of the area this scroll bar is attached to.
	 * @param horizontal Flag to tell the component whether it is horizontal or vertical.
	 */
	public ScrollbarComponent(double[] sc, int[] position, int track, boolean horizontal, String id)
	{
		super(sc,null,id);
		start = new int[]{position[0],position[1]};
		trackLength = track;
		if(horizontal)
		{
			textureLocation = "resources/Editor/textures/UI/scrollbar/horizBar.png";
		}
		else
		{
			textureLocation = "resources/Editor/textures/UI/scrollbar/vertBar.png";
		}
		loadTexture(textureLocation);
		if(horizontal) {offset=width+32;}
		else {offset = height+32;}
		setPos(position);
		vertical = !horizontal;
	}
	public void clamp(int[] newPos)
	{
		int addr1 = 0;
		int addr2 = 1;
		if(!vertical)
		{
			addr1 = 1;
			addr2 = 0;
		}
		if(start[addr1] != newPos[addr1]){newPos[addr1] = start[addr1];}
		if(newPos[addr2] < start[addr2]){newPos[addr2] = start[addr2];}
		if(newPos[addr2] + offset > trackLength){newPos[addr2] = trackLength-offset;}
		
	}
	@Override
	public void handleMouseInput(MouseEvent e) 
	{
		if(intersects(e) || selected)
		{
			selected = true;
			int[] newPos = new int[]{e.getX()-width/2,e.getY()-height/2};
			clamp(newPos);
			setPos(newPos);
		}
	}

	@Override
	public void handleKeyInput(KeyEvent e) 
	{
	}

	@Override
	public void update() 
	{
	}

	public double getDelta()
	{
		int addr = 0;
		if(vertical)
		{
			addr = 1;
		}
		double ans = ((double)(pos[addr] - start[addr])/(trackLength-offset-Tile.TILE_SIZE-start[addr]));
		if(ans>1) {ans = 1d;}
		return ans;
	}
	public boolean getVertical(){return vertical;}
	@Override
	public Component copy() 
	{
		return null;
	}
	
	public void setSelected(boolean i) {selected = i;}
	public boolean isSelected() {return selected;}
	
	@Override
	public void cRender(Graphics2D g) {}
	@Override
	public void componentInit() {
		// TODO Auto-generated method stub
		
	}
	
}
