package editor.UI.packs.idle.commands;

import core.UI.Command;
import editor.level.*;

public class SaveMapCommand extends Command
{

	public SaveMapCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		System.out.println(target);
		((Map)target).saveMap((String)inputs[0]);
		return null;
	}

}
