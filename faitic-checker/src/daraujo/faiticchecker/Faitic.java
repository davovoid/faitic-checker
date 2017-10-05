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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPInputStream;

public class Faitic {

	/**
	 * 
	 * This code can be used in both java and Android versions.
	 * Just copy the code from here to the end of file and change
	 * the "android" constant so that it matches the platform
	 *
	 */

	private static final boolean android = false;

	private static final String urlMain="https://faitic.uvigo.es/index.php/es/";
	private static final String urlSubjects="https://faitic.uvigo.es/index.php/es/materias";
	private static CookieManager cookieManager;
	public static Logger logger;

	private static boolean cCancelDownload=false;
	private static Semaphore sCancelDownload=new Semaphore(1);
	
	private static long cDownloadSize=0;
	private static Semaphore sDownloadSize=new Semaphore(1);
	private static long cDownloaded=1;
	private static Semaphore sDownloaded=new Semaphore(1);

	public Faitic(boolean verbose){
		toDoAtStartup(verbose, android);
	}

	private static void toDoAtStartup(boolean verbose, boolean android){

		startCookieSession();
		logger=new Logger(verbose);

	}

	protected static boolean getCancelDownload(){

		try{

			sCancelDownload.acquire();
			boolean out=cCancelDownload;
			sCancelDownload.release();

			return out;

		} catch(Exception ex){

			// Weird. Stop the download just in case

			ex.printStackTrace();
			return true;

		}

	}

	protected static void setCancelDownload(boolean value){

		try{

			sCancelDownload.acquire();
			cCancelDownload=value;
			sCancelDownload.release();

		} catch(Exception ex){

			ex.printStackTrace();

		}

	}

	protected static long getDownloadSize(){

		try{

			sDownloadSize.acquire();
			long out=cDownloadSize;
			sDownloadSize.release();

			return out;

		} catch(Exception ex){

			// Weird. Stop the download just in case

			ex.printStackTrace();
			return 0;

		}

	}

	protected static void setDownloadSize(long value){

		try{

			sDownloadSize.acquire();
			cDownloadSize=value;
			sDownloadSize.release();

		} catch(Exception ex){

			ex.printStackTrace();

		}

	}

	protected static long getDownloaded(){

		try{

			sDownloaded.acquire();
			long out=cDownloaded;
			sDownloaded.release();

			return out;

		} catch(Exception ex){

			// Weird. Stop the download just in case

			ex.printStackTrace();
			return 1;

		}

	}

	protected static void setDownloaded(long value){

		try{

			sDownloaded.acquire();
			cDownloaded=value;
			sDownloaded.release();

		} catch(Exception ex){

			ex.printStackTrace();

		}

	}

	private static void startCookieSession(){

		cookieManager=new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
		cookieManager.getCookieStore().removeAll();

		if(android) {
			CookieHandler.setDefault(cookieManager);
		}

	}

	public static String lastRequestedURL="";
	
	public static String requestDocument(String strurl, String post) throws Exception{
		
		return requestDocument(strurl,post,"UTF-8");
		
	}

	public static String requestDocument(String strurl, String post, String inputCharset) throws Exception{

		lastRequestedURL=strurl;

		logger.log(Logger.INFO, "Requesting URL: " + strurl);
		logger.log(Logger.INFO, "Post data: " + post);

		logger.log(Logger.INFO, "--- Creating connection ---");

		URL url=new URL(strurl);

		List<HttpCookie> cookiesAssoc=cookieManager.getCookieStore().get(url.toURI());
		String cookiesAssocStr="";

		for(HttpCookie cookieAssoc : cookiesAssoc){

			cookiesAssocStr+=(cookiesAssocStr.length()>0 ? "; " : "") + cookieAssoc.getName() + "=" + cookieAssoc.getValue();

		}

		HttpURLConnection connection= (HttpURLConnection) url.openConnection();

		// Time out settings
		if(android) {
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		}
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setUseCaches(false);

		connection.setRequestProperty("Accept-Encoding", "gzip");

		if(post.length()>0){
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//connection.setRequestProperty("charset", "utf-8");
			//connection.setRequestProperty("Content-Length", "" + post.length());
		}

		if(cookiesAssocStr.length()>0){
			connection.setRequestProperty("Cookie", cookiesAssocStr);
			logger.log(Logger.INFO, "Cookies: " + cookiesAssocStr);
		}

		if(post.length()>0){

			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.write(post.getBytes("UTF-8"));

		}

		logger.log(Logger.INFO, "--- Petition sent. Reading ---");

		StringBuffer output=new StringBuffer();
		InputStream reader;

		if(connection.getContentEncoding()!=null)
			if(connection.getContentEncoding().equals("gzip")){

				reader=new GZIPInputStream(connection.getInputStream());
				logger.log(Logger.INFO, " + GZIP ENCODED");

			} else{

				reader = connection.getInputStream();

			}
		else{

			reader = connection.getInputStream();

		}

		byte[] temp = new byte[1000];
		int read = reader.read(temp);

		int counter=0;

		while (read != -1) {
			output.append(new String(temp,0,read,inputCharset));
			read = reader.read(temp);
			counter+=read;

		}

		reader.close();

		int status=connection.getResponseCode();

		String headerName;

		for (int i=1; (headerName = connection.getHeaderFieldKey(i))!=null; i++) {

			if(headerName.toLowerCase().equals("set-cookie")){

				String cookiesToSet=connection.getHeaderField(i);


				if(!android){
					for(String cookieToSet : cookiesToSet.split(";")){

						String[] cookieParameters=cookieToSet.split("=");

						cookieManager.getCookieStore().add(url.toURI(), new HttpCookie(cookieParameters[0].trim(), cookieParameters[1].trim()));

						logger.log(Logger.INFO, " + Adding cookie \"" + cookieToSet + "\" to uri \"" + url.toURI().toString() + "\".");

					}
				}else {
					int c = 0;

					for (String cookieToSet : cookiesToSet.split(";")) {

						String[] cookieParameters = cookieToSet.split("=");


						if (c == 1 || cookieParameters[0].contains("path") || cookieParameters[0].contains("expire")) {
							logger.log(Logger.INFO, "GALLETA NO CARGADA " + cookieParameters[0] + "///" + cookieParameters[1]);
						} else {

							c = 1;

							HttpCookie galleta = new HttpCookie(cookieParameters[0].trim(), cookieParameters[1].trim());
							galleta.setPath("/");

							cookieManager.getCookieStore().add((url).toURI(), galleta);

							logger.log(Logger.INFO, " + Adding cookie \"" + cookieToSet + "\" to uri \"" + (url).toURI().toString() + "\".");

						}


					}
				}


			}

		}

		if (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER){


			logger.log(Logger.INFO, "--- Redirected ---");

			return requestDocument(connection.getHeaderField("Location"),"");


		}

		else{

			logger.log(Logger.INFO, "--- Request finished ---\n");

			return output.toString();

		}

	}

	public static void downloadFile(String strurl, String post, String filename) throws Exception{

		if(getCancelDownload()) return;	// Download cancelled, don't dare to continue

		System.out.println(" -- Downloading file from \"" + strurl + "\" and saving to \"" + filename + "\"...");

		lastRequestedURL=strurl;

		logger.log(Logger.INFO, "Requesting URL: " + strurl);
		logger.log(Logger.INFO, "Post data: " + post);

		logger.log(Logger.INFO, "--- Creating connection ---");

		URL url=new URL(strurl);

		List<HttpCookie> cookiesAssoc=cookieManager.getCookieStore().get(url.toURI());
		String cookiesAssocStr="";

		for(HttpCookie cookieAssoc : cookiesAssoc){

			cookiesAssocStr+=(cookiesAssocStr.length()>0 ? "; " : "") + cookieAssoc.getName() + "=" + cookieAssoc.getValue();

		}

		HttpURLConnection connection= (HttpURLConnection) url.openConnection();

		// Time out settings
		if(android) {
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		}
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setUseCaches(false);

		if(post.length()>0){
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//connection.setRequestProperty("charset", "utf-8");
			//connection.setRequestProperty("Content-Length", "" + post.length());
		}

		if(cookiesAssocStr.length()>0){
			connection.setRequestProperty("Cookie", cookiesAssocStr);
			logger.log(Logger.INFO, "Cookies: " + cookiesAssocStr);
		}

		if(post.length()>0){

			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.write(post.getBytes("UTF-8"));

		}

		logger.log(Logger.INFO, "--- Petition sent. Reading ---");

		// Check cookies and if the document redirects

		int status=connection.getResponseCode();

		String headerName;

		for (int i=1; (headerName = connection.getHeaderFieldKey(i))!=null; i++) {

			if(headerName.toLowerCase().equals("set-cookie")){

				String cookiesToSet=connection.getHeaderField(i);

				if(!android){
					for(String cookieToSet : cookiesToSet.split(";")){

						String[] cookieParameters=cookieToSet.split("=");

						cookieManager.getCookieStore().add(url.toURI(), new HttpCookie(cookieParameters[0].trim(), cookieParameters[1].trim()));

						logger.log(Logger.INFO, " + Adding cookie \"" + cookieToSet + "\" to uri \"" + url.toURI().toString() + "\".");

					}
				}else {
					int c = 0;

					for (String cookieToSet : cookiesToSet.split(";")) {

						String[] cookieParameters = cookieToSet.split("=");


						if (c == 1 || cookieParameters[0].contains("path") || cookieParameters[0].contains("expire")) {
							logger.log(Logger.INFO, "GALLETA NO CARGADA " + cookieParameters[0] + "///" + cookieParameters[1]);
						} else {

							c = 1;

							HttpCookie galleta = new HttpCookie(cookieParameters[0].trim(), cookieParameters[1].trim());
							galleta.setPath("/");

							cookieManager.getCookieStore().add((url).toURI(), galleta);

							logger.log(Logger.INFO, " + Adding cookie \"" + cookieToSet + "\" to uri \"" + (url).toURI().toString() + "\".");

						}


					}
				}


			}

		}

		// Does the document redirect?

		if (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER){


			logger.log(Logger.INFO, "--- Redirected ---");

			downloadFile(connection.getHeaderField("Location"),"", filename);

			return;

		}

		// OK, the document doesn't redirect. Download it

		InputStream reader;	// Response document

		reader = connection.getInputStream();

		// Save the download length
		
		setDownloadSize(connection.getContentLengthLong());
		setDownloaded(0);
		
		// Let's write the document

		FileOutputStream filewriter;

		int tempfilenumber=1;
		String tempfilename=filename + ".tmp" + tempfilenumber;

		while(new File(tempfilename).exists()){

			// Iterates until the file doesn't exist

			tempfilename=filename + ".tmp" + (++tempfilenumber);

		}

		logger.log(Logger.INFO, " + Saving temp as: " + tempfilename);

		filewriter = new FileOutputStream(tempfilename);

		byte[] temp; int read;

		try{

			temp = new byte[2048];
			read = reader.read(temp);

			while (read != -1 && !getCancelDownload()) {
				
				setDownloaded(getDownloaded()+read);
				
				filewriter.write(temp, 0, read);

				read = reader.read(temp);

			}

			// Close the writers

			filewriter.close();
			reader.close();

			if(!getCancelDownload()){

				// Success. Substitute the file

				logger.log(Logger.INFO, " + Renaming temp to: " + filename);

				File oldfile=new File(filename);
				File tempfile=new File(tempfilename);

				boolean deletingsuccess=true;

				if(oldfile.exists()){
					if(!oldfile.isDirectory()){

						deletingsuccess=oldfile.delete();

					} else{

						deletingsuccess=false;

					}
				}

				if(deletingsuccess){

					// Correctly deleted

					tempfile.renameTo(oldfile);

					System.out.println("Success.");


				}

			} else{

				logger.log(Logger.ERROR, "--- Download cancelled ---\n");

			}

		} catch(Exception ex){

			ex.printStackTrace();

			try{filewriter.close();} catch(Exception ex2){ex2.printStackTrace();}
			try{reader.close();} catch(Exception ex2){ex2.printStackTrace();}

		}

		logger.log(Logger.INFO, "--- Request finished ---\n");


		return;

	}

	public static String getRedirectedURL(String strurl, String post) throws Exception{

		lastRequestedURL=strurl;

		logger.log(Logger.INFO, "Requesting URL: " + strurl);
		logger.log(Logger.INFO, "Post data: " + post);

		logger.log(Logger.INFO, "--- Creating connection ---");

		URL url=new URL(strurl);

		List<HttpCookie> cookiesAssoc=cookieManager.getCookieStore().get(url.toURI());
		String cookiesAssocStr="";

		for(HttpCookie cookieAssoc : cookiesAssoc){

			cookiesAssocStr+=(cookiesAssocStr.length()>0 ? "; " : "") + cookieAssoc.getName() + "=" + cookieAssoc.getValue();

		}

		HttpURLConnection connection= (HttpURLConnection) url.openConnection();

		// Time out settings
		if(android) {
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		}
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setUseCaches(false);

		connection.setRequestProperty("Accept-Encoding", "gzip");

		if(post.length()>0){
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//connection.setRequestProperty("charset", "utf-8");
			//connection.setRequestProperty("Content-Length", "" + post.length());
		}

		if(cookiesAssocStr.length()>0){
			connection.setRequestProperty("Cookie", cookiesAssocStr);
			logger.log(Logger.INFO, "Cookies: " + cookiesAssocStr);
		}

		if(post.length()>0){

			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.write(post.getBytes("UTF-8"));

		}

		logger.log(Logger.INFO, "--- Petition sent. Waiting for redirecting info ---");

		int status=connection.getResponseCode();

		String headerName;

		// Getting cookies

		for (int i=1; (headerName = connection.getHeaderFieldKey(i))!=null; i++) {

			if(headerName.toLowerCase().equals("set-cookie")){

				String cookiesToSet=connection.getHeaderField(i);

				if(!android){
					for(String cookieToSet : cookiesToSet.split(";")){

						String[] cookieParameters=cookieToSet.split("=");

						cookieManager.getCookieStore().add(url.toURI(), new HttpCookie(cookieParameters[0].trim(), cookieParameters[1].trim()));

						logger.log(Logger.INFO, " + Adding cookie \"" + cookieToSet + "\" to uri \"" + url.toURI().toString() + "\".");

					}
				}else {
					int c = 0;

					for (String cookieToSet : cookiesToSet.split(";")) {

						String[] cookieParameters = cookieToSet.split("=");


						if (c == 1 || cookieParameters[0].contains("path") || cookieParameters[0].contains("expire")) {
							logger.log(Logger.INFO, "GALLETA NO CARGADA " + cookieParameters[0] + "///" + cookieParameters[1]);
						} else {

							c = 1;

							HttpCookie galleta = new HttpCookie(cookieParameters[0].trim(), cookieParameters[1].trim());
							galleta.setPath("/");

							cookieManager.getCookieStore().add((url).toURI(), galleta);

							logger.log(Logger.INFO, " + Adding cookie \"" + cookieToSet + "\" to uri \"" + (url).toURI().toString() + "\".");

						}


					}
				}


			}

		}

		// Return status, there will be the redirection

		if (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER){


			logger.log(Logger.INFO, "--- Redirected. ---");

			String redURL=connection.getHeaderField("Location");

			logger.log(Logger.INFO, "URL: " + redURL);
			return redURL;


		}

		else{

			logger.log(Logger.INFO, "--- Request finished. Not redirected ---\n");

			return null;

		}

	}

	public static String generatePostLogin(String username, String password) throws Exception{

		StringBuffer output=new StringBuffer();

		String documentMain=requestDocument(urlMain,"");

		int formStart=documentMain.toLowerCase().indexOf("<form action=\"/index.php/es/\" method=\"post\" id=\"login-form\"");

		int formEnd=documentMain.toLowerCase().indexOf("</form>", formStart);

		// Form detected

		if(formStart>=0 && formEnd>=0){

			int currentpos=documentMain.toLowerCase().indexOf("<input",formStart);

			while(currentpos>=formStart && currentpos<formEnd){

				String type=null, name=null, value=null;

				int closer=documentMain.toLowerCase().indexOf(">",currentpos);

				String[] sentence=documentMain.substring(currentpos, closer).split(" ");	// The input divided by the spaces

				for(String sentencePart : sentence){	// Read the parts of the input

					String partname=sentencePart.substring(0, sentencePart.indexOf("=") >=0 ? sentencePart.indexOf("=") : 0);

					String partvalue=sentencePart.substring(sentencePart.indexOf("=") >=0 ? sentencePart.indexOf("=")+1 : 0, sentencePart.length());


					switch(partname.toLowerCase()){

					case "type" : type=partvalue.replace("\"", ""); break;
					case "name" : name=partvalue.replace("\"", ""); break;
					case "value" : value=partvalue.replace("\"", ""); break;

					default:;

					}

				}

				if(type!=null && name!=null && value!=null)
					if(!type.toLowerCase().contains("checkbox"))	{ // To be sent

						if(output.length()>0) output.append("&");

						output.append(name + "=" + URLEncoder.encode(value, "UTF-8"));

					}

				// Prepare for next while loop
				currentpos=documentMain.toLowerCase().indexOf("<input",currentpos+1);

			}


		}

		if(output.length()>0) output.append("&");
		output.append("username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8"));

		return output.toString();

	}
	

	public static String faiticLogin(String username, String password) throws Exception{

		String responseToLogin=requestDocument(urlMain,generatePostLogin(username, password));

		int errorToLoginIndex=responseToLogin.indexOf("<dd class=\"error message\">");

		// If there was an error
		if(errorToLoginIndex >= 0){

			int firstLiError=responseToLogin.indexOf("<li>",errorToLoginIndex);
			int lastLiError=responseToLogin.indexOf("</li>",errorToLoginIndex);

			if(firstLiError>0 && lastLiError>firstLiError){

				logger.log(Logger.ERROR, " -- Error: " + responseToLogin.substring(firstLiError+4, lastLiError) + " -- ");
				return null;

			}

		}

		// No error, go to the document we want (Languages change the destination)

		//return responseToLogin;
		return requestDocument(urlSubjects,"");


	}


	public static String faiticLogout(String documentMain) throws Exception{

		StringBuffer output=new StringBuffer();

		int formStart=documentMain.toLowerCase().indexOf("<form action=\"/index.php/es/materias\" method=\"post\" id=\"login-form\"");

		int formEnd=documentMain.toLowerCase().indexOf("</form>", formStart);

		// Form detected

		if(formStart>=0 && formEnd>=0){

			int currentpos=documentMain.toLowerCase().indexOf("<input",formStart);

			while(currentpos>=formStart && currentpos<formEnd){

				String type=null, name=null, value=null;

				int closer=documentMain.toLowerCase().indexOf(">",currentpos);

				String[] sentence=documentMain.substring(currentpos, closer).split(" ");	// The input divided by the spaces

				for(String sentencePart : sentence){	// Read the parts of the input

					String partname=sentencePart.substring(0, sentencePart.indexOf("=") >=0 ? sentencePart.indexOf("=") : 0);

					String partvalue=sentencePart.substring(sentencePart.indexOf("=") >=0 ? sentencePart.indexOf("=")+1 : 0, sentencePart.length());


					switch(partname.toLowerCase()){

					case "type" : type=partvalue.replace("\"", ""); break;
					case "name" : name=partvalue.replace("\"", ""); break;
					case "value" : value=partvalue.replace("\"", ""); break;

					default:;

					}

				}

				if(type!=null && name!=null && value!=null)
					if(!type.toLowerCase().contains("checkbox"))	{ // To be sent

						if(output.length()>0) output.append("&");

						output.append(name + "=" + URLEncoder.encode(value, "UTF-8"));

					}

				// Prepare for next while loop
				currentpos=documentMain.toLowerCase().indexOf("<input",currentpos+1);

			}


		}

		return requestDocument(urlSubjects,output.toString());

	}


	public static ArrayList<Subject> faiticSubjects(String documentToCheck){	// 0 url 1 name

		ArrayList<Subject> subjectList=new ArrayList<Subject>();

		// Login was unsuccessful
		if(documentToCheck==null) return subjectList;

		// Login successful:

		int subjectIndex=documentToCheck.indexOf("<span class=\"asignatura\"");

		while(subjectIndex>=0){

			// Check subjects one by one

			int hrefIndex=documentToCheck.indexOf("<a href=\"", subjectIndex);
			int hrefURLCloserIndex=documentToCheck.indexOf("\"",hrefIndex+"<a href=\"".length());

			int hrefFirstTagCloserIndex=documentToCheck.indexOf(">",hrefURLCloserIndex);
			int hrefSecondTagOpenerIndex=documentToCheck.indexOf("<", hrefFirstTagCloserIndex);


			String subjectURL=documentToCheck.substring(hrefIndex+"<a href=\"".length(), hrefURLCloserIndex);
			String subjectName=documentToCheck.substring(hrefFirstTagCloserIndex+1, hrefSecondTagOpenerIndex).trim();

			subjectList.add(new Subject(subjectURL, subjectName));

			subjectIndex=documentToCheck.indexOf("<span class=\"asignatura\"",subjectIndex+1);

		}

		return subjectList;

	}

	public static DocumentFromURL goToSubject(String url) throws Exception{	// 0 is the url and 1 is the document itself

		String documentMain=requestDocument(url,"");

		StringBuffer output=new StringBuffer();

		int formStart=documentMain.toLowerCase().indexOf("<form name='frm'");

		int formEnd=documentMain.toLowerCase().indexOf("</form>", formStart);

		int actionStart=documentMain.indexOf("action='", formStart);
		int actionEnd=documentMain.indexOf("'", actionStart+"action='".length());

		String urlForAction=documentMain.substring(actionStart + "action='".length(), actionEnd);

		// Form detected

		if(formStart>=0 && formEnd>=0){

			int currentpos=documentMain.toLowerCase().indexOf("<input",formStart);

			while(currentpos>=formStart && currentpos<formEnd){

				String type=null, name=null, value=null;

				int closer=documentMain.toLowerCase().indexOf(">",currentpos);

				String[] sentence=documentMain.substring(currentpos, closer).split(" ");	// The input divided by the spaces

				for(String sentencePart : sentence){	// Read the parts of the input

					String partname=sentencePart.substring(0, sentencePart.indexOf("=") >=0 ? sentencePart.indexOf("=") : 0);

					String partvalue=sentencePart.substring(sentencePart.indexOf("=") >=0 ? sentencePart.indexOf("=")+1 : 0, sentencePart.length());


					switch(partname.toLowerCase()){

					case "type" : type=partvalue.replace("'", ""); break;
					case "name" : name=partvalue.replace("'", ""); break;
					case "value" : value=partvalue.replace("'", ""); break;

					default:;

					}

				}

				if(type!=null && name!=null && value!=null)
					if(!type.toLowerCase().contains("checkbox"))	{ // To be sent

						if(output.length()>0) output.append("&");

						output.append(name + "=" + URLEncoder.encode(value, "UTF-8"));

					}

				// Prepare for next while loop
				currentpos=documentMain.toLowerCase().indexOf("<input",currentpos+1);

			}


		}

		return new DocumentFromURL(urlForAction,requestDocument(urlForAction,output.toString()));


	}

	public static final int CLAROLINE=0;
	public static final int MOODLE=1;
	public static final int MOODLE2=2;
	public static final int MOODLE3=3;
	public static final int UNKNOWN=99;

	public static int subjectPlatformType(String url){

		if(url.toLowerCase().contains("/claroline/")){

			// Claroline based, no doubts

			return CLAROLINE;

		}else if(url.toLowerCase().contains("/moodle")){

			// Moodle based, but which version?

			int platforminfostart=url.toLowerCase().indexOf("/moodle");
			int platforminfoend=url.indexOf("/", platforminfostart+1);

			if(platforminfostart>=0 && platforminfoend>platforminfostart){

				String platforminfo=url.toLowerCase().substring(platforminfostart+1, platforminfoend);

				if(platforminfo.contains("_")){

					// Moodle 2+

					if(platforminfo.contains("moodle2_")){

						// Moodle 2

						return MOODLE2;

					}

					if(platforminfo.contains("moodle3_")){

						// Moodle 3

						return MOODLE3;

					}

					// Reserved for newer Moodle versions

				} else{

					// Moodle 1

					return MOODLE;

				}

			}

		}

		// No matches
		return UNKNOWN;

	}

	public static void logoutSubject(String platformURL, String platformDocument, int platformType) throws Exception{

		if(platformType==CLAROLINE){

			String logoutURL=platformURL.substring(0, platformURL.lastIndexOf("?") >=0 ? platformURL.lastIndexOf("?") : platformURL.length()) + "?logout=true";

			requestDocument(logoutURL,"");

		}

		else if(platformType==MOODLE || platformType==MOODLE2 || platformType==MOODLE3){

			// More complicated :( pay attention because this is about to start...

			int endOfURLShouldStartWith= platformURL.indexOf("/", platformURL.indexOf("/moodle")+1);

			if(endOfURLShouldStartWith>=0){

				String logoutURLShouldStartWith=platformURL.substring(0, endOfURLShouldStartWith) + "/login/logout.php";
				// This is the url that should appear on the document, but with all the parameters given as GET

				// Let's look for this entry

				int hereIsTheLogoutURL=platformDocument.indexOf(logoutURLShouldStartWith);

				int hereEndsTheLogoutURL=platformDocument.indexOf("\"",hereIsTheLogoutURL);

				//System.out.println("\n\n" + logoutURLShouldStartWith + "\n\n");

				if(hereIsTheLogoutURL>=0 && hereEndsTheLogoutURL>hereIsTheLogoutURL){

					// Gotcha!

					requestDocument(platformDocument.substring(hereIsTheLogoutURL, hereEndsTheLogoutURL),"");

				}

			}

		}

	}

	public static ArrayList<FileFromURL> listDocumentsClaroline(String platformURL) throws Exception{

		/*
		 * 0 -> Path (incl. filename)
		 * 1 -> URL to file
		 */

		ArrayList<FileFromURL> list=new ArrayList<FileFromURL>();

		int untilWhenUrlToUse= platformURL.indexOf("/", platformURL.indexOf("/claroline")+1);

		if(untilWhenUrlToUse>=0){

			String urlBase = platformURL.substring(0, untilWhenUrlToUse);
			String urlToUse =  urlBase + "/document/document.php";
			listDocumentsClarolineInternal(urlToUse,list, urlBase);	// Recursive

		}

		cleanArtifacts(list);
		deleteRepeatedFiles(list);
		sortandaggrupatelist(list);

		return list;

	}

	private static void listDocumentsClarolineInternal(String urlToAnalyse, ArrayList<FileFromURL> list, String urlBase) throws Exception{

		String document;

		try{

			document=requestDocument(urlToAnalyse,"");

		} catch(Exception ex){

			return;

		}

		if(!urlToAnalyse.equals(lastRequestedURL)) return;		// If the page redirected us

		// Check for documents...

		int dirStart=document.indexOf("<a class=\" item");

		int dirEnd=document.lastIndexOf("End of Claroline Body");

		if(dirStart>=0 && dirEnd>dirStart){

			String documentToAnalyse=document.substring(dirStart, dirEnd);

			// First check for files

			int ocurrence=documentToAnalyse.indexOf("goto/index.php");

			while(ocurrence>=0){

				int endOfOcurrence=documentToAnalyse.indexOf("\"", ocurrence+1);

				if(endOfOcurrence>ocurrence){

					String urlGot=urlBase + "/document/" + documentToAnalyse.substring(ocurrence, endOfOcurrence).replace("&amp;", "&").replace(" ", "%20");

					String pathForFile=urlGot.substring((urlBase + "/document/goto/index.php/").length(), urlGot.lastIndexOf("?") >=0 ? urlGot.lastIndexOf("?") : urlGot.length());

					list.add(new FileFromURL(urlGot, URLDecoder.decode("/" + pathForFile, "iso-8859-1")));

				}

				ocurrence=documentToAnalyse.indexOf("goto/index.php", ocurrence+1);

			}


			// Now for directories

			ocurrence=documentToAnalyse.indexOf("/document/document.php?cmd=exChDir");

			while(ocurrence>=0){

				int endOfOcurrence=documentToAnalyse.indexOf("\"", ocurrence+1);

				if(endOfOcurrence>ocurrence){

					String urlGot=urlBase + documentToAnalyse.substring(ocurrence, endOfOcurrence).replace("&amp;", "&").replace(" ", "%20");

					listDocumentsClarolineInternal(urlGot, list, urlBase);

				}

				ocurrence=documentToAnalyse.indexOf("/document/document.php?cmd=exChDir", ocurrence+1);

			}


			// Now for linked pictures

			ocurrence=documentToAnalyse.indexOf("/document/document.php?docView=image");

			while(ocurrence>=0){

				int endOfOcurrence=documentToAnalyse.indexOf("\"", ocurrence+1);

				if(endOfOcurrence>ocurrence){

					String urlGot=urlBase + documentToAnalyse.substring(ocurrence, endOfOcurrence).replace("&amp;", "&").replace(" ", "%20");

					try{

						String document2=requestDocument(urlGot, "");

						listDocumentsClarolineInternalImage(document2, list, urlBase);

					} catch(Exception ex){

						ex.printStackTrace();

					}


				}

				ocurrence=documentToAnalyse.indexOf("/document/document.php?docView=image", ocurrence+1);

			}


		}



	}

	public static void listDocumentsClarolineInternalImage(String document, ArrayList<FileFromURL> list, String urlBase) throws Exception{

		int endOfBody=document.lastIndexOf("End of Claroline Body");

		if(endOfBody>=0){

			int startOfEmpty=document.lastIndexOf("<!-- empty -->", endOfBody);

			if(startOfEmpty>=0 && endOfBody>startOfEmpty){

				// There is the place in which we should find the text

				String documentToAnalyse=document.substring(startOfEmpty, endOfBody);

				int ocurrence=documentToAnalyse.indexOf("goto/index.php");

				while(ocurrence>=0){

					int endOfOcurrence=documentToAnalyse.indexOf("\"", ocurrence+1);

					if(endOfOcurrence>ocurrence){

						String urlGot=urlBase + "/document/" + documentToAnalyse.substring(ocurrence, endOfOcurrence).replace("&amp;", "&").replace(" ", "%20");

						String pathForFile=urlGot.substring((urlBase + "/document/goto/index.php/").length(), urlGot.lastIndexOf("?") >=0 ? urlGot.lastIndexOf("?") : urlGot.length());

						list.add(new FileFromURL(urlGot, URLDecoder.decode("/" + pathForFile, "iso-8859-1")));

					}

					ocurrence=documentToAnalyse.indexOf("goto/index.php", ocurrence+1);

				}


			}

		}

	}
	
	public static String readClarolineIntro(String platformURL) throws Exception{

		String output="";
		
		int untilWhenUrlToUse= platformURL.indexOf("/", platformURL.indexOf("/claroline")+1);

		if(untilWhenUrlToUse>=0){

			// Get url from intro using the platform url
			
			String urlBase = platformURL.substring(0, untilWhenUrlToUse);
			String urlToUse =  urlBase + "/course/index.php";
			
			// Get the document
			
			String document=requestDocument(urlToUse,"","ISO-8859-1");

			// Parse the document
			
			int preintro=document.indexOf("<!-- - - - - - - - - - - Claroline Body - - - - - - - - - -->");
			
			if(preintro<0) return output;
			
			int introstart=document.indexOf("<td valign=\"top\">",preintro+1);
			
			if(introstart<0) return output;
			
			int postend=document.lastIndexOf("<!-- - - - - - - - - - -   End of Claroline Body   - - - - - - - - - - -->");
			
			if(postend<0) return output;
			
			int introend=document.lastIndexOf("</td>",postend-1);
			
			introstart+=17; // Compensation for introstart
			
			if(introstart>=introend) return output;
			
			// No problem parsing
			
			String introhtmloriginal=document.substring(introstart, introend);
			
			output=filterTags(introhtmloriginal, new String[]{}); // TODO try to correct urls to be absolute
			
		}
		
		return output;

	}


	public static String readClarolineAnnouncements(String platformURL) throws Exception{

		String output="";
		
		int untilWhenUrlToUse= platformURL.indexOf("/", platformURL.indexOf("/claroline")+1);

		if(untilWhenUrlToUse>=0){

			// Get url from announcements using the platform url
			
			String urlBase = platformURL.substring(0, untilWhenUrlToUse);
			String urlToUse =  urlBase + "/announcements/announcements.php";
			
			// Get the document
			
			String document=requestDocument(urlToUse,"","ISO-8859-1");

			// Parse the document
			
			int preintro=document.indexOf("<!-- - - - - - - - - - - Claroline Body - - - - - - - - - -->");
			
			if(preintro<0) return output;
			
			int introstart=document.indexOf("<table class=\"claroTable\"",preintro+1);
			
			if(introstart<0) return output;
			
			int postend=document.lastIndexOf("<!-- - - - - - - - - - -   End of Claroline Body   - - - - - - - - - - -->");
			
			if(postend<0) return output;
			
			int introend=document.lastIndexOf("</div>",postend-1);
			
			if(introstart>=introend) return output;
			
			// No problem parsing
			
			String introhtmloriginal=document.substring(introstart, introend);
			
			output=filterTags(introhtmloriginal, new String[]{"img"});
			
		}
		
		return output;

	}



	public static ArrayList<FileFromURL> listDocumentsMoodle(String platformURL) throws Exception{

		/*
		 * 0 -> Path (incl. filename)
		 * 1 -> URL to file
		 */

		ArrayList<FileFromURL> list=new ArrayList<FileFromURL>();

		int untilWhenUrlToUse= platformURL.indexOf("/", platformURL.indexOf("/moodle")+1);

		if(untilWhenUrlToUse>=0){

			String urlBase = platformURL.substring(0, untilWhenUrlToUse);
			String urlGetMethod=platformURL.indexOf("?") >= 0 ? platformURL.substring(platformURL.indexOf("?") + 1, platformURL.length()) : "";
			String urlForResources= urlBase + "/mod/resource/index.php" + (urlGetMethod.length()>0 ? "?" + urlGetMethod : "");

			// For board
			System.out.println(" == Board checking... ==");
			listDocumentsMoodleInternal(platformURL, list, urlBase, true);

			// For resources
			System.out.println(" == Resources checking... ==");
			listDocumentsMoodleInternal(urlForResources, list, urlBase, false);


		}

		cleanArtifacts(list);
		deleteRepeatedFiles(list);
		sortandaggrupatelist(list);

		return list;

	}


	private static void listDocumentsMoodleInternal(String urlToUse, ArrayList<FileFromURL> list, String urlBase, boolean onboard) throws Exception{

		//System.out.println("---Accessed---");

		String resourcePage;

		try{

			resourcePage=requestDocument(urlToUse, "");

		} catch(Exception ex){

			return;

		}

		if(!urlToUse.equals(lastRequestedURL)) return;		// If the page redirected us

		// The list of files from this resource

		int bodyStart=resourcePage.indexOf("<!-- END OF HEADER -->");

		int bodyEnd=resourcePage.indexOf("<!-- START OF FOOTER -->", bodyStart);

		if(bodyStart >=0 && bodyEnd > bodyStart){

			String whereToSearch=resourcePage.substring(bodyStart, bodyEnd);

			int URLStart=whereToSearch.indexOf(urlBase + "/file.php/");
			int URLEnd=whereToSearch.indexOf("\"", URLStart);

			if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
				URLEnd=whereToSearch.indexOf("\'", URLStart);

			while(URLStart>=0 && URLStart<URLEnd){

				String urlToFile=whereToSearch.substring(URLStart, URLEnd);
				urlToFile=urlToFile.replace("&amp;", "&");

				int filePathStart=urlToFile.indexOf("/", (urlBase + "/file.php/").length()+1);

				String filePath=urlToFile.substring(filePathStart, urlToFile.length());

				list.add(new FileFromURL(urlToFile,URLDecoder.decode(filePath, "iso-8859-1")));	// Added to list

				// For next loop

				URLStart=whereToSearch.indexOf(urlBase + "/file.php/", URLEnd);
				URLEnd=whereToSearch.indexOf("\"",URLStart);

				if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
					URLEnd=whereToSearch.indexOf("\'", URLStart);

			}

			// Then directories

			URLStart=onboard ? whereToSearch.indexOf("/mod/resource/view.php?") : whereToSearch.indexOf("view.php?");
			URLEnd=whereToSearch.indexOf("\"", URLStart);

			if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
				URLEnd=whereToSearch.indexOf("\'", URLStart);

			while(URLStart>=0 && URLStart<URLEnd){

				String urlList=urlBase + (onboard ? "" : "/mod/resource/") + whereToSearch.substring(URLStart, URLEnd);
				urlList=urlList.replace("&amp;", "&").replace(" ", "%20");

				// We have got the url, but we don't know if it's a folder or not, let's check it

				try{

					String realurl = getRedirectedURL(urlList, "");

					if(realurl==null){

						// Folder, recursive search

						listDocumentsMoodleInternal(urlList, list, urlBase, false);

					} else if(realurl.contains(urlBase)){

						// Document, let's get the real name

						int filePathStart=realurl.indexOf("/", (urlBase + "/file.php/").length()+1);

						if(filePathStart>=0){

							String filePath=realurl.substring(filePathStart, realurl.length());

							list.add(new FileFromURL(realurl,URLDecoder.decode(filePath, "iso-8859-1")));	// Added to list

						}


					}


				} catch(Exception ex){

					ex.printStackTrace();

				}


				// For next loop

				URLStart=onboard ? whereToSearch.indexOf("/mod/resource/view.php?",URLEnd) : whereToSearch.indexOf("view.php?",URLEnd);
				URLEnd=whereToSearch.indexOf("\"", URLStart);

				if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0) // There is a ' before the "
					URLEnd=whereToSearch.indexOf("\'", URLStart);

			}

		}




	}


	public static ArrayList<FileFromURL> listDocumentsMoodle2(String platformURL) throws Exception{

		/*
		 * 0 -> Path (incl. filename)
		 * 1 -> URL to file
		 */

		ArrayList<FileFromURL> list=new ArrayList<FileFromURL>();

		int untilWhenUrlToUse= platformURL.indexOf("/", platformURL.indexOf("/moodle")+1);

		if(untilWhenUrlToUse>=0){

			String urlBase = platformURL.substring(0, untilWhenUrlToUse);
			String urlGetMethod=platformURL.indexOf("?") >= 0 ? platformURL.substring(platformURL.indexOf("?") + 1, platformURL.length()) : "";
			String urlForResources= urlBase + "/mod/resource/index.php" + (urlGetMethod.length()>0 ? "?" + urlGetMethod : "");

			// Board
			System.out.println(" == Board checking... ==");
			listDocumentsMoodle2Internal(platformURL, list, urlBase, "", true);

			// Resources
			System.out.println(" == Resources checking... ==");
			listDocumentsMoodle2Internal(urlForResources, list, urlBase, "", false);


		}

		cleanArtifacts(list);
		deleteRepeatedFiles(list);
		sortandaggrupatelist(list);

		return list;

	}


	private static void listDocumentsMoodle2Internal(String urlToUse, ArrayList<FileFromURL> list, String urlBase, String folder, boolean onboard) throws Exception{

		//System.out.println("---Accessed---");

		String resourcePage;

		try{

			resourcePage=requestDocument(urlToUse, "");

		} catch(Exception ex){

			return;

		}

		if(!urlToUse.equals(lastRequestedURL)) return;		// If the page redirected us

		// The list of files from this resource

		int bodyStart=resourcePage.indexOf("<div id=\"page-content\"");

		int bodyEnd=resourcePage.indexOf("</section>", bodyStart);

		if(bodyStart >=0 && bodyEnd > bodyStart){

			String whereToSearch=resourcePage.substring(bodyStart, bodyEnd);

			int URLStart=onboard ? whereToSearch.indexOf("/mod/resource/view.php?") : whereToSearch.indexOf("view.php?");
			int URLEnd=whereToSearch.indexOf("\"", URLStart);

			if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
				URLEnd=whereToSearch.indexOf("\'", URLStart);

			while(URLStart>=0 && URLStart<URLEnd){

				String urlList=urlBase + (onboard ? "" : "/mod/resource/") + whereToSearch.substring(URLStart, URLEnd);
				urlList=urlList.replace("&amp;", "&");

				// We have got the url, but we don't know if it's a folder or not, let's check it

				int indeximg=whereToSearch.indexOf("<img src=", URLEnd);
				int endofimg=whereToSearch.indexOf(">", indeximg);

				int endofa=whereToSearch.indexOf("<", endofimg);

				int folderindex=whereToSearch.indexOf("folder-24",indeximg);

				String filename=endofimg>=0 && endofa>endofimg ? whereToSearch.substring(endofimg+1, endofa).trim() : "undefined";

				if(folderindex>=0 && folderindex<endofimg){

					// Folder, recursive search

					listDocumentsMoodle2Internal(urlList, list, urlBase, folder + "/" + filename,false);

				} else{

					// Document, let's get the real name

					try{

						String realurl = getRedirectedURL(urlList, "");
						String realname=filename;	// By now

						if(realurl!=null && realurl.contains("/pluginfile.php/")){ // Only download files, and not page resources or whatever

							// Redirected, get the real name

							int questionMarkIndex=realurl.indexOf("?");
							String realurlwithoutquestion=realurl.substring(0, questionMarkIndex >=0 ? questionMarkIndex : realurl.length());
							int lastDivider=realurl.substring(0, questionMarkIndex >=0 ? questionMarkIndex : realurl.length()).lastIndexOf("/");	// No error because it starts at 0

							// If there is any possibility of reaching a subfolder

							if(realurlwithoutquestion.contains("/pluginfile.php/") && realurlwithoutquestion.contains("/mod_folder/content/")){

								int modfoldercontentindex=realurlwithoutquestion.indexOf("/mod_folder/content/");

								if(realurlwithoutquestion.indexOf("/",modfoldercontentindex+("/mod_folder/content/").length())>=0){

									lastDivider=realurlwithoutquestion.indexOf("/",modfoldercontentindex+("/mod_folder/content/").length());

								}

							} else if(realurlwithoutquestion.contains("/pluginfile.php/") && realurlwithoutquestion.contains("/mod_label/intro/")){

								lastDivider=realurlwithoutquestion.indexOf("/mod_label/intro/")+("/mod_label/intro").length();

							}

							if(lastDivider>=0){

								// Got a name

								realname=URLDecoder.decode(realurl.substring(lastDivider+1, questionMarkIndex>=0 ? questionMarkIndex : realurl.length()),"UTF-8");

							}


							list.add(new FileFromURL(urlList,folder + "/" + realname));

						}

					} catch(Exception ex){
						ex.printStackTrace();
					}

				}

				// For next loop

				URLStart=onboard ? whereToSearch.indexOf("/mod/resource/view.php?",URLEnd) : whereToSearch.indexOf("view.php?",URLEnd);
				URLEnd=whereToSearch.indexOf("\"", URLStart);

				if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
					URLEnd=whereToSearch.indexOf("\'", URLStart);

			}

			// For folders in the board

			URLStart=whereToSearch.indexOf("/mod/folder/view.php?");
			URLEnd=whereToSearch.indexOf("\"", URLStart);

			if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
				URLEnd=whereToSearch.indexOf("\'", URLStart);

			while(URLStart>=0 && URLStart<URLEnd && onboard){

				String urlList=urlBase + whereToSearch.substring(URLStart, URLEnd);
				urlList=urlList.replace("&amp;", "&");

				// We have got the url, and we know it MUST be a folder, checking the folder

				listDocumentsMoodle2Internal(urlList, list, urlBase, folder, false);


				// For next loop

				URLStart=whereToSearch.indexOf("/mod/folder/view.php?",URLEnd);
				URLEnd=whereToSearch.indexOf("\"", URLStart);

				if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
					URLEnd=whereToSearch.indexOf("\'", URLStart);

			}

			// For files found

			URLStart=whereToSearch.indexOf("/pluginfile.php/");
			URLEnd=whereToSearch.indexOf("\"", URLStart);

			if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
				URLEnd=whereToSearch.indexOf("\'", URLStart);


			while(URLStart>=0 && URLStart<URLEnd){

				String urlFile=urlBase + whereToSearch.substring(URLStart, URLEnd);
				urlFile=urlFile.replace("&amp;", "&");

				String realname="undefined";

				// Get file name

				int questionMarkIndex=urlFile.indexOf("?");
				String realurlwithoutquestion=urlFile.substring(0, questionMarkIndex >=0 ? questionMarkIndex : urlFile.length());
				int lastDivider=urlFile.substring(0, questionMarkIndex >=0 ? questionMarkIndex : urlFile.length()).lastIndexOf("/");	// No error because it starts at 0

				// If there is any possibility of reaching a subfolder

				if(realurlwithoutquestion.contains("/pluginfile.php/") && realurlwithoutquestion.contains("/mod_folder/content/")){

					int modfoldercontentindex=realurlwithoutquestion.indexOf("/mod_folder/content/");

					if(realurlwithoutquestion.indexOf("/",modfoldercontentindex+("/mod_folder/content/").length())>=0){

						lastDivider=realurlwithoutquestion.indexOf("/",modfoldercontentindex+("/mod_folder/content/").length());

					}

				} else if(realurlwithoutquestion.contains("/pluginfile.php/") && realurlwithoutquestion.contains("/mod_label/intro/")){

					lastDivider=realurlwithoutquestion.indexOf("/mod_label/intro/")+("/mod_label/intro").length();

				}

				if(lastDivider>=0){

					// Got a name

					realname=URLDecoder.decode(urlFile.substring(lastDivider+1, questionMarkIndex>=0 ? questionMarkIndex : urlFile.length()),"UTF-8");

				}

				list.add(new FileFromURL(urlFile,folder + "/" + realname));

				// For next loop

				URLStart=whereToSearch.indexOf("/pluginfile.php/", URLEnd);
				URLEnd=whereToSearch.indexOf("\"", URLStart);

				if(whereToSearch.indexOf("\'", URLStart)<URLEnd && whereToSearch.indexOf("\'", URLStart)>=0)
					URLEnd=whereToSearch.indexOf("\'", URLStart);

			}

		}

	}

	protected static void deleteRepeatedFiles(ArrayList<FileFromURL> list){	// Deletes files with same url

		// Make a copy of list

		int pos=0;

		while(pos<list.size()){	// From 0 to size

			FileFromURL element=list.get(pos);	// To compare

			int i=pos+1;

			while(i<list.size()){	// From pos+1 to size

				// 1 is url
				if(element.getURL().equals(list.get(i).getURL())){

					list.remove(i);	// Delete element
					i--;			// The i index must be reduced

					//out.set(i, new String[]{"Repeated:" + out.get(i)[0],out.get(i)[1]});

				}

				i++;
			}

			pos++;

		}

	}

	protected static void cleanArtifacts(ArrayList<FileFromURL> list){

		for(int i=0; i<list.size(); i++){

			FileFromURL element=list.get(i);

			// First for the name
			String name=element.getFileDestination().trim();	// Trim path

			int until=name.indexOf("<");
			until=name.indexOf(">")>=0 && name.indexOf(">")<until ? name.indexOf(">") : until;

			if(until>=0) name=name.substring(0, until);	// Delete unwanted exceeded code
			name=name.replaceAll("[*?\"<>|]", "_");	// Correct special characters

			if(name.length()<=0) name="undefined"; // Just in case

			// Second for the url

			String url=element.getURL().trim();	// Trim url

			until=url.indexOf("<") <= url.indexOf(">") ? url.indexOf("<") : url.indexOf(">");

			if(until>=0) url=url.substring(0, until);		// Delete unwanted exceeded code

			list.set(i, new FileFromURL(url, name));

		}

	}
	
	protected static boolean aisbeforeb(String a, String b){
		
		int maxcheck=a.length();
		if(b.length()<maxcheck) maxcheck=b.length();
		
		// Checkers
		
		boolean concluded=false;
		boolean before=false;
		
		// To upper case
		
		String aupper=a.toUpperCase();
		String bupper=b.toUpperCase();
		
		// Check every character
		
		for(int i=0; i<maxcheck && !concluded; i++){
			
			int chara=(int) aupper.charAt(i);
			int charb=(int) bupper.charAt(i);
			
			if(chara<charb){
				
				concluded=true;
				before=true;
				
			} else if (chara>charb){
				
				concluded=true;
				before=false;
				
			}
			
		}
		
		if(!concluded){
			
			// Equals until the maxcheck, check the length
			
			before=a.length()<b.length();
			
		}
		
		// If the same... well... it wouldn't expect that
		
		return before;
		
	}

	protected static void sortList(ArrayList<FileFromURL> list){

		ArrayList<FileFromURL> orderedlist=new ArrayList<FileFromURL>();
		
		for(FileFromURL element : list){
			
			boolean elementisafter=true; // The checked element is after the current i-element. The for will continue until the element is not after
			int i=0;
			
			for(i=0; i<orderedlist.size() && elementisafter; i++){
				
				elementisafter=aisbeforeb(orderedlist.get(i).getFileDestination(),element.getFileDestination()); // a before b, so b after a, so element after i-element
				
				if(!elementisafter) i--; // Required when for is finished because it will automatically do i++
				
			}
			
			orderedlist.add(i, element); // Add at i, pos in orderedlist with value that should be after element
			
		}
		
		list.clear();
		
		for(FileFromURL element : orderedlist) list.add(element);
		
	}

	protected static void sortListString(ArrayList<String> list){

		ArrayList<String> orderedlist=new ArrayList<String>();
		
		for(String element : list){
			
			boolean elementisafter=true; // The checked element is after the current i-element. The for will continue until the element is not after
			int i=0;
			
			for(i=0; i<orderedlist.size() && elementisafter; i++){
				
				elementisafter=aisbeforeb(orderedlist.get(i),element); // a before b, so b after a, so element after i-element
				
				if(!elementisafter) i--; // Required when for is finished because it will automatically do i++
				
			}
			
			orderedlist.add(i, element); // Add at i, pos in orderedlist with value that should be after element
			
		}
		
		list.clear();
		
		for(String element : orderedlist) list.add(element);
		
	}
	
	protected static void sortandaggrupatelist(ArrayList<FileFromURL> list){
		
		// First get all the folders, with no repetition
		
		ArrayList<String> folderlist=new ArrayList<String>();
		
		for(FileFromURL element : list){ // Check repetition for each element. We are checking repetition on parents!
			
			boolean contained=false;
			
			for(int i=0; i<folderlist.size() && !contained; i++){
				
				contained=element.getParent().equals(folderlist.get(i)); // getParent gives the folder containing the file
				
			}
			
			if(!contained) folderlist.add(element.getParent()); // Parent not contained
			
		}
		
		// Order folderlist
		
		sortListString(folderlist);
		
		// Lists for the elements
		
		ArrayList<FileFromURL> orderedlist=new ArrayList<FileFromURL>();
		ArrayList<FileFromURL> elementswithsameparent=new ArrayList<FileFromURL>();
		
		// Check elements with same parent for all the parents in the list and order them
		
		for(String parent : folderlist){
			
			elementswithsameparent.clear();
			
			// Fill with elements with same parent
			
			for(FileFromURL element : list){
				
				if(element.getParent().equals(parent)) elementswithsameparent.add(element);
				
			}
			
			// Order them
			
			sortList(elementswithsameparent);
			
			// Now all to orderedlist and go on
			
			for(FileFromURL element : elementswithsameparent) orderedlist.add(element);
			
		}
		
		// All to the original list

		list.clear();
		
		for(FileFromURL element : orderedlist) list.add(element);
		
	}
	
	public static String filterTags(String html, String[] tags){

		String output=html+"";
		
		while(output.indexOf("< ") >= 0){
			
			output.replace("< ","<");
			
		}

		while(output.indexOf("</ ") >= 0){
			
			output.replace("</ ","</");
			
		}
		
		// No problem now with cases like the ones from before
		
		for(String tag : tags){
			
			for(String posiblestart : new String[]{"<","</"}){ // For all type of tags
				
				int firsttagstart=output.toLowerCase().indexOf(posiblestart + tag);
				int firsttagend=output.indexOf(">", firsttagstart);
				
				while(firsttagstart>=0 && firsttagend>firsttagstart){ // Do until no tag remains
					
					output=output.replace(output.substring(firsttagstart,firsttagend+1),""); // Remove the tag
					
					// Next loop
					firsttagstart=output.toLowerCase().indexOf(posiblestart + tag);
					firsttagend=output.indexOf(">", firsttagstart);

				}
				
				
			}

		}
		

		System.out.println("Ended");
		
		return output;
		
	}
	
}
