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

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.Cursor;

import javax.swing.SwingConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ScheduleViewerGUI {

	protected static TextData textdata;
	
	protected static JFrame frmScheduleViewer;
	private static JPanel panelOptions, panelSchedule;
	
	protected static String username;
	
	private static Schedule schedule;
	
	private static int scheduleindex=-1;
	
	private static JLabel[] titleLabels;
	private static JLabel addScheduleLabel;
	
	private static JLabel lblEdit, lblDelete, lblDuplicate, lblExport, lblleft, lblright,
	lbleventname, lbleventhours, lblclose;
	
	private static JTextPane txtdescription;
	
	private static JPanel paneldescription;
	private static JScrollPane scrollDescription;

	/**
	 * SCHEDULE THINGS
	 */
	
	private static void initializeScheduleList(){
		
		panelOptions.removeAll();
		panelOptions.updateUI();
		
		String[] schedulenames=schedule.getScheduleNames();
		
		titleLabels=new JLabel[schedulenames.length];
		
		for(int i=0; i<schedulenames.length; i++){
			
			String schedulename=schedulenames[i];
			
			JLabel titleLabel = new JLabel(schedulename);
			titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			titleLabel.setForeground(new Color(0,110,198,255));
			titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
			
			titleLabel.addMouseListener(new MouseAdapter(){
				
				@Override
				public void mouseClicked(MouseEvent arg0){
					
					titleLabelSelected((JLabel)arg0.getSource());
					
				}
				
			});
			
			panelOptions.add(titleLabel);
			titleLabels[i]=titleLabel;
			
		}
		
		addScheduleLabel=new JLabel("+");
		addScheduleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addScheduleLabel.setForeground(new Color(0,110,198,255));
		addScheduleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		
		addScheduleLabel.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent arg0){
				
				int prevtitlelabelslength=titleLabels.length;
				
				activateEditor(prevtitlelabelslength);
				
				if(titleLabels.length>prevtitlelabelslength) selectSchedule(prevtitlelabelslength);
				
			}
			
		});
		
		panelOptions.add(addScheduleLabel);
		
		if(scheduleindex<titleLabels.length) selectSchedule(scheduleindex);
		
	}
	
	private static void titleLabelSelected(JLabel titleLabel){
		
		int selectedLabel=-1;
		
		for(int i=0; i<titleLabels.length; i++){
			
			if(titleLabels[i].equals(titleLabel)){
				selectedLabel=i;
			}
			
		}
		
		if(selectedLabel>=0){
			
			selectSchedule(selectedLabel);
			
		}
		
	}
	
	private static void activateEditor(int scheduleIndex){
		
		ScheduleEditorGUI scheduleeditor=new ScheduleEditorGUI(textdata);
		scheduleeditor.schedule=schedule;
		scheduleeditor.username=username;
		scheduleeditor.scheduleIndex=scheduleIndex;
		
		scheduleeditor.setVisible(true);
		
		// Schedule editor closed, refresh
		initializeScheduleList();
		
	}
	
	private static void selectSchedule(int index){
		
		scheduleindex=index;
		
		for(int i=0; i<titleLabels.length; i++){
			
			titleLabels[i].setForeground(new Color(0,110,198,255));
			titleLabels[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
		}
		
		if(index>=0){
			
			titleLabels[index].setForeground(SystemColor.windowText);
			titleLabels[index].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
		}

		schedule.readEvents(index);
		
		lblEdit.setVisible(index>=0);
		lblDelete.setVisible(index>=0);
		lblDuplicate.setVisible(index>=0);
		lblExport.setVisible(index>=0);
		lblleft.setVisible(index>=0);
		lblright.setVisible(index>=0);
		
		lblleft.setEnabled(index>0);
		lblright.setEnabled(index<titleLabels.length-1);
		
		if(index>=0){
			
			showSchedule(panelSchedule);
			
		} else{
			
			panelSchedule.removeAll();
			panelSchedule.updateUI();
			
		}
		
		paneldescription.setVisible(false);
		
	}
	
	private static void showSchedule(JPanel targetPanel){
		
		targetPanel.removeAll();
		targetPanel.updateUI();
		
		// Check minimum and maximum minutes, all days are common, as well as the minimum and maximum days.
		
		int minimum=-1, maximum=-1;
		int minday=-1, maxday=-1;
		
		for(ScheduleEvent event : schedule.eventList){
			
			if(minimum==-1 || event.getMinuteStart()<minimum){
				
				minimum=event.getMinuteStart();
				
			}
			

			if(maximum==-1 || event.getMinuteEnd()>maximum){
				
				maximum=event.getMinuteEnd();
				
			}
			
			
			if(minday==-1 || event.getDay()<minday){
				
				minday=event.getDay();
				
			}
			

			if(maxday==-1 || event.getDay()>maxday){
				
				maxday=event.getDay();
				
			}
			
			
		}
		
		// Now round them to half an hour (30 minutes)
		
		minimum-=minimum%30;
		if(maximum%30>0) maximum+=30-(maximum%30);
		
		// Now get all times from all events so as to order them in an ArrayList (Incl. minimum and maximum)
		
		ArrayList<Integer> listminutes=new ArrayList<Integer>();
		
		listminutes.add(minimum);
		listminutes.add(maximum);
		
		// So for each event
		for(ScheduleEvent event : schedule.eventList){
			
			boolean startissmaller=true;
			boolean endissmaller=true;
			
			// Check position for start (no added if equal to any element)
			
			for(int i=0; i<listminutes.size() && startissmaller; i++){
				
				if(event.getMinuteStart()==(int)listminutes.get(i)){
					
					startissmaller=false;
					
				} else if(event.getMinuteStart()<(int)listminutes.get(i)){

					listminutes.add(i, event.getMinuteStart());
					startissmaller=false;
					
				}
				
			}
			
			// And for end
			
			for(int i=0; i<listminutes.size() && endissmaller; i++){
				
				if(event.getMinuteEnd()==(int)listminutes.get(i)){
					
					endissmaller=false;
					
				} else if(event.getMinuteEnd()<(int)listminutes.get(i)){
					
					listminutes.add(i, event.getMinuteEnd());
					endissmaller=false;
					
				}
				
			}
			
			// No need to check if it exceeded the list as there is the maximum

		}
		
		// Now let's add partial minutes going 30 by 30 minutes
		
		int partialminute=minimum;
		int startlookingpos=0;		// So as not to start where it is checked that it cannot be
		
		while(partialminute<maximum){
			
			boolean issmaller=true;
			
			for(int i=startlookingpos; i<listminutes.size() && issmaller; i++){
				
				if(partialminute==(int)listminutes.get(i)){
					
					issmaller=false;
					startlookingpos=i;
					
				} else if(partialminute<(int)listminutes.get(i)){
					
					listminutes.add(i, partialminute);
					
					issmaller=false;
					startlookingpos=i;
					
				}
				
				
			}
			
			partialminute+=30; // Increment
			
		}
		
		// Now there is a minimum, a maximum, the actual minutes and the partial half hours.
		// Also we have the minimum and maximum day required.
		
		// So let's arrange the panel
		
		// Layout
		ColumnSpec[] columnspec=new ColumnSpec[maxday-minday+4];
		RowSpec[] rowspec=new RowSpec[listminutes.size()+3-1]; // Two different minutes make one element
		
		columnspec[0]=ColumnSpec.decode("10dlu:grow");
		columnspec[columnspec.length-1]=ColumnSpec.decode("10dlu:grow");
		
		for(int i=1; i<columnspec.length-1; i++)
			columnspec[i]=ColumnSpec.decode("pref:grow");
		
		rowspec[0]=RowSpec.decode("10dlu:grow");
		rowspec[rowspec.length-1]=RowSpec.decode("10dlu:grow");
		
		for(int i=1; i<rowspec.length-1; i++)
			rowspec[i]=RowSpec.decode("pref:grow");
		
		targetPanel.setLayout(new FormLayout(columnspec,rowspec));
		
		// Arranged layout. Now set the elements
		
		String[] dayofweek=textdata.getKey("daysofweek").split(",");
		
		for(int day=minday; day<=maxday; day++){
			
			JPanel panelDay = new JCustomPanel();
			panelDay.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.UNRELATED_GAP_COLSPEC,
					ColumnSpec.decode("pref:grow"),
					FormFactory.UNRELATED_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("pref:grow"),
					FormFactory.RELATED_GAP_ROWSPEC,}));
			
			targetPanel.add(panelDay, (day-minday+3) + ", 2, fill, fill");
			
			JLabel labelDay = new JLabel(dayofweek[day]);
			labelDay.setHorizontalAlignment(JLabel.CENTER);
			panelDay.add(labelDay,"2, 2, fill, fill");

		}
		
		// Now it is a little bit tricky.
		// We need to check if the minute in listminutes is divisible between 30.
		// If not, we will be looking for some element that is that way
		
		int elementspassed=1;
		int elementstart=listminutes.get(0); // Always divisible

		for(int i=1; i<listminutes.size(); i++){
			
			if(listminutes.get(i)%30==0){
				
				// Divisible
				
				int elementend=listminutes.get(i);
				
				// GUI related
				
				int hs=ScheduleEvent.getHour(elementstart);
				int ms=ScheduleEvent.getMinute(elementstart);
				int he=ScheduleEvent.getHour(elementend);
				int me=ScheduleEvent.getMinute(elementend);
				
				String output= 	hs + ":" + (ms > 9 ? ms : "0" + ms) + " - " +
								he + ":" + (me > 9 ? me : "0" + me);
				
				JPanel panelHour = new JCustomPanel();
				panelHour.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.UNRELATED_GAP_COLSPEC,
						ColumnSpec.decode("pref:grow"),
						FormFactory.UNRELATED_GAP_COLSPEC,},
					new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("pref:grow"),
						FormFactory.RELATED_GAP_ROWSPEC,}));
				
				targetPanel.add(panelHour, "2, " + (i-elementspassed + 3) + ", 1, " + elementspassed + ", fill, fill");

				JLabel labelHour = new JLabel(output);
				labelHour.setHorizontalAlignment(JLabel.CENTER);
				panelHour.add(labelHour,"2, 2, fill, fill");

				// Next iteration
				
				elementspassed=1;
				elementstart=elementend;
				
			} else{
				
				elementspassed++;
				
			}
			
			
			
		}
		
		// Now for every event make a panel and so on
		
		for(ScheduleEvent event : schedule.eventList){
			
			int posstart=listminutes.indexOf(event.getMinuteStart());
			int posend=listminutes.indexOf(event.getMinuteEnd());
			int day=event.getDay();
			
			JPanel panelEvent = new JCustomPanel(true);
			panelEvent.setBackground(event.getColor());
			targetPanel.add(panelEvent, (day + 3) + ", " + (posstart + 3) + ", 1, " + (posend-posstart) + ", fill, fill");
			panelEvent.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.UNRELATED_GAP_COLSPEC,
					FormFactory.GLUE_COLSPEC,
					FormFactory.UNRELATED_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("pref:grow"),
					FormFactory.PREF_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,}));
			
			//JLabel lblSubject = new JLabel(event.getEventName());
			//lblSubject.setHorizontalAlignment(SwingConstants.CENTER);
			//panelEvent.add(lblSubject, "2, 2");
			
			JTextPane lblSubject=new JTextPane();
			lblSubject.setText(event.getEventName());
			lblSubject.setEditable(false);
			lblSubject.setOpaque(false);
			lblSubject.setFont(new Font("Dialog", Font.BOLD, 12));
			
			StyledDocument std=lblSubject.getStyledDocument();
			SimpleAttributeSet sas=new SimpleAttributeSet();
			StyleConstants.setAlignment(sas, StyleConstants.ALIGN_CENTER);
			std.setParagraphAttributes(0, std.getLength(), sas, false);
			
			panelEvent.add(lblSubject, "2, 2");

			int hs=ScheduleEvent.getHour(event.getMinuteStart());
			int ms=ScheduleEvent.getMinute(event.getMinuteStart());
			int he=ScheduleEvent.getHour(event.getMinuteEnd());
			int me=ScheduleEvent.getMinute(event.getMinuteEnd());
			
			String output= 	hs + ":" + (ms > 9 ? ms : "0" + ms) + " - " +
							he + ":" + (me > 9 ? me : "0" + me);
			
			JLabel lblHourSubject = new JLabel(output);
			lblHourSubject.setFont(new Font("Dialog", Font.ITALIC, 10));
			lblHourSubject.setHorizontalAlignment(SwingConstants.CENTER);
			panelEvent.add(lblHourSubject, "2, 3");
			

			if(Color.RGBtoHSB(event.getColor().getRed(), event.getColor().getGreen(), event.getColor().getBlue(), null)[2]<0.8){
				
				lblSubject.setForeground(Color.white);
				lblHourSubject.setForeground(Color.white);
				
			}
			
			// Actions when panel or the elements inside it are clicked
			
			MouseAdapter dowhenclicked=new CustomMouseAdapter(event){
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
					
					if(arg0.getComponent().isEnabled()){
						
						ScheduleEvent event=(ScheduleEvent)super.getObject();
						
						lbleventname.setText(event.getEventName().replace("\n", " "));

						int hs=ScheduleEvent.getHour(event.getMinuteStart());
						int ms=ScheduleEvent.getMinute(event.getMinuteStart());
						int he=ScheduleEvent.getHour(event.getMinuteEnd());
						int me=ScheduleEvent.getMinute(event.getMinuteEnd());
						
						String output= 	hs + ":" + (ms > 9 ? ms : "0" + ms) + " - " +
										he + ":" + (me > 9 ? me : "0" + me);
						
						lbleventhours.setText(" " + textdata.getKey("daysofweek").split(",")[event.getDay()].toLowerCase() + ", " + output + " ");
						
						txtdescription.setText(event.getEventDescription()!=null ? event.getEventDescription() : "");

						txtdescription.setCaretPosition(0);

						scrollDescription.setVisible(event.getEventDescription()!=null);
						
						paneldescription.setVisible(true);
						
					}
					
				}
				
			};
			
			lblSubject.addMouseListener(dowhenclicked);
			lblHourSubject.addMouseListener(dowhenclicked);
			panelEvent.addMouseListener(dowhenclicked);
			
			
		}
		
		// And that's all!
		
	}
	
	private static boolean exportScheduleToImage(JPanel panel, String fileDest, String extension, double multiplier){
		

		Dimension prevsize=panel.getSize();
		
		panel.setSize(new Dimension((int)(prevsize.getWidth()*multiplier),(int)(prevsize.getHeight()*multiplier)));
		
		for(Component comp : panel.getComponents()){
			
			if(comp instanceof JPanel){
				
				for (Component comp2 : ((JPanel) comp).getComponents()){

						int currentsize=comp2.getFont().getSize();
						
						comp2.setFont(new Font(comp2.getFont().getFontName(), comp2.getFont().getStyle(),currentsize*(int)Math.floor(multiplier)));
						
					
				}
				
			}
			
		}
		
		BufferedImage imgToExport=new BufferedImage(panel.getWidth(), panel.getHeight(), 
				extension.toLowerCase().equals("jpg") || extension.toLowerCase().equals("bmp") ? 
				BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2=imgToExport.createGraphics();
		
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    if(extension.toLowerCase().equals("jpg") || extension.toLowerCase().equals("bmp")){
	    	
	    	g2.setColor(Color.white);
	    	g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
	    	
	    }
	    
	    panel.paintAll(g2);
		
	    boolean returnvalue=false;
	    
	    try {
	    	
			returnvalue=ImageIO.write(imgToExport, extension, new File(fileDest));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	    

		for(Component comp : panel.getComponents()){
			
			if(comp instanceof JPanel){
				
				for (Component comp2 : ((JPanel) comp).getComponents()){

						int currentsize=comp2.getFont().getSize();
						
						comp2.setFont(new Font(comp2.getFont().getFontName(), comp2.getFont().getStyle(),currentsize/(int)Math.floor(multiplier)));
						
				}
				
			}
			
		}
		
		panel.setSize(prevsize);
	    
	    return returnvalue;
	    
	}
	
	
	private static void doAtActivation(){
		
		frmScheduleViewer.setTitle(textdata.getKey("scheduleviewertitle",username));
		
		schedule=new Schedule(username);
		
		initializeScheduleList();
		
		if(titleLabels.length>0) selectSchedule(0);
		else selectSchedule(-1);
		
	}
	
	/**
	 * Create the application.
	 */
	public ScheduleViewerGUI(TextData td) {
		textdata=td;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmScheduleViewer = new JFrame();
		frmScheduleViewer.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {

				doAtActivation();
				
				frmScheduleViewer.setSize(frmScheduleViewer.getWidth()+1, frmScheduleViewer.getHeight()+1);

				frmScheduleViewer.setSize(frmScheduleViewer.getWidth()-1, frmScheduleViewer.getHeight()-1);

				
			}
		});
		frmScheduleViewer.setIconImage(Toolkit.getDefaultToolkit().getImage(ScheduleViewerGUI.class.getResource("/daraujo/faiticchecker/icon.png")));
		frmScheduleViewer.setTitle(textdata.getKey("scheduleviewertitle",""));
		frmScheduleViewer.setBounds(100, 100, 800, 600);
		frmScheduleViewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panelEverything = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){

				// Variables
				
				int topbarheight=panelOptions!=null ? panelOptions.getHeight() : 40;
				int bottombarheight=paneldescription!=null ? paneldescription.getHeight() : 100;
				Color borderColor=new Color(110,110,110,200);
				
				// Background
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				
				for(int i=0; i<super.getHeight(); i++){
					g.setColor(new Color(195,209,220, 0+i*150/super.getHeight() ));
					g.drawLine(0, i, super.getWidth(), i);
				}
				
				g.setColor(new Color(255,255,255,40));
				g.fillOval(-getWidth(), -getHeight(), getWidth()*2, getHeight()*2);
				
				// Top bar
				
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), topbarheight);
				
				g.setColor(borderColor);
				g.drawLine(0, topbarheight-1, getWidth(), topbarheight-1);
				
				for(int i=0; i<8; i++){
					
					g.setColor(new Color(110,110,110,(8-i)*40/8));
					g.drawLine(0, topbarheight+i, getWidth(), topbarheight+i);
					
					
				}
				
				// bottom bar
				if(paneldescription!=null){
					
					if(paneldescription.isVisible()){
						
						g.setColor(Color.white);
						g.fillRect(0,getHeight()-bottombarheight, getWidth(), bottombarheight);
						
						g.setColor(borderColor);
						g.drawLine(0, getHeight()-bottombarheight, getWidth(), getHeight()-bottombarheight);
						
						for(int i=0; i<8; i++){
							
							g.setColor(new Color(110,110,110,(8-i)*40/8));
							g.drawLine(0, getHeight()-bottombarheight-i, getWidth(), getHeight()-bottombarheight-i);
							
							
						}
						
						
					}
					
				}
				
				
				
			}
			
		};
		
		frmScheduleViewer.getContentPane().add(panelEverything, BorderLayout.CENTER);
		panelEverything.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,},
			new RowSpec[] {
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.PREF_ROWSPEC,}));
		
		panelOptions = new JPanel();
		panelOptions.setOpaque(false);
		panelEverything.add(panelOptions, "1, 1, fill, fill");
		panelOptions.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
		
		
		JLabel lblExample = new JLabel("Example 1");
		lblExample.setFont(new Font("Dialog", Font.BOLD, 16));
		panelOptions.add(lblExample);
		
		JLabel lblExample_1 = new JLabel("Example 2");
		lblExample_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblExample_1.setForeground(new Color(0,110,198,255));
		lblExample_1.setFont(new Font("Dialog", Font.BOLD, 16));
		panelOptions.add(lblExample_1);
		
		JLabel label = new JLabel("+");
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.setForeground(new Color(0,110,198,255));
		label.setFont(new Font("Dialog", Font.BOLD, 16));
		panelOptions.add(label);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(new Color(255,255,255,0),new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollPane.getVerticalScrollBar().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(new Color(255,255,255,0),new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollPane.getHorizontalScrollBar().setOpaque(false);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(11);
		panel.setOpaque(false);
		panelEverything.add(panel, "3, 1, fill, fill");
		
		lblleft = new JLabel("<");
		lblleft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(((JLabel)arg0.getSource()).isEnabled()){
					
					schedule.moveSchedule(scheduleindex, scheduleindex-1);
					initializeScheduleList();
					
					selectSchedule(scheduleindex-1);
					
				}
				
				
			}
		});
		lblleft.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblleft.setForeground(new Color(0,110,198,255));
		lblleft.setFont(new Font("Dialog", Font.BOLD, 14));
		lblleft.setVisible(false);
		panel.add(lblleft);
		
		lblright = new JLabel(">");
		lblright.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if(((JLabel)e.getSource()).isEnabled()){
					
					schedule.moveSchedule(scheduleindex, scheduleindex+1);
					initializeScheduleList();
					
					selectSchedule(scheduleindex+1);
					
				}
				
			}
		});
		lblright.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblright.setForeground(new Color(0,110,198,255));
		lblright.setFont(new Font("Dialog", Font.BOLD, 14));
		lblright.setVisible(false);
		panel.add(lblright);
		
		lblDuplicate = new JLabel(textdata.getKey("scheduleviewerduplicate"));
		panel.add(lblDuplicate);
		lblDuplicate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(((JLabel)arg0.getSource()).isEnabled()){
					
					schedule.duplicateSchedule(scheduleindex);
					initializeScheduleList();
					
					selectSchedule(scheduleindex+1);
					
				}
				
			}
		});
		
		lblDuplicate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblDuplicate.setForeground(new Color(0,110,198,255));
		lblDuplicate.setFont(new Font("Dialog", Font.BOLD, 14));
		lblDuplicate.setVisible(false);
		
		lblEdit = new JLabel(textdata.getKey("schedulevieweredit"));
		panel.add(lblEdit);
		lblEdit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(((JLabel)arg0.getSource()).isEnabled()){
					
					activateEditor(scheduleindex);
					
				}
				
			}
		});
		lblEdit.setVisible(false);
		lblEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblEdit.setForeground(new Color(0,110,198,255));
		lblEdit.setFont(new Font("Dialog", Font.BOLD, 14));
		
		lblExport = new JLabel(textdata.getKey("scheduleviewerexport"));
		lblExport.setVisible(false);
		lblExport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(((JLabel)arg0.getSource()).isEnabled()){
					
					// Export menu
					
					ExportScheduleGUI exportMenu=new ExportScheduleGUI(textdata);
					
					exportMenu.schedulename=titleLabels[scheduleindex].getText();
					exportMenu.username=username;
					
					exportMenu.setVisible(true);
					
					if(exportMenu.isAccepted()){
						
						// Accepted export
						
						boolean exportresult=false;
						
						if(exportMenu.cbwhattoexport.getSelectedIndex()==0 && "png,jpg,bmp".contains(((String)exportMenu.cbfiletype.getSelectedItem()).toLowerCase())){
							
							// Selected schedule
							
							exportresult=exportScheduleToImage(panelSchedule, exportMenu.txtdestination.getText(),
								(String)exportMenu.cbfiletype.getSelectedItem(), (double)(int)exportMenu.szoom.getValue()/100.0);

							
						} else if(exportMenu.cbwhattoexport.getSelectedIndex()==0 && "json,faicheck".contains(((String)exportMenu.cbfiletype.getSelectedItem()).toLowerCase())){

							// Selected schedule
							
							exportresult=schedule.exportOneSchedule(scheduleindex, exportMenu.txtdestination.getText());
							
						} else if(exportMenu.cbwhattoexport.getSelectedIndex()==1 && "json,faicheck".contains(((String)exportMenu.cbfiletype.getSelectedItem()).toLowerCase())){

							// All schedules
							
							exportresult=schedule.exportAllSchedules(exportMenu.txtdestination.getText());
							
						}
						
						if(exportresult){
							
							JOptionPane.showMessageDialog(frmScheduleViewer, textdata.getKey("exportsuccess",exportMenu.txtdestination.getText()), textdata.getKey("exportsuccesstitle"), JOptionPane.INFORMATION_MESSAGE);
							
						} else{
							
							JOptionPane.showMessageDialog(frmScheduleViewer, textdata.getKey("exporterror",exportMenu.txtdestination.getText()), textdata.getKey("exporterrortitle"), JOptionPane.ERROR_MESSAGE);
							
						}
						
					}
					
				}
				
			}
		});
		lblExport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblExport.setForeground(new Color(0,110,198,255));
		lblExport.setFont(new Font("Dialog", Font.BOLD, 14));
		panel.add(lblExport);
		
		lblDelete = new JLabel(textdata.getKey("scheduleviewerdelete"));
		panel.add(lblDelete);
		lblDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(((JLabel)arg0.getSource()).isEnabled()){
					
					if(JOptionPane.showConfirmDialog(frmScheduleViewer, textdata.getKey("scheduleviewerdeleteconfirm", titleLabels[scheduleindex].getText()), 
							textdata.getKey("scheduleviewerdeleteconfirmtitle"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){

						schedule.removeSchedule(scheduleindex);
						initializeScheduleList();
						
						selectSchedule(-1);
						
					}
					
				}
				
			}
		});
		lblDelete.setVisible(false);
		lblDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblDelete.setForeground(new Color(0,110,198,255));
		lblDelete.setFont(new Font("Dialog", Font.BOLD, 14));
		panelEverything.add(scrollPane, "1, 2, 3, 1, fill, fill");
		
		panelSchedule = new JPanel();
		scrollPane.setViewportView(panelSchedule);
		panelSchedule.setOpaque(false);
		panelSchedule.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("10dlu:grow"),
				FormFactory.PREF_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				RowSpec.decode("10dlu:grow"),}));
		
		JPanel panel_2 = new JCustomPanel();
		panelSchedule.add(panel_2, "3, 2, fill, fill");
		
		JLabel lblMonday = new JLabel("Monday");
		panel_2.add(lblMonday);
		
		JPanel panel_3 = new JCustomPanel();
		panelSchedule.add(panel_3, "4, 2, fill, fill");
		
		JLabel lblTuesday = new JLabel("Tuesday");
		panel_3.add(lblTuesday);
		
		JPanel panel_4 = new JCustomPanel();
		panelSchedule.add(panel_4, "5, 2, fill, fill");
		
		JLabel lblWednesday = new JLabel("Wednesday");
		panel_4.add(lblWednesday);
		
		JPanel panel_5 = new JCustomPanel();
		panelSchedule.add(panel_5, "2, 3, fill, fill");
		
		JLabel label_1 = new JLabel("9:00 - 9:30");
		panel_5.add(label_1);
		
		JPanel panel_6 = new JCustomPanel();
		panelSchedule.add(panel_6, "2, 4, fill, fill");
		
		JLabel label_2 = new JLabel("9:30 - 10:00");
		panel_6.add(label_2);
		
		JPanel panel_9 = new JCustomPanel(true);
		panel_9.setBackground(new Color(102, 153, 204));
		panelSchedule.add(panel_9, "4, 4, 1, 2, fill, fill");
		panel_9.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JLabel lblSubject = new JLabel("Subject");
		lblSubject.setHorizontalAlignment(SwingConstants.CENTER);
		panel_9.add(lblSubject, "2, 2");
		
		
		JLabel label_4 = new JLabel("9:30 - 10:30");
		label_4.setFont(new Font("Dialog", Font.ITALIC, 10));
		label_4.setHorizontalAlignment(SwingConstants.CENTER);
		panel_9.add(label_4, "2, 3");
		
		JPanel panel_7 = new JCustomPanel();
		panelSchedule.add(panel_7, "2, 5, fill, fill");
		
		JLabel label_3 = new JLabel("10:00 - 10:30");
		panel_7.add(label_3);
		
		paneldescription = new JPanel();
		paneldescription.setVisible(false);
		paneldescription.setOpaque(false);
		panelEverything.add(paneldescription, "1, 3, 3, 1, fill, fill");
		paneldescription.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("min(50dlu;default)"),
				FormFactory.UNRELATED_GAP_ROWSPEC,}));
		
		
		lbleventname = new JLabel("Event name");
		lbleventname.setFont(new Font("Dialog", Font.BOLD, 16));
		lbleventname.setForeground(new Color(33,33,33,255));
		paneldescription.add(lbleventname, "2, 2");
		
		lbleventhours = new JLabel("Event hours");
		lbleventhours.setFont(new Font("Dialog", Font.ITALIC, 16));
		lbleventhours.setForeground(new Color(117,117,117,255));
		paneldescription.add(lbleventhours, "4, 2");
		
		lblclose = new JLabel("X");
		lblclose.setFont(new Font("Dialog", Font.BOLD, 16));
		lblclose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getComponent().isEnabled()){
					
					paneldescription.setVisible(false);
					
				}
				
			}
		});
		lblclose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblclose.setForeground(new Color(0,110,198,255));
		paneldescription.add(lblclose, "6, 2");
		
		scrollDescription = new JScrollPane();
		scrollDescription.setBorder(null);
		scrollDescription.getVerticalScrollBar().setUI(new CustomScrollBarUI(new Color(255,255,255,0),new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollDescription.getVerticalScrollBar().setOpaque(false);
		scrollDescription.getHorizontalScrollBar().setUI(new CustomScrollBarUI(new Color(255,255,255,0),new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollDescription.getHorizontalScrollBar().setOpaque(false);
		scrollDescription.setOpaque(false);
		scrollDescription.getViewport().setOpaque(false);
		paneldescription.add(scrollDescription, "2, 4, 5, 1, fill, fill");
		
		txtdescription = new JTextPane();
		txtdescription.setFont(new Font("Dialog", Font.PLAIN, 15));
		txtdescription.setText("Event description");
		txtdescription.setOpaque(false);
		txtdescription.setEditable(false);
		scrollDescription.setViewportView(txtdescription);
	}
}
