package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;
public class DeleteAreaSelectionCommand extends Command
{

	public DeleteAreaSelectionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((AreaSelectState)target).deleteArea(true);
		return null;
	}
}
