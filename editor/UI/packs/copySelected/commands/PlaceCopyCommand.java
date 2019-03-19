package editor.UI.packs.copySelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.copySelected.state.*;

public class PlaceCopyCommand extends Command
{
	public PlaceCopyCommand(Object tar) 
	{
		super(tar);
	}

	@Override
	public Object execute(Object[] inputs) 
	{
		((CopySelectedState)target).placeCopy((MouseEvent)inputs[0]);
		return null;
	}
	

}
