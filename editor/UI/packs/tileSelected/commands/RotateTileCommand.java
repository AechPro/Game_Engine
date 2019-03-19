package editor.UI.packs.tileSelected.commands;

import core.UI.Command;
import editor.UI.packs.tileSelected.state.TileSelectedState;

public class RotateTileCommand extends Command
{

	public RotateTileCommand(Object tar) {
		super(tar);
	}

	@Override
	public Object execute(Object[] inputs) 
	{
		((TileSelectedState)target).rotateTile();
		return null;
	}

}
