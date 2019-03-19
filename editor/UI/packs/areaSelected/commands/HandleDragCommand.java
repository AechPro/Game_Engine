package editor.UI.packs.areaSelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;

public class HandleDragCommand extends Command
{

	public HandleDragCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((AreaSelectState)target).handleDrag((MouseEvent)inputs[0]);
		return null;
	}

}
