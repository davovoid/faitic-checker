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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class JCustomButton extends JButton {

	public JCustomButton(String text){
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setForeground(Color.WHITE);
		setText(text);
		setOpaque(false);
		setMinimumSize(new Dimension(60, 30));
		
	}
	
	@Override
	public void paint(Graphics g){

		//super.paintComponent(g);
		
		Color borderColor= !isEnabled() ? new Color(200,200,200,255) : new Color(0,110,198,255);
		Color fontColor=getForeground();
		
		int maxwidth=super.getWidth();
		int maxheight=super.getHeight();
		int marginleft=(super.getWidth()-maxwidth)/2;
		int margintop=(super.getHeight()-maxheight)/2;

		Graphics2D g2=(Graphics2D) g;
		
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setColor(borderColor);
	    
		g2.fillRoundRect(marginleft, margintop, maxwidth, maxheight, 6, 6);
		/*
		g2.setColor(borderColor.brighter());
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(marginleft, margintop, maxwidth-1, maxheight-1, 6, 6);
		
		g2.setColor(borderColor.darker());
		g2.setStroke(new BasicStroke(1));
		g2.drawRoundRect(marginleft, margintop, maxwidth-1, maxheight-1, 6, 6);
		*/
		g2.setColor(fontColor);
		g2.setFont(getFont());
		g2.drawString(getText(), (getWidth()-g.getFontMetrics().stringWidth(getText()))/2, g.getFontMetrics().getAscent()+(getHeight()-g.getFontMetrics().getAscent())/2);
		
		
	}
}
