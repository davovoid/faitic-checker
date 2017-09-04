/*
 * 	Faicheck - A NON OFFICIAL application to manage the Faitic Platform
 * 	Copyright (C) 2016, 2017 David Ricardo Araújo Piñeiro
 * 	
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
