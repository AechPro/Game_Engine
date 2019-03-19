package core.UI.commands;

import core.UI.Command;
import core.anims.Animation;

public class AnimationCycleCommand extends Command
{

	public AnimationCycleCommand(Object tar) {
		super(tar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(Object[] inputs) 
	{
		((Animation)target).incrementFrame();
		return null;
	}

}
