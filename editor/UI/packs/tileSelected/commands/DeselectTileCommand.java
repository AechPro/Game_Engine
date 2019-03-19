package editor.UI.packs.tileSelected.commands;

import core.UI.Command;
import editor.UI.packs.tileSelected.state.TileSelectedState;

public class DeselectTileCommand extends Command

{

	public DeselectTileCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((TileSelectedState)target).deselectTile();
		return null;
	}

}
