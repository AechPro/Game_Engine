package editor.UI.packs.idle.commands;


import core.UI.Command;
import editor.UI.packs.idle.state.IdleState;

public class ZoomInCommand extends Command
{

	public ZoomInCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).zoomIn();
		return null;
	}

}
