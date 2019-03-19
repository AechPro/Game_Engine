package editor.UI.packs.tileSelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.tileSelected.state.TileSelectedState;

public class HandleDragCommand extends Command
{

	public HandleDragCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((TileSelectedState)target).handleDrag((MouseEvent)inputs[0], (boolean)inputs[1]);
		return null;
	}

}
