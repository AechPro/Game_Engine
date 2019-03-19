package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;
public class CreateCircularAreaCommand extends Command
{

	public CreateCircularAreaCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((AreaSelectState)target).createCircularArea();
		return null;
	}
}
