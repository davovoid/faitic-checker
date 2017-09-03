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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
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
import java.util.ArrayList;

import javax.swing.JScrollPane;

public class ScheduleViewerGUI {

	protected static TextData textdata;
	
	protected static JFrame frmScheduleViewer;
	private static JPanel panelOptions, panelSchedule;
	
	protected static String username;
	
	private static Schedule schedule;
	
	private static int scheduleindex=-1;
	
	private static JLabel[] titleLabels;
	private static JLabel addScheduleLabel;
	
	private static JLabel lblEdit, lblDelete;

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
		
		if(index>=0){
			
			showSchedule(panelSchedule);
			
		} else{
			
			panelSchedule.removeAll();
			panelSchedule.updateUI();
			
		}
		
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
			columnspec[i]=FormFactory.BUTTON_COLSPEC;
		
		rowspec[0]=RowSpec.decode("10dlu:grow");
		rowspec[rowspec.length-1]=RowSpec.decode("10dlu:grow");
		
		for(int i=1; i<rowspec.length-1; i++)
			rowspec[i]=FormFactory.BUTTON_ROWSPEC;
		
		targetPanel.setLayout(new FormLayout(columnspec,rowspec));
		
		// Arranged layout. Now set the elements
		
		String[] dayofweek=textdata.getKey("daysofweek").split(",");
		
		for(int day=minday; day<=maxday; day++){
			
			JPanel panelDay = new JCustomPanel();
			targetPanel.add(panelDay, (day-minday+3) + ", 2, fill, fill");
			
			JLabel labelDay = new JLabel(dayofweek[day]);
			panelDay.add(labelDay);

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
				targetPanel.add(panelHour, "2, " + (i-elementspassed + 3) + ", 1, " + elementspassed + ", fill, fill");

				JLabel labelHour = new JLabel(output);
				panelHour.add(labelHour);

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
			
			JLabel lblSubject = new JLabel(event.getEventName());
			lblSubject.setHorizontalAlignment(SwingConstants.CENTER);
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
			
			
		}
		
		// And that's all!
		
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
				
				
				
			}
			
		};
		
		frmScheduleViewer.getContentPane().add(panelEverything, BorderLayout.CENTER);
		panelEverything.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
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
		
		lblEdit = new JLabel(textdata.getKey("schedulevieweredit"));
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
		panelEverything.add(lblEdit, "3, 1");
		
		lblDelete = new JLabel(textdata.getKey("scheduleviewerdelete"));
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
		panelEverything.add(lblDelete, "5, 1");
		panelEverything.add(scrollPane, "1, 2, 6, 1, fill, fill");
		
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
	}
}
