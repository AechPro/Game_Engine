package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;
public class CopyAreaSelectionCommand extends Command
{

	public CopyAreaSelectionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((AreaSelectState)target).copyArea();
		return null;
	}
}
