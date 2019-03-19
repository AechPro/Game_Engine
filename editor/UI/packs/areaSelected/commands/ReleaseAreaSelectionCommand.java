package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;
public class ReleaseAreaSelectionCommand extends Command
{

	public ReleaseAreaSelectionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((AreaSelectState)target).release();
		return null;
	}
}
