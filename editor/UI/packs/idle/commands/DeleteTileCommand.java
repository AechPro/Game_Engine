package editor.UI.packs.idle.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.idle.state.IdleState;

public class DeleteTileCommand extends Command
{

	public DeleteTileCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((IdleState)target).deleteTile((MouseEvent)inputs[0]);
		return null;
	}
}
