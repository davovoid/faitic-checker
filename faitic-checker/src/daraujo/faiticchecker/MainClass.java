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
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainClass {

	//TODO change this
	protected static boolean verbose=true;
	
	protected static boolean detectupdateenabled=true;

	/**
	 * Launch the application.
	 */
		
	public static void main(String[] args) {
		
		// Get args
		
		int i=0;
		
		while(i<args.length){
			
			String arg=args[i];
			
			if(arg.toLowerCase().equals("--verbose"))
				verbose=true;
			
			else if(arg.toLowerCase().equals("--update")){
				
				// Updating. Copy one file to the other
				String from=args[i+1];	// New
				String to=args[i+2];	// Previous
				i+=2;
				
				// BUG FIX: check from and to
				
				if(from.charAt(0)=='\\' && from.charAt(2)==':' && from.charAt(3)=='\\') from=from.substring(1, from.length());

				if(to.charAt(0)=='\\' && to.charAt(2)==':' && to.charAt(3)=='\\') to=to.substring(1, to.length());
				
				// END BUG FIX
				
				System.out.println(" - Copying file " + from + " to " + to);
				//JOptionPane.showMessageDialog(null, " - Copying file " + from + " to " + to);
				
				// File replacement
				
				int iterations=0;
				boolean replaced=false;
				
				while(!replaced && iterations++ <= 10){ // While not replaced and there are iterations available
					
					try {
						
						Files.copy(new File(from).toPath(), new File(to).toPath(), StandardCopyOption.REPLACE_EXISTING); // Can fail
						replaced=true; // Successfully replaced
						
					} catch (IOException e1) { // Not replaced
						
						e1.printStackTrace();
						
						try {
							
							TimeUnit.SECONDS.sleep(1);
							
						} catch (InterruptedException e) {
							
							e.printStackTrace();
							
							JOptionPane.showMessageDialog(null, "Iteration " + iterations + " out of 10: file coudn't be replaced." + (iterations <= 10 ? " File will be tried to be replaced again." : ""));
							
							
						}
						

					}
					

					
				}
				
				if(replaced){ // Replaced. It can be continued
					
					System.out.println(" File copied.");
					
					String toexecute=to;
					
					System.out.println(" - Executing " + toexecute);
					
					//JOptionPane.showMessageDialog(null, " - Executing " + toexecute);
							
					ProcessBuilder procUpdater=new ProcessBuilder();
					
					ArrayList<String> listaComandos=new ArrayList<String>();
					listaComandos.add("java");
					listaComandos.add("-jar");
					listaComandos.add(toexecute);
					listaComandos.add("--deleteupdate");
					listaComandos.add(from);
					procUpdater.command(listaComandos);
					
					try {
						
						procUpdater.start();
						
						return; // To stop execution
						
					} catch (IOException e) {

						e.printStackTrace();
						
					}
					
					
				}
				
				
			} else if(arg.toLowerCase().equals("--deleteupdate")){
				
				String todelete=args[i+1];
				i++;
				
				detectupdateenabled=false;
				
				System.out.println(" - Deleting update from " + todelete);
				//JOptionPane.showMessageDialog(null, " - Deleting update from " + todelete);
				
				// File deletion
				
				int iterations=0;
				boolean deleted=false;
				
				while(new File(todelete).exists() && !deleted && iterations++ <= 10){ // While not deleted and there are iterations available

					deleted=new File(todelete).delete(); // False if not deleted
						
					if(!deleted)
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
							JOptionPane.showMessageDialog(null, "Iteration " + iterations + " out of 10: file coudn't be deleted." + (iterations <= 10 ? " File will be tried to be deleted again." : ""));

						}
					
					
				}
				
				if(deleted){
					
					System.out.println(" Done.");
					//JOptionPane.showMessageDialog(null, 
					//		"Successfully updated.");

				}
					
				
			}
			
			
			i++;
			
		}

		// Set UI
		
		try {
			
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Open app
					
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					if(!ClassicRoutines.isThereAnyUserDataPath()){

						// No settings folder available. That means first execution.
						
						FirstStart dialog = new FirstStart();
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
						
					}
					
					if(ClassicRoutines.isThereAnyUserDataPath()){
					
						// If it is correctly configured
						
						// Check if there is waiting an update
						
						/*if(new File(ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/update.jar")).exists() && LoginGUI.isTheJarPathAFile() && detectupdateenabled){
							
							// An update waiting, do your best :3
							
							
							
						} else{
						*/
							// Open GUI
							
							LoginGUI window = new LoginGUI(verbose);
							window.loginFrame.setVisible(true);
						
						//}
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
