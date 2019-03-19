package editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.UI.UIStateMachine;
import core.camera.Camera;
import editor.UI.packs.areaSelected.state.AreaSelectState;
import editor.UI.packs.copySelected.state.CopySelectedState;
import editor.UI.packs.idle.state.IdleState;
import editor.UI.packs.tileSelected.state.TileSelectedState;
import editor.level.Map;
import editor.spriteSheetManager.sheetCutter;

public class Main extends JPanel implements Runnable
{
	public static final String IDLE_STATE = "idle_state";
	public static final String AREA_SELECT_STATE = "area_select_state";
	public static final String TILE_SELECTED_STATE = "tile_selected_state";
	public static final String COPY_SELECTED_STATE = "copy_selected_state";
	
	private static final long serialVersionUID = 2454013823625055695L;
	private Thread thread;
	private BufferedImage img;
	private Graphics2D g;
	private boolean running;
	private static final int imgWidth = (int)Math.round(1280), imgHeight = (int)Math.round(720);
	private static final int windowWidth = 1920, windowHeight = 1080;
	private UIStateMachine userInterface;
	private Map level;
	private Camera camera;
	private AffineTransform prevGfxTransform;
	
	private AffineTransform screenScale;
	private CameraFocus cameraSubject;
	private static BufferStrategy frameBuffer;

	private double[] scale;

	public Main(){super();}
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			thread = new Thread(this);
			try{thread.start();}
			catch(Exception e)
			{
				System.out.println("Main thread could not be started!");
				e.printStackTrace();
			}
		}
	}
	public void init()
	{
		setDoubleBuffered(true);
		running = false;
		img = new BufferedImage(windowWidth, windowHeight,BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D)img.getGraphics();
		
		scale = new double[] {(double)windowWidth/imgWidth,(double)windowHeight/imgHeight};
		
		//scale = new double[] {1.0,1.0};
		
		screenScale = new AffineTransform();
		screenScale.scale(scale[0], scale[1]);
		
		cameraSubject = new CameraFocus(new double[] {windowWidth/2 - 216, windowHeight/2 - 64});
		
		camera = new Camera(0,0,windowWidth, windowHeight, scale, -100, cameraSubject);
		level = new Map(10,15,camera);
		userInterface = new UIStateMachine(scale);
		
		//sheetCutter.cutAndSave("resources/sheetTiles", "hollow_tree.png");
		
		initStateMachine();
		
		addMouseListener(new MouseInputHandler());
		addMouseMotionListener(new MouseMotionHandler());
		addKeyListener(new KeyInputHandler());
		requestFocus();
		
		running = true;
	}
	public void run()
	{
		init();
		//long t1 = System.nanoTime();
		
		while(running)
		{	
			//t1 = System.nanoTime();
			update();
			//System.out.println("UPDATE: "+(System.nanoTime() - t1)/1000d+"us");
			//t1 = System.nanoTime();
			render(1.0);
			//System.out.println("RENDER: "+(System.nanoTime() - t1)/1000d+"us");
			//t1 = System.nanoTime();
			draw();
			//System.out.println("DRAW: "+(System.nanoTime() - t1)/1000d+"us");

			//frameDelta = (System.nanoTime() - frameStart)/1000000000d;
		}
	}
	public void update()
	{
		userInterface.update();
		camera.update();
		cameraSubject.update();
	}
	public void render(double delta)
	{
		clearCanvas();
		prevGfxTransform = g.getTransform();
		
		g.setTransform(camera.computeTransform(delta,false));
		level.render(g);
		//((CameraFocus)cameraSubject).render(g);
		g.setTransform(prevGfxTransform);
		//level.drawBounds(g);
		userInterface.render(g);
	}
	public void draw()
	{
		do {
	         // The following loop ensures that the contents of the drawing buffer
	         // are consistent in case the underlying surface was recreated
	         do {
	             // Get a new graphics context every time through the loop
	             // to make sure the strategy is validated
	             Graphics2D g2 = (Graphics2D)frameBuffer.getDrawGraphics();
	             g2.drawImage(img,0,32,windowWidth,windowHeight,null);
	             g2.dispose();

	             // Repeat the rendering if the drawing buffer contents
	             // were restored
	         } while (frameBuffer.contentsRestored());

	         // Display the buffer
	         frameBuffer.show();

	         // Repeat the rendering if the drawing buffer was lost
	     } while (frameBuffer.contentsLost());
	}
	
	public void initStateMachine()
	{
		CopySelectedState copySelected = new CopySelectedState(camera,level);
		TileSelectedState tileSelected = new TileSelectedState(camera,level);
		AreaSelectState areaSelect = new AreaSelectState(camera,level);
		IdleState idle = new IdleState(camera, level);
		
		userInterface.addState(IDLE_STATE,idle);
		userInterface.addState(AREA_SELECT_STATE,areaSelect);
		userInterface.addState(TILE_SELECTED_STATE,tileSelected);
		userInterface.addState(COPY_SELECTED_STATE, copySelected);
	}
	public void clearCanvas()
	{
		g.clearRect(0,0,windowWidth,windowHeight);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, windowWidth, windowHeight);
		g.setColor(Color.WHITE);
	}
	public void delay(int millis)
	{
		try{Thread.sleep(millis);}
		catch(Exception e){e.printStackTrace();}
	}
	public static void main(String[] args)
	{
		System.setProperty("sun.java2d.opengl", "true");
		JFrame frame = new JFrame("Level Editor");
		frame.setContentPane(new Main());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setSize(windowWidth,windowHeight);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		frameBuffer = frame.getBufferStrategy();
		
	}
	private class MouseInputHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent e){userInterface.mousePressed(e);}
		public void mouseReleased(MouseEvent e){userInterface.mouseReleased(e);}
		public void mouseClicked(MouseEvent e){userInterface.mouseClicked(e);}
	}
	private class KeyInputHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent e){userInterface.keyPressed(e);}
		public void keyReleased(KeyEvent e){userInterface.keyReleased(e);}
		public void keyTyped(KeyEvent e){userInterface.keyTyped(e);}
	}
	private class MouseMotionHandler extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e) {userInterface.mouseMoved(e);}
		public void mouseDragged(MouseEvent e) {userInterface.mouseDragged(e);}
	}
}
