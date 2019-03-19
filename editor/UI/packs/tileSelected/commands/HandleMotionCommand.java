package editor.UI.packs.tileSelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.tileSelected.state.TileSelectedState;

public class HandleMotionCommand extends Command
{

	public HandleMotionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((TileSelectedState)target).handleMotion((MouseEvent)inputs[0]);
		return null;
	}

}
