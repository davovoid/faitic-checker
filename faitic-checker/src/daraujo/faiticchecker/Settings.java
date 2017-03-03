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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Settings {

	protected static JSONObject jsonConf;
	protected static JSONParser jsonParser;
	
	private static String ifilename;
	
	public Settings(String filename){
		
		ifilename=filename;
		loadSettings();
		
	}
	
	protected static void loadSettings(){	// Done at initialization
		
		jsonParser=new JSONParser();	// Initializes the JSONParser
		
		String settingsFile=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/" + ifilename);
		
		if(new File(settingsFile).exists()){
			
			try{
				
				jsonConf=(JSONObject) jsonParser.parse(ClassicRoutines.readFile(settingsFile));
				return;
				
			} catch(Exception e){
				
			}
			
		}
		
		// Not returned. JSONConf couldn't be loaded
		
		jsonConf=new JSONObject();
		toDoIfNeverCreated();
		
	}
	
	protected static void saveSettings(){

		String settingsFile=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/" + ifilename);
		
		ClassicRoutines.writeFile(settingsFile,jsonConf.toJSONString());
		
	}
	
	protected static void toDoIfNeverCreated(){
		
		// Rewrite this method to get new functionalities
		
		/*JSONObject jsonfolders=new JSONObject();
		jsonConf.put("SubjectFolders", jsonfolders);*/
		
	}
	
	public static String getSubjectUniqueName(String subject){
		
		final String acceptedCharacters="1234567890qwertyuiopasdfghjklzxcvbnm ";
		
		StringBuffer output=new StringBuffer();
		
		for(int i=0; i<subject.length(); i++){
			
			// Accept just some characters from acceptedCharacters
			if(acceptedCharacters.indexOf(subject.toLowerCase().charAt(i))>=0) output.append(subject.toLowerCase().charAt(i));
			
		}
		
		// Output trimmed
		return output.toString().trim();
		
	}
	
	protected static String getSubjectPath(String subject){
		
		JSONObject jsonfoldersabs=null, jsonfoldersrel=null;
		
		if(jsonConf.containsKey("SubjectFolders")) jsonfoldersabs=(JSONObject)jsonConf.get("SubjectFolders");
		if(jsonConf.containsKey("RelativeSubjectFolders")) jsonfoldersrel=(JSONObject)jsonConf.get("RelativeSubjectFolders");
		
		String subjectUniqueName=getSubjectUniqueName(subject);
		
		String outpath=null;
		
		if(jsonfoldersrel!=null) if(jsonfoldersrel.containsKey(subjectUniqueName)) outpath=ClassicRoutines.getAbsolutePath((String) jsonfoldersrel.get(subjectUniqueName), ClassicRoutines.getJarPathFolder());	// First relative
		if(jsonfoldersabs!=null) if(jsonfoldersabs.containsKey(subjectUniqueName) && outpath==null) outpath=(String) jsonfoldersabs.get(subjectUniqueName);	// If not absolute
		
		return outpath;
		
	}
	
	protected static void setSubjectPath(String subject, String path){
		
		boolean portable=ClassicRoutines.isPortable();
		
		// If subject folders key doesn't exist, it is created
		if(!jsonConf.containsKey("SubjectFolders")) jsonConf.put("SubjectFolders", new JSONObject());					// Not portable
		if(!jsonConf.containsKey("RelativeSubjectFolders")) jsonConf.put("RelativeSubjectFolders", new JSONObject());	// Portable
		
		JSONObject jsonfoldersabs=(JSONObject) jsonConf.get("SubjectFolders");
		JSONObject jsonfoldersrel=(JSONObject) jsonConf.get("RelativeSubjectFolders");
		
		String subjectUniqueName=getSubjectUniqueName(subject);
		
		// If the subject folder is registered, it is deleted so as to add it again
		if(jsonfoldersabs.containsKey(subjectUniqueName)) jsonfoldersabs.remove(subjectUniqueName);
		if(jsonfoldersrel.containsKey(subjectUniqueName)) jsonfoldersrel.remove(subjectUniqueName);
		
		String relpath=ClassicRoutines.getRelativePath(path, ClassicRoutines.getJarPathFolder());
		
		if(path.equals(relpath) || !portable){
			// Save it as absolute if equal or not portable
			jsonfoldersabs.put(subjectUniqueName, path);
		} else{
			// Save it as relative
			jsonfoldersrel.put(subjectUniqueName,relpath);
			
		}
		
		// Update the SubjectFolders section
		if(jsonConf.containsKey("SubjectFolders")) jsonConf.remove("SubjectFolders");
		jsonConf.put("SubjectFolders", jsonfoldersabs);
		if(jsonConf.containsKey("RelativeSubjectFolders")) jsonConf.remove("RelativeSubjectFolders");
		jsonConf.put("RelativeSubjectFolders", jsonfoldersrel);
		
		
	}
	
}
