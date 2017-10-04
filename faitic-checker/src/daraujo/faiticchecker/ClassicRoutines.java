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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;

public class ClassicRoutines {

	/**
	 * Classic routines: IO, etc.
	 */
	

	public final static String APPNAME="faitic_checker";
	
	public static boolean isPortable(){
		
		return getUserDataPath(true).equals(getUserDataPath(true,true));
		
	}
	
	public static String getUserDataPath(boolean useMyDataPath){

		String out=getUserDataPath(true,true);
		
		if(new File(out).exists()){
			
			return out;
			
		} else{
			
			return getUserDataPath(useMyDataPath,false);
			
		}
		
		
	}
	
	public static boolean isThereAnyUserDataPath(){

		if(new File(getUserDataPath(true,true)).exists()){
			
			return true;
			
		} else if(new File(getUserDataPath(true,false)).exists()){
			
			return true;
			
		} else return false;
		
	}
	
	public static String getJarPathFolder(){
		
		String jarPath=LoginGUI.getJarPath();
		return jarPath.substring(0, jarPath.lastIndexOf(cpath("/")));
		
	}
	
	public static String getRelativePath(String path, String folderAsReference){
		
		String divider=cpath("/");
		
		int indexofdivider=folderAsReference.lastIndexOf(divider) == folderAsReference.length()-1 ? folderAsReference.lastIndexOf(divider) : folderAsReference.length();
		// if it ends with a /, it is taken as the last /, if not everything is taken (/ in length())
		
		StringBuffer out=new StringBuffer();
		
		while(indexofdivider>=0){
			
			String result=folderAsReference.substring(0, indexofdivider) + divider;	// going to parents. Path WITH / !!!
			
			if(path.indexOf(result)==0){
				
				// It starts with the same string, that means it is relative to this folder
				
				out.append(path.substring(result.length(), path.length()));
				
				return out.toString();	// Ready
				
			} else{
				
				out.append("../");
				
			}
			
			indexofdivider=folderAsReference.lastIndexOf(divider, indexofdivider-1);
			
		}
		
		return path;	// No relative found
		
	}
	
	public static String getAbsolutePath(String relPath, String folderAsReference){
		

		String divider=cpath("/");
		
		int indexofdivider=folderAsReference.lastIndexOf(divider) == folderAsReference.length()-1 ? folderAsReference.lastIndexOf(divider) : folderAsReference.length();
		// if it ends with a /, it is taken as the last /, if not everything is taken (/ in length())
		
		String folderCorrected=folderAsReference.substring(0, indexofdivider) + divider;	// going to parents. Path WITH / !!!
		
		File path=new File(folderCorrected + relPath);
		
		try {
			
			return path.getCanonicalPath();
			
		} catch (IOException e) {

			e.printStackTrace();
			return path.getAbsolutePath();
			
		}
		
	}
	
	public static String getUserDataPath(boolean useMyDataPath, boolean portable){

		String out;
		
		if(!portable){
		
			String OS = System.getProperty("os.name").toUpperCase();
	    
			if (OS.contains("WIN")) out=cpath(System.getenv("APPDATA") + "/." + APPNAME);
			else if (OS.contains("MAC")) out=cpath(System.getProperty("user.home") + "/Library/Application Support/" + APPNAME);
			else if (OS.contains("NUX")) out=cpath(System.getProperty("user.home") + "/." + APPNAME);
			else out=cpath(getJarPathFolder() + "/." + APPNAME);
	    
		} else{
			
			out=cpath(getJarPathFolder() + "/" + APPNAME + "-settings");
			
		}
			
	    if(!useMyDataPath)
	    	return out.substring(0, out.lastIndexOf(cpath("/")));
	    else
	    	return out;
	    
	}
	

	/* Common functions */
	
	public static String cpath(String path){	// Corrects path mistakes and applies OS-specific separators

		String newpath;
		String sep;

		/* First of all replace to specific OS separators */
		
	    if (System.getProperty("os.name").toUpperCase().contains("WIN")){
	    
	    	newpath=path.replace("/", "\\");
	    	sep="\\";
	    	
	    } else{

	    	newpath=path.replace("\\", "/");
	    	sep="/";
	    	
	    }

	    /* Replace consecutive separators */
	    while(newpath.indexOf(sep+sep)>=0)
	    	newpath=newpath.replace(sep + sep, sep);
		
	    /* End */
	    
	    //System.out.println("Path referenced: " + newpath);
	    
	    return newpath;
	    

	}

	public static String readFile(String pathtofile){
		
	    System.out.println("File being read: " + pathtofile);
	    
		StringBuffer textread=new StringBuffer();
	
		InputStream reader=null;
		
		try{	// Possible file not found

			reader = new FileInputStream(pathtofile);
		
			byte[] buffer=new byte[1000];		// Buffer for reading
			int bytesread=reader.read(buffer);	// Number of bytes read
		
			while(bytesread!=-1){				// If it keeps reading...
			
				textread.append(new String(buffer,0,bytesread,"UTF-8"));	// Read as UTF-8
				bytesread=reader.read(buffer);								// Refresh bytes read
			
			}

		    System.out.println("File read successfully.");
		    
			
		} catch(Exception ex){
			
			ex.printStackTrace();
			
		} finally{
			
			try{ if(reader!=null) reader.close(); }			// Close the reader !!
			catch(Exception ex2){ ex2.printStackTrace(); }
			
		}
		
		return textread.toString();
		
	}
	
	public static boolean writeFile(String pathtofile, String text){
		
	    System.out.println("File being written: " + pathtofile);
	    System.out.println("Text length: " + text.length());
	    
	    boolean success=true;
	    PrintWriter writer = null;
	    
	    try{
	    	
	    	writer=new PrintWriter(new File(pathtofile), "UTF-8");			// Writing as UTF-8
	    	writer.print(text);
	    	writer.flush();													// Write it to the file
	    	
	    } catch(Exception ex){
	    	
	    	ex.printStackTrace();
	    	success=false;
	    	
	    } finally{
	    	
	    	try{ if(writer!=null) writer.close(); }							// Close writer !!
	    	catch(Exception ex2){ ex2.printStackTrace(); success=false; }
	    	
	    }
	    
	    if(success) System.out.println("File written successfully.");
	    
	    return success;
		
	}
	

	protected static String createNeededFolders(String file){	// Returns the same value, but all needed folders are created. No need to use cpath!!
		
		file=cpath(file);	// Corrects the file name
		
		int position=-1;
		
		do{
			
			position=file.indexOf(cpath("/"),position+1);			// Finds the next slash to check the path
			if(position<=-1) break;									// If it doesn't exist, break
			String folder=cpath(file.substring(0, position));		// Set the folder path to check
			
			if(!new File(folder).exists() && folder.length()>0){	// Exists?
				
				System.out.println("Creating folder: " + folder);
				
				new File(folder).mkdir();							// Let's create it
			}

		}while(position!=-1 && position+1<file.length());			// While there are still folders to create or check
		
		return file;
		
	}
	
	
	
}
