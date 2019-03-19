package editor.UI.packs.copySelected.commands;

import core.UI.Command;
import editor.UI.packs.copySelected.state.*;

public class RotateCopyCommand extends Command
{

	public RotateCopyCommand(Object tar) {
		super(tar);
	}

	@Override
	public Object execute(Object[] inputs) 
	{
		((CopySelectedState)target).rotateCopy();
		return null;
	}

}
