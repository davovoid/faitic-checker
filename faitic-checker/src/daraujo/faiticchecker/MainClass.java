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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainClass {

	protected static boolean verbose=false;

	/**
	 * Launch the application.
	 */
		
	public static void main(String[] args) {
		
		// Set UI
		
		try {
			
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Get args
		
		for(String arg : args){
						
			if(arg.toLowerCase().equals("--verbose"))
				verbose=true;
						
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
						
						LoginGUI window = new LoginGUI(verbose);
						window.loginFrame.setVisible(true);
					
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
