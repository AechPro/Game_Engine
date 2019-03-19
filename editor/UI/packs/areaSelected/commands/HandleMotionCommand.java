package editor.UI.packs.areaSelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;

public class HandleMotionCommand extends Command
{

	public HandleMotionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((AreaSelectState)target).handleMotion((MouseEvent)inputs[0]);
		return null;
	}

}
