package editor.UI.packs.tileCreator.state;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import core.UI.Command;
import core.UI.State;
import core.camera.Camera;
import core.util.TextureHandler;
import editor.UI.packs.tileCreator.commands.*;
import editor.UI.packs.tileCreator.panel.TileCreatorDisplayPanel;
import editor.level.Map;

public class TileCreatorState extends State
{
	
	public TileCreatorState(Camera cam) 
	{
		super(cam);
	}

	@Override
	public void init()
	{
		displayPanel = new TileCreatorDisplayPanel(camera.getScreenScale(), (int)camera.getViewPort().getWidth(), (int)camera.getViewPort().getHeight());
		Command c = new CutAndSaveCommand(this);
		displayPanel.attachCommand("cut_and_save", c);
	}

	@Override
	public void onEnter() 
	{
	}

	@Override
	public void onExit() 
	{
	}

	@Override
	public void stateUpdate() 
	{
	}
	
}
