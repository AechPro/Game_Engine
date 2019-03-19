package core.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.map.tiles.AnimatedTile;
import core.map.tiles.Tile;
import core.util.TextureHandler;

public class Chunk
{
	private int width, height;
	private Tile[][] tiles, decorations;
	private int[] pos;
	private BufferedImage background,foreground;
	private double[] scale;
	private ArrayList<Tile> updateTiles;
	private Tile[] animatedTiles;
	private Rectangle bounds;
	private int renderX, renderY;
	private double angle;
	private boolean hasForeground;
	
	private String textureID;
	
	public Chunk(int x, int y, int w, int h, double[] sc)
	{
		tiles = new Tile[w][h];
		decorations = new Tile[w][h];
		width = w;
		height = h;
		bounds = new Rectangle(x,y,width*Tile.TILE_SIZE,height*Tile.TILE_SIZE);
		pos = new int[] {x,y};
		scale = sc;
		
		hasForeground = false;
		
		for(int i=0;i<100;i++)
		{
			textureID += ""+(double)((int)(Math.random()*i) | (int)(Math.random()*Math.sqrt(i)));
		}
	}
	public void update()
	{
		if(updateTiles != null && updateTiles.size() > 0)
		{
			for(int i=0,stop=updateTiles.size();i<stop;i++)
			{
				updateTiles.get(i).update();
			}
		}
	}
	public void renderForeground(Graphics2D g)
	{
		if(foreground != null)
		{
			g.drawImage(foreground, renderX, renderY, null);
		}
		if(animatedTiles != null && animatedTiles.length > 0)
		{
			for(int i=0,stop=animatedTiles.length;i<stop;i++)
			{
				animatedTiles[i].render(g,1.0);
			}
		}
	}
	public void renderBackground(Graphics2D g)
	{
		renderX = (int)(scale[0]*pos[0]);
		renderY = (int)(scale[1]*pos[1]);
		g.drawImage(background, renderX, renderY, null);
	}
	public void addTile(Tile i)
	{
		if(i == null) {return;}
		int tx = (int)(i.getX()-pos[0])/Tile.TILE_SIZE;
		int ty = (int)(i.getY()-pos[1])/Tile.TILE_SIZE;
		
		if(tx >= width || ty >= height) {return;}
		//if(tx < 0 || ty < 0) {return;}
		
		//System.out.println(pos[0]+", "+pos[1]);
		//System.out.println("("+i.getX()+","+i.getY()+") -> ("+tx+","+ty+")");
		
		if(i.isDecoration())
		{
			decorations[tx][ty]=i;
			hasForeground = true;
		}
		else
		{
			tiles[tx][ty]=i;
		}
		
	}
	public Rectangle getBounds() {return bounds;}
	public void reScale(double[] sc)
	{
		scale = sc;
		background = TextureHandler.scaleTexture(textureID, sc);
	}
	public void buildImage()
	{
		ArrayList<Tile> animated = new ArrayList<Tile>();
		
		background = new BufferedImage((int)(width*Tile.TILE_SIZE*scale[0]), (int)(height*Tile.TILE_SIZE*scale[1]),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)background.getGraphics();
		
		for(int x=0;x<tiles.length;x++)
		{
			for(int y=0;y<tiles[0].length;y++)
			{
				if(tiles[x][y] != null)
				{
					if(tiles[x][y] instanceof AnimatedTile)
					{
						animated.add(tiles[x][y]);
					}
					else
					{
						g.drawImage(tiles[x][y].getTexture(),(int)(Tile.TILE_SIZE*x*scale[0]), (int)(Tile.TILE_SIZE*y*scale[1]), null);
					}
				}
			}
		}
		g.dispose();
		
		if(hasForeground)
		{
			foreground = new BufferedImage((int)(width*Tile.TILE_SIZE*scale[0]), (int)(height*Tile.TILE_SIZE*scale[1]),BufferedImage.TYPE_INT_ARGB);
			g = (Graphics2D)foreground.getGraphics();
			
			g.setColor(new Color(255,255,255,0));
			g.fillRect(0,0,foreground.getWidth(),foreground.getHeight());
			
			for(int x=0;x<decorations.length;x++)
			{
				for(int y=0;y<decorations[0].length;y++)
				{
					if(decorations[x][y] != null)
					{
						if(decorations[x][y] instanceof AnimatedTile)
						{
							animated.add(decorations[x][y]);
						}
						else
						{
							g.drawImage(decorations[x][y].getTexture(),(int)(Tile.TILE_SIZE*x*scale[0]), (int)(Tile.TILE_SIZE*y*scale[1]), null);
						}
					}
				}
			}
			g.dispose();
			foreground = TextureHandler.compressTexture(foreground);
			TextureHandler.putTexture(foreground, textureID+"f");
		}
		else
		{
			foreground = null;
		}
		
		animatedTiles = new Tile[animated.size()];
		for(int i=0;i<animated.size();i++)
		{
			animatedTiles[i] = animated.get(i);
		}
		
		animated = null;
		
		
		background = TextureHandler.compressTexture(background);
		TextureHandler.putTexture(background, textureID);
	}
	
	public void removeTile(Tile t)
	{
		int x = 0;
		int y = 0;
		for(int i=0;i<tiles.length;i++)
		{
			if(x != 0 || y != 0)
			{
				break;
			}
			for(int j=0;j<tiles[i].length;j++)
			{
				if(tiles[i][j] == t)
				{
					tiles[i][j] = null;
					x = i;
					y = j;
					break;
				}
			}
		}
		Graphics2D g = (Graphics2D)background.getGraphics();
		g.clearRect((int)(x*Tile.TILE_SIZE*scale[0]),
				    (int)(y*Tile.TILE_SIZE*scale[1]),
				    (int)(t.getWidth()*t.getScale()[0]),
				    (int)(t.getHeight()*t.getScale()[1]));
		g.dispose();
	}
	
	public void rotate(double theta)
	{
		angle+=theta;
		background = TextureHandler.rotateTexture(textureID, angle, new double[] {1,1});
	}
	
	public Tile[][] getTiles(){return tiles;}
	public Tile[][] getDecorations(){return decorations;}
	public void setPos(int[] newPos)
	{
		pos = newPos;
	}
}