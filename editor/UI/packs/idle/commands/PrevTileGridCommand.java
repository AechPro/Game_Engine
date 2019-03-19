package editor.UI.packs.idle.commands;

import core.UI.Command;
import editor.UI.packs.idle.panel.IdleDisplayPanel;

public class PrevTileGridCommand extends Command {

	public PrevTileGridCommand(Object tar) {
		super(tar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(Object[] inputs) {
		
		((IdleDisplayPanel)target).prevTileGrid();
		return null;
	}

}