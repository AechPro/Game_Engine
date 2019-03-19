package editor.UI.packs.idle.commands;

import core.UI.Command;
import editor.UI.packs.idle.state.IdleState;

public class HandleScrollbarCommand extends Command
{

	public HandleScrollbarCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).handleScrollbar((double)inputs[0], (boolean)inputs[1]);
		return null;
	}

}
