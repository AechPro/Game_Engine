package core.map;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import core.camera.Camera;
import core.map.tiles.*;
import core.phys.PhysicsObject;
import core.util.QuadTree;
import core.util.TextureHandler;
import editor.level.Map;
public class TileMap
{
	private ArrayList<Tile> tiles, decorations;
	private String mapFile;
	private int width,height;
	private int pixelWidth, pixelHeight;
	private int[] position;
	private BufferedImage background;
	private Camera camera;

	private HashMap<String, BufferedImage> staticTextures;
	private ArrayList<Chunk> visibleChunks;
	private BufferedImage[] imgs;

	public TileMap(Camera cam)
	{
		camera = cam;
		staticTextures = new HashMap<String, BufferedImage>();
	}
	public void update()
	{
		if(visibleChunks == null)
		{
			return;
		}
		for(int i=0,stop=visibleChunks.size();i<stop;i++)
		{
			if(camera.visible(visibleChunks.get(i).getBounds(),0))
			{
				visibleChunks.get(i).update();
			}
		}
	}
	public void renderBackground(Graphics2D g)
	{
		//long t1 = System.nanoTime();
		//Tile t = null;
		if(background != null)
		{
			g.drawImage(background, 0, 0, null);
		}
		for(int i=0,stop=visibleChunks.size();i<stop;i++)
		{
			
			//System.out.println("\nCHUNK CHECK");
			if(camera.visible(visibleChunks.get(i).getBounds(),0))
			{
				visibleChunks.get(i).renderBackground(g);
			}
		}
		//System.out.println((System.nanoTime() - t1)/1000000+"ms");
		//g.drawImage(background, 0,0,null);
	}
	public void renderForeground(Graphics2D g)
	{
		for(int i=0,stop=visibleChunks.size();i<stop;i++)
		{
			
			//System.out.println("\nCHUNK CHECK");
			if(camera.visible(visibleChunks.get(i).getBounds(),0))
			{
				visibleChunks.get(i).renderForeground(g);
			}
		}
	}
	public void loadMap(String location)
	{
		System.out.println("Loading tile map...");
		mapFile = location;
		tiles = new ArrayList<Tile>();
		width = 0;
		height = 0;
		double angle = 0;
		BufferedImage tileImage = null;
		int startX = 0;
		int startY = 0;
		int x = startX;
		int y = startY;

		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(location));
			String[] delimiters = new String[] {Map.MAP_FILE_ENTRY_READ_DELIMITERS,","};
			String raw = "";
			String textureKey = "";
			Tile tileToAdd = null;
			while((raw = reader.readLine()) != null)
			{
				String[] parsed = raw.split(delimiters[0]);
				width = 0;
				for(int k=0;k<parsed.length;k++)
				{
					
					//System.out.println(parsed[k]);
					String[] dataPack = parsed[k].split(delimiters[1]);

					textureKey = dataPack[0];
					angle = Double.parseDouble(dataPack[2]);
					tileImage = getStaticTexture(textureKey,angle);
					tileToAdd = buildTile(textureKey,x,y,angle);
					tileToAdd.setTexture(tileImage,textureKey,camera.getScreenScale());
					addTile(tileToAdd);

					textureKey = dataPack[1];
					if(!textureKey.equals("null"))
					{
						angle = Double.parseDouble(dataPack[3]);
						tileImage = getStaticTexture(textureKey,angle);
						tileToAdd = buildTile(textureKey,x,y,angle);
						tileToAdd.setTexture(tileImage,textureKey,camera.getScreenScale());
						addTile(tileToAdd);
					}

					x+=tileToAdd.getWidth();
					width++;
				}
				pixelWidth = x;

				height++;
				x=startX;
				y+=tileToAdd.getHeight();
			}
			pixelHeight = y;
			
			reader.close();
			
			chunkMap();
			
			System.out.println("Tile map successfully loaded!");
		}
		catch(Exception e)
		{
			System.out.println("Failed to load tile map!");
			e.printStackTrace();
		}

		System.out.println("MAP WIDTH: "+getWidth()+" TILES, "+pixelWidth+" PIXELS\nMAP HEIGHT: "+getHeight()+" TILES "+pixelHeight+" PIXELS");
	}
	public Tile buildTile(String textureKey, int x, int y, double angle)
	{
		Tile tileToAdd = null;
		textureKey = textureKey.replaceAll("\\\\", "/");
		if(textureKey.contains(Map.STATIC_TILE_DIRECTORY_EXT))
		{
			if(textureKey.contains(Map.DECORATION_TILE_DIRECTORY_EXT))
			{
				tileToAdd = new DecorationTile(new int[]{x,y}, angle, textureKey, camera.getScreenScale());
			}
			else if(textureKey.contains(Map.TERRAIN_TILE_DIRECTORY_EXT))
			{
				tileToAdd = new TerrainTile(new int[]{x,y}, angle, textureKey, camera.getScreenScale());
			}
			else if(textureKey.contains(Map.WALL_TILE_DIRECTORY_EXT))
			{
				tileToAdd = new WallTile(new int[]{x,y}, angle, textureKey, camera.getScreenScale());
			}
		}
		//System.out.println("Building "+textureKey);
		return tileToAdd;
	}
	public void addTile(Tile t)
	{
		if(tiles == null) {tiles = new ArrayList<Tile>();}
		if(decorations == null) {decorations = new ArrayList<Tile>();}

		synchronized(tiles)
		{
			if(!staticTextures.keySet().contains(t.getTexturePath()))
			{
				staticTextures.put(t.getTexturePath(), t.getTexture());
			}

			tiles.add(t);
			if(t.isDecoration())
			{
				decorations.add(t);
			}
		}

	}

	public BufferedImage getStaticTexture(String textureKey,double angle)
	{
		BufferedImage tileImage = null;
		if(!staticTextures.containsKey(textureKey))
		{
			tileImage = TextureHandler.loadTexture(textureKey,camera.getScreenScale());
			staticTextures.put(textureKey,tileImage);
		}
		else
		{
			tileImage = staticTextures.get(textureKey);
		}
		if(angle != 0)
		{
			tileImage = TextureHandler.rotateTexture(textureKey, angle, camera.getScreenScale());
		}
		return tileImage;
	}

	private void chunkMap()
	{
		int chunkW = 10;
		int chunkH = 10;
		visibleChunks = new ArrayList<Chunk>();
		Chunk[][] chunks = new Chunk[(pixelWidth/(chunkW*Tile.TILE_SIZE))+1][(pixelHeight/(chunkH*Tile.TILE_SIZE))+1];
		Chunk chunk = null; 
		chunk = new Chunk(0,0,chunkW,chunkH,camera.getScreenScale());
		for(int x=0;x<chunks.length;x++)
		{
			int xo = x*chunkW*Tile.TILE_SIZE;
			for(int y=0;y<chunks[0].length;y++)
			{
				int yo = y*chunkH*Tile.TILE_SIZE;
				chunk = new Chunk(xo,yo,chunkW,chunkH, camera.getScreenScale());

				for(int i=1+xo,stop=xo+chunkW*Tile.TILE_SIZE;i<stop;i+=Tile.TILE_SIZE)
				{
					for(int j=1+yo,jstop=yo+chunkH*Tile.TILE_SIZE;j<jstop;j+=Tile.TILE_SIZE)
					{
						chunk.addTile(findTile(i+1,j+1));
						chunk.addTile(findDecoration(i+1,j+1));
					}
				}
				chunk.buildImage();
				visibleChunks.add(chunk);
			}
		}
	}

	public Tile findTile(int x, int y)
	{
		if(tiles == null) {return null;}

		for(PhysicsObject t : tiles)
		{
			if(x > t.getX() && x < t.getX()+t.getWidth())
			{
				if(y > t.getY() && y < t.getY()+t.getHeight())
				{
					if(!((Tile)t).isDecoration())
					{
						return (Tile)t;
					}
				}
			}
		}
		return null;
	}
	
	public Tile findDecoration(int x, int y)
	{
		if(tiles == null) {return null;}

		for(PhysicsObject t : decorations)
		{
			if(x > t.getX() && x < t.getX()+t.getWidth())
			{
				if(y > t.getY() && y < t.getY()+t.getHeight())
				{
					return (Tile)t;
				}
			}
		}
		return null;
	}
	public Tile getTile(double px, double py)
	{
		int x = (int)((px)/Tile.TILE_SIZE);
		int y = (int)((py)/Tile.TILE_SIZE);

		int index = x + y*width;
		if(index<0 || index>=tiles.size()){return null;}

		return tiles.get(index);
	}


	public int[] getPos(){return new int[]{position[0],position[1]};}
	public ArrayList<Tile> getTiles(){return tiles;}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public int getPixelWidth() {return pixelWidth;}
	public int getPixelHeight() {return pixelHeight;}
	public int getX(){return position[0];}
	public int getY(){return position[1];}
	public String getMapFile() {return mapFile;}
}