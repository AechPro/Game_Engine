package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;
public class FillAreaSelectionCommand extends Command
{
	public FillAreaSelectionCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((AreaSelectState)target).fillArea();
		return null;
	}
}
