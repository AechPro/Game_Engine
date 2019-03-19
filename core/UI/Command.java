package core.UI;

public abstract class Command
{
	protected Object target;
	public Command(Object tar)
	{
		//reference
		target = tar;
	}
	public abstract Object execute(Object[] inputs);
}
