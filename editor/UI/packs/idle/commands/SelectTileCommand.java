package editor.UI.packs.idle.commands;

import core.UI.Command;
import editor.UI.components.TileComponent;
import editor.UI.packs.idle.state.IdleState;

public class SelectTileCommand extends Command
{

	public SelectTileCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).selectTile((TileComponent)inputs[0]);
		return null;
	}
	

}
