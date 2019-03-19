package editor.UI.commands;

import core.UI.Command;
import editor.level.Map;
public class RestorePreviousMapCommand extends Command
{
	public RestorePreviousMapCommand(Object tar) {super(tar);}

	@Override
	public Object execute(Object[] inputs) 
	{
		((Map)target).loadPrevSet();
		return null;
	}
}
