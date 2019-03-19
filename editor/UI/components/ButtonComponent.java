package editor.UI.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import core.UI.Command;
import core.UI.Component;

public class ButtonComponent extends Component
{
	private Command onClickCommand;
	public ButtonComponent(double[] sc, Command onClick, int w, int h, String id) {
		super(sc,null,id);
		onClickCommand = onClick;
		width = w;
		height = h;
	}

	@Override
	public void handleMouseInput(MouseEvent e) 
	{
		if(intersects(e))
		{
			setOutlineColor(Color.yellow);
			onClickCommand.execute(null);
			
		}
	}

	@Override
	public void handleKeyInput(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() 
	{
		
	}

	@Override
	public Component copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cRender(Graphics2D g) 
	{
		if(outlineColor != null)
		{
			outlineColor = null;
		}
	}

	@Override
	public void componentInit() {
		// TODO Auto-generated method stub
		
	}
	
}
