package editor.UI.packs.copySelected.commands;

import core.UI.Command;
import editor.UI.packs.copySelected.state.*;

public class DeselectCopyCommand extends Command
{
	public DeselectCopyCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((CopySelectedState)target).deselectCopy();
		return null;
	}

}
