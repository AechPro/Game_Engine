package core.util;

import java.util.HashMap;

import core.UI.Command;

public class TimerManager 
{
	public static final int THREE_FPS_TIMER = 3;
	public static final int FIVE_FPS_TIMER = 5;
	public static final int SEVEN_FPS_TIMER = 7;
	public static final int TEN_FPS_TIMER = 10;
	public static final int THIRTEEN_FPS_TIMER = 13;
	public static final int FIFTEEN_FPS_TIMER = 15;
	public static final int SEVENTEEN_FPS_TIMER = 17;
	public static final int TWENTY_FPS_TIMER = 20;

	private HashMap<Integer, TimerThread> timerMap;
	
	private int[] timingRates;
	public TimerManager()
	{
		timerMap = new HashMap<Integer, TimerThread>();
		
		timingRates = new int[8];
		timingRates[0] = THREE_FPS_TIMER;
		timingRates[1] = FIVE_FPS_TIMER;
		timingRates[2] = SEVEN_FPS_TIMER;
		timingRates[3] = TEN_FPS_TIMER;
		timingRates[4] = THIRTEEN_FPS_TIMER;
		timingRates[5] = FIFTEEN_FPS_TIMER;
		timingRates[6] = SEVENTEEN_FPS_TIMER;
		timingRates[7] = TWENTY_FPS_TIMER;
	}
	public void build()
	{
		for(int i=0;i<timingRates.length;i++)
		{
			timerMap.put(timingRates[i], new TimerThread(timingRates[i]));
		}

	}
	public Integer findClosestTimer(int fps)
	{
		int m = Math.abs(timingRates[0] - fps);
		int idx = 0;
		for(int i=0;i<timingRates.length;i++)
		{
			if(Math.abs(timingRates[i] - fps) < m)
			{
				m = Math.abs(timingRates[i] - fps);
				idx = i;
			}
		}
		return timingRates[idx];
	}
	public void start()
	{
		for(Integer key : timerMap.keySet())
		{
			timerMap.get(key).start();
		}
	}
	public void attachCommand(Command comm,Integer key)
	{
		timerMap.get(key).attachCommand(comm);
	}
}
