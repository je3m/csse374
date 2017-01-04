package problem.renderers.linux;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import problem.components.IComponent;
import problem.components.Window;
import problem.renderers.AbstractRenderer;

public class WindowRenderer extends AbstractRenderer {
	public static final int V_SPACE = 3;
	public static final int H_SPACE = 3;
	public static final int TITLE_HEIGHT = 25;
	
	public WindowRenderer() {
		this(null);
	}

	public WindowRenderer(IComponent c) {
		super(c);
	}

	@Override
	public void render(Graphics2D g) {
		Window c = (Window)this.getComponent();
		
		// Draw the border
		g.setColor(new Color(48,0,0));
		g.draw3DRect(1, 1, c.getBounds().width - H_SPACE, c.getBounds().height - V_SPACE, true);

		// Draw the title bar
		g.setColor(new Color(96,0,0));
		g.fill3DRect(1, 1, (int)c.getBounds().width - H_SPACE , TITLE_HEIGHT, true);
		
		// Draw the title
		g.setFont(new Font("Arial", Font.PLAIN, 17)); 		
		g.setColor(Color.white);
		g.drawString(c.getTitle(), 5, 20);
	}
}
