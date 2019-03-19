package editor.UI.packs.tileSelected.commands;

import core.UI.Command;
import editor.UI.packs.tileSelected.state.TileSelectedState;

public class PlaceTileCommand extends Command
{
	public PlaceTileCommand(Object tar) 
	{
		super(tar);
	}

	@Override
	public Object execute(Object[] inputs) 
	{
		((TileSelectedState)target).placeTile(true);
		return null;
	}
	

}
