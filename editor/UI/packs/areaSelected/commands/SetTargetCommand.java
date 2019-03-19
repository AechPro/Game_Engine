package editor.UI.packs.areaSelected.commands;

import java.awt.event.MouseEvent;

import core.UI.Command;
import editor.UI.packs.areaSelected.state.AreaSelectState;

public class SetTargetCommand extends Command {

	public SetTargetCommand(Object tar) {
		super(tar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(Object[] inputs) {
		
		((AreaSelectState)target).setTarget((MouseEvent)inputs[0]);
		return null;
	}

}