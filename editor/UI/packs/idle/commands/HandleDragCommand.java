package editor.UI.packs.idle.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.idle.state.IdleState;

public class HandleDragCommand extends Command
{

	public HandleDragCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).handleDrag((MouseEvent)inputs[0]);
		return null;
	}

}
