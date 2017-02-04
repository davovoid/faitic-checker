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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.awt.Cursor;
import java.awt.Toolkit;

import javax.swing.JCheckBox;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.FlowLayout;

public class LoginGUI {

	protected static boolean verbose;
	
	protected static Locale language;
	protected static TextData textdata=new TextData(new Locale("es"));
	
	private static JPanel panelLogin;
	private static JPanel pSpanish, pEnglish, pGalician;

	protected static Faitic faitic;
	protected static String mainDocument;
	protected static Settings settings;
	
	protected static JFrame loginFrame;
	
	//private final static Image imgUVigo=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoUVigo.png")).getImage();
	//private final static Image imgFaitic=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaitic.png")).getImage();
	
	private final static Image imgFaicheck=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaicheck.png")).getImage();
	
	private final static Image spanishFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/spain.png")).getImage();
	private final static Image galicianFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/galicia.png")).getImage();
	private final static Image englishFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/england.png")).getImage();
	private final static Image flagButton=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/flagButton.png")).getImage();
	
	private static JTextField txtUsuario;
	private static JPasswordField pwdPassword;
	
	private static JPanel panelStatus;
	private static JLabel lblLoginStatus;
	
	private static JCheckBox cRememberUsename, cRememberPassword;
	
	private static String username;

	private static ActionListener enterPressed = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			fcnLogin();
		}
	};
	private JPanel panel_1;
	private JLabel lblConfigurationFolder;
	
	private static void setLanguage(String lang){
		
		language=new Locale(lang);
		textdata=new TextData(language);
		
		if(settings != null) settings.jsonConf.put("Language", lang);

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
	
	private static void setLanguageWarning(){
		
		panelStatus.setBackground(Color.LIGHT_GRAY);
		lblLoginStatus.setText(textdata.getKey("selectlanguagewarning"));
		panelStatus.setVisible(true);
		
	}
	
	private static void fcnLogin(){

		try {
			
			username=txtUsuario.getText();
			
			mainDocument=faitic.faiticLogin(username,String.valueOf(pwdPassword.getPassword()));

			if(mainDocument!=null){
				
				// Success
				
				panelStatus.setBackground(Color.green.darker());
				lblLoginStatus.setText(textdata.getKey("loginsuccessful"));
				
				// Save user name and password, if required. Password will be encoded
				if(settings.jsonConf.containsKey("Username")) settings.jsonConf.remove("Username");
				if(settings.jsonConf.containsKey("EncodedPassword")) settings.jsonConf.remove("EncodedPassword");
				
				if(cRememberUsename.isSelected()){
					
					settings.jsonConf.put("Username", username);
					
					if(cRememberPassword.isSelected()){
						
						try{
							
							settings.jsonConf.put("EncodedPassword", Encrypter.encrpytAES(String.valueOf(pwdPassword.getPassword()),username));
							
						} catch(Exception ex){
							
							ex.printStackTrace();
							
						}
						
					}
				
				}
				
				// Reset password textbox
				pwdPassword.setText("");
				
				
				// Open subjects menu
						try {
							
							SubjectsGUI window = new SubjectsGUI(textdata);
							
							window.mainDocument=mainDocument;
							window.faitic=faitic;

							// End with settings and create it for the subjects GUI
							
							settings.saveSettings();
							
							//loginFrame.setVisible(false);
							
							loginFrame.dispose();
							
							window.settings=new Settings("user-" + username + ".conf");
							
							window.subjectsFrame.setVisible(true);
							
							
						} catch (Exception ex) {
							ex.printStackTrace();
						}

			} else{
				
				// Unsuccessful
				

				panelStatus.setBackground(Color.RED);
				lblLoginStatus.setText(textdata.getKey("loginunsuccessful"));
				
				
			}
			
		} catch (Exception e1) {

			e1.printStackTrace();
			

			panelStatus.setBackground(Color.RED);
			lblLoginStatus.setText(textdata.getKey("loginerror"));
			
			
		}
		
		panelStatus.setVisible(true);
		
	}
	
	
	/**
	 * Create the application.
	 */
	public LoginGUI(boolean vverbose) {
		verbose=vverbose;
		
		// Faitic and settings load
		faitic=new Faitic(verbose);
		settings=new Settings("login.conf");
		
		// Language load
		if(settings.jsonConf.containsKey("Language")) setLanguage((String)settings.jsonConf.get("Language"));
		else setLanguage("es");
		
		// Interface load
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		loginFrame = new JFrame();
		loginFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(LoginGUI.class.getResource("/daraujo/faiticchecker/icon.png")));
		loginFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {

				settings.saveSettings();
				
				loginFrame.dispose();
				
			}
			@Override
			public void windowOpened(WindowEvent arg0) {
				
				// Warn about the verbose option
				
				if(verbose){

					int questionResult = JOptionPane.showConfirmDialog(loginFrame, textdata.getKey("verboseactive"),
							textdata.getKey("verboseactivetitle"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

					if(questionResult == JOptionPane.CANCEL_OPTION){
						
						loginFrame.dispose();
						
					}

				}
				
				// Load username if available
				
				if(settings.jsonConf.containsKey("Username")){
					
					username=(String) settings.jsonConf.get("Username");
					
					txtUsuario.setText(username);
					cRememberUsename.setSelected(true);
					
					if(settings.jsonConf.containsKey("EncodedPassword")){
						
						try {
							
							// Password remembered, user too
							
							pwdPassword.setText(Encrypter.decryptAES((String)settings.jsonConf.get("EncodedPassword"), username));
							
							cRememberPassword.setSelected(true);
							pwdPassword.requestFocus();
							
							
						} catch (Exception e) {

							// Error loading password
							
							cRememberPassword.setSelected(false);
							pwdPassword.requestFocus();
							
							e.printStackTrace();
							
						}
						
					}else{

						// Only user remembered
						
						cRememberPassword.setSelected(false);
						pwdPassword.requestFocus();
						
					}
					
				} else{
					
					// No user nor password remembered
					
					cRememberUsename.setSelected(false);
					cRememberPassword.setSelected(false);
					
					txtUsuario.requestFocus();
					
				}
				
				// Update language menu
				updateLanguageMenu();
				
				System.out.println(textdata.getKey("language"));
				
				// Show config folder
				lblConfigurationFolder.setText(textdata.getKey("configfolder", ClassicRoutines.getUserDataPath(true)));
				
			}
		});
		loginFrame.getContentPane().setBackground(Color.WHITE);
		loginFrame.setTitle(textdata.getKey("loginframetitle"));

		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		loginFrame.setBounds(screenSize.getWidth()>900 ? (int)(screenSize.getWidth()-900)/2 : 0,screenSize.getHeight() > 700 ? (int)(screenSize.getHeight()-700)/2 : 0, 900, 700);
		
		JPanel panelWithEverything = new JPanel(){
			

			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

			    //g2.setComposite(AlphaComposite.Src);
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
				//Color backgroundColor=new Color(119,177,224,100);
				//Color backgroundColor2=new Color(129,167,204,100);
				
				Color borderColor=new Color(0,110,198,255);
				
				int imgwidth=380;
				int imgheight=imgwidth*imgFaicheck.getHeight(null)/imgFaicheck.getWidth(null);
				
				int loginposy=panelLogin != null ? panelLogin.getY() : 300;
				
				g2.drawImage(imgFaicheck, (super.getWidth()-imgwidth)/2, (loginposy-imgheight)/2, imgwidth, imgheight, null);
				
			}
			
		};
		panelWithEverything.setOpaque(false);
		panelWithEverything.setBackground(Color.WHITE);
		loginFrame.getContentPane().add(panelWithEverything, BorderLayout.CENTER);
		panelWithEverything.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("default:grow(5)"),
				FormFactory.MIN_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				RowSpec.decode("default:grow(3)"),
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,}));
		
		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panelWithEverything.add(panel_1, "3, 1, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		JPanel panel = new JPanel();
		panel_1.add(panel, "2, 2");
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
				setLanguageWarning();
				
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
				setLanguageWarning();

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
				setLanguageWarning();
				
			}
		});
		pEnglish.setEnabled(false);
		pEnglish.setPreferredSize(new Dimension(40, 40));
		pEnglish.setMinimumSize(new Dimension(40, 40));
		pEnglish.setOpaque(false);
		panel.add(pEnglish);
		
		
		panelLogin = new JPanel(){

			@Override
			public void paintComponent(Graphics g){

				Color borderColor=new Color(140,140,140,255);
				
				for(int i=0; i<8; i++){
					
					g.setColor(new Color(200,200,200,200*(i+1)/8));
					
					if(i%3==0){
						g.drawLine(i/3, i/3, super.getWidth()-i, i/3);	// top
						g.drawLine(i/3, i/3+1, i/3, super.getHeight()-i-1);	// left
					}

					g.drawLine(super.getWidth()-i, i/3+1, super.getWidth()-i, super.getHeight()-i-1);	// Right
					g.drawLine(i/3, super.getHeight()-i, super.getWidth()-i, super.getHeight()-i);	// Bottom
					
				}

				g.setColor(borderColor);
				g.drawRect(3, 3, super.getWidth()-11, super.getHeight()-11);
				g.drawRect(3, 3, super.getWidth()-12, super.getHeight()-12);
				
			}

		};
		panelLogin.setOpaque(false);
		panelLogin.setMinimumSize(new Dimension(460, 280));
		panelWithEverything.add(panelLogin, "2, 2, fill, fill");
		panelLogin.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("48px"),
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("56px"),},
			new RowSpec[] {
				RowSpec.decode("default:grow"),
				FormFactory.MIN_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblUsuario = new JLabel(textdata.getKey("lblusername"));
		panelLogin.add(lblUsuario, "2, 2, right, default");
		
		txtUsuario = new JTextField();
		txtUsuario.addActionListener(enterPressed);
		txtUsuario.setMinimumSize(new Dimension(4, 25));
		panelLogin.add(txtUsuario, "4, 2, fill, default");
		txtUsuario.setColumns(10);
		
		JLabel lblContrasea = new JLabel(textdata.getKey("lblpassword"));
		panelLogin.add(lblContrasea, "2, 4, right, default");
		
		pwdPassword = new JPasswordField();
		pwdPassword.addActionListener(enterPressed);
		pwdPassword.setMinimumSize(new Dimension(4, 25));
		panelLogin.add(pwdPassword, "4, 4, fill, default");
		
		cRememberUsename = new JCheckBox(textdata.getKey("rememberusername"));
		cRememberUsename.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				
				cRememberPassword.setEnabled(arg0.getStateChange()==arg0.SELECTED);
				
			}
		});
		cRememberUsename.setOpaque(false);
		panelLogin.add(cRememberUsename, "4, 6");
		
		cRememberPassword = new JCheckBox(textdata.getKey("rememberpassword"));
		cRememberPassword.setEnabled(false);
		cRememberPassword.setOpaque(false);
		panelLogin.add(cRememberPassword, "4, 8");
		
		JPanel panel_2 = new JPanel();
		panel_2.setOpaque(false);
		panelLogin.add(panel_2, "2, 10, 3, 1, fill, fill");
		
		JButton btnLogin = new JButton(textdata.getKey("btnlogin"));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				fcnLogin();
				
			}
		});
		panel_2.add(btnLogin);
		
		panelStatus = new JPanel(){

			@Override
			public void paintComponent(Graphics g){

				//super.paintComponent(g);
				
				Color borderColor=super.getBackground();
				

				for(int i=0; i<8; i++){
					
					g.setColor(new Color(200,200,200,200*(i+1)/8));
					
					if(i%3==0){
						g.drawLine(i/3, 7+i/3, super.getWidth()-i, 7+i/3);	// top
						g.drawLine(i/3, 7+i/3+1, i/3, super.getHeight()-i-1);	// left
					}

					g.drawLine(super.getWidth()-i, 7+i/3+1, super.getWidth()-i, super.getHeight()-i-1);	// Right
					g.drawLine(i/3, super.getHeight()-i, super.getWidth()-i, super.getHeight()-i);	// Bottom
					
				}

				g.setColor(borderColor);
				g.drawRect(3, 10, super.getWidth()-11, super.getHeight()-11-7);
				g.drawRect(3, 10, super.getWidth()-12, super.getHeight()-12-7);
				
				
			}

		};
		panelStatus.setVisible(false);
		//panelStatus.setBackground(Color.RED);
		panelStatus.setOpaque(false);
		panelWithEverything.add(panelStatus, "2, 4, fill, fill");
		panelStatus.setLayout(new BorderLayout(0, 0));
		
		lblLoginStatus = new JLabel("Error text");
		lblLoginStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginStatus.setMinimumSize(new Dimension(85, 45));
		panelStatus.add(lblLoginStatus);
		
		lblConfigurationFolder = new JLabel("Configuration folder:");
		lblConfigurationFolder.setForeground(Color.GRAY);
		lblConfigurationFolder.setHorizontalAlignment(SwingConstants.CENTER);
		panelWithEverything.add(lblConfigurationFolder, "1, 6, 3, 1");
		
		JLabel lblAbout = new JLabel(textdata.getKey("appbriefdescription"));
		panelWithEverything.add(lblAbout, "2, 8");
		lblAbout.setForeground(Color.GRAY);
		lblAbout.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblAcercaDe = new JLabel(textdata.getKey("btnabout") + "     ");
		panelWithEverything.add(lblAcercaDe, "3, 8");
		lblAcercaDe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblAcercaDe.setForeground(new Color(0,110,198,255));
		lblAcercaDe.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblAcercaDe.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				try {
					
					About window = new About(textdata);
					window.frameAbout.setVisible(true);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				
			}
		});
	}
}
