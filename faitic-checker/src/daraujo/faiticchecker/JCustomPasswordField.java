package daraujo.faiticchecker;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

public class JCustomPasswordField extends JPasswordField {

	private Color iBorderColor;

	public JCustomPasswordField(Color borderColor) {

		super();
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setOpaque(false);

		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		iBorderColor=borderColor;

	}

	@Override
	public void paintComponent(Graphics g){

		Graphics2D g2=(Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(iBorderColor);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

		g2.setColor(Color.white);
		g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 4, 4);

		super.paintComponent(g);

	}

}
