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

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import java.awt.BorderLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.zip.CRC32;
import java.awt.SystemColor;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.awt.Toolkit;

import javax.swing.JSplitPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;

public class SubjectsGUI {

	protected static JFrame subjectsFrame;
	
	protected static TextData textdata=new TextData(new Locale("es"));
	
	protected static Faitic faitic;
	protected static String mainDocument;
	protected static Settings settings;
	protected static String username;
	protected static boolean online=true;
	protected static boolean offlinesaving=true;
	protected static boolean verbose=false;
	protected static boolean justloggingout=false;
	
	private static JPanel panelLogos, panelSubjects, panelSubject, panelOptions, panelEverything;
	
	private final static Image imgFaicheck=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaicheck.png")).getImage();
	// private final static Image iconSchedule=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/schedule.png")).getImage();
	// private final static Image iconSearch=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/search.png")).getImage();
	
	private static JCustomButton btnDescargarMarcados;
	private JLabel btnMarcarNuevos, btnMarcarTodo, btnMarcarNada;
	
	private static JLabel[] lblSubjects;
	private static JCheckBox[] cArchivos, cFolders;
	private static JLabel[] lArchivos, lParentPaths, lGeneralParentPaths;
	private static JLabel[] btnAbrirArchivos;
	
	private static int selectedSubject=-1, prevSelectedSubject=-1;
	private static ArrayList<Subject> subjectList;
	private static DocumentFromURL subject;
	private static int subjectType;
	private static String subjectURL;
	private static ArrayList<FileFromURL> fileList;
	private static String htmlannouncements=null, htmlintroduction=null;
	private static String prevhtmlannouncements=null, prevhtmlintroduction=null;
	private static boolean announcementsread=true, introductionread=true;
	private static String subjectPath;
	
	private static File jDirChooserCurrentDir;
	
	private static JLabel lblSubjectName;
	private static JLabel lblProperties;
	private static JScrollPane scrollPane;
	private static JPanel panelToDownload;
	private JLabel lblSeleccioneUnaAsignatura;
	private static JLabel itemSelectSubjectFolder;
	private static JPanel panelLoading;
	public static Timer timer=null, timerDownloadCheck=null;
	
	
	protected static String loadingText="Loading...";
	protected static Semaphore accessToLoadingText=new Semaphore(1);
	
	protected static int cloadingpercent=-1;
	protected static Semaphore sloadingpercent=new Semaphore(1);
	
	protected static boolean isLoading=false;
	private JSplitPane splitPane;
	private JScrollPane scrollPane_1;
	private JPanel panelLogoSpace;
	private static JLabel lblSubjectFolder;
	private static JLabel lblOpenFolder;
	
	private static boolean descargando=false;
	private static JPanel panelSearch;
	private static JTextField txtSearch;
	private static JPanel btnSearch;
	private JPanel panel;
	private static JPanel btnSchedule, btnLogout;
	private static JPanel panelSections;
	private static JLabel lblIntroduction,lblAnnouncements,lblFiles;
	private JPanel btnDeleteSearch;
	
	private static int cnDownloadedFiles=0;
	private static Semaphore snDownloadedFiles=new Semaphore(1);
	
	private static int nFilesToDownload=0;
	
	
	/**
	 * Application functions
	 */
	
	// Concurrency things
	
	private static String readLoadingText(){
		
		try {
			
			accessToLoadingText.acquire();
			String toreturn=loadingText;
			accessToLoadingText.release();
			return toreturn;
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			return "Loading...";
			
		}

	}
	
	private static void writeLoadingText(String text){
		
		try {
			accessToLoadingText.acquire();
			loadingText=text;
			accessToLoadingText.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected static int getNDownloadedFiles(){

		try{

			snDownloadedFiles.acquire();
			int out=cnDownloadedFiles;
			snDownloadedFiles.release();

			return out;

		} catch(Exception ex){

			// Weird. Stop the download just in case

			ex.printStackTrace();
			return 0;

		}

	}

	protected static void setNDownloadedFiles(int value){

		try{

			snDownloadedFiles.acquire();
			cnDownloadedFiles=value;
			snDownloadedFiles.release();

		} catch(Exception ex){

			ex.printStackTrace();

		}

	}
	

	protected static int getloadingpercent(){

		try{

			sloadingpercent.acquire();
			int out=cloadingpercent;
			sloadingpercent.release();

			return out;

		} catch(Exception ex){

			// Weird. Stop the download just in case

			ex.printStackTrace();
			return -1;

		}

	}

	protected static void setloadingpercent(int value){

		try{

			sloadingpercent.acquire();
			cloadingpercent=value;
			sloadingpercent.release();

		} catch(Exception ex){

			ex.printStackTrace();

		}

	}
	

	private static void blockInterface(){

		panelLoading.setVisible(true);
		scrollPane.setVisible(false);
		for(Component comp : panelOptions.getComponents()){
			comp.setEnabled(false);
		}

		itemSelectSubjectFolder.setVisible(false);

		btnSearch.setVisible(false);
		panelSearch.setVisible(false);
		btnLogout.setVisible(false);

	}
	
	private static void activateInterface(){

		itemSelectSubjectFolder.setVisible(selectedSubject>=0);

		panelLoading.setVisible(false);
		scrollPane.setVisible(true);
		for(Component comp : panelOptions.getComponents()){
			comp.setEnabled(online);
		}
		
		if(fileList!=null) if(fileList.size()>0) btnSearch.setVisible(true);

		btnLogout.setVisible(true);
		
		panelOptions.setVisible(selectedSubject>=0 && online);
	}
	
	// The rest

	protected static Color getColorIdentifierForFile(String extension){

		byte[] extarray=extension.toLowerCase().getBytes(StandardCharsets.UTF_8);
		
		CRC32 crc32=new CRC32();
		crc32.update(extarray);
		
		long crc32output=crc32.getValue();
		
		int mat=(int)(crc32output & 0xff);
		
		//System.out.println(mat);
		
		Color outColor=Color.getHSBColor(mat/256.0f, 1.0f, 0.5f);
		
		return outColor;
		
	}

	protected static BufferedImage getImgIdentifierForFile(File file, int height){
		
		// Get extension
		String filename=file.getName();
		String extension=filename.lastIndexOf(".")>=0 && filename.lastIndexOf(".")<filename.length()-1 ? filename.substring(filename.lastIndexOf(".")+1, filename.length()) : filename;
		
		// The picture's first declaration
		BufferedImage imgout=new BufferedImage(32, height, BufferedImage.TYPE_INT_ARGB);
		
		// Get font width with certain font size and configure the text
		
		int fontheight=height-2;
		Font textfont=new Font("Dialog", Font.PLAIN, fontheight);
		String outText=extension.toUpperCase();
		
		FontMetrics measurer=imgout.getGraphics().getFontMetrics(textfont);
		int fontwidth=measurer.stringWidth(outText);
		int fontAscent=measurer.getAscent();
		
		// New declaration of the picture, with the new size
		imgout=new BufferedImage(fontwidth+8, height, BufferedImage.TYPE_INT_ARGB);
		
		// Draw everything
		
		Graphics2D g2=imgout.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(getColorIdentifierForFile(extension));
		g2.setStroke(new BasicStroke(1));
		
		g2.fillRoundRect(0, 0, fontwidth+8, height, 3, 3);
		
		// Now the text
		
		g2.setFont(textfont);
		g2.setColor(Color.white);
		g2.drawString(outText, 4, fontAscent);
		
		return imgout;
		
	}
	
	
	private static int mustbebetween(int value, int min, int max){
		
		return mustbebetween(value, min, max, -1);
		
	}
	
	private static int mustbebetween(int value, int min, int max, int ifnot){
		
		if(value>=min && value<=max){
			return value;
		} else return ifnot;
		
	}
	
	private static void fillSubjects(String[] subjects){
		
		panelSubjects.removeAll();
		panelSubjects.updateUI();
		
		RowSpec[] sRowSpec=new RowSpec[2*subjects.length+1];
		
		for(int i=0; i<sRowSpec.length-1; i+=2){
			
			sRowSpec[i] = i==0 ? FormFactory.PARAGRAPH_GAP_ROWSPEC : FormFactory.RELATED_GAP_ROWSPEC;
			sRowSpec[i+1] = RowSpec.decode("max(32px;pref)");
			
			//System.out.println("adding row with i=" + i);
			
		}
		
		sRowSpec[sRowSpec.length-1]=FormFactory.PARAGRAPH_GAP_ROWSPEC;
		
		panelSubjects.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			sRowSpec));
		
		lblSubjects=new JLabel[subjects.length];
		
		for(int i=0; i<subjects.length; i++){
			
			lblSubjects[i]=new JLabel(subjects[i]);
			lblSubjects[i].setHorizontalAlignment(SwingConstants.LEFT);
			lblSubjects[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			lblSubjects[i].setFont(new Font("Dialog", Font.PLAIN, 19));
			//lblSubjects[i].setForeground(Color.black);
			
			lblSubjects[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					
					if(isLoading) return;
					
					isLoading=true;
					
					// When a subject is clicked
					
					// 1 Detecting the subject clicked
					
					prevSelectedSubject=selectedSubject;
					
					selectedSubject=-1;
					
					for(int i=0; i<lblSubjects.length; i++){
						
						if(arg0.getComponent().equals(lblSubjects[i])){
							
							selectedSubject=i;
							
						}
						
					}
					
					panelEverything.repaint();
					
					// 2 The click action
					
					// Open subject

					if(selectedSubject>=0){
						
						// Avoid bad clicking
						blockInterface();
						
						writeLoadingText(textdata.getKey("loadingdefaulttext"));
						lblSubjectName.setText(textdata.getKey("loadingopeningsubject"));
						lblProperties.setText("");
						lblSubjectFolder.setText("");
						panelSections.setVisible(false);
						
						SwingWorker thread=new SwingWorker(){

							@Override
							protected Object doInBackground() throws Exception {
								
								try{
									

									if(prevSelectedSubject>=0 && subjectURL!=null && subject!=null && online) {
										
										// Important not to override values used by logout BEFORE logging out
										
										writeLoadingText(textdata.getKey("loadingclosingprevioussubject"));
										faitic.logoutSubject(subjectURL, subject.getDocument(), subjectType);
										
									}
									
									writeLoadingText(textdata.getKey("loadingopeningsubject"));
									
									String subjectName=subjectList.get(selectedSubject).getName();
									
									// Announcements and intro cleaned
									htmlannouncements=null; htmlintroduction=null;
									prevhtmlannouncements=null; prevhtmlintroduction=null;
									announcementsread=true; introductionread=true;
									
									if(online){
										
										// Online mode

										subject=faitic.goToSubject(subjectList.get(selectedSubject).getURL());
										subjectType=faitic.subjectPlatformType(subject.getURL());
										subjectURL=subject.getURL();

										writeLoadingText(textdata.getKey("loadinglistingfiles"));

										if(subjectType == Faitic.CLAROLINE){

											fileList = faitic.listDocumentsClaroline(subjectURL);
											htmlintroduction=faitic.readClarolineIntro(subjectURL);
											htmlannouncements=faitic.readClarolineAnnouncements(subjectURL);

										}
										else if(subjectType == Faitic.MOODLE){

											fileList = faitic.listDocumentsMoodle(faitic.lastRequestedURL);

										}else if(subjectType == Faitic.MOODLE2 || subjectType == Faitic.MOODLE3){

											fileList = faitic.listDocumentsMoodle2(faitic.lastRequestedURL);

										} else{

											//Unknown
											if(fileList!=null) fileList.clear();
											else fileList=new ArrayList<FileFromURL>();

										}

										if(fileList!=null && offlinesaving) {
											
											// New data and allowed to read offline
											
											prevhtmlannouncements=OfflineFaitic.getKey(username, subjectName,"announcements");
											prevhtmlintroduction=OfflineFaitic.getKey(username, subjectName,"introduction");
											
											OfflineFaitic.setOfflineFileList(username, subjectList.get(selectedSubject).getName(), fileList, htmlannouncements, htmlintroduction);
											
										} else if(!offlinesaving){
											
											// No offline available, but new data
											
											prevhtmlannouncements=htmlannouncements;
											prevhtmlintroduction=htmlintroduction;
											
										}

									} else{
										
										// Offline mode
										
										subjectType=Faitic.UNKNOWN;
										fileList=OfflineFaitic.getOfflineFileList(username, subjectName);
										htmlannouncements=OfflineFaitic.getKey(username, subjectName,"announcements");
										htmlintroduction=OfflineFaitic.getKey(username, subjectName,"introduction");
										
										prevhtmlannouncements=htmlannouncements;
										prevhtmlintroduction=htmlintroduction;
										
									}
									
									// Check section read
									
									introductionread=sectionIsRead(prevhtmlintroduction, htmlintroduction, "introduction", subjectName);
									announcementsread=sectionIsRead(prevhtmlannouncements, htmlannouncements, "announcements", subjectName);
									
									// Get subject path
									subjectPath=settings.getSubjectPath(subjectName);
									
									if(subjectPath!=null){
										
										System.out.println("Subject path: " + subjectPath);
										
										boolean thePathExists=false;
										
										if(new File(subjectPath).exists())
											if(new File(subjectPath).isDirectory())
												thePathExists=true;
										
										if(!thePathExists){
											// The path doesn't exist or it is not a directory. Ignored.
											subjectPath=null;
											System.out.println("Path not valid (Deleted or not directory). Ignored.");
										}
										
									}else{

										System.out.println("Subject path not defined yet");
										
									}
									
									// TODO put default folders (important to know if relative or not)
									
									if(subjectPath==null){
										
										// Still null, so let's put a default one
										
										subjectPath=getDefaultFolderForSubject(subjectName);
										
									}
									
								}catch(Exception ex){
									
									ex.printStackTrace();
									
									//Error, so the subject will not be selected
									
									selectedSubject=-1;
									
									// And no files
									
									if(fileList!=null) fileList.clear();
									else fileList=new ArrayList<FileFromURL>();
									
								}

								writeLoadingText(textdata.getKey("loadingdefaulttext"));
								
								return null;
							}
							
							@Override
							protected void done(){
								
								if(selectedSubject>=0){
									//Only if a subject is correctly selected

									// Preparing the menu, UI
									lblSubjectName.setText(subjectList.get(selectedSubject).getName());

									if(subjectType == Faitic.CLAROLINE){

										lblProperties.setText(textdata.getKey("nameclaroline"));

									}
									else if(subjectType == Faitic.MOODLE){

										lblProperties.setText(textdata.getKey("namemoodle"));

									}else if(subjectType == Faitic.MOODLE2){

										lblProperties.setText(textdata.getKey("namemoodle2"));

									}else if(subjectType == Faitic.MOODLE3){

										lblProperties.setText(textdata.getKey("namemoodle3"));

									} else{

										lblProperties.setText(textdata.getKey("nameunknown"));

									}

									if(subjectType!=Faitic.UNKNOWN || !online)
										lblProperties.setText(textdata.getKey("subjectsummary",lblProperties.getText(), fileList.size() + "", fileList.size()!=1 ? "s" : ""));

									/*String[] fileListNames=new String[fileList.size()];

									for(int i=0; i<fileList.size(); i++){
										fileListNames[i]=fileList.get(i)[0];
									}*/

									// Show subject path
									
									if(subjectPath != null){
										
										lblSubjectFolder.setText(subjectPath);
										
									} else{
										
										lblSubjectFolder.setText(textdata.getKey("labelsubjectfoldernotselected"));
										
									}
									
									// Sections for subjects supported
									
									panelSections.setVisible(htmlintroduction!=null || htmlannouncements!=null);
									if(panelSections.isVisible()){
										
										selectsectionbutton(lblFiles);

										lblIntroduction.setIcon(introductionread ? null : new ImageIcon(getHotIcon(7)));
										
										lblAnnouncements.setIcon(announcementsread ? null : new ImageIcon(getHotIcon(7)));

									}
									
									// List files now, do after getting the subject path, UI
									fillFilesFromSubject();

								} else{
									
									// Clicked but selected -1: there was an error
									
									lblSubjectName.setText(textdata.getKey("erroropeningsubjecttitle"));
									lblProperties.setText(textdata.getKey("erroropeningsubjectsummary"));
									
									fileList=new ArrayList<FileFromURL>();
									
									fillFilesFromSubject();
									
								}

								// Show interface again
								activateInterface();

								isLoading=false;
								
							}
							
							
						};
						
						// Execute all the thing from above
						thread.execute();
						
						
					} else isLoading=false;
					
				}
			});
			
			//System.out.println("adding element with pos=" + (int)(i*2+2));
			//System.out.println("The subject was " + subjects[i]);
			
			panelSubjects.add(lblSubjects[i],"2, " + (int)(i*2+2));
			
		}
		
		panelSubjects.repaint();
		
	}
	
	private static String getDefaultFolderForSubject(String subject){
		
		String[] wordsinsubject=subject.split(" ");
		StringBuffer initials=new StringBuffer();
		
		for(int i=0; i<wordsinsubject.length; i++){ // Write initials. It will take the upper cased letters and numbers
			
			boolean foundinitial=false;
			String letters="QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
			
			for(int pos=0; pos<wordsinsubject[i].length() && !foundinitial; pos++){
				
				if(letters.indexOf(wordsinsubject[i].charAt(pos))>=0){ // Avoid symbols
					initials.append(wordsinsubject[i].charAt(pos));
					foundinitial=true;
				}
				
			}
			
		}
		
		// If no name is found
		
		if(initials.length()<=0) initials.append("untitled");
		
		// Different if it is portable or not
		
		if(ClassicRoutines.isPortable()){
			
			return ClassicRoutines.cpath(ClassicRoutines.getJarPathFolder() + "/Faicheck subjects/" + initials.toString());
			
		} else{

			return ClassicRoutines.cpath(System.getProperty("user.home") + "/Faicheck subjects/" + initials.toString());
			
		}
		
	}
	
	private static boolean searchInText(String text, String search){
		
		for(String word : search.toLowerCase().split(" ")){
			
			if(!text.toLowerCase().contains(word)){
				
				return false;
				
			}
			
		}
		
		return true;
		
	}
	
	private static boolean sectionIsRead(String oldtext, String newtext, String key, String subjectName){
		
		if(!offlinesaving) return true;
		
		boolean areequal;
		
		if(oldtext==null ^ newtext==null){
			
			areequal=false;
			
		} else if(oldtext==null && newtext==null){
			
			areequal=true;
			
		} else{
			
			areequal=oldtext.equals(newtext);
			
		}
		
		if(OfflineFaitic.getKey(username, subjectName, key + "read")==null){ // If not set
			
			OfflineFaitic.setKey(username, subjectName, key + "read",areequal ? "1" : "0"); // Save the situation

			return areequal;
			
		}
		
		if(OfflineFaitic.getKey(username, subjectName, key + "read").equals("0")) return false; // So it will be different until read
		else{ // If read before or not taken into account
			
				OfflineFaitic.setKey(username, subjectName, key + "read",areequal ? "1" : "0"); // Save the situation

				return areequal;
				
		}
		
	}
	
	private static void deselectallsectionbuttons(){
		
		for(Component comp : panelSections.getComponents()){
			
			if(comp instanceof JLabel){
				
				JLabel lblcomp=(JLabel) comp;
				
				lblcomp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				lblcomp.setForeground(new Color(0,110,198,255));
				
			}
			
		}
		
	}
	
	private static void selectsectionbutton(Component selcomp){
		
		deselectallsectionbuttons();
		
		for(Component comp : panelSections.getComponents()){
			
			if(comp instanceof JLabel && selcomp.equals(comp)){
				
				JLabel lblcomp=(JLabel) comp;
				
				lblcomp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				lblcomp.setForeground(SystemColor.windowText);
				
			}
			
		}
		
		panelOptions.setVisible(selcomp.equals(lblFiles) && online && selectedSubject>=0);
		
	}
	
	private static void fillWithHTML(String html){
		
		panelToDownload.removeAll();
		panelToDownload.updateUI();

		panelToDownload.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
		new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JTextPane htmlvisor=new JTextPane();
		htmlvisor.setEditable(false);
		htmlvisor.setContentType("text/html");
		
		// Prepare the html
		
		StringBuffer outputhtml=new StringBuffer();
		
		outputhtml.append("<html><head>");
		outputhtml.append("<style type=\"text/css\">");
		
		outputhtml.append("body { font-family: arial, helvetica, sans-serif; color: #212121; }");
		outputhtml.append(".claroTable th { margin: 35px 0px 5px 0px; padding: 5px 5px 5px 5px; text-align: left;"
				+ " border-top: 1px solid #006ec6; border-bottom: 0px solid #888888; background-color: #D8ECFB; }");
		outputhtml.append(".claroTable {border-bottom: 1px solid #888888;}");
		outputhtml.append("a { color: #006ec6; text-decoration: none; font-weight: bold; }");
		
		
		outputhtml.append("</style>");
		outputhtml.append("</head><body>");
		
		outputhtml.append(html);
		
		outputhtml.append("</body></html>");
		
		htmlvisor.setText(outputhtml.toString());
		
		htmlvisor.setCaretPosition(0);
		
		System.out.println(outputhtml.toString());
		
		htmlvisor.addHyperlinkListener(new HyperlinkListener(){

			@Override
			public void hyperlinkUpdate(HyperlinkEvent arg0) {

				if(arg0.getEventType()==EventType.ACTIVATED){
					
					String host=arg0.getURL().getHost();
					String path=arg0.getURL().getFile();
					String url=arg0.getURL().toString();
					
					if(host.equals("cursos.faitic.uvigo.es")){
						
						int endofname=path.indexOf("?");
						if(endofname<=0) endofname=path.length();
						int startofname=path.lastIndexOf("/",endofname-1);
						if(startofname<=0) startofname=-1;
						
						try {
							
							txtSearch.setText(URLDecoder.decode(path.substring(startofname+1, endofname), "iso-8859-1"));

							panelSearch.setVisible(true);
							txtSearch.requestFocus();
							
							
						} catch (UnsupportedEncodingException e) {
							
							e.printStackTrace();
							
						}
						
					} else if(host.length()>0 && Desktop.isDesktopSupported()){
						
						try {
							
							Desktop.getDesktop().browse(arg0.getURL().toURI());
							
						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
						
					}
					
				}
				
			}
			
			
			
		});
		
		panelToDownload.add(htmlvisor, "2, 2, fill, fill");
		
		
	}
	
	private static BufferedImage getHotIcon(int width){
		
		BufferedImage img=new BufferedImage(width,width,BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2=img.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setColor(Color.red);
	    g2.fillOval(0, 0, width, width);
		
	    return img;
	    
	}
	
	private static void fillFilesFromSubject(){ fillFilesFromSubject(""); }
	
	private static void fillFilesFromSubject(String search){
		
		panelToDownload.removeAll();
		panelToDownload.updateUI();
		
		// Check matches and number of folders
		
		int matchcounter=0;
		int folders=0;
		String lastfolder="";

		boolean[] matcheswithtext=new boolean[fileList.size()];
		
		for(int i=0; i<fileList.size(); i++){
			
			matcheswithtext[i]=searchInText(fileList.get(i).getFileDestination(),search);
			
			if(matcheswithtext[i]){
			
				matchcounter++;
				
				// Check if folder changes
				
				if(!fileList.get(i).getParent().equals(lastfolder)){ // previously checking also if fileList.get(i).getParent().replace("/", "").length()>0
					
					folders++;
					lastfolder=fileList.get(i).getParent();
					
				}
			
			}
			
		}
		
		lastfolder=""; // Reset last folder
		
		// Now with counter let's size the table
		
		RowSpec[] fRowSpec=new RowSpec[matchcounter*4+folders*4+1];
		
		for(int i=0; i<fRowSpec.length; i++){
			
			fRowSpec[i]= i==0 || i==fRowSpec.length-1 ? FormFactory.PARAGRAPH_GAP_ROWSPEC : i % 4 == 0 ? FormFactory.PARAGRAPH_GAP_ROWSPEC : i % 2 == 0 ? FormFactory.LINE_GAP_ROWSPEC : FormFactory.PREF_ROWSPEC;
		
		}
		
		panelToDownload.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			fRowSpec));
		
		cArchivos=new JCheckBox[fileList.size()];
		lArchivos=new JLabel[fileList.size()];
		lParentPaths=new JLabel[fileList.size()];
		
		lGeneralParentPaths=new JLabel[folders];
		cFolders=new JSubjectCheckBox[folders];
		int currentgeneralparentpath=0; // When filling the labels
		
		btnAbrirArchivos=new JLabel[fileList.size()];
		
		int iDisc=0;	// For putting the info in the GUI table
		
		int folderaccu=0; // +4 when new folder label is added
		int foldercounter=0; // +1 when new folder is identified
		
		// lastfolder already declared and reset
		
		for(int i=0; i<fileList.size(); i++){
			
			boolean isAlreadyDownloaded=fileIsAlreadyDownloaded(subjectPath, fileList.get(i).getFileDestination());
			boolean matchfound=matcheswithtext[i];
			
			if(matchfound && !fileList.get(i).getParent().equals(lastfolder)){ // Same check than before
				
				// Add label for folder
				
				String textforlabel=fileList.get(i).getParent();
				if(textforlabel.length()>0) if(textforlabel.charAt(0)=='/') textforlabel=textforlabel.substring(1, textforlabel.length());
				if(textforlabel.length()>0) if(textforlabel.charAt(textforlabel.length()-1)=='/') textforlabel=textforlabel.substring(0, textforlabel.length()-1);
				textforlabel=textforlabel.replace("/", " > ").replace("_", " ");

				lGeneralParentPaths[currentgeneralparentpath]=new JLabel(textforlabel.length()>0 ? textforlabel : textdata.getKey("filelistrootfolder"));
				lGeneralParentPaths[currentgeneralparentpath].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				lGeneralParentPaths[currentgeneralparentpath].setFont(new Font("Dialog", Font.BOLD, 18));
				lGeneralParentPaths[currentgeneralparentpath].setPreferredSize(new Dimension(10,40));
				lGeneralParentPaths[currentgeneralparentpath].setVerticalAlignment(JLabel.BOTTOM);
				lGeneralParentPaths[currentgeneralparentpath].setForeground(new Color(33,33,33,255));
				lGeneralParentPaths[currentgeneralparentpath].addMouseListener(new CustomMouseAdapter(fileList.get(i).getParent()){

					@Override
					public void mouseClicked(MouseEvent arg0) {

							if(subjectPath!=null){
								
								// Subject path selected

								String parentname=(String) getObject();
								
								if(fileIsAlreadyDownloaded(subjectPath,parentname)){
									
									// Already created. Open it
									
									try {
										
										Desktop.getDesktop().open(new File(fileDestination(subjectPath,parentname)));
										
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}

							
							
							
							
						}
						
						
					}

				});
				
				
				panelToDownload.add(lGeneralParentPaths[currentgeneralparentpath], "4, " + (int)(iDisc*4+folderaccu+2));
				
				// Isolate folder
				
				JLabel btnIsolateFolder=new JLabel("[ " + textdata.getKey("filelistisolate") + " ]");
				btnIsolateFolder.setForeground(new Color(0,110,198,255));
				btnIsolateFolder.setVerticalAlignment(JLabel.BOTTOM);
				btnIsolateFolder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btnIsolateFolder.setHorizontalAlignment(JLabel.CENTER);
				
				btnIsolateFolder.addMouseListener(new CustomMouseAdapter(fileList.get(i).getParent()){

					@Override
					public void mouseClicked(MouseEvent arg0) {

						panelSearch.setVisible(true);
						
						txtSearch.setText((String) getObject());
						txtSearch.selectAll();
						txtSearch.requestFocus();
						
					}
					
				});
				
				panelToDownload.add(btnIsolateFolder, "6, " + (int)(iDisc*4+folderaccu+2) + ", center, bottom");
				
				// The check button for selecting all the folder
				
				cFolders[foldercounter]=new JSubjectCheckBox(fileList.get(i).getParent());
				cFolders[foldercounter].setOpaque(false);
				cFolders[foldercounter].setVisible(online); // Visible determines if it's selectable with the selection buttons
				cFolders[foldercounter].setMinimumSize(new Dimension(40,20));
				cFolders[foldercounter].setPreferredSize(new Dimension(40,20));
				cFolders[foldercounter].setMaximumSize(new Dimension(40,20));
				cFolders[foldercounter].setHorizontalAlignment(SwingConstants.CENTER);

				// Checked changed
				cFolders[foldercounter].addItemListener(new CustomItemListener(fileList.get(i).getParent()){

					@Override
					public void itemStateChanged(ItemEvent arg0) {
						
						if(cArchivos==null) return;
						
						for(int i=0; i<fileList.size(); i++){
							
							if(fileList.get(i).getParent().equals((String)getObject())){
								
								// If the parents match
								
								if(cArchivos!=null){
									
									// If not null
									
									if(arg0.getStateChange()==arg0.SELECTED && !cArchivos[i].isSelected()) cArchivos[i].setSelected(true);
									if(arg0.getStateChange()==arg0.DESELECTED && cArchivos[i].isSelected()) cArchivos[i].setSelected(false);
									// Done this way so as not to arise unnecessary events

								}
								
							}
							
						}
						
					}
					
				});
				
				if(online) panelToDownload.add(cFolders[foldercounter], "2, " + (int)(iDisc*4+folderaccu+2) + ", center, bottom");
				
				// Increase folderaccu for next line
				folderaccu+=2;

				JPanel separatorpanel=new JPanel(){
					
					@Override
					public void paintComponent(Graphics g){

						/*Graphics2D g2=(Graphics2D) g;

					    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

						g.setColor(new Color(220,220,220,255));
						
						g.fillRect(0, 0, getWidth(), getHeight());
						
						int radius=5;
						
						for(int i=3; i>0; i--){
							
							g2.setColor(new Color(100,100,100,80*(4-i)/3));
							g2.fillRoundRect(-i, -(radius+i), getWidth()+2*i, (radius+i)*2, (radius+i)*2, (radius+i)*2);
							g2.fillRoundRect(-i, getHeight()-1-(radius+i), getWidth()+2*i, (radius+i)*2, (radius+i)*2, (radius+i)*2);
							
						}
						
						g2.setColor(Color.WHITE);
						g2.fillRoundRect(0, -radius, getWidth(), radius*2, radius, radius);
						g2.fillRoundRect(0, getHeight()-1-radius, getWidth(), radius*2, radius, radius);*/
						
						
						g.setColor(new Color(160,160,160,10));
						
						for(int i=0; i<getWidth()/2; i+=5){
							
							g.drawLine(i, (getHeight()-1)/2, getWidth()-2*i, (getHeight()-1)/2);

						}
						
					}
					
				};
				
				separatorpanel.setPreferredSize(new Dimension(5,5));
				
				panelToDownload.add(separatorpanel, "1, " + (int)(iDisc*4+folderaccu+2) + ", 7, 1");
								
				// Increase currentgeneralparentpath
				currentgeneralparentpath++;
				
				// Increase folderaccu
				folderaccu+=2;
				
				// Increase foldercounter
				foldercounter++;
				
				// Update last folder
				lastfolder=fileList.get(i).getParent();
				
			}
			
			cArchivos[i]=new JSubjectCheckBox();
			cArchivos[i].setOpaque(false);
			cArchivos[i].setVisible(online && matchfound); // Visible determines if it's selectable with the selection buttons
			cArchivos[i].setMinimumSize(new Dimension(40,40));
			cArchivos[i].setHorizontalAlignment(SwingConstants.CENTER);
			
			// Later checking and event must be added
			
			if(matchfound)panelToDownload.add(cArchivos[i], "2, " + (int)(iDisc*4+folderaccu+2) + ", 1, 3");
			
			String completePath=fileList.get(i).getFileDestination();
			int divisionpos=completePath.lastIndexOf("/");
			String filename=divisionpos>=0 && divisionpos<completePath.length()-1 ? completePath.substring(divisionpos+1, completePath.length()) : completePath;
			String parentname=divisionpos>=0 && divisionpos<completePath.length()-1 ? completePath.substring(0, divisionpos+1) : "";
			
			lArchivos[i]=new JLabel(" " + filename);
			lArchivos[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			lArchivos[i].setFont(new Font("Dialog", Font.BOLD, 12));
			lArchivos[i].setForeground(!online && !isAlreadyDownloaded ? new Color(160,160,160,255) : new Color(33,33,33,255));
			lArchivos[i].setIcon(new ImageIcon(getImgIdentifierForFile(new File(fileDestination(subjectPath, fileList.get(i).getFileDestination())),12)));
			lArchivos[i].addMouseListener(new MouseAdapter(){

				@Override
				public void mouseClicked(MouseEvent arg0) {

					int index=-1;
					
					for(int i=0; i<lArchivos.length; i++){
						
						if(lArchivos[i].equals(arg0.getComponent())) index=i;
						
					}
					
					if(index>=0){
						
						// Detected the element clicked, open the file if it exists
						

						if(subjectPath!=null){
							
							// Subject path selected

							String fileRelPath=fileList.get(index).getFileDestination();
							
							if(fileIsAlreadyDownloaded(subjectPath,fileRelPath)){
								
								// Already downloaded. Open it
								
								try {
									
									Desktop.getDesktop().open(new File(fileDestination(subjectPath,fileRelPath)));
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							} else{
								
								// Not downloaded. Download and open it
								
								downloadFiles(index,true);
								
								
							}
						}
						
						
						
						
					}
					
					
				}

			});
			
			if(matchfound)panelToDownload.add(lArchivos[i], "4, " + (int)(iDisc*4+folderaccu+2));
			
			// Label for URL

			lParentPaths[i]=new JLabel(parentname);
			lParentPaths[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			lParentPaths[i].setFont(new Font("Dialog", Font.PLAIN, 10));
			lParentPaths[i].setForeground(!online && !isAlreadyDownloaded ? new Color(160,160,160,255) : new Color(117,117,117,255));
			lParentPaths[i].addMouseListener(new MouseAdapter(){

				@Override
				public void mouseClicked(MouseEvent arg0) {

					int index=-1;
					
					for(int i=0; i<lParentPaths.length; i++){
						
						if(lParentPaths[i].equals(arg0.getComponent())) index=i;
						
					}

					if(index>=0){
						
						// Detected the element clicked, open the file if it exists
						

						if(subjectPath!=null){
							
							// Subject path selected

							String completePath=fileList.get(index).getFileDestination();
							int divisionpos=completePath.lastIndexOf("/");
							String parentname=divisionpos>=0 && divisionpos<completePath.length()-1 ? completePath.substring(0, divisionpos+1) : "";
							
							if(fileIsAlreadyDownloaded(subjectPath,parentname)){
								
								// Already created. Open it
								
								try {
									
									Desktop.getDesktop().open(new File(fileDestination(subjectPath,parentname)));
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						}
						
						
						
						
					}
					
					
				}

			});
			
			if(matchfound)panelToDownload.add(lParentPaths[i], "4, " + (int)(iDisc*4+folderaccu+4));
			
			// Download and open button
			
			btnAbrirArchivos[i]=new JLabel("  " + (isAlreadyDownloaded ? textdata.getKey("filelistdelete") : textdata.getKey("filelistdownload")) + "  "){

				@Override
				public void paintComponent(Graphics g){

					//super.paintComponent(g);
					
					Color borderColor= getText().contains(textdata.getKey("filelistdelete")) ? new Color(180,180,180,255) : new Color(0,110,198,255);
					
					int maxwidth=super.getWidth();
					int maxheight=super.getHeight()-4;
					int marginleft=(super.getWidth()-maxwidth)/2;
					int margintop=(super.getHeight()-maxheight)/2;

					Graphics2D g2=(Graphics2D) g;
					
				    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				    g2.setColor(borderColor);
				    
					g2.fillRoundRect(marginleft, margintop, maxwidth, maxheight, 6, 6);
					/*
					g2.setColor(borderColor.brighter());
					g2.setStroke(new BasicStroke(2));
					g2.drawRoundRect(marginleft, margintop, maxwidth-1, maxheight-1, 6, 6);
					
					g2.setColor(borderColor.darker());
					g2.setStroke(new BasicStroke(1));
					g2.drawRoundRect(marginleft, margintop, maxwidth-1, maxheight-1, 6, 6);
					*/
					super.paintComponent(g);
					
				}

			};
			btnAbrirArchivos[i].setVisible(online || isAlreadyDownloaded);
			btnAbrirArchivos[i].setForeground(Color.white);
			btnAbrirArchivos[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			btnAbrirArchivos[i].setHorizontalAlignment(SwingConstants.CENTER);
			
			btnAbrirArchivos[i].addMouseListener(new MouseAdapter(){

				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
					// Detect the file index
					int myFileIndex=-1;
					
					for(int i=0; i<btnAbrirArchivos.length; i++){
						if(btnAbrirArchivos[i].equals(arg0.getSource())) myFileIndex=i;
					}
					
					if(myFileIndex>=0){
						
						// Detected, action
						
						if(subjectPath==null){
							
							// No subject path selected, it will be a save as
							
							String fileName=new File(ClassicRoutines.cpath(fileList.get(myFileIndex).getFileDestination())).getName();
							
							JFileChooser fileSaver=new JFileChooser();
							fileSaver.setFileSelectionMode(JFileChooser.FILES_ONLY);
							fileSaver.setDialogTitle("Descargar como...");
							fileSaver.setCurrentDirectory(new File("."));
							fileSaver.setAcceptAllFileFilterUsed(true);
							fileSaver.setMultiSelectionEnabled(false);
							
							fileSaver.setSelectedFile(new File(fileName));
							
							int fileSaveResult=fileSaver.showSaveDialog(subjectsFrame);
							
							if(fileSaveResult==JFileChooser.APPROVE_OPTION){
								
								// File successfully selected
								
								String selectedFile=fileSaver.getSelectedFile().getAbsolutePath();
								
								// Download the file
								
								String fileURL=fileList.get(myFileIndex).getURL();
								
								try {
									
									faitic.downloadFile(fileURL, "", selectedFile);
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							
							
							
							
						} else{
							
							// Subject path selected

							String fileRelPath=fileList.get(myFileIndex).getFileDestination();
							
							if(fileIsAlreadyDownloaded(subjectPath,fileRelPath)){
								
								// Already downloaded. Delete it
								
								File fileToDelete=new File(fileDestination(subjectPath,fileRelPath));
								
								System.out.println("Deleting file \"" + fileToDelete.getAbsolutePath() + "\"...");
								
								if(fileToDelete.delete()) System.out.println("Success.");
								else System.out.println("Deletion error.");
								
								setDownloadButtonsText();
								
							} else{
								
								// Not downloaded yet. Download it
								
								downloadFiles(myFileIndex,false); // Caution! Asynchronous!!
								
							}
							
						}
						
					}
					
				}
				
			});
			
			if(matchfound)panelToDownload.add(btnAbrirArchivos[i], "6, " + (int)(iDisc*4+folderaccu+2) + ", 1, 3");
			if(matchfound)iDisc++;
			
		}
		
		// Post-checkbox event adding
		
		for(int i=0; i<fileList.size(); i++){
			
			boolean isAlreadyDownloaded=fileIsAlreadyDownloaded(subjectPath, fileList.get(i).getFileDestination());
			
			// Checked changed
			cArchivos[i].addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					
					updateDownloadMarkedText();	// "Download marked files" button text set
					
					// Now check if the corresponding folder is (de)selected, only if all the files with this parent are (de)selected
					
					// Get check position
					int currentpos=-1;
					
					for(int i=0; i<cArchivos.length && currentpos<0; i++){
						
						if(cArchivos[i].equals(arg0.getSource())) currentpos=i;
						
					}
					
					if(currentpos>=0){ // Position found
						
						// System.out.println("Found");
						
						// Parent of file checked/unchecked
						String parentoffile=fileList.get(currentpos).getParent();
						
						// Get parent check position
						int checkparentpos=-1;
						
						for(int i=0; i<cFolders.length && checkparentpos<0; i++){
							
							if(((String)((JSubjectCheckBox)cFolders[i]).getObject()).equals(parentoffile))
								checkparentpos=i;
							
						}
						
						if(checkparentpos>=0){ // There is a parent check

							// System.out.println("Parent found");
							
							boolean currentIsSelected=arg0.getStateChange()==arg0.SELECTED;
							
							boolean sameselection=true;
							
							// Check if all files from same parent share the same state
							
							for(int i=0; i<cArchivos.length && sameselection; i++){
								
								if(fileList.get(i).getParent().equals(parentoffile) &&
										cArchivos[i].isSelected()!=currentIsSelected) sameselection=false;
								
							}
							
							if(sameselection && cFolders[checkparentpos].isSelected() != currentIsSelected){
								// All selected the same way but not the parent check, change the parent state
								
								cFolders[checkparentpos].setSelected(currentIsSelected);
								
							}
							
						}
						
						
					}
					
					
				}
				
			});
			
			// checkbox state definition

			cArchivos[i].setSelected(!isAlreadyDownloaded); // Not selected if downloaded
			
			
			
		}
		
		updateDownloadMarkedText();	// Download marked button text set
		
		// Done
		
		panelToDownload.repaint();
		
	}
	
	private static String fileDestination(String subjectPath, String fileRelativePath){
		
		return ClassicRoutines.cpath(subjectPath + "/" + fileRelativePath);
		
	}
	
	private static boolean fileIsAlreadyDownloaded(String subjectPath, String fileRelativePath){
		
		if(subjectPath==null) return false;
		
		if(new File(fileDestination(subjectPath, fileRelativePath)).exists()){
			return true;
		}
		
		return false;
		
	}
	
	private static void selectNotDownloadedFiles(){
		
		if(fileList==null) return;
		
		for(int i=0; i<fileList.size(); i++){
			
			String fileRelPath=fileList.get(i).getFileDestination();
			
			cArchivos[i].setSelected(!fileIsAlreadyDownloaded(subjectPath, fileRelPath) && cArchivos[i].isVisible());
			
		}
		
	}
	
	private static void setDownloadButtonsText(){
		
		if(fileList==null) return;
		
		for(int i=0; i<fileList.size(); i++){
			
			String fileRelPath=fileList.get(i).getFileDestination();
			
			boolean isAlreadyDownloaded=fileIsAlreadyDownloaded(subjectPath, fileRelPath);
			
			btnAbrirArchivos[i].setText("  " + (isAlreadyDownloaded ? textdata.getKey("filelistdelete") : textdata.getKey("filelistdownload")) + "  ");
			btnAbrirArchivos[i].setVisible(online || isAlreadyDownloaded);
			
		}
		
	}
	
	private static void downloadFileFromList(int i){

		// To be downloaded
		
		String fileRelPath=fileList.get(i).getFileDestination();
		String whereToDownloadTheFile=fileList.get(i).getURL();
		
		String strFileDestination=ClassicRoutines.createNeededFolders(fileDestination(subjectPath, fileRelPath));
		
		try {
			
			faitic.downloadFile(whereToDownloadTheFile, "", strFileDestination);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private static void downloadFiles(final int fileindex, final boolean openfile){

		if(descargando){

			faitic.setCancelDownload(true);
			btnDescargarMarcados.setEnabled(false);

		} else{

			if(isLoading) return;
			isLoading=true;

			if(fileList==null){

				JOptionPane.showMessageDialog(subjectsFrame, 
						textdata.getKey("subjectnotselectederror"), 
						textdata.getKey("subjectnotselectederrortitle"), JOptionPane.ERROR_MESSAGE);

				isLoading=false;

				return;
			}

			if(cArchivos==null) {

				isLoading=false;

				return;
			}

			if(subjectPath==null) askToSelectSubjectFolder();
			if(subjectPath==null) {

				isLoading=false;

				return;
			}

			blockInterface();

			faitic.setCancelDownload(false);
			setNDownloadedFiles(0);
			
			descargando=true;
			btnDescargarMarcados.setEnabled(true);

			btnDescargarMarcados.setText(textdata.getKey("btncanceldownload"));

			timerDownloadCheck=new Timer();

			writeLoadingText(textdata.getKey("loadingdownloading", "1", (fileindex<0 ? nFilesToDownload : 1) + "", "-", "-"));
			setloadingpercent(0);
			
			SwingWorker thread=new SwingWorker(){

				@Override
				protected Object doInBackground() throws Exception {

					if(fileindex<0){ // Not just one file. Download marked files
						
						for(int i=0; i<fileList.size(); i++){

							if(cArchivos[i].isSelected()){

								downloadFileFromList(i);

								setNDownloadedFiles(getNDownloadedFiles()+1);

							}

						}
						
					} else{ // The selected fileindex file
						
						downloadFileFromList(fileindex);

						if(openfile){
							
							// Open file after downloaded. Supposed subject path set
							
							String fileRelPath=fileList.get(fileindex).getFileDestination();
							
							if(fileIsAlreadyDownloaded(subjectPath,fileRelPath)){
								
								// Already downloaded. Open it
								
								try {
									
									Desktop.getDesktop().open(new File(fileDestination(subjectPath,fileRelPath)));
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							
						}
						
						setNDownloadedFiles(getNDownloadedFiles()+1);

					}

					writeLoadingText(textdata.getKey("loadingdefaulttext"));

					return null;
				}

				@Override
				protected void done(){

					isLoading=false;
					
					// Stop download timer
					if(timerDownloadCheck!=null){
						timerDownloadCheck.cancel();
						timerDownloadCheck.purge();
						timerDownloadCheck=null;
					}
					
					setloadingpercent(-1);
					
					selectNotDownloadedFiles();
					setDownloadButtonsText();

					activateInterface();

					faitic.setCancelDownload(false);
					btnDescargarMarcados.setEnabled(true);
					descargando=false;
					
					updateDownloadMarkedText();

				}

			};

			thread.execute();
			
			timerDownloadCheck.scheduleAtFixedRate(new TimerTask(){

				@Override
				public void run() {

					long downloaded=faitic.getDownloaded();
					long downloadsize=faitic.getDownloadSize();
					int ndownloadedfiles=getNDownloadedFiles();
					
					if(fileindex<0 && nFilesToDownload == 0 || downloadsize == 0) return; // Divided by zero if not controlled
					
					writeLoadingText(textdata.getKey("loadingdownloading", (ndownloadedfiles+1) + "", (fileindex<0 ? nFilesToDownload : 1) + "",
							(downloaded>1024*1024 ? ((double)(downloaded*10/1024/1024)/10.0) + " MiB" :
							downloaded>1024 ? ((double)(downloaded*10/1024)/10.0) + " kiB" :
							downloaded + " B"),
							(downloadsize>1024*1024 ? ((double)(downloadsize*10/1024/1024)/10.0) + " MiB" :
								downloadsize>1024 ? ((double)(downloadsize*10/1024)/10.0) + " kiB" :
									downloadsize + " B")));
					
					setloadingpercent(ndownloadedfiles*100/(fileindex<0 ? nFilesToDownload : 1) + (int)(downloaded*100/downloadsize/(fileindex<0 ? nFilesToDownload : 1)));
					
				}
				
			}, 100, 100);

		}
	}
	
	private static void selectSubjectFolder(){

		String subjectName=subjectList.get(selectedSubject).getName();
		
		JFileChooser folderSelector=new JFileChooser();
		folderSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		folderSelector.setDialogTitle(textdata.getKey("folderselectortitle", subjectName));
		folderSelector.setCurrentDirectory(subjectPath==null ? (jDirChooserCurrentDir!=null ? jDirChooserCurrentDir :  new File(".")) : new File(subjectPath));
		folderSelector.setAcceptAllFileFilterUsed(false);
		folderSelector.setMultiSelectionEnabled(false);
		
		int folderSelectionResult=folderSelector.showOpenDialog(subjectsFrame);
		
		if(folderSelectionResult==JFileChooser.APPROVE_OPTION){
			
			// Folder successfully selected
			
			String selectedFolder=folderSelector.getSelectedFile().getAbsolutePath();
			
			// For the next time this menu is opened
			jDirChooserCurrentDir=folderSelector.getCurrentDirectory();
			
			// Actions with the folder
			
			System.out.println(selectedFolder);
			
			settings.setSubjectPath(subjectName, selectedFolder);
			
			subjectPath=selectedFolder;
			
			lblSubjectFolder.setText(subjectPath);
			
			activateInterface();

			setDownloadButtonsText();
			
		}
		
	}
	
	private static void askToSelectSubjectFolder(){

		String subjectName=subjectList.get(selectedSubject).getName();
		
		int questionResult = JOptionPane.showConfirmDialog(subjectsFrame,textdata.getKey("subjectwithoutfoldererror", subjectName),
				textdata.getKey("subjectwithoutfoldererrortitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if(questionResult == JOptionPane.YES_OPTION){
			
			selectSubjectFolder();
			
		}

	}
	
	private static void updateDownloadMarkedText(){
		
		if(cArchivos==null){
			
			btnDescargarMarcados.setText(textdata.getKey("btndownloadmarked",""));
			
		} else{
			
			int markedChecks=0;
			
			for(JCheckBox checkbox : cArchivos){
				if(checkbox.isSelected()) markedChecks++;
			}
			
			btnDescargarMarcados.setText(textdata.getKey("btndownloadmarked"," (" + markedChecks + ")"));
			
			nFilesToDownload=markedChecks;
			
		}
		
	}
	
	
	private static void doAtActivation(){
		
		subjectList=online ? faitic.faiticSubjects(mainDocument) : OfflineFaitic.getOfflineSubjectList(username);
		
		if(online && offlinesaving) OfflineFaitic.setOfflineSubjectList(username, subjectList);
		
		String[] subjects=new String[subjectList.size()];
		
		for(int i=0; i<subjects.length; i++){
			
			subjects[i]=subjectList.get(i).getName();
			
		}
		
		fillSubjects(subjects);
		
		//activateInterface();
		
		panelOptions.setVisible(false);
		
		if(!online) subjectsFrame.setTitle(textdata.getKey("subjectsframetitleoffline"));
		
		//fillFilesFromSubject(new String[]{"Archivo falso 1", "Esto no es falso :3"});
		
	}
	
	private static void todowhenclosing(){

		isLoading=true;
		
		blockInterface();
		panelLoading.setVisible(true);
		writeLoadingText(textdata.getKey("loadingclosingsession"));
		
		SwingWorker thread=new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				
				try {

					settings.saveSettings();
					
					if(selectedSubject>=0 && subjectURL!=null && subject!=null && online)
						faitic.logoutSubject(subjectURL, subject.getDocument(), subjectType);
					
					
					if(online) faitic.faiticLogout(mainDocument);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				return null;
			}
			
			@Override
			protected void done(){

				// Stop the timer and make the panel invisible for the loading animation
				
				panelLoading.setVisible(false);
				
				if(timer!=null){
					timer.cancel();
					timer.purge();
					timer=null;
				}

				if(justloggingout){
					
					// Reset the variables
					
					justloggingout=false;
					
					selectedSubject=-1;
					prevSelectedSubject=-1;
					isLoading=false;
					
					// Dispose
					
					subjectsFrame.dispose();
					
					LoginGUI window = new LoginGUI(verbose);
					window.loginFrame.setVisible(true);
									
				} else{
					
					subjectsFrame.dispose();
					
				}
				
				//System.exit(0);
				
			}
			
		};
		
		thread.execute();
		
	}
	
	/**
	 * Create the application.
	 */
	public SubjectsGUI(TextData td) {
		textdata=td;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		subjectsFrame = new JFrame();
		subjectsFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(SubjectsGUI.class.getResource("/daraujo/faiticchecker/icon.png")));
		subjectsFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if(isLoading) return;
				todowhenclosing();
			}
			@Override
			public void windowOpened(WindowEvent e) {

				doAtActivation();
				
				subjectsFrame.setSize(subjectsFrame.getWidth()+1, subjectsFrame.getHeight()+1);

				subjectsFrame.setSize(subjectsFrame.getWidth()-1, subjectsFrame.getHeight()-1);

			}
		});
		subjectsFrame.getContentPane().setBackground(Color.WHITE);
		subjectsFrame.setTitle(textdata.getKey("subjectsframetitle"));

		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		subjectsFrame.setBounds(screenSize.getWidth()>900 ? (int)(screenSize.getWidth()-900)/2 : 0,screenSize.getHeight() > 700 ? (int)(screenSize.getHeight()-700)/2 : 0, 900, 700);
		
		subjectsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		panelEverything = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){

				// Inherited
				super.paintComponent(g);
				
				// Parameters
				//Color borderColor=new Color(0,110,198,255);
				Color borderColor=new Color(110,110,110,255);
				
				int panellogoheight=panelLogos != null ? panelLogos.getHeight()-1 : 100;
				int panelsubjectswidth=panelSubjects != null ? panelSubjects.getWidth()-1 : 220;
				int paneloptionsheight=panelLoading != null ? panelLoading.getHeight() : 50;
				
				int subjectypos=panellogoheight+10, subjectheight=0;
				
				int minsubjectypos=panellogoheight+1;
				int maxsubjectypos=super.getHeight()-paneloptionsheight;
				
				if(selectedSubject>=0)
					if(lblSubjects[selectedSubject]!=null && scrollPane_1!=null){
					
						// Subject selected and label reachable
						
						subjectypos=lblSubjects[selectedSubject].getY() + panellogoheight-(int)scrollPane_1.getViewport().getViewPosition().getY();
						subjectheight=lblSubjects[selectedSubject].getHeight();
						
				}
						
				// Background
				
//				g.setColor(new Color(239,244,248,120));
//				for(int i=panellogoheight; i<super.getHeight(); i++){
//					g.drawLine(0, i, super.getWidth(), i);
//				}
//				
				
				for(int i=panellogoheight; i<super.getHeight(); i++){
					g.setColor(new Color(195,209,220, 0+(i-panellogoheight)*120/(super.getHeight()-panellogoheight) ));
					g.drawLine(0, i, super.getWidth(), i);
				}
				
				
//				Random random=new Random(1);
//				
//				for(int i=panellogoheight; i<super.getHeight(); i++){
//					
//					g.setColor(new Color(226,231,234,60+random.nextInt(100)));
//					g.drawLine(0, i, super.getWidth(), i);
//					
//				}
				

				// Header
				
				g.setColor(borderColor);
				g.drawLine(0, panellogoheight, panelsubjectswidth, panellogoheight);
				
				g.setColor(new Color(200,200,200,255));
				g.drawLine(panelsubjectswidth+1, panellogoheight, super.getWidth(), panellogoheight);

				// Shadows
				
				for(int i=0; i<5; i++){

					g.setColor(new Color(176,180,182,140*(5-i)/5));
					g.drawLine(0, panellogoheight+i, panelsubjectswidth-i-1, panellogoheight+i);
					
				}
				
				
				
				// Subjects and options
				
				// Subject area
				for(int i=panellogoheight+1; i<super.getHeight()-paneloptionsheight; i++){
					
					g.setColor(Color.white);
					
					g.drawLine(panelsubjectswidth, i, super.getWidth(), i);
					
				}
				
				// Borders
				
				g.setColor(borderColor);
				
				g.drawLine(panelsubjectswidth, panellogoheight, panelsubjectswidth, super.getHeight()-paneloptionsheight);
				g.drawLine(panelsubjectswidth, super.getHeight()-paneloptionsheight, super.getWidth(), super.getHeight()-paneloptionsheight);
				

				for(int i=0; i<5; i++){
					
					// Shadows for the subject place
					g.setColor(new Color(176,180,182,140*(5-i)/5));
					
					if(selectedSubject>=0){
						
						if(panellogoheight+i+1 < subjectypos-i-1-8){

							g.drawLine(panelsubjectswidth-i-1, panellogoheight+i+1, panelsubjectswidth-i-1,
									mustbebetween(subjectypos-i-1-8,minsubjectypos,maxsubjectypos,super.getHeight()-paneloptionsheight+i+1));
							
						}
						
						if(subjectypos+subjectheight+i+1+8 < super.getHeight()-paneloptionsheight+i+1){
							
							g.drawLine(panelsubjectswidth-i-1,
									mustbebetween(subjectypos+subjectheight+i+1+8, minsubjectypos, maxsubjectypos, panellogoheight+i+1),
									panelsubjectswidth-i-1, super.getHeight()-paneloptionsheight+i+1);
							
						}
						
					} else{

						g.drawLine(panelsubjectswidth-i-1, panellogoheight+i+1, panelsubjectswidth-i-1, super.getHeight()-paneloptionsheight+i+1);
						
					}
					
					g.drawLine(panelsubjectswidth-i, super.getHeight()-paneloptionsheight+i+1, super.getWidth(), super.getHeight()-paneloptionsheight+i+1);
					
				}
				
				
				// Subjects
				
				if(selectedSubject>=0)
					if(lblSubjects[selectedSubject]!=null){
					
						// Subject selected and label reachable

						int before=mustbebetween(subjectypos-8, minsubjectypos, maxsubjectypos, minsubjectypos);
						int after=mustbebetween(subjectypos+subjectheight+8, minsubjectypos, maxsubjectypos, maxsubjectypos);
						
						if(before!=minsubjectypos || after!=maxsubjectypos) // Not covering all the subjects
							for(int i = before; i< after; i++){
							
							g.setColor(Color.white);
							g.drawLine(0, i, panelsubjectswidth, i);
							
						}

						for(int i=0; i<5; i++){
							
							// Shadows for the subject place
							g.setColor(new Color(176,180,182,140*(5-i)/5));
							
							before=mustbebetween(subjectypos-8-i-1, minsubjectypos, maxsubjectypos, -1);
							after=mustbebetween(subjectypos+subjectheight+8+i+1, minsubjectypos, maxsubjectypos, -1);
						
							g.drawLine(0, before, panelsubjectswidth-i-1, before);
							g.drawLine(0, after, panelsubjectswidth-i-1, after);
							
						}
						
						g.setColor(borderColor);
						
						before=mustbebetween(subjectypos-8, minsubjectypos, maxsubjectypos, -1);
						after=mustbebetween(subjectypos+subjectheight+8, minsubjectypos, maxsubjectypos, -1);
					
						g.drawLine(0, before, panelsubjectswidth, before);
						g.drawLine(0, after, panelsubjectswidth, after);
						
						
					
				}
				
			}
			
		};
		panelEverything.setOpaque(false);
		subjectsFrame.getContentPane().add(panelEverything, BorderLayout.CENTER);
		panelEverything.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("100px"),
				FormFactory.GLUE_ROWSPEC,
				RowSpec.decode("max(20px;min)"),}));
		
		panelLogos = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				// Inherited
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

			    //g2.setComposite(AlphaComposite.Src);
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
				// Parameters
				int imgheight=super.getHeight()-25*2;
				int imgwidth=imgheight*imgFaicheck.getWidth(null)/imgFaicheck.getHeight(null);
				
				//System.out.println(imgwidth);
				
				int logowidth=panelLogoSpace != null ? (int) panelLogoSpace.getMinimumSize().getWidth() : 260;
				
				g2.drawImage(imgFaicheck, 15, 25, imgwidth, imgheight, null);
				
				// Divider
				
				//g.setColor(new Color(200,200,200,255));
				//g.drawLine(logowidth, 0, logowidth, super.getHeight()-2);

				
			}
			
		};
		
		panelLogos.setOpaque(false);
		panelEverything.add(panelLogos, "1, 1, fill, fill");
		panelLogos.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.MIN_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		panelLogoSpace = new JPanel();
		panelLogoSpace.setMinimumSize(new Dimension(252, 10));
		panelLogoSpace.setOpaque(false);
		panelLogos.add(panelLogoSpace, "1, 1, 1, 7, fill, fill");
		
		panel = new JPanel();
		panel.setOpaque(false);
		panelLogos.add(panel, "3, 2, 6, 1, fill, fill");
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,},
			new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		btnSchedule = new JPanel(){
			 
			@Override
			public void paintComponent(Graphics g){
				
				Graphics2D g2=(Graphics2D) g;
				
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    //g2.drawImage(iconSchedule, 0, 0, getWidth(), getHeight(), null);
			    
			    g2.setColor(new Color(0,110,198,255));
			    g2.fillRoundRect(getWidth()/8, getHeight()/8+1, getWidth()*6/8, getHeight()*6/8, 5, 5);
			    
			    g2.setColor(new Color(255,255,255,255));
			    g2.fillRect(getWidth()/8+2, getHeight()/8+7, getWidth()*6/8-4, getHeight()*6/8-8);

			    g2.setColor(new Color(0,110,198,255));
			    g2.fillRoundRect(getWidth()/8+3, getHeight()/12, 3, 5, 2, 2);
			    g2.fillRoundRect(getWidth()*7/8-7, getHeight()/12, 3, 5, 2, 2);
			    
			}
			
		};
		btnSchedule.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getComponent().isEnabled()){
					
					ScheduleViewerGUI scheduleviewergui=new ScheduleViewerGUI(textdata);
					
					scheduleviewergui.username=username;
					scheduleviewergui.frmScheduleViewer.setVisible(true);
					
				}

				
			}
		});
		
		lblSubjectName = new JLabel(textdata.getKey("selectsubject"));
		panel.add(lblSubjectName, "1, 1, 1, 3");
		lblSubjectName.setForeground(new Color(33,33,33,255));
		lblSubjectName.setFont(new Font("Dialog", Font.PLAIN, 23));
		btnSchedule.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSchedule.setPreferredSize(new Dimension(30, 30));
		btnSchedule.setOpaque(false);
		panel.add(btnSchedule, "5, 2, fill, fill");
		

		btnLogout = new JPanel(){
			 
			@Override
			public void paintComponent(Graphics g){
				
				Graphics2D g2=(Graphics2D) g;
				
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    //g2.drawImage(iconSchedule, 0, 0, getWidth(), getHeight(), null);
			    //g2.setColor(Color.gray);
			    //g2.fillRect(0, 0, getWidth(),getHeight());
			    
			    g2.setColor(new Color(0,110,198,255));
			    g2.setStroke(new BasicStroke(2));
			    
			    g2.drawArc(getWidth()/6, getHeight()/6, getWidth()*4/6, getHeight()*4/6, 120, 300);
			    g2.drawLine(getWidth()/2, getHeight()/12, getWidth()/2, getHeight()/2);
			    
			}
			
		};
		btnLogout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getComponent().isEnabled() && !isLoading){
					
					justloggingout=true;
					
					todowhenclosing();
					
				}

				
			}
		});
		btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnLogout.setPreferredSize(new Dimension(30, 30));
		btnLogout.setOpaque(false);
		panel.add(btnLogout, "7, 2, fill, fill");
		
		btnSearch = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				Graphics2D g2=(Graphics2D) g;
				
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    //g2.drawImage(iconSearch, 0, 0, getWidth(), getHeight(), null);

			    g2.setColor(new Color(0,110,198,255));
			    g2.setStroke(new BasicStroke(2));

			    g2.drawOval(getWidth()/8, getHeight()/8+1, getWidth()*7/12, getWidth()*7/12);
			    g2.drawLine(18, 18, 25, 25);
			    

			}
			
		};
		btnSearch.setPreferredSize(new Dimension(30, 30));
		panel.add(btnSearch, "3, 2");
		btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSearch.setVisible(false);
		btnSearch.setOpaque(false);
		btnSearch.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent arg0){
				
				if(arg0.getComponent().isEnabled()){
					
					panelSearch.setVisible(!panelSearch.isVisible());
					
					if(panelSearch.isVisible()){
						
						txtSearch.requestFocus();
						
					} else{
						
						txtSearch.setText("");
						
					}
					
				}

			}
			
		});
		
		lblProperties = new JLabel("");
		lblProperties.setForeground(new Color(117,117,117,255));
		panelLogos.add(lblProperties, "4, 4, 5, 1");
		
		lblSubjectFolder = new JLabel("");
		lblSubjectFolder.setForeground(new Color(117,117,117,255));
		lblSubjectFolder.setHorizontalAlignment(SwingConstants.LEFT);
		lblSubjectFolder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblSubjectFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getComponent().isEnabled()){

					if(selectedSubject>=0 && subjectPath!=null){
						
						if(new File(subjectPath).exists())
							if(new File(subjectPath).isDirectory())
								try {
									
									// Exists and is directory, so let's open it
									
									Desktop.getDesktop().open(new File(subjectPath));
									
								} catch (IOException e) {

									e.printStackTrace();
									
								}
						
					}
					
				}
				
			}
		});
		
		panelLogos.add(lblSubjectFolder, "4, 6");
		
		itemSelectSubjectFolder = new JLabel(textdata.getKey("btnchoosesubjectfolder"));
		itemSelectSubjectFolder.setForeground(new Color(0,110,198,255));
		itemSelectSubjectFolder.setVisible(false);
		itemSelectSubjectFolder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		itemSelectSubjectFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getComponent().isEnabled()){

					selectSubjectFolder();
					selectNotDownloadedFiles();
					
				}
				
			}
		});
		panelLogos.add(itemSelectSubjectFolder, "8, 6");
		
		panelLoading = new JPanel(){
			
			public int angle=0;
			public int angleBase=0;
			public boolean filled=false;
			
			TimerTask task=null;
			
			@Override
			public void paintComponent(Graphics g){
				
				//super.paintComponent(g);
				
				// Timer
				if(super.isVisible() && timer==null){
					task=new TimerTask(){

						@Override
						public void run() {
							
							int percent=getloadingpercent();
							
							if(percent<0){
								
								if(angle%20!=0) angle=0;
								if(angleBase%4!=0) angleBase=0;
									
								angle+=20;
								angleBase+=4;
								
								if(angle>=360){
									
									angle-=360;
									filled=!filled;
									
								}
								
								if(angleBase>=360) angleBase-=360;

							} else{
								
								filled=false;
								angleBase=-90;
								angle=360*percent/100;
								
							}
							
							//System.out.println(angle);
							panelLoading.repaint();
							
						}
						
					};
					
					timer=new Timer();
					timer.scheduleAtFixedRate(task, 80, 80);
					//System.out.println("Started");
				}
				
				// Painting
				
				Graphics2D g2 = (Graphics2D) g;

			    //g2.setComposite(AlphaComposite.Src);
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
			    g2.setColor((Color)(SystemColor.textText).brighter());
			    
			    String text=readLoadingText();
			    
			    Font textFont=new Font("Dialog", Font.PLAIN, 14);
			    g2.setFont(textFont);
			    
			    Rectangle2D textBounds=getFontMetrics(textFont).getStringBounds(text, g2);
			    // int loadingSide=super.getHeight()/2;
			    
			    //g2.drawString(text, (int)(super.getHeight()-loadingSide)/2 + loadingSide + 10, (int)(super.getHeight()+textBounds.getHeight())/2);
			    g2.drawString(text, (int)(super.getWidth()-textBounds.getWidth())/2, (int)(super.getHeight()+textBounds.getHeight()-4)/2);
			    
				//g2.setColor(new Color(255,171,43,255));
			    /*
			    g2.setColor(new Color(0,110,198,255));
				g2.setStroke(new BasicStroke(4));
				
				g2.drawArc((int)(super.getHeight()-loadingSide)/2, (int)(super.getHeight()-loadingSide)/2, loadingSide, loadingSide, -angleBase, !filled ? -angle : 360-angle);
				*/
			    
				g.setColor(new Color(0,110,198,255));
				g.fillRect(!filled ? 0 : angle*super.getWidth()/360, super.getHeight()-4, !filled ? angle*super.getWidth()/360 : (360-angle)*super.getWidth()/360, 4);
				
			}
			
		};
		panelLoading.addComponentListener(new ComponentAdapter(){
			
			@Override
			public void componentHidden(ComponentEvent e){
				
				if(timer!=null){
					timer.cancel();
					timer.purge();
					timer=null;
				}
				
			}
			
		});
		panelLoading.setMinimumSize(new Dimension(10, 40));
		panelLoading.setVisible(false);
		
		splitPane = new JSplitPane();
		splitPane.setDividerSize(6);
		splitPane.setOpaque(false);
		splitPane.setBorder(null);
		
		((BasicSplitPaneDivider)splitPane.getComponent(2)).setBorder(null);
		((BasicSplitPaneDivider)splitPane.getComponent(2)).setBackground(new Color(255,255,255,0));
		((BasicSplitPaneDivider)splitPane.getComponent(2)).addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {

				//panelLogoSpace.setMinimumSize(new Dimension(splitPane.getDividerLocation(),10));
				panelEverything.repaint();
				//panelLogos.repaint();
				
				
				
			}
			
		});
		
		panelEverything.add(splitPane, "1, 2, fill, fill");
		
		panelSubject = new JPanel();
		splitPane.setRightComponent(panelSubject);
		panelSubject.setOpaque(false);
		panelSubject.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,},
			new RowSpec[] {
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PREF_ROWSPEC,}));
		
		panelSearch = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				Color borderColor=new Color(200,200,200,255);
				Color borderColor2=new Color(170,170,170,255);
			    Color borderColorShadow=new Color(50,50,50,5);
			    
			    int margintop=5;
			    int marginleft=20;
			    
				g.setColor(borderColor);
				g.drawLine(0, super.getHeight()-1, super.getWidth(), super.getHeight()-1);
				

				Graphics2D g2=(Graphics2D) g;
				
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    g2.setColor(borderColorShadow);
			    
			    for(int i=0; i<5; i++){
			    	
			    	g.fillRoundRect(i+marginleft, i+margintop, super.getWidth()-4*i/3-2*marginleft, super.getHeight()-4*i/3-2*margintop, 5+6-i, 5+6-i);
			    	
			    }
			    
			    g.setColor(Color.white);
			    g.fillRoundRect(2+marginleft, 2+margintop, super.getWidth()-6-2*marginleft, super.getHeight()-6-2*margintop, 5, 5);
			    
			    g.setColor(borderColor2);
			    g.drawRoundRect(2+marginleft, 2+margintop, super.getWidth()-6-2*marginleft, super.getHeight()-6-2*margintop, 5, 5);
				
				
			}
			
		};
		panelSearch.setVisible(false);
		panelSearch.setOpaque(false);
		panelSubject.add(panelSearch, "1, 1, fill, fill");
		panelSearch.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("15dlu"),
				FormFactory.GLUE_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("18pt"),
				ColumnSpec.decode("15dlu"),},
			new RowSpec[] {
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.PARAGRAPH_GAP_ROWSPEC,}));
		
		txtSearch = new JTextField(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				if(getText().length()<=0){
					
					g.setColor(new Color(200,200,200,255));
					g.setFont(getFont());
					
					g.drawString(textdata.getKey("lblSearch"), 0, getFontMetrics(getFont()).getAscent());
					
				}
				
			}
			
		};
		txtSearch.setBackground(Color.WHITE);
		txtSearch.setBorder(null);
		txtSearch.setFont(new Font("Dialog", Font.PLAIN, 18));
		txtSearch.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				textChanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				textChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textChanged();
			}
			
			public void textChanged(){
				
				if(fileList!=null && !isLoading){
					
					// Previous selection
					
					boolean[] prevSelected=new boolean[cArchivos.length];
					for(int i=0; i<prevSelected.length; i++){
						
						if(cArchivos[i]!=null){
							
							prevSelected[i]=cArchivos[i].isSelected();
							
						} else{
							
							prevSelected[i]=false;
							
						}
						
					}
					
					// Fill subjects
					
					fillFilesFromSubject(txtSearch.getText());
					
					// Recover the selection
					
					for(int i=0; i<prevSelected.length; i++){
						
						if(cArchivos[i]!=null){
							
							cArchivos[i].setSelected(prevSelected[i]);
							
						}
						
					}
					
					if(panelSections.isVisible()){
						
						selectsectionbutton(lblFiles);
						
					}
					
				}
				
				btnDeleteSearch.repaint();

			}
			
			
		});

		panelSearch.add(txtSearch, "2, 2, fill, default");
		txtSearch.setColumns(10);
		
		btnDeleteSearch = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){

				Graphics2D g2=(Graphics2D) g;
				
			    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			    g2.setColor(txtSearch.getText().length()>0 ? new Color(0,110,198,255) : new Color(220,220,220,255));
			    g2.setStroke(new BasicStroke(2));
			    
			    int side=(getWidth()<getHeight() ? getWidth() : getHeight())-5;
			    
			    int heightdiff=getHeight()-side;
			    int widthdiff=getWidth()-side;
			    
			    g2.drawLine(widthdiff/2, heightdiff/2, widthdiff/2+side, heightdiff/2+side);
			    g2.drawLine(widthdiff/2+side, heightdiff/2, widthdiff/2, heightdiff/2+side);
			    
			    
			}
			
		};
		btnDeleteSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getComponent().isEnabled()){
					
					txtSearch.setText("");
					
				}
				
			}
		});
		btnDeleteSearch.setOpaque(false);
		btnDeleteSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelSearch.add(btnDeleteSearch, "4, 2, fill, fill");
		
		scrollPane = new JScrollPane();
		panelSubject.add(scrollPane, "1, 2, fill, fill");
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.white,new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
		scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.white,new Color(110,110,110,255),new Color(110,110,110,50)));
		scrollPane.setBorder(null);
		
		panelToDownload = new JPanel()/*{
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				if(lURLs==null || lArchivos==null) return;
				
				for(int i=1; i<lURLs.length; i++){
					
					int min = lURLs[i-1].getY() + lURLs[i-1].getHeight();
					int max = lArchivos[i].getY();
					
					g.setColor(new Color(70,70,70,30));
					g.drawLine(0, (max+min)/2+1, super.getWidth(), (max+min)/2+1);
					g.setColor(new Color(150,150,150,250));
					g.drawLine(0, (max+min)/2-3, super.getWidth(), (max+min)/2-3);
					
				}
				
				g.setColor(new Color(70,70,70,30));
				g.drawLine(0, 0, super.getWidth(), 0);
				g.drawLine(0, super.getHeight()-1, super.getWidth(), super.getHeight()-1);
				g.setColor(new Color(150,150,150,250));
				g.drawLine(0, super.getHeight()-2, super.getWidth(), super.getHeight()-2);
				
				
			}
			
		}*/;
		panelToDownload.setOpaque(false);
		scrollPane.setViewportView(panelToDownload);
		panelToDownload.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		lblSeleccioneUnaAsignatura = new JLabel(textdata.getKey("lblchooseasubject"));
		lblSeleccioneUnaAsignatura.setForeground(new Color(117,117,117,255));
		lblSeleccioneUnaAsignatura.setHorizontalAlignment(SwingConstants.CENTER);
		panelToDownload.add(lblSeleccioneUnaAsignatura, "2, 2");
		
		panelSections = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				Color borderColor=new Color(110,110,110,180);
				
				// Background

				for(int i=0; i<super.getHeight(); i++){
					g.setColor(new Color(220,220,220, i*150/getHeight() ));
					g.drawLine(0, i, super.getWidth(), i);
				}
				
				// Bottom bar
				
				g.setColor(borderColor);
				g.drawLine(0, 0, getWidth(), 0);
				
				for(int i=0; i<8; i++){
					
					g.setColor(new Color(110,110,110,(8-i)*40/8));
					g.drawLine(0, i, getWidth(), i);
					
					
				}
				
			}
			
		};
		panelSections.setVisible(false);
		FlowLayout fl_panelSections = (FlowLayout) panelSections.getLayout();
		fl_panelSections.setVgap(8);
		fl_panelSections.setHgap(10);
		panelSections.setOpaque(false);
		panelSubject.add(panelSections, "1, 4, fill, fill");
		
		lblIntroduction = new JLabel(textdata.getKey("sectionintroduction"));
		lblIntroduction.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getComponent().isEnabled()){
					
					fillWithHTML(htmlintroduction !=null ? htmlintroduction : "");
					
					selectsectionbutton(arg0.getComponent());
					
					if(offlinesaving) OfflineFaitic.setKey(username, subjectList.get(selectedSubject).getName(), "introductionread","1"); // Save as read if possible
					
					((JLabel)arg0.getComponent()).setIcon(null);
					
				}
				
			}
		});
		lblIntroduction.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblIntroduction.setForeground(new Color(0,110,198,255));
		panelSections.add(lblIntroduction);
		
		lblAnnouncements = new JLabel(textdata.getKey("sectionannouncements"));
		lblAnnouncements.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				if(arg0.getComponent().isEnabled()){
					
					fillWithHTML(htmlannouncements !=null ? htmlannouncements : "");
					
					selectsectionbutton(arg0.getComponent());
					
					if(offlinesaving) OfflineFaitic.setKey(username, subjectList.get(selectedSubject).getName(), "announcementsread","1"); // Save as read if possible

					((JLabel)arg0.getComponent()).setIcon(null);
					
				}
				
			}
		});
		lblAnnouncements.setForeground(new Color(0,110,198,255));
		lblAnnouncements.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelSections.add(lblAnnouncements);
		
		lblFiles = new JLabel(textdata.getKey("sectionfiles"));
		lblFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				if(arg0.getComponent().isEnabled()){
					
					fillFilesFromSubject();
					
					selectsectionbutton(arg0.getComponent());
					
				}
				
			}
		});
		panelSections.add(lblFiles);
		
		panelOptions = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				Color borderColor=new Color(110,110,110,180);
				
				g.setColor(borderColor);
				g.drawLine(0, 0, getWidth(), 0);
				
			}
			
		};
		
		panelSubject.add(panelOptions, "1, 3");
		panelOptions.setOpaque(false);
		panelOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 8));
		
		btnMarcarTodo = new JLabel(textdata.getKey("btnmarkall"));
		btnMarcarTodo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if(!e.getComponent().isEnabled()) return;
				
				if(fileList==null){

					JOptionPane.showMessageDialog(subjectsFrame, 
							textdata.getKey("subjectnotselectederror"), 
							textdata.getKey("subjectnotselectederrortitle"), JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				if(cArchivos==null) return;
				
				for(int i=0; i<cArchivos.length; i++){
					cArchivos[i].setSelected(cArchivos[i].isVisible());
				}
				
			}
		});
		btnMarcarTodo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnMarcarTodo.setForeground(new Color(0,110,198,255));
		panelOptions.add(btnMarcarTodo);
		
		btnMarcarNada = new JLabel(textdata.getKey("btnmarknone"));
		btnMarcarNada.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if(!e.getComponent().isEnabled()) return;
				
				if(fileList==null){

					JOptionPane.showMessageDialog(subjectsFrame, 
							textdata.getKey("subjectnotselectederror"), 
							textdata.getKey("subjectnotselectederrortitle"), JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				if(cArchivos==null) return;
				
				for(int i=0; i<cArchivos.length; i++){
					cArchivos[i].setSelected(false);
				}
				
			}
		});
		btnMarcarNada.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnMarcarNada.setForeground(new Color(0,110,198,255));
		panelOptions.add(btnMarcarNada);
		
		btnDescargarMarcados = new JCustomButton(textdata.getKey("btndownloadmarked",""));
		btnDescargarMarcados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				downloadFiles(-1,false);

			}
		});
		
		btnMarcarNuevos = new JLabel(textdata.getKey("btnmarknew"));
		btnMarcarNuevos.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if(!e.getComponent().isEnabled()) return;
				
				if(fileList==null){

					JOptionPane.showMessageDialog(subjectsFrame, 
							textdata.getKey("subjectnotselectederror"), 
							textdata.getKey("subjectnotselectederrortitle"), JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				if(subjectPath==null) askToSelectSubjectFolder();
				if(subjectPath==null) {

					return;
				}
				
				selectNotDownloadedFiles();
				//setDownloadButtonsText();
				
			}
		});
		
		btnMarcarNuevos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnMarcarNuevos.setForeground(new Color(0,110,198,255));
		
		panelOptions.add(btnMarcarNuevos);
		panelOptions.add(btnDescargarMarcados);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setOpaque(false);
		scrollPane_1.setBorder(null);
		scrollPane_1.getViewport().setOpaque(false);
		scrollPane_1.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane_1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				// TODO Auto-generated method stub
				
				panelEverything.repaint();
				
			}
			
		});
		
		scrollPane_1.getVerticalScrollBar().setUI(new CustomScrollBarUI(UIManager.getColor("Spinner.background"),new Color(110,110,110,255),UIManager.getColor("Spinner.background")));

		splitPane.setLeftComponent(scrollPane_1);
		
		panelSubjects = new JPanel();
		scrollPane_1.setViewportView(panelSubjects);
		panelSubjects.setOpaque(false);
		panelSubjects.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				RowSpec.decode("max(45px;pref)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(45px;pref)"),}));
		splitPane.setDividerLocation(252);
		panelLoading.setOpaque(false);
		panelEverything.add(panelLoading, "1, 3, fill, fill");
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
	}
}
