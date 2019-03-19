package editor.UI.packs.idle.commands;

import core.UI.Command;
import editor.UI.packs.idle.state.IdleState;

public class PasteAreaCommand extends Command
{

	public PasteAreaCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).pasteArea();
		return null;
	}

}
