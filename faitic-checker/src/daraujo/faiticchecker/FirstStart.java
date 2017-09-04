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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingConstants;

import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextPane;

import java.awt.Cursor;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JRadioButton;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class FirstStart extends JDialog {

	protected static Locale language;
	protected static TextData textdata=new TextData(new Locale("es"));
	
	private final static Image imgFaicheck=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaicheck.png")).getImage();

	private final static Image spanishFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/spain.png")).getImage();
	private final static Image galicianFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/galicia.png")).getImage();
	private final static Image englishFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/england.png")).getImage();
	private final static Image flagButton=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/flagButton.png")).getImage();
	
	private static JPanel pSpanish, pEnglish, pGalician;

	private static JCustomButton btnNext, btnCancel;
	private static JTextPane txtWelcomeDesc, txtAppdataDesc, txtRelativeDesc;
	private static JRadioButton rSaveAppdata, rSaveRelative;
	private static JLabel lblWelcome;
	
	private static ItemListener itemlistener=new ItemListener(){
		@Override
		public void itemStateChanged(ItemEvent e) {

			if(rSaveAppdata==null || rSaveRelative==null) return;
			
			rSaveAppdata.setForeground(rSaveAppdata.isSelected() ? new Color(0,110,198,255) : new Color(169,192,210,255));
			rSaveRelative.setForeground(rSaveRelative.isSelected() ? new Color(0,110,198,255) : new Color(169,192,210,255));
			
		}
	};
	
	private static String appdatapath, relativepath;
	
	private void updateWindowText(){
		
		btnNext.setText(textdata.getKey("nextbutton"));
		btnCancel.setText(textdata.getKey("cancelbutton"));
		txtWelcomeDesc.setText("<p style=\"text-align:justify; font-family: Dialog; font-size: 17pt; margin: 0px; color:#212121;\">" +
								textdata.getKey("welcometext") + "</p>");
		
		txtAppdataDesc.setText("<p style=\"text-align:justify; font-family: Dialog; font-size: 17pt; margin: 0px; color:#757575;\">" +
				textdata.getKey("appdatadesc", appdatapath) + "</p>");
		
		txtRelativeDesc.setText("<p style=\"text-align:justify; font-family: Dialog; font-size: 17pt; margin: 0px; color:#757575;\">" +
				textdata.getKey("relativedesc", relativepath) + "</p>");
		
		rSaveAppdata.setText(" " + textdata.getKey("appdatacheck"));
		rSaveRelative.setText(" " + textdata.getKey("relativecheck"));
		
		lblWelcome.setText(textdata.getKey("welcome"));
		
		setTitle(textdata.getKey("firststarttitle"));
		
	}
	
	private static void setLanguage(String lang){
		
		language=new Locale(lang);
		textdata=new TextData(language);
		
	}
	
	private static String getLanguage(){
		
		return language.getLanguage();
		
	}
	
	private static void updateLanguageMenu(){
		
		if(pSpanish == null || pEnglish == null || pGalician == null || language == null) return;
		
		String lang=getLanguage().toLowerCase();
		
		pSpanish.setEnabled(false);
		pEnglish.setEnabled(false);
		pGalician.setEnabled(false);
		
		switch(lang){
		
		case "es":
			pSpanish.setEnabled(true); break;
		case "gl":
			pGalician.setEnabled(true); break;
		case "en":
			pEnglish.setEnabled(true); break;
		default:
			break;
			
		}
		
	}
	
	private static void setpaths(){
		
		relativepath=ClassicRoutines.getUserDataPath(true, true);
		appdatapath=ClassicRoutines.getUserDataPath(true, false);
		
	}
	
	protected static void createsettingspath(boolean appdata){
		
		String path;
		
		if(appdata){
			path=appdatapath;
		} else{
			path=relativepath;
		}
		
		if(!new File(path).exists()){
			
			// It doesn't exist, let's create the folder
			
			ClassicRoutines.createNeededFolders(ClassicRoutines.cpath(path + "/"));
			
		}
		
	}
	
	private static void firstConfig(){
		
		// Once the path is configured
		
		Settings settings=new Settings("login.conf");
		
		if(settings != null) settings.jsonConf.put("Language", getLanguage());
		
		settings.saveSettings();

		
	}
	
	/**
	 * Create the dialog.
	 */
	public FirstStart() {
		setTitle("Faicheck - Bienvenido");
		setIconImage(Toolkit.getDefaultToolkit().getImage(FirstStart.class.getResource("/daraujo/faiticchecker/icon.png")));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				
				setpaths();
				
				setLanguage("es");
				updateLanguageMenu();
				updateWindowText();
				
			}
		});
		setModal(true);

		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(screenSize.getWidth()>900 ? (int)(screenSize.getWidth()-900)/2 : 0,screenSize.getHeight() > 700 ? (int)(screenSize.getHeight()-700)/2 : 0, 900, 700);
		
		JPanel panelEverything = new JPanel();
		panelEverything.setBackground(Color.WHITE);
			
		getContentPane().add(panelEverything, BorderLayout.CENTER);
		panelEverything.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("30dlu"),
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("70dlu"),
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("70dlu"),
				ColumnSpec.decode("30dlu"),},
			new RowSpec[] {
				RowSpec.decode("0px:grow(3)"),
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				RowSpec.decode("15dlu"),
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				RowSpec.decode("30dlu"),}));
		
		JPanel panelLogo = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
				Color borderColor=new Color(0,110,198,255);
				
				int imgwidth=380;
				int imgheight=imgwidth*imgFaicheck.getHeight(null)/imgFaicheck.getWidth(null);
				
				g2.drawImage(imgFaicheck, (super.getWidth()-imgwidth)/2, (super.getHeight()-imgheight)/2, imgwidth, imgheight, null);
				
			}
			
			
		};
		panelLogo.setBackground(Color.WHITE);
		panelEverything.add(panelLogo, "1, 1, 7, 1, fill, fill");
		panelLogo.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,}));
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.setOpaque(false);
		
		pSpanish = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, super.isEnabled() ? 1f : 0.3f));
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
			    g2.drawImage(flagButton, 0, 0, super.getWidth(), super.getHeight(), null);
			    
				g2.drawImage(spanishFlag, 8, 12, 24, 16, null);
				
			}
			
		};
		pSpanish.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				setLanguage("es");
				updateLanguageMenu();
				updateWindowText();
			}
		});
		pSpanish.setOpaque(false);
		pSpanish.setPreferredSize(new Dimension(40, 40));
		pSpanish.setMinimumSize(new Dimension(40, 40));
		panel.add(pSpanish);
		
		pGalician = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, super.isEnabled() ? 1f : 0.3f));
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    g2.drawImage(flagButton, 0, 0, super.getWidth(), super.getHeight(), null);
			    
				g2.drawImage(galicianFlag, 8, 12, 24, 16, null);
				
			}
			
		};
		pGalician.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				setLanguage("gl");
				updateLanguageMenu();
				updateWindowText();
				
			}
		});
		pGalician.setEnabled(false);
		pGalician.setPreferredSize(new Dimension(40, 40));
		pGalician.setOpaque(false);
		pGalician.setMinimumSize(new Dimension(40, 40));
		panel.add(pGalician);
		
		pEnglish = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, super.isEnabled() ? 1f : 0.3f));
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    g2.drawImage(flagButton, 0, 0, super.getWidth(), super.getHeight(), null);
			    
				g2.drawImage(englishFlag, 8, 12, 24, 16, null);
				
			}
			
		};
		pEnglish.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				setLanguage("en");
				updateLanguageMenu();
				updateWindowText();
				
			}
		});
		pEnglish.setEnabled(false);
		pEnglish.setPreferredSize(new Dimension(40, 40));
		pEnglish.setMinimumSize(new Dimension(40, 40));
		pEnglish.setOpaque(false);
		panel.add(pEnglish);
		
		panelLogo.add(panel, "2, 2, fill, fill");
		
		lblWelcome = new JLabel("¡Bienvenido!");
		lblWelcome.setFont(new Font("Dialog", Font.BOLD, 30));
		lblWelcome.setForeground(new Color(0,110,198,255));
		panelEverything.add(lblWelcome, "2, 3, 5, 1");
		
		txtWelcomeDesc = new JTextPane();
		txtWelcomeDesc.setBorder(null);
		txtWelcomeDesc.setContentType("text/html");
		txtWelcomeDesc.setFont(new Font("Dialog", Font.PLAIN, 17));
		txtWelcomeDesc.setEditable(false);
		txtWelcomeDesc.setText("<p style=\"text-align:justify; font-family: Dialog; font-size: 17pt; margin: 0px;\">Bienvenido a la aplicación NO OFICIAL de gestión de archivos de Faitic. Para comenzar, es necesario elegir el tipo de configuración. No se preocupe, le explicaremos cuál opción sería perfecta para usted.</p>");
		panelEverything.add(txtWelcomeDesc, "2, 5, 5, 1, fill, fill");
		
		JPanel panelAppdata = new JPanel();
		panelAppdata.setBackground(Color.WHITE);
		panelEverything.add(panelAppdata, "2, 7, 5, 1, fill, fill");
		panelAppdata.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("15dlu"),
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("15dlu"),},
			new RowSpec[] {
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,}));
		
		rSaveAppdata = new JRadioButton("Los ajustes se guardarán en este ordenador");
		rSaveAppdata.addItemListener(itemlistener);
		rSaveAppdata.setSelected(true);
		rSaveAppdata.setFont(new Font("Dialog", Font.BOLD, 22));
		rSaveAppdata.setBackground(Color.WHITE);
		rSaveAppdata.setForeground(new Color(0,110,198,255));
		rSaveAppdata.setIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxfalse.png")));
		rSaveAppdata.setSelectedIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxtrue.png")));
		panelAppdata.add(rSaveAppdata, "2, 2");
		
		txtAppdataDesc = new JTextPane();
		txtAppdataDesc.setEditable(false);
		txtAppdataDesc.setContentType("text/html");
		txtAppdataDesc.setFont(new Font("Dialog", Font.PLAIN, 17));
		txtAppdataDesc.setText("<p style=\"text-align:justify; font-family: Dialog; font-size: 17pt; margin: 0px;\">Su configuración se guardará en \"%0\". Útil si va a usar el programa sólo en este ordenador.</p>");
		panelAppdata.add(txtAppdataDesc, "2, 4, fill, fill");
		
		JPanel panelRelative = new JPanel();
		panelRelative.setBackground(Color.WHITE);
		panelEverything.add(panelRelative, "2, 9, 5, 1, fill, fill");
		panelRelative.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("15dlu"),
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("15dlu"),},
			new RowSpec[] {
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,}));
		
		rSaveRelative = new JRadioButton("Los ajustes se guardarán junto a la aplicación");
		rSaveRelative.addItemListener(itemlistener);
		rSaveRelative.setFont(new Font("Dialog", Font.BOLD, 22));
		rSaveRelative.setBackground(Color.WHITE);
		rSaveRelative.setForeground(new Color(169,192,210,255));
		rSaveRelative.setIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxfalse.png")));
		rSaveRelative.setSelectedIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxtrue.png")));
		panelRelative.add(rSaveRelative, "2, 2");
		
		txtRelativeDesc = new JTextPane();
		txtRelativeDesc.setEditable(false);
		txtRelativeDesc.setContentType("text/html");
		txtRelativeDesc.setFont(new Font("Dialog", Font.PLAIN, 17));
		txtRelativeDesc.setBackground(Color.WHITE);
		txtRelativeDesc.setText("<p style=\"text-align:justify; font-family: Dialog; font-size: 17pt; margin: 0px;\">Su configuración se guardará en \"%0\". Útil si va a usar el programa en un pendrive.</p>");
		panelRelative.add(txtRelativeDesc, "2, 4, fill, fill");
		
		ButtonGroup rgroup=new ButtonGroup();
		rgroup.add(rSaveAppdata);
		rgroup.add(rSaveRelative);
		
		btnCancel = new JCustomButton("Cancelar");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				dispose();
				
			}
		});
		panelEverything.add(btnCancel, "4, 11");
		
		btnNext = new JCustomButton("Siguiente >");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				createsettingspath(rSaveAppdata.isSelected());
				firstConfig();
				dispose();
				
			}
		});
		panelEverything.add(btnNext, "6, 11");

	}
}
