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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;

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
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
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
import java.io.File;
import java.io.IOException;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;

public class ScheduleEditorGUI extends JDialog {
	
	protected static TextData textdata;
	
	public static Schedule schedule;
	public static int scheduleIndex;
	public static String username;
	
	private static String[] schedulenames;
	
	private static JEditorPane txteventname;
	private static JTextField txtschedulename, txtschedulepos;
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
		
		//if(selectedindex>=0 && selectedindex<eventListModel.getSize()){
			
		//	listevents.setSelectedIndex(selectedindex);
			
		//}
		
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
	public ScheduleEditorGUI(TextData td) {
		
		textdata=td;
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				
				schedulenames=schedule.getScheduleNames();
				
				if(scheduleIndex<schedulenames.length){
					
					// Edit mode
					
					txtschedulename.setText(schedulenames[scheduleIndex]);
					
				} else{
					
					txtschedulename.setText(textdata.getKey("untitledschedule",""+(scheduleIndex+1)));
					
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
		setTitle(textdata.getKey("scheduleeditortitle"));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(150, 150, 700, 500);
		
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
		
		JLabel lblScheduleEditor = new JLabel(textdata.getKey("scheduleeditortitle"));
		lblScheduleEditor.setFont(new Font("Dialog", Font.PLAIN, 25));
		panel.add(lblScheduleEditor, "2, 2, 5, 1");
		
		JLabel lblScheduleName = new JLabel(textdata.getKey("editorschedulename"));
		panel.add(lblScheduleName, "2, 4, right, default");
		
		txtschedulename = new JCustomTextField("",new Color(0,110,198,255));
		panel.add(txtschedulename, "4, 4, fill, default");
		txtschedulename.setColumns(10);
		
		JLabel lblSchedulePosition = new JLabel(textdata.getKey("editorscheduleposition"));
		panel.add(lblSchedulePosition, "2, 6, right, default");
		
		txtschedulepos = new JCustomTextField("",new Color(180,180,180,255));
		txtschedulepos.setEnabled(false);
		panel.add(txtschedulepos, "4, 6, fill, default");
		txtschedulepos.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, textdata.getKey("eventeditortitle"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		
		JLabel lblEventName = new JLabel(textdata.getKey("editoreventname"));
		panel_1.add(lblEventName, "2, 2, right, default");
		
		txteventname = new JCustomEditorPane(new Color(0,110,198,255));
		panel_1.add(txteventname, "4, 2, 7, 1, fill, default");
		//txteventname.setColumns(10);
		
		JLabel lblAssocSubject = new JLabel(textdata.getKey("editorassocsubject"));
		lblAssocSubject.setEnabled(false);
		panel_1.add(lblAssocSubject, "2, 4, right, default");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		panel_1.add(comboBox, "4, 4, 7, 1, fill, default");
		
		JLabel lblDay = new JLabel(textdata.getKey("editorday"));
		panel_1.add(lblDay, "2, 6, right, default");
		
		cbday = new JComboBox();
		cbday.setModel(new DefaultComboBoxModel(textdata.getKey("daysofweek").split(",")));
		panel_1.add(cbday, "4, 6, 7, 1, fill, default");
		
		JLabel lblStartsAt = new JLabel(textdata.getKey("editorstartsat"));
		panel_1.add(lblStartsAt, "2, 8, right, default");
		
		shstart = new JSpinner();
		shstart.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		shstart.setValue(9);
		panel_1.add(shstart, "4, 8");
		
		JLabel lblH = new JLabel(textdata.getKey("editorshorthour"));
		panel_1.add(lblH, "6, 8");
		
		smstart = new JSpinner();
		smstart.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		smstart.setValue(0);
		panel_1.add(smstart, "8, 8");
		
		JLabel lblMin = new JLabel(textdata.getKey("editorshortminute"));
		panel_1.add(lblMin, "10, 8");
		
		JLabel lblEndsAt = new JLabel(textdata.getKey("editorendsat"));
		panel_1.add(lblEndsAt, "2, 10, right, default");
		
		shend = new JSpinner();
		shend.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		shend.setValue(10);
		panel_1.add(shend, "4, 10");
		
		JLabel lblH_1 = new JLabel(textdata.getKey("editorshorthour"));
		panel_1.add(lblH_1, "6, 10");
		
		smend = new JSpinner();
		smend.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		smend.setValue(0);
		panel_1.add(smend, "8, 10");
		
		JLabel lblMin_1 = new JLabel(textdata.getKey("editorshortminute"));
		panel_1.add(lblMin_1, "10, 10");
		
		JLabel lblEventColor = new JLabel(textdata.getKey("editoreventcolor"));
		panel_1.add(lblEventColor, "2, 12, right, default");
		
		pcolor = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){

				Graphics2D g2=(Graphics2D) g;
				
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    g2.setColor(new Color(0,110,198,255));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
				
			    g2.setColor(Color.white);
				g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 4, 4);
				
			    g2.setColor(getBackground());
				g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 2, 2);
				
			}
			
		};
		pcolor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				JPanel currentpanel=(JPanel)arg0.getSource();
				
				if(currentpanel.isEnabled()){
					
					JColorChooser colorchooser=new JColorChooser();
					Color result=colorchooser.showDialog(null, textdata.getKey("editorcolorpickertitle"), currentpanel.getBackground());
					
					if(result!=null) currentpanel.setBackground(result);
					
				}
				
			}
		});
		pcolor.setBackground(new Color(153, 204, 255));
		pcolor.setPreferredSize(new Dimension(10, 20));
		panel_1.add(pcolor, "4, 12, 7, 1, fill, fill");
		
		btnaddevent = new JCustomButton(textdata.getKey("editoraddasnewevent"));
		btnaddevent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if((int)shstart.getValue()*60+(int)smstart.getValue()>=(int)shend.getValue()*60+(int)smend.getValue()){

					JOptionPane.showMessageDialog(null, textdata.getKey("editorerrorendbeforestart"), textdata.getKey("editorerrortitle"), JOptionPane.ERROR_MESSAGE);
					
				} else if(txteventname.getText().length()<=0){
					
					JOptionPane.showMessageDialog(null, textdata.getKey("editorerrornotitleevent"), textdata.getKey("editorerrortitle"), JOptionPane.ERROR_MESSAGE);
					
				}else {

					addNewEvent();
					listScheduleEvents();
					//listevents.setSelectedIndex(-1);

				}
			}
		});
		panel_1.add(btnaddevent, "2, 14, 9, 1");
		
		btnmodifyevent = new JCustomButton(textdata.getKey("editormodifyevent"));
		btnmodifyevent.setEnabled(false);
		btnmodifyevent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(listevents.getSelectedIndex()>=0){

					if((int)shstart.getValue()*60+(int)smstart.getValue()>=(int)shend.getValue()*60+(int)smend.getValue()){

						JOptionPane.showMessageDialog(null, textdata.getKey("editorerrorendbeforestart"), textdata.getKey("editorerrortitle"), JOptionPane.ERROR_MESSAGE);
						
					} else if(txteventname.getText().length()<=0){
						
						JOptionPane.showMessageDialog(null, textdata.getKey("editorerrornotitleevent"), textdata.getKey("editorerrortitle"), JOptionPane.ERROR_MESSAGE);
						
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
		
		JLabel lblEventList = new JLabel(textdata.getKey("editoreventlist"));
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
		
		btnDeleteEvent = new JCustomButton(textdata.getKey("editordeleteevent"));
		btnDeleteEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(listevents.getSelectedIndex()>=0){
					
					int selectedIndex=listevents.getSelectedIndex();
					
					schedule.eventList.remove(selectedIndex);

					listScheduleEvents();
					
					//if(selectedIndex<eventListModel.getSize()){
						
					//	listevents.setSelectedIndex(selectedIndex);
						
					//}
					
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
		 
		JButton btnSave = new JCustomButton(textdata.getKey("editorbtnsave"));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(txtschedulename.getText().length()<=0){

					JOptionPane.showMessageDialog(null, textdata.getKey("editorerrornotitleschedule"), textdata.getKey("editorerrortitle"), JOptionPane.ERROR_MESSAGE);
					return;
					
				}
				
				if(schedule.eventList.size()<=0){

					JOptionPane.showMessageDialog(null, textdata.getKey("editorerrornoeventsschedule"), textdata.getKey("editorerrortitle"), JOptionPane.ERROR_MESSAGE);
					return;
					
				}
				
				schedule.saveSchedule(txtschedulename.getText(), scheduleIndex, scheduleIndex>=schedulenames.length); // True if out of bounds, so when new entry
				setVisible(false);
				
			}
		});
		
		JButton btnImportFromFile = new JCustomButton(textdata.getKey("editorimportfile"));
		btnImportFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser openfilemenu=new JFileChooser();
				
				openfilemenu.setDialogTitle(textdata.getKey("editorimportchoosertitle"));
				openfilemenu.setMultiSelectionEnabled(false);
				
				openfilemenu.setFileFilter(new FileFilter(){

					@Override
					public boolean accept(File arg0) {

						String path=arg0.getAbsolutePath();
						
						if(path.toLowerCase().lastIndexOf(".json") == path.length()-5){
							
							return true;
							
						}
						
						if(arg0.isDirectory()) return true;
						
						return false;
					}

					@Override
					public String getDescription() {
						
						return textdata.getKey("filefilter","JSON");
					}
					
				});
				
				
				int dialogresult=openfilemenu.showOpenDialog(null);
				
				if(dialogresult==JFileChooser.APPROVE_OPTION){
					
					try {
						
						schedule.importFromFile(openfilemenu.getSelectedFile().getCanonicalPath());
						setVisible(false);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		});
		panelOptions.add(btnImportFromFile);
		panelOptions.add(btnSave);
		
		JButton btnCancel = new JCustomButton(textdata.getKey("editorbtncancel"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				setVisible(false);
				
			}
		});
		panelOptions.add(btnCancel);
		
	}
}
