package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.components.TileComponent;
import editor.UI.packs.areaSelected.state.AreaSelectState;

public class GiveTargetCommand extends Command {

	public GiveTargetCommand(Object tar) {
		super(tar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(Object[] inputs) {
		
		((AreaSelectState)target).giveTarget((TileComponent)inputs[0]);
		return null;
	}

}