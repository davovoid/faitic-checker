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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

public class JSubjectCheckBox extends JCheckBox {

	Object iValue;
	
	public JSubjectCheckBox() {
		
		super("");

	}
	

	public JSubjectCheckBox(Object value) {
		
		super("");
		iValue=value;
		
	}

	public Object getObject(){ return iValue; }
	
	@Override
	public void paintComponent(Graphics g){

		//super.paintComponent(g);
		
		Color borderColor=isSelected() ? new Color(0,110,198,255) : new Color(200,200,200,255);
		
		int maxside=17;
		int marginleft=super.getWidth()<super.getHeight() ? 0 : (super.getWidth()-maxside)/2;
		int margintop=super.getWidth()<super.getHeight() ? (super.getHeight()-maxside)/2 : 0;

		Graphics2D g2=(Graphics2D) g;
		
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setColor(borderColor);
	    
		g2.fillRoundRect(marginleft, margintop, maxside+1, maxside+1, 3, 3);
		
		if(isSelected()){
			
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.white);
			
			g2.drawLine(marginleft + 4, margintop+maxside/2, marginleft + maxside/2-1 , margintop+maxside-6);
			g2.drawLine(marginleft + maxside/2 -1, margintop+maxside-6, marginleft + maxside - 4, margintop + 6);
			
		} else{
			
			g2.setColor(Color.white);
			g2.fillRect(marginleft+2, margintop+2, maxside-4+1, maxside-4+1);
			
		}
		
	}


}
