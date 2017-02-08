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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.awt.SystemColor;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.awt.Toolkit;

import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ScrollPaneConstants;

public class SubjectsGUI {

	protected static JFrame subjectsFrame;
	
	protected static TextData textdata=new TextData(new Locale("es"));
	
	protected static Faitic faitic;
	protected static String mainDocument;
	protected static Settings settings;
	
	private static JPanel panelLogos, panelSubjects, panelSubject, panelOptions, panelEverything;
	
	private final static Image imgFaicheck=new ImageIcon(LoginGUI.class.getResource("/daraujo/faiticchecker/logoFaicheck.png")).getImage();
	
	private static JButton btnDescargarMarcados;
	private JButton btnMarcarNuevos;
	private JButton btnMarcarTodo;
	private JButton btnMarcarNada;
	
	private static JLabel[] lblSubjects;
	private static JCheckBox[] cArchivos;
	private static JLabel[] lArchivos;
	private static JButton[] btnAbrirArchivos;
	
	private static int selectedSubject=-1, prevSelectedSubject=-1;
	private static ArrayList<String[]> subjectList;
	private static String[] subject;
	private static int subjectType;
	private static String subjectURL;
	private static ArrayList<String[]> fileList;
	private static String subjectPath;
	
	private static File jDirChooserCurrentDir;
	
	private static JLabel lblSubjectName;
	private static JLabel lblProperties;
	private static JScrollPane scrollPane;
	private static JPanel panelToDownload;
	private JLabel lblSeleccioneUnaAsignatura;
	private static JLabel itemSelectSubjectFolder;
	private static JPanel panelLoading;
	
	protected static String loadingText="Loading...";
	protected static Semaphore accessToLoadingText=new Semaphore(1);
	protected static boolean isLoading=false;
	private JSplitPane splitPane;
	private JScrollPane scrollPane_1;
	private JPanel panelLogoSpace;
	private static JLabel lblSubjectFolder;
	private static JLabel lblOpenFolder;
	
	private static boolean descargando=false;
	
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
	
	private static void blockInterface(){

		panelLoading.setVisible(true);
		scrollPane.setVisible(false);
		for(Component comp : panelOptions.getComponents()){
			comp.setEnabled(false);
		}

		itemSelectSubjectFolder.setVisible(false);
		lblOpenFolder.setVisible(false);


	}
	
	private static void activateInterface(){

		itemSelectSubjectFolder.setVisible(selectedSubject>=0);
		lblOpenFolder.setVisible(subjectPath!=null && selectedSubject>=0);

		panelLoading.setVisible(false);
		scrollPane.setVisible(true);
		for(Component comp : panelOptions.getComponents()){
			comp.setEnabled(true);
		}
	}
	
	// The rest

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
						
						SwingWorker thread=new SwingWorker(){

							@Override
							protected Object doInBackground() throws Exception {
								
								try{
									

									if(prevSelectedSubject>=0 && subjectURL!=null && subject!=null) {
										
										// Important not to override values used by logout BEFORE logging out
										
										writeLoadingText(textdata.getKey("loadingclosingprevioussubject"));
										faitic.logoutSubject(subjectURL, subject[1], subjectType);
										
									}
									
									writeLoadingText(textdata.getKey("loadingopeningsubject"));
									
									subject=faitic.goToSubject(subjectList.get(selectedSubject)[0]);
									subjectType=faitic.subjectPlatformType(subject[0]);
									subjectURL=subject[0];
									String subjectName=subjectList.get(selectedSubject)[1];
									
									writeLoadingText(textdata.getKey("loadinglistingfiles"));
									
									if(subjectType == faitic.CLAROLINE){
										
										fileList = faitic.listDocumentsClaroline(subjectURL);
										
									}
									else if(subjectType == faitic.MOODLE){
											
										fileList = faitic.listDocumentsMoodle(faitic.lastRequestedURL);

									}else if(subjectType == faitic.MOODLE2){
										
									fileList = faitic.listDocumentsMoodle2(faitic.lastRequestedURL);

									} else{
										
										//Unknown
										if(fileList!=null) fileList.clear();
										else fileList=new ArrayList<String[]>();
										
									}

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
									
								}catch(Exception ex){
									
									ex.printStackTrace();
									
									//Error, so the subject will not be selected
									
									selectedSubject=-1;
									
									// And no files
									
									if(fileList!=null) fileList.clear();
									else fileList=new ArrayList<String[]>();
									
								}

								writeLoadingText(textdata.getKey("loadingdefaulttext"));
								
								return null;
							}
							
							@Override
							protected void done(){
								
								if(selectedSubject>=0){
									//Only if a subject is correctly selected

									// Preparing the menu, UI
									lblSubjectName.setText(subjectList.get(selectedSubject)[1]);

									if(subjectType == faitic.CLAROLINE){

										lblProperties.setText(textdata.getKey("nameclaroline"));

									}
									else if(subjectType == faitic.MOODLE){

										lblProperties.setText(textdata.getKey("namemoodle"));

									}else if(subjectType == faitic.MOODLE2){

										lblProperties.setText(textdata.getKey("namemoodle2"));

									} else{

										lblProperties.setText(textdata.getKey("nameunknown"));

									}

									if(subjectType!=faitic.UNKNOWN)
										lblProperties.setText(textdata.getKey("subjectsummary",lblProperties.getText(), fileList.size() + "", fileList.size()!=1 ? "s" : ""));

									String[] fileListNames=new String[fileList.size()];

									for(int i=0; i<fileList.size(); i++){
										fileListNames[i]=fileList.get(i)[0];
									}

									// Show subject path
									
									if(subjectPath != null){
										
										lblSubjectFolder.setText(subjectPath);
										
									} else{
										
										lblSubjectFolder.setText(textdata.getKey("labelsubjectfoldernotselected"));
										
									}
									
									// List files now, do after getting the subject path, UI
									fillFilesFromSubject(fileListNames);

								} else{
									
									// Clicked but selected -1: there was an error
									
									lblSubjectName.setText(textdata.getKey("erroropeningsubjecttitle"));
									lblProperties.setText(textdata.getKey("erroropeningsubjectsummary"));
									
									fillFilesFromSubject(new String[]{});
									
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
	
	private static void fillFilesFromSubject(String[] fileListString){
		
		panelToDownload.removeAll();
		panelToDownload.updateUI();
		
		RowSpec[] fRowSpec=new RowSpec[fileListString.length*2+3];
		
		for(int i=0; i<fRowSpec.length; i++){
			
			fRowSpec[i]= i==0 || i==fRowSpec.length-1 ? FormFactory.UNRELATED_GAP_ROWSPEC : i % 2 == 0 ? FormFactory.RELATED_GAP_ROWSPEC : FormFactory.PREF_ROWSPEC;
		}
		
		panelToDownload.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			fRowSpec));
		
		JLabel lbldescargar = new JLabel(textdata.getKey("filelistmarked"));
		panelToDownload.add(lbldescargar, "2, 2");
		
		JLabel lblArchivo = new JLabel(textdata.getKey("filelistfilename"));
		panelToDownload.add(lblArchivo, "4, 2");
		
		cArchivos=new JCheckBox[fileListString.length];
		lArchivos=new JLabel[fileListString.length];
		btnAbrirArchivos=new JButton[fileListString.length];
		
		for(int i=0; i<fileListString.length; i++){
			
			boolean isAlreadyDownloaded=fileIsAlreadyDownloaded(subjectPath, fileListString[i]);
			
			cArchivos[i]=new JCheckBox("");
			cArchivos[i].setOpaque(false);
			cArchivos[i].setHorizontalAlignment(SwingConstants.CENTER);
			cArchivos[i].setSelected(!isAlreadyDownloaded); // Not selected if downloaded
			
			// Checked changed
			cArchivos[i].addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					
					updateDownloadMarkedText();	// "Download marked files" button text set
					
				}
				
			});
			
			panelToDownload.add(cArchivos[i], "2, " + (int)(i*2+4));
			
			lArchivos[i]=new JLabel(fileListString[i]);
			lArchivos[i].setFont(new Font("Monospaced", Font.PLAIN, 12));
			lArchivos[i].addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent arg0) {

					int index=-1;
					
					for(int i=0; i<lArchivos.length; i++){
						
						if(lArchivos[i].equals(arg0.getComponent())) index=i;
						
					}
					
					if(index>=0){
						
						// Detected the element clicked
						
						cArchivos[index].setSelected(!cArchivos[index].isSelected());
						
					}
					
					
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
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			panelToDownload.add(lArchivos[i], "4, " + (int)(i*2+4));
			
			btnAbrirArchivos[i]=new JButton(isAlreadyDownloaded ? textdata.getKey("filelistopen") : textdata.getKey("filelistdownload"));
			
			btnAbrirArchivos[i].addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
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
							
							String fileName=new File(ClassicRoutines.cpath(fileList.get(myFileIndex)[0])).getName();
							
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
								
								String fileURL=fileList.get(myFileIndex)[1];
								
								try {
									
									faitic.downloadFile(fileURL, "", selectedFile);
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							
							
							
							
						} else{
							
							// Subject path selected

							String fileRelPath=fileList.get(myFileIndex)[0];
							
							if(fileIsAlreadyDownloaded(subjectPath,fileRelPath)){
								
								// Already downloaded. Open it
								
								try {
									
									Desktop.getDesktop().open(new File(fileDestination(subjectPath,fileRelPath)));
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							} else{
								
								// Not downloaded yet. Download it
								
								downloadFileFromList(myFileIndex);
								setDownloadButtonsText();
								
								
							}
							
						}
						
					}
					
				}
				
			});
			
			panelToDownload.add(btnAbrirArchivos[i], "6, " + (int)(i*2+4));
			
		}
		
		updateDownloadMarkedText();	// Download marked button text set
		
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
			
			String fileRelPath=fileList.get(i)[0];
			
			cArchivos[i].setSelected(!fileIsAlreadyDownloaded(subjectPath, fileRelPath));
			
		}
		
	}
	
	private static void setDownloadButtonsText(){
		
		if(fileList==null) return;
		
		for(int i=0; i<fileList.size(); i++){
			
			String fileRelPath=fileList.get(i)[0];
			
			btnAbrirArchivos[i].setText(fileIsAlreadyDownloaded(subjectPath, fileRelPath) ? textdata.getKey("filelistopen") : textdata.getKey("filelistdownload"));
			
		}
		
	}
	
	private static void downloadFileFromList(int i){

		// To be downloaded
		
		String fileRelPath=fileList.get(i)[0];
		String whereToDownloadTheFile=fileList.get(i)[1];
		
		String strFileDestination=ClassicRoutines.createNeededFolders(fileDestination(subjectPath, fileRelPath));
		
		try {
			
			faitic.downloadFile(whereToDownloadTheFile, "", strFileDestination);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private static void selectSubjectFolder(){

		String subjectName=subjectList.get(selectedSubject)[1];
		
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

		String subjectName=subjectList.get(selectedSubject)[1];
		
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
			
			int numMarcados=0;
			
			for(JCheckBox checkbox : cArchivos){
				if(checkbox.isSelected()) numMarcados++;
			}
			
			btnDescargarMarcados.setText(textdata.getKey("btndownloadmarked"," (" + numMarcados + ")"));
			
		}
		
	}
	
	
	private static void doAtActivation(){
		
		subjectList=faitic.faiticSubjects(mainDocument);
		
		String[] subjects=new String[subjectList.size()];
		
		for(int i=0; i<subjects.length; i++){
			
			subjects[i]=subjectList.get(i)[1];
			
		}
		
		fillSubjects(subjects);
		
		//fillFilesFromSubject(new String[]{"Archivo falso 1", "Esto no es falso :3"});
		
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
				
				isLoading=true;
				
				blockInterface();
				panelLoading.setVisible(true);
				writeLoadingText(textdata.getKey("loadingclosingsession"));
				
				SwingWorker thread=new SwingWorker(){

					@Override
					protected Object doInBackground() throws Exception {
						
						try {

							settings.saveSettings();
							
							if(selectedSubject>=0 && subjectURL!=null && subject!=null)
								faitic.logoutSubject(subjectURL, subject[1], subjectType);
							
							
							faitic.faiticLogout(mainDocument);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						return null;
					}
					
					@Override
					protected void done(){
						
						subjectsFrame.dispose();
						System.exit(0);
						
					}
					
				};
				
				thread.execute();
				
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
				Color borderColor=new Color(0,110,198,255);
				
				int panellogoheight=panelLogos != null ? panelLogos.getHeight()-1 : 100;
				int panelsubjectswidth=panelSubjects != null ? panelSubjects.getWidth()-1 : 220;
				int paneloptionsheight=panelOptions != null ? panelOptions.getHeight() : 50;
				
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
				
				g.setColor(new Color(239,244,248,120));
				for(int i=panellogoheight; i<super.getHeight(); i++){
					g.drawLine(0, i, super.getWidth(), i);
				}

				// Header
				
				g.setColor(borderColor);
				g.drawLine(0, panellogoheight, panelsubjectswidth, panellogoheight);
				
				g.setColor(new Color(200,200,200,255));
				g.drawLine(panelsubjectswidth+1, panellogoheight, super.getWidth(), panellogoheight);

				for(int i=0; i<3; i++){

					g.setColor(new Color(120,120,120,150*(3-i)/3));
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
				

				for(int i=0; i<3; i++){
					
					// Shadows for the subject place
					g.setColor(new Color(120,120,120,150*(3-i)/3));
					
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

						for(int i=0; i<3; i++){
							
							// Shadows for the subject place
							g.setColor(new Color(120,120,120,150*(3-i)/3));
							
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
				ColumnSpec.decode("220px:grow"),
				FormFactory.PREF_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("100px"),
				FormFactory.GLUE_ROWSPEC,
				FormFactory.MIN_ROWSPEC,}));
		
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
				
				g.setColor(new Color(200,200,200,255));
				g.drawLine(logowidth, 0, logowidth, super.getHeight()-2);

				
			}
			
		};
		
		panelLogos.setOpaque(false);
		panelEverything.add(panelLogos, "1, 1, 2, 1, fill, fill");
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
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		panelLogoSpace = new JPanel();
		panelLogoSpace.setMinimumSize(new Dimension(252, 10));
		panelLogoSpace.setOpaque(false);
		panelLogos.add(panelLogoSpace, "1, 1, 1, 7, fill, fill");
		
		lblSubjectName = new JLabel(textdata.getKey("selectsubject"));
		panelLogos.add(lblSubjectName, "3, 2, 6, 1");
		lblSubjectName.setFont(new Font("Dialog", Font.PLAIN, 23));
		
		lblProperties = new JLabel("");
		panelLogos.add(lblProperties, "4, 4, 5, 1");
		
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
		
		lblSubjectFolder = new JLabel("");
		lblSubjectFolder.setHorizontalAlignment(SwingConstants.LEFT);
		panelLogos.add(lblSubjectFolder, "4, 6");
		panelLogos.add(itemSelectSubjectFolder, "6, 6");
		
		lblOpenFolder = new JLabel(textdata.getKey("btnopensubjectfolder"));
		lblOpenFolder.setForeground(new Color(0,110,198,255));
		lblOpenFolder.setVisible(false);
		lblOpenFolder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblOpenFolder.addMouseListener(new MouseAdapter() {
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
		
		panelLogos.add(lblOpenFolder, "8, 6");
		
		panelLoading = new JPanel(){
			
			public Timer timer=null;
			
			public int angle=0;
			public int angleBase=0;
			public boolean filled=false;
			
			TimerTask task=new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					angle+=20;
					angleBase+=4;
					if(angle>=360){
						angle-=360;
						filled=!filled;
					}
					if(angleBase>=360) angleBase-=360;

					//System.out.println(angle);
					panelLoading.repaint();
					
				}
				
			};
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				// Timer
				if(super.isVisible() && timer==null){
					timer=new Timer();
					timer.scheduleAtFixedRate(task, 80, 80);
				} else if(!super.isVisible() && timer!=null){
					timer.cancel();
					timer=null;
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
			    int loadingSide=super.getHeight()/2;
			    
			    g2.drawString(text, (int)(super.getHeight()-loadingSide)/2 + loadingSide + 10, (int)(super.getHeight()+textBounds.getHeight())/2);
			    
				//g2.setColor(new Color(255,171,43,255));
			    g2.setColor(new Color(0,110,198,255));
				g2.setStroke(new BasicStroke(4));
				
				g2.drawArc((int)(super.getHeight()-loadingSide)/2, (int)(super.getHeight()-loadingSide)/2, loadingSide, loadingSide, -angleBase, !filled ? -angle : 360-angle);
				
			}
			
		};
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
		
		panelEverything.add(splitPane, "1, 2, 2, 1, fill, fill");
		
		panelSubject = new JPanel();
		splitPane.setRightComponent(panelSubject);
		panelSubject.setOpaque(false);
		panelSubject.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panelSubject.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.setBorder(null);
		
		panelToDownload = new JPanel();
		panelToDownload.setOpaque(false);
		scrollPane.setViewportView(panelToDownload);
		panelToDownload.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GLUE_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,}));
		
		lblSeleccioneUnaAsignatura = new JLabel(textdata.getKey("lblchooseasubject"));
		lblSeleccioneUnaAsignatura.setForeground(SystemColor.textInactiveText);
		lblSeleccioneUnaAsignatura.setHorizontalAlignment(SwingConstants.CENTER);
		panelToDownload.add(lblSeleccioneUnaAsignatura, "2, 2, 3, 1");
		
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
		
		panelOptions = new JPanel();
		panelOptions.setOpaque(false);
		panelEverything.add(panelOptions, "2, 3, fill, fill");
		panelOptions.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.PREF_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,}));
		
		btnDescargarMarcados = new JButton(textdata.getKey("btndownloadmarked",""));
		btnDescargarMarcados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

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
					descargando=true;
					btnDescargarMarcados.setEnabled(true);

					btnDescargarMarcados.setText(textdata.getKey("btncanceldownload"));

					writeLoadingText("Descargando...");

					SwingWorker thread=new SwingWorker(){

						@Override
						protected Object doInBackground() throws Exception {

							int nDownloadedFiles=0;

							for(int i=0; i<fileList.size(); i++){

								if(cArchivos[i].isSelected()){

									downloadFileFromList(i);

									writeLoadingText(textdata.getKey("loadingdownloading", ++nDownloadedFiles + ""));

								}

							}

							writeLoadingText(textdata.getKey("loadingdefaulttext"));

							return null;
						}

						@Override
						protected void done(){

							isLoading=false;

							selectNotDownloadedFiles();
							setDownloadButtonsText();

							activateInterface();

							faitic.setCancelDownload(false);
							btnDescargarMarcados.setEnabled(true);
							descargando=false;

						}

					};

					thread.execute();

				}

			}
		});
		panelOptions.add(btnDescargarMarcados, "2, 2");
		
		btnMarcarNuevos = new JButton(textdata.getKey("btnmarknew"));
		btnMarcarNuevos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

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
		panelOptions.add(btnMarcarNuevos, "4, 2");
		
		btnMarcarTodo = new JButton(textdata.getKey("btnmarkall"));
		btnMarcarTodo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(fileList==null){

					JOptionPane.showMessageDialog(subjectsFrame, 
							textdata.getKey("subjectnotselectederror"), 
							textdata.getKey("subjectnotselectederrortitle"), JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				if(cArchivos==null) return;
				
				for(int i=0; i<cArchivos.length; i++){
					cArchivos[i].setSelected(true);
				}
				
			}
		});
		panelOptions.add(btnMarcarTodo, "6, 2");
		
		btnMarcarNada = new JButton(textdata.getKey("btnmarknone"));
		btnMarcarNada.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

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
		panelOptions.add(btnMarcarNada, "8, 2");
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
	}
}
