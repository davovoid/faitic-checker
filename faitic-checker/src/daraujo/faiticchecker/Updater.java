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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Updater {

	private static String UPDATEURL;
	
	protected static String sha256, downloadname, currentversion, urlmoreinfo, downloadurl, description;
	
	public Updater(String channel){
		
		UPDATEURL= "https://davovoid.github.io/update-" + channel + ".json";
		
	}
	
	private static String getFileFromInternet(String url) throws Exception{	// Really simplified version of the Faitic.java class
		
		HttpURLConnection connection= (HttpURLConnection) new URL(url).openConnection();
		
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		
		InputStream strin = connection.getInputStream();
		
		StringBuffer output=new StringBuffer();

		byte[] temp = new byte[1000];
		int read = strin.read(temp);

			int counter=0;

		while (read != -1) {
			output.append(new String(temp,0,read,StandardCharsets.UTF_8));
			read = strin.read(temp);
			counter+=read;

		}

		strin.close();
		
		return output.toString();
		
	}
	

	private static boolean saveFileFromInternet(String url, File fileDestination) throws Exception{	// Really simplified version of the Faitic.java class
		
		System.out.println("Downloading \"" + url + "\" and saving into \"" + fileDestination.getAbsolutePath() + "\"...");
		
		HttpURLConnection connection= (HttpURLConnection) new URL(url).openConnection();
		
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		
		InputStream strin = connection.getInputStream();
		
		FileOutputStream filewriter = new FileOutputStream(fileDestination);

		byte[] temp; int read;
		
		try{
			
			temp = new byte[1000];
			read = strin.read(temp);

			while (read != -1) {
				filewriter.write(temp, 0, read);
				
				read = strin.read(temp);
				
			}
			
			// Close the writers

			filewriter.close();
			strin.close();
			
			System.out.println("Success.");
			
			return true;
			
		} catch(Exception ex){
			
			ex.printStackTrace();
			
			try{filewriter.close();} catch(Exception ex2){ex2.printStackTrace();}
			try{strin.close();} catch(Exception ex2){ex2.printStackTrace();}
			
			return false;
			
		}
		
		
	}
	
	
	protected static void fillUpdateInfo(String lang) throws Exception{
		
		String updateDocument=getFileFromInternet(UPDATEURL);
		
		JSONParser jsonparser=new JSONParser();
		
		JSONObject jsonobject = (JSONObject) jsonparser.parse(updateDocument);
		
		sha256=(String) jsonobject.get("sha256");
		downloadname=(String) jsonobject.get("downloadname");
		currentversion=(String) jsonobject.get("currentversion");
		urlmoreinfo=(String) jsonobject.get("urlmoreinfo");
		downloadurl=(String) jsonobject.get("downloadurl");
		
		JSONArray arraydesc=(JSONArray) jsonobject.get("description");
		
		for(Object arraydescitem : arraydesc){
			
			JSONObject arraydescitemjson=(JSONObject) arraydescitem;
			
			String langitem=(String) arraydescitemjson.get("lang");
			
			if(langitem.indexOf(lang)>=0){
				
				// Correct language
				
				description=(String) arraydescitemjson.get("plaintext");
				
			}
			
		}
		
	}
	
	protected boolean isThereANewVersion(){
		
		return !About.VERSION.equals(currentversion);	// Non-equivalence implies not the same version, so there is another version
		
	}

	protected static boolean checksha256fromfile(File file, String checksumToCheck) throws NoSuchAlgorithmException, IOException{
		
		MessageDigest digester=MessageDigest.getInstance("SHA-256");
		
		FileInputStream reader=new FileInputStream(file);
		
		byte[] temp=new byte[1024];
		int templen=reader.read(temp);
		
		while(templen>=0){
			
			digester.update(temp, 0, templen);
			templen=reader.read(temp);
			
		}
		
		reader.close();
		
		byte[] digesterOutput=digester.digest();
		
		String digesterOutputString="";
		
		for(byte b : digesterOutput)
			digesterOutputString+=String.format("%02x", b).toLowerCase();
		
		return digesterOutputString.equals(checksumToCheck.toLowerCase());
		
	}
	
	protected static String updateMyself(){ // Points to the downloaded jar file
		
		if(LoginGUI.isTheJarPathAFile()){
			
			// Let's download it
			
			String tempfilename=ClassicRoutines.createNeededFolders(ClassicRoutines.getUserDataPath(true) + "/update.jar");
			
			File outputFile=new File(tempfilename);
			
			System.out.println(" - Output file for updated jar: " + outputFile.getAbsolutePath());
			
			boolean fileSaved=false;
			
			try {
				
				fileSaved=saveFileFromInternet(downloadurl, outputFile);
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
			}
			
			if(fileSaved){
				
				boolean thechecksummatches=false;
				
				try {
					
					thechecksummatches=checksha256fromfile(outputFile, sha256);
					
				} catch (NoSuchAlgorithmException e) {

					thechecksummatches=true;	// Change ASAP					
					e.printStackTrace();
					
				} catch (IOException e) {

					e.printStackTrace();
					return null;
					
				}
				
				// Continue
				
				if(thechecksummatches) return outputFile.getAbsolutePath();
				else return null;
				
			}
			
		}
		
		return null;
		
	}
	
}
