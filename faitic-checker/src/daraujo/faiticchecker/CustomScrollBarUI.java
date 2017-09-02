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
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScrollBarUI extends BasicScrollBarUI {

	private Color back, fore, forenobar;
	
	public CustomScrollBarUI(Color background, Color foreground, Color foregroundnobar){
		
		back=background;
		fore=foreground;
		forenobar=foregroundnobar;
		
	}
	
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    	
    	boolean isVertical=c.getWidth()<c.getHeight();
    	
    	g.setColor(back);
    	g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

    	int barwidth=isVertical ? trackBounds.width/2 : trackBounds.height/2;
    	
    	Graphics2D g2=(Graphics2D) g;
    	
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
    	g2.setColor(forenobar);
    	
    	if(isVertical)
    		g2.fillRoundRect(trackBounds.x+barwidth/2, trackBounds.y, barwidth, trackBounds.height, barwidth,barwidth);
    	else
    		g2.fillRoundRect(trackBounds.x, trackBounds.y+barwidth/2, trackBounds.width, barwidth, barwidth,barwidth);

    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

    	boolean isVertical=c.getWidth()<c.getHeight();
    	
    	int barwidth=isVertical ? thumbBounds.width/2 : thumbBounds.height/2;
    	
    	Graphics2D g2=(Graphics2D) g;
    	
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
    	g2.setColor(fore);
    	
    	if(isVertical)
    		g2.fillRoundRect(thumbBounds.x+barwidth/2, thumbBounds.y, barwidth, thumbBounds.height, barwidth,barwidth);
    	else
    		g2.fillRoundRect(thumbBounds.x, thumbBounds.y+barwidth/2, thumbBounds.width, barwidth, barwidth,barwidth);
    	
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation){
    	
    	return new JButton(){
    		
    		@Override
    		public void paint(Graphics g){

    			g.setColor(back);
    			g.fillRect(0, 0, getWidth(), getHeight());
//
//    	    	Graphics2D g2=(Graphics2D) g;
//    	    	
//    		    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//    		    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//    		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//    		    
    			
    		}
    		
    	};
    	
    }

    @Override
    protected JButton createIncreaseButton(int orientation){
    	
    	return new JButton(){
    		
    		@Override
    		public void paint(Graphics g){
    			
    			g.setColor(back);
    			g.fillRect(0, 0, getWidth(), getHeight());
//
//    	    	Graphics2D g2=(Graphics2D) g;
//    	    	
//    		    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//    		    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//    		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//    		    
    			
    		}
    		
    	};
    	
    }
    
}
