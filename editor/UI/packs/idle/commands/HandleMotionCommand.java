package editor.UI.packs.idle.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.idle.state.IdleState;

public class HandleMotionCommand extends Command
{

	public HandleMotionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).handleMotion((MouseEvent)inputs[0]);
		return null;
	}

}
