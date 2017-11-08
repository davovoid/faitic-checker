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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Shortcut {

	public static final int WINDOWS=0;
	public static final int LINUX=1;
	
	public static void saveFileFromResources(String resource, String file) throws IOException{

		InputStream reader=Shortcut.class.getResourceAsStream("/daraujo/faiticchecker/" + resource);
		
		FileOutputStream writer=new FileOutputStream(new File(file));
		
		byte[] temp=new byte[1024];
		int tempint=reader.read(temp); // Number of bytes read, -1 if EOF
		
		while(tempint>=0){
			
			writer.write(temp, 0, tempint); // Write data to file
			writer.flush();
			
			tempint=reader.read(temp); // Recursive
			
		}
		
		reader.close();
		writer.close();

	}

	public static String readFileFromResources(String resource){
	    
		StringBuffer textread=new StringBuffer();
	
		InputStream reader=null;
		
		try{	// Possible file not found

			reader = Shortcut.class.getResourceAsStream("/daraujo/faiticchecker/" + resource);
		
			byte[] buffer=new byte[1000];		// Buffer for reading
			int bytesread=reader.read(buffer);	// Number of bytes read
		
			while(bytesread!=-1){				// If it keeps reading...
			
				textread.append(new String(buffer,0,bytesread,"UTF-8"));	// Read as UTF-8
				bytesread=reader.read(buffer);								// Refresh bytes read
			
			}
			
		} catch(Exception ex){
			
			ex.printStackTrace();
			
		} finally{
			
			try{ if(reader!=null) reader.close(); }			// Close the reader !!
			catch(Exception ex2){ ex2.printStackTrace(); }
			
		}
		
		return textread.toString();
		
	}


	public static void copyFile(String from, String to) throws IOException{

		InputStream reader=new FileInputStream(new File(from));
		
		FileOutputStream writer=new FileOutputStream(new File(to));
		
		byte[] temp=new byte[1024];
		int tempint=reader.read(temp); // Number of bytes read, -1 if EOF
		
		while(tempint>=0){
			
			writer.write(temp, 0, tempint); // Write data to file
			writer.flush();
			
			tempint=reader.read(temp); // Recursive
			
		}
		
		reader.close();
		writer.close();

	}

	
	public static void createShortcut(String shortcutfolder){
		
		if(System.getProperty("os.name").toUpperCase().contains("WIN")){
			
			createShortcut(shortcutfolder, WINDOWS);
			
		} else if(System.getProperty("os.name").toUpperCase().contains("NUX")){

			createShortcut(shortcutfolder, LINUX);
			
		}
		
	}
	
	public static void createShortcut(String shortcutfolder, int os){
		
		if(!LoginGUI.isTheJarPathAFile()) return; // If not a file
		
		String shortcutfile=ClassicRoutines.cpath(shortcutfolder + "/Faicheck");
		String iconfile=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/icon");
		String jarpath=LoginGUI.getJarPath();
		
		// New way of creating shortcuts: self-copying to the config. folder and shortcutting that
		if(new File(jarpath).exists())
			if(new File(jarpath).isFile()){
				
				// Can be copied
				String jardestination=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/faicheck.jar");
				
				try {
					
					if(!new File(jardestination).exists()){
						
							copyFile(jarpath, jardestination);
	
					}
					
					jarpath=jardestination;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		
		if(os==WINDOWS){
			
			String vbsfile=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/shortcut.vbs");
			shortcutfile+=".lnk";
			iconfile+=".ico";
			
			createShortcutWindows(shortcutfile, iconfile, vbsfile, jarpath);
			
		} else if(os==LINUX){
			
			shortcutfile+=".desktop";
			iconfile+=".png";
			
			createShortcutLinux(shortcutfile, iconfile, jarpath);
			
			
		}
		
	}
	
	private static void createShortcutWindows(String shortcutfile, String iconfile, String vbsfile, String jarpath){
		
		try {
			
			saveFileFromResources("faicheckicon.ico", iconfile);
		
			String vbsscript = readFileFromResources("shortcut.vbs");
			
			vbsscript=vbsscript.replace("%0", jarpath).replace("%1", iconfile).replace("%2", shortcutfile);
			
			ClassicRoutines.writeFile(vbsfile, vbsscript);
			
			List<String> commandlist=new ArrayList<String>();
			commandlist.add("wscript");
			commandlist.add(vbsfile);
			
			Process executor=new ProcessBuilder(commandlist).start(); // The shortcut creator (VBS program)
			executor.waitFor(); // Wait for the program to end
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void createShortcutLinux(String shortcutfile, String iconfile, String jarpath){
		
		try {
			
			saveFileFromResources("icon.png", iconfile);
			
			String desktopfile = readFileFromResources("Faicheck.desktop");
			
			desktopfile = desktopfile.replace("%0", jarpath).replace("%1", iconfile);
			
			ClassicRoutines.writeFile(shortcutfile, desktopfile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
