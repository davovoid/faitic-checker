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
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

public class ScheduleViewerGUI {

	protected static JFrame frmScheduleViewer;
	private static JPanel panelOptions;
	
	protected static String username;

	/**
	 * Create the application.
	 */
	public ScheduleViewerGUI() {
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
				
				frmScheduleViewer.setSize(frmScheduleViewer.getWidth()+1, frmScheduleViewer.getHeight()+1);

				frmScheduleViewer.setSize(frmScheduleViewer.getWidth()-1, frmScheduleViewer.getHeight()-1);

				
			}
		});
		frmScheduleViewer.setIconImage(Toolkit.getDefaultToolkit().getImage(ScheduleViewerGUI.class.getResource("/daraujo/faiticchecker/icon.png")));
		frmScheduleViewer.setTitle("Schedule viewer");
		frmScheduleViewer.setBounds(100, 100, 800, 600);
		frmScheduleViewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panelEverything = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){

				// Variables
				
				int topbarheight=panelOptions!=null ? panelOptions.getHeight() : 40;
				Color borderColor=new Color(110,110,110,180);
				
				// Background
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				
				for(int i=0; i<super.getHeight(); i++){
					g.setColor(new Color(195,209,220, 0+i*210/super.getHeight() ));
					g.drawLine(0, i, super.getWidth(), i);
				}
				
				g.setColor(new Color(255,255,255,40));
				g.fillOval(-getWidth(), -getHeight(), getWidth()*2, getHeight()*2);
				
				// Top bar
				
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), topbarheight);
				
				g.setColor(borderColor);
				g.drawLine(0, topbarheight, getWidth(), topbarheight);
				
				for(int i=0; i<8; i++){
					
					g.setColor(new Color(110,110,110,(8-i)*40/8));
					g.drawLine(0, topbarheight+i, getWidth(), topbarheight+i);
					
					
				}
				
				
				
			}
			
		};
		
		frmScheduleViewer.getContentPane().add(panelEverything, BorderLayout.CENTER);
		panelEverything.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,},
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
		
		JPanel panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panelEverything.add(panel_1, "1, 2, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.PREF_ROWSPEC,}));
		
		JPanel panel_2 = new JCustomPanel();
		panel_1.add(panel_2, "3, 2, fill, fill");
		
		JLabel lblMonday = new JLabel("Monday");
		panel_2.add(lblMonday);
		
		JPanel panel_3 = new JCustomPanel();
		panel_1.add(panel_3, "4, 2, fill, fill");
		
		JLabel lblTuesday = new JLabel("Tuesday");
		panel_3.add(lblTuesday);
		
		JPanel panel_4 = new JCustomPanel();
		panel_1.add(panel_4, "5, 2, fill, fill");
		
		JLabel lblWednesday = new JLabel("Wednesday");
		panel_4.add(lblWednesday);
		
		JPanel panel_5 = new JCustomPanel();
		panel_1.add(panel_5, "2, 3, fill, fill");
		
		JLabel label_1 = new JLabel("9:00 - 9:30");
		panel_5.add(label_1);
		
		JPanel panel_6 = new JCustomPanel();
		panel_1.add(panel_6, "2, 4, fill, fill");
		
		JLabel label_2 = new JLabel("9:30 - 10:00");
		panel_6.add(label_2);
		
		JPanel panel_9 = new JCustomPanel();
		panel_9.setBackground(new Color(51, 102, 153));
		panel_1.add(panel_9, "4, 4, 1, 2, fill, fill");
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
		panel_1.add(panel_7, "2, 5, fill, fill");
		
		JLabel label_3 = new JLabel("10:00 - 10:30");
		panel_7.add(label_3);
	}
}
