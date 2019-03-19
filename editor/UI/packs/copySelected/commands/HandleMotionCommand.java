package editor.UI.packs.copySelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.copySelected.state.*;

public class HandleMotionCommand extends Command
{

	public HandleMotionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((CopySelectedState)target).handleMotion((MouseEvent)inputs[0]);
		return null;
	}

}
