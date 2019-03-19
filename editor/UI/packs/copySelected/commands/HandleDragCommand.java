package editor.UI.packs.copySelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.copySelected.state.CopySelectedState;
import editor.UI.packs.tileSelected.state.TileSelectedState;

public class HandleDragCommand extends Command
{

	public HandleDragCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((CopySelectedState)target).handleDrag((MouseEvent)inputs[0]);
		return null;
	}

}
