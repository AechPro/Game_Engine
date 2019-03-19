package editor.UI.packs.areaSelected.commands;

import core.UI.Command;
import editor.UI.packs.areaSelected.panel.AreaSelectDisplayPanel;

public class LoadTilePanelCommand extends Command {

	public LoadTilePanelCommand(Object tar) {
		super(tar);
	}

	@Override
	public Object execute(Object[] inputs) {
		
		((AreaSelectDisplayPanel)target).loadPanel();
		return null;
	}

}
