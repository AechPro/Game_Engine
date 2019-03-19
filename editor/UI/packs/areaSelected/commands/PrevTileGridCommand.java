package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.panel.AreaSelectDisplayPanel;

public class PrevTileGridCommand extends Command {

	public PrevTileGridCommand(Object tar) {
		super(tar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(Object[] inputs) {
		
		((AreaSelectDisplayPanel)target).prevTileGrid();
		return null;
	}

}