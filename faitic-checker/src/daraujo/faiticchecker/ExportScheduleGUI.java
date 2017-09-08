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

import javax.swing.JDialog;

import java.awt.Dialog.ModalityType;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExportScheduleGUI extends JDialog {
	
	protected static JTextField txtdestination;
	protected static JComboBox cbwhattoexport, cbfiletype;
	protected static JSpinner szoom;
	
	protected static String schedulename;
	protected static String username;
	
	private static JPanel panelOptions;

	private boolean iAccepted=false;
	
	private void accept(){ iAccepted=true; }
	protected boolean isAccepted(){ return iAccepted; }
	
	private static String correctName(String name){
		
		String acceptedchar="1234567890qwertyuiopasdfghjklzxcvbnm_.-() ";
		
		StringBuffer out=new StringBuffer();
		
		
		for(int i=0; i<name.length(); i++){
			
			if(acceptedchar.indexOf(name.toLowerCase().charAt(i))>=0)
				out.append(name.charAt(i));
			
		}
		
		return out.toString().replace(" ", "_");
		
	}
	
	/**
	 * Create the dialog.
	 */
	public ExportScheduleGUI() {

		setTitle("Export schedule");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 520, 324);
		
		JPanel panel=new JPanel(){
			
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
		
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("4dlu:grow"),
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;pref):grow(3)"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				ColumnSpec.decode("4dlu:grow"),},
			new RowSpec[] {
				RowSpec.decode("7dlu:grow"),
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				RowSpec.decode("9dlu:grow"),
				FormFactory.PREF_ROWSPEC,}));
		
		JLabel lblExportSchedule = new JLabel("Export schedule");
		lblExportSchedule.setFont(new Font("Dialog", Font.PLAIN, 25));
		panel.add(lblExportSchedule, "2, 2, 5, 1");
		
		JLabel lblWhatToExport = new JLabel("What to export:");
		panel.add(lblWhatToExport, "2, 4, right, default");
		
		cbwhattoexport = new JComboBox();
		cbwhattoexport.setEnabled(false);
		cbwhattoexport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				cbfiletype.setModel(new DefaultComboBoxModel(
						cbwhattoexport.getSelectedIndex()==0 ?
								new String[] {"png", "jpg", "bmp"} :
								new String[] {"png", "jpg", "bmp", "xml"}));
				
			}
		});

		cbwhattoexport.setModel(new DefaultComboBoxModel(new String[] {"Current schedule", "All schedules"}));
		panel.add(cbwhattoexport, "4, 4, fill, default");
		
		JLabel lblFileType = new JLabel("File type:");
		panel.add(lblFileType, "2, 6, right, default");
		
		cbfiletype = new JComboBox();
		cbfiletype.setModel(new DefaultComboBoxModel(new String[] {"png", "jpg", "bmp"}));
		panel.add(cbfiletype, "4, 6, fill, default");
		
		JLabel lblDestination = new JLabel("Destination:");
		panel.add(lblDestination, "2, 8, right, default");
		
		txtdestination = new JCustomTextField("",new Color(0,110,198,255));
		panel.add(txtdestination, "4, 8, fill, default");
		txtdestination.setColumns(10);
		
		JButton btnbrowse = new JCustomButton("...");
		btnbrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser savedialog=new JFileChooser();
				savedialog.setSelectedFile(new File("Export-" + correctName(schedulename) + (String)cbfiletype.getSelectedItem()));
				savedialog.setMultiSelectionEnabled(false);
				
				int dialogresult=savedialog.showSaveDialog(null);
				
				if(dialogresult==JFileChooser.APPROVE_OPTION){
					
					txtdestination.setText(savedialog.getSelectedFile().getAbsolutePath());
					
				}
				
			}
		});
		panel.add(btnbrowse, "6, 8");
		
		JLabel lblZoom = new JLabel("Resolution (%):");
		panel.add(lblZoom, "2, 10, right, fill");
		
		szoom = new JSpinner();
		szoom.setModel(new SpinnerNumberModel(100, 10, 1000, 10));
		panel.add(szoom, "4, 10");
		
		panelOptions = new JPanel();
		panelOptions.setOpaque(false);
		FlowLayout fl_panelOptions = (FlowLayout) panelOptions.getLayout();
		fl_panelOptions.setAlignment(FlowLayout.RIGHT);
		fl_panelOptions.setVgap(10);
		fl_panelOptions.setHgap(10);
		panel.add(panelOptions, "1, 12, 7, 1, fill, fill");
		
		JButton btnExport = new JCustomButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(txtdestination.getText().length()<=0) return;
				
				accept();
				setVisible(false);
				
			}
		});
		panelOptions.add(btnExport);
		
		JButton btnCancel = new JCustomButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				setVisible(false);
				
			}
		});
		panelOptions.add(btnCancel);
		
		getContentPane().add(panel);
		

	}
}
