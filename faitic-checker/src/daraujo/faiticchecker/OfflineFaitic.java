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
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class OfflineFaitic {

	public OfflineFaitic(){
		
	}
	
	public static boolean offlineExists(String username){
		
		return new File(ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/offline-" + username + "-subjects.json")).exists();
		
	}
	
	public static ArrayList<Subject> getOfflineSubjectList(String username){
		
		JSONParser jsonParser=new JSONParser();	// Initializes the JSONParser
		
		String subjectsFile=ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/offline-" + username + "-subjects.json");
		
		if(new File(subjectsFile).exists()){
			
			try{
				
				JSONObject subjectsjson=(JSONObject) jsonParser.parse(ClassicRoutines.readFile(subjectsFile));

				if(subjectsjson.containsKey("subjects")){
					
					JSONArray subjectsArray=(JSONArray) subjectsjson.get("subjects");
					
					ArrayList<Subject> out=new ArrayList<Subject>();
					
					for(Object subjectjsonobj : subjectsArray){
						
						JSONObject subjectjson=(JSONObject) subjectjsonobj;
						
						if(subjectjson.containsKey("name"))
							out.add(new Subject("",(String)subjectjson.get("name")));
						
					}
					
					return out;
					
				}
				
			} catch(Exception e){
				
			}
			
		}
		
		// Not returned. JSONConf couldn't be loaded
		return new ArrayList<Subject>();
		
	}
	
	public static void setOfflineSubjectList(String username, ArrayList<Subject> subjectList){
		
		String subjectsFile=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/offline-" + username + "-subjects.json");
		
		JSONObject subjectsjson=new JSONObject();
		
		JSONArray subjectsArray=new JSONArray();
		
		for(Subject subject : subjectList){
			
			JSONObject subjectjson=new JSONObject();
			
			subjectjson.put("name", subject.getName());
			//subjectjson.put("url", subject.getURL());
			
			subjectsArray.add(subjectjson);
			
		}
		
		subjectsjson.put("username", username);
		subjectsjson.put("subjects", subjectsArray);
		
		ClassicRoutines.writeFile(subjectsFile,subjectsjson.toJSONString());
		
		
	}
	
	public static ArrayList<FileFromURL> getOfflineFileList(String username, String subjectName){

		JSONParser jsonParser=new JSONParser();	// Initializes the JSONParser

		String secureSubjectName=Settings.getSubjectUniqueName(subjectName);
		
		String filesFile=ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/offline-" + username + "-subject-" + secureSubjectName + ".json");
		
		if(new File(filesFile).exists()){
			
			try{
				
				JSONObject filesjson=(JSONObject) jsonParser.parse(ClassicRoutines.readFile(filesFile));

				if(filesjson.containsKey("files")){
					
					JSONArray filesArray=(JSONArray) filesjson.get("files");
					
					ArrayList<FileFromURL> out=new ArrayList<FileFromURL>();
					
					for(Object filejsonobj : filesArray){
						
						JSONObject filejson=(JSONObject) filejsonobj;
						
						if(filejson.containsKey("filedestination"))
							out.add(new FileFromURL("",(String)filejson.get("filedestination")));
						
					}
					
					return out;
					
				}
				
			} catch(Exception e){
				
			}
			
		}
		
		// Not returned. JSONConf couldn't be loaded
		return new ArrayList<FileFromURL>();
		
	}
	
	public static void setOfflineFileList(String username, String subjectName, ArrayList<FileFromURL>fileList){

		String secureSubjectName=Settings.getSubjectUniqueName(subjectName);
		
		String filesFile=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/offline-" + username + "-subject-" + secureSubjectName + ".json");
		
		JSONObject filesjson=new JSONObject();
		
		JSONArray fileArray=new JSONArray();
		
		for(FileFromURL file : fileList){
			
			JSONObject filejson=new JSONObject();
			
			filejson.put("filedestination", file.getFileDestination());
			//filejson.put("url", file.getURL());
			
			fileArray.add(filejson);
			
		}
		
		filesjson.put("username", username);
		filesjson.put("subjectname", subjectName);		
		filesjson.put("files", fileArray);
		
		ClassicRoutines.writeFile(filesFile,filesjson.toJSONString());
		
		
	}

}
