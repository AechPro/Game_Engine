package core.util;
import java.util.Stack;

public class FixedSizeStack<T> extends Stack<T> 
{
	private static final long serialVersionUID = -1981506474196883263L;
	private int maxElements;

    public FixedSizeStack(int maxEles) 
    {
        super();
        maxElements = maxEles;
    }

    @Override
    public T push(T element) 
    {
    	for(int i=0,stop = this.size() - maxElements;i<stop;i++)
    	{
    		remove(0);
    	}
        return super.push(element);
    }
}