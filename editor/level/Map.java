package editor.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Shape;

import core.camera.Camera;
import core.map.tiles.*;
import core.phys.PhysicsObject;
import core.util.FixedSizeStack;
import core.util.QuadTree;
import core.util.TextureHandler;
import editor.UI.components.TileComponent;

public class Map 
{
	public static final String BASE_TILE_DIRECTORY = "resources/textures/tiles";
	public static final String DECORATION_TILE_DIRECTORY_EXT = "/decorations";
	public static final String WALL_TILE_DIRECTORY_EXT = "/walls";
	public static final String TERRAIN_TILE_DIRECTORY_EXT = "/terrain";
	public static final String MULTI_TILE_DIRECTORY_EXT = "/multi";
	public static final String ANIMATED_TILE_DIRECTORY_EXT = "/animated";
	public static final String STATIC_TILE_DIRECTORY_EXT = "/static";
	
	public static final String DEFAULT_TERRAIN_TILE_DIRECTORY = BASE_TILE_DIRECTORY+"/grass/terrain/static/tile16.png";
	public static final String DEFAULT_WALL_TILE_DIRECTORY = BASE_TILE_DIRECTORY+"/ancient_tree/walls/static/tile10.png";
	
	public static final String MAP_FILE_ENTRY_WRITE_DELIMITERS = "_|_";
	public static final String MAP_FILE_ENTRY_READ_DELIMITERS = "_\\|_";
	
	private ArrayList<Tile> tiles, decorations;
	private String mapFile;
	private int width,height;
	private double[] position;

	private int levelBoundaryLeft;
	private int levelBoundaryTop;
	private int levelBoundaryBottom;
	private int levelBoundaryRight;
	private int maxQuadObjects;

	private ArrayList<PhysicsObject> renderCandidates;
	private ArrayList<Tile> decorationsToRender;
	private QuadTree tileTree;
	private Rectangle quadTreeBounds;
	private Camera camera;
	private Rectangle vision, gfxClip;

	private int numPrevSets;
	private PrevTileSet prevSet;
	private FixedSizeStack<PrevTileSet> prevSets;
	
	private Shape gfxPrev;
	private boolean reScaleRequest;

	private HashMap<String, BufferedImage> staticTextures;
	public Map(int rows, int cols, Camera cam)
	{
		width = cols;
		height = rows;
		camera = cam;

		levelBoundaryTop = 48-16;
		levelBoundaryLeft = 216-16;
		levelBoundaryBottom = height*Tile.TILE_SIZE + levelBoundaryTop;
		levelBoundaryRight = width*Tile.TILE_SIZE + levelBoundaryLeft;
		maxQuadObjects = 100;

		staticTextures = new HashMap<String,BufferedImage>();

		tiles = new ArrayList<Tile>();
		renderCandidates = new ArrayList<PhysicsObject>();
		decorationsToRender = new ArrayList<Tile>();
		decorations = new ArrayList<Tile>();

		numPrevSets = 10;
		prevSets = new FixedSizeStack<PrevTileSet>(numPrevSets);
		
		prevSet = null;
		
		quadTreeBounds = new Rectangle(0,0, levelBoundaryRight, levelBoundaryBottom);
		tileTree = new QuadTree(quadTreeBounds, maxQuadObjects,0);

		int w = (int)cam.getViewPort().getWidth() - 216-32;
		int h = (int)cam.getViewPort().getHeight() - 64-32-16;
		vision = new Rectangle(levelBoundaryLeft, levelBoundaryTop ,w,h);
		gfxClip = new Rectangle(0,0,w,h);
		//loadMap("resources/saved_level.txt");
	}
	public void update()
	{
		if(reScaleRequest)
		{
			reScaleMap();
		}
		synchronized(tiles)
		{
			double vw  = camera.getViewPort().getWidth()/(camera.getScreenScale()[0]);
			double vh = camera.getViewPort().getHeight()/camera.getScreenScale()[1];
			double vx = camera.getBounds().getX();
			double vy = camera.getBounds().getY();
			vision.setBounds((int)vx, (int)vy, (int)vw, (int)vh);
			gfxClip.setBounds((int)(-camera.getTransform().getTranslateX()+levelBoundaryLeft), (int)(-camera.getTransform().getTranslateY()+levelBoundaryTop), (int)gfxClip.getWidth(), (int)gfxClip.getHeight());
			renderCandidates.clear();
			decorationsToRender.clear();

			synchronized(tileTree)
			{
				tileTree.retrieve(vision, renderCandidates);
			}

			for(int i=0;i<renderCandidates.size();i++)
			{
				if(((Tile)renderCandidates.get(i)).isDecoration())
				{
					decorationsToRender.add((Tile)renderCandidates.get(i));
					renderCandidates.remove(i);
					i--;
				}
			}
		}
	}

	public void render(Graphics2D g)
	{
		if(tiles == null) {return;}
		if(tileTree == null) {return;}
		if(renderCandidates == null) {return;}
		//System.out.println("Finding camera bounds");
		
		gfxPrev = g.getClip();
		g.setClip(gfxClip);
		
		for(int i=0;i<renderCandidates.size();i++)
		{
			((Tile)renderCandidates.get(i)).render(g,1.0);
		}
		for(int i=0;i<decorationsToRender.size();i++)
		{
			decorationsToRender.get(i).render(g,1.0);
		}
		g.setClip(gfxPrev);
		//drawQuad(g);

	}
	public void drawBounds(Graphics2D g)
	{
		g.setColor(Color.RED);
		g.fillRect(levelBoundaryLeft, levelBoundaryTop, levelBoundaryRight, levelBoundaryBottom);
	}
	
	public void loadPrevSet()
	{
		if(prevSets.empty()) {return;}
		synchronized(tiles)
		{
			tileTree.clear();
			prevSet = prevSets.pop();
			tiles = prevSet.getTiles();
			decorations = prevSet.getDecorations();

			synchronized(tileTree)
			{
				for(Tile t : tiles)
				{
					tileTree.insert(t);
				}
				for(Tile t : decorations)
				{
					tileTree.insert(t);
				}
			}
			reScaleMap();
		}
	}
	public void saveCurrentSet()
	{
		PrevTileSet set = new PrevTileSet();
		set.setTiles(tiles);
		set.setDecorations(decorations);
		prevSets.push(set);
	}

	public Tile buildTile(String textureKey, int x, int y, double angle)
	{
		Tile tileToAdd = null;
		textureKey = textureKey.replaceAll("\\\\", "/");
		if(textureKey.contains(STATIC_TILE_DIRECTORY_EXT))
		{
			if(textureKey.contains(DECORATION_TILE_DIRECTORY_EXT))
			{
				tileToAdd = new DecorationTile(new int[]{x,y}, angle, textureKey, camera.getScreenScale());
			}
			else if(textureKey.contains(TERRAIN_TILE_DIRECTORY_EXT))
			{
				tileToAdd = new TerrainTile(new int[]{x,y}, angle, textureKey, camera.getScreenScale());
			}
			else if(textureKey.contains(WALL_TILE_DIRECTORY_EXT))
			{
				tileToAdd = new WallTile(new int[]{x,y}, angle, textureKey, camera.getScreenScale());
			}
		}
		//System.out.println("Building "+textureKey);
		return tileToAdd;
	}


	public Tile buildTile(TileComponent t)
	{
		String textureKey = t.getTextureLocation();
		Tile tileToAdd = buildTile(textureKey,t.getX(),t.getY(),t.getAngle());
		//BufferedImage tileImage = null;

		//tileImage = t.getTexture();
		//tileToAdd.setTexture(tileImage,textureKey, camera.getScreenScale());
		tileToAdd.setAngle(t.getAngle());
		return tileToAdd;
	}
	public void addTile(TileComponent t)
	{
		Tile tileToAdd = buildTile(t);
		addTile(tileToAdd);
	}
	public void addTile(Tile t)
	{
		if(tiles == null) {tiles = new ArrayList<Tile>();}
		if(decorations == null) {decorations = new ArrayList<Tile>();}
		if(tileTree == null) {tileTree = new QuadTree(quadTreeBounds, maxQuadObjects,0);}

		synchronized(tileTree)
		{
			synchronized(tiles)
			{
				if(!staticTextures.keySet().contains(t.getTexturePath()))
				{
					staticTextures.put(t.getTexturePath(), t.getTexture());
				}

				tileTree.insert(t);
				tiles.add(t);
				if(t.isDecoration())
				{
					decorations.add(t);
				}
			}
		}
	}
	public void removeTile(Tile t) 
	{
		if(t == null) {return;}
		synchronized(tileTree)
		{
			synchronized(tiles)
			{
				tileTree.remove(t);
				tiles.remove(t);
				if(t.isDecoration())
				{
					decorations.remove(t);
				}
			}
		}
	}

	public void removeTile(double[] pos)
	{
		Tile t = findTile((int)pos[0],(int)pos[1]);
		removeTile(t);
	}
	public void removeTile(int x, int y) 
	{
		Tile t = findTile(x,y);
		removeTile(t);
	}
	public boolean removeDecoration(double[] pos)
	{
		return removeDecoration((int)pos[0],(int)pos[1]);
	}
	public boolean removeDecoration(int x, int y)
	{
		Tile t = findDecoration(x,y);
		if(t != null)
		{
			removeTile(t);
			return true;
		}
		return false;
	}
	public Tile findTile(int x, int y)
	{
		if(tiles == null) {return null;}
		if(tileTree == null) {return null;}

		ArrayList<PhysicsObject> nearest =  new ArrayList<PhysicsObject>();
		tileTree.retrieve(x,y,Tile.TILE_SIZE,Tile.TILE_SIZE,nearest);
		for(PhysicsObject t : nearest)
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
	public Tile findDecoration(int x, int y)
	{
		if(tiles == null) {return null;}
		if(tileTree == null) {return null;}

		ArrayList<PhysicsObject> nearest =  new ArrayList<PhysicsObject>();
		tileTree.retrieve(x,y,Tile.TILE_SIZE,Tile.TILE_SIZE,nearest);
		for(PhysicsObject t : nearest)
		{
			if(!((Tile)t).isDecoration()) 
			{
				continue;
			}
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
	public void loadMap(String location)
	{
		System.out.println("Loading tile map...");
		mapFile = location;
		tiles = new ArrayList<Tile>();
		tileTree = new QuadTree(quadTreeBounds, maxQuadObjects,0);
		width = 0;
		height = 0;
		double angle = 0;
		BufferedImage tileImage = null;
		int startX = getOriginTilePosition()[0];
		int startY = getOriginTilePosition()[1];
		int x = startX;
		int y = startY;

		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(location));
			String[] delimiters = new String[] {MAP_FILE_ENTRY_READ_DELIMITERS,","};
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

				height++;
				x=startX;
				y+=tileToAdd.getHeight();
			}
			reader.close();

			ArrayList<PhysicsObject> all = new ArrayList<PhysicsObject>();
			tileTree.getAllObjects(all);
			System.out.println("TREE CONTAINS: "+all.size()+" OBJECTS");
			
			for(int i=0;i<numPrevSets;i++)
			{
				saveCurrentSet();
			}
			
			System.out.println("Tile map successfully loaded!");
		}
		catch(Exception e)
		{
			System.out.println("Failed to load tile map!");
			e.printStackTrace();
		}

		System.out.println("MAP WIDTH: "+getWidth()+"\nMAP HEIGHT: "+getHeight());
	}
	public void saveMap(String location)
	{
		System.out.println("Saving...");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(location));
			Tile t = null, deco = null;
			int[] origin = getOriginTilePosition();
			double angle = 0;
			String[] tileData = new String[4];
			for(int i=0;i<height;i++)
			{
				for(int j=0;j<width;j++)
				{
					t = findTile(origin[0]+j*Tile.TILE_SIZE+1, origin[1]+i*Tile.TILE_SIZE+1);
					if(t == null)
					{
						//tileDir,decorationDir,tileAngle,decorationAngle
						tileData[0] = DEFAULT_TERRAIN_TILE_DIRECTORY;
						tileData[2] = "0";
						if(j == width-1 || i == height-1 || i==0 || j==0)
						{
							tileData[0] = DEFAULT_WALL_TILE_DIRECTORY;
							tileData[2] = "0";
						}
					}
					else
					{
						angle = t.getRenderAngle()*100;
						angle = (int)angle;
						angle/=100;
						tileData[0] = t.getTexturePath();
						tileData[2] = ""+angle;
						
					}
					

					deco = findDecoration(origin[0]+j*Tile.TILE_SIZE+1, origin[1]+i*Tile.TILE_SIZE+1);
					if(deco == null)
					{
						tileData[1] = "null";
						tileData[3] = "0";
					}
					else
					{
						angle = deco.getRenderAngle()*100;
						angle = (int)angle;
						angle/=100;
						
						tileData[1] = deco.getTexturePath();
						tileData[3] = ""+angle;
					}
					String entry = "";
					for(String s : tileData)
					{
						entry += s+",";
					}
					entry = entry.substring(0,entry.length()-1);
					writer.write(entry+MAP_FILE_ENTRY_WRITE_DELIMITERS);
				}
				writer.write("\n");
			}
			writer.close();
		}
		catch(Exception e){e.printStackTrace();}
		System.out.println("Saved to "+location);
	}
	
	public int[] getNearestTile(int[] pos)
	{
		int[] ans = new int[pos.length];
		double bestSoFar = Double.MAX_VALUE;
		ArrayList<PhysicsObject> nearest = new ArrayList<PhysicsObject>();
		tileTree.retrieve(pos[0], pos[1], 1, 1, nearest);

		for(PhysicsObject t : nearest)
		{
			double dist = Math.sqrt(Math.pow(pos[0]-t.getX(),2)+Math.pow(t.getY()-pos[1], 2));
			if(dist<bestSoFar)
			{
				bestSoFar = dist;
				ans[0] = (int)t.getX();
				ans[1] = (int)t.getY();
			}
		}

		return ans;
	}
	
	public void reScale()
	{
		reScaleRequest = true;
	}
	
	private void reScaleMap()
	{
		double[] sc = camera.getScreenScale();
		BufferedImage tileImage;
		String textureKey = "";
		for(String key : staticTextures.keySet())
		{
			textureKey = key;
			tileImage = TextureHandler.scaleTexture(textureKey,sc);
			staticTextures.replace(key, tileImage);
		}

		//tileTree.clear();
		for(Tile t : tiles)
		{
			textureKey = t.getTexturePath();
			if(t.getRenderAngle() != 0)
			{
				t.setTexture(TextureHandler.rotateTexture(textureKey, t.getRenderAngle(), sc), sc);
			}
			else
			{
				t.setTexture(staticTextures.get(textureKey), sc);
			}
			//tileTree.insert(t);
		}
		System.gc();
		reScaleRequest = false;
	}
	public void drawQuad(Graphics2D g)
	{

		ArrayList<Rectangle> quads = new ArrayList<Rectangle>();
		tileTree.getNodes(quads);
		g.setColor(Color.YELLOW);
		for(Rectangle r : quads)
		{
			//System.out.println(r.getX()+" "+r.getY());
			g.drawRect((int)Math.round(r.getX()), Math.round((int)r.getY()), (int)Math.round(r.getWidth()), (int)Math.round(r.getHeight()));
		}
		ArrayList<PhysicsObject> objs = new ArrayList<PhysicsObject>();
		tileTree.getAllObjects(objs);
	}
	public int getPixelWidth()
	{
		int ans = 0;
		for(Tile t : tiles)
		{
			if((int)(t.getX()+t.getWidth()) > ans) {ans = (int)t.getX()+(int)t.getWidth();}
		}
		return (int)(0.01+ans*camera.getScreenScale()[0]);
	}

	public int getPixelHeight()
	{
		int ans = 0;
		for(Tile t : tiles)
		{
			if((int)(t.getY()+t.getHeight()) > ans) {ans = (int)(t.getY()+t.getHeight());}
		}
		return (int)(0.01+ans*camera.getScreenScale()[1]);
	}
	public int[] getOriginTilePosition()
	{
		return new int[] {0,0};
	}
	public double[] getPos(){return new double[]{position[0],position[1]};}
	public ArrayList<Tile> getTiles(){return tiles;}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public double getX(){return position[0];}
	public double getY(){return position[1];}
	public int getLevelBoundaryTop() {return levelBoundaryTop;}
	public int getLevelBoundaryBottom() {return levelBoundaryBottom;}
	public int getLevelBoundaryLeft() {return levelBoundaryLeft;}
	public int getLevelBoundaryRight() {return levelBoundaryRight;}
}
