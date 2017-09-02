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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.Dialog.ModalityType;
import java.awt.Toolkit;
import java.awt.Color;

import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.border.TitledBorder;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;

import java.awt.Dimension;

import javax.swing.border.EtchedBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ScheduleEditorGUI extends JDialog {
	
	public static Schedule schedule;
	public static int scheduleIndex;
	public static String username;
	
	private static String[] schedulenames;
	
	private static JTextField txtschedulename, txtschedulepos, txteventname;
	private static JSpinner shstart,smstart,shend,smend;
	private static JList listevents;
	private static JComboBox cbday;
	private static JCustomButton btnaddevent, btnmodifyevent, btnDeleteEvent;
	private static JPanel panelOptions, pcolor;
	
	private static DefaultListModel eventListModel=new DefaultListModel();

	private static void listScheduleEvents(){
		
		int selectedindex=listevents.getSelectedIndex();
		
		eventListModel.clear();
		
		for(int i=0; i<schedule.eventList.size(); i++){
			
			eventListModel.addElement(schedule.eventList.get(i).getEventName());
			
		}
		
		if(selectedindex>=0 && selectedindex<eventListModel.getSize()){
			
			listevents.setSelectedIndex(selectedindex);
			
		}
		
	}
	
	private static void addNewEvent(){

		ScheduleEvent event=new ScheduleEvent(txteventname.getText(),
				(int)shstart.getValue()*60+(int)smstart.getValue(),
				(int)shend.getValue()*60+(int)smend.getValue(),
				cbday.getSelectedIndex(), pcolor.getBackground(), null);

		schedule.eventList.add(event);

		
	}
	
	private static void modifyEvent(int eventindex){

		if(eventindex<schedule.eventList.size() && eventindex>=0){

			ScheduleEvent event=schedule.eventList.get(eventindex);

			event.modify(txteventname.getText(),
					(int)shstart.getValue()*60+(int)smstart.getValue(),
					(int)shend.getValue()*60+(int)smend.getValue(),
					cbday.getSelectedIndex(), pcolor.getBackground(), null);

		}

	}
	
	private static void fillwithevent(int eventindex){
		
		if(eventindex<schedule.eventList.size() && eventindex>=0){
			
			ScheduleEvent event=schedule.eventList.get(eventindex);
			
			txteventname.setText(event.getEventName());
			
			shstart.setValue(event.getHour(event.getMinuteStart()));
			smstart.setValue(event.getMinute(event.getMinuteStart()));
			
			shend.setValue(event.getHour(event.getMinuteEnd()));
			smend.setValue(event.getMinute(event.getMinuteEnd()));
			
			cbday.setSelectedIndex(event.getDay());
			
			pcolor.setBackground(event.getColor());
			
		}
		
	}
	
	/**
	 * Create the dialog.
	 */
	public ScheduleEditorGUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				
				schedulenames=schedule.getScheduleNames();
				
				if(scheduleIndex<schedulenames.length){
					
					// Edit mode
					
					txtschedulename.setText(schedulenames[scheduleIndex]);
					
				} else{
					
					txtschedulename.setText("Untitled-" + (scheduleIndex+1));
					
				}
				
				txtschedulepos.setText("" + scheduleIndex);
				
				schedule.readEvents(scheduleIndex);

				listScheduleEvents();
				
				txteventname.requestFocus();
				
				setSize(getWidth()+1, getHeight()+1);
				setSize(getWidth()-1, getHeight()-1);

			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(ScheduleEditorGUI.class.getResource("/daraujo/faiticchecker/icon.png")));
		setTitle("Schedule editor");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(150, 200, 600, 472);
		
		JPanel panel = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
 
				// Variables
				
				int bottombarheight=panelOptions!=null ? panelOptions.getHeight() : 40;
				Color borderColor=new Color(110,110,110,180);
				
				// Background
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				
				for(int i=getHeight()-bottombarheight; i<super.getHeight(); i++){
					g.setColor(new Color(220,220,220, 0+(i-(getHeight()-bottombarheight))*150/bottombarheight ));
					g.drawLine(0, i, super.getWidth(), i);
				}
				
				// Bottom bar
				
				g.setColor(Color.white);
				g.fillRect(0,0,getWidth(),getHeight()-bottombarheight);
				
				g.setColor(borderColor);
				g.drawLine(0, getHeight()-bottombarheight-1, getWidth(), getHeight()-bottombarheight-1);
				
				for(int i=0; i<8; i++){
					
					g.setColor(new Color(110,110,110,(8-i)*40/8));
					g.drawLine(0, getHeight()-bottombarheight+i, getWidth(), getHeight()-bottombarheight+i);
					
					
				}
				
				
				
			}
			
		};
		
		panel.setBackground(Color.WHITE);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,}));
		
		JLabel lblScheduleEditor = new JLabel("Schedule editor");
		lblScheduleEditor.setFont(new Font("Dialog", Font.PLAIN, 25));
		panel.add(lblScheduleEditor, "2, 2, 5, 1");
		
		JLabel lblScheduleName = new JLabel("Schedule name:");
		panel.add(lblScheduleName, "2, 4, right, default");
		
		txtschedulename = new JTextField();
		panel.add(txtschedulename, "4, 4, fill, default");
		txtschedulename.setColumns(10);
		
		JLabel lblSchedulePosition = new JLabel("Schedule position:");
		panel.add(lblSchedulePosition, "2, 6, right, default");
		
		txtschedulepos = new JTextField();
		txtschedulepos.setEnabled(false);
		panel.add(txtschedulepos, "4, 6, fill, default");
		txtschedulepos.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Event editor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setOpaque(false);
		panel.add(panel_1, "2, 8, 3, 1, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,}));
		
		JLabel lblEventName = new JLabel("Event name:");
		panel_1.add(lblEventName, "2, 2, right, default");
		
		txteventname = new JTextField();
		panel_1.add(txteventname, "4, 2, 7, 1, fill, default");
		txteventname.setColumns(10);
		
		JLabel lblAssocSubject = new JLabel("Assoc. subject:");
		lblAssocSubject.setEnabled(false);
		panel_1.add(lblAssocSubject, "2, 4, right, default");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		panel_1.add(comboBox, "4, 4, 7, 1, fill, default");
		
		JLabel lblDay = new JLabel("Day:");
		panel_1.add(lblDay, "2, 6, right, default");
		
		cbday = new JComboBox();
		cbday.setModel(new DefaultComboBoxModel(new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}));
		panel_1.add(cbday, "4, 6, 7, 1, fill, default");
		
		JLabel lblStartsAt = new JLabel("Starts at:");
		panel_1.add(lblStartsAt, "2, 8");
		
		shstart = new JSpinner();
		shstart.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		shstart.setValue(9);
		panel_1.add(shstart, "4, 8");
		
		JLabel lblH = new JLabel("h");
		panel_1.add(lblH, "6, 8");
		
		smstart = new JSpinner();
		smstart.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		smstart.setValue(0);
		panel_1.add(smstart, "8, 8");
		
		JLabel lblMin = new JLabel("min");
		panel_1.add(lblMin, "10, 8");
		
		JLabel lblEndsAt = new JLabel("Ends at:");
		panel_1.add(lblEndsAt, "2, 10");
		
		shend = new JSpinner();
		shend.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		shend.setValue(10);
		panel_1.add(shend, "4, 10");
		
		JLabel lblH_1 = new JLabel("h");
		panel_1.add(lblH_1, "6, 10");
		
		smend = new JSpinner();
		smend.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		smend.setValue(0);
		panel_1.add(smend, "8, 10");
		
		JLabel lblMin_1 = new JLabel("min");
		panel_1.add(lblMin_1, "10, 10");
		
		JLabel lblEventColor = new JLabel("Event color:");
		panel_1.add(lblEventColor, "2, 12");
		
		pcolor = new JPanel();
		pcolor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				JPanel currentpanel=(JPanel)arg0.getSource();
				
				if(currentpanel.isEnabled()){
					
					JColorChooser colorchooser=new JColorChooser();
					Color result=colorchooser.showDialog(null, "Choose color...", currentpanel.getBackground());
					
					if(result!=null) currentpanel.setBackground(result);
					
				}
				
			}
		});
		pcolor.setBackground(new Color(255, 102, 51));
		pcolor.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		pcolor.setPreferredSize(new Dimension(10, 20));
		panel_1.add(pcolor, "4, 12, 7, 1, fill, fill");
		
		btnaddevent = new JCustomButton("Add as new event");
		btnaddevent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if((int)shstart.getValue()*60+(int)smstart.getValue()>=(int)shend.getValue()*60+(int)smend.getValue()){

					JOptionPane.showMessageDialog(null, "Error: start is equal or later than end.", "Error", JOptionPane.ERROR_MESSAGE);
					
				} else if(txteventname.getText().length()<=0){
					
					JOptionPane.showMessageDialog(null, "Error: no title for the event.", "Error", JOptionPane.ERROR_MESSAGE);
					
				}else {

					addNewEvent();
					listScheduleEvents();
					listevents.setSelectedIndex(eventListModel.getSize()-1);

				}
			}
		});
		panel_1.add(btnaddevent, "2, 14, 9, 1");
		
		btnmodifyevent = new JCustomButton("Modify selected event");
		btnmodifyevent.setEnabled(false);
		btnmodifyevent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(listevents.getSelectedIndex()>=0){

					if((int)shstart.getValue()*60+(int)smstart.getValue()>=(int)shend.getValue()*60+(int)smend.getValue()){

						JOptionPane.showMessageDialog(null, "Error: start is equal or later than end.", "Error", JOptionPane.ERROR_MESSAGE);
						
					} else if(txteventname.getText().length()<=0){
						
						JOptionPane.showMessageDialog(null, "Error: no title for the event.", "Error", JOptionPane.ERROR_MESSAGE);
						
					}else {

						modifyEvent(listevents.getSelectedIndex());
						listScheduleEvents();
					
					}
				}

			}
		});
		panel_1.add(btnmodifyevent, "2, 16, 9, 1");
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(null);
		panel_2.setOpaque(false);
		panel.add(panel_2, "6, 4, 1, 5, fill, fill");
		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,}));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.white,new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.white,new Color(110,110,110,255),new Color(110,110,110,50)));
		
		JLabel lblEventList = new JLabel("Event list");
		panel_2.add(lblEventList, "1, 1");
		panel_2.add(scrollPane, "1, 3, fill, fill");
		
		listevents = new JList();
		listevents.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				
				btnDeleteEvent.setEnabled(listevents.getSelectedIndex()>=0);
				btnmodifyevent.setEnabled(listevents.getSelectedIndex()>=0);
				
				fillwithevent(listevents.getSelectedIndex());
				
			}
		});
		listevents.setModel(eventListModel);
		scrollPane.setViewportView(listevents);
		
		btnDeleteEvent = new JCustomButton("Delete selected event");
		btnDeleteEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(listevents.getSelectedIndex()>=0){
					
					int selectedIndex=listevents.getSelectedIndex();
					
					schedule.eventList.remove(selectedIndex);

					listScheduleEvents();
					
					if(selectedIndex<eventListModel.getSize()){
						
						listevents.setSelectedIndex(selectedIndex);
						
					}
					
				}
				
			}
		});
		btnDeleteEvent.setEnabled(false);
		panel_2.add(btnDeleteEvent, "1, 5");
		
		panelOptions = new JPanel();
		FlowLayout fl_panelOptions = (FlowLayout) panelOptions.getLayout();
		fl_panelOptions.setAlignment(FlowLayout.RIGHT);
		fl_panelOptions.setVgap(8);
		fl_panelOptions.setHgap(16);
		panelOptions.setOpaque(false);
		panel.add(panelOptions, "1, 10, 7, 1, fill, fill");
		 
		JButton btnSave = new JCustomButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(txtschedulename.getText().length()<=0){

					JOptionPane.showMessageDialog(null, "Error: no title for the schedule.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
					
				}
				
				if(schedule.eventList.size()<=0){

					JOptionPane.showMessageDialog(null, "Error: no events for the schedule.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
					
				}
				
				schedule.saveSchedule(txtschedulename.getText(), scheduleIndex, scheduleIndex>=schedulenames.length); // True if out of bounds, so when new entry
				setVisible(false);
				
			}
		});
		panelOptions.add(btnSave);
		
		JButton btnCancel = new JCustomButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				setVisible(false);
				
			}
		});
		panelOptions.add(btnCancel);
		
	}
}
