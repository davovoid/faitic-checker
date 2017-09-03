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
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.SystemColor;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class About {

	protected static final String VERSION="1.0.0-testing";
	private static final String WEBPAGE="https://davovoid.github.io/faicheck/";

	protected static TextData textdata;
	
	private final static Image imgIcon=new ImageIcon(About.class.getResource("/daraujo/faiticchecker/icon.png")).getImage();

	private final static Image imgFaicheck=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaicheck.png")).getImage();
	
	protected static JFrame frameAbout;
	
	protected static JPanel panelHeader, panelFooter;
	private JLabel lblFaiticChecker;
	private JLabel lblProgramaNoOficial;
	private JLabel lblCreadoPorDavid;
	private JScrollPane scrollPane;
	private static JTextPane txtAbout;
	private static JLabel lblLicensesAndAttributions;
	private static JLabel lblContributors;
	private static JLabel lblFaicheckLicense;
	private JLabel labelWebPage;

	/**
	 * Create the application.
	 */
	public About(TextData td) {
		textdata=td;
		initialize();
	}
	
	private static void loadTextFile(String filename){

		// About textpane
		
		InputStream reader=About.class.getResourceAsStream("/daraujo/faiticchecker/" + filename);

		StringBuffer aboutText=new StringBuffer();
		
		try {

			byte[] temp=new byte[1024];
			int templen=reader.read(temp);
			
			while(templen>=0){
				
				aboutText.append(new String(temp,0,templen,StandardCharsets.UTF_8));
				templen=reader.read(temp);
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		txtAbout.setText(aboutText.toString());
		
		// Keep scrollPane up
		
		txtAbout.setCaretPosition(0);

	}
	
	private static void selectOption(JLabel selectedLabel, String filename){
		
		JLabel[] labels=new JLabel[]{lblLicensesAndAttributions, lblFaicheckLicense, lblContributors};
		
		for(JLabel label : labels){
			
			if(label.equals(selectedLabel)){
				
				// Selected label
				
				label.setForeground(SystemColor.controlText);
				
			} else{
				
				// Not selected label
				
				label.setForeground(new Color(0,110,198,255));
				
			}
			
		}
		
		loadTextFile(filename);
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameAbout = new JFrame();
		frameAbout.setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/daraujo/faiticchecker/icon.png")));
		frameAbout.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				
				frameAbout.setSize(frameAbout.getWidth()+1, frameAbout.getHeight()+1);

				frameAbout.setSize(frameAbout.getWidth()-1, frameAbout.getHeight()-1);

				// Version
				
				lblFaiticChecker.setText(lblFaiticChecker.getText() + " v." + VERSION);
				
				// Open option
				selectOption(lblLicensesAndAttributions,"about.txt");
				
			}
		});
		frameAbout.getContentPane().setBackground(Color.WHITE);
		frameAbout.setTitle(textdata.getKey("aboutframetitle"));
		frameAbout.setBounds(50, 50, 670, 550);
		frameAbout.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panelWithEverything = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

			    //g2.setComposite(AlphaComposite.Src);
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
				Color borderColor=new Color(0,110,198,255);
				
				int topHeight=panelHeader!=null ? panelHeader.getHeight() : 150;
				int bottomHeight=panelFooter!=null ? panelFooter.getHeight() : 50;
				
				int leftIcon=140;
				
			}
			
		};
		panelWithEverything.setOpaque(false);
		
		frameAbout.getContentPane().add(panelWithEverything, BorderLayout.CENTER);
		panelWithEverything.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("120px"),
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				RowSpec.decode("40px"),}));
		
		panelHeader = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){

				// Inherited
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

			    //g2.setComposite(AlphaComposite.Src);
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
			    int iconSide=120;
			    
			    g2.drawImage(imgIcon, 15, (super.getHeight()-iconSide)/2, iconSide, iconSide, null);
			    
			}
			
		};
		panelHeader.setOpaque(false);
		panelWithEverything.add(panelHeader, "1, 1, 3, 1, fill, fill");
		panelHeader.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("150px"),
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		lblFaiticChecker = new JLabel("Faicheck");
		lblFaiticChecker.setForeground(new Color(0,110,198,255));
		//lblFaiticChecker.setHorizontalAlignment(SwingConstants.CENTER);
		lblFaiticChecker.setFont(new Font("SansSerif", Font.BOLD, 20));
		panelHeader.add(lblFaiticChecker, "2, 2");
		
		lblProgramaNoOficial = new JLabel(textdata.getKey("appbriefdescription"));
		lblProgramaNoOficial.setForeground(Color.DARK_GRAY);
		panelHeader.add(lblProgramaNoOficial, "2, 4");
		
		lblCreadoPorDavid = new JLabel("©2016, 2017 David Ricardo Araújo Piñeiro");
		lblCreadoPorDavid.setForeground(Color.GRAY);
		panelHeader.add(lblCreadoPorDavid, "2, 6");
		
		labelWebPage = new JLabel(WEBPAGE);
		labelWebPage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				try {
					Desktop.getDesktop().browse(new URI(WEBPAGE));
				
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
			}
		});
		labelWebPage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelWebPage.setForeground(new Color(0,110,198,255));
		panelHeader.add(labelWebPage, "2, 8");
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.white,new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.white,new Color(110,110,110,255),new Color(110,110,110,50)));
		panelWithEverything.add(scrollPane, "2, 3, fill, fill");
		
		txtAbout = new JTextPane(){
			
			@Override
			public void paintComponent(Graphics g){
				
				g.setColor(new Color(255,255,255,200));
				g.fillRect(0, 0, super.getWidth(), super.getHeight());
				
				super.paintComponent(g);
				
			}
			
		};
		txtAbout.setBackground(Color.WHITE);
		txtAbout.setBorder(null);
		txtAbout.setFont(new Font("Monospaced", Font.PLAIN, 14));
		txtAbout.setEditable(false);
		//txtAbout.setBackground(new Color(255,255,255,200));
		scrollPane.setViewportView(txtAbout);
		
		panelFooter = new JPanel();
		panelFooter.setOpaque(false);
		panelWithEverything.add(panelFooter, "1, 5, 3, 1, fill, fill");
		panelFooter.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		lblLicensesAndAttributions = new JLabel(textdata.getKey("licensesandattributions"));
		lblLicensesAndAttributions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				selectOption(lblLicensesAndAttributions,"about.txt");
			}
		});
		lblLicensesAndAttributions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblLicensesAndAttributions.setForeground(new Color(0,110,198,255));
		panelFooter.add(lblLicensesAndAttributions, "2, 2");
		
		lblFaicheckLicense = new JLabel(textdata.getKey("programlicense"));
		lblFaicheckLicense.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectOption(lblFaicheckLicense,"COPYING");
				//txtAbout.setText(txtAbout.getText().replace("\\", "\\\\").replace("\n\n", "\\n\\n").replace("\n", " ").replace("\\n\\n", "\n\n").replace("\\\\", "\\"));
			}
		});
		lblFaicheckLicense.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblFaicheckLicense.setForeground(new Color(0,110,198,255));
		panelFooter.add(lblFaicheckLicense, "4, 2");
		
		lblContributors = new JLabel(textdata.getKey("contributors"));
		lblContributors.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectOption(lblContributors,"contributors.txt");
			}
		});
		lblContributors.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblContributors.setForeground(new Color(0,110,198,255));
		panelFooter.add(lblContributors, "6, 2");
		
	}
}
