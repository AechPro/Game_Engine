package editor.UI.packs.idle.commands;


import core.UI.Command;
import editor.UI.packs.idle.state.IdleState;

public class ZoomOutCommand extends Command
{

	public ZoomOutCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).zoomOut();
		return null;
	}

}
