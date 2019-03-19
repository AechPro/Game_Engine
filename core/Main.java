package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.camera.Camera;
import core.level.*;
import core.util.TimerManager;

public class Main extends JPanel implements Runnable
{
	public static final Random rng = new Random((long)(Math.random()*Long.MAX_VALUE));
	private static final long serialVersionUID = 2454013823625055695L;
	public static final TimerManager timingManager = new TimerManager();
	private Thread thread;
	private BufferedImage img;
	private Graphics2D g;
	private boolean running;
	private static final int windowWidth = 1920, windowHeight = 1080;
	private static final int imgWidth = 1280, imgHeight = 720;
	private Player playerCharacter;
	private Camera camera;
	private Level currentLevel;
	private double frameDelta;
	private int fps;
	private double frameRate;
	
	private static BufferStrategy frameBuffer;
	
	private Font font;
	
	private long frameStart;
	
	public Main()
	{
		super();
		create();
	}
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
		//setDoubleBuffered(true);
		running = false;
		img = new BufferedImage(windowWidth, windowHeight,BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D)img.getGraphics();
		font = new Font("TimesRoman",0,16);
		g.setFont(font);
		g.setClip(0,0,windowWidth,windowHeight);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		timingManager.build();
		
		fps = 60;
		double[] scale = new double[] {(double)windowWidth/imgWidth,(double)windowHeight/imgHeight};
		//double[] scale = new double[] {1.0,1.0};
		playerCharacter = new Player(new double[]{windowWidth/2.0+300, windowHeight/2.0}, 0.0, scale);
		camera = new Camera((int)(windowWidth),(int)(windowHeight),scale,32,playerCharacter);
		
		currentLevel = new firstLevel(playerCharacter,camera,scale);
		
		addMouseMotionListener(new MouseMotionHandler());
		addMouseListener(new MouseInputHandler());
		addKeyListener(new KeyInputHandler());
		requestFocus();
		
		timingManager.start();
		running = true;
	}
	public void run()
	{
		init();
		long secondTicker = System.nanoTime();
		double frameTick = 0;
		double lag = 0;
		
		double timeStep = 1d/fps;
		frameStart = System.nanoTime();
		
		int count = 0 ;
		//long t1 = 0l;
		while(running)
		{	
			frameStart = System.nanoTime();
			lag+=frameDelta;
			
			count = 0;
			while(lag >= timeStep && count < 5)
			{
				//t1 = System.nanoTime();
				update();
				//System.out.println("UPDATE: "+(System.nanoTime()- t1)/1000000+"ms");
				lag -= timeStep;
				count++;
			}
			//if(count > 1) {System.out.println("UPDATED "+count+" TIMES");}
			//delay(30);
			//t1 = System.nanoTime();
			render(frameDelta/timeStep);
			//System.out.println("RENDER: "+(System.nanoTime()- t1)/1000000+"ms");
			
			//t1 = System.nanoTime();
			draw();
			//System.out.println("DRAW: "+(System.nanoTime()- t1)/1000000+"ms");

			frameDelta = (System.nanoTime() - frameStart)/1000000000d;
			
			frameTick += 1;
			if(System.nanoTime() - secondTicker >= 1000000000) 
			{
				frameRate = (int)frameTick;
				frameTick = 0;
				secondTicker = System.nanoTime();
			}
			
		}
	}
	public void update()
	{
		currentLevel.update();
	}
	public void render(double delta)
	{
		clearCanvas();
		currentLevel.render(g,delta);
		
		g.setColor(Color.GREEN);
		g.drawString("FPS: "+frameRate,2,15);
		
	}
	public void draw()
	{
		//Toolkit.getDefaultToolkit().sync();
		//Graphics2D g2 = (Graphics2D)getGraphics();
		//g2.drawImage(img,0,32,windowWidth,windowHeight,null);
        //g2.dispose();
		
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
		//g2.setClip(0,0,imgWidth,imgHeight);
	}
	
	public void create()
	{
		System.setProperty("sun.java2d.opengl", "true");
		JFrame frame = new JFrame("Game Engine");
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setSize(windowWidth,windowHeight);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		frameBuffer = frame.getBufferStrategy();
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
		new Main();
	}
	private class MouseInputHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mouseClicked(MouseEvent e){}
	}
	private class KeyInputHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			currentLevel.keyPressed(e);
		}
		public void keyReleased(KeyEvent e)
		{
			currentLevel.keyReleased(e);
		}
		public void keyTyped(KeyEvent e){}
	}
	private class MouseMotionHandler extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e) {}
		public void mouseDragged(MouseEvent e) {}
	}
}
