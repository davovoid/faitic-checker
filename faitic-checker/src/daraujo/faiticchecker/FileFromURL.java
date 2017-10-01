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

public class FileFromURL {

	private String inturl, intfiledestination;
	
	public FileFromURL(String url, String fileDestination) {

		inturl=url;
		intfiledestination=fileDestination;
		
	}
	
	public String getURL(){ return inturl; }
	
	public String getFileDestination(){ return intfiledestination; }

	public String getParent(){
		
		int lastdivider=getFileDestination().lastIndexOf("/");
		
		if(lastdivider>=0) return getFileDestination().substring(0, lastdivider+1);
		
		return "";
		
	}
	
}
