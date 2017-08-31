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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class JCustomPanel extends JPanel {

	private boolean iBackColor=false;
	
	public JCustomPanel(){
		
	}
	
	public JCustomPanel(boolean backColor){
		
		iBackColor=backColor;
		
	}
	
	@Override
	public void paintComponent(Graphics g){

		Color borderColor=super.getBackground();
		
		Graphics2D g2=(Graphics2D) g;
		
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    Color borderColorShadow=new Color(50,50,50,15);
	    
	    g2.setColor(borderColorShadow);
	    
	    for(int i=0; i<5; i++){
	    	
	    	g.fillRoundRect(i, i, super.getWidth()-4*i/3, super.getHeight()-4*i/3, 5+6-i, 5+6-i);
	    	
	    }
	    
	    g.setColor(iBackColor ? borderColor.brighter().brighter() : Color.white);
	    g.fillRoundRect(2, 2, super.getWidth()-6, super.getHeight()-6, 5, 5);
	    
	    g.setColor(borderColor);
	    g.drawRoundRect(2, 2, super.getWidth()-6, super.getHeight()-6, 5, 5);
		
	}

	
}
