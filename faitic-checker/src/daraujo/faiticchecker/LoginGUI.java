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
import java.awt.List;
import java.awt.RenderingHints;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.SwingWorker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.awt.Cursor;
import java.awt.Toolkit;

import javax.swing.JCheckBox;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.FlowLayout;

import javax.swing.JTextPane;

public class LoginGUI {

	protected static boolean verbose;
	
	protected static Locale language;
	protected static TextData textdata=new TextData(new Locale("es"));
	
	private static JCustomPanel panelLogin;
	private static JPanel pSpanish, pEnglish, pGalician;

	protected static Faitic faitic;
	protected static String mainDocument;
	protected static Settings settings;
	protected static Updater updater;
	
	private static String updateJar=null;
	
	protected static JFrame loginFrame;
	
	//private final static Image imgUVigo=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoUVigo.png")).getImage();
	//private final static Image imgFaitic=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaitic.png")).getImage();
	
	private final static Image imgFaicheck=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaicheck.png")).getImage();

	private final static Image iconSettings=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/settings.png")).getImage();
	
	private final static Image spanishFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/spain.png")).getImage();
	private final static Image galicianFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/galicia.png")).getImage();
	private final static Image englishFlag=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/england.png")).getImage();
	private final static Image flagButton=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/flagButton.png")).getImage();
	
	private final static Image backgroundLogin =new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/background_login.png")).getImage();
	
	private static JTextField txtUsuario;
	private static JPasswordField pwdPassword;
	
	private static JPanel panelStatus;
	private static JLabel lblLoginStatus;
	
	private static JCheckBox cRememberUsername, cRememberPassword;
	
	private static JCustomButton btnLogin;
	
	private static String username;
	private static char[] tmpPassword;
	
	private static String updateChannel="v1";
	private static final String updateChannelDefault="v1";
	
	private static boolean offlinesaving=true;

	private static ActionListener enterPressed = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			fcnLogin();
		}
	};
	private JLabel lblConfigurationFolder;
	private static JPanel panelUpdater;
	private static JLabel lblUpdateHeader;
	private static JTextPane txtUpdate;
	private static JLabel lblMoreInfo;
	private static JLabel lblUpdate;
	
	private static Semaphore semLang=new Semaphore(1);
	private JPanel panelSettings;
	private JPanel panel_3;
	private JCheckBox cCheckForUpdates;
	private JPanel panel_1;
	private JPanel panel_4;
	private JCustomButton btnCreatePortable;
	private static JButton btnOfflineMode;
	private JCheckBox cOfflineSaving;
	private JLabel lblCanalDeActualizaciones;
	private static JTextField txtUpdateChannel;
	private JButton btnGuardar;
	private JButton btnPredeterminado;
	private JPanel panel;
	
	protected static String getJarPath(){

		try{
			
			String jarPath=ClassicRoutines.cpath(URLDecoder.decode(MainClass.class.getProtectionDomain().getCodeSource().getLocation().getPath(),"UTF-8"));

			if(jarPath.charAt(0)=='\\' && jarPath.charAt(2)==':' && jarPath.charAt(3)=='\\') jarPath=jarPath.substring(1, jarPath.length());
			
			return jarPath;
			
		} catch(Exception ex){
			
			ex.printStackTrace();
			
		}
		
		// If there is an error
		return null;

	}
	
	protected static boolean isTheJarPathAFile(){
		
		String jarPath=getJarPath();
		
		if(jarPath==null) return false;
		
		return jarPath.lastIndexOf("\\") < jarPath.length()-1 && jarPath.lastIndexOf("/") < jarPath.length() -1;
		
	}
	
	private static void showUpdates(){
		
		SwingWorker trabajador=new SwingWorker(){

			@Override
			protected Updater doInBackground() {

				try {
					
					updater=new Updater(updateChannel);
					updater.fillUpdateInfo(getLanguage());
					
				} catch (Exception e) {
					
					e.printStackTrace();
					updater=null;
					
				}
				
				return null;
				
			}
			
			@Override
			protected void done(){
				
				try {
					
					if(updater==null){
						
						panelUpdater.setVisible(false);
						return;
						
					}
					
					// Testing purposes

					System.out.println(updater.sha256);
					System.out.println(updater.downloadname);
					System.out.println(updater.currentversion);
					System.out.println(updater.urlmoreinfo);
					System.out.println(updater.downloadurl);
					System.out.println(updater.description);
					
					System.out.println(updater.isThereANewVersion());
				
					// Real thing
					
					if(updater.isThereANewVersion() && isTheJarPathAFile()){
						
						lblUpdateHeader.setText(textdata.getKey("updateheader", updater.currentversion, About.VERSION));
						txtUpdate.setText(textdata.getKey("textupdate", updater.description, updater.urlmoreinfo));
						panelUpdater.setVisible(true);
						
					} else{
						
						panelUpdater.setVisible(false);
						
					}
					
				} catch (Exception e) {
					
					e.printStackTrace();
					panelUpdater.setVisible(false);
					
				}
				
			}
			
			
			
		};
		
		
		trabajador.execute();

		
	}
	
	private static void setLanguage(String lang){
		
		try{
		
			semLang.acquire();
		
			language=new Locale(lang);
			textdata=new TextData(language);
		
			if(settings != null) settings.jsonConf.put("Language", lang);

			semLang.release();
		
		} catch(Exception ex){
			
			ex.printStackTrace();
			
		}
		
	}
	
	private static String getLanguage(){
		
		try{
		
			semLang.acquire();
		
			String output=language.getLanguage();
			
			semLang.release();
			
			return output;
		
		} catch(Exception ex){
			
			ex.printStackTrace();
			
		}
		
		return "";

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

	private static void setInterfaceEnabled(boolean enabled){
		
		txtUsuario.setEnabled(enabled);
		pwdPassword.setEnabled(enabled);
		
		btnLogin.setEnabled(enabled);
		btnOfflineMode.setEnabled(enabled);
		
		cRememberUsername.setEnabled(enabled);
		cRememberPassword.setEnabled(cRememberUsername.isSelected() && enabled);
		
		
	}
	
	private static void fcnLogin(){
		
		fcnLogin(true);
		
	}
	
	private static void fcnLogin(final boolean online){

		username=txtUsuario.getText();
		tmpPassword=pwdPassword.getPassword();

		setInterfaceEnabled(false);
		
		if(online)
			btnLogin.setText(textdata.getKey("btnloginnow"));
		else
			btnOfflineMode.setText(textdata.getKey("btnofflinemodenow"));
		
		SwingWorker trabajador=new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {

				try{
					// Log in

					mainDocument=online ? faitic.faiticLogin(username,String.valueOf(tmpPassword)) : "";	// If offline, then ""

					// Cleaning the pass

					for(int i=0; i<tmpPassword.length; i++){

						tmpPassword[i]=0;

					}

					tmpPassword=new char[0];

				} catch (Exception e1) {

					e1.printStackTrace();

					// Error. Null for login error

					mainDocument=null;


				}


				return null;
			}

			@Override
			protected void done(){


				if(mainDocument!=null){	// Offline assigns "" to mainDocument !!

					// Success

					panelStatus.setBackground(Color.green.darker());
					lblLoginStatus.setText(textdata.getKey("loginsuccessful"));

					if(online){	// Save data only if online

						// Save user name and password, if required. Password will be encoded
						if(settings.jsonConf.containsKey("Username")) settings.jsonConf.remove("Username");
						if(settings.jsonConf.containsKey("EncodedPassword")) settings.jsonConf.remove("EncodedPassword");

						if(cRememberUsername.isSelected()){

							settings.jsonConf.put("Username", username);

							if(cRememberPassword.isSelected()){

								try{

									settings.jsonConf.put("EncodedPassword", Encrypter.encrpytAES(String.valueOf(pwdPassword.getPassword()),username));

								} catch(Exception ex){

									ex.printStackTrace();

								}

							}

						}

					}

					// Reset password textbox
					pwdPassword.setText("");


					// Open subjects menu
					try {

						SubjectsGUI window = new SubjectsGUI(textdata);

						window.mainDocument=mainDocument;
						window.faitic=online ? faitic : null;
						window.username=username;
						window.online=online;
						window.offlinesaving=offlinesaving;
						window.verbose=verbose;

						// End with settings and create it for the subjects GUI

						settings.saveSettings();

						//loginFrame.setVisible(false);

						loginFrame.dispose();

						window.settings=new Settings("user-" + username + ".conf");

						window.subjectsFrame.setVisible(true);

						window.subjectsFrame.setBounds(loginFrame.getBounds());
						window.subjectsFrame.setExtendedState(loginFrame.getExtendedState());
						
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} else{

					// Unsuccessful


					panelStatus.setBackground(Color.RED);
					lblLoginStatus.setText(textdata.getKey("loginunsuccessful"));


				}

				setInterfaceEnabled(true);
				panelStatus.setVisible(true);
				btnLogin.setText(textdata.getKey("btnlogin"));
				
			}

		};
		
		trabajador.execute();


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

	private void toDoOnClosing(){

		settings.saveSettings();

		loginFrame.dispose();
		
		if(updateJar!=null){
			
			ProcessBuilder procUpdater=new ProcessBuilder();
			
			ArrayList<String> listaComandos=new ArrayList<String>();
			listaComandos.add("java");
			listaComandos.add("-jar");
			listaComandos.add(updateJar);
			listaComandos.add("--update");
			listaComandos.add(updateJar);		// New one
			listaComandos.add(getJarPath());	// Previous (Current) one
			
			procUpdater.command(listaComandos);
			try {
				
				procUpdater.start();
				
			} catch (IOException e) {

				e.printStackTrace();
				
			}
			
		}
		
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
				toDoOnClosing();
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
					cRememberUsername.setSelected(true);
					
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
					
					cRememberUsername.setSelected(false);
					cRememberPassword.setSelected(false);
					
					txtUsuario.requestFocus();
					
				}
				
				// Update language menu
				updateLanguageMenu();
				
				System.out.println(textdata.getKey("language"));
				
				// Show config folder
				lblConfigurationFolder.setText(textdata.getKey("configfolder", ClassicRoutines.getUserDataPath(true)));
				
				// Portable button
				btnCreatePortable.setVisible(!ClassicRoutines.isPortable());
				
				// Is offline saving activated or not
				
				if(settings.jsonConf.containsKey("offlinesavingdeactivated")) offlinesaving=false;
				cOfflineSaving.setSelected(offlinesaving);
				
				// Faicheck updater. Should be done as the last thing
				System.out.println("My JAR file: " + getJarPath());
				
				cCheckForUpdates.setSelected(!settings.jsonConf.containsKey("noupdatecheck"));
				
				// Update channel
				if(settings.jsonConf.containsKey("updatechannel")){
					
					updateChannel=(String) settings.jsonConf.get("updatechannel");
					btnPredeterminado.setEnabled(true);
					
				} else{
					
					btnPredeterminado.setEnabled(false);
					
				}
				
				txtUpdateChannel.setText(updateChannel);
				
				// After preparing all that it is needed, check for updates if allowed
				
				if(isTheJarPathAFile() && !settings.jsonConf.containsKey("noupdatecheck")) showUpdates();
				
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
				
				// Background
				
				int backwidth=getWidth();
				int backheight=backwidth * backgroundLogin.getHeight(null) / backgroundLogin.getWidth(null);
				
				g2.drawImage(backgroundLogin, 0, getHeight()-backheight, backwidth, backheight, null);
				
				
				// Logo and so on
				
				int imgwidth=380;
				int imgheight=imgwidth*imgFaicheck.getHeight(null)/imgFaicheck.getWidth(null);
				
				int loginposy=panelLogin != null ? panelLogin.getY() : 300;
				int settingsendy=panelSettings!=null ? panelSettings.isVisible() ? panelSettings.getHeight() : 0 : 0;
				
				int logoposx=(super.getWidth()-imgwidth)/2;
				int logoposy=(loginposy-imgheight-settingsendy)*3/4 + settingsendy;
				
				g2.drawImage(imgFaicheck, logoposx, logoposy, imgwidth, imgheight, null);
				
				String briefdesc=textdata.getKey("appbriefdescription");
				Font briefdescfont=new Font(Font.DIALOG,Font.BOLD,12);
				
				g2.setFont(briefdescfont);

				int briefdescx=(getWidth()-g2.getFontMetrics().stringWidth(briefdesc))/2;
				int briefdescy=getHeight()-g2.getFontMetrics().getMaxDescent()-6;
				
				g2.setColor(new Color(60,93,56,200).darker());
				g2.fillRoundRect(briefdescx-10, briefdescy-g2.getFontMetrics().getMaxAscent()-3, g2.getFontMetrics().stringWidth(briefdesc)+20, g2.getFontMetrics().getMaxAscent() + 
						g2.getFontMetrics().getMaxDescent()+6, 6, 6);
				
				g2.setColor(Color.WHITE);
				g2.drawString(briefdesc, briefdescx, briefdescy);
				
			}
			
		};
		panelWithEverything.setOpaque(false);
		panelWithEverything.setBackground(Color.WHITE);
		loginFrame.getContentPane().add(panelWithEverything, BorderLayout.CENTER);
		panelWithEverything.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				ColumnSpec.decode("460px"),
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				FormFactory.PREF_ROWSPEC,
				RowSpec.decode("default:grow(4)"),
				FormFactory.MIN_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				RowSpec.decode("default:grow(3)"),}));
		
		panelSettings = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				g.setColor(new Color(240,240,240,255));
				g.fillRect(0, 0, super.getWidth(), super.getHeight());
				
				g.setColor(new Color(70,70,70,255));
				g.drawLine(0, super.getHeight()-1, super.getWidth(), super.getHeight()-1);
				
				for(int i=0; i<=5; i++){
					g.setColor(new Color(70,70,70,90*(5-i)/5));
					g.drawLine(0, super.getHeight()-2-i, super.getWidth(), super.getHeight()-2-i);
				}
				
				Random random=new Random(1);
				
				for(int i=0; i<super.getHeight(); i++){
					
					g.setColor(new Color(255,255,255,50+random.nextInt(110)));
					g.drawLine(0, i, super.getWidth(), i);
					
				}
				
			}
			
		};
		panelSettings.setVisible(false);
		panelWithEverything.add(panelSettings, "1, 1, 3, 1");
		panelSettings.setOpaque(false);
		panelSettings.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("30dlu"),
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,}));
		
		btnCreatePortable = new JCustomButton(textdata.getKey("btnconverttoportable"));
		btnCreatePortable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				settings.saveSettings();	// Save current configuration

				String relativepath=ClassicRoutines.getUserDataPath(true, true);
				
				if(!new File(relativepath).exists()){
					
					// It doesn't exist, let's create the relative folder
					
					ClassicRoutines.createNeededFolders(ClassicRoutines.cpath(relativepath + "/"));
					
				}

				if(isTheJarPathAFile()){

					ProcessBuilder proc=new ProcessBuilder();

					ArrayList<String> listaComandos=new ArrayList<String>();
					listaComandos.add("java");
					listaComandos.add("-jar");
					listaComandos.add(getJarPath());	// Open the jar again

					proc.command(listaComandos);
					try {

						proc.start();

					} catch (IOException e) {

						e.printStackTrace();

					}

				}

				
				System.exit(0);
				
			}
		});
		panelSettings.add(btnCreatePortable, "2, 2, 6, 1");
		
		
		cCheckForUpdates = new JCheckBox(textdata.getKey("checkforupdatesatstartup"));
		cCheckForUpdates.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				
				if(arg0.getStateChange()==arg0.SELECTED){
					
					// Check for updates automatically on start-up
					
					if(settings.jsonConf.containsKey("noupdatecheck")) settings.jsonConf.remove("noupdatecheck");
					
				} else{
					
					// Don't check for updates automatically
					
					if(!settings.jsonConf.containsKey("noupdatecheck")) settings.jsonConf.put("noupdatecheck","");
					
				}
				
				System.out.println("Check for updates: " + (arg0.getStateChange()==arg0.SELECTED ? "ON" : "OFF"));
				
			}
		});
		
		cCheckForUpdates.setIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxfalse.png")));
		cCheckForUpdates.setSelectedIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxtrue.png")));
		
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
		panelSettings.add(pSpanish, "9, 2");
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
		panelSettings.add(pGalician, "10, 2");
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
		panelSettings.add(pEnglish, "11, 2");
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
		panelSettings.add(cCheckForUpdates, "3, 4, 5, 1");
		cCheckForUpdates.setOpaque(false);
		
		cOfflineSaving = new JCheckBox(textdata.getKey("checkofflinesaving"));
		cOfflineSaving.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				
				if(arg0.getStateChange()==arg0.SELECTED && settings.jsonConf.containsKey("offlinesavingdeactivated")){
					
					// If the offline mode is active and the deactivation setting is there
					
					settings.jsonConf.remove("offlinesavingdeactivated");
					
				} else if(arg0.getStateChange()==arg0.DESELECTED && !settings.jsonConf.containsKey("offlinesavingdeactivated")){
					
					settings.jsonConf.put("offlinesavingdeactivated","");
					
				}
				
				offlinesaving=arg0.getStateChange()==arg0.SELECTED;
				
			}
		});

		cOfflineSaving.setIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxfalse.png")));
		cOfflineSaving.setSelectedIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxtrue.png")));
		
		cOfflineSaving.setOpaque(false);
		panelSettings.add(cOfflineSaving, "3, 6, 5, 1");
		
		lblCanalDeActualizaciones = new JLabel(textdata.getKey("updatechannelsettingslabel"));
		panelSettings.add(lblCanalDeActualizaciones, "3, 8, right, default");
		
		txtUpdateChannel = new JCustomTextField("",new Color(0,110,198,255));
		txtUpdateChannel.setFont(new Font("Dialog", Font.PLAIN, 14));
		panelSettings.add(txtUpdateChannel, "5, 8, fill, default");
		txtUpdateChannel.setColumns(10);
		
		btnGuardar = new JCustomButton(textdata.getKey("updatechannelsettingsupdate"));
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int response=JOptionPane.showConfirmDialog(loginFrame, textdata.getKey("updatechannelsettingswarning"), textdata.getKey("updatechannelsettingswarningtitle"), JOptionPane.YES_NO_OPTION);

				if(response==JOptionPane.YES_OPTION){

					updateChannel=txtUpdateChannel.getText();

					if(settings.jsonConf.containsKey("updatechannel")) settings.jsonConf.remove("updatechannel");
					settings.jsonConf.put("updatechannel", updateChannel);

					btnPredeterminado.setEnabled(true);

					if(isTheJarPathAFile() && !settings.jsonConf.containsKey("noupdatecheck")) showUpdates();

				}
				
			}
		});
		panelSettings.add(btnGuardar, "7, 8");
		
		btnPredeterminado = new JCustomButton(textdata.getKey("updatechannelsettingsdelete"));
		btnPredeterminado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				updateChannel=updateChannelDefault;
				if(settings.jsonConf.containsKey("updatechannel")) settings.jsonConf.remove("updatechannel");
				txtUpdateChannel.setText(updateChannel);
				
				btnPredeterminado.setEnabled(false);
				
				if(isTheJarPathAFile() && !settings.jsonConf.containsKey("noupdatecheck")) showUpdates();
				
			}
		});
		panelSettings.add(btnPredeterminado, "9, 8, 3, 1");
		
		lblConfigurationFolder = new JLabel("Configuration folder:");
		panelSettings.add(lblConfigurationFolder, "3, 10, 3, 1");
		lblConfigurationFolder.setForeground(Color.GRAY);
		lblConfigurationFolder.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		JLabel lblAcercaDe = new JLabel(textdata.getKey("btnabout"));
		panelSettings.add(lblAcercaDe, "7, 10, 5, 1");
		lblAcercaDe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblAcercaDe.setForeground(new Color(0,110,198,255));
		lblAcercaDe.setHorizontalAlignment(SwingConstants.CENTER);
		
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
		
		panel = new JPanel();
		panel.setOpaque(false);
		panelWithEverything.add(panel, "2, 2, fill, fill");
		panel.setLayout(new FormLayout(new ColumnSpec[] {},
			new RowSpec[] {}));
		
		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panelWithEverything.add(panel_1, "3, 2, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		panel_4 = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, super.isEnabled() ? 1f : 0.3f));
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.drawImage(iconSettings, 0, 0, super.getWidth(), super.getHeight(), null);
				
			}
			
		};
		panel_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panelSettings.setVisible(!panelSettings.isVisible());
				panelLogin.setVisible(!panelSettings.isVisible());
			}
		});
		panel_4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel_4.setPreferredSize(new Dimension(30, 30));
		panel_4.setMinimumSize(new Dimension(30, 30));
		panel_4.setOpaque(false);
		panel_1.add(panel_4, "2, 2, fill, fill");
		
		
		panelLogin = new JCustomPanel();
		panelLogin.setOpaque(false);
		panelLogin.setBackground(new Color(140,140,140,255));
		panelLogin.setMinimumSize(new Dimension(460, 280));
		panelWithEverything.add(panelLogin, "2, 3, fill, fill");
		panelLogin.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("48px"),
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("51px"),},
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
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblUsuario = new JLabel(textdata.getKey("lblusername"));
		panelLogin.add(lblUsuario, "2, 2, right, default");
		
		txtUsuario = new JCustomTextField("",new Color(0,110,198,255));
		txtUsuario.addActionListener(enterPressed);
		txtUsuario.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0) {

				textChanged();
				
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {

				textChanged();
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {

				textChanged();
				
			}
			
			private void textChanged(){
				
				btnOfflineMode.setVisible(OfflineFaitic.offlineExists(txtUsuario.getText()));
				
			}
			
		});
		txtUsuario.setMinimumSize(new Dimension(4, 25));
		panelLogin.add(txtUsuario, "4, 2, fill, default");
		txtUsuario.setColumns(10);
		
		JLabel lblContrasea = new JLabel(textdata.getKey("lblpassword"));
		panelLogin.add(lblContrasea, "2, 4, right, default");
		
		pwdPassword = new JCustomPasswordField(new Color(0,110,198,255));
		pwdPassword.addActionListener(enterPressed);
		pwdPassword.setMinimumSize(new Dimension(4, 25));
		panelLogin.add(pwdPassword, "4, 4, fill, default");
		
		cRememberUsername = new JCheckBox(textdata.getKey("rememberusername"));
		cRememberUsername.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				
				cRememberPassword.setEnabled(arg0.getStateChange()==arg0.SELECTED);
				
			}
		});
		cRememberUsername.setOpaque(false);
		cRememberUsername.setIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxfalse.png")));
		cRememberUsername.setSelectedIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxtrue.png")));
		panelLogin.add(cRememberUsername, "4, 6");
		
		cRememberPassword = new JCheckBox(textdata.getKey("rememberpassword"));
		cRememberPassword.setEnabled(false);
		cRememberPassword.setOpaque(false);
		cRememberPassword.setIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxfalse.png")));
		cRememberPassword.setSelectedIcon(new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/checkboxtrue.png")));
		panelLogin.add(cRememberPassword, "4, 8");
		
		btnLogin = new JCustomButton(textdata.getKey("btnlogin"));
		panelLogin.add(btnLogin, "2, 10, 3, 1");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				fcnLogin();
				
			}
		});
		
		btnOfflineMode = new JCustomButton(textdata.getKey("btnofflinemode"));
		btnOfflineMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				fcnLogin(false);
				
			}
		});
		btnOfflineMode.setVisible(false);
		panelLogin.add(btnOfflineMode, "2, 12, 3, 1");
		
		panelUpdater = new JCustomPanel();
		panelUpdater.setBackground(Color.LIGHT_GRAY);
		panelUpdater.setVisible(false);
		
		panelStatus = new JCustomPanel();
		panelStatus.setVisible(false);
		//panelStatus.setBackground(Color.RED);
		panelStatus.setOpaque(false);
		panelWithEverything.add(panelStatus, "2, 5, fill, fill");
		panelStatus.setLayout(new BorderLayout(0, 0));
		
		lblLoginStatus = new JLabel("Error text");
		panelStatus.add(lblLoginStatus, BorderLayout.CENTER);
		lblLoginStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginStatus.setMinimumSize(new Dimension(85, 45));
		panelUpdater.setOpaque(false);
		panelWithEverything.add(panelUpdater, "2, 7, fill, fill");
		panelUpdater.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				ColumnSpec.decode("10dlu"),},
			new RowSpec[] {
				RowSpec.decode("12dlu"),
				FormFactory.PREF_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				RowSpec.decode("11dlu"),}));
		
		lblUpdateHeader = new JLabel("Update header");
		lblUpdateHeader.setHorizontalAlignment(SwingConstants.CENTER);
		panelUpdater.add(lblUpdateHeader, "2, 2, 4, 1");
		
		txtUpdate = new JTextPane();
		txtUpdate.setEditable(false);
		txtUpdate.setVisible(false);
		panelUpdater.add(txtUpdate, "2, 4, 4, 1, fill, fill");
		
		lblMoreInfo = new JLabel(textdata.getKey("lblupdatemoreinfo"));
		lblMoreInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				((JLabel)arg0.getComponent()).setText(txtUpdate.isVisible() ? textdata.getKey("lblupdatemoreinfo") : textdata.getKey("lblupdatelessinfo"));
				panelLogin.setVisible(txtUpdate.isVisible());
				txtUpdate.setVisible(!txtUpdate.isVisible());
				
			}
		});
		lblMoreInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblMoreInfo.setForeground(new Color(0,110,198,255));
		panelUpdater.add(lblMoreInfo, "3, 6");
		
		lblUpdate = new JLabel(textdata.getKey("lblupdatenow"));
		lblUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(!arg0.getComponent().isEnabled()) return;
				
				btnLogin.setEnabled(false);
				btnOfflineMode.setEnabled(false);
				
				((JLabel) arg0.getComponent()).setText(textdata.getKey("lblupdatenowprocessing"));
				
				arg0.getComponent().setEnabled(false);
				
				SwingWorker trabajador = new SwingWorker(){

					@Override
					protected String doInBackground() throws Exception {
						
						return updater.updateMyself();
					}
					
					@Override
					protected void done(){
						

						String updateResult=null;
						
						try {
							
							updateResult = (String) get();
							
						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(updateResult!=null){
							
							// There is the jar path
							
							updateJar=updateResult;
							
							//toDoOnClosing();
							lblUpdate.setText(textdata.getKey("lblupdatenowrestart"));
							
						} else{

							lblUpdate.setText(textdata.getKey("lblupdatenowerror"));
							
						}
						
						
					}
					
				};
				
				trabajador.execute();
				
			}
		});
		lblUpdate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblUpdate.setForeground(new Color(0,110,198,255));
		panelUpdater.add(lblUpdate, "5, 6");
	}
}
