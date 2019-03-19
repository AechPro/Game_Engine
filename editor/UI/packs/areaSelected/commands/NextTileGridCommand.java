package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.panel.AreaSelectDisplayPanel;

public class NextTileGridCommand extends Command {

	public NextTileGridCommand(Object tar) {
		super(tar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(Object[] inputs) {
		
		((AreaSelectDisplayPanel)target).nextTileGrid();
		return null;
	}

}
